package dev.craftefix.craftUtils;

import dev.craftefix.craftUtils.commands.*;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import revxrsal.commands.bukkit.BukkitLamp;

import java.util.Map;

public final class EnableLamp {
    private final Main plugin;

    public EnableLamp(Main plugin) {
        this.plugin = plugin;
    }

    public void enable() {
        var lamp = BukkitLamp.builder(this.plugin).build();
        AdminGUI adminGUI = new AdminGUI();

        var commandMap = Map.of(
            "tp", new TpCommands(),
            "message", new MessageCommands(),
            "tpAsk", new TpAskCommands(),
            "enderChest", new EnderChestCommands(),
            "adminGUI", new AdminGUICommand(adminGUI),
            "alias", new AliasCommands(),
            "ability", new AbilityCommands()
        );

        var commands = plugin.getConfig().getConfigurationSection("commands").getKeys(false);
        for (String command : commands) {
            if (plugin.getConfig().getBoolean("commands." + command)) {
                var commandInstance = commandMap.get(command);
                if (commandInstance != null) {
                    lamp.register(commandInstance);
                    plugin.getLogger().info(command + " command registered.");
                } else {
                    plugin.getLogger().warning("Unknown command in config: " + command);
                }
            }
        }
        lamp.register(new CraftUtilsCommand());
        plugin.getLogger().info("Commands registered.");
    }

    public void registerListeners(Listener... listeners) {
        PluginManager pluginManager = plugin.getServer().getPluginManager();
        for (Listener listener : listeners) {
            pluginManager.registerEvents(listener, plugin);
        }
    }
}