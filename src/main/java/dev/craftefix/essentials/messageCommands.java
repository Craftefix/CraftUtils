package dev.craftefix.essentials;

import org.bukkit.command.CommandSender;
import revxrsal.commands.annotation.Command;

public class messageCommands {

    @Command("example")
    public void exampleCommand(CommandSender sender, String arg) {
        try {
            sender.sendMessage("Du hast das Argument eingegeben: " + arg);

            // Hier könnte eine komplexere Logik eingefügt werden
        } catch (Exception e) {
            // Fehler beim Ausführen des Commands protokollieren und Spieler benachrichtigen
            sender.sendMessage("Es ist ein Fehler aufgetreten: " + e.getMessage());
            e.printStackTrace();
        }
    }
}