package dev.craftefix.essentials;

import org.bukkit.plugin.java.JavaPlugin;
import revxrsal.commands.bukkit.BukkitLamp;

public final class main extends JavaPlugin {


    @Override
    public void onEnable () {
        saveDefaultConfig();
        var lamp = BukkitLamp.builder(this).build();
        lamp.register(new tpCommands());
        lamp.register(new messageCommands());
        if (getConfig().getBoolean("developer-settings.enable-logging")) {
            getLogger().info("Debugging aktiviert!");


        }

    }
}
