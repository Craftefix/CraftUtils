package dev.craftefix.craftUtils.gui;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Fluent builder for creating CustomGUIs
 */
public class GUIBuilder {
    private final JavaPlugin plugin;
    private final String title;
    private final int rows;
    private final List<GUISlot> slots;

    private static class GUISlot {
        final int slot;
        final GUIItem item;

        GUISlot(int slot, GUIItem item) {
            this.slot = slot;
            this.item = item;
        }
    }

    /**
     * Creates a new GUI builder
     * @param plugin The plugin instance
     * @param title GUI title
     * @param rows Number of rows (1-6)
     */
    public GUIBuilder(JavaPlugin plugin, String title, int rows) {
        this.plugin = plugin;
        this.title = title;
        this.rows = rows;
        this.slots = new ArrayList<>();
    }

    /**
     * Adds an item at a specific slot
     * @param slot The slot position (0-53)
     * @param item The GUI item
     * @return This builder for chaining
     */
    public GUIBuilder setItem(int slot, GUIItem item) {
        slots.add(new GUISlot(slot, item));
        return this;
    }

    /**
     * Adds an item at row/column coordinates
     * @param row Row (0-5)
     * @param col Column (0-8)
     * @param item The GUI item
     * @return This builder for chaining
     */
    public GUIBuilder setItem(int row, int col, GUIItem item) {
        return setItem(row * 9 + col, item);
    }

    /**
     * Adds a button at a specific slot
     * @param slot The slot position
     * @param material The material
     * @param name The display name
     * @param clickHandler The click handler
     * @return This builder for chaining
     */
    public GUIBuilder setButton(int slot, Material material, String name, Consumer<InventoryClickEvent> clickHandler) {
        return setItem(slot, GUIItem.createButton(material, name, clickHandler));
    }

    /**
     * Adds a button at row/column coordinates
     * @param row Row
     * @param col Column
     * @param material The material
     * @param name The display name
     * @param clickHandler The click handler
     * @return This builder for chaining
     */
    public GUIBuilder setButton(int row, int col, Material material, String name, Consumer<InventoryClickEvent> clickHandler) {
        return setItem(row * 9 + col, GUIItem.createButton(material, name, clickHandler));
    }

    /**
     * Adds a back button that opens another GUI
     * @param slot The slot position
     * @param targetGUI The GUI to open when clicked
     * @return This builder for chaining
     */
    public GUIBuilder setBackButton(int slot, CustomGUI targetGUI) {
        return setButton(slot, Material.ARROW, "§cBack", event -> targetGUI.open((org.bukkit.entity.Player) event.getWhoClicked()));
    }

    /**
     * Adds a back button at row/column coordinates
     * @param row Row
     * @param col Column
     * @param targetGUI The GUI to open when clicked
     * @return This builder for chaining
     */
    public GUIBuilder setBackButton(int row, int col, CustomGUI targetGUI) {
        return setBackButton(row * 9 + col, targetGUI);
    }

    /**
     * Builds and returns the CustomGUI
     * @return The created CustomGUI
     */
    public CustomGUI build() {
        CustomGUI gui = new CustomGUI(plugin, rows, title);
        for (GUISlot slot : slots) {
            gui.setItem(slot.slot, slot.item.getItemStack(), slot.item.getClickHandler());
        }
        return gui;
    }

    /**
     * Static factory method for creating a builder
     * @param plugin The plugin instance
     * @param title GUI title
     * @param rows Number of rows
     * @return A new GUIBuilder
     */
    public static GUIBuilder create(JavaPlugin plugin, String title, int rows) {
        return new GUIBuilder(plugin, title, rows);
    }
}