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

    /**
     * Initializes the EnderChestCommands instance and loads the language configuration for localized messages.
     */
    public EnderChestCommands() {
        this.lang = Main.getInstance().getLang();
    }

    /**
     * Opens the Ender Chest inventory of the specified player for the command executor.
     *
     * If no target is specified, opens the executor's own Ender Chest. If a target is specified, the executor must have the appropriate permission to view others' Ender Chests. Sends localized success or error messages based on the outcome.
     *
     * @param target the player whose Ender Chest to open; if null, opens the executor's own Ender Chest
     */
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

    /**
     * Handles player quit events by closing any Ender Chest inventory viewers and cleaning up tracking data.
     *
     * If another player is viewing the quitting player's Ender Chest, their inventory is closed.
     */
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player target = event.getPlayer();
        Player viewer = openInventories.remove(target.getUniqueId());
        if (viewer != null) {
            viewer.closeInventory();
        }
    }
}