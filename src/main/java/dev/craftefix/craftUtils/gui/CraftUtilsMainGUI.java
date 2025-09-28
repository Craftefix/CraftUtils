package dev.craftefix.craftUtils.gui;

import dev.craftefix.craftUtils.database.HomeManager;
import dev.craftefix.craftUtils.database.WarpManager;
import dev.craftefix.craftUtils.language.LanguageManager;
import dev.craftefix.craftUtils.EnableLamp;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main CraftUtils GUI that shows available categories based on player permissions
 */
public class CraftUtilsMainGUI {
    
    public static void openMainGUI(Player player, JavaPlugin plugin) {
        openMainGUI(player, plugin, null);
    }
    
    public static void openMainGUI(Player player, JavaPlugin plugin, LanguageManager languageManager) {
        openMainGUI(player, plugin, languageManager, null, null);
    }
    
    public static void openMainGUI(Player player, JavaPlugin plugin, LanguageManager languageManager, 
                                  HomeManager homeManager, WarpManager warpManager) {
        
        String title = languageManager != null ? 
            languageManager.getLegacy(player, "gui.main-title") : 
            "§6§lCraftUtils §8- §7Command GUI";
            
        CustomGUI gui = GUIBuilder.create(plugin, title, 6).build();
        
        int slot = 10; // Start position for categories
        
        // Utilities category
        if (GUIPermissionChecker.hasUtilitiesAccess(player)) {
            String utilitiesName = languageManager != null ? 
                languageManager.getLegacy(player, "gui.utilities") : "§b§lUtilities";
            String utilitiesDesc = languageManager != null ? 
                languageManager.getLegacy(player, "gui.utilities-desc") : "§7Access utility commands";
                
            GUIItem utilitiesItem = GUIItem.createButton(Material.IRON_PICKAXE, utilitiesName, event -> {
                GUIFeedbackManager.playClickSound(player);
                UtilitiesGUI.openUtilitiesGUI(player, plugin, languageManager);
            }).setLore(utilitiesDesc);
            gui.setItem(slot++, utilitiesItem.getItemStack(), utilitiesItem.getClickHandler());
        }
        
        // Abilities category
        if (GUIPermissionChecker.hasAbilitiesAccess(player)) {
            String abilitiesName = languageManager != null ? 
                languageManager.getLegacy(player, "gui.abilities") : "§a§lAbilities";
            String abilitiesDesc = languageManager != null ? 
                languageManager.getLegacy(player, "gui.abilities-desc") : "§7Manage player abilities";
                
            GUIItem abilitiesItem = GUIItem.createButton(Material.FEATHER, abilitiesName, event -> {
                GUIFeedbackManager.playClickSound(player);
                AbilitiesGUI.openAbilitiesGUI(player, plugin, languageManager);
            }).setLore(abilitiesDesc);
            gui.setItem(slot++, abilitiesItem.getItemStack(), abilitiesItem.getClickHandler());
        }
        
        // Gamemode category
        if (GUIPermissionChecker.hasGamemodeAccess(player)) {
            String gamemodeName = languageManager != null ? 
                languageManager.getLegacy(player, "gui.gamemode") : "§e§lGamemode";
            String gamemodeDesc = languageManager != null ? 
                languageManager.getLegacy(player, "gui.gamemode-desc") : "§7Change your gamemode";
                
            GUIItem gamemodeItem = GUIItem.createButton(Material.COMMAND_BLOCK, gamemodeName, event -> {
                GUIFeedbackManager.playClickSound(player);
                GamemodeGUI.openGamemodeGUI(player, plugin, languageManager);
            }).setLore(gamemodeDesc);
            gui.setItem(slot++, gamemodeItem.getItemStack(), gamemodeItem.getClickHandler());
        }
        
        // Homes & Warps category
        if (GUIPermissionChecker.hasHomesWarpsAccess(player)) {
            String homesWarpsName = languageManager != null ? 
                languageManager.getLegacy(player, "gui.homes-warps") : "§d§lHomes & Warps";
            String homesWarpsDesc = languageManager != null ? 
                languageManager.getLegacy(player, "gui.homes-warps-desc") : "§7Teleport to homes and warps";
                
            GUIItem homesWarpsItem = GUIItem.createButton(Material.ENDER_PEARL, homesWarpsName, event -> {
                GUIFeedbackManager.playClickSound(player);
                HomesWarpsGUI.openHomesWarpsGUI(player, plugin, languageManager, homeManager, warpManager);
            }).setLore(homesWarpsDesc);
            gui.setItem(slot++, homesWarpsItem.getItemStack(), homesWarpsItem.getClickHandler());
        }
        
        // Admin category
        if (GUIPermissionChecker.hasAdminAccess(player)) {
            String adminName = languageManager != null ? 
                languageManager.getLegacy(player, "gui.admin") : "§c§lAdmin";
            String adminDesc = languageManager != null ? 
                languageManager.getLegacy(player, "gui.admin-desc") : "§7Access admin commands";
                
            GUIItem adminItem = GUIItem.createButton(Material.REDSTONE, adminName, event -> {
                GUIFeedbackManager.playClickSound(player);
                dev.craftefix.craftUtils.AdminGUI adminGUIInstance = EnableLamp.getAdminGUI();
                if (adminGUIInstance != null) {
                    adminGUIInstance.openAdminGUI(player);
                } else {
                    player.sendMessage("§cAdmin GUI not available");
                }
            }).setLore(adminDesc);
            gui.setItem(slot++, adminItem.getItemStack(), adminItem.getClickHandler());
        }
        
        // Language selection button (bottom row)
        String languageName = languageManager != null ? 
            languageManager.getLegacy(player, "gui.language") : "§f§lLanguage";
        String languageDesc = languageManager != null ? 
            languageManager.getLegacy(player, "gui.language-desc") : "§7Change your language";
            
        GUIItem languageItem = GUIItem.createButton(Material.BOOK, languageName, event -> {
            GUIFeedbackManager.playClickSound(player);
            if (languageManager != null) {
                LanguageGUI.openLanguageGUI(player, plugin, languageManager);
            }
        }).setLore(languageDesc);
        gui.setItem(48, languageItem.getItemStack(), languageItem.getClickHandler());
        
        // Sound toggle button (bottom row)
        String soundName = languageManager != null ? 
            languageManager.getLegacy(player, "gui.sound-settings") : "§e§lSound Settings";
        String soundDesc = languageManager != null ? 
            languageManager.getLegacy(player, "gui.sound-settings-desc") : "§7Toggle GUI sounds";
            
        GUIItem soundItem = GUIItem.createButton(Material.NOTE_BLOCK, soundName, event -> {
            boolean enabled = GUIFeedbackManager.toggleSound(player);
            String status = languageManager != null ? 
                (enabled ? languageManager.getLegacy(player, "gui.sounds-enabled") : 
                           languageManager.getLegacy(player, "gui.sounds-disabled")) :
                (enabled ? "§aGUI sounds enabled!" : "§cGUI sounds disabled!");
                
            player.sendMessage(status);
            
            if (enabled) {
                GUIFeedbackManager.playClickSound(player);
            }
        }).setLore(soundDesc);
        gui.setItem(50, soundItem.getItemStack(), soundItem.getClickHandler());
            
        gui.open(player);
        GUIFeedbackManager.playClickSound(player);
    }
}