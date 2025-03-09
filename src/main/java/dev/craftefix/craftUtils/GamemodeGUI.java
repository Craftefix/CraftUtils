package dev.craftefix.craftUtils;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class GamemodeGUI implements Listener {

    private final JavaPlugin plugin;
    private final Inventory gamemodeInventory;

    public GamemodeGUI(JavaPlugin plugin) {
        this.plugin = plugin;
        this.gamemodeInventory = Bukkit.createInventory(null, 9, "Gamemode GUI");

        // Create buttons
        ItemStack survivalButton = createButton(Material.GRASS_BLOCK, "Survival");
        ItemStack creativeButton = createButton(Material.DIAMOND_BLOCK, "Creative");
        ItemStack adventureButton = createButton(Material.MAP, "Adventure");
        ItemStack spectatorButton = createButton(Material.ENDER_EYE, "Spectator");

        // Add buttons to inventory
        gamemodeInventory.setItem(0, survivalButton);
        gamemodeInventory.setItem(1, creativeButton);
        gamemodeInventory.setItem(2, adventureButton);
        gamemodeInventory.setItem(3, spectatorButton);

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

    public void openGamemodeGUI(Player player) {
        player.openInventory(gamemodeInventory);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null || !event.getView().getTitle().equals("Gamemode GUI")) {
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
            case "Survival":
                player.setGameMode(GameMode.SURVIVAL);
                player.sendMessage("Gamemode set to Survival.");
                break;
            case "Creative":
                player.setGameMode(GameMode.CREATIVE);
                player.sendMessage("Gamemode set to Creative.");
                break;
            case "Adventure":
                player.setGameMode(GameMode.ADVENTURE);
                player.sendMessage("Gamemode set to Adventure.");
                break;
            case "Spectator":
                player.setGameMode(GameMode.SPECTATOR);
                player.sendMessage("Gamemode set to Spectator.");
                break;
            default:
                break;
        }
    }
}