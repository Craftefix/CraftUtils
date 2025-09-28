package dev.craftefix.craftUtils.migration;

import dev.craftefix.craftUtils.Main;
import dev.craftefix.craftUtils.database.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Logger;

public class MigrationManager {
    private static MigrationManager instance;
    private final Main plugin;
    private final Logger logger;
    private final DatabaseManager dbManager;
    private final List<Migration> registeredMigrations;
    private final Set<String> executedMigrations;

    private MigrationManager(Main plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.dbManager = plugin.getDatabaseManager();
        this.registeredMigrations = new ArrayList<>();
        this.executedMigrations = new HashSet<>();
    }

    public static MigrationManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("MigrationManager not initialized! Call initialize() first.");
        }
        return instance;
    }

    public static void initialize(Main plugin) {
        if (instance == null) {
            instance = new MigrationManager(plugin);
        }
    }

    public void registerMigration(Migration migration) {
        if (migration == null) {
            throw new IllegalArgumentException("Migration cannot be null");
        }
        registeredMigrations.add(migration);
        logger.info("Registered migration: " + migration.getId());
    }

    public void runMigrations() {
        // Check if migrations are enabled
        if (!plugin.getConfig().getBoolean("migrations.enabled", true)) {
            logger.info("Migration system is disabled in config");
            return;
        }

        try {
            initializeMigrationTable();
            loadExecutedMigrations();
            
            List<Migration> pendingMigrations = getPendingMigrations();
            if (pendingMigrations.isEmpty()) {
                if (plugin.getConfig().getBoolean("migrations.verbose-logging", true)) {
                    logger.info("No pending migrations to execute");
                }
                return;
            }

            logger.info("Found " + pendingMigrations.size() + " pending migrations");
            
            for (Migration migration : pendingMigrations) {
                executeMigration(migration);
            }
            
            logger.info("All migrations completed successfully");
            
        } catch (Exception e) {
            logger.severe("Failed to run migrations: " + e.getMessage());
            
            if (plugin.getConfig().getBoolean("migrations.fail-on-error", true)) {
                throw new RuntimeException("Migration failure", e);
            } else {
                logger.warning("Ignoring migration failure due to configuration setting");
            }
        }
    }

    private void initializeMigrationTable() throws SQLException {
        String createTableSQL = """
            CREATE TABLE IF NOT EXISTS craftutils_migrations (
                id VARCHAR(255) PRIMARY KEY,
                type VARCHAR(50) NOT NULL,
                description TEXT,
                executed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                execution_time_ms BIGINT NOT NULL
            )
            """;
            
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(createTableSQL)) {
            stmt.executeUpdate();
            logger.info("Migration tracking table initialized");
        }
    }

    private void loadExecutedMigrations() throws SQLException {
        String selectSQL = "SELECT id FROM craftutils_migrations";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(selectSQL);
             ResultSet rs = stmt.executeQuery()) {
            
            executedMigrations.clear();
            while (rs.next()) {
                executedMigrations.add(rs.getString("id"));
            }
            
            logger.info("Loaded " + executedMigrations.size() + " executed migrations");
        }
    }

    private List<Migration> getPendingMigrations() {
        return registeredMigrations.stream()
                .filter(migration -> !executedMigrations.contains(migration.getId()))
                .sorted(Comparator.comparing(Migration::getId))
                .toList();
    }

    private void executeMigration(Migration migration) throws MigrationException {
        boolean verboseLogging = plugin.getConfig().getBoolean("migrations.verbose-logging", true);
        
        if (verboseLogging) {
            logger.info("Executing migration: " + migration.getId() + " - " + migration.getDescription());
        }
        
        long startTime = System.currentTimeMillis();
        
        try {
            migration.up();
            long executionTime = System.currentTimeMillis() - startTime;
            
            recordMigrationExecution(migration, executionTime);
            executedMigrations.add(migration.getId());
            
            if (verboseLogging) {
                logger.info("Migration completed: " + migration.getId() + " (" + executionTime + "ms)");
            }
            
        } catch (Exception e) {
            logger.severe("Migration failed: " + migration.getId() + " - " + e.getMessage());
            throw new MigrationException("Failed to execute migration: " + migration.getId(), e);
        }
    }

    private void recordMigrationExecution(Migration migration, long executionTime) throws SQLException {
        String insertSQL = """
            INSERT INTO craftutils_migrations (id, type, description, execution_time_ms) 
            VALUES (?, ?, ?, ?)
            """;
            
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(insertSQL)) {
            
            stmt.setString(1, migration.getId());
            stmt.setString(2, migration.getType().name());
            stmt.setString(3, migration.getDescription());
            stmt.setLong(4, executionTime);
            stmt.executeUpdate();
        }
    }

    public boolean isMigrationExecuted(String migrationId) {
        return executedMigrations.contains(migrationId);
    }

    public List<Migration> getRegisteredMigrations() {
        return new ArrayList<>(registeredMigrations);
    }

    public Set<String> getExecutedMigrations() {
        return new HashSet<>(executedMigrations);
    }

    public DatabaseManager getDatabaseManager() {
        return dbManager;
    }

    public void rollbackMigration(String migrationId) throws MigrationException {
        Migration migration = registeredMigrations.stream()
                .filter(m -> m.getId().equals(migrationId))
                .findFirst()
                .orElseThrow(() -> new MigrationException("Migration not found: " + migrationId));

        if (!executedMigrations.contains(migrationId)) {
            throw new MigrationException("Migration not executed, cannot rollback: " + migrationId);
        }

        logger.info("Rolling back migration: " + migrationId);
        
        try {
            migration.down();
            removeExecutedMigrationRecord(migrationId);
            executedMigrations.remove(migrationId);
            
            logger.info("Migration rollback completed: " + migrationId);
            
        } catch (Exception e) {
            logger.severe("Migration rollback failed: " + migrationId + " - " + e.getMessage());
            throw new MigrationException("Failed to rollback migration: " + migrationId, e);
        }
    }

    private void removeExecutedMigrationRecord(String migrationId) throws SQLException {
        String deleteSQL = "DELETE FROM craftutils_migrations WHERE id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(deleteSQL)) {
            
            stmt.setString(1, migrationId);
            stmt.executeUpdate();
        }
    }
}