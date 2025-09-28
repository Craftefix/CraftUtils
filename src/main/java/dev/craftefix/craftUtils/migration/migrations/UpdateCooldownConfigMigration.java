package dev.craftefix.craftUtils.migration.migrations;

import dev.craftefix.craftUtils.migration.ConfigMigration;
import dev.craftefix.craftUtils.migration.MigrationException;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.IOException;

public class UpdateCooldownConfigMigration extends ConfigMigration {

    public UpdateCooldownConfigMigration() {
        super("002_update_cooldown_config", "Update config.yml to add new cooldown settings structure");
    }

    @Override
    public void up() throws MigrationException {
        try {
            createBackup("config.yml");
            
            FileConfiguration config = getPluginConfig();
            
            // Add new cooldown structure if it doesn't exist
            if (!config.contains("cooldowns")) {
                config.set("cooldowns.enabled", true);
                config.set("cooldowns.default.fly", 3);
                config.set("cooldowns.default.heal", 5);
                config.set("cooldowns.default.feed", 5);
                config.set("cooldowns.default.gamemode", 2);
                config.set("cooldowns.default.teleport", 1);
                config.set("cooldowns.default.home", 1);
                config.set("cooldowns.default.warp", 1);
                config.set("cooldowns.default.trash", 1);
                config.set("cooldowns.default.gui", 1);
                
                logger.info("Added cooldown configuration structure");
            }
            
            // Move old teleport cooldown setting if it exists
            if (config.contains("teleport.cooldown")) {
                int oldCooldown = config.getInt("teleport.cooldown", 1);
                config.set("cooldowns.default.teleport", oldCooldown);
                removeConfigKey(config, "teleport.cooldown");
                logger.info("Migrated old teleport cooldown setting");
            }
            
            // Add new GUI settings
            setDefaultIfMissing(config, "gui.sound_effects", true);
            setDefaultIfMissing(config, "gui.pagination_size", 45);
            
            savePluginConfig();
            reloadPluginConfig();
            
        } catch (IOException e) {
            throw new MigrationException("Failed to update cooldown configuration", e);
        }
    }

    @Override
    public void down() throws MigrationException {
        try {
            FileConfiguration config = getPluginConfig();
            
            // Remove cooldown structure
            removeConfigKey(config, "cooldowns");
            
            // Remove GUI settings
            removeConfigKey(config, "gui.sound_effects");
            removeConfigKey(config, "gui.pagination_size");
            
            savePluginConfig();
            reloadPluginConfig();
            
            logger.info("Reverted cooldown configuration changes");
            
        } catch (Exception e) {
            throw new MigrationException("Failed to revert cooldown configuration", e);
        }
    }
}