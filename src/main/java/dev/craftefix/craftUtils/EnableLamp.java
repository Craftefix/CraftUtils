package dev.craftefix.craftUtils;

import dev.craftefix.craftUtils.commands.*;
import dev.craftefix.craftUtils.database.HomeManager;
import dev.craftefix.craftUtils.database.WarpManager;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import revxrsal.commands.bukkit.BukkitLamp;

import java.util.Map;

public final class EnableLamp {
    private final Main plugin;
    private HomeManager homeManager;

    public EnableLamp(Main plugin) {
        this.plugin = plugin;
        this.homeManager = new HomeManager(); // Initialize homeManager here
    }

    public void enable() {
        var lamp = BukkitLamp.builder(plugin).build();
        int registeredCount = 0;
        int disabledCount = 0;
        int failedCount = 0;
        AdminGUI adminGUI = new AdminGUI();
        WarpManager warpManager = new WarpManager();

        var commandMap = Map.of(
                "tp", new TpCommand(),
                "message", new MessageCommands(),
                "tpAsk", new TpAskCommands(),
                "enderChest", new EnderChestCommands(),
                "adminGUI", new AdminGUICommand(adminGUI),
                "alias", new AliasCommands(),
                "ability", new AbilityCommands(),
                "homes", new HomeCommand(homeManager),
                "warps", new WarpCommand(warpManager),
                "crash", new CrashCommand()
        );

        var commandsSection = plugin.getConfig().getConfigurationSection("commands");
        if (commandsSection == null) {
            plugin.getLogger().severe("Commands section not found in config.yml!");
            return;
        }
        for (String key : commandsSection.getKeys(false)) {
            if (commandMap.containsKey(key)) {
                plugin.getLogger().warning("Command '" + key + "' disabled.");
                disabledCount++;
            }
        }
        for (String commandName : commandsSection.getKeys(true)) {
            if (commandMap.containsKey(commandName)) {
                var command = commandMap.get(commandName);
                lamp.register(command);
                registeredCount++;
                plugin.getLogger().info("Registered command: " + commandName);
            } else {
                plugin.getLogger().warning("Command '" + commandName + "' not found in command map.");
                failedCount++;
            }
        }

        // Always register the core command
        lamp.register(new CraftUtilsCommand());
        plugin.getLogger().info("Registered " + registeredCount + " commands from config.");
        plugin.getLogger().info("Disabled " + disabledCount + " commands from config.");
        plugin.getLogger().info("Couldn't find " + failedCount + " commands in config.");
        registerListeners();
    }

    public void registerListeners(Listener... listeners) {
        PluginManager pluginManager = plugin.getServer().getPluginManager();
        for (Listener listener : listeners) {
            pluginManager.registerEvents(listener, plugin);
        }
    }
}