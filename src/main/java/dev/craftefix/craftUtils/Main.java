package dev.craftefix.craftUtils;


import org.bukkit.plugin.java.JavaPlugin;
import revxrsal.commands.bukkit.BukkitLamp;

public final class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        var lamp = BukkitLamp.builder(this).build();
        lamp.register(new TpCommands());
        lamp.register(new MessageCommands());
        lamp.register(new TpAskCommands());
        lamp.register(new EnderChestCommands());
        lamp.register(new TrashCommands());
        AdminCommands adminCommands = new AdminCommands(this);
        lamp.register(adminCommands);

    }
}