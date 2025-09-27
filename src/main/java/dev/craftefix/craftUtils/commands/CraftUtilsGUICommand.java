package dev.craftefix.craftUtils.commands;

import dev.craftefix.craftUtils.gui.CraftUtilsMainGUI;
import dev.craftefix.craftUtils.gui.GUIFeedbackManager;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.bukkit.annotation.CommandPermission;

/**
 * Main command handler for CraftUtils GUI system
 */
public class CraftUtilsGUICommand {
    private final JavaPlugin plugin;
    
    public CraftUtilsGUICommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Command({"cug", "craftutilsgui", "cu gui"})
    @CommandPermission("CraftUtils.gui")
    public void openGUI(Player player) {
        GUIFeedbackManager.playClickSound(player);
        CraftUtilsMainGUI.openMainGUI(player, plugin);
    }
    
    @Command({"cug sounds", "cu gui sounds"})
    @CommandPermission("CraftUtils.gui")
    public void toggleSounds(Player player) {
        boolean enabled = GUIFeedbackManager.toggleSound(player);
        String status = enabled ? "§aenabled" : "§cdisabled";
        player.sendMessage("§6[CraftUtils] §7GUI sounds " + status + "§7.");
        
        if (enabled) {
            GUIFeedbackManager.playClickSound(player);
        }
    }
}