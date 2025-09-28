package dev.craftefix.craftUtils.migration;

import dev.craftefix.craftUtils.Main;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public abstract class ConfigMigration implements Migration {
    protected final Main plugin;
    protected final Logger logger;
    private final String id;
    private final String description;

    public ConfigMigration(String id, String description) {
        this.plugin = Main.getInstance();
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
        return MigrationType.CONFIG;
    }

    protected FileConfiguration loadConfig(String fileName) throws IOException {
        File configFile = new File(plugin.getDataFolder(), fileName);
        if (!configFile.exists()) {
            throw new IOException("Config file not found: " + fileName);
        }
        return YamlConfiguration.loadConfiguration(configFile);
    }

    protected void saveConfig(FileConfiguration config, String fileName) throws IOException {
        File configFile = new File(plugin.getDataFolder(), fileName);
        config.save(configFile);
        logger.info("Saved config file: " + fileName);
    }

    protected FileConfiguration getPluginConfig() {
        return plugin.getConfig();
    }

    protected void savePluginConfig() {
        plugin.saveConfig();
        logger.info("Saved plugin config.yml");
    }

    protected void reloadPluginConfig() {
        plugin.reloadConfig();
        logger.info("Reloaded plugin config.yml");
    }

    protected boolean configKeyExists(FileConfiguration config, String key) {
        return config.contains(key);
    }

    protected void moveConfigValue(FileConfiguration config, String oldKey, String newKey) {
        if (config.contains(oldKey)) {
            Object value = config.get(oldKey);
            config.set(newKey, value);
            config.set(oldKey, null);
            logger.info("Moved config value from '" + oldKey + "' to '" + newKey + "'");
        }
    }

    protected void setDefaultIfMissing(FileConfiguration config, String key, Object defaultValue) {
        if (!config.contains(key)) {
            config.set(key, defaultValue);
            logger.info("Set default value for config key '" + key + "': " + defaultValue);
        }
    }

    protected void removeConfigKey(FileConfiguration config, String key) {
        if (config.contains(key)) {
            config.set(key, null);
            logger.info("Removed config key: " + key);
        }
    }

    protected void createBackup(String fileName) throws IOException {
        if (!plugin.getConfig().getBoolean("migrations.create-backups", true)) {
            logger.info("Backup creation disabled, skipping backup for: " + fileName);
            return;
        }
        
        File originalFile = new File(plugin.getDataFolder(), fileName);
        if (!originalFile.exists()) {
            return;
        }

        String backupFileName = fileName + ".backup." + System.currentTimeMillis();
        File backupFile = new File(plugin.getDataFolder(), backupFileName);
        
        FileConfiguration original = YamlConfiguration.loadConfiguration(originalFile);
        original.save(backupFile);
        
        logger.info("Created backup: " + backupFileName);
    }
}