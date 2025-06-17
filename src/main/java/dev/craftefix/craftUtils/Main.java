package dev.craftefix.craftUtils;


import dev.craftefix.craftUtils.database.DatabaseManager;
import dev.craftefix.craftUtils.database.HomeManager;
import dev.craftefix.craftUtils.language.LanguageManager;
import dev.craftefix.craftUtils.utils.ColorManager;
import org.bstats.bukkit.Metrics;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class Main extends JavaPlugin implements Listener {
    private static Main instance;
    private DatabaseManager databaseManager;
    private LanguageManager languageManager;
    private YamlConfiguration colors;
    private ColorManager colorManager;

    /**
     * Initializes and enables the plugin, setting up configuration files, core managers, metrics, commands, and event listeners.
     *
     * This method loads the colors configuration, initializes the database and language managers, registers commands and event listeners, sets up plugin metrics, and prepares the color manager. It is called automatically when the plugin is enabled by the server.
     */
    @Override
    public void onEnable() {
        instance = this;

        // Load colors first
        saveResource("colors.yml", false);
        colors = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "colors.yml"));

        // Initialize database
        databaseManager = new DatabaseManager(this);

        // Initialize language manager after the database
        languageManager = new LanguageManager(this, databaseManager);

        // Register the suggestion provider
        HomeManager homeManager = new HomeManager();
        Player player = getServer().getPlayer("playerName"); // Replace "playerName" with the actual player name


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

        // Register language event listeners
        getServer().getPluginManager().registerEvents(new PlayerLanguageListener(), this);

        // Initialize ColorManager after colors.yml is loaded
        colorManager = new ColorManager(this);

        getLogger().info("Plugin enabled successfully.");
    }
    /**
     * Handles plugin shutdown by closing the database connection and logging the disable event.
     *
     * Logs a warning if the database connection cannot be closed.
     */
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
    /**
     * Returns the singleton instance of the plugin main class.
     *
     * @return the current Main plugin instance
     */
    public static Main getInstance() {
        return instance;
    }

    /**
     * Retrieves the language configuration for the specified player.
     *
     * @param player the player whose language configuration is requested
     * @return the YamlConfiguration representing the player's language settings
     */
    public YamlConfiguration getLang(Player player) {
        return languageManager.getPlayerLangConfig(player);
    }

    /**
     * Returns the loaded colors configuration.
     *
     * @return the YamlConfiguration containing color settings
     */
    public YamlConfiguration getColors() {
        return colors;
    }

    /**
     * Returns the LanguageManager instance used by the plugin.
     *
     * @return the LanguageManager responsible for handling player language configurations
     */
    public LanguageManager getLanguageManager() {
        return languageManager;
    }

    /**
     * Returns the ColorManager instance used by the plugin.
     *
     * @return the ColorManager responsible for handling color configurations
     */
    public ColorManager getColorManager() {
        return colorManager;
    }
}

class PlayerLanguageListener implements Listener {
    /**
     * Loads the language configuration for a player when they join the server.
     *
     * @param event the player join event containing the joining player
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Main.getInstance().getLanguageManager().loadPlayerLanguage(event.getPlayer());
    }
}
