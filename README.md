[![Alpha Builder](https://github.com/Craftefix/CraftUtils/actions/workflows/build.yml/badge.svg?branch=alpha-releases)](https://github.com/Craftefix/CraftUtils/actions/workflows/build.yml)
![CodeRabbit Pull Request Reviews](https://img.shields.io/coderabbit/prs/github/Craftefix/CraftUtils?utm_source=oss&utm_medium=github&utm_campaign=Craftefix%2FCraftUtils&labelColor=171717&color=FF570A&link=https%3A%2F%2Fcoderabbit.ai&label=CodeRabbit+Reviews)

# CraftUtils Plugin

A comprehensive utilities plugin for Paper servers running Minecraft 1.21+. This lightweight plugin provides essential server management tools without modifying vanilla game mechanics.

## Features

- **Player Management**: Comprehensive moderation tools including mute/unmute, pardon system
- **Teleportation System**: Full teleport request system, homes, warps, and location-based teleports
- **Utility Commands**: Quick access to workbench, anvil, ender chest, player vaults
- **Player Abilities**: Flight toggle, healing, food restoration, item repair
- **Messaging System**: Private messaging with reply functionality
- **Admin Tools**: Powerful admin GUI for server management
- **Database Support**: SQLite and MySQL/MariaDB compatibility with HikariCP connection pooling
- **Discord Integration**: Webhook support for moderation actions

## Commands

### Moderation Commands
| Command | Aliases | Description | Permission |
|---------|---------|-------------|------------|
| `/mute <player> [duration] [reason]` | - | Mute a player | `craftutils.mute` |
| `/unmute <player>` | - | Unmute a player | `craftutils.unmute` |
| `/pardon <player>` | - | Pardon (unban) a player | `craftutils.pardon` |

### Utility Commands
| Command | Aliases | Description | Permission |
|---------|---------|-------------|------------|
| `/vault [1-10]` | `/cu vault`, `/pv` | Open player vault | `CraftUtils.vault` |
| `/workbench` | `/cu workbench`, `/wb` | Open crafting table | `CraftUtils.workbench` |
| `/anvil` | `/cu anvil` | Open anvil interface | `CraftUtils.anvil` |
| `/enderchest` | `/cu enderchest`, `/ec` | Open ender chest | `CraftUtils.enderchest` |
| `/back` | `/cu back` | Teleport to previous location | `CraftUtils.back` |
| `/repair [hand\|all]` | `/cu repair`, `/fix` | Repair items | `CraftUtils.repair` |
| `/trash` | `/cu trash` | Open trash inventory | `CraftUtils.trash` |

### Player Abilities
| Command | Aliases | Description | Permission |
|---------|---------|-------------|------------|
| `/fly [player]` | `/cu fly` | Toggle flight mode | `CraftUtils.fly` |
| `/heal [player]` | `/cu heal` | Heal player (health & food) | `CraftUtils.heal` |
| `/eat [player]` | `/cu eat` | Restore food level | `CraftUtils.eat` |

### Teleportation Commands
| Command | Aliases | Description | Permission |
|---------|---------|-------------|------------|
| `/cu tp location <x> <y> <z>` | - | Teleport to coordinates | `CraftUtils.teleport.location` |
| `/cu tp others <player> <x> <y> <z>` | - | Teleport others to coordinates | `CraftUtils.teleport.others` |
| `/cu tp here <entity>` | - | Teleport entity to you | `CraftUtils.teleport.here` |
| `/cu tp self <entity>` | - | Teleport to entity | `CraftUtils.teleport.self` |

### Teleport Requests
| Command | Aliases | Description | Permission |
|---------|---------|-------------|------------|
| `/tpask <player>` | `/tpa`, `/cu tpask` | Request teleport to player | `CraftUtils.tpask` |
| `/tpaaccept` | `/cu tpaaccept` | Accept teleport request | `CraftUtils.tpaaccept` |
| `/tpadeny` | `/cu tpadeny` | Deny teleport request | `CraftUtils.tpadeny` |
| `/tpacancel` | `/cu tpacancel` | Cancel teleport request | `CraftUtils.tpacancel` |

### Home Management
| Command | Aliases | Description | Permission |
|---------|---------|-------------|------------|
| `/home [name]` | `/cu home` | Teleport to home | `CraftUtils.home` |
| `/sethome [name]` | `/cu sethome` | Set home location | `CraftUtils.home.sethome` |
| `/delhome <name>` | `/cu delhome` | Delete home | `CraftUtils.home.delete` |
| `/homes` | `/cu homes` | List all homes | `CraftUtils.home.list` |

### Warp Management
| Command | Aliases | Description | Permission |
|---------|---------|-------------|------------|
| `/warp <name>` | `/cu warp` | Teleport to warp | `CraftUtils.warp` |
| `/setwarp <name>` | `/cu setwarp` | Create warp point | `CraftUtils.warp.setwarp` |
| `/delwarp <name>` | `/cu delwarp` | Delete warp | `CraftUtils.warp.delete` |
| `/warps` | `/cu warps` | List all warps | `CraftUtils.warp.list` |

### Messaging
| Command | Aliases | Description | Permission |
|---------|---------|-------------|------------|
| `/msg <player> <message>` | `/message`, `/tell`, `/cu msg`, `/w`, `/whisper` | Send private message | `CraftUtils.message` |
| `/reply <message>` | `/r`, `/cu reply` | Reply to last message | `CraftUtils.reply` |

### Gamemode Shortcuts
| Command | Aliases | Description | Permission |
|---------|---------|-------------|------------|
| `/gmc [player]` | - | Set Creative mode | `CraftUtils.Gamemode.Creative` |
| `/gms [player]` | - | Set Survival mode | `CraftUtils.Gamemode.Survival` |
| `/gma [player]` | - | Set Adventure mode | `CraftUtils.Gamemode.Adventure` |

### Administration
| Command | Aliases | Description | Permission |
|---------|---------|-------------|------------|
| `/admingui` | `/cu admingui` | Open admin GUI | `CraftUtils.admingui` |
| `/cu` | - | Show plugin info and help | `craftutils.main` |

## Permission Notes

- Most commands have both self and others variants (e.g., `CraftUtils.fly` vs `CraftUtils.fly.others`)
- Home limits can be set with `craftutils.homes.{number}` (1-20) or `craftutils.homes.unlimited`
- Staff notifications for moderation actions: `craftutils.mute.notify`, `craftutils.pardon.notify`
- Repair permissions: `CraftUtils.repair` (hand only) vs `CraftUtils.repair.all` (entire inventory)

## Installation

1. Download the latest release from the [Releases page](https://github.com/Craftefix/CraftUtils/releases)
2. Place the JAR file in your server's `plugins` folder
3. Restart your server
4. Configure the plugin via `plugins/CraftUtils/config.yml`

## Requirements

- **Minecraft Version**: 1.21+
- **Server Software**: Paper (recommended) or Spigot
- **Java Version**: 21+

## Support

- **GitHub Issues**: [Report bugs or request features](https://github.com/Craftefix/CraftUtils/issues)
- **Compatibility**: Should work with Folia, but not fully tested or officially supported
