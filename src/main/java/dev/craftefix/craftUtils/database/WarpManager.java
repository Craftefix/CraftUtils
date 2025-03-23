package dev.craftefix.craftUtils.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WarpManager {

    // Create a warp
    public void createWarp(String warpName, double x, double y, double z) {
        String query = "INSERT INTO warps (warp_name, x, y, z) VALUES (?, ?, ?, ?)";
        try (Connection connection = DatabaseManager.getConnection(); PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, warpName);
            stmt.setDouble(2, x);
            stmt.setDouble(3, y);
            stmt.setDouble(4, z);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // List all warps
    public List<Warp> listWarps() {
        List<Warp> warps = new ArrayList<>();
        String query = "SELECT * FROM warps";
        try (Connection connection = DatabaseManager.getConnection(); Statement stmt = connection.createStatement(); ResultSet resultSet = stmt.executeQuery(query)) {
            while (resultSet.next()) {
                warps.add(new Warp(
                        resultSet.getString("warp_name"),
                        resultSet.getDouble("x"),
                        resultSet.getDouble("y"),
                        resultSet.getDouble("z")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return warps;
    }

    // Inner class for Warp representation
    public static class Warp {
        private String warpName;
        private double x, y, z;

        public Warp(String warpName, double x, double y, double z) {
            this.warpName = warpName;
            this.x = x;
            this.y = y;
            this.z = z;
        }

        // Getters and setters
        public String getWarpName() { return warpName; }
        public double getX() { return x; }
        public double getY() { return y; }
        public double getZ() { return z; }
    }
}
