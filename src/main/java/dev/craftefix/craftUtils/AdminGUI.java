package dev.craftefix.craftUtils;

import dev.craftefix.craftUtils.gui.*;
import io.papermc.paper.ban.BanListType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class AdminGUI {

    // Admin GUI for managing players
    // Responsible for creating and managing the Admin GUI with sub-GUIs for players, gamemodes, and broadcasting messages

    // Main Admin GUI
    // - Gamemode GUI: Survival, Creative, Adventure, Spectator
    // - Broadcast GUI: Shutdown, Restart, Maintenance messages
    // - Player List GUI: Moderate Player GUI (Ban, Kick, Kill, IP-Ban, Clear Inventory, Clear Ender Chest, Teleport)

    private final CustomGUI adminGui;
    private final CustomGUI gamemodeGui;
    private final CustomGUI broadcastGui;
    private final PaginatedGUI playerListGui;
    private final CustomGUI moderatePlayerGui;
    private final JavaPlugin plugin;

    public void openAdminGUI(Player player) {
        adminGui.open(player);
    }

    // Creates the Admin GUI with sub-GUIs for managing players, gamemodes, and broadcasting messages
    // Opening is handled by the open*Gui methods below
    public AdminGUI(JavaPlugin plugin){
        this.plugin = plugin;

        // Admin GUI - Main menu
        adminGui = GUIBuilder.create(plugin, "Admin GUI", 1)
            .setButton(1, Material.DIAMOND_SWORD, "§eGamemode", this::openGamemodeGui)
            .setButton(4, Material.PAPER, "§eBroadcast", this::openBroadcastGui)
            .setButton(7, Material.PLAYER_HEAD, "§ePlayers", this::openSelectorGui)
            .build();

        // Gamemode GUI
        gamemodeGui = GUIBuilder.create(plugin, "Gamemode GUI", 1)
            .setButton(0, Material.GRASS_BLOCK, "§aSurvival", event -> changeGamemode(event, GameMode.SURVIVAL))
            .setButton(2, Material.DIAMOND_BLOCK, "§bCreative", event -> changeGamemode(event, GameMode.CREATIVE))
            .setButton(4, Material.MAP, "§eAdventure", event -> changeGamemode(event, GameMode.ADVENTURE))
            .setButton(6, Material.ENDER_EYE, "§7Spectator", event -> changeGamemode(event, GameMode.SPECTATOR))
            .setBackButton(8, adminGui)
            .build();

        // Broadcast GUI
        broadcastGui = GUIBuilder.create(plugin, "Broadcast GUI", 1)
            .setButton(0, Material.POWERED_RAIL, "§cShutdown Message",
                event -> broadcast("Server is shutting down", NamedTextColor.RED))
            .setButton(1, Material.POWERED_RAIL, "§cRestart Message",
                event -> broadcast("Server is restarting", NamedTextColor.RED))
            .setButton(2, Material.POWERED_RAIL, "§cMaintenance (Soon™) Message",
                event -> broadcast("Server is going in Maintenance (Soon™)", NamedTextColor.RED))
            .setBackButton(8, adminGui)
            .build();

        // Player List GUI with Pagination (5 content rows + 1 navigation row)
        playerListGui = new PaginatedGUI(plugin, "Player List", 5);
        playerListGui.setBackButton(5, 8, adminGui); // Back button on navigation row

        // Moderate Player GUI
        moderatePlayerGui = GUIBuilder.create(plugin, "Moderate Player", 1)
            .setBackButton(8, playerListGui)
            .build();
    }
    public void changeGamemode(InventoryClickEvent event, GameMode gameMode) {
        if (event.getWhoClicked().getGameMode() == gameMode) {
            event.getWhoClicked().sendMessage(Component.text("You are already in " + gameMode.toString().toLowerCase(), NamedTextColor.GRAY));
        } else {
            event.getWhoClicked().setGameMode(gameMode);
            event.getWhoClicked().sendMessage(Component.text("Your gamemode has been changed to " + gameMode.toString().toLowerCase(), NamedTextColor.GRAY));
        }
    }

    private void openGamemodeGui(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        gamemodeGui.open(player);
    }

    private void openBroadcastGui(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        broadcastGui.open(player);
    }

    private void openSelectorGui(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        populatePlayerList();
        playerListGui.setPage(0);
        playerListGui.open(player);
    }

    // Populates the player list with online players
    private void populatePlayerList() {
        // Clear existing pages
        playerListGui.clearPages();

        List<GUIItem> playerItems = new ArrayList<>();

        for (Player target : Bukkit.getOnlinePlayers()) {
            if (target.hasPermission("CraftUtils.hide")) {
                continue;
            }

            ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) skull.getItemMeta();
            if (meta != null) {
                meta.setOwningPlayer(target);
                meta.displayName(Component.text(target.getName()));
                skull.setItemMeta(meta);
            }

            GUIItem skullItem = new GUIItem(skull)
                .setClickHandler(event -> {
                    Player executor = (Player) event.getWhoClicked();
                    String targetName = target.getName();
                    Player targetPlayer = Bukkit.getPlayerExact(targetName);
                    if (targetPlayer != null) {
                        openPlayerGUI(executor, targetPlayer);
                    }
                });

            playerItems.add(skullItem);
        }

        if (playerItems.isEmpty()) {
            GUIItem noPlayersItem = GUIItem.createButton(Material.BARRIER, "§cNo players online");
            PaginatedGUI.Page page = new PaginatedGUI.Page();
            page.setItem(2, 4, noPlayersItem); // Center of the page
            playerListGui.addPage(page);
            return;
        }

        // Create pages with 45 items per page (5 rows * 9 columns)
        int itemsPerPage = 45;
        int pageCount = (int) Math.ceil(playerItems.size() / (double) itemsPerPage);

        for (int i = 0; i < pageCount; i++) {
            PaginatedGUI.Page page = new PaginatedGUI.Page();
            for (int j = 0; j < itemsPerPage; j++) {
                int index = i * itemsPerPage + j;
                if (index >= playerItems.size()) break;
                int x = j % 9;
                int y = j / 9;
                page.setItem(y, x, playerItems.get(index));
            }
            playerListGui.addPage(page);
        }
    }

    private void openPlayerGUI(Player executor, Player target) {
        // Recreate the moderate player GUI with target-specific actions
        CustomGUI targetModerateGUI = GUIBuilder.create(plugin, "Moderate " + target.getName(), 1)
            .setButton(0, Material.BARRIER, "§cBan", event -> {
                Bukkit.getBanList(BanListType.PROFILE).addBan(target.getPlayerProfile(), "You have been banned", (java.time.Instant) null, null);
                target.kick(Component.text("You have been banned"));
                executor.sendMessage(Component.text("Banned " + target.getName(), NamedTextColor.RED));
            })
            .setButton(1, Material.LEATHER_BOOTS, "§cKick", event -> {
                target.kick(Component.text("You have been kicked"));
                executor.sendMessage(Component.text("Kicked " + target.getName(), NamedTextColor.RED));
            })
            .setButton(2, Material.TNT, "§cKill", event -> {
                target.setHealth(0);
                executor.sendMessage(Component.text("Killed " + target.getName(), NamedTextColor.RED));
            })
            .setButton(3, Material.STRUCTURE_VOID, "§cIP-Ban", event -> {
                Bukkit.getBanList(BanListType.IP).addBan(target.getAddress().getHostString(), "You have been IP-banned", null, null);
                target.kick(Component.text("You have been IP-banned"));
                executor.sendMessage(Component.text("IP-banned " + target.getName(), NamedTextColor.RED));
            })
            .setButton(4, Material.PAPER, "§eClear Inventory", event -> {
                target.getInventory().clear();
                executor.sendMessage(Component.text("Cleared " + target.getName() + "'s inventory", NamedTextColor.YELLOW));
            })
            .setButton(5, Material.ENDER_EYE, "§eClear Ender Chest", event -> {
                target.getEnderChest().clear();
                executor.sendMessage(Component.text("Cleared " + target.getName() + "'s ender chest", NamedTextColor.YELLOW));
            })
            .setButton(6, Material.ENDER_PEARL, "§aTeleport to", event -> {
                event.getWhoClicked().teleport(target);
                executor.sendMessage(Component.text("Teleported to " + target.getName(), NamedTextColor.GREEN));
            })
            .setBackButton(8, playerListGui)
            .build();

        targetModerateGUI.open(executor);
    }

    private void broadcast(String message, NamedTextColor color) {
        Bukkit.broadcast(Component.text(message, color).decorate(TextDecoration.BOLD));
    }
}