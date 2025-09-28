# Commands

## Teleportation

| Command | Permission | Description |
|---------|-----------|-------------|
| `/home [name]` | `CraftUtils.home` | Teleport to your home |
| `/sethome [name]` | `CraftUtils.sethome` | Set a home location |
| `/delhome <name>` | `CraftUtils.delhome` | Delete a home |
| `/warp <name>` | `CraftUtils.warp` | Teleport to a warp |
| `/tpask <player>` | `CraftUtils.tpask` | Request teleport to player |
| `/tpaaccept` | `CraftUtils.tpaaccept` | Accept teleport request |
| `/tpadeny` | `CraftUtils.tpadeny` | Deny teleport request |
| `/tpacancel` | `CraftUtils.tpacancel` | Cancel your teleport request |
| `/back` | `CraftUtils.back` | Return to previous location |
| `/cu tp location <x> <y> <z>` | `CraftUtils.teleport.location` | Teleport to coordinates |
| `/cu tp here <player>` | `CraftUtils.teleport.here` | Teleport player to you |
| `/cu tp self <player>` | `CraftUtils.teleport.self` | Teleport to player |
| `/cu tp others <player> <x> <y> <z>` | `CraftUtils.teleport.others` | Teleport others to coordinates |

## Utilities

| Command | Permission | Description |
|---------|-----------|-------------|
| `/vault [1-10]` | `CraftUtils.vault` | Open personal storage |
| `/repair [hand\|all]` | `CraftUtils.repair` | Repair items in hand |
| `/repair all` | `CraftUtils.repair` + `CraftUtils.repair.all` | Repair all inventory items |
| `/craftingtable` | `CraftUtils.craftingtable` | Open portable workbench |
| `/anvil` | `CraftUtils.anvil` | Open portable anvil |

## Player Abilities

| Command | Permission | Description |
|---------|-----------|-------------|
| `/fly [player]` | `CraftUtils.fly` | Toggle flight |
| `/fly <player>` | `CraftUtils.fly.others` | Toggle flight for others |
| `/heal [player]` | `CraftUtils.heal` | Restore health |
| `/heal <player>` | `CraftUtils.heal.others` | Heal other players |
| `/eat [player]` | `CraftUtils.eat` | Restore hunger |
| `/eat <player>` | `CraftUtils.eat.others` | Feed other players |
| `/trash` | `CraftUtils.trash` | Open trash GUI |

## Gamemode

| Command | Permission | Description |
|---------|-----------|-------------|
| `/gmc [player]` | `CraftUtils.Gamemode.Creative` | Creative mode |
| `/gms [player]` | `CraftUtils.Gamemode.Survival` | Survival mode |
| `/gma [player]` | `CraftUtils.Gamemode.Adventure` | Adventure mode |

## Messaging

| Command | Permission | Description |
|---------|-----------|-------------|
| `/msg <player> <message>` | `CraftUtils.message` | Private message |
| `/r <message>` | `CraftUtils.reply` | Reply to last message |

## Moderation

| Command | Permission | Description |
|---------|-----------|-------------|
| `/mute <player> [time] [reason]` | `CraftUtils.mute` | Mute player |
| `/unmute <player>` | `CraftUtils.unmute` | Unmute player |
| `/pardon <player>` | `CraftUtils.pardon` | Unban player |

## GUI

| Command | Permission | Description |
|---------|-----------|-------------|
| `/cug` | `CraftUtils.gui` | Open main GUI |
| `/cug sounds` | `CraftUtils.gui` | Toggle GUI sounds |
| `/admingui` | `CraftUtils.admingui` | Open admin GUI |

## Command Aliases

### Utility Aliases
- `/pv` = `/vault`
- `/ct` = `/craftingtable`
- `/fix` = `/repair`

### Teleport Aliases  
- `/tpa` = `/tpask`

### Messaging Aliases
- `/w`, `/whisper`, `/message`, `/tell` = `/msg`
- `/reply` = `/r`

### GUI Aliases
- `/craftutilsgui` = `/cug`

### Universal Prefix
All commands work with `/cu` prefix: `/cu home`, `/cu vault`, `/cu fly`, etc.

## Time Formats

For `/mute` duration: `30s`, `5m`, `2h`, `1d`