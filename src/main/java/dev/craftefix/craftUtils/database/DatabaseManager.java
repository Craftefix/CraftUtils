package dev.craftefix.craftUtils.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.craftefix.craftUtils.Main;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

    private static HikariDataSource dataSource;

    // Initialize the HikariCP DataSource
    static {
        Main plugin = Main.getInstance();
        FileConfiguration config = plugin.getConfig();
        String username = config.getString("database.username");
        String host = config.getString("database.host");
        int port = config.getInt("database.port");
        String password = config.getString("database.password");
        String database = config.getString("database.database");

        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database);
        hikariConfig.setUsername(username);
        hikariConfig.setPassword(password);
        hikariConfig.setMaximumPoolSize(10);  // Adjust the pool size as needed

        dataSource = new HikariDataSource(hikariConfig);
        createTablesIfNotExist();
    }

    // Get a connection from the pool
    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    // Close the data source
    public static void close() {
        if (dataSource != null) {
            dataSource.close();
        }
    }

    // Create tables if they do not exist
    private static void createTablesIfNotExist() {
        String createHomes = "CREATE TABLE IF NOT EXISTS homes (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "owner_uuid VARCHAR(36) NOT NULL," +
                "home_name VARCHAR(32) NOT NULL," +
                "x DOUBLE NOT NULL," +
                "y DOUBLE NOT NULL," +
                "z DOUBLE NOT NULL," +
                "world VARCHAR(64) NOT NULL," +
                "UNIQUE KEY unique_home (owner_uuid, home_name)," +
                "UNIQUE KEY unique_location (x, y, z, world)" +
                ")";
        String createWarps = "CREATE TABLE IF NOT EXISTS warps (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "warp_name VARCHAR(32) NOT NULL UNIQUE," +
                "x DOUBLE NOT NULL," +
                "y DOUBLE NOT NULL," +
                "z DOUBLE NOT NULL," +
                "private TINYINT(1) NOT NULL DEFAULT 0," +
                "world VARCHAR(64) NOT NULL," +
                "UNIQUE KEY unique_location (x, y, z, world)" +
                ")";
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(createHomes);
            stmt.executeUpdate(createWarps);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
