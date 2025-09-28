package dev.craftefix.craftUtils.gui;

import dev.craftefix.craftUtils.language.LanguageManager;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Gamemode GUI with creative, survival, adventure modes and current mode indicator
 */
public class GamemodeGUI {
    
    public static void openGamemodeGUI(Player player, JavaPlugin plugin, LanguageManager languageManager) {
        String title = languageManager != null ? 
            languageManager.getMessage(player, "gui.gamemode").toString() : 
            "§e§lGamemode";
            
        CustomGUI gui = GUIBuilder.create(plugin, title, 1).build();
        
        GameMode currentMode = player.getGameMode();
        
        int slot = 1;
        
        // Creative mode
        if (player.hasPermission("CraftUtils.gamemode.creative")) {
            boolean isCurrent = currentMode == GameMode.CREATIVE;
            Material material = isCurrent ? Material.GOLDEN_SWORD : Material.IRON_SWORD;
            String status = isCurrent ? " §a(CURRENT)" : "";
            
            GUIItem creativeItem = GUIItem.createButton(material, "§bCreative" + status, event -> {
                GUIFeedbackManager.playClickSound(player);
                player.performCommand("gamemode creative");
            }).setLore("§7Switch to creative mode");
            gui.setItem(slot++, creativeItem.getItemStack(), creativeItem.getClickHandler());
        }
        
        // Survival mode
        if (player.hasPermission("CraftUtils.gamemode.survival")) {
            boolean isCurrent = currentMode == GameMode.SURVIVAL;
            Material material = isCurrent ? Material.GOLDEN_PICKAXE : Material.IRON_PICKAXE;
            String status = isCurrent ? " §a(CURRENT)" : "";
            
            GUIItem survivalItem = GUIItem.createButton(material, "§aSurvival" + status, event -> {
                GUIFeedbackManager.playClickSound(player);
                player.performCommand("gamemode survival");
            }).setLore("§7Switch to survival mode");
            gui.setItem(slot++, survivalItem.getItemStack(), survivalItem.getClickHandler());
        }
        
        // Adventure mode
        if (player.hasPermission("CraftUtils.gamemode.adventure")) {
            boolean isCurrent = currentMode == GameMode.ADVENTURE;
            Material material = isCurrent ? Material.GOLDEN_BOOTS : Material.IRON_BOOTS;
            String status = isCurrent ? " §a(CURRENT)" : "";
            
            GUIItem adventureItem = GUIItem.createButton(material, "§eAdventure" + status, event -> {
                GUIFeedbackManager.playClickSound(player);
                player.performCommand("gamemode adventure");
            }).setLore("§7Switch to adventure mode");
            gui.setItem(slot++, adventureItem.getItemStack(), adventureItem.getClickHandler());
        }
        
        // Current mode indicator
        String currentModeText = "§7Current Mode: §e" + currentMode.toString().toLowerCase();
        GUIItem currentModeItem = GUIItem.createButton(Material.COMPASS, currentModeText).setLore("§8Select a different mode above");
        gui.setItem(4, currentModeItem.getItemStack(), currentModeItem.getClickHandler());
        
        // Back to main menu
        String backText = languageManager != null ? 
            languageManager.getMessage(player, "gui.back").toString() : 
            "§7Back to Main Menu";
            
        GUIItem backItem = GUIItem.createButton(Material.ARROW, backText, event -> {
            GUIFeedbackManager.playClickSound(player);
            CraftUtilsMainGUI.openMainGUI(player, plugin, languageManager);
        }).setLore("§7Return to main menu");
        gui.setItem(8, backItem.getItemStack(), backItem.getClickHandler());
            
        gui.open(player);
        GUIFeedbackManager.playClickSound(player);
    }
}