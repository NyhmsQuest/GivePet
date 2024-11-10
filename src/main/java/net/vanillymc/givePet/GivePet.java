package net.vanillymc.givePet;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class GivePet extends JavaPlugin implements Listener, CommandExecutor, TabCompleter {

    private final Map<UUID, UUID> transferMap = new HashMap<>();
    private final Map<UUID, Integer> transferTasks = new HashMap<>();
    private FileConfiguration config;
    private MiniMessage miniMessage;
    private Map<String, String> restrictedWorlds;
    private int transferTimeout;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        config = getConfig();
        miniMessage = MiniMessage.miniMessage();
        transferTimeout = config.getInt("transfer_timeout", 30); // Default timeout of 30 seconds
        getServer().getPluginManager().registerEvents(this, this);
        this.getCommand("givepet").setExecutor(this);
        this.getCommand("givepet").setTabCompleter(this);

        // Load restricted worlds initially
        loadRestrictedWorlds();
    }

    private void loadRestrictedWorlds() {
        restrictedWorlds = new HashMap<>();
        for (String world : config.getConfigurationSection("restricted_worlds").getKeys(false)) {
            restrictedWorlds.put(world, config.getString("restricted_worlds." + world));
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(miniMessage.deserialize(config.getString("messages.not_a_player")));
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("reload")) {
                if (!player.hasPermission("givepet.reload")) {
                    player.sendMessage(miniMessage.deserialize(config.getString("messages.reload_permission")));
                    return true;
                }
                reloadConfig();
                config = getConfig();
                transferTimeout = config.getInt("transfer_timeout", 30);

                // Reload restricted worlds to reflect any changes in config
                loadRestrictedWorlds();

                player.sendMessage(miniMessage.deserialize(config.getString("messages.reload")));
                return true;
            } else if (args[0].equalsIgnoreCase("cancel")) {
                cancelTransfer(player.getUniqueId());
                player.sendMessage(miniMessage.deserialize(config.getString("messages.transfer_canceled")));
                return true;
            }
        }

        if (args.length != 1) {
            player.sendMessage(miniMessage.deserialize(config.getString("messages.usage")));
            return true;
        }

        Player targetPlayer = Bukkit.getPlayer(args[0]);

        if (targetPlayer == null || !targetPlayer.isOnline() || isPlayerVanished(targetPlayer)) {
            player.sendMessage(miniMessage.deserialize(config.getString("messages.player_not_found")));
            return true;
        }

        if (isWorldRestricted(targetPlayer.getWorld().getName())) {
            String customWorldName = getCustomWorldName(targetPlayer.getWorld().getName());
            player.sendMessage(miniMessage.deserialize(config.getString("messages.restricted_world").replace("%world%", customWorldName)));
            return true;
        }

        transferMap.put(player.getUniqueId(), targetPlayer.getUniqueId());
        player.sendMessage(miniMessage.deserialize(config.getString("messages.selection_prompt").replace("%player%", targetPlayer.getName())));

        // Schedule a timeout task for this transfer
        int taskId = new BukkitRunnable() {
            @Override
            public void run() {
                if (transferMap.containsKey(player.getUniqueId())) {
                    cancelTransfer(player.getUniqueId());
                    player.sendMessage(miniMessage.deserialize(config.getString("messages.transfer_timeout")));
                }
            }
        }.runTaskLater(this, transferTimeout * 20L).getTaskId();

        transferTasks.put(player.getUniqueId(), taskId);
        return true;
    }

    private void cancelTransfer(UUID playerId) {
        transferMap.remove(playerId);
        Integer taskId = transferTasks.remove(playerId);
        if (taskId != null) {
            Bukkit.getScheduler().cancelTask(taskId);
        }
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Entity clickedEntity = event.getRightClicked();

        if (transferMap.containsKey(player.getUniqueId()) && clickedEntity instanceof Tameable) {
            Tameable pet = (Tameable) clickedEntity;

            if (pet.isTamed() && pet.getOwner().equals(player)) {
                UUID targetPlayerUUID = transferMap.get(player.getUniqueId());
                Player targetPlayer = Bukkit.getPlayer(targetPlayerUUID);

                if (targetPlayer != null && targetPlayer.isOnline() && !isPlayerVanished(targetPlayer)) {
                    if (isWorldRestricted(targetPlayer.getWorld().getName())) {
                        String customWorldName = getCustomWorldName(targetPlayer.getWorld().getName());
                        player.sendMessage(miniMessage.deserialize(config.getString("messages.restricted_world").replace("%world%", customWorldName)));
                        cancelTransfer(player.getUniqueId());
                        return;
                    }

                    pet.setOwner(targetPlayer);
                    Location targetLocation = targetPlayer.getLocation();
                    pet.teleport(targetLocation);

                    player.sendMessage(miniMessage.deserialize(config.getString("messages.success_transfer").replace("%player%", targetPlayer.getName())));
                    targetPlayer.sendMessage(miniMessage.deserialize(config.getString("messages.received_pet").replace("%player%", player.getName())));

                    cancelTransfer(player.getUniqueId());
                } else {
                    player.sendMessage(miniMessage.deserialize(config.getString("messages.player_not_found")));
                    cancelTransfer(player.getUniqueId());
                }
            } else {
                player.sendMessage(miniMessage.deserialize(config.getString("messages.not_your_pet")));
            }
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Remove player from transfer map on disconnect
        cancelTransfer(event.getPlayer().getUniqueId());
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> suggestions = new ArrayList<>();
        if (args.length == 1) {
            if ("reload".startsWith(args[0].toLowerCase()) && sender.hasPermission("givepet.reload")) {
                suggestions.add("reload");
            }
            if ("cancel".startsWith(args[0].toLowerCase())) {
                suggestions.add("cancel");
            }
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (!isPlayerVanished(onlinePlayer)) {
                    suggestions.add(onlinePlayer.getName());
                }
            }
        }
        return suggestions;
    }

    private boolean isWorldRestricted(String worldName) {
        // Check if the world name is in the restricted worlds map
        return restrictedWorlds.containsKey(worldName);
    }

    private String getCustomWorldName(String worldName) {
        // Return custom name if it exists, otherwise return the world name itself
        return restrictedWorlds.getOrDefault(worldName, worldName);
    }

    private boolean isPlayerVanished(Player player) {
        // Check Essentials vanish
        Plugin vanishPlugin = Bukkit.getPluginManager().getPlugin("Essentials");
        if (vanishPlugin != null && vanishPlugin.isEnabled()) {
            if (player.hasMetadata("vanished")) {
                return true;
            }
        }

        // Check VanishNoPacket vanish
        vanishPlugin = Bukkit.getPluginManager().getPlugin("VanishNoPacket");
        if (vanishPlugin != null && vanishPlugin.isEnabled()) {
            if (player.hasMetadata("vanished")) {
                return true;
            }
        }

        // Check CMI vanish via reflection
        Plugin cmiPlugin = Bukkit.getPluginManager().getPlugin("CMI");
        if (cmiPlugin != null && cmiPlugin.isEnabled()) {
            try {
                // Get CMI instance
                Object cmiInstance = cmiPlugin.getClass().getMethod("getInstance").invoke(cmiPlugin);
                // Access PlayerManager
                Object playerManager = cmiInstance.getClass().getMethod("getPlayerManager").invoke(cmiInstance);
                // Get CMIUser for the player
                Object cmiUser = playerManager.getClass().getMethod("getUser", Player.class).invoke(playerManager, player);

                // Check if the user is vanished
                if (cmiUser != null) {
                    return (boolean) cmiUser.getClass().getMethod("isVanished").invoke(cmiUser);
                }
            } catch (Exception e) {
                e.printStackTrace(); // Log any reflection errors for debugging
            }
        }

        return false;
    }
}
