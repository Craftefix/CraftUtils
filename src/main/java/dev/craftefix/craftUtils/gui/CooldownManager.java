package dev.craftefix.craftUtils.gui;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CooldownManager {
    private static CooldownManager instance;
    private final Map<UUID, Map<String, Long>> playerCooldowns;
    
    // Default cooldowns in seconds
    private static final Map<String, Integer> DEFAULT_COOLDOWNS = new HashMap<>();
    
    static {
        DEFAULT_COOLDOWNS.put("fly", 3);
        DEFAULT_COOLDOWNS.put("heal", 5);
        DEFAULT_COOLDOWNS.put("feed", 5);
        DEFAULT_COOLDOWNS.put("gamemode", 2);
        DEFAULT_COOLDOWNS.put("teleport", 1);
        DEFAULT_COOLDOWNS.put("home", 1);
        DEFAULT_COOLDOWNS.put("warp", 1);
        DEFAULT_COOLDOWNS.put("trash", 1);
        DEFAULT_COOLDOWNS.put("gui", 1);
    }
    
    private CooldownManager() {
        this.playerCooldowns = new HashMap<>();
    }
    
    public static CooldownManager getInstance() {
        if (instance == null) {
            instance = new CooldownManager();
        }
        return instance;
    }
    
    public boolean isOnCooldown(Player player, String action) {
        // Check if player has bypass permission
        if (player.hasPermission("CraftUtils.cooldown.bypass." + action) || 
            player.hasPermission("CraftUtils.cooldown.bypass.*")) {
            return false;
        }
        
        UUID playerId = player.getUniqueId();
        Map<String, Long> cooldowns = playerCooldowns.get(playerId);
        
        if (cooldowns == null) {
            return false;
        }
        
        Long lastUse = cooldowns.get(action);
        if (lastUse == null) {
            return false;
        }
        
        int cooldownTime = getCooldownTime(action);
        long timeElapsed = (System.currentTimeMillis() - lastUse) / 1000;
        
        return timeElapsed < cooldownTime;
    }
    
    public void setCooldown(Player player, String action) {
        // Don't set cooldown if player has bypass permission
        if (player.hasPermission("CraftUtils.cooldown.bypass." + action) || 
            player.hasPermission("CraftUtils.cooldown.bypass.*")) {
            return;
        }
        
        UUID playerId = player.getUniqueId();
        playerCooldowns.computeIfAbsent(playerId, k -> new HashMap<>())
                      .put(action, System.currentTimeMillis());
    }
    
    public long getRemainingCooldown(Player player, String action) {
        // Return 0 if player has bypass permission
        if (player.hasPermission("CraftUtils.cooldown.bypass." + action) || 
            player.hasPermission("CraftUtils.cooldown.bypass.*")) {
            return 0;
        }
        
        UUID playerId = player.getUniqueId();
        Map<String, Long> cooldowns = playerCooldowns.get(playerId);
        
        if (cooldowns == null) {
            return 0;
        }
        
        Long lastUse = cooldowns.get(action);
        if (lastUse == null) {
            return 0;
        }
        
        int cooldownTime = getCooldownTime(action);
        long timeElapsed = (System.currentTimeMillis() - lastUse) / 1000;
        
        return Math.max(0, cooldownTime - timeElapsed);
    }
    
    private int getCooldownTime(String action) {
        return DEFAULT_COOLDOWNS.getOrDefault(action.toLowerCase(), 1);
    }
    
    public void clearCooldowns(Player player) {
        playerCooldowns.remove(player.getUniqueId());
    }
    
    public void clearAllCooldowns() {
        playerCooldowns.clear();
    }
    
    public String formatRemainingTime(long seconds) {
        if (seconds <= 0) {
            return "0s";
        }
        
        if (seconds < 60) {
            return seconds + "s";
        } else if (seconds < 3600) {
            return (seconds / 60) + "m " + (seconds % 60) + "s";
        } else {
            long hours = seconds / 3600;
            long minutes = (seconds % 3600) / 60;
            long secs = seconds % 60;
            return hours + "h " + minutes + "m " + secs + "s";
        }
    }
}