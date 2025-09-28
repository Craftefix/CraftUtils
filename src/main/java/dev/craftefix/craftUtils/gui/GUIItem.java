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
     * Adds enchantment glint to the item (makes it appear enchanted)
     * @return This GUIItem for chaining
     */
    public GUIItem setGlint(boolean glint) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            if (glint) {
                // Add a fake enchantment to create glint effect
                meta.addEnchant(org.bukkit.enchantments.Enchantment.LURE, 1, true);
                meta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS);
            } else {
                // Remove enchantments
                meta.getEnchants().keySet().forEach(meta::removeEnchant);
                meta.removeItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS);
            }
            itemStack.setItemMeta(meta);
        }
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

    /**
     * Creates a custom head button with base64 texture, name, and click handler
     * @param base64Texture The base64 encoded texture data
     * @param name The display name
     * @param clickHandler The click handler
     * @return A new GUIItem with custom head
     */
    public static GUIItem createCustomHeadButton(String base64Texture, String name, Consumer<InventoryClickEvent> clickHandler) {
        ItemStack customHead = HeadUtils.createCustomHead(base64Texture);
        return new GUIItem(customHead).setName(name).setClickHandler(clickHandler);
    }
}