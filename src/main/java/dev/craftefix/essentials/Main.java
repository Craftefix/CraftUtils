package dev.craftefix.essentials;

import org.bukkit.plugin.java.JavaPlugin;
import revxrsal.commands.bukkit.BukkitLamp;

public final class Main extends JavaPlugin {


    @Override
    public void onEnable () {
        var lamp = BukkitLamp.builder(this).build();
        lamp.register(new TpCommands());
        lamp.register(new MessageCommands());
        lamp.register(new TpAskCommands());
        // lamp.register(new HomeCommands());
    }
}
