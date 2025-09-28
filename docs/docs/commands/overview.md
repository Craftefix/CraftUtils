---
sidebar_position: 1
---

# Commands Overview

CraftUtils provides **35+ commands** across 8 different categories. This page gives you a complete overview of all available commands.

## Quick Reference

### 🎮 Core Commands
| Command | Description | Permission |
|---------|-------------|------------|
| `/cu` | Show plugin info and version | `CraftUtils.main` |
| `/cu language` | Change your language | `CraftUtils.language` |
| `/cu reload` | Reload configuration | `CraftUtils.admin` |

### 🏠 Home & Warp System
| Command | Description | Permission |
|---------|-------------|------------|
| `/home [name]` | Teleport to home | `CraftUtils.home` |
| `/sethome [name]` | Set a home location | `CraftUtils.home.sethome` |
| `/delhome <name>` | Delete a home | `CraftUtils.home.delete` |
| `/homes` | List all your homes | `CraftUtils.home.list` |
| `/warp <name>` | Teleport to warp | `CraftUtils.warp` |
| `/setwarp <name>` | Create a warp | `CraftUtils.warp.setwarp` |
| `/delwarp <name>` | Delete a warp | `CraftUtils.warp.delete` |
| `/warps` | List all warps | `CraftUtils.warp.list` |

### 📞 Teleportation
| Command | Description | Permission |
|---------|-------------|------------|
| `/tpa <player>` | Request teleport to player | `CraftUtils.tpask` |
| `/tpaaccept` | Accept teleport request | `CraftUtils.tpaaccept` |
| `/tpadeny` | Deny teleport request | `CraftUtils.tpadeny` |
| `/tpacancel` | Cancel your teleport request | `CraftUtils.tpacancel` |
| `/tp <x> <y> <z>` | Teleport to coordinates | `CraftUtils.teleport.location` |
| `/tp <player>` | Teleport to player | `CraftUtils.teleport.others` |
| `/tp <player> here` | Teleport player to you | `CraftUtils.teleport.here` |
| `/tp here` | Teleport to your location | `CraftUtils.teleport.self` |

### 🛠️ Utility Commands
| Command | Description | Permission |
|---------|-------------|------------|
| `/vault [number]` | Open player vault | `CraftUtils.vault` |
| `/workbench` | Open crafting table | `CraftUtils.craftingtable` |
| `/anvil` | Open anvil interface | `CraftUtils.anvil` |
| `/back` | Return to previous location | `CraftUtils.back` |
| `/repair` | Repair item in hand | `CraftUtils.repair` |
| `/enderchest` | Open ender chest | `CraftUtils.enderchest` |

### ⚡ Player Abilities
| Command | Description | Permission |
|---------|-------------|------------|
| `/fly` | Toggle flight mode | `CraftUtils.fly` |
| `/heal` | Heal yourself | `CraftUtils.heal` |
| `/eat` | Satisfy hunger | `CraftUtils.eat` |
| `/trash` | Open trash GUI | `CraftUtils.trash` |

### 💬 Messaging
| Command | Description | Permission |
|---------|-------------|------------|
| `/msg <player> <message>` | Send private message | `CraftUtils.message` |
| `/reply <message>` | Reply to last message | `CraftUtils.reply` |

**Aliases:** `/tell`, `/w`, `/whisper` for `/msg` and `/r` for `/reply`

### 🎮 Gamemode Shortcuts
| Command | Description | Permission |
|---------|-------------|------------|
| `/gmc` | Switch to Creative | `CraftUtils.Gamemode.Creative` |
| `/gms` | Switch to Survival | `CraftUtils.Gamemode.Survival` |
| `/gma` | Switch to Adventure | `CraftUtils.Gamemode.Adventure` |

### 🎨 GUI Commands
| Command | Description | Permission |
|---------|-------------|------------|
| `/cug` | Open main CraftUtils GUI | `CraftUtils.gui` |
| `/cug sounds` | Toggle GUI sounds | `CraftUtils.gui` |
| `/admingui` | Open admin management GUI | `CraftUtils.admingui` |

### 🔨 Moderation
| Command | Description | Permission |
|---------|-------------|------------|
| `/mute <player> [time] [reason]` | Mute a player | `CraftUtils.mute` |
| `/unmute <player>` | Unmute a player | `CraftUtils.unmute` |
| `/pardon <player>` | Unban a player | `CraftUtils.pardon` |

## Command Categories

Each command category can be enabled or disabled in the configuration file. This allows you to use only the features your server needs.

### Configuration Example

```yaml title="config.yml"
commands:
  cu: true          # Core commands
  homes: true       # Home system
  warps: true       # Warp system  
  tp: true          # Teleportation
  tpAsk: true       # Teleport requests
  utility: true     # Utility commands
  ability: true     # Player abilities
  message: true     # Messaging system
  alias: true       # Gamemode shortcuts
  cug: true         # GUI commands
  adminGUI: true    # Admin GUI
  mute: true        # Moderation commands
  enderChest: true  # Ender chest access
```

## Need More Details?

- **[Player Abilities](./player-abilities)** - Detailed guide to fly, heal, eat, and trash commands
- **[Teleportation](./teleportation)** - Complete teleportation system guide
- **[Utilities](./utilities)** - Vault, workbench, anvil, and repair commands
- **[Moderation](./moderation)** - Mute system and Discord integration
- **[GUI System](./gui)** - Graphical user interfaces

---

*All commands support tab completion and have built-in help messages.*