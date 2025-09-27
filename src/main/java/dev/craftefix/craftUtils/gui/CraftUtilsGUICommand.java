package dev.craftefix.craftUtils.gui;

import dev.craftefix.craftUtils.Main;
import dev.craftefix.craftUtils.language.LanguageManager;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.annotation.CommandPermission;

/**
 * Main command handler for the CraftUtils GUI system
 */
public class CraftUtilsGUICommand {
    
    private final JavaPlugin plugin;
    
    public CraftUtilsGUICommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Command("cug")
    @CommandPermission("CraftUtils.gui")
    public void onDefault(Player player) {
        // Get language manager from Main plugin instance
        LanguageManager languageManager = null;
        if (plugin instanceof Main mainPlugin) {
            languageManager = mainPlugin.getLanguageManager();
        }
        
        CraftUtilsMainGUI.openMainGUI(player, plugin, languageManager);
    }
    
    @Command("cug sounds")
    @CommandPermission("CraftUtils.gui")
    public void toggleSounds(Player player) {
        boolean newSetting = GUIFeedbackManager.toggleSound(player);
        String status = newSetting ? "§aenabled" : "§cdisabled";
        player.sendMessage("§7GUI sounds " + status + "§7!");
        
        if (newSetting) {
            GUIFeedbackManager.playClickSound(player);
        }
    }
}