package dev.craftefix.craftUtils.gui;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Manages GUI feedback sounds with per-player toggle capability
 */
public class GUIFeedbackManager {
    private static final Map<UUID, Boolean> soundSettings = new HashMap<>();
    private static final Sound GUI_SOUND = Sound.ENTITY_EXPERIENCE_ORB_PICKUP;
    private static final float SOUND_VOLUME = 0.5f;
    private static final float SOUND_PITCH = 1.0f;
    
    /**
     * Plays GUI feedback sound for player if enabled
     * @param player The player to play sound for
     */
    public static void playClickSound(Player player) {
        if (isSoundEnabled(player)) {
            player.playSound(player.getLocation(), GUI_SOUND, SOUND_VOLUME, SOUND_PITCH);
        }
    }
    
    /**
     * Checks if GUI sounds are enabled for player
     * @param player The player to check
     * @return true if sounds are enabled (default: true)
     */
    public static boolean isSoundEnabled(Player player) {
        return soundSettings.getOrDefault(player.getUniqueId(), true);
    }
    
    /**
     * Toggles GUI sound setting for player
     * @param player The player to toggle setting for
     * @return new setting value
     */
    public static boolean toggleSound(Player player) {
        boolean newSetting = !isSoundEnabled(player);
        soundSettings.put(player.getUniqueId(), newSetting);
        return newSetting;
    }
    
    /**
     * Sets GUI sound setting for player
     * @param player The player
     * @param enabled Whether sounds should be enabled
     */
    public static void setSoundEnabled(Player player, boolean enabled) {
        soundSettings.put(player.getUniqueId(), enabled);
    }
    
    /**
     * Clears player's sound setting (cleanup on disconnect)
     * @param player The player
     */
    public static void clearPlayer(Player player) {
        soundSettings.remove(player.getUniqueId());
    }
}