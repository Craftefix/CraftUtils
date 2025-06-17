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

    public YamlConfiguration getLang(Player player) {
        return languageManager.getPlayerLangConfig(player);
    }

    public YamlConfiguration getColors() {
        return colors;
    }

    public LanguageManager getLanguageManager() {
        return languageManager;
    }

    public ColorManager getColorManager() {
        return colorManager;
    }
}

class PlayerLanguageListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Main.getInstance().getLanguageManager().loadPlayerLanguage(event.getPlayer());
    }
}
