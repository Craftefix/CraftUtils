# Teleportation Commands

CraftUtils provides comprehensive teleportation features including direct teleports, teleport requests, and location-based teleports.

## Direct Teleportation

### `/cu tp location <x> <y> <z>`
**Permission:** `CraftUtils.teleport.location`  
**Description:** Teleport to specific coordinates

**Usage:**
- `/cu tp location 100 64 -50` - Teleports to coordinates (100, 64, -50)

**Safety Features:**
- Validates coordinates are within world border
- Prevents teleporting above Y=1000
- Only works within the current world

### `/cu tp self <target>`
**Permission:** `CraftUtils.teleport.self`  
**Description:** Teleport yourself to another player or entity

**Usage:**
- `/cu tp self PlayerName` - Teleports you to the specified player

### `/cu tp here <target>`
**Permission:** `CraftUtils.teleport.here`  
**Description:** Teleport another player or entity to your location

**Usage:**
- `/cu tp here PlayerName` - Teleports the specified player to you
- Supports entity selectors for multiple targets

### `/cu tp others <target> <x> <y> <z>`
**Permission:** `CraftUtils.teleport.others`  
**Description:** Teleport other players/entities to specific coordinates

**Usage:**
- `/cu tp others PlayerName 100 64 -50` - Teleports the player to those coordinates
- Supports entity selectors for multiple targets

## Teleport Requests (TPA System)

### `/tpask <player>`
**Aliases:** `/tpa`, `/cu tpask`  
**Permission:** `CraftUtils.tpask`  
**Description:** Send a teleport request to another player

**Usage:**
- `/tpask Steve` - Sends a teleport request to Steve
- `/tpa Alex` - Alternative command to request teleport to Alex

**Features:**
- Interactive clickable messages for easy response
- Automatic cleanup of old requests (5 minutes)
- Prevents self-teleport requests
- Only one pending request per target player

### `/tpaaccept`
**Aliases:** `/cu tpaaccept`  
**Permission:** `CraftUtils.tpaaccept`  
**Description:** Accept an incoming teleport request

**Usage:**
- `/tpaaccept` - Accepts the most recent teleport request sent to you
- Can also click the green "[Accept]" button in chat

### `/tpadeny`
**Aliases:** `/cu tpadeny`  
**Permission:** `CraftUtils.tpadeny`  
**Description:** Deny an incoming teleport request

**Usage:**
- `/tpadeny` - Denies the most recent teleport request sent to you
- Can also click the red "[Deny]" button in chat

### `/tpacancel`
**Aliases:** `/cu tpacancel`  
**Permission:** `CraftUtils.tpacancel`  
**Description:** Cancel your outgoing teleport request

**Usage:**
- `/tpacancel` - Cancels any teleport request you've sent
- Can also click the red "[Cancel]" button in your original request message

## Navigation Commands

### `/back`
**Aliases:** `/cu back`  
**Permission:** `CraftUtils.back`  
**Description:** Return to your previous location

**Usage:**
- `/back` - Teleports you to your last location before a teleport

**Features:**
- Tracks locations before warps, homes, and other teleports
- Does not track `/back` commands to prevent infinite loops
- Resets on server disconnect/reconnect

## Safety & Validation

All teleportation commands include safety checks:

- **World Border Validation:** Prevents teleporting outside the world border
- **Height Limits:** Blocks teleportation above Y=1000
- **Online Status:** Verifies players are online before teleporting
- **Permission Checks:** Ensures proper permissions for all operations

## TPA Request Management

- **Automatic Cleanup:** Old requests (5+ minutes) are automatically removed
- **Player Disconnect:** All requests involving disconnected players are cancelled
- **One Request Limit:** Players can only have one pending request to each target
- **Interactive Messages:** Clickable chat messages for easy acceptance/denial

## Configuration

Enable/disable these commands in your `config.yml`:

```yaml
commands:
  tp: true
  tpask: true
  back: true
```

## Tips

1. **TPA Etiquette:** Always ask before sending teleport requests, especially repeatedly
2. **Coordinate Safety:** Double-check coordinates before using `/cu tp location` to avoid teleporting into walls or lava
3. **Back Command:** Use immediately after accidental teleports to return quickly
4. **Request Management:** TPA requests expire after 5 minutes, so respond promptly

## Troubleshooting

**"Invalid location: outside world border or height exceeds 1000":**
- Check that your coordinates are within the world border
- Ensure Y coordinate is 1000 or lower

**"You already have a pending request to this player!":**
- Wait for your current request to expire (5 minutes) or be answered
- Use `/tpacancel` to cancel your current request

**"No previous location found" with `/back`:**
- You haven't teleported anywhere this session
- The back location resets when you reconnect to the server

**TPA requests not working:**
- Ensure both players are online
- Check that the target player has the necessary permissions
- Verify the commands are enabled in the server configuration