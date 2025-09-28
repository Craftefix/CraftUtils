# GUI Commands

CraftUtils features an extensive graphical user interface system providing easy access to all plugin features through interactive menus.

## Main GUI Access

### `/cug`
**Aliases:** `/craftutilsgui`, `/cu gui`  
**Permission:** `CraftUtils.gui`  
**Description:** Opens the main CraftUtils GUI

**Usage:**
- `/cug` - Opens the primary interface
- `/craftutilsgui` - Alternative command
- `/cu gui` - Namespace alternative

**Features:**
- Central hub for all CraftUtils features
- Category-based organization
- Permission-aware interface (only shows available options)
- Sound feedback and visual indicators

### `/cug sounds`
**Aliases:** `/cu gui sounds`  
**Permission:** `CraftUtils.gui`  
**Description:** Toggle GUI sound effects

**Usage:**
- `/cug sounds` - Toggles sound effects on/off
- Plays confirmation sound if sounds are enabled
- Personal setting per player

## GUI Categories

### Player Abilities
**Access:** Main GUI → Player Abilities  
**Features:**
- Fly toggle
- Heal/feed commands
- Trash inventory access
- Permission-based visibility

### Utilities
**Access:** Main GUI → Utilities  
**Features:**
- Vault access (all 10 vaults)
- Portable crafting table
- Portable anvil
- Item repair options
- Back/teleport navigation

### Teleportation
**Access:** Main GUI → Teleportation  
**Features:**
- Home management
- Warp access
- Quick teleport commands
- TPA request management

### Gamemode Management
**Access:** Main GUI → Gamemode  
**Permission Requirements:**
- `CraftUtils.gamemode.creative`
- `CraftUtils.gamemode.survival`
- `CraftUtils.gamemode.adventure`
- `CraftUtils.gamemode.spectator`

**Features:**
- One-click gamemode switching
- Visual gamemode indicators
- Only shows permitted gamemodes

### Language Settings
**Access:** Main GUI → Language  
**Features:**
- Switch between available languages
- Real-time language changes
- Persistent language preferences

## Admin GUI System

### `/admingui`
**Aliases:** `/cu admingui`  
**Permission:** `CraftUtils.admingui`  
**Description:** Opens advanced administration interface

**Features:**
- Player management tools
- Bulk gamemode changes
- Server administration options
- Advanced configuration access

## Specialized GUIs

### Homes & Warps GUI
**Access:** Through teleportation category  
**Features:**
- Paginated home/warp lists
- Quick teleport buttons
- Home/warp management
- Search and filter options

### Abilities GUI
**Access:** Through player abilities category  
**Features:**
- Toggle flight mode
- Quick heal/feed
- Inventory management
- Personal utilities

## GUI Permissions

### Basic Access
- `CraftUtils.gui` - Access main GUI system

### Category Permissions
Individual features require their respective permissions:
- Vault: `CraftUtils.vault`
- Fly: `CraftUtils.fly`
- Heal: `CraftUtils.heal`
- Gamemode: `CraftUtils.gamemode.*`
- Admin: `CraftUtils.admingui`

### Sound Control
- Sound effects are per-player preferences
- No additional permissions required for sound toggle

## GUI Features

### Interactive Elements
- **Click Handlers:** Responsive button interactions
- **Sound Feedback:** Audio confirmation for actions
- **Visual Indicators:** Clear status displays
- **Pagination:** Organized navigation for large lists

### Permission Integration
- Automatic hiding of unavailable features
- Real-time permission checking
- Graceful degradation for limited permissions

### Language Support
- Multi-language GUI text
- Dynamic language switching
- Persistent language preferences

## Configuration

Enable/disable GUI features in your `config.yml`:

```yaml
commands:
  gui: true
  admingui: true

# Language support
languages:
  - en
  - de
```

## Tips

1. **First-Time Users:** Start with `/cug` to explore available features
2. **Sound Preferences:** Use `/cug sounds` to customize audio feedback
3. **Quick Access:** Bookmark frequently used GUI sections
4. **Admin Tools:** Use `/admingui` for server management tasks
5. **Mobile-Friendly:** GUIs work well with touch/mobile interfaces

## Troubleshooting

**GUI not opening:**
- Check you have `CraftUtils.gui` permission
- Verify GUI commands are enabled in config.yml
- Ensure you're using the correct command syntax

**Missing GUI options:**
- GUI automatically hides features you don't have permission for
- Contact an administrator for additional permissions
- Check that specific features are enabled in server configuration

**Sound issues:**
- Use `/cug sounds` to toggle sound effects
- Sound settings are per-player and persistent
- Some players may have client-side sound disabled

**Admin GUI access denied:**
- Requires `CraftUtils.admingui` permission
- This is typically restricted to server administrators
- Use regular `/cug` for standard features

## GUI Navigation Tips

**Main Menu Structure:**
```
CraftUtils Main GUI
├── Player Abilities
│   ├── Fly Toggle
│   ├── Heal/Feed
│   └── Trash
├── Utilities  
│   ├── Vaults
│   ├── Crafting
│   └── Repair
├── Teleportation
│   ├── Homes
│   ├── Warps
│   └── TPA
├── Gamemode
│   ├── Creative
│   ├── Survival
│   └── Adventure
└── Settings
    └── Language
```

**Efficient Workflow:**
1. Open main GUI with `/cug`
2. Navigate to desired category
3. Use quick-access buttons for common tasks
4. Return to main menu or close as needed