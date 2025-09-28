package dev.craftefix.craftUtils;


import dev.craftefix.craftUtils.database.DatabaseManager;
import dev.craftefix.craftUtils.database.HomeManager;
import dev.craftefix.craftUtils.language.LanguageManager;
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

}
