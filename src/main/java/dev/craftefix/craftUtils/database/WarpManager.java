package dev.craftefix.craftUtils.database;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class WarpManager {
    // Existing code remains the same...


    public List<Warp> getAllWarps(Player player) {
        return getAllWarps(Optional.ofNullable(player));
    }

    public List<Warp> getAllWarps() {
        return getAllWarps(Optional.empty());
    }

    private List<Warp> getAllWarps(Optional<Player> player) {
        List<Warp> warps = new ArrayList<>();
        String query = "SELECT * FROM warps";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                boolean hidden = resultSet.getInt("private") == 1;
                if (hidden && player.map(p -> !p.hasPermission("craftutils.warps.showhidden")).orElse(true)) {
                    continue;
                }
                warps.add(new Warp(
                    resultSet.getString("warp_name"),
                    resultSet.getDouble("x"),
                    resultSet.getDouble("y"),
                    resultSet.getDouble("z"),
                    hidden,
                    resultSet.getString("world")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return warps;
    }

    public Optional<Warp> getWarp(String warpName, Player player) {
        return getWarp(warpName, Optional.ofNullable(player));
    }

    public Optional<Warp> getWarp(String warpName) {
        return getWarp(warpName, Optional.empty());
    }

    private Optional<Warp> getWarp(String warpName, Optional<Player> player) {
        String query = "SELECT * FROM warps WHERE warp_name = ?";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, warpName);
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                boolean hidden = resultSet.getInt("private") == 1;
                if (hidden && player.map(p -> !p.hasPermission("craftutils.warps.showhidden")).orElse(true)) {
                    return Optional.empty();
                }
                return Optional.of(new Warp(
                    resultSet.getString("warp_name"),
                    resultSet.getDouble("x"),
                    resultSet.getDouble("y"),
                    resultSet.getDouble("z"),
                    hidden,
                    resultSet.getString("world")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    // Create a warp with name and world
    public void createWarp(String warpName, double x, double y, double z, int hidden, World world) {
        String query = "INSERT INTO warps (warp_name, x, y, z, private ,world) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, warpName);
            stmt.setDouble(2, x);
            stmt.setDouble(3, y);
            stmt.setDouble(4, z);
            stmt.setInt(5, hidden);
            stmt.setString(6, world.getName());
            stmt.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException e) {
            System.err.println("Warp creation failed: Duplicate warp name or location.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Delete a warp
    public void deleteWarp(String warpName) {
        String query = "DELETE FROM warps WHERE warp_name = ?";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, warpName);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static class Warp {
        private String warpName;
        private double x, y, z;
        private String world;
        private boolean hidden;

        public Warp(String warpName, double x, double y, double z, boolean hidden, String world) {
            this.warpName = warpName;
            this.x = x;
            this.y = y;
            this.z = z;
            this.hidden = hidden;
            this.world = world;
        }

        public String getWarpName() { return warpName; }
        public double getX() { return x; }
        public double getY() { return y; }
        public double getZ() { return z; }
        public String getWarp() { return getWarpName(); }
        public boolean isHidden() { return hidden; }
        public String getWorldName() { return world; }
        public World getWorld() { return Bukkit.getWorld(world); }
    }
}