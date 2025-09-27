package dev.craftefix.craftUtils.gui;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/**
 * Wrapper class for creating GUI items with display names, lore, and click handlers
 */
public class GUIItem {
    private final ItemStack itemStack;
    private Consumer<InventoryClickEvent> clickHandler;

    /**
     * Creates a GUI item from a material
     * @param material The material
     */
    public GUIItem(Material material) {
        this(new ItemStack(material));
    }

    /**
     * Creates a GUI item from an ItemStack
     * @param itemStack The item stack
     */
    public GUIItem(ItemStack itemStack) {
        this.itemStack = itemStack.clone();
    }

    /**
     * Sets the display name of the item
     * @param name The display name
     * @return This GUIItem for chaining
     */
    public GUIItem setName(String name) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            itemStack.setItemMeta(meta);
        }
        return this;
    }

    /**
     * Sets the lore of the item
     * @param lore The lore lines
     * @return This GUIItem for chaining
     */
    public GUIItem setLore(String... lore) {
        return setLore(Arrays.asList(lore));
    }

    /**
     * Sets the lore of the item
     * @param lore The lore lines as a list
     * @return This GUIItem for chaining
     */
    public GUIItem setLore(List<String> lore) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            meta.setLore(new ArrayList<>(lore));
            itemStack.setItemMeta(meta);
        }
        return this;
    }

    /**
     * Adds a line to the lore
     * @param line The lore line to add
     * @return This GUIItem for chaining
     */
    public GUIItem addLore(String line) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            List<String> lore = meta.getLore();
            if (lore == null) {
                lore = new ArrayList<>();
            }
            lore.add(line);
            meta.setLore(lore);
            itemStack.setItemMeta(meta);
        }
        return this;
    }

    /**
     * Sets the click handler for this item
     * @param clickHandler The consumer that handles clicks
     * @return This GUIItem for chaining
     */
    public GUIItem setClickHandler(Consumer<InventoryClickEvent> clickHandler) {
        this.clickHandler = clickHandler;
        return this;
    }

    /**
     * Gets the click handler
     * @return The click handler consumer
     */
    public Consumer<InventoryClickEvent> getClickHandler() {
        return clickHandler;
    }

    /**
     * Gets the underlying ItemStack
     * @return The ItemStack
     */
    public ItemStack getItemStack() {
        return itemStack.clone();
    }

    /**
     * Creates a simple button item with just a material and name
     * @param material The material
     * @param name The display name
     * @return A new GUIItem
     */
    public static GUIItem createButton(Material material, String name) {
        return new GUIItem(material).setName(name);
    }

    /**
     * Creates a simple button item with material, name, and click handler
     * @param material The material
     * @param name The display name
     * @param clickHandler The click handler
     * @return A new GUIItem
     */
    public static GUIItem createButton(Material material, String name, Consumer<InventoryClickEvent> clickHandler) {
        return createButton(material, name).setClickHandler(clickHandler);
    }
}