---
sidebar_position: 2
---

# Getting Started

This guide will walk you through installing and setting up CraftUtils on your Paper server.

## Installation

### Step 1: Download

1. Visit the [CraftUtils Releases page](https://github.com/Craftefix/CraftUtils/releases)
2. Download the latest `CraftUtils-X.X.X.jar` file

### Step 2: Install

1. Stop your server if it's running
2. Place the downloaded JAR file into your server's `plugins/` folder
3. Start your server

### Step 3: First Launch

When CraftUtils loads for the first time, it will:

- Create a `plugins/CraftUtils/` folder
- Generate a default `config.yml` file
- Create the database file (`database.db` for SQLite)
- Register all enabled commands

## Basic Configuration

### Enable/Disable Features

Open `plugins/CraftUtils/config.yml` and customize which features you want:

```yaml title="config.yml"
commands:
  cu: true          # Main command
  tp: true          # Teleportation commands
  message: true     # Private messaging
  tpAsk: true       # Teleport requests
  enderChest: true  # Ender chest access
  adminGUI: true    # Admin GUI
  alias: true       # Gamemode shortcuts (gmc, gms, gma)
  ability: true     # Player abilities (fly, heal, eat, trash)
  homes: true       # Home system
  warps: true       # Warp system
  utility: true     # Utility commands (vault, workbench, anvil, back, repair)
  mute: true        # Mute system
  pardon: true      # Pardon system
  cug: true         # CraftUtils GUI
```

### Database Setup

By default, CraftUtils uses SQLite (recommended for most servers):

```yaml title="config.yml"
database:
  type: sqlite
  file: plugins/CraftUtils/database.db
```

For larger servers, you can use MariaDB:

```yaml title="config.yml"
database:
  type: mariadb
  username: your_username
  host: localhost
  port: 3306
  password: your_password
  database: CraftUtils
```

## Testing Your Installation

After restarting your server with the new configuration:

1. **Check the console** for any errors during startup
2. **Test basic commands:**
   - `/cu` - Shows plugin information
   - `/cug` - Opens the main GUI (if enabled)
   - `/sethome test` - Sets a test home (if enabled)

## Permission Setup

CraftUtils integrates with permission plugins like **LuckPerms**.

### Quick Permission Setup

Give players basic access:
```
/lp group default permission set CraftUtils.home true
/lp group default permission set CraftUtils.tpask true
/lp group default permission set CraftUtils.message true
```

Give staff moderation access:
```
/lp group staff permission set CraftUtils.mute true
/lp group staff permission set CraftUtils.unmute true
/lp group staff permission set CraftUtils.admingui true
```

## Next Steps

- **[Commands Reference](./commands)** - Complete list of all available commands and permissions

## Need Help?

- **Issues or Bugs:** [GitHub Issues](https://github.com/Craftefix/CraftUtils/issues)