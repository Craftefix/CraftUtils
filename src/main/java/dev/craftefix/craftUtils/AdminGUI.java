package dev.craftefix.craftUtils;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import io.papermc.paper.ban.BanListType;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class AdminGUI {

    private final JavaPlugin plugin;
    private final ChestGui adminGui;
    private final ChestGui gamemodeGui;
    private final ChestGui broadcastGui;
    private final ChestGui playerListGui;
    private final ChestGui moderatePlayerGui;
    private final PaginatedPane playerPane;
    private final StaticPane moderatePlayerPane;


    private void cancelEvent(InventoryClickEvent event) {
        event.setCancelled(true);
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

    public void openAdminGUI(Player player) {
        adminGui.show(player);
    }

    private void openGamemodeGui(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        gamemodeGui.show(player);
    }
    public AdminGUI(JavaPlugin plugin) {
        this.plugin = plugin;


    // Create grey filler item
    ItemStack greyFiller = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
    ItemMeta fillerMeta = greyFiller.getItemMeta();
    if (fillerMeta != null) {
        fillerMeta.setDisplayName(" ");
        greyFiller.setItemMeta(fillerMeta);
    }
    GuiItem fillerItem = new GuiItem(greyFiller);


    // Admin  GUI
    adminGui = new ChestGui(1, "Admin GUI");
    StaticPane adminPane = new StaticPane(0, 0, 9, 1);
    adminPane.addItem(new GuiItem(createButton(Material.DIAMOND_SWORD, "Gamemode"), this::openGamemodeGui), 0, 0);
    adminPane.addItem(new GuiItem(createButton(Material.PAPER, "Broadcast"), this::openBroadcastGui), 1, 0);
    adminPane.addItem(new GuiItem(createButton(Material.PLAYER_HEAD, "Players"), this::openSelectorGui), 2, 0);
    adminPane.fillWith(greyFiller); // Use ItemStack instead of GuiItemadminGui.addPane(adminPane);
    adminGui.addPane(adminPane);


    // Player GUI
    moderatePlayerGui = new ChestGui(1, "Moderate Player");
    moderatePlayerPane = new StaticPane(0, 0, 9, 1);
    moderatePlayerPane.addItem(new GuiItem(createButton(Material.DIAMOND_SWORD, "Ban"), inventoryClickEvent -> {
        Player target = (Player) inventoryClickEvent.getWhoClicked();
        Bukkit.getBanList(BanListType.PROFILE).addBan(target.getName(), "You have been banned", null, null);
        target.kick(Component.text("You have been banned"));
    }), 0, 0);
    moderatePlayerPane.addItem(new GuiItem(createButton(Material.PAPER, "Kick"), inventoryClickEvent -> {
        Player target = (Player) inventoryClickEvent.getWhoClicked();
        target.kick(Component.text("You have been kicked"));
    }), 1, 0);
    moderatePlayerPane.addItem(new GuiItem(createButton(Material.IRON_SWORD, "Kill"), inventoryClickEvent -> {
        Player target = (Player) inventoryClickEvent.getWhoClicked();
        target.setHealth(0);
    }), 2, 0);
    moderatePlayerPane.fillWith(fillerItem.getItem());
    moderatePlayerPane.addItem(new GuiItem(createButton(Material.ARROW, "Back"), event -> adminGui.show((Player) event.getWhoClicked())), 8, 0);
    moderatePlayerGui.addPane(moderatePlayerPane);


    // Gamemode GUI
    gamemodeGui = new ChestGui(1, "Gamemode GUI");
    StaticPane gamemodePane = new StaticPane(0, 0, 9, 1);
    gamemodePane.addItem(new GuiItem(createButton(Material.GRASS_BLOCK, "Survival"), event -> event.getWhoClicked().setGameMode(GameMode.SURVIVAL)), 0, 0);
    gamemodePane.addItem(new GuiItem(createButton(Material.DIAMOND_BLOCK, "Creative"), event -> event.getWhoClicked().setGameMode(GameMode.CREATIVE)), 1, 0);
    gamemodePane.addItem(new GuiItem(createButton(Material.MAP, "Adventure"), event -> event.getWhoClicked().setGameMode(GameMode.ADVENTURE)), 2, 0);
    gamemodePane.addItem(new GuiItem(createButton(Material.ENDER_EYE, "Spectator"), event -> event.getWhoClicked().setGameMode(GameMode.SPECTATOR)), 3, 0);
    gamemodePane.fillWith(fillerItem.getItem());
    gamemodePane.addItem(new GuiItem(createButton(Material.ARROW, "Back"), event -> adminGui.show((Player) event.getWhoClicked())), 8, 0);
    gamemodeGui.addPane(gamemodePane);

    // Broadcast GUI
    broadcastGui = new ChestGui(1, "Broadcast GUI");
    StaticPane broadcastPane = new StaticPane(0, 0, 9, 1);
    broadcastPane.fillWith(fillerItem.getItem());
    broadcastPane.addItem(new GuiItem(createButton(Material.ARROW, "Back"), event -> adminGui.show((Player) event.getWhoClicked())), 8, 0);
    broadcastGui.addPane(broadcastPane);

    // Player List GUI with Pagination
    playerListGui = new ChestGui(6, "Player List");
    playerPane = new PaginatedPane(0, 0, 9, 5);
    playerListGui.addPane(playerPane);

    // Navigation Pane
    StaticPane navigationPane = new StaticPane(0, 5, 9, 1);
    navigationPane.addItem(new GuiItem(createButton(Material.ARROW, "Previous Page"), event -> {
        if (playerPane.getPage() > 0) {
            playerPane.setPage(playerPane.getPage() - 1);
            playerListGui.update();
        }
    }), 2, 0);
    navigationPane.addItem(new GuiItem(createButton(Material.ARROW, "Next Page"), event -> {
        if (playerPane.getPage() < Math.max(playerPane.getPages() - 1, 0)) {
            playerPane.setPage(playerPane.getPage() + 1);
            playerListGui.update();
        }
    }), 6, 0);
    navigationPane.fillWith(fillerItem.getItem());
    navigationPane.addItem(new GuiItem(createButton(Material.ARROW, "Back"), event -> adminGui.show((Player) event.getWhoClicked())), 8, 0);
    playerListGui.addPane(navigationPane);

    // Make items non-removable
    adminGui.setOnGlobalClick(this::cancelEvent);
    gamemodeGui.setOnGlobalClick(this::cancelEvent);
    broadcastGui.setOnGlobalClick(this::cancelEvent);
    playerListGui.setOnGlobalClick(this::cancelEvent);
}

    private void openBroadcastGui(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        broadcastGui.show(player);
    }

    private void openSelectorGui(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        populatePlayerList();
        playerListGui.show(player);
    }

    private void populatePlayerList() {
        playerPane.clear();
        List<GuiItem> playerItems = new ArrayList<>();

        for (Player target : Bukkit.getOnlinePlayers()) {
            ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) skull.getItemMeta();
            meta.setOwningPlayer(target);
            meta.setDisplayName(target.getName());
            skull.setItemMeta(meta);

            GuiItem guiItem = new GuiItem(skull, event -> {
                event.setCancelled(true);
                Player executor = (Player) event.getWhoClicked();
                String targetName = event.getCurrentItem().getItemMeta().getDisplayName();
                Player targetPlayer = Bukkit.getPlayerExact(targetName);
                if (targetPlayer != null) {
                    openPlayerGUI(executor, targetPlayer);
                }
            });

            playerItems.add(guiItem);
        }

        if (playerItems.isEmpty()) {
            ItemStack noPlayers = createButton(Material.BARRIER, "§cNo players online");
            StaticPane noPlayersPane = new StaticPane(0, 0, 9, 5);
            noPlayersPane.addItem(new GuiItem(noPlayers), 4, 2);
            playerPane.addPane(0, noPlayersPane);
            return;
        }

        int itemsPerPage = 45;
        int pageCount = (int) Math.ceil(playerItems.size() / (double) itemsPerPage);

        for (int i = 0; i < pageCount; i++) {
            StaticPane pagePane = new StaticPane(0, 0, 9, 5);
            for (int j = 0; j < itemsPerPage; j++) {
                int index = i * itemsPerPage + j;
                if (index >= playerItems.size()) break;
                int x = j % 9;
                int y = j / 9;
                pagePane.addItem(playerItems.get(index), x, y);
            }
            playerPane.addPane(i, pagePane);
        }
    }

    private void openPlayerGUI(Player executor, Player target) {
        moderatePlayerPane.clear();
        moderatePlayerPane.addItem(new GuiItem(createButton(Material.DIAMOND_SWORD, "Ban"), inventoryClickEvent -> {
            Bukkit.getBanList(BanListType.PROFILE).addBan(target.getName(), "You have been banned", null, null);
            target.kick(Component.text("You have been banned"));
        }), 0, 0);
        moderatePlayerPane.addItem(new GuiItem(createButton(Material.PAPER, "Kick"), inventoryClickEvent -> {
            target.kick(Component.text("You have been kicked"));
        }), 1, 0);
        moderatePlayerPane.addItem(new GuiItem(createButton(Material.IRON_SWORD, "Kill"), inventoryClickEvent -> {
            target.setHealth(0);
        }), 2, 0);
        moderatePlayerGui.addPane(moderatePlayerPane);
        moderatePlayerGui.show(executor);
    }
    private void openPlayerGUI(Player player) {
        moderatePlayerGui.show(player);
    }

}