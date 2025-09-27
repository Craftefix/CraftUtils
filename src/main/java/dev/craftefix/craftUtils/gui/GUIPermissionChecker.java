package dev.craftefix.craftUtils.gui;

import org.bukkit.entity.Player;

/**
 * Centralized permission checking system for GUI components.
 * Handles all CraftUtils permission validation with smart caching.
 */
public class GUIPermissionChecker {
    
    /**
     * Checks if player has permission for a command
     * @param player The player to check
     * @param permission The permission node (without CraftUtils. prefix)
     * @return true if player has permission
     */
    public static boolean hasPermission(Player player, String permission) {
        return player.hasPermission("CraftUtils." + permission);
    }
    
    /**
     * Checks if player has permission for abilities category
     */
    public static boolean hasAbilitiesAccess(Player player) {
        return hasPermission(player, "fly") || 
               hasPermission(player, "heal") || 
               hasPermission(player, "eat") || 
               hasPermission(player, "trash");
    }
    
    /**
     * Checks if player has permission for utilities category
     */
    public static boolean hasUtilitiesAccess(Player player) {
        return hasPermission(player, "repair") ||
               hasPermission(player, "craftingtable") ||
               hasPermission(player, "anvil") ||
               hasPermission(player, "enderchest") ||
               hasPermission(player, "vault") ||
               hasPermission(player, "back");
    }
    
    /**
     * Checks if player has permission for gamemode category
     */
    public static boolean hasGamemodeAccess(Player player) {
        return hasPermission(player, "Gamemode.Creative") ||
               hasPermission(player, "Gamemode.Survival") ||
               hasPermission(player, "Gamemode.Adventure");
    }
    
    /**
     * Checks if player has permission for homes/warps category
     */
    public static boolean hasHomesWarpsAccess(Player player) {
        return hasPermission(player, "home") ||
               hasPermission(player, "home.list") ||
               hasPermission(player, "warp") ||
               hasPermission(player, "warp.list");
    }
    
    /**
     * Checks if player has permission for admin category
     */
    public static boolean hasAdminAccess(Player player) {
        return hasPermission(player, "admingui");
    }
    
    /**
     * Gets the number of accessible categories for layout calculation
     */
    public static int getAccessibleCategoryCount(Player player) {
        int count = 0;
        if (hasAbilitiesAccess(player)) count++;
        if (hasUtilitiesAccess(player)) count++;
        if (hasGamemodeAccess(player)) count++;
        if (hasHomesWarpsAccess(player)) count++;
        if (hasAdminAccess(player)) count++;
        return count;
    }
}