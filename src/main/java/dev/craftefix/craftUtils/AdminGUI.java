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
import revxrsal.commands.annotation.Command;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public class AdminGUI implements Listener {

    private final JavaPlugin plugin;
    private final Inventory adminInventory;
    private final PlayersGUI playersGUI;
    private final GamemodeGUI gamemodeGUI;
    private final BroadcastGUI broadcastGUI;

    public AdminGUI(JavaPlugin plugin) {
        this.plugin = plugin;
        this.adminInventory = Bukkit.createInventory(null, 9, "Admin GUI");
        this.playersGUI = new PlayersGUI(plugin);
        this.gamemodeGUI = new GamemodeGUI(plugin);
        this.broadcastGUI = new BroadcastGUI(plugin);

        // Create buttons
        ItemStack gamemodeButton = createButton(Material.DIAMOND_SWORD, "Gamemode");
        ItemStack broadcastButton = createButton(Material.PAPER, "Broadcast");
        ItemStack playersButton = createButton(Material.PLAYER_HEAD, "Players");

        // Add buttons to inventory
        adminInventory.setItem(0, gamemodeButton);
        adminInventory.setItem(1, broadcastButton);
        adminInventory.setItem(2, playersButton);

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
    @Command("adminGUI")
    @CommandPermission("CraftNet.craftUtils.adminGUI")
    public void openAdminGUI(Player player) {
        player.openInventory(adminInventory);
    }

    private void openNewGUI(Player player, String title) {
        switch (title) {
            case "Players GUI":
                playersGUI.openPlayersGUI(player);
                break;
            case "Gamemode GUI":
                gamemodeGUI.openGamemodeGUI(player);
                break;
            case "Broadcast GUI":
                broadcastGUI.openBroadcastGUI(player);
                break;
            default:
                Inventory newInventory = Bukkit.createInventory(null, 9, title);
                player.openInventory(newInventory);
                break;
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null || !event.getView().getTitle().equals("Admin GUI")) {
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
            case "Gamemode":
                openNewGUI(player, "Gamemode GUI");
                break;
            case "Broadcast":
                openNewGUI(player, "Broadcast GUI");
                break;
            case "Players":
                openNewGUI(player, "Players GUI");
                break;
            default:
                break;
        }
    }
}