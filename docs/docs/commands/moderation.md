# Moderation Commands

CraftUtils provides comprehensive moderation tools for server administration, including muting, unmuting, and pardoning players.

## Player Muting

### `/mute <player> [duration] [reason]`
**Permission:** `CraftUtils.mute`  
**Description:** Mute a player to prevent them from chatting

**Usage:**
- `/mute PlayerName` - Permanently mutes the player
- `/mute PlayerName 30m` - Mutes for 30 minutes
- `/mute PlayerName 2h Spamming chat` - Mutes for 2 hours with reason
- `/mute PlayerName 1d Being disrespectful` - Mutes for 1 day with reason

**Duration Formats:**
- `s` - Seconds (e.g., `30s` = 30 seconds)
- `m` - Minutes (e.g., `15m` = 15 minutes)  
- `h` - Hours (e.g., `2h` = 2 hours)
- `d` - Days (e.g., `7d` = 7 days)

**Features:**
- Works on both online and offline players
- Automatic reason defaulting ("No reason provided")
- Database storage for persistent mutes
- Staff notification system
- Discord webhook integration
- Prevents self-muting attempts

### `/unmute <player>`
**Permission:** `CraftUtils.unmute`  
**Description:** Remove a mute from a player

**Usage:**
- `/unmute PlayerName` - Removes the mute from the specified player

**Features:**
- Works with both online and offline players
- Validates that player is actually muted
- Staff notification broadcast
- Discord webhook integration
- Automatic cleanup of mute data

## Ban Management

### `/pardon <player>`
**Permission:** `CraftUtils.pardon`  
**Description:** Unban (pardon) a banned player

**Usage:**
- `/pardon PlayerName` - Removes the ban from the specified player

**Features:**
- Supports both modern profile bans and legacy name bans
- Works with Minecraft's native ban system
- Staff notification broadcast
- Validates ban status before pardoning
- Compatible with Paper server software

## Notification System

### Staff Notifications
**Permission:** `CraftUtils.mute.notify` (for mute notifications)  
**Permission:** `CraftUtils.pardon.notify` (for pardon notifications)

**Features:**
- Real-time notifications to online staff
- Includes player name, action, and reason
- Color-coded messages for easy identification
- Only visible to players with notification permissions

### Discord Integration
All moderation actions are logged to Discord if webhooks are configured:
- Player mutes with duration and reason
- Player unmutes with moderator information
- Automatic webhook delivery for server logging

## Mute Management Features

### Database Storage
- Persistent mute data across server restarts
- UUID-based player identification
- Mute history tracking
- Automatic expiration handling

### Duration Handling
- Flexible duration parsing (seconds to days)
- Automatic mute expiration
- Clear duration display in messages
- Permanent mutes for administrative purposes

### Player Experience
- Clear mute messages with duration and reason
- Notification when muted or unmuted
- Prevention of duplicate mute applications

## Permission Structure

### Core Permissions
- `CraftUtils.mute` - Mute players
- `CraftUtils.unmute` - Unmute players  
- `CraftUtils.pardon` - Pardon banned players

### Notification Permissions
- `CraftUtils.mute.notify` - Receive mute/unmute notifications
- `CraftUtils.pardon.notify` - Receive pardon notifications

### Admin Integration
- `CraftUtils.admingui` - Access moderation through admin GUI

## Configuration

Enable/disable moderation features in your `config.yml`:

```yaml
commands:
  mute: true
  unmute: true
  pardon: true
  admingui: true

# Discord webhook integration
discord:
  webhooks:
    moderation: "https://discord.com/api/webhooks/..."
```

## Best Practices

### Muting Guidelines
1. **Always provide reasons** for mutes to maintain transparency
2. **Use appropriate durations** - start with shorter durations for minor offenses
3. **Document repeat offenders** for escalating punishments
4. **Communicate with other staff** about ongoing issues

### Duration Recommendations
- **Minor spam/caps:** 5-15 minutes
- **Inappropriate language:** 30 minutes - 2 hours
- **Harassment:** 6-24 hours
- **Severe violations:** 1-7 days
- **Permanent mutes:** Only for extreme cases

## Example Usage

### Basic Muting
```bash
/mute Steve 10m Please don't spam the chat
/mute Alex 1h Using inappropriate language  
/mute Builder1 30m Excessive caps usage
```

### Management Commands
```bash
/unmute Steve          # Remove Steve's mute
/pardon Alex           # Unban Alex
```

### Checking Status
```bash
/mute PlayerName       # Will show error if already muted
/unmute PlayerName     # Will show error if not muted
/pardon PlayerName     # Will show error if not banned
```

## Troubleshooting

**"Player has never joined the server":**
- The player name doesn't exist in server records
- Check spelling and ensure the player has connected before

**"Player is already muted":**
- The player already has an active mute
- Use `/unmute` first if you need to change the mute duration/reason

**"Player is not muted":**
- Attempted to unmute a player who isn't muted
- Verify the player name and current mute status

**"Player is not banned":**
- Attempted to pardon a player who isn't banned
- Check server ban list to verify ban status

**Discord webhooks not working:**
- Verify webhook URL is correctly configured
- Check Discord server permissions
- Ensure webhook channel exists and is accessible

## Integration with Other Systems

### Chat Management
- Muted players cannot send chat messages
- Automatic mute enforcement during gameplay
- Integration with chat plugins and formats

### Database Integration
- All mute data stored in plugin database
- Persistent across server restarts
- Query-able for staff review and analytics

### Discord Logging
- Real-time moderation logs
- Staff accountability tracking
- Server audit trails for administrative review