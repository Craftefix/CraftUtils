package dev.craftefix.craftUtils.gui;

import org.bukkit.entity.Player;

/**
 * Interface for GUIs that can be refreshed to show updated data
 */
public interface RefreshableGUI {
    /**
     * Refreshes the GUI content and reopens it for the player
     * @param player The player viewing the GUI
     */
    void refreshAndReopen(Player player);
}