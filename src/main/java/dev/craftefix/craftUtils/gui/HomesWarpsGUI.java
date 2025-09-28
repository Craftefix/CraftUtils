package dev.craftefix.craftUtils.gui;

import dev.craftefix.craftUtils.database.HomeManager;
import dev.craftefix.craftUtils.database.WarpManager;
import dev.craftefix.craftUtils.language.LanguageManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

/**
 * Homes & Warps GUI with dynamic loading of player homes and server warps
 */
public class HomesWarpsGUI {
    
    public static void openHomesWarpsGUI(Player player, JavaPlugin plugin, LanguageManager languageManager,
                                        HomeManager homeManager, WarpManager warpManager) {
        String title = languageManager != null ? 
            languageManager.getMessage(player, "gui.homes-warps").toString() : 
            "§d§lHomes & Warps";
            
        CustomGUI gui = GUIBuilder.create(plugin, title, 3).build();
        
        // Separator item
        GUIItem separatorItem = GUIItem.createButton(Material.GRAY_STAINED_GLASS_PANE, "§8Homes & Warps").setLore("§7Homes above, Warps below");
        gui.setItem(13, separatorItem.getItemStack(), separatorItem.getClickHandler());
        
        // Load homes (top row)
        loadHomes(gui, player, homeManager, languageManager);
        
        // Load warps (bottom row)  
        loadWarps(gui, player, warpManager, languageManager);
        
        // Back to main menu
        String backText = languageManager != null ? 
            languageManager.getMessage(player, "gui.back").toString() : 
            "§7Back to Main Menu";
            
        GUIItem backItem = GUIItem.createButton(Material.ARROW, backText, event -> {
            GUIFeedbackManager.playClickSound(player);
            CraftUtilsMainGUI.openMainGUI(player, plugin, languageManager);
        }).setLore("§7Return to main menu");
        gui.setItem(26, backItem.getItemStack(), backItem.getClickHandler());
            
        gui.open(player);
        GUIFeedbackManager.playClickSound(player);
    }
    
    private static void loadHomes(CustomGUI gui, Player player, HomeManager homeManager, LanguageManager languageManager) {
        try {
            if (homeManager == null) {
                GUIItem unavailableItem = GUIItem.createButton(Material.BARRIER, "§cHome System Unavailable").setLore("§7Please try again later");
                gui.setItem(4, unavailableItem.getItemStack(), unavailableItem.getClickHandler());
                return;
            }
            
            if (!player.hasPermission("CraftUtils.home")) {
                GUIItem noPermItem = GUIItem.createButton(Material.BARRIER, "§cNo Home Permission").setLore("§7You need permission to use home commands");
                gui.setItem(4, noPermItem.getItemStack(), noPermItem.getClickHandler());
                return;
            }
            
            List<HomeManager.Home> homes = homeManager.getAllHomes(player.getUniqueId().toString());
            
            if (homes.isEmpty()) {
                GUIItem noHomesItem = GUIItem.createButton(Material.RED_BED, "§eNo Homes Set").setLore("§7Use §e/sethome §7to create one");
                gui.setItem(4, noHomesItem.getItemStack(), noHomesItem.getClickHandler());
            } else {
                int slot = 1;
                for (HomeManager.Home home : homes) {
                    if (slot > 7) break; // Max 7 homes in top row
                    
                    GUIItem homeItem = GUIItem.createButton(Material.GREEN_BED, "§a" + home.getHomeName(), event -> {
                        GUIFeedbackManager.playClickSound(player);
                        player.performCommand("home " + home.getHomeName());
                    }).setLore("§7Click to teleport home");
                    gui.setItem(slot++, homeItem.getItemStack(), homeItem.getClickHandler());
                }
            }
        } catch (Exception e) {
            GUIItem errorItem = GUIItem.createButton(Material.BARRIER, "§cError Loading Homes").setLore("§7Please try again later");
            gui.setItem(4, errorItem.getItemStack(), errorItem.getClickHandler());
        }
    }
    
    private static void loadWarps(CustomGUI gui, Player player, WarpManager warpManager, LanguageManager languageManager) {
        try {
            if (warpManager == null) {
                GUIItem warpUnavailableItem = GUIItem.createButton(Material.BARRIER, "§cWarp System Unavailable").setLore("§7Please try again later");
                gui.setItem(22, warpUnavailableItem.getItemStack(), warpUnavailableItem.getClickHandler());
                return;
            }
            
            if (!player.hasPermission("CraftUtils.warp")) {
                GUIItem warpNoPermItem = GUIItem.createButton(Material.BARRIER, "§cNo Warp Permission").setLore("§7You need permission to use warp commands");
                gui.setItem(22, warpNoPermItem.getItemStack(), warpNoPermItem.getClickHandler());
                return;
            }
            
            List<WarpManager.Warp> warps = warpManager.getAllWarps(player);
            
            if (warps.isEmpty()) {
                GUIItem noWarpsItem = GUIItem.createButton(Material.ENDER_PEARL, "§eNo Warps Available").setLore("§7Contact an admin to create warps");
                gui.setItem(22, noWarpsItem.getItemStack(), noWarpsItem.getClickHandler());
            } else {
                int slot = 19; // Start of bottom row
                for (WarpManager.Warp warp : warps) {
                    if (slot > 25) break; // Max 7 warps in bottom row
                    
                    GUIItem warpItem = GUIItem.createButton(Material.ENDER_PEARL, "§d" + warp.getWarpName(), event -> {
                        GUIFeedbackManager.playClickSound(player);
                        player.performCommand("warp " + warp.getWarpName());
                    }).setLore("§7Click to teleport to warp");
                    gui.setItem(slot++, warpItem.getItemStack(), warpItem.getClickHandler());
                }
            }
        } catch (Exception e) {
            GUIItem warpErrorItem = GUIItem.createButton(Material.BARRIER, "§cError Loading Warps").setLore("§7Please try again later");
            gui.setItem(22, warpErrorItem.getItemStack(), warpErrorItem.getClickHandler());
        }
    }
}