package dev.craftefix.craftUtils;

import dev.craftefix.craftUtils.commands.*;
import dev.craftefix.craftUtils.commands.moderation.MuteCommand;
import dev.craftefix.craftUtils.database.DatabaseManager;
import dev.craftefix.craftUtils.database.HomeManager;
import dev.craftefix.craftUtils.database.WarpManager;
import dev.craftefix.craftUtils.database.PlayerVaultManager;
import dev.craftefix.craftUtils.listeners.PlayerEventListener;
import dev.craftefix.craftUtils.listeners.ModerationEventListener;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import revxrsal.commands.bukkit.BukkitLamp;

import java.util.Map;

public final class EnableLamp {
    private final Main plugin;
    private final DatabaseManager databaseManager;
    private HomeManager homeManager;

    public EnableLamp(Main plugin, DatabaseManager databaseManager) {
        this.plugin = plugin;
        this.databaseManager = databaseManager;
        this.homeManager = new HomeManager(databaseManager);
    }

    public void enable() {
        // Initialize lamp command framework
        var lamp = BukkitLamp.builder(this.plugin)
            .build();

        // Initialize managers and GUI
        AdminGUI adminGUI = new AdminGUI(this.plugin);
        WarpManager warpManager = new WarpManager(databaseManager);
        PlayerVaultManager vaultManager = new PlayerVaultManager(databaseManager);
        MuteCommand muteCommand = new MuteCommand(this.plugin, databaseManager);

        // Map of command names to their implementations
        var commandMap = new java.util.HashMap<String, Object>();
        commandMap.put("tp", new TpCommand());
        commandMap.put("message", new MessageCommands());
        commandMap.put("tpAsk", new TpAskCommands());
        commandMap.put("enderChest", new EnderChestCommands());
        commandMap.put("adminGUI", new AdminGUICommand(adminGUI));
        commandMap.put("alias", new AliasCommands());
        commandMap.put("ability", new AbilityCommands());
        commandMap.put("homes", new HomeCommand(this.homeManager));
        commandMap.put("warps", new WarpCommand(warpManager));
        commandMap.put("utility", new UtilityCommands(vaultManager));
        commandMap.put("mute", muteCommand);
        commandMap.put("pardon", muteCommand);

        // Register commands based on config
        var configSection = plugin.getConfig().getConfigurationSection("commands");
        if (configSection != null) {
            var commands = configSection.getKeys(false);
            for (String command : commands) {
                if (plugin.getConfig().getBoolean("commands." + command)) {
                    var commandInstance = commandMap.get(command);
                    if (commandInstance != null) {
                        lamp.register(commandInstance);
                        plugin.getLogger().info("Registered command: " + command);
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
                lamp.register(entry.getValue());
                plugin.getLogger().info("Registered command (fallback): " + entry.getKey());
            }
        }

        // Register the main CraftUtils command
        lamp.register(new CraftUtilsCommand());
        plugin.getLogger().info("CraftUtils commands initialized successfully.");
        
        // Register event listeners
        registerListeners(
            new PlayerEventListener(plugin, muteCommand.getMuteManager()),
            new ModerationEventListener(plugin)
        );
    }

    public void registerListeners(Listener... listeners) {
        PluginManager pluginManager = plugin.getServer().getPluginManager();
        for (Listener listener : listeners) {
            pluginManager.registerEvents(listener, plugin);
        }
    }
}