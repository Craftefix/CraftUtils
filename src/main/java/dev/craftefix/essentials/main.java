package dev.craftefix.essentials;

import org.bukkit.plugin.java.JavaPlugin;
import revxrsal.commands.bukkit.BukkitLamp;

public final class main extends JavaPlugin {

    @Override
    public void onEnable() {
        try {
            System.out.println("try to save conf");
            // Erstellt oder lädt die Standardkonfiguration
            saveDefaultConfig();

            // Setze das Lamp-Framework auf
            System.out.println("loaded config.yml, trying to register commands" );
            var lamp = BukkitLamp.builder(this).build();
            lamp.register(new tpCommands());
            lamp.register(new messageCommands());
            System.out.println("Loaded commands, trying to check for debug state");
            // Debugging-Info (falls aktiviert in der Konfiguration)
            if (getConfig().getBoolean("developer-settings.enable-logging")) {
                getLogger().info("Debugging aktiviert!");
            }
        } catch (Exception e) {
            System.out.println("cahted exception");
            // Fehler im OnEnable-Block protokollieren
            getLogger().severe("Ein Fehler ist im OnEnable-Prozess aufgetreten: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        try {
            // Alles sauber deaktivieren
            getLogger().info("Plugin wird deaktiviert...");
        } catch (Exception e) {
            // Fehler im OnDisable-Block protokollieren
            getLogger().severe("Ein Fehler ist im OnDisable-Prozess aufgetreten: " + e.getMessage());
            e.printStackTrace();
        }
    }
}