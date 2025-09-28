---
sidebar_position: 1
---

# CraftUtils

**A lightweight utilities plugin for Minecraft Paper servers.**

## What it does

- **🏠 Homes & Warps** - Players set homes, admins create warps
- **✈️ Teleportation** - TPA requests, admin teleports, `/back` command  
- **🎒 Storage** - Personal vaults, portable crafting tables, enderchests
- **⚡ Player Tools** - Fly toggle, heal/feed, gamemode shortcuts
- **💬 Messaging** - Private messages with reply system
- **🔨 Moderation** - Mute players with Discord webhooks
- **🖱️ GUI Interface** - Point-and-click for everything above

## Installation

1. **Download** the latest `.jar` from releases
2. **Drop** into your server's `plugins/` folder  
3. **Restart** your server
4. **Edit** `plugins/CraftUtils/config.yml` to enable features you want

## Requirements

- **Server:** Paper 1.21+ (Spigot works but Paper recommended)
- **Java:** Version 21 or higher
- **Permissions:** Any plugin (LuckPerms, GroupManager, etc.)

## Default Configuration

Most core features are **enabled by default**. Configure what you need in `config.yml`:

```yaml
commands:
  homes: true      # /home, /sethome commands  
  warps: true      # /warp command (admin sets warps)
  tpAsk: true      # /tpa teleport requests
  ability: true    # /fly, /heal, /eat commands
  message: true    # /msg, /reply commands
  cug: true        # /cug GUI interface
  utility: false   # /vault, /repair, /back (disabled by default)
  mute: false      # /mute, /unmute (disabled by default)
  pardon: false    # /pardon (disabled by default)
```

## What's Next?

- **[Commands Reference](./commands)** - All available commands and permissions
- **[Setup Guide](./getting-started)** - Detailed installation and configuration
