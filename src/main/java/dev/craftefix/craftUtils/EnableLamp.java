package dev.craftefix.craftUtils;

import dev.craftefix.craftUtils.commands.*;
import dev.craftefix.craftUtils.commands.moderation.MuteCommand;
import dev.craftefix.craftUtils.gui.CraftUtilsGUICommand;
import dev.craftefix.craftUtils.language.LanguageManager;
import dev.craftefix.craftUtils.database.BackLocationManager;
import dev.craftefix.craftUtils.database.DatabaseManager;
import dev.craftefix.craftUtils.database.HomeManager;
import dev.craftefix.craftUtils.database.WarpManager;
import dev.craftefix.craftUtils.database.PlayerVaultManager;
import dev.craftefix.craftUtils.listeners.PlayerEventListener;
import dev.craftefix.craftUtils.listeners.ModerationEventListener;
import dev.craftefix.craftUtils.listeners.TeleportTrackingListener;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import revxrsal.commands.bukkit.BukkitLamp;

import java.util.Map;

public final class EnableLamp {
    private final Main plugin;
    private final DatabaseManager databaseManager;
    private HomeManager homeManager;
    private LanguageManager languageManager;
    private static AdminGUI adminGUIInstance;

    public EnableLamp(Main plugin, DatabaseManager databaseManager) {
        this.plugin = plugin;
        this.databaseManager = databaseManager;
        this.homeManager = new HomeManager(databaseManager);
    }
    
    public static AdminGUI getAdminGUI() {
        return adminGUIInstance;
    }

    public void enable() {
        // Initialize lamp command framework
        var lamp = BukkitLamp.builder(this.plugin)
            .build();

        // Initialize language system
        this.languageManager = new LanguageManager(this.plugin);
        
        // Initialize managers and GUI
        AdminGUI adminGUI = new AdminGUI(this.plugin);
        adminGUIInstance = adminGUI;
        WarpManager warpManager = new WarpManager(databaseManager);
        PlayerVaultManager vaultManager = new PlayerVaultManager(databaseManager);
        BackLocationManager backLocationManager = new BackLocationManager(databaseManager);
        MuteCommand muteCommand = new MuteCommand(this.plugin, databaseManager);

        // Map of command names to their implementations
        var commandMap = new java.util.HashMap<String, Object>();
        CraftUtilsCommand craftUtilsCommand = new CraftUtilsCommand();
        craftUtilsCommand.setLanguageManager(this.languageManager);
        commandMap.put("cu", craftUtilsCommand);
        commandMap.put("tp", new TpCommand());
        commandMap.put("message", new MessageCommands());
        commandMap.put("tpAsk", new TpAskCommands());
        commandMap.put("enderChest", new EnderChestCommands());
        commandMap.put("adminGUI", new AdminGUICommand(adminGUI));
        commandMap.put("alias", new AliasCommands());
        commandMap.put("ability", new AbilityCommands());
        commandMap.put("homes", new HomeCommand(this.homeManager));
        commandMap.put("warps", new WarpCommand(warpManager));
        UtilityCommands utilityCommands = new UtilityCommands(vaultManager, backLocationManager);
        TeleportTrackingListener teleportTrackingListener = new TeleportTrackingListener(backLocationManager);
        utilityCommands.setTeleportListener(teleportTrackingListener);
        commandMap.put("utility", utilityCommands);
        commandMap.put("mute", muteCommand);
        commandMap.put("pardon", muteCommand);
        commandMap.put("cug", new CraftUtilsGUICommand(plugin));

        // Register commands based on config
        var configSection = plugin.getConfig().getConfigurationSection("commands");
        if (configSection != null) {
            var commands = configSection.getKeys(false);
            for (String command : commands) {
                if (plugin.getConfig().getBoolean("commands." + command)) {
                    var commandInstance = commandMap.get(command);
                    if (commandInstance != null) {
                        plugin.getLogger().info("About to register command: " + command + " (" + commandInstance.getClass().getSimpleName() + ")");
                        try {
                            lamp.register(commandInstance);
                            plugin.getLogger().info("Successfully registered command: " + command);
                        } catch (Exception e) {
                            plugin.getLogger().severe("Failed to register command: " + command + " (" + commandInstance.getClass().getSimpleName() + ")");
                            plugin.getLogger().severe("Error: " + e.getClass().getSimpleName() + ": " + e.getMessage());
                            e.printStackTrace();
                            // Continue with other commands instead of crashing
                        }
                    } else {
                        plugin.getLogger().warning("Unknown command in config: " + command);
                    }
                } else {
                    plugin.getLogger().info("Command disabled in config: " + command);
                }
            }
        } else {
            plugin.getLogger().warning("Commands section not found in config! Loading all commands...");
            // Fallback: register all commands if config section is missing
            for (var entry : commandMap.entrySet()) {
                try {
                    lamp.register(entry.getValue());
                    plugin.getLogger().info("Successfully registered command (fallback): " + entry.getKey());
                } catch (Exception e) {
                    plugin.getLogger().severe("Failed to register command (fallback): " + entry.getKey() + " (" + entry.getValue().getClass().getSimpleName() + ")");
                    plugin.getLogger().severe("Error: " + e.getClass().getSimpleName() + ": " + e.getMessage());
                    e.printStackTrace();
                    // Continue with other commands instead of crashing
                }
            }
        }

        plugin.getLogger().info("CraftUtils commands initialized successfully.");
        
        // Register event listeners
        registerListeners(
            new PlayerEventListener(plugin, muteCommand.getMuteManager(), backLocationManager),
            new ModerationEventListener(plugin),
            teleportTrackingListener,
            utilityCommands
        );
        
        // Start periodic cleanup task for back locations (every 5 minutes)
        plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            backLocationManager.cleanupExpiredLocations();
        }, 20L * 60 * 5, 20L * 60 * 5); // 5 minutes initial delay, then every 5 minutes
    }

    public void registerListeners(Listener... listeners) {
        PluginManager pluginManager = plugin.getServer().getPluginManager();
        for (Listener listener : listeners) {
            pluginManager.registerEvents(listener, plugin);
        }
    }
    
    public LanguageManager getLanguageManager() {
        return languageManager;
    }
}