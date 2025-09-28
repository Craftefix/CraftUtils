package dev.craftefix.craftUtils.migration;

import dev.craftefix.craftUtils.Main;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

public class VersionManager {
    private static VersionManager instance;
    private final Main plugin;
    private final Logger logger;
    private final MigrationManager migrationManager;

    private VersionManager(Main plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.migrationManager = MigrationManager.getInstance();
    }

    public static VersionManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("VersionManager not initialized! Call initialize() first.");
        }
        return instance;
    }

    public static void initialize(Main plugin) {
        if (instance == null) {
            instance = new VersionManager(plugin);
        }
    }

    public void initializeVersionTracking() throws SQLException {
        String createVersionTableSQL = """
            CREATE TABLE IF NOT EXISTS craftutils_versions (
                component VARCHAR(50) PRIMARY KEY,
                version VARCHAR(20) NOT NULL,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
            )
            """;

        try (Connection conn = migrationManager.getDatabaseManager().getConnection();
             PreparedStatement stmt = conn.prepareStatement(createVersionTableSQL)) {
            stmt.executeUpdate();
            logger.info("Version tracking table initialized");
        }

        // Initialize versions if they don't exist
        initializeComponentVersion("plugin", getCurrentPluginVersion());
        initializeComponentVersion("database", getCurrentDatabaseVersion());
        initializeComponentVersion("config", getCurrentConfigVersion());
    }

    private void initializeComponentVersion(String component, String version) throws SQLException {
        String selectSQL = "SELECT version FROM craftutils_versions WHERE component = ?";
        String insertSQL = "INSERT INTO craftutils_versions (component, version) VALUES (?, ?)";
        String updateSQL = "UPDATE craftutils_versions SET version = ? WHERE component = ?";

        try (Connection conn = migrationManager.getDatabaseManager().getConnection()) {
            // Check if version exists
            try (PreparedStatement selectStmt = conn.prepareStatement(selectSQL)) {
                selectStmt.setString(1, component);
                ResultSet rs = selectStmt.executeQuery();
                
                if (!rs.next()) {
                    // Version doesn't exist, insert it
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertSQL)) {
                        insertStmt.setString(1, component);
                        insertStmt.setString(2, version);
                        insertStmt.executeUpdate();
                        logger.info("Initialized " + component + " version: " + version);
                    }
                }
            }
        }
    }

    public String getCurrentPluginVersion() {
        return plugin.getDescription().getVersion();
    }

    public String getCurrentConfigVersion() {
        FileConfiguration config = plugin.getConfig();
        return config.getString("version.config", "1.0.0");
    }

    public String getCurrentDatabaseVersion() {
        FileConfiguration config = plugin.getConfig();
        return config.getString("version.database", "1.0.0");
    }

    public String getStoredVersion(String component) throws SQLException {
        String selectSQL = "SELECT version FROM craftutils_versions WHERE component = ?";
        
        try (Connection conn = migrationManager.getDatabaseManager().getConnection();
             PreparedStatement stmt = conn.prepareStatement(selectSQL)) {
            
            stmt.setString(1, component);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getString("version");
            } else {
                return "0.0.0"; // Default version if not found
            }
        }
    }

    public void updateStoredVersion(String component, String version) throws SQLException {
        String selectSQL = "SELECT version FROM craftutils_versions WHERE component = ?";
        String insertSQL = "INSERT INTO craftutils_versions (component, version) VALUES (?, ?)";
        String updateSQL = "UPDATE craftutils_versions SET version = ? WHERE component = ?";
        
        try (Connection conn = migrationManager.getDatabaseManager().getConnection()) {
            // Check if version exists
            try (PreparedStatement selectStmt = conn.prepareStatement(selectSQL)) {
                selectStmt.setString(1, component);
                ResultSet rs = selectStmt.executeQuery();
                
                if (rs.next()) {
                    // Version exists, update it
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateSQL)) {
                        updateStmt.setString(1, version);
                        updateStmt.setString(2, component);
                        updateStmt.executeUpdate();
                    }
                } else {
                    // Version doesn't exist, insert it
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertSQL)) {
                        insertStmt.setString(1, component);
                        insertStmt.setString(2, version);
                        insertStmt.executeUpdate();
                    }
                }
                
                logger.info("Updated " + component + " version to: " + version);
            }
        }
    }

    public boolean isVersionNewer(String currentVersion, String storedVersion) {
        return compareVersions(currentVersion, storedVersion) > 0;
    }

    public int compareVersions(String version1, String version2) {
        String[] v1Parts = version1.split("\\.");
        String[] v2Parts = version2.split("\\.");
        
        int maxLength = Math.max(v1Parts.length, v2Parts.length);
        
        for (int i = 0; i < maxLength; i++) {
            int v1Part = i < v1Parts.length ? parseVersionPart(v1Parts[i]) : 0;
            int v2Part = i < v2Parts.length ? parseVersionPart(v2Parts[i]) : 0;
            
            if (v1Part != v2Part) {
                return Integer.compare(v1Part, v2Part);
            }
        }
        
        return 0; // Versions are equal
    }

    private int parseVersionPart(String part) {
        try {
            return Integer.parseInt(part);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public void checkVersionCompatibility() throws SQLException {
        String pluginVersion = getCurrentPluginVersion();
        String configVersion = getCurrentConfigVersion();
        String databaseVersion = getCurrentDatabaseVersion();
        
        String storedPluginVersion = getStoredVersion("plugin");
        String storedConfigVersion = getStoredVersion("config");
        String storedDatabaseVersion = getStoredVersion("database");
        
        logger.info("Version Check:");
        logger.info("  Plugin: " + pluginVersion + " (stored: " + storedPluginVersion + ")");
        logger.info("  Config: " + configVersion + " (stored: " + storedConfigVersion + ")");
        logger.info("  Database: " + databaseVersion + " (stored: " + storedDatabaseVersion + ")");
        
        if (isVersionNewer(pluginVersion, storedPluginVersion)) {
            logger.info("Plugin version updated from " + storedPluginVersion + " to " + pluginVersion);
            updateStoredVersion("plugin", pluginVersion);
        }
        
        if (isVersionNewer(configVersion, storedConfigVersion)) {
            logger.info("Config version updated from " + storedConfigVersion + " to " + configVersion);
            updateStoredVersion("config", configVersion);
        }
        
        if (isVersionNewer(databaseVersion, storedDatabaseVersion)) {
            logger.info("Database version updated from " + storedDatabaseVersion + " to " + databaseVersion);
            updateStoredVersion("database", databaseVersion);
        }
    }
}