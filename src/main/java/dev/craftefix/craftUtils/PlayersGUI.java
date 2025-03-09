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
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class PlayersGUI implements Listener {

    private final JavaPlugin plugin;
    private final Inventory playersInventory;

    public PlayersGUI(JavaPlugin plugin) {
        this.plugin = plugin;
        this.playersInventory = Bukkit.createInventory(null, 54, "Players GUI");

        // Register events
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void openPlayersGUI(Player player) {
        playersInventory.clear();
        List<Player> onlinePlayers = (List<Player>) Bukkit.getOnlinePlayers();
        for (int i = 0; i < onlinePlayers.size() && i < 54; i++) {
            Player onlinePlayer = onlinePlayers.get(i);
            ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) playerHead.getItemMeta();
            if (meta != null) {
                meta.setOwningPlayer(onlinePlayer);
                meta.setDisplayName(onlinePlayer.getName());
                playerHead.setItemMeta(meta);
            }
            playersInventory.setItem(i, playerHead);
        }
        player.openInventory(playersInventory);
    }

    private void openPlayerOptionsGUI(Player player, Player target) {
        Inventory optionsInventory = Bukkit.createInventory(null, 9, "Options for " + target.getName());

        ItemStack banButton = createButton(Material.BARRIER, "Ban");
        ItemStack ipBanButton = createButton(Material.BARRIER, "IP Ban");

        optionsInventory.setItem(0, banButton);
        optionsInventory.setItem(1, ipBanButton);

        player.openInventory(optionsInventory);
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

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();

        if (clickedItem == null || !clickedItem.hasItemMeta()) {
            return;
        }

        String title = event.getView().getTitle();
        String itemName = clickedItem.getItemMeta().getDisplayName();

        if (title.equals("Players GUI")) {
            event.setCancelled(true); // Prevent item pickup
            Player target = Bukkit.getPlayer(itemName);
            if (target != null) {
                openPlayerOptionsGUI(player, target);
            }
        } else if (title.startsWith("Options for ")) {
            event.setCancelled(true); // Prevent item pickup
            String targetName = title.substring(12);
            Player target = Bukkit.getPlayer(targetName);
            if (target != null) {
                switch (itemName) {
                    case "Ban":
                        target.banPlayer("You have been banned.");
                        player.sendMessage(target.getName() + " has been banned.");
                        break;
                    case "IP Ban":
                        Bukkit.banIP(target.getAddress().getAddress().getHostAddress());
                        target.kickPlayer("You have been IP banned.");
                        player.sendMessage(target.getName() + " has been IP banned.");
                        break;
                    default:
                        break;
                }
            }
        }
    }
}