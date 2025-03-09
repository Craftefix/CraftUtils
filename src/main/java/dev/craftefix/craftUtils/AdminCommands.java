package dev.craftefix.craftUtils;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.ScrollingGui;
import net.kyori.adventure.text.Component;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public class AdminCommands {

    private final JavaPlugin plugin;

    public AdminCommands(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Command("admingui")
    @CommandPermission("CraftNet.craftUtils.admingui")
    public void openAdminGui(Player player) {
        Gui adminGui = Gui.gui()
                .title(Component.text("Admin GUI"))
                .rows(3)
                .create();

        GuiItem gamemodeItem = ItemBuilder.from(Material.DIAMOND_SWORD)
                .name(Component.text("Gamemode"))
                .asGuiItem(event -> openGamemodeGui(player));

        GuiItem playersItem = ItemBuilder.from(Material.PLAYER_HEAD)
                .name(Component.text("Players"))
                .asGuiItem(event -> openPlayersGui(player));

        adminGui.setItem(13, gamemodeItem);
        adminGui.setItem(15, playersItem);
        adminGui.open(player);
    }

    private void openGamemodeGui(Player player) {
        Gui gamemodeGui = Gui.gui()
                .title(Component.text("Gamemode"))
                .rows(1)
                .create();

        GuiItem survivalItem = ItemBuilder.from(Material.GRASS_BLOCK)
                .name(Component.text("Survival"))
                .asGuiItem(event -> player.setGameMode(GameMode.SURVIVAL));

        GuiItem creativeItem = ItemBuilder.from(Material.DIAMOND_BLOCK)
                .name(Component.text("Creative"))
                .asGuiItem(event -> player.setGameMode(GameMode.CREATIVE));

        GuiItem spectatorItem = ItemBuilder.from(Material.ENDER_EYE)
                .name(Component.text("Spectator"))
                .asGuiItem(event -> player.setGameMode(GameMode.SPECTATOR));

        gamemodeGui.setItem(2, survivalItem);
        gamemodeGui.setItem(4, creativeItem);
        gamemodeGui.setItem(6, spectatorItem);
        gamemodeGui.open(player);
    }

    private void openPlayersGui(Player player) {
        ScrollingGui playersGui = Gui.scrolling()
                .title(Component.text("Players"))
                .rows(3)
                .pageSize(9)
                .create();

        for (Player target : Bukkit.getOnlinePlayers()) {
            GuiItem playerItem = ItemBuilder.from(Material.PLAYER_HEAD)
                    .name(Component.text(target.getName()))
                    .asGuiItem(event -> openPlayerActionsGui(player, target));
            playersGui.addItem(playerItem);
        }

        playersGui.open(player);
    }

    private void openPlayerActionsGui(Player player, Player target) {
        Gui playerActionsGui = Gui.gui()
                .title(Component.text("Actions for " + target.getName()))
                .rows(3)
                .create();

        GuiItem banItem = ItemBuilder.from(Material.BARRIER)
                .name(Component.text("Ban"))
                .asGuiItem(event -> {
                    Bukkit.getBanList(BanList.Type.NAME).addBan(target.getName(), "Banned by admin", null, player.getName());
                    target.kick(Component.text("You have been banned."));
                });

        GuiItem ipBanItem = ItemBuilder.from(Material.BARRIER)
                .name(Component.text("IP Ban"))
                .asGuiItem(event -> {
                    if (target.getAddress() != null) {
                        Bukkit.getBanList(BanList.Type.IP).addBan(target.getAddress().getAddress().getHostAddress(), "IP Banned by admin", null, player.getName());
                        target.kick(Component.text("You have been IP banned."));
                    }
                });

        GuiItem kickItem = ItemBuilder.from(Material.FEATHER)
                .name(Component.text("Kick"))
                .asGuiItem(event -> target.kick(Component.text("You have been kicked.")));

        GuiItem tpItem = ItemBuilder.from(Material.ENDER_PEARL)
                .name(Component.text("Teleport to"))
                .asGuiItem(event -> player.teleport(target));

        GuiItem killItem = ItemBuilder.from(Material.DIAMOND_SWORD)
                .name(Component.text("Kill"))
                .asGuiItem(event -> target.setHealth(0));

        GuiItem backItem = ItemBuilder.from(Material.ARROW)
                .name(Component.text("Back"))
                .asGuiItem(event -> openPlayersGui(player));

        playerActionsGui.setItem(10, banItem);
        playerActionsGui.setItem(11, ipBanItem);
        playerActionsGui.setItem(12, kickItem);
        playerActionsGui.setItem(13, tpItem);
        playerActionsGui.setItem(14, killItem);
        playerActionsGui.setItem(16, backItem);
        playerActionsGui.open(player);
    }
}