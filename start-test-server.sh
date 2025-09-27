#!/bin/bash

echo "Starting CraftUtils test server..."
docker compose up -d

echo "Waiting for server to start..."
sleep 30

echo "Checking server logs..."
docker compose logs minecraft

echo ""
echo "Server started! You can:"
echo "1. Connect with Minecraft client to localhost:25565"
echo "2. View logs: docker compose logs -f minecraft"  
echo "3. Execute commands: docker compose exec minecraft rcon-cli"
echo "4. Stop server: docker compose down"
echo ""
echo "Test user credentials: testuser (no password needed, offline mode)"