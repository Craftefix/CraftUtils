package dev.craftefix.craftUtils;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import io.papermc.paper.ban.BanListType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;

public class AdminGUI {

    private final ChestGui adminGui;
    private final ChestGui gamemodeGui;
    private final ChestGui broadcastGui;
    private final ChestGui playerListGui;
    private final ChestGui moderatePlayerGui;
    private final PaginatedPane playerPane;
    private final StaticPane moderatePlayerPane;
    private final GuiItem fillerItem;
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

    public AdminGUI(){

        // Create grey filler item
        ItemStack greyFiller = new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE);
        ItemMeta fillerMeta = greyFiller.getItemMeta();
        if (fillerMeta != null) {
            fillerMeta.setDisplayName(" ");
            fillerMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

            greyFiller.setItemMeta(fillerMeta);
        }
        this.fillerItem = new GuiItem(greyFiller);

        // Admin GUI
        adminGui = new ChestGui(1, "Admin GUI");
        StaticPane adminPane = new StaticPane(0, 0, 9, 1);
        adminPane.addItem(new GuiItem(createButton(Material.DIAMOND_SWORD, "Gamemode"), this::openGamemodeGui), 1, 0);
        adminPane.addItem(new GuiItem(createButton(Material.PAPER, "Broadcast"), this::openBroadcastGui), 4, 0);
        adminPane.addItem(new GuiItem(createButton(Material.PLAYER_HEAD, "Players"), this::openSelectorGui), 7, 0);
        adminPane.fillWith(fillerItem.getItem());
        adminGui.addPane(adminPane);

        // Player GUI
        moderatePlayerGui = new ChestGui(1, "Moderate Player");
        moderatePlayerPane = new StaticPane(0, 0, 9, 1);
        moderatePlayerPane.fillWith(fillerItem.getItem());
        moderatePlayerPane.addItem(new GuiItem(createButton(Material.ARROW, "Back"), event -> adminGui.show(event.getWhoClicked())), 8, 0);
        moderatePlayerGui.addPane(moderatePlayerPane);

        // Gamemode GUI
        gamemodeGui = new ChestGui(1, "Gamemode GUI");
        StaticPane gamemodePane = new StaticPane(0, 0, 9, 1);
        gamemodePane.addItem(new GuiItem(createButton(Material.GRASS_BLOCK, "Survival"), event -> event.getWhoClicked().setGameMode(GameMode.SURVIVAL)), 0, 0);
        gamemodePane.addItem(new GuiItem(createButton(Material.DIAMOND_BLOCK, "Creative"), event -> event.getWhoClicked().setGameMode(GameMode.CREATIVE)), 2, 0);
        gamemodePane.addItem(new GuiItem(createButton(Material.MAP, "Adventure"), event -> event.getWhoClicked().setGameMode(GameMode.ADVENTURE)), 4, 0);
        gamemodePane.addItem(new GuiItem(createButton(Material.ENDER_EYE, "Spectator"), event -> event.getWhoClicked().setGameMode(GameMode.SPECTATOR)), 6, 0);
        gamemodePane.fillWith(fillerItem.getItem());
        gamemodePane.addItem(new GuiItem(createButton(Material.ARROW, "Back"), event -> adminGui.show(event.getWhoClicked())), 8, 0);
        gamemodeGui.addPane(gamemodePane);

        // Broadcast GUI
        broadcastGui = new ChestGui(1, "Broadcast GUI");
        StaticPane broadcastPane = new StaticPane(0, 0, 9, 1);
        broadcastPane.addItem(new GuiItem(createButton(Material.POWERED_RAIL, "Shutdown Message"), nouse -> Bukkit.broadcast(Component.text()
                .append(Component.text("Server is shutting down", NamedTextColor.RED).decorate(TextDecoration.BOLD)).build())), 0, 0);
        broadcastPane.addItem(new GuiItem(createButton(Material.POWERED_RAIL, "Restart Message"), nouse -> Bukkit.broadcast(Component.text()
                .append(Component.text("Server is restarting", NamedTextColor.RED).decorate(TextDecoration.BOLD)).build())), 1, 0);
        broadcastPane.addItem(new GuiItem(createButton(Material.POWERED_RAIL, "Maintenance (Soon™) Message"), nouse -> Bukkit.broadcast(Component.text()
                .append(Component.text("Server is going in Maintenance (Soon™)", NamedTextColor.RED).decorate(TextDecoration.BOLD)).build())), 2, 0);

        broadcastPane.addItem(new GuiItem(createButton(Material.ARROW, "Back"), event -> adminGui.show(event.getWhoClicked())), 8, 0);
        broadcastGui.addPane(broadcastPane);

        broadcastPane.fillWith(fillerItem.getItem());

        // Player List GUI with Pagination
        playerListGui = new ChestGui(6, "Player List");
        playerPane = new PaginatedPane(0, 0, 9, 5);
        playerListGui.addPane(playerPane);

        // Navigation Pane
        StaticPane navigationPane = new StaticPane(0, 5, 9, 1);
        navigationPane.addItem(new GuiItem(createButton(Material.ARROW, "Previous Page"), nouse -> {
            if (playerPane.getPage() > 0) {
                playerPane.setPage(playerPane.getPage() - 1);
                playerListGui.update();
            }
        }), 2, 0);

        navigationPane.addItem(new GuiItem(createButton(Material.ARROW, "Next Page"), nouse -> {
            if (playerPane.getPage() < Math.max(playerPane.getPages() - 1, 0)) {
                playerPane.setPage(playerPane.getPage() + 1);
                playerListGui.update();
            }
        }), 7, 0);

        navigationPane.fillWith(fillerItem.getItem());
        playerListGui.addPane(navigationPane);


        // Make items non-removable
        adminGui.setOnGlobalClick(this::cancelEvent);
        gamemodeGui.setOnGlobalClick(this::cancelEvent);
        broadcastGui.setOnGlobalClick(this::cancelEvent);
        playerListGui.setOnGlobalClick(this::cancelEvent);
        moderatePlayerGui.setOnGlobalClick(this::cancelEvent);
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
            if (target.hasPermission("CraftUtils.hide")) {
                continue;
            }
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
            pagePane.fillWith(fillerItem.getItem());
            playerPane.addPane(i, pagePane);
        }
    }

    private void openPlayerGUI(Player executor, Player target) {
        moderatePlayerPane.clear();
        moderatePlayerPane.addItem(new GuiItem(createButton(Material.BARRIER, "Ban"), nouse -> {
            Bukkit.getBanList(BanListType.PROFILE).addBan(target.getName(), "You have been banned", null, null);
            target.kick(Component.text("You have been banned"));
        }), 0, 0);
        moderatePlayerPane.addItem(new GuiItem(createButton(Material.MACE, "Kick"), nouse -> target.kick(Component.text("You have been kicked"))), 1, 0);
        moderatePlayerPane.addItem(new GuiItem(createButton(Material.TNT, "Kill"), nouse -> target.setHealth(0)), 2, 0);
        moderatePlayerPane.addItem(new GuiItem(createButton(Material.STRUCTURE_VOID, "IP-Ban"), nouse -> {
            Bukkit.getBanList(BanListType.IP).addBan(target.getAddress().getHostString(), "You have been IP-banned", null, null);
            target.kick(Component.text("You have been IP-banned")); }), 3, 0);
        moderatePlayerPane.addItem(new GuiItem(createButton(Material.PAPER, "Clear Inventory"), nouse -> target.getInventory().clear()), 4, 0);
        moderatePlayerPane.addItem(new GuiItem(createButton(Material.ENDER_EYE, "Clear Ender Chest"), nouse -> target.getEnderChest().clear()), 5, 0);
        moderatePlayerPane.addItem(new GuiItem(createButton(Material.ENDER_PEARL, "Teleport to"), event -> event.getWhoClicked().teleport(target)), 6, 0);
        moderatePlayerPane.addItem(new GuiItem(createButton(Material.ARROW, "Back"), event -> playerListGui.show(event.getWhoClicked())), 8, 0);

        moderatePlayerPane.fillWith(fillerItem.getItem());
        moderatePlayerGui.addPane(moderatePlayerPane);
        moderatePlayerGui.addPane(moderatePlayerPane);
        moderatePlayerGui.show(executor);
    }
}