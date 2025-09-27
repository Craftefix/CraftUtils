package dev.craftefix.craftUtils.gui;

import dev.craftefix.craftUtils.language.LanguageManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Abilities GUI with fly, heal, feed, and trash commands
 */
public class AbilitiesGUI {
    
    public static void openAbilitiesGUI(Player player, JavaPlugin plugin, LanguageManager languageManager) {
        String title = languageManager != null ? 
            languageManager.getMessage(player, "gui.abilities").toString() : 
            "§a§lAbilities";
            
        CustomGUI gui = GUIBuilder.create(plugin, title, 1).build();
        
        int slot = 1;
        
        // Fly command with status
        if (player.hasPermission("CraftUtils.fly")) {
            boolean canFly = player.getAllowFlight();
            Material material = canFly ? Material.ELYTRA : Material.FEATHER;
            String status = canFly ? " §a(ON)" : " §c(OFF)";
            
            GUIItem flyItem = GUIItem.createButton(material, "§bFly" + status, event -> {
                GUIFeedbackManager.playClickSound(player);
                player.performCommand("fly");
            }).setLore("§7Toggle flight mode");
            gui.setItem(slot++, flyItem.getItemStack(), flyItem.getClickHandler());
        }
        
        // Heal command
        if (player.hasPermission("CraftUtils.heal")) {
            GUIItem healItem = GUIItem.createButton(Material.GOLDEN_APPLE, "§cHeal", event -> {
                GUIFeedbackManager.playClickSound(player);
                player.performCommand("heal");
            }).setLore("§7Restore your health");
            gui.setItem(slot++, healItem.getItemStack(), healItem.getClickHandler());
        }
        
        // Feed command
        if (player.hasPermission("CraftUtils.feed")) {
            GUIItem feedItem = GUIItem.createButton(Material.COOKED_BEEF, "§6Feed", event -> {
                GUIFeedbackManager.playClickSound(player);
                player.performCommand("feed");
            }).setLore("§7Restore your hunger");
            gui.setItem(slot++, feedItem.getItemStack(), feedItem.getClickHandler());
        }
        
        // Trash command
        if (player.hasPermission("CraftUtils.trash")) {
            GUIItem trashItem = GUIItem.createButton(Material.LAVA_BUCKET, "§4Trash", event -> {
                GUIFeedbackManager.playClickSound(player);
                player.performCommand("trash");
            }).setLore("§7Open trash GUI");
            gui.setItem(slot++, trashItem.getItemStack(), trashItem.getClickHandler());
        }
        
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