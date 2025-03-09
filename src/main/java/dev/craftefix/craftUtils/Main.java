package dev.craftefix.craftUtils;

import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;
import revxrsal.commands.bukkit.BukkitLamp;

public final class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("Plugin is enabling...");

        try {
            Class.forName("craftUtils.libs.gui.BaseGui");
            getLogger().info("Triumph-GUI loaded successfully.");
        } catch (ClassNotFoundException e) {
            getLogger().severe("Failed to load Triumph-GUI. Make sure it's shaded correctly.");
        }


        // Register commands
        var lamp = BukkitLamp.builder(this).build();
        lamp.register(new TpCommands());
        lamp.register(new MessageCommands());
        lamp.register(new TpAskCommands());
        lamp.register(new EnderChestCommands());
        lamp.register(new TrashCommands());
        lamp.register(new AdminCommands(this));
        getLogger().info("Commands registered.");

        // Initialize bStats
        int pluginId = 25026;
        new Metrics(this, pluginId);
        getLogger().info("Metrics initialized.");

        getLogger().info("Plugin enabled successfully.");
    }
}
