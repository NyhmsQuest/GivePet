# GivePet

**GivePet** is a Paper plugin for Minecraft servers that allows players to easily transfer ownership of their pets to other players. This plugin is ideal for SMP or community servers where pet ownership can be shared, and administrators can manage pet transfers efficiently with configurable settings and permissions.

## Features

- **Pet Ownership Transfer**: Players can transfer the ownership of their tamed pets to other players.
- **Transfer Timeout**: Configurable timeout for transfers, automatically canceling the process if it is not completed in time.
- **Restricted Worlds**: Prevent pet transfers in specific worlds. Each world can have a custom display name in the config.
- **Configurable Messages**: All plugin messages are customizable, supporting MiniMessage formatting for more advanced text formatting.
- **Vanish Plugin Support**: Compatible with Essentials, CMI, and VanishNoPacket, ensuring that vanished players don’t interfere with the transfer process.

## Commands

- `/givepet <player>`: Initiates a pet transfer to the specified player.
- `/givepet cancel`: Cancels an ongoing pet transfer.
- `/givepet reload`: Reloads the plugin’s configuration files.

## Permissions

- `givepet.use`: Allows players to use the `/givepet` command.
- `givepet.reload`: Allows reloading the plugin’s configuration using `/givepet reload`.

## Configuration

The plugin's configuration file can be found at `plugins/GivePet/config.yml`.

### Example Configuration

```yaml
# Time in seconds before a transfer request expires
transfer_timeout: 30

# List of worlds where pets can't be transferred to
restricted_worlds:
  Spawn: "ꜱᴘᴀᴡɴ"
  resource_world: "ʀᴇꜱᴏᴜʀᴄᴇ ᴡᴏʀʟᴅ"
  resource_nether: "ʀᴇꜱᴏᴜʀᴄᴇ ɴᴇᴛʜᴇʀ"

# Messages that will be sent to players for different events
messages:
  not_a_player: "<#FBA9A9>ᴏɴʟʏ ᴘʟᴀʏᴇʀꜱ ᴄᴀɴ ᴜꜱᴇ ᴛʜɪꜱ ᴄᴏᴍᴍᴀɴᴅ."
  reload_permission: "<#FBA9A9>ʏᴏᴜ ᴅᴏ ɴᴏᴛ ʜᴀᴠᴇ ᴘᴇʀᴍɪꜱꜱɪᴏɴ ᴛᴏ ᴜꜱᴇ ᴛʜɪꜱ ᴄᴏᴍᴍᴀɴᴅ."
  reload: "<#B2FBA9>ɢɪᴠᴇᴘᴇᴛ ᴄᴏɴꜰɪɢᴜʀᴀᴛɪᴏɴ ʀᴇʟᴏᴀᴅᴇᴅ."
  usage: "<#BDB0D0>ᴜꜱᴀɢᴇ: <#A180D0>/givepet <player>"
  player_not_found: "<#FBA9A9>ᴘʟᴀʏᴇʀ ɴᴏᴛ ꜰᴏᴜɴᴅ."
  restricted_world: "<#FBA9A9>ʏᴏᴜ ᴄᴀɴ'ᴛ ᴛʀᴀɴꜱꜰᴇʀ ᴀ ᴘᴇᴛ ᴛᴏ ᴘʟᴀʏᴇʀꜱ ɪɴ <#A180D0>%world%<#FBA9A9>."
  selection_prompt: "<#BDB0D0>ʀɪɢʜᴛ-ᴄʟɪᴄᴋ ʏᴏᴜʀ ᴘᴇᴛ ᴛᴏ ᴛʀᴀɴꜱꜰᴇʀ ɪᴛ ᴛᴏ <#A180D0>%player%<#FBA9A9>.<newline><#BDB0D0>ᴏʀ ᴄᴀɴᴄᴇʟ ᴡɪᴛʜ <click:run_command:'/givepet cancel'><hover:show_text:'<green>ᴄʟɪᴄᴋ ᴛᴏ ʀᴜɴ ᴛʜɪꜱ ᴄᴏᴍᴍᴀɴᴅ.'><white>[</white><color:#FBF69E>/givepet cancel</color:#FBF69E><white>]</white>"
  success_transfer: "<#B2FBA9>ꜱᴜᴄᴄᴇꜱꜱꜰᴜʟʟʏ ᴛʀᴀɴꜱꜰᴇʀʀᴇᴅ ʏᴏᴜʀ ᴘᴇᴛ ᴛᴏ <#A180D0>%player%<#B2FBA9>."
  received_pet: "<#B2FBA9>ʏᴏᴜ ʜᴀᴠᴇ ʀᴇᴄᴇɪᴠᴇᴅ ᴀ ᴘᴇᴛ ꜰʀᴏᴍ <#A180D0>%player%<#B2FBA9>."
  transfer_canceled: "<#FBA9A9>ᴘᴇᴛ ᴛʀᴀɴꜱꜰᴇʀ ᴄᴀɴᴄᴇʟᴇᴅ."
  transfer_timeout: "<#FBA9A9>ʏᴏᴜʀ ᴘᴇᴛ ᴛʀᴀɴꜱꜰᴇʀ ʀᴇǫᴜᴇꜱᴛ ʜᴀꜱ ᴛɪᴍᴇᴅ ᴏᴜᴛ."
  no_active_transfer: "<#FBA9A9>ʏᴏᴜ ᴀʀᴇ ɴᴏᴛ ᴄᴜʀʀᴇɴᴛʟʏ ᴛʀᴀɴꜱꜰᴇʀʀɪɴɢ ᴀ ᴘᴇᴛ."
  not_your_pet: "<#FBA9A9>ᴛʜɪꜱ ɪꜱ ɴᴏᴛ ʏᴏᴜʀ ᴘᴇᴛ."
```

## Installation

1. Download the latest release of `GivePet` from the [Releases](https://github.com/NyhmsQuest/GivePet/releases) section.
2. Place the downloaded `.jar` file into the `plugins` folder of your Paper server.
3. Start or restart your server to generate the configuration files.
4. Adjust the settings in `config.yml` as needed.
5. Reload the plugin using `/givepet reload` to apply changes.

## Compatibility

- Paper 1.21.1 (and newer versions)
- Works with popular vanish plugins like Essentials, CMI, and VanishNoPacket.

## Contributing

Contributions are welcome! Please feel free to submit issues, feature requests, or pull requests.

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for more information.

## Contact

For questions or support, feel free to open an issue on GitHub or reach out to me directly.
