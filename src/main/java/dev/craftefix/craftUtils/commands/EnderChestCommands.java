package dev.craftefix.craftUtils.commands;

import dev.craftefix.craftUtils.Main;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Named;
import revxrsal.commands.annotation.Optional;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EnderChestCommands implements Listener {
    private final Map<UUID, Player> openInventories = new HashMap<>();
    private final YamlConfiguration lang;

    public EnderChestCommands() {
        this.lang = Main.getInstance().getLang();
    }

    @Command({"enderchest", "ec", "cu enderchest"})
    @CommandPermission("CraftUtils.enderchest")
    public void enderchest(Player player, @Optional @Named("target") Player target) {
        if (target == null) {
            target = player;
        } else if (!player.hasPermission("CraftUtils.enderchest.others")) {
            String message = lang.getString("enderchest.error.no-permission", "You do not have permission to view others' ender chests.");
            player.sendMessage(Component.text(message, NamedTextColor.RED));
            return;
        }

        if (!target.isOnline()) {
            String message = lang.getString("enderchest.error.player-offline", "Player not found or not online.");
            player.sendMessage(Component.text(message, NamedTextColor.RED));
            return;
        }

        Inventory enderChest = target.getEnderChest();
        player.openInventory(enderChest);
        openInventories.put(target.getUniqueId(), player);

        if (target != player) {
            String message = lang.getString("enderchest.success.opened", "Opened {player}'s ender chest.")
                    .replace("{player}", target.getName());
            player.sendMessage(Component.text(message, NamedTextColor.GREEN));
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player target = event.getPlayer();
        Player viewer = openInventories.remove(target.getUniqueId());
        if (viewer != null) {
            viewer.closeInventory();
        }
    }
}