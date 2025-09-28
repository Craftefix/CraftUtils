package dev.craftefix.craftUtils.migration;

import dev.craftefix.craftUtils.Main;
import dev.craftefix.craftUtils.database.DatabaseManager;

import java.util.logging.Logger;

public abstract class DatabaseMigration implements Migration {
    protected final Main plugin;
    protected final MigrationManager migrationManager;
    protected final Logger logger;
    private final String id;
    private final String description;

    public DatabaseMigration(String id, String description) {
        this.plugin = Main.getInstance();
        this.migrationManager = MigrationManager.getInstance();
        this.logger = plugin.getLogger();
        this.id = id;
        this.description = description;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public MigrationType getType() {
        return MigrationType.DATABASE;
    }

    protected DatabaseManager getDatabaseManager() {
        return migrationManager.getDatabaseManager();
    }
}