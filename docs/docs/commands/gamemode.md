# Gamemode Commands

CraftUtils provides quick gamemode switching commands for server administrators and authorized players.

## Quick Gamemode Commands

### `/gmc [player]`
**Permission:** `CraftUtils.Gamemode.Creative`  
**Description:** Switch to Creative mode

**Usage:**
- `/gmc` - Changes your gamemode to Creative
- `/gmc PlayerName` - Changes the specified player's gamemode to Creative

### `/gms [player]`
**Permission:** `CraftUtils.Gamemode.Survival`  
**Description:** Switch to Survival mode

**Usage:**
- `/gms` - Changes your gamemode to Survival
- `/gms PlayerName` - Changes the specified player's gamemode to Survival

### `/gma [player]`
**Permission:** `CraftUtils.Gamemode.Adventure`  
**Description:** Switch to Adventure mode

**Usage:**
- `/gma` - Changes your gamemode to Adventure
- `/gma PlayerName` - Changes the specified player's gamemode to Adventure

## GUI-Based Gamemode Switching

### Gamemode GUI
**Access:** Through the main CraftUtils GUI (`/cug` or `/craftutilsgui`)  
**Permission:** Various gamemode permissions (see below)  
**Description:** Interactive GUI for gamemode switching

**Available Options:**
- **Creative Mode** - `CraftUtils.gamemode.creative`
- **Survival Mode** - `CraftUtils.gamemode.survival`  
- **Adventure Mode** - `CraftUtils.gamemode.adventure`
- **Spectator Mode** - `CraftUtils.gamemode.spectator`

**Features:**
- Visual interface with clear gamemode options
- Only shows gamemodes you have permission to use
- Click-to-switch functionality
- Sound feedback when switching modes

## Admin GUI Integration

### Admin Gamemode Management
**Access:** Through Admin GUI (`/admingui`)  
**Permission:** `CraftUtils.admingui`  
**Description:** Advanced gamemode management for staff

**Features:**
- Manage gamemodes for other players
- Bulk gamemode changes
- Quick access to all gamemode options
- Staff-only interface

## Permission Structure

### Command Permissions
- `CraftUtils.Gamemode.Creative` - Use `/gmc` command
- `CraftUtils.Gamemode.Survival` - Use `/gms` command  
- `CraftUtils.Gamemode.Adventure` - Use `/gma` command

### GUI Permissions
- `CraftUtils.gamemode.creative` - Access Creative mode in GUIs
- `CraftUtils.gamemode.survival` - Access Survival mode in GUIs
- `CraftUtils.gamemode.adventure` - Access Adventure mode in GUIs
- `CraftUtils.gamemode.spectator` - Access Spectator mode in GUIs

### Admin Permissions
- `CraftUtils.admingui` - Access admin gamemode management

## Configuration

Enable/disable these commands in your `config.yml`:

```yaml
commands:
  gamemode: true
  admingui: true
  gui: true
```

## Command vs GUI Usage

### When to Use Commands:
- Quick gamemode switches during building/testing
- Command-line preference
- Scripting and automation
- Changing other players' gamemodes

### When to Use GUIs:
- Visual preference over commands
- New players unfamiliar with command syntax
- Discovering available gamemode options
- Integrated workflow with other CraftUtils features

## Tips

1. **Quick Building:** Use `/gmc` for rapid switching to Creative mode while building
2. **Testing:** Switch between Creative and Survival to test game mechanics
3. **Staff Management:** Use the Admin GUI to manage player gamemodes efficiently
4. **Permission Setup:** Grant specific gamemode permissions based on player roles

## Troubleshooting

**Commands not working:**
- Verify you have the correct permission for the gamemode
- Check that gamemode commands are enabled in config.yml
- Ensure the target player (if specified) is online

**GUI options not showing:**
- Permissions determine which gamemode options appear in GUIs
- Contact an administrator if you need additional gamemode permissions

**Can't change other players' gamemodes:**
- Commands work on other players if you have the permission and they're online
- Admin GUI provides more advanced player management options

## Examples

**Self gamemode changes:**
```
/gmc        # Switch to Creative
/gms        # Switch to Survival  
/gma        # Switch to Adventure
```

**Changing other players:**
```
/gmc Steve     # Put Steve in Creative mode
/gms Alex      # Put Alex in Survival mode
/gma Builder1  # Put Builder1 in Adventure mode
```

**GUI Access:**
```
/cug           # Open main GUI, then click Gamemode
/admingui      # Open admin GUI for advanced management
```