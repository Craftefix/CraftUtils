package dev.craftefix.craftUtils;


import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic

        // Register the commands
        EnableLamp enableLamp = new EnableLamp(this);
        enableLamp.enable();

        // Create the config file
        getConfig().options().copyDefaults();
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
}
