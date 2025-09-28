package dev.craftefix.craftUtils;


import dev.craftefix.craftUtils.database.DatabaseManager;
import dev.craftefix.craftUtils.database.HomeManager;
import dev.craftefix.craftUtils.language.LanguageManager;
import dev.craftefix.craftUtils.migration.MigrationManager;
import dev.craftefix.craftUtils.migration.VersionManager;
import dev.craftefix.craftUtils.migration.migrations.AddUserPreferencesMigration;
import dev.craftefix.craftUtils.migration.migrations.UpdateCooldownConfigMigration;
import org.bstats.bukkit.Metrics;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {
    private static Main instance;
    private DatabaseManager databaseManager;
    private LanguageManager languageManager;

    @Override
    public void onEnable() {
        instance = this;
        
        // Create the config file
        saveDefaultConfig();
        
        // Initialize database
        try {
            databaseManager = new DatabaseManager();
            databaseManager.initialize();
            getLogger().info("Database initialized successfully (" + databaseManager.getDatabaseType() + ")");
        } catch (Exception e) {
            getLogger().severe("Failed to initialize database: " + e.getMessage());
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Initialize version tracking and migrations
        try {
            MigrationManager.initialize(this);
            VersionManager.initialize(this);
            
            VersionManager versionManager = VersionManager.getInstance();
            MigrationManager migrationManager = MigrationManager.getInstance();
            
            // Initialize version tracking
            versionManager.initializeVersionTracking();
            
            // Check version compatibility
            versionManager.checkVersionCompatibility();
            
            // Register migrations here
            registerMigrations(migrationManager);
            
            // Run pending migrations
            migrationManager.runMigrations();
            getLogger().info("Migration and version system initialized successfully");
        } catch (Exception e) {
            getLogger().severe("Failed to initialize migration/version system: " + e.getMessage());
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Register the commands
        try {
            EnableLamp enableLamp = new EnableLamp(this, databaseManager);
            enableLamp.enable();
            
            // Get language manager from EnableLamp
            this.languageManager = enableLamp.getLanguageManager();
            getLogger().info("Commands and language system initialized successfully.");
        } catch (Exception e) {
            getLogger().severe("Failed to initialize lamp/command system: " + e.getMessage());
            e.printStackTrace();
            
            // Attempt to close database connection to avoid masking the original error
            try {
                if (databaseManager != null) {
                    databaseManager.close();
                }
            } catch (Exception dbCloseException) {
                getLogger().warning("Failed to close database connection during cleanup: " + dbCloseException.getMessage());
            }
            
            // Disable plugin and stop further initialization
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Test Discord integration if enabled
        try {
            if (getConfig().getBoolean("discord.enabled", false)) {
                getServer().getScheduler().runTaskLater(this, () -> {
                    dev.craftefix.craftUtils.discord.DiscordWebhookManager.getInstance(this).testDiscordConnection();
                }, 40L); // 2 second delay to ensure everything is loaded
            }
        } catch (Exception e) {
            getLogger().warning("Failed to test Discord integration: " + e.getMessage());
        }

        // Initialize bStats
        try {
            int pluginId = 25026;
            new Metrics(this, pluginId);
            getLogger().info("Metrics initialized.");
        } catch (Exception exception) {
            getLogger().warning("Failed to initialize bStats, are you Connected to the internet?");
        }

        getLogger().info("Plugin enabled successfully.");
    }
    @Override
    public void onDisable() {
        try {
            if (databaseManager != null) {
                databaseManager.close();
            }
        } catch (Exception exception) {
            getLogger().warning("Failed to close the database connection.");
            getLogger().warning(exception.getMessage());
        }
        getLogger().info("Plugin disabled successfully.");
    }
    public static synchronized Main getInstance() {
        return instance;
    }
    
    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }
    
    public LanguageManager getLanguageManager() {
        return languageManager;
    }

    private void registerMigrations(MigrationManager migrationManager) {
        // Register example migrations
        migrationManager.registerMigration(new AddUserPreferencesMigration());
        migrationManager.registerMigration(new UpdateCooldownConfigMigration());
        
        getLogger().info("Registered migrations with MigrationManager");
    }

}
