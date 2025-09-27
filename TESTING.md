# CraftUtils Testing Guide

## Test Setup Complete ✅

Your CraftUtils plugin has been built and configured for testing with Docker.

### Files Created:
- `docker-compose.yml` - Docker configuration for Paper server with your plugin
- `start-test-server.sh` - Helper script to start the test server
- `build/libs/CraftUtils-0.0.b55-all.jar` - Your compiled plugin

## How to Test

### 1. Start the Server
```bash
# Start the server (first time will download Docker image)
./start-test-server.sh

# Or manually:
docker compose up -d
```

### 2. View Server Logs
```bash
docker compose logs -f minecraft
```

### 3. Connect to Server
- **Server IP**: `localhost:25565` 
- **Username**: `testuser` (offline mode, no password needed)
- **Version**: Minecraft 1.21.1

### 4. Test Plugin Commands

Based on your plugin code, test these commands:

#### Core Commands
- `/craftutils` - Main plugin command
- `/cu` - Alias for craftutils command

#### Home System
- `/home` - Teleport to home
- `/sethome [name]` - Set a home location
- `/delhome [name]` - Delete a home
- `/homes` - List your homes

#### Warp System  
- `/warp [name]` - Teleport to warp
- `/setwarp [name]` - Set a warp (admin)
- `/delwarp [name]` - Delete warp (admin)
- `/warps` - List warps

#### Teleport & TPA
- `/tp [player]` - Teleport to player (admin)
- `/tpa [player]` - Send teleport request
- `/tpaccept` - Accept teleport request
- `/tpdeny` - Deny teleport request

#### Utility Commands
- `/enderchest` or `/ec` - Open ender chest
- `/heal` - Heal yourself (if you have permission)
- `/feed` - Feed yourself (if you have permission)

#### Admin Commands
- `/admingui` - Open admin GUI

### 5. Stop the Server
```bash
docker compose down
```

## Plugin Features to Test

Your CraftUtils plugin includes:
1. **Home Management** - Multiple homes per player
2. **Warp System** - Server-wide teleportation points
3. **TPA System** - Player-to-player teleport requests
4. **Utility Commands** - Ender chest, heal, feed
5. **Admin GUI** - Administrative interface
6. **Database Support** - SQLite and MariaDB
7. **Lamp Command Framework** - Modern command system

## Troubleshooting

### Server won't start?
```bash
# Check container status
docker compose ps

# View full logs
docker compose logs minecraft

# Restart container
docker compose restart minecraft
```

### Plugin not loading?
Check server logs for any errors during plugin initialization.

### Need to rebuild plugin?
```bash
./gradlew shadowJar
docker compose restart minecraft
```

## Container is Still Downloading
The itzg/minecraft-server image is large (~1GB+). Once download completes, the server will start automatically and your plugin will be loaded.

Monitor progress with: `docker compose logs -f minecraft`