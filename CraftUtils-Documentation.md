# CraftUtils Documentation

## Table of Contents
1. [Installation & Setup](#installation--setup)
2. [Configuration Guide](#configuration-guide)
3. [Command System](#command-system)
4. [Permission System](#permission-system)
5. [Database Configuration](#database-configuration)
6. [Discord Integration](#discord-integration)
7. [API Documentation](#api-documentation)
8. [Troubleshooting](#troubleshooting)
9. [Development Guide](#development-guide)

## Installation & Setup

### Requirements
- **Java**: 17 or higher
- **Minecraft Server**: Paper 1.19+ (recommended), Spigot 1.19+
- **Database**: SQLite (default) or MySQL

### Installation Steps
1. Download the latest CraftUtils JAR from releases
2. Place the plugin in your server's `plugins/` directory
3. Start your server to generate the default configuration
4. Stop the server and configure `plugins/CraftUtils/config.yml`
5. Restart the server

### First-Time Setup
1. Set up permissions for your staff team
2. Configure database settings if using MySQL
3. Set up Discord webhook if desired
4. Test commands with appropriate permissions

## Configuration Guide

### Main Configuration (`config.yml`)

```yaml
# Database Configuration
database:
  type: "sqlite"  # Options: sqlite, mysql
  host: "localhost"
  port: 3306
  database: "craftutils"
  username: "username"
  password: "password"

# Discord Integration
discord:
  enabled: false
  webhook_url: "https://discord.com/api/webhooks/..."
  avatar_url: "https://your-server.com/avatar.png"
  username: "CraftUtils"

# Command Configuration - Enable/Disable Commands
commands:
  homes: true        # /home, /sethome, /delhome
  warps: true        # /warp, /setwarp, /delwarp
  tpa: true          # /tpa, /tpaccept, /tpdeny
  tp: true           # /tp and related commands
  enderchest: true   # /ec, /enderchest
  messages: true     # /msg, /reply, /mail
  moderation: true   # /mute, /unmute, /tempmute
  admin: true        # /craftutils, /admingui
  utility: true      # Utility commands
  abilities: true    # Special ability commands
  aliases: true      # Command aliases
```

### Command-Specific Settings

#### Home System
- **Max Homes per Player**: Configurable via permissions
- **Cooldown**: Set per-group cooldowns
- **Cross-World**: Enable/disable cross-world teleportation

#### Warp System
- **Public Warps**: Available to all players
- **Staff Warps**: Restricted warps for staff members
- **Categories**: Organize warps by category

#### TPA System
- **Request Timeout**: Default 60 seconds
- **Cross-World**: Allow cross-world teleport requests
- **Block Lists**: Players can block TPA requests

## Command System

### Home Commands
| Command | Permission | Description |
|---------|------------|-------------|
| `/home [name]` | `craftutils.home` | Teleport to home |
| `/sethome [name]` | `craftutils.sethome` | Set a home location |
| `/delhome [name]` | `craftutils.delhome` | Delete a home |
| `/homes` | `craftutils.homes` | List your homes |

**Usage Examples:**
```
/sethome          - Sets default home
/sethome base     - Sets named home "base"
/home             - Teleports to default home
/home base        - Teleports to "base" home
/delhome base     - Deletes "base" home
```

### Warp Commands
| Command | Permission | Description |
|---------|------------|-------------|
| `/warp <name>` | `craftutils.warp` | Teleport to warp |
| `/setwarp <name>` | `craftutils.setwarp` | Create a warp |
| `/delwarp <name>` | `craftutils.delwarp` | Delete a warp |
| `/warps` | `craftutils.warps` | List available warps |

### Teleportation Commands
| Command | Permission | Description |
|---------|------------|-------------|
| `/tpa <player>` | `craftutils.tpa` | Request teleport to player |
| `/tpahere <player>` | `craftutils.tpahere` | Request player teleport to you |
| `/tpaccept` | `craftutils.tpaccept` | Accept TPA request |
| `/tpdeny` | `craftutils.tpdeny` | Deny TPA request |
| `/tp <player>` | `craftutils.tp` | Direct teleport (admin) |

### Utility Commands
| Command | Permission | Description |
|---------|------------|-------------|
| `/ec [player]` | `craftutils.enderchest` | Open enderchest |
| `/msg <player> <message>` | `craftutils.message` | Send private message |
| `/reply <message>` | `craftutils.reply` | Reply to last message |
| `/mail <player> <message>` | `craftutils.mail` | Send offline message |

### Moderation Commands
| Command | Permission | Description |
|---------|------------|-------------|
| `/mute <player> [reason]` | `craftutils.mute` | Permanently mute player |
| `/tempmute <player> <time> [reason]` | `craftutils.tempmute` | Temporarily mute player |
| `/unmute <player>` | `craftutils.unmute` | Unmute player |

**Time Format Examples:**
- `1h` - 1 hour
- `30m` - 30 minutes  
- `2d` - 2 days
- `1w` - 1 week

### Admin Commands
| Command | Permission | Description |
|---------|------------|-------------|
| `/craftutils reload` | `craftutils.admin` | Reload configuration |
| `/admingui` | `craftutils.admin` | Open admin GUI |

## Permission System

### Default Permissions
```yaml
# Basic user permissions
craftutils.home: true
craftutils.sethome: true
craftutils.homes: true
craftutils.warp: true
craftutils.warps: true
craftutils.tpa: true
craftutils.tpaccept: true
craftutils.tpdeny: true
craftutils.enderchest: true
craftutils.message: true
craftutils.reply: true

# Staff permissions
craftutils.tp: op
craftutils.setwarp: op
craftutils.delwarp: op
craftutils.mute: op
craftutils.unmute: op
craftutils.tempmute: op
craftutils.admin: op
```

### Permission Groups
Create permission groups for different player types:

**VIP Group:**
```yaml
craftutils.home.limit.5: true
craftutils.sethome.cooldown.bypass: true
```

**Staff Group:**
```yaml
craftutils.*: true
craftutils.enderchest.others: true
craftutils.tp.bypass: true
```

## Database Configuration

### SQLite (Default)
- **File Location**: `plugins/CraftUtils/database.db`
- **No setup required**: Automatically created
- **Recommended for**: Small to medium servers

### MySQL Setup
1. Create a database for CraftUtils
2. Update config.yml with MySQL credentials:
```yaml
database:
  type: "mysql"
  host: "localhost"
  port: 3306
  database: "craftutils"
  username: "your_username"
  password: "your_password"
```

### Database Tables
The plugin automatically creates these tables:
- `craftutils_homes` - Player home locations
- `craftutils_warps` - Server warp points
- `craftutils_mutes` - Mute records
- `craftutils_mail` - Offline messages
- `craftutils_player_vaults` - Player vault data

## Discord Integration

### Webhook Setup
1. Create a Discord webhook in your server
2. Copy the webhook URL
3. Configure in config.yml:
```yaml
discord:
  enabled: true
  webhook_url: "YOUR_WEBHOOK_URL"
  avatar_url: "https://your-server.com/logo.png"
  username: "CraftUtils Bot"
```

### Logged Events
- Player joins/leaves
- Mute/unmute actions
- Admin command usage
- Plugin errors and warnings

## API Documentation

### For Developers
CraftUtils provides an API for other plugins to integrate with:

```java
// Get the API instance
CraftUtilsAPI api = CraftUtils.getAPI();

// Home management
api.getHomeManager().createHome(player, "home_name", location);
api.getHomeManager().deleteHome(player, "home_name");

// Warp management  
api.getWarpManager().createWarp("warp_name", location);
api.getWarpManager().deleteWarp("warp_name");

// Mute management
api.getMuteManager().mutePlayer(player, duration, reason);
api.getMuteManager().unmutePlayer(player);
```

### Events
Listen to CraftUtils events:
```java
@EventHandler
public void onHomeCreate(HomeCreateEvent event) {
    Player player = event.getPlayer();
    String homeName = event.getHomeName();
    // Handle event
}
```

## Troubleshooting

### Common Issues

#### Commands Not Working
1. Check if commands are enabled in config.yml
2. Verify player has correct permissions
3. Check console for error messages
4. Ensure plugin loaded correctly

#### Database Connection Issues
**MySQL Connection Failed:**
- Verify MySQL server is running
- Check credentials in config.yml
- Ensure database exists
- Check firewall settings

**SQLite File Locked:**
- Stop server completely
- Check file permissions
- Restart server

#### Permission Issues
- Use `/lp user <player> permission check craftutils.home` (LuckPerms)
- Verify inheritance from groups
- Check for negative permissions

### Debug Mode
Enable debug logging in config.yml:
```yaml
debug: true
```

### Getting Help
1. Check this documentation
2. Review console logs for errors
3. Check GitHub issues
4. Join our Discord support server

## Development Guide

### Building from Source
```bash
git clone https://github.com/your-repo/CraftUtils.git
cd CraftUtils
./gradlew build
```

### Project Structure
```
src/main/java/dev/craftefix/craftUtils/
├── commands/           # Command implementations
├── database/          # Database managers
├── discord/           # Discord integration
├── gui/              # GUI system
├── listeners/        # Event listeners
├── suggestions/      # Command suggestions
├── Main.java         # Plugin main class
└── EnableLamp.java   # Command enabler
```

### Adding New Commands
1. Create command class in `commands/` package
2. Extend appropriate base class
3. Register in `EnableLamp.java`
4. Add to config.yml commands section
5. Add permissions to plugin.yml

### Code Style
- Use Java 17+ features
- Follow camelCase naming
- Add JavaDoc for public methods
- Use try-with-resources for database operations
- Implement proper error handling

### Testing
- Test with different permission setups
- Verify database operations
- Test command edge cases
- Check memory usage with large datasets

### Contributing
1. Fork the repository
2. Create feature branch
3. Make changes with tests
4. Submit pull request
5. Follow code review process

---

*This documentation is for CraftUtils v1.0.0. For updates and support, visit our GitHub repository.*