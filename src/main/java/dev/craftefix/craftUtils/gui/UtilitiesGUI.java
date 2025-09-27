package dev.craftefix.craftUtils.gui;

import dev.craftefix.craftUtils.language.LanguageManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Utilities GUI with repair, crafting, anvil, enderchest, vault, and back commands
 */
public class UtilitiesGUI {
    
    public static void openUtilitiesGUI(Player player, JavaPlugin plugin, LanguageManager languageManager) {
        String title = languageManager != null ? 
            languageManager.getMessage(player, "gui.utilities").toString() : 
            "§b§lUtilities";
            
        CustomGUI gui = GUIBuilder.create(plugin, title, 1).build();
        
        int slot = 1;
        
        // Repair command
        if (player.hasPermission("CraftUtils.repair")) {
            GUIItem repairItem = GUIItem.createButton(Material.ANVIL, "§aRepair", event -> {
                GUIFeedbackManager.playClickSound(player);
                player.performCommand("repair");
            }).setLore("§7Repair your held item");
            gui.setItem(slot++, repairItem.getItemStack(), repairItem.getClickHandler());
        }
        
        // Crafting table
        if (player.hasPermission("CraftUtils.crafting")) {
            GUIItem craftingItem = GUIItem.createButton(Material.CRAFTING_TABLE, "§eCrafting Table", event -> {
                GUIFeedbackManager.playClickSound(player);
                player.performCommand("crafting");
            }).setLore("§7Open a portable crafting table");
            gui.setItem(slot++, craftingItem.getItemStack(), craftingItem.getClickHandler());
        }
        
        // Anvil
        if (player.hasPermission("CraftUtils.anvil")) {
            GUIItem anvilItem = GUIItem.createButton(Material.ANVIL, "§6Anvil", event -> {
                GUIFeedbackManager.playClickSound(player);
                player.performCommand("anvil");
            }).setLore("§7Open a portable anvil");
            gui.setItem(slot++, anvilItem.getItemStack(), anvilItem.getClickHandler());
        }
        
        // Enderchest
        if (player.hasPermission("CraftUtils.enderchest")) {
            GUIItem enderchestItem = GUIItem.createButton(Material.ENDER_CHEST, "§dEnder Chest", event -> {
                GUIFeedbackManager.playClickSound(player);
                player.performCommand("enderchest");
            }).setLore("§7Open your ender chest");
            gui.setItem(slot++, enderchestItem.getItemStack(), enderchestItem.getClickHandler());
        }
        
        // Vault
        if (player.hasPermission("CraftUtils.vault")) {
            GUIItem vaultItem = GUIItem.createButton(Material.CHEST, "§bVault", event -> {
                GUIFeedbackManager.playClickSound(player);
                player.performCommand("vault");
            }).setLore("§7Open your personal vault");
            gui.setItem(slot++, vaultItem.getItemStack(), vaultItem.getClickHandler());
        }
        
        // Back command
        if (player.hasPermission("CraftUtils.back")) {
            GUIItem backCommandItem = GUIItem.createButton(Material.COMPASS, "§7Back", event -> {
                GUIFeedbackManager.playClickSound(player);
                player.performCommand("back");
            }).setLore("§7Return to your last location");
            gui.setItem(slot++, backCommandItem.getItemStack(), backCommandItem.getClickHandler());
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