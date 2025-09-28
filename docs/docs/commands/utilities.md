# Utility Commands

These commands provide essential server utilities for storage, crafting, repairs, and navigation.

## Storage & Crafting

### `/vault [number]`
**Aliases:** `/cu vault`, `/pv`  
**Permission:** `CraftUtils.vault`  
**Description:** Opens your personal vault storage

**Usage:**
- `/vault` - Opens vault #1
- `/vault 3` - Opens vault #3

**Features:**
- Each player gets 10 personal vaults (numbered 1-10)
- Each vault has 54 storage slots (like a double chest)
- Contents are saved automatically when you close the vault
- Vaults are loaded asynchronously for better performance

### `/craftingtable`
**Aliases:** `/cu craftingtable`, `/ct`  
**Permission:** `CraftUtils.craftingtable`  
**Description:** Opens a portable crafting table

**Usage:**
- `/craftingtable` - Opens crafting interface anywhere

### `/anvil`
**Aliases:** `/cu anvil`  
**Permission:** `CraftUtils.anvil`  
**Description:** Opens a portable anvil interface

**Usage:**
- `/anvil` - Opens anvil interface anywhere

## Item Management

### `/repair [target]`
**Aliases:** `/cu repair`, `/fix`  
**Permission:** `CraftUtils.repair`  
**Description:** Repairs damaged items

**Usage:**
- `/repair` or `/repair hand` - Repairs item in your main hand
- `/repair all` - Repairs all damaged items in your inventory

**Additional Permissions:**
- `CraftUtils.repair.all` - Required for `/repair all`

**Features:**
- Only works on damageable items (tools, armor, weapons)
- Shows confirmation messages for successful repairs
- Counts and reports how many items were repaired with `/repair all`

## Navigation

### `/back`
**Aliases:** `/cu back`  
**Permission:** `CraftUtils.back`  
**Description:** Teleports you to your previous location

**Usage:**
- `/back` - Returns to your last teleport location

**Features:**
- Tracks your location before teleports (warps, homes, tp commands)
- Does not track `/back` teleports to prevent loops
- Shows error if no previous location is available

## Configuration

Enable/disable these commands in your `config.yml`:

```yaml
commands:
  vault: true
  craftingtable: true
  anvil: true
  repair: true
  back: true
```

## Tips

1. **Vault Organization:** Use different vault numbers for different item categories (1 for blocks, 2 for tools, etc.)
2. **Repair Strategy:** Use `/repair all` before major adventures to ensure all equipment is in top condition
3. **Back Command:** Very useful after warping to wrong locations or exploring dangerous areas
4. **Portable Crafting:** Perfect for building projects where you're far from your base

## Troubleshooting

**"No previous location found" when using `/back`:**
- This means you haven't teleported anywhere yet this session
- The back location is reset when you reconnect to the server

**"You don't have permission to repair all items":**
- Contact an administrator to get the `CraftUtils.repair.all` permission
- You can still use `/repair` or `/repair hand` to repair individual items

**Vault not opening:**
- Check that the `vault` command is enabled in the server's config.yml
- Ensure you have the `CraftUtils.vault` permission