---
sidebar_position: 2
---

# Player Abilities

Player ability commands give players enhanced capabilities on your server. These commands provide quality-of-life improvements for gameplay.

## 🚁 Flight Command

Allow players to toggle flight mode outside of Creative mode.

### Usage
```
/fly [player]
```

### Examples
```bash
/fly                    # Toggle your own flight
/fly Steve              # Toggle Steve's flight (admin only)
```

### Permissions
- `CraftUtils.fly` - Toggle your own flight
- `CraftUtils.fly.others` - Toggle other players' flight

### How It Works
- Toggles `allowFlight` permission for the player
- Does not work in Spectator mode (already flying)
- Shows status messages when enabled/disabled
- **Note:** May not work with some Anti-Cheat plugins

---

## ❤️ Heal Command

Instantly restore health and hunger to full.

### Usage
```
/heal [player]
```

### Examples
```bash
/heal                   # Heal yourself
/heal Steve             # Heal Steve (admin only)
```

### Permissions
- `CraftUtils.heal` - Heal yourself
- `CraftUtils.heal.others` - Heal other players

### Features
- Restores full health (20 HP)
- Restores full hunger (20 hunger points)
- Works on yourself or other players (with permission)

---

## 🍖 Eat Command

Instantly satisfy hunger without consuming food items.

### Usage
```
/eat [player]
```

### Examples
```bash
/eat                    # Satisfy your hunger
/eat Steve              # Satisfy Steve's hunger (admin only)
```

### Permissions
- `CraftUtils.eat` - Satisfy your own hunger
- `CraftUtils.eat.others` - Satisfy other players' hunger

### Features
- Sets hunger to maximum (20 points)
- No food items required
- Works on yourself or other players (with permission)

---

## 🗑️ Trash Command

Open a disposal interface to permanently delete unwanted items.

### Usage
```
/trash
```

### Examples
```bash
/trash                  # Open trash GUI
```

### Permissions
- `CraftUtils.trash` - Open trash interface

### Features
- **27-slot chest interface** - Standard chest size for disposal
- **Permanent deletion** - Items are deleted when interface closes
- **Simple and safe** - Just drag items in and close
- **No restrictions** - Delete any item type

### How It Works

1. Run `/trash` to open the disposal interface
2. Place unwanted items in the chest GUI
3. Items are permanently deleted when you close the interface
4. **Warning:** There's no way to recover deleted items!

---

## Configuration

Enable or disable ability commands in your `config.yml`:

```yaml title="config.yml"
commands:
  ability: true    # Enables /fly, /heal, /eat, /trash
```

## Permission Examples

### LuckPerms Setup

Give players basic abilities:
```bash
/lp group default permission set CraftUtils.fly true
/lp group default permission set CraftUtils.heal true
/lp group default permission set CraftUtils.eat true
/lp group default permission set CraftUtils.trash true
```

Give admins ability to affect others:
```bash
/lp group admin permission set CraftUtils.fly.others true
/lp group admin permission set CraftUtils.heal.others true
/lp group admin permission set CraftUtils.eat.others true
```

## Common Use Cases

### Survival Servers
- Give `/heal` to VIP ranks as a perk
- Allow `/fly` for builders during construction
- Provide `/trash` to all players for inventory management

### Creative Servers
- Enable all abilities by default
- Perfect for building and creative projects

### PvP Arenas
- Use `/heal` to reset players between matches
- Enable `/eat` to eliminate food management

---

**Need help?** Check our [Troubleshooting Guide](../support/troubleshooting) or [FAQ](../support/faq).