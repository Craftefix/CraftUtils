package dev.craftefix.craftUtils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class BroadcastGUI implements Listener {

    private final JavaPlugin plugin;
    private final Inventory broadcastInventory;

    public BroadcastGUI(JavaPlugin plugin) {
        this.plugin = plugin;
        this.broadcastInventory = Bukkit.createInventory(null, 9, "Broadcast GUI");

        // Create buttons
        ItemStack infoButton = createButton(Material.PAPER, "Info");
        ItemStack warningButton = createButton(Material.PAPER, "Warning");
        ItemStack alertButton = createButton(Material.PAPER, "Alert");

        // Add buttons to inventory
        broadcastInventory.setItem(0, infoButton);
        broadcastInventory.setItem(1, warningButton);
        broadcastInventory.setItem(2, alertButton);

        // Register events
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    private ItemStack createButton(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            item.setItemMeta(meta);
        }
        return item;
    }

    public void openBroadcastGUI(Player player) {
        player.openInventory(broadcastInventory);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null || !event.getView().getTitle().equals("Broadcast GUI")) {
            return;
        }

        event.setCancelled(true); // Prevent item pickup

        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();

        if (clickedItem == null || !clickedItem.hasItemMeta()) {
            return;
        }

        String itemName = clickedItem.getItemMeta().getDisplayName();

        switch (itemName) {
            case "Info":
                Bukkit.broadcastMessage("[INFO] This is an informational message.");
                break;
            case "Warning":
                Bukkit.broadcastMessage("[WARNING] This is a warning message.");
                break;
            case "Alert":
                Bukkit.broadcastMessage("[ALERT] This is an alert message.");
                break;
            default:
                break;
        }
    }
}