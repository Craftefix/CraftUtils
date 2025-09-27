package dev.craftefix.craftUtils.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.craftefix.craftUtils.Main;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

    private HikariDataSource dataSource;
    private DatabaseType databaseType;

    public enum DatabaseType {
        SQLITE, MARIADB
    }

    // Initialize the HikariCP DataSource
    public void initialize() {
        Main plugin = Main.getInstance();
        FileConfiguration config = plugin.getConfig();
        String type = config.getString("database.type", "sqlite").toLowerCase();
        
        databaseType = type.equals("mariadb") ? DatabaseType.MARIADB : DatabaseType.SQLITE;
        
        HikariConfig hikariConfig = new HikariConfig();
        
        if (databaseType == DatabaseType.SQLITE) {
            String dbFile = config.getString("database.file", "plugins/CraftUtils/database.db");
            File file = new File(dbFile);
            file.getParentFile().mkdirs();
            
            hikariConfig.setJdbcUrl("jdbc:sqlite:" + dbFile);
            hikariConfig.setMaximumPoolSize(1); // SQLite doesn't support multiple connections well
        } else {
            String username = config.getString("database.username");
            String host = config.getString("database.host");
            int port = config.getInt("database.port");
            String password = config.getString("database.password");
            String database = config.getString("database.database");

            hikariConfig.setJdbcUrl("jdbc:mariadb://" + host + ":" + port + "/" + database);
            hikariConfig.setUsername(username);
            hikariConfig.setPassword(password);
            hikariConfig.setMaximumPoolSize(10);
        }

        dataSource = new HikariDataSource(hikariConfig);
        createTablesIfNotExist();
    }

    // Get a connection from the pool
    public synchronized Connection getConnection() throws SQLException {
        if (dataSource == null) {
            initialize();
        }
        return dataSource.getConnection();
    }

    // Close the data source
    public synchronized void close() {
        if (dataSource != null) {
            dataSource.close();
            dataSource = null;
        }
    }

    // Get the database type
    public DatabaseType getDatabaseType() {
        return databaseType;
    }

    // Create tables if they do not exist
    private void createTablesIfNotExist() {
        String createHomes, createWarps, createVaults, createMutes, createBackLocations;
        
        if (databaseType == DatabaseType.SQLITE) {
            createHomes = "CREATE TABLE IF NOT EXISTS homes (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "owner_uuid TEXT NOT NULL," +
                    "home_name TEXT NOT NULL," +
                    "x REAL NOT NULL," +
                    "y REAL NOT NULL," +
                    "z REAL NOT NULL," +
                    "yaw REAL NOT NULL DEFAULT 0," +
                    "pitch REAL NOT NULL DEFAULT 0," +
                    "world TEXT NOT NULL," +
                    "UNIQUE(owner_uuid, home_name)" +
                    ")";
            createWarps = "CREATE TABLE IF NOT EXISTS warps (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "warp_name TEXT NOT NULL UNIQUE," +
                    "x REAL NOT NULL," +
                    "y REAL NOT NULL," +
                    "z REAL NOT NULL," +
                    "yaw REAL NOT NULL DEFAULT 0," +
                    "pitch REAL NOT NULL DEFAULT 0," +
                    "`private` INTEGER NOT NULL DEFAULT 0," +
                    "world TEXT NOT NULL" +
                    ")";
            createMutes = "CREATE TABLE IF NOT EXISTS mutes (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "player_uuid TEXT NOT NULL UNIQUE," +
                    "player_name TEXT NOT NULL," +
                    "muted_by TEXT NOT NULL," +
                    "reason TEXT," +
                    "mute_time INTEGER NOT NULL," +
                    "unmute_time INTEGER," +
                    "active INTEGER NOT NULL DEFAULT 1" +
                    ")";
            createVaults = "CREATE TABLE IF NOT EXISTS player_vaults (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "owner_uuid TEXT NOT NULL," +
                    "vault_number INTEGER NOT NULL," +
                    "contents TEXT NOT NULL," +
                    "UNIQUE(owner_uuid, vault_number)" +
                    ")";
            createBackLocations = "CREATE TABLE IF NOT EXISTS back_locations (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "player_uuid TEXT NOT NULL UNIQUE," +
                    "world_name TEXT NOT NULL," +
                    "x REAL NOT NULL," +
                    "y REAL NOT NULL," +
                    "z REAL NOT NULL," +
                    "yaw REAL NOT NULL," +
                    "pitch REAL NOT NULL," +
                    "created_at INTEGER NOT NULL," +
                    "last_online INTEGER NOT NULL DEFAULT 0" +
                    ")";
        } else {
            createHomes = "CREATE TABLE IF NOT EXISTS homes (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "owner_uuid VARCHAR(36) NOT NULL," +
                    "home_name VARCHAR(32) NOT NULL," +
                    "x DOUBLE NOT NULL," +
                    "y DOUBLE NOT NULL," +
                    "z DOUBLE NOT NULL," +
                    "yaw FLOAT NOT NULL DEFAULT 0," +
                    "pitch FLOAT NOT NULL DEFAULT 0," +
                    "world VARCHAR(64) NOT NULL," +
                    "UNIQUE KEY unique_home (owner_uuid, home_name)" +
                    ")";
            createWarps = "CREATE TABLE IF NOT EXISTS warps (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "warp_name VARCHAR(32) NOT NULL UNIQUE," +
                    "x DOUBLE NOT NULL," +
                    "y DOUBLE NOT NULL," +
                    "z DOUBLE NOT NULL," +
                    "yaw FLOAT NOT NULL DEFAULT 0," +
                    "pitch FLOAT NOT NULL DEFAULT 0," +
                     "`private` TINYINT(1) NOT NULL DEFAULT 0," +
                    "world VARCHAR(64) NOT NULL" +
                    ")";
            createMutes = "CREATE TABLE IF NOT EXISTS mutes (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "player_uuid VARCHAR(36) NOT NULL UNIQUE," +
                    "player_name VARCHAR(16) NOT NULL," +
                    "muted_by VARCHAR(16) NOT NULL," +
                    "reason TEXT," +
                    "mute_time BIGINT NOT NULL," +
                    "unmute_time BIGINT," +
                    "active TINYINT(1) NOT NULL DEFAULT 1" +
                    ")";
            createVaults = "CREATE TABLE IF NOT EXISTS player_vaults (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "owner_uuid VARCHAR(36) NOT NULL," +
                    "vault_number INT NOT NULL," +
                    "contents TEXT NOT NULL," +
                    "UNIQUE KEY unique_vault (owner_uuid, vault_number)" +
                    ")";
            createBackLocations = "CREATE TABLE IF NOT EXISTS back_locations (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "player_uuid VARCHAR(36) NOT NULL UNIQUE," +
                    "world_name VARCHAR(64) NOT NULL," +
                    "x DOUBLE NOT NULL," +
                    "y DOUBLE NOT NULL," +
                    "z DOUBLE NOT NULL," +
                    "yaw FLOAT NOT NULL," +
                    "pitch FLOAT NOT NULL," +
                    "created_at BIGINT NOT NULL," +
                    "last_online BIGINT NOT NULL DEFAULT 0" +
                    ")";
        }
        
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(createHomes);
            stmt.executeUpdate(createWarps);
            stmt.executeUpdate(createVaults);
            stmt.executeUpdate(createMutes);
            stmt.executeUpdate(createBackLocations);
            
            // Drop kits table if it exists (removing kit system)
            try {
                stmt.executeUpdate("DROP TABLE IF EXISTS kits");
            } catch (SQLException ignored) {}
        } catch (SQLException e) {
            Main.getInstance().getLogger().severe("Failed to create database tables: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
