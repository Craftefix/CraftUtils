package dev.craftefix.craftUtils.commands;

import dev.craftefix.craftUtils.Main;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Optional;
import revxrsal.commands.bukkit.annotation.CommandPermission;


public class AbilityCommands {
    private final YamlConfiguration lang;

    /**
     * Initializes the AbilityCommands instance with the language configuration from the main plugin.
     */
    public AbilityCommands() {
        this.lang = Main.getInstance().getLang();
    }

    // This Class is responsible for handling the commands that are related to the abilities of the player.
    // The commands are /fly, /heal, /eat, and /trash.
    // The /fly command toggles the player's flight mode. (Doesn't work with most Anti-Cheat plugins)
    // The /heal command sets the player's health and food level to 20.
    // The /eat command sets the player's food level to 20.
    /**
     * Toggles flight mode for the executing player or a specified target player.
     *
     * If a target is provided, the executor must have the "CraftUtils.fly.others" permission; otherwise, a no-permission message is sent.
     */

    @Command({"fly", "cu fly"})
    @CommandPermission("CraftUtils.fly")
    public void fly(Player actor, @Optional Player target) {
        if (target == null) {
            setFly(actor);
        } else {
            if (actor.hasPermission("CraftUtils.fly.others")) {
                setFly(target);
            } else {
                String message = lang.getString("abilities.fly.no-permission-others", "You do not have permission to set other players to fly mode.");
                actor.sendMessage(Component.text()
                        .append(Component.text(lang.getString("abilities.prefix", "Perk") + " ", NamedTextColor.LIGHT_PURPLE).decorate(TextDecoration.BOLD))
                        .append(Component.text("» ", NamedTextColor.DARK_GRAY).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE))
                        .append(Component.text(message, NamedTextColor.RED)));
            }
        }
    }

    /**
     * Toggles the player's flight mode and sends a localized message indicating whether flight was enabled or disabled.
     *
     * @param player the player whose flight mode is being toggled
     */
    private void setFly(Player player) {
        player.setAllowFlight(!player.getAllowFlight());
        String message;
        if (player.getAllowFlight()) {
            message = lang.getString("abilities.fly.enabled", "Flight mode enabled for {player}.")
                    .replace("{player}", player.getName());
        } else {
            message = lang.getString("abilities.fly.disabled", "Flight mode disabled for {player}.")
                    .replace("{player}", player.getName());
        }
        player.sendMessage(Component.text()
                .append(Component.text(lang.getString("abilities.prefix", "Perk") + " ", NamedTextColor.LIGHT_PURPLE).decorate(TextDecoration.BOLD))
                .append(Component.text("» ", NamedTextColor.DARK_GRAY).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE))
                .append(Component.text(message, NamedTextColor.GRAY)));
    }

    /**
     * Restores health and food level to maximum for the executor or a specified player.
     *
     * If a target player is provided, heals that player and notifies the executor. Otherwise, heals the executor.
     */
    @Command({"heal", "cu heal"})
    @CommandPermission("CraftUtils.heal")
    public void heal(Player actor, @Optional Player target) {
        if (target == null) {
            actor.setHealth(20);
            actor.setFoodLevel(20);
            String message = lang.getString("abilities.heal.success", "You have been healed.");
            actor.sendMessage(Component.text()
                    .append(Component.text(lang.getString("abilities.prefix", "Perk") + " ", NamedTextColor.LIGHT_PURPLE).decorate(TextDecoration.BOLD))
                    .append(Component.text("» ", NamedTextColor.DARK_GRAY).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE))
                    .append(Component.text(message, NamedTextColor.GRAY)));
        } else {
            target.setHealth(20);
            target.setFoodLevel(20);
            String message = lang.getString("abilities.heal.success-other", "Healed {player}.")
                    .replace("{player}", target.getName());
            actor.sendMessage(Component.text()
                    .append(Component.text(lang.getString("abilities.prefix", "Perk") + " ", NamedTextColor.LIGHT_PURPLE).decorate(TextDecoration.BOLD))
                    .append(Component.text("» ", NamedTextColor.DARK_GRAY).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE))
                    .append(Component.text(message, NamedTextColor.GRAY)));
        }
    }

    /**
     * Restores the food level to maximum for the executor or a specified target player.
     *
     * If a target is provided, their food level is set to full and the executor is notified. Otherwise, the executor's own food level is restored and they receive a confirmation message.
     */
    @Command({"feed", "eat", "cu feed"})
    @CommandPermission("CraftUtils.feed")
    public void feed(Player actor, @Optional Player target) {
        if (target == null) {
            actor.setFoodLevel(20);
            String message = lang.getString("abilities.feed.success", "Your hunger has been satisfied.");
            actor.sendMessage(Component.text()
                    .append(Component.text(lang.getString("abilities.prefix", "Perk") + " ", NamedTextColor.LIGHT_PURPLE).decorate(TextDecoration.BOLD))
                    .append(Component.text("» ", NamedTextColor.DARK_GRAY).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE))
                    .append(Component.text(message, NamedTextColor.GRAY)));
        } else {
            target.setFoodLevel(20);
            String message = lang.getString("abilities.feed.success-other", "Fed {player}.")
                    .replace("{player}", target.getName());
            actor.sendMessage(Component.text()
                    .append(Component.text(lang.getString("abilities.prefix", "Perk") + " ", NamedTextColor.LIGHT_PURPLE).decorate(TextDecoration.BOLD))
                    .append(Component.text("» ", NamedTextColor.DARK_GRAY).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE))
                    .append(Component.text(message, NamedTextColor.GRAY)));
        }
    }

    /****
     * Opens a chest inventory titled "Trash" for the player to use as a disposal container.
     *
     * Sends a localized confirmation message to the player after opening the inventory.
     */
    @Command({"trash", "disposal", "cu trash"})
    @CommandPermission("CraftUtils.trash")
    public void trash(Player actor) {
        Inventory trashInventory = Bukkit.createInventory(actor, InventoryType.CHEST, Component.text("Trash"));
        actor.openInventory(trashInventory);
        String message = lang.getString("abilities.trash.opened", "Opened trash inventory.");
        actor.sendMessage(Component.text()
                .append(Component.text(lang.getString("abilities.prefix", "Perk") + " ", NamedTextColor.LIGHT_PURPLE).decorate(TextDecoration.BOLD))
                .append(Component.text("» ", NamedTextColor.DARK_GRAY).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE))
                .append(Component.text(message, NamedTextColor.GRAY)));
    }
}
