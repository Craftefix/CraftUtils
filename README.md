# CraftUtils Plugin

A lightweight utilities plugin for Paper servers (Minecraft 1.21+) with modular command system.

## Features

- **Moderation**: Mute/unmute, pardon system with Discord webhooks
- **Teleportation**: Homes, warps, teleport requests, coordinate teleports
- **Utilities**: Player vaults, workbench, anvil, ender chest, repair tools
- **Player Tools**: Flight, healing, gamemode shortcuts, messaging
- **Admin GUI**: Comprehensive server management interface
- **Database**: SQLite/MariaDB support with connection pooling

## Configuration

Commands can be enabled/disabled in `config.yml`:

```yaml
commands:
  tp: true          # Teleportation commands
  message: true     # Private messaging
  tpAsk: true       # Teleport requests
  enderChest: true  # Ender chest access
  adminGUI: true    # Admin GUI
  alias: true       # Gamemode shortcuts
  ability: true     # Player abilities (fly, heal)
  homes: true       # Home management
  warps: true       # Warp management
  utility: true     # Utility commands (vault, workbench, etc)
  mute: true        # Mute system
  pardon: true      # Pardon system
```

## Installation

1. Download from [Releases](https://github.com/Craftefix/CraftUtils/releases)
2. Place in `plugins/` folder
3. Restart server
4. Configure via `plugins/CraftUtils/config.yml`

## Requirements

- Minecraft 1.21+
- Paper/Spigot
- Java 21+

## Support

[GitHub Issues](https://github.com/Craftefix/CraftUtils/issues) for bugs and feature requests.
