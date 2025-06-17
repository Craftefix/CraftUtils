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
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;

public class AdminGUI {
    private final YamlConfiguration lang;

    public AdminGUI() {
        this.lang = Main.getInstance().getLang();
    }


    public void openAdminGUI(Player player) {
        ChestGui adminGui = new ChestGui(3, lang.getString("admin-gui.titles.main", "Admin GUI"));
        StaticPane adminPane = new StaticPane(0, 0, 9, 1);

        adminPane.addItem(new GuiItem(createButton(Material.DIAMOND_SWORD, lang.getString("admin-gui.buttons.gamemode", "Gamemode")), this::openGamemodeGui), 1, 0);
        adminPane.addItem(new GuiItem(createButton(Material.PAPER, lang.getString("admin-gui.buttons.broadcast", "Broadcast")), this::openBroadcastGui), 4, 0);
        adminPane.addItem(new GuiItem(createButton(Material.PLAYER_HEAD, lang.getString("admin-gui.buttons.players", "Players")), this::openSelectorGui), 7, 0);
        adminPane.fillWith(fillerItem.getItem());
        adminGui.addPane(adminPane);

        // Player GUI
        ChestGui moderatePlayerGui = new ChestGui(1, lang.getString("admin-gui.titles.moderate-player", "Moderate Player"));
        StaticPane moderatePlayerPane = new StaticPane(0, 0, 9, 1);
        moderatePlayerPane.fillWith(fillerItem.getItem());
        moderatePlayerPane.addItem(new GuiItem(createButton(Material.ARROW, lang.getString("admin-gui.buttons.back", "Back")), event -> adminGui.show(event.getWhoClicked())), 8, 0);
        moderatePlayerGui.addPane(moderatePlayerPane);

        // Gamemode GUI
        ChestGui gamemodeGui = new ChestGui(1, lang.getString("admin-gui.titles.gamemode", "Gamemode GUI"));
        StaticPane gamemodePane = new StaticPane(0, 0, 9, 1);
        gamemodePane.addItem(new GuiItem(createButton(Material.GRASS_BLOCK, lang.getString("admin-gui.buttons.survival", "Survival")), event -> changeGamemode(event, GameMode.SURVIVAL)), 0, 0);
        gamemodePane.addItem(new GuiItem(createButton(Material.DIAMOND_BLOCK, lang.getString("admin-gui.buttons.creative", "Creative")), event -> changeGamemode(event, GameMode.CREATIVE)), 2, 0);
        gamemodePane.addItem(new GuiItem(createButton(Material.MAP, lang.getString("admin-gui.buttons.adventure", "Adventure")), event -> changeGamemode(event, GameMode.ADVENTURE)), 4, 0);
        gamemodePane.addItem(new GuiItem(createButton(Material.ENDER_EYE, lang.getString("admin-gui.buttons.spectator", "Spectator")), event -> changeGamemode(event, GameMode.SPECTATOR)), 6, 0);
        gamemodePane.fillWith(fillerItem.getItem());
        gamemodePane.addItem(new GuiItem(createButton(Material.ARROW, lang.getString("admin-gui.buttons.back", "Back")), event -> adminGui.show(event.getWhoClicked())), 8, 0);
        gamemodeGui.addPane(gamemodePane);


        // Broadcast GUI
        ChestGui broadcastGui = new ChestGui(1, lang.getString("admin-gui.titles.broadcasts", "Broadcast Messages"));
        StaticPane broadcastPane = new StaticPane(0, 0, 9, 1);
        broadcastPane.addItem(new GuiItem(createButton(Material.POWERED_RAIL, "Shutdown Message"), nouse -> Bukkit.broadcast(Component.text()
                .append(Component.text(lang.getString("admin-gui.messages.shutdown", "Server is shutting down"), NamedTextColor.RED)
                        .decorate(TextDecoration.BOLD)).build())), 0, 0);
        broadcastPane.addItem(new GuiItem(createButton(Material.POWERED_RAIL, "Restart Message"), nouse -> Bukkit.broadcast(Component.text()
                .append(Component.text(lang.getString("admin-gui.messages.restart", "Server is restarting"), NamedTextColor.RED)
                        .decorate(TextDecoration.BOLD)).build())), 1, 0);
        broadcastPane.addItem(new GuiItem(createButton(Material.POWERED_RAIL, "Maintenance Message"), nouse -> Bukkit.broadcast(Component.text()
                .append(Component.text(lang.getString("admin-gui.messages.maintenance", "Server is going in Maintenance"), NamedTextColor.RED)
                        .decorate(TextDecoration.BOLD)).build())), 2, 0);

        broadcastPane.addItem(new GuiItem(createButton(Material.ARROW, lang.getString("admin-gui.buttons.back", "Back")), event -> adminGui.show(event.getWhoClicked())), 8, 0);
        broadcastGui.addPane(broadcastPane);

        broadcastPane.fillWith(fillerItem.getItem());

        // Player List GUI with Pagination
        ChestGui playerListGui = new ChestGui(6, lang.getString("admin-gui.titles.player-list", "Player List"));
        PaginatedPane playerPane = new PaginatedPane(0, 0, 9, 5);
        playerListGui.addPane(playerPane);

        // Navigation Pane
        StaticPane navigationPane = new StaticPane(0, 5, 9, 1);
        navigationPane.addItem(new GuiItem(createButton(Material.SPECTRAL_ARROW, lang.getString("admin-gui.buttons.previous-page", "Previous Page")), nouse -> {
            if (playerPane.getPage() > 0) {
                playerPane.setPage(playerPane.getPage() - 1);
                playerListGui.update();
            }
        }), 2, 0);

        navigationPane.addItem(new GuiItem(createButton(Material.SPECTRAL_ARROW, lang.getString("admin-gui.buttons.next-page", "Next Page")), nouse -> {
            if (playerPane.getPage() < Math.max(playerPane.getPages() - 1, 0)) {
                playerPane.setPage(playerPane.getPage() + 1);
                playerListGui.update();
            }
        }), 6, 0);
        navigationPane.addItem(new GuiItem(createButton(Material.ARROW, lang.getString("admin-gui.buttons.back", "Back")), event -> adminGui.show(event.getWhoClicked())), 8, 0);

        navigationPane.fillWith(fillerItem.getItem());
        playerListGui.addPane(navigationPane);


        // Make items non-removable
        adminGui.setOnGlobalClick(this::cancelEvent);
        gamemodeGui.setOnGlobalClick(this::cancelEvent);
        broadcastGui.setOnGlobalClick(this::cancelEvent);
        playerListGui.setOnGlobalClick(this::cancelEvent);
        moderatePlayerGui.setOnGlobalClick(this::cancelEvent);
    }
    public void changeGamemode(InventoryClickEvent event, GameMode gameMode) {
        if (event.getWhoClicked().getGameMode() == gameMode) {
            event.getWhoClicked().sendMessage(Component.text(lang.getString("admin-gui.messages.gamemode-already", "You are already in {gamemode}")
                    .replace("{gamemode}", gameMode.toString().toLowerCase()), NamedTextColor.GRAY));
        } else {
            event.getWhoClicked().setGameMode(gameMode);
            event.getWhoClicked().sendMessage(Component.text(lang.getString("admin-gui.messages.gamemode-changed", "Your gamemode has been changed to {gamemode}")
                    .replace("{gamemode}", gameMode.toString().toLowerCase()), NamedTextColor.GRAY));
        }
    }

    private void openGamemodeGui(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ChestGui gui = new ChestGui(3, lang.getString("admin-gui.titles.gamemode", "Gamemode Selection"));
        StaticPane gamemodePane = new StaticPane(0, 0, 9, 3);

        addGamemodeButton(gamemodePane, player, GameMode.CREATIVE, Material.GRASS_BLOCK, 0, 0);
        addGamemodeButton(gamemodePane, player, GameMode.SURVIVAL, Material.WOODEN_SWORD, 1, 0);
        addGamemodeButton(gamemodePane, player, GameMode.ADVENTURE, Material.MAP, 2, 0);
        addGamemodeButton(gamemodePane, player, GameMode.SPECTATOR, Material.ENDER_EYE, 3, 0);

        gui.addPane(gamemodePane);
        gui.show(player);
    }

    private void openBroadcastGui(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ChestGui gui = new ChestGui(3, lang.getString("admin-gui.titles.broadcasts", "Broadcast Messages"));
        StaticPane broadcastPane = new StaticPane(0, 0, 9, 3);

        broadcastPane.addItem(new GuiItem(createButton(Material.POWERED_RAIL, "Shutdown Message"), nouse -> Bukkit.broadcast(Component.text()
                .append(Component.text(lang.getString("admin-gui.messages.shutdown", "Server is shutting down"), NamedTextColor.RED)
                        .decorate(TextDecoration.BOLD)).build())), 0, 0);

        broadcastPane.addItem(new GuiItem(createButton(Material.POWERED_RAIL, "Restart Message"), nouse -> Bukkit.broadcast(Component.text()
                .append(Component.text(lang.getString("admin-gui.messages.restart", "Server is restarting"), NamedTextColor.RED)
                        .decorate(TextDecoration.BOLD)).build())), 1, 0);

        broadcastPane.addItem(new GuiItem(createButton(Material.POWERED_RAIL, "Maintenance Message"), nouse -> Bukkit.broadcast(Component.text()
                .append(Component.text(lang.getString("admin-gui.messages.maintenance", "Server is going in Maintenance"), NamedTextColor.RED)
                        .decorate(TextDecoration.BOLD)).build())), 2, 0);

        gui.addPane(broadcastPane);
        gui.show(player);
    }

    private void openSelectorGui(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ChestGui playerListGui = new ChestGui(6, lang.getString("admin-gui.titles.player-list", "Player List"));
        PaginatedPane playerPane = new PaginatedPane(0, 0, 9, 5);
        playerListGui.addPane(playerPane);

        // Navigation Pane
        StaticPane navigationPane = new StaticPane(0, 5, 9, 1);
        navigationPane.addItem(new GuiItem(createButton(Material.SPECTRAL_ARROW, lang.getString("admin-gui.buttons.previous-page", "Previous Page")), nouse -> {
            if (playerPane.getPage() > 0) {
                playerPane.setPage(playerPane.getPage() - 1);
                playerListGui.update();
            }
        }), 2, 0);

        navigationPane.addItem(new GuiItem(createButton(Material.SPECTRAL_ARROW, lang.getString("admin-gui.buttons.next-page", "Next Page")), nouse -> {
            if (playerPane.getPage() < Math.max(playerPane.getPages() - 1, 0)) {
                playerPane.setPage(playerPane.getPage() + 1);
                playerListGui.update();
            }
        }), 6, 0);
        navigationPane.addItem(new GuiItem(createButton(Material.ARROW, lang.getString("admin-gui.buttons.back", "Back")), event -> adminGui.show(event.getWhoClicked())), 8, 0);

        navigationPane.fillWith(fillerItem.getItem());
        playerListGui.addPane(navigationPane);

        // Populates the player list with online players
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

        playerListGui.show(player);
    }

    private void openPlayerGUI(Player executor, Player target) {
        ChestGui moderatePlayerGui = new ChestGui(1, lang.getString("admin-gui.titles.moderate-player", "Moderate Player"));
        StaticPane moderatePlayerPane = new StaticPane(0, 0, 9, 1);
        moderatePlayerPane.clear();
        moderatePlayerPane.addItem(new GuiItem(createButton(Material.BARRIER, "Ban"), nouse -> {
            Bukkit.getBanList(BanListType.PROFILE).addBan(target.getName(), null, null, null);
            target.kick(Component.text(lang.getString("moderation.ban.message", "You have been banned")));
        }), 0, 0);
        moderatePlayerPane.addItem(new GuiItem(createButton(Material.MACE, "Kick"), nouse -> target.kick(Component.text(lang.getString("moderation.kick.message", "You have been kicked")))), 1, 0);
        moderatePlayerPane.addItem(new GuiItem(createButton(Material.TNT, "Kill"), nouse -> target.setHealth(0)), 2, 0);
        moderatePlayerPane.addItem(new GuiItem(createButton(Material.BARRIER, "IP Ban"), nouse -> {
            Bukkit.getBanList(BanListType.IP).addBan(target.getAddress().getAddress().getHostAddress(), null, null, null);
            target.kick(Component.text(lang.getString("moderation.ipban.message", "You have been IP-banned")));
        }), 3, 0);
        moderatePlayerPane.addItem(new GuiItem(createButton(Material.PAPER, "Clear Inventory"), nouse -> target.getInventory().clear()), 4, 0);
        moderatePlayerPane.addItem(new GuiItem(createButton(Material.ENDER_EYE, "Clear Ender Chest"), nouse -> target.getEnderChest().clear()), 5, 0);
        moderatePlayerPane.addItem(new GuiItem(createButton(Material.ENDER_PEARL, "Teleport to"), event -> event.getWhoClicked().teleport(target)), 6, 0);
        moderatePlayerPane.addItem(new GuiItem(createButton(Material.ARROW, lang.getString("admin-gui.buttons.back", "Back")), event -> playerListGui.show(event.getWhoClicked())), 8, 0);

        moderatePlayerPane.fillWith(fillerItem.getItem());
        moderatePlayerGui.addPane(moderatePlayerPane);
        moderatePlayerGui.show(executor);
    }

    private final GuiItem fillerItem;

    // Create a button with both a (Material,  Name)
    private ItemStack createButton(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            item.setItemMeta(meta);
        }
        return item;
    }

    private void cancelEvent(InventoryClickEvent event) {
        event.setCancelled(true);
    }

    // Make items non-removable
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
    }

    private void addGamemodeButton(StaticPane pane, Player player, GameMode gameMode, Material icon, int x, int y) {
        pane.addItem(new GuiItem(createButton(icon, gameMode.toString()), event -> {
            if (player.getGameMode() == gameMode) {
                String message = lang.getString("admin-gui.messages.gamemode-already", "You are already in {gamemode}")
                        .replace("{gamemode}", gameMode.toString().toLowerCase());
                event.getWhoClicked().sendMessage(Component.text(message, NamedTextColor.GRAY));
            } else {
                player.setGameMode(gameMode);
                String message = lang.getString("admin-gui.messages.gamemode-changed", "Your gamemode has been changed to {gamemode}")
                        .replace("{gamemode}", gameMode.toString().toLowerCase());
                event.getWhoClicked().sendMessage(Component.text(message, NamedTextColor.GRAY));
            }
            event.getWhoClicked().closeInventory();
        }), x, y);
    }
}

