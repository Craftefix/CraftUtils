package dev.craftefix.craftUtils.database;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

public class BackLocationManager {
    private final DatabaseManager databaseManager;
    private static final long EXPIRY_TIME = 5 * 60 * 60 * 1000; // 5 hours in milliseconds

    public BackLocationManager(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public void storeLocation(UUID playerUUID, Location location) {
        String query = "INSERT INTO back_locations (player_uuid, world_name, x, y, z, yaw, pitch, created_at, last_online) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                      "ON DUPLICATE KEY UPDATE world_name = VALUES(world_name), x = VALUES(x), y = VALUES(y), z = VALUES(z), " +
                      "yaw = VALUES(yaw), pitch = VALUES(pitch), created_at = VALUES(created_at), last_online = VALUES(last_online)";

        // For SQLite, we need a different approach
        if (databaseManager.getDatabaseType() == DatabaseManager.DatabaseType.SQLITE) {
            query = "INSERT OR REPLACE INTO back_locations (player_uuid, world_name, x, y, z, yaw, pitch, created_at, last_online) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        }

        try (Connection connection = databaseManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, playerUUID.toString());
            stmt.setString(2, location.getWorld().getName());
            stmt.setDouble(3, location.getX());
            stmt.setDouble(4, location.getY());
            stmt.setDouble(5, location.getZ());
            stmt.setFloat(6, location.getYaw());
            stmt.setFloat(7, location.getPitch());
            stmt.setLong(8, System.currentTimeMillis());
            stmt.setLong(9, System.currentTimeMillis()); // Player is online when storing location
            stmt.executeUpdate();
        } catch (SQLException e) {
            dev.craftefix.craftUtils.Main.getInstance().getLogger().severe("Database error storing back location: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Optional<Location> getLocation(UUID playerUUID) {
        // Clean up expired locations first
        cleanupExpiredLocations();

        String query = "SELECT world_name, x, y, z, yaw, pitch FROM back_locations WHERE player_uuid = ? AND created_at > ?";
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, playerUUID.toString());
            stmt.setLong(2, System.currentTimeMillis() - EXPIRY_TIME);

            try (ResultSet resultSet = stmt.executeQuery()) {
                if (resultSet.next()) {
                    World world = Bukkit.getWorld(resultSet.getString("world_name"));
                    if (world != null) {
                        return Optional.of(new Location(
                            world,
                            resultSet.getDouble("x"),
                            resultSet.getDouble("y"),
                            resultSet.getDouble("z"),
                            resultSet.getFloat("yaw"),
                            resultSet.getFloat("pitch")
                        ));
                    }
                }
            }
        } catch (SQLException e) {
            dev.craftefix.craftUtils.Main.getInstance().getLogger().severe("Database error loading back location: " + e.getMessage());
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public void deleteLocation(UUID playerUUID) {
        String query = "DELETE FROM back_locations WHERE player_uuid = ?";
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, playerUUID.toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            dev.craftefix.craftUtils.Main.getInstance().getLogger().severe("Database error deleting back location: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void updatePlayerOnlineStatus(UUID playerUUID) {
        String query = "UPDATE back_locations SET last_online = ? WHERE player_uuid = ?";
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, System.currentTimeMillis());
            stmt.setString(2, playerUUID.toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            dev.craftefix.craftUtils.Main.getInstance().getLogger().severe("Database error updating player online status: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void cleanupExpiredLocations() {
        long currentTime = System.currentTimeMillis();
        long offlineThreshold = 10 * 60 * 1000; // 10 minutes in milliseconds
        
        // Delete locations where:
        // 1. Created more than 5 hours ago, OR
        // 2. Player has been offline for more than 10 minutes
        String query = "DELETE FROM back_locations WHERE created_at < ? OR last_online < ?";
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, currentTime - EXPIRY_TIME); // 5 hour expiry
            stmt.setLong(2, currentTime - offlineThreshold); // 10 minute offline expiry
            stmt.executeUpdate();
        } catch (SQLException e) {
            dev.craftefix.craftUtils.Main.getInstance().getLogger().severe("Database error cleaning up expired back locations: " + e.getMessage());
            e.printStackTrace();
        }
    }
}