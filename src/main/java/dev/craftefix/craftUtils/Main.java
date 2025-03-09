import dev.craftefix.craftUtils.*;
import dev.triumphteam.gui.guis.GuiListener;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;
import revxrsal.commands.bukkit.BukkitLamp;

public final class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("Plugin is enabling...");

        // Register the GUI listener
        getServer().getPluginManager().registerEvents(new GuiListener(), this);
        getLogger().info("GuiListener registered.");

        var lamp = BukkitLamp.builder(this).build();
        lamp.register(new TpCommands());
        lamp.register(new MessageCommands());
        lamp.register(new TpAskCommands());
        lamp.register(new EnderChestCommands());
        lamp.register(new TrashCommands());
        lamp.register(new AdminCommands(this));
        getLogger().info("Commands registered.");

        int pluginId = 25026;
        new Metrics(this, pluginId);
        getLogger().info("Metrics initialized.");

        getLogger().info("Plugin enabled successfully.");
    }
}