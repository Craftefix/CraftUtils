package dev.craftefix.craftUtils.database;

import org.bukkit.Bukkit;
import org.bukkit.World;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class HomeManager {
    public void createHome(String playerUUID, String homeName, double x, double y, double z, World world) {
        String query = "INSERT INTO homes (owner_uuid, home_name, x, y, z, world) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, playerUUID);
            stmt.setString(2, homeName);
            stmt.setDouble(3, x);
            stmt.setDouble(4, y);
            stmt.setDouble(5, z);
            stmt.setString(6, world.getName());
            stmt.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException e) {
            System.err.println("Home creation failed: Duplicate home name or location.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Home> getAllHomes(String playerUUID) {
        List<Home> homes = new ArrayList<>();
        String query = "SELECT * FROM homes WHERE owner_uuid = ?";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, playerUUID);
            try (ResultSet resultSet = stmt.executeQuery()) {
                while (resultSet.next()) {
                    homes.add(new Home(
                            resultSet.getString("owner_uuid"),
                            resultSet.getString("home_name"),
                            resultSet.getDouble("x"),
                            resultSet.getDouble("y"),
                            resultSet.getDouble("z"),
                            resultSet.getString("world")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return homes;
    }

    public Optional<Home> getHome(String playerUUID, String homeName) {
        String query = "SELECT * FROM homes WHERE owner_uuid = ? AND home_name = ?";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, playerUUID);
            stmt.setString(2, homeName);
            try (ResultSet resultSet = stmt.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(new Home(
                            resultSet.getString("owner_uuid"),
                            resultSet.getString("home_name"),
                            resultSet.getDouble("x"),
                            resultSet.getDouble("y"),
                            resultSet.getDouble("z"),
                            resultSet.getString("world")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public void updateHome(String playerUUID, String homeName, double x, double y, double z, World world) {
        String query = "UPDATE homes SET x = ?, y = ?, z = ?, world = ? WHERE owner_uuid = ? AND home_name = ?";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setDouble(1, x);
            stmt.setDouble(2, y);
            stmt.setDouble(3, z);
            stmt.setString(4, world.getName());
            stmt.setString(5, playerUUID);
            stmt.setString(6, homeName);
            stmt.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException e) {
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteHome(String playerUUID, String homeName) {
        String query = "DELETE FROM homes WHERE owner_uuid = ? AND home_name = ?";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, playerUUID);
            stmt.setString(2, homeName);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static class Home {
        private String playerUUID;
        private String homeName;
        private double x, y, z;
        private String worldName;

        public Home(String playerUUID, String homeName, double x, double y, double z, String worldName) {
            this.playerUUID = playerUUID;
            this.homeName = homeName;
            this.x = x;
            this.y = y;
            this.z = z;
            this.worldName = worldName;
        }

        public String getPlayerUUID() { return playerUUID; }
        public String getHomeName() { return homeName; }
        public double getX() { return x; }
        public double getY() { return y; }
        public double getZ() { return z; }
        public String getWorldName() { return worldName; }
        public World getWorld() { return Bukkit.getWorld(worldName); }
    }
}