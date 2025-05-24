package dev.craftefix.craftUtils;


import dev.craftefix.craftUtils.database.DatabaseManager;
import dev.craftefix.craftUtils.database.HomeManager;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {
    private static Main instance;


    @Override
    public void onEnable() {
        instance = this;

        HomeManager homeManager = new HomeManager();



        // Register the commands
        EnableLamp enableLamp = new EnableLamp(this);
        enableLamp.enable();

        // Create the config file
        saveDefaultConfig();

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
            DatabaseManager.close();
        } catch (Exception exception) {
            getLogger().warning("Failed to close the database connection.");
            getLogger().warning(exception.getMessage());
        }
        getLogger().info("Plugin disabled successfully.");
    }
    public static Main getInstance() {
        return instance;
    }

}
