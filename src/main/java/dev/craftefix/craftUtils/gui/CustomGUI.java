package dev.craftefix.craftUtils.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Custom GUI base class with automatic light gray glass pane backgrounds
 * and built-in click event handling.
 */
public class CustomGUI implements Listener {
    protected final JavaPlugin plugin;
    protected final Inventory inventory;
    protected final Map<Integer, Consumer<InventoryClickEvent>> clickHandlers;
    protected final ItemStack backgroundItem;

    /**
     * Creates a new CustomGUI with automatic background filling
     * @param plugin The plugin instance
     * @param rows Number of rows (1-6)
     * @param title GUI title
     */
    public CustomGUI(JavaPlugin plugin, int rows, String title) {
        this.plugin = plugin;
        this.inventory = Bukkit.createInventory(null, rows * 9, title);
        this.clickHandlers = new HashMap<>();

        // Create background item (light gray glass pane)
        this.backgroundItem = new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = backgroundItem.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(" ");
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            backgroundItem.setItemMeta(meta);
        }

        // Fill background automatically
        fillBackground();

        // Register event listener
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    /**
     * Fills the entire inventory with background items
     */
    protected void fillBackground() {
        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, backgroundItem.clone());
            }
        }
    }

    /**
     * Sets an item at a specific slot with a click handler
     * @param slot The slot position (0-53)
     * @param item The item to place
     * @param clickHandler Consumer that handles clicks on this item
     */
    public void setItem(int slot, ItemStack item, Consumer<InventoryClickEvent> clickHandler) {
        inventory.setItem(slot, item);
        if (clickHandler != null) {
            clickHandlers.put(slot, clickHandler);
        }
    }

    /**
     * Sets an item at a specific slot without a click handler
     * @param slot The slot position (0-53)
     * @param item The item to place
     */
    public void setItem(int slot, ItemStack item) {
        setItem(slot, item, null);
    }

    /**
     * Sets an item at row/column coordinates
     * @param row Row (0-5)
     * @param col Column (0-8)
     * @param item The item to place
     * @param clickHandler Consumer that handles clicks on this item
     */
    public void setItem(int row, int col, ItemStack item, Consumer<InventoryClickEvent> clickHandler) {
        setItem(row * 9 + col, item, clickHandler);
    }

    /**
     * Sets an item at row/column coordinates without click handler
     * @param row Row (0-5)
     * @param col Column (0-8)
     * @param item The item to place
     */
    public void setItem(int row, int col, ItemStack item) {
        setItem(row, col, item, null);
    }

    /**
     * Opens the GUI for a player
     * @param player The player to show the GUI to
     */
    public void open(Player player) {
        player.openInventory(inventory);
    }

    /**
     * Gets the underlying inventory
     * @return The bukkit inventory
     */
    public Inventory getInventory() {
        return inventory;
    }

    /**
     * Updates the inventory for all viewers
     */
    public void update() {
        // Re-fill background where needed
        fillBackground();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().equals(inventory)) {
            // Only cancel clicks on GUI slots, not player inventory slots
            if (event.getRawSlot() < inventory.getSize()) {
                event.setCancelled(true); // Cancel clicks on GUI items
                
                Consumer<InventoryClickEvent> handler = clickHandlers.get(event.getSlot());
                if (handler != null) {
                    handler.accept(event);
                }
            } else {
                // For player inventory slots, cancel shift-click and double-click actions
                if (event.isShiftClick() || event.getClick() == org.bukkit.event.inventory.ClickType.DOUBLE_CLICK) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        // Optional: handle cleanup if needed
    }
}