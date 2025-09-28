package dev.craftefix.craftUtils.gui;

import dev.craftefix.craftUtils.database.HomeManager;
import dev.craftefix.craftUtils.database.WarpManager;
import dev.craftefix.craftUtils.language.LanguageManager;
import org.bukkit.ChatColor;
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
            languageManager.getLegacy(player, "gui.homes-warps") : 
            "§d§lHomes & Warps";
            
        CustomGUI gui = GUIBuilder.create(plugin, title, 3).build();
        CooldownManager cooldownManager = CooldownManager.getInstance();
        
        // Separator item
        GUIItem separatorItem = GUIItem.createButton(Material.GRAY_STAINED_GLASS_PANE, 
            languageManager.getLegacy(player, "gui.homes-warps-separator"))
            .setLore(languageManager.getLegacy(player, "gui.homes-warps-separator-desc"));
        gui.setItem(13, separatorItem.getItemStack(), separatorItem.getClickHandler());
        
        // Load homes (top row)
        loadHomes(gui, player, plugin, homeManager, languageManager, cooldownManager);
        
        // Load warps (bottom row)  
        loadWarps(gui, player, plugin, warpManager, languageManager, cooldownManager);
        
        // Back to main menu
        String backText = languageManager != null ? 
            languageManager.getLegacy(player, "gui.back") : 
            "§7Back to Main Menu";
            
        GUIItem backItem = GUIItem.createButton(Material.ARROW, backText, event -> {
            GUIFeedbackManager.playClickSound(player);
            CraftUtilsMainGUI.openMainGUI(player, plugin, languageManager);
        }).setLore(languageManager.getLegacy(player, "gui.back-desc"));
        gui.setItem(26, backItem.getItemStack(), backItem.getClickHandler());
            
        gui.open(player);
        GUIFeedbackManager.playClickSound(player);
    }
    
    private static void loadHomes(CustomGUI gui, Player player, JavaPlugin plugin, HomeManager homeManager, LanguageManager languageManager, CooldownManager cooldownManager) {
        try {
            if (homeManager == null) {
                GUIItem unavailableItem = GUIItem.createButton(Material.BARRIER, 
                    languageManager.getLegacy(player, "gui.home-unavailable"))
                    .setLore(languageManager.getLegacy(player, "gui.home-unavailable-desc"));
                gui.setItem(4, unavailableItem.getItemStack(), unavailableItem.getClickHandler());
                return;
            }
            
            if (!player.hasPermission("CraftUtils.home")) {
                GUIItem noPermItem = GUIItem.createButton(Material.BARRIER, 
                    languageManager.getLegacy(player, "gui.home-no-permission"))
                    .setLore(languageManager.getLegacy(player, "gui.home-no-permission-desc"));
                gui.setItem(4, noPermItem.getItemStack(), noPermItem.getClickHandler());
                return;
            }
            
            List<HomeManager.Home> homes = homeManager.getAllHomes(player.getUniqueId().toString());
            
            if (homes.isEmpty()) {
                GUIItem noHomesItem = GUIItem.createButton(Material.RED_BED, 
                    languageManager.getLegacy(player, "gui.no-homes"))
                    .setLore(languageManager.getLegacy(player, "gui.no-homes-desc"));
                gui.setItem(4, noHomesItem.getItemStack(), noHomesItem.getClickHandler());
            } else {
                int slot = 1;
                for (HomeManager.Home home : homes) {
                    if (slot > 7) break; // Max 7 homes in top row
                    
                    GUIItem homeItem = GUIItem.createButton(Material.GREEN_BED, "§a" + home.getHomeName(), event -> {
                        if (cooldownManager.isOnCooldown(player, "home")) {
                            long remaining = cooldownManager.getRemainingCooldown(player, "home");
                            String timeStr = cooldownManager.formatRemainingTime(remaining);
                            String cooldownMsg = plugin.getConfig().getString("messages.cooldown-message", 
                                "&cYou must wait {time} before using this again.");
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                                cooldownMsg.replace("{time}", timeStr)));
                            return;
                        }
                        GUIFeedbackManager.playClickSound(player);
                        cooldownManager.setCooldown(player, "home");
                        player.closeInventory();
                        // Execute command with slight delay to ensure GUI is closed first
                        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                            player.performCommand("home " + home.getHomeName());
                        }, 1L);
                    }).setLore(languageManager.getLegacy(player, "gui.home-teleport-desc"));
                    gui.setItem(slot++, homeItem.getItemStack(), homeItem.getClickHandler());
                }
            }
        } catch (Exception e) {
            GUIItem errorItem = GUIItem.createButton(Material.BARRIER, 
                languageManager.getLegacy(player, "gui.home-error"))
                .setLore(languageManager.getLegacy(player, "gui.home-error-desc"));
            gui.setItem(4, errorItem.getItemStack(), errorItem.getClickHandler());
        }
    }
    
    private static void loadWarps(CustomGUI gui, Player player, JavaPlugin plugin, WarpManager warpManager, LanguageManager languageManager, CooldownManager cooldownManager) {
        try {
            if (warpManager == null) {
                GUIItem warpUnavailableItem = GUIItem.createButton(Material.BARRIER, 
                    languageManager.getLegacy(player, "gui.warp-unavailable"))
                    .setLore(languageManager.getLegacy(player, "gui.warp-unavailable-desc"));
                gui.setItem(22, warpUnavailableItem.getItemStack(), warpUnavailableItem.getClickHandler());
                return;
            }
            
            if (!player.hasPermission("CraftUtils.warp")) {
                GUIItem warpNoPermItem = GUIItem.createButton(Material.BARRIER, 
                    languageManager.getLegacy(player, "gui.warp-no-permission"))
                    .setLore(languageManager.getLegacy(player, "gui.warp-no-permission-desc"));
                gui.setItem(22, warpNoPermItem.getItemStack(), warpNoPermItem.getClickHandler());
                return;
            }
            
            List<WarpManager.Warp> warps = warpManager.getAllWarps(player);
            
            if (warps.isEmpty()) {
                GUIItem noWarpsItem = GUIItem.createButton(Material.ENDER_PEARL, 
                    languageManager.getLegacy(player, "gui.no-warps"))
                    .setLore(languageManager.getLegacy(player, "gui.no-warps-desc"));
                gui.setItem(22, noWarpsItem.getItemStack(), noWarpsItem.getClickHandler());
            } else {
                int slot = 19; // Start of bottom row
                for (WarpManager.Warp warp : warps) {
                    if (slot > 25) break; // Max 7 warps in bottom row
                    
                    GUIItem warpItem = GUIItem.createButton(Material.ENDER_PEARL, "§d" + warp.getWarpName(), event -> {
                        if (cooldownManager.isOnCooldown(player, "warp")) {
                            long remaining = cooldownManager.getRemainingCooldown(player, "warp");
                            String timeStr = cooldownManager.formatRemainingTime(remaining);
                            String cooldownMsg = plugin.getConfig().getString("messages.cooldown-message", 
                                "&cYou must wait {time} before using this again.");
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                                cooldownMsg.replace("{time}", timeStr)));
                            return;
                        }
                        GUIFeedbackManager.playClickSound(player);
                        cooldownManager.setCooldown(player, "warp");
                        player.closeInventory();
                        // Execute command with slight delay to ensure GUI is closed first
                        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                            player.performCommand("warp " + warp.getWarpName());
                        }, 1L);
                    }).setLore(languageManager.getLegacy(player, "gui.warp-teleport-desc"));
                    gui.setItem(slot++, warpItem.getItemStack(), warpItem.getClickHandler());
                }
            }
        } catch (Exception e) {
            GUIItem warpErrorItem = GUIItem.createButton(Material.BARRIER, 
                languageManager.getLegacy(player, "gui.warp-error"))
                .setLore(languageManager.getLegacy(player, "gui.warp-error-desc"));
            gui.setItem(22, warpErrorItem.getItemStack(), warpErrorItem.getClickHandler());
        }
    }
}