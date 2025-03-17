package dev.craftefix.craftUtils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Optional;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public class AbilityCommands {

    @Command({"fly", "cu fly"})
    @CommandPermission("CraftUtils.fly")
    public void fly(Player actor, @Optional Player target) {
        if (target == null) {
            setFly(actor);
        } else {
            if (actor.hasPermission("CraftUtils.fly.others")) {
                setFly(target);
            } else {
                actor.sendMessage(Component.text()
                        .append(Component.text("Perk ", NamedTextColor.LIGHT_PURPLE).decorate(TextDecoration.BOLD))
                        .append(Component.text("» ", NamedTextColor.DARK_GRAY).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE))
                        .append(Component.text("You do not have permission to set other players to fly mode.", NamedTextColor.RED)));
            }
        }
    }

    private void setFly(Player actor) {
        if (actor.getGameMode() == GameMode.SPECTATOR) {
            actor.sendMessage(Component.text()
                    .append(Component.text("Perk ", NamedTextColor.DARK_PURPLE).decorate(TextDecoration.BOLD))
                    .append(Component.text("» ", NamedTextColor.DARK_GRAY).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE))
                    .append(Component.text("You are already flying in spectator mode.", NamedTextColor.LIGHT_PURPLE)));
        } else if (actor.getAllowFlight()) {
            actor.setAllowFlight(false);
            actor.sendMessage(Component.text()
                    .append(Component.text("Perk ", NamedTextColor.LIGHT_PURPLE).decorate(TextDecoration.BOLD))
                    .append(Component.text("» ", NamedTextColor.DARK_GRAY).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE))
                    .append(Component.text("Flight mode disabled.", NamedTextColor.GRAY)));
        } else {
            actor.setAllowFlight(true);
            actor.sendMessage(Component.text()
                    .append(Component.text("Perk ", NamedTextColor.LIGHT_PURPLE).decorate(TextDecoration.BOLD))
                    .append(Component.text("» ", NamedTextColor.DARK_GRAY).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE))
                    .append(Component.text("Flight mode enabled.", NamedTextColor.GRAY)));
        }
    }

    @Command({"heal", "cu heal"})
    @CommandPermission("CraftUtils.heal")
    public void heal(Player actor, @Optional Player target) {
        if (target == null) {
            actor.setHealth(20);
            actor.setFoodLevel(20);
            actor.sendMessage(Component.text()
                    .append(Component.text("Perk ", NamedTextColor.LIGHT_PURPLE).decorate(TextDecoration.BOLD))
                    .append(Component.text("» ", NamedTextColor.DARK_GRAY).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE))
                    .append(Component.text("You have been healed.", NamedTextColor.GRAY)));
        } else {
            if (actor.hasPermission("Craftutils.heal.others")) {
                target.setHealth(20);
                target.setFoodLevel(20);
                target.sendMessage(Component.text()
                        .append(Component.text("Perk ", NamedTextColor.LIGHT_PURPLE).decorate(TextDecoration.BOLD))
                        .append(Component.text("» ", NamedTextColor.DARK_GRAY).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE))
                        .append(Component.text("You have been healed by ", NamedTextColor.GRAY))
                        .append(Component.text(actor.getName(), NamedTextColor.BLUE)));
                actor.sendMessage(Component.text()
                        .append(Component.text("Perk ", NamedTextColor.LIGHT_PURPLE).decorate(TextDecoration.BOLD))
                        .append(Component.text("» ", NamedTextColor.DARK_GRAY).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE))
                        .append(Component.text("You have healed ", NamedTextColor.GRAY))
                        .append(Component.text(target.getName(), NamedTextColor.BLUE)));
            } else {
                actor.sendMessage(Component.text()
                        .append(Component.text("Perk ", NamedTextColor.LIGHT_PURPLE).decorate(TextDecoration.BOLD))
                        .append(Component.text("» ", NamedTextColor.DARK_GRAY).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE))
                        .append(Component.text("You do not have permission to heal other players.", NamedTextColor.LIGHT_PURPLE)));
            }
        }
    }
    @Command({"eat", "cu eat"})
    @CommandPermission("CraftUtils.eat")
    public void eat(Player actor, @Optional Player target) {
        if (target == null) {
            actor.setFoodLevel(20);
            actor.sendMessage(Component.text()
                    .append(Component.text("Perk ", NamedTextColor.LIGHT_PURPLE).decorate(TextDecoration.BOLD))
                    .append(Component.text("» ", NamedTextColor.DARK_GRAY).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE))
                    .append(Component.text("You have been fed.", NamedTextColor.GRAY)));
        } else if (actor.hasPermission("CraftUtils.eat.others")) {
            target.setFoodLevel(20);
            actor.sendMessage(Component.text()
                    .append(Component.text("Perk ", NamedTextColor.LIGHT_PURPLE).decorate(TextDecoration.BOLD))
                    .append(Component.text("» ", NamedTextColor.DARK_GRAY).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE))
                    .append(Component.text("You have fed ", NamedTextColor.GRAY))
                    .append(Component.text(target.getName(), NamedTextColor.BLUE)));
            target.sendMessage(Component.text()
                    .append(Component.text("Perk ", NamedTextColor.LIGHT_PURPLE).decorate(TextDecoration.BOLD))
                    .append(Component.text("» ", NamedTextColor.DARK_GRAY).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE))
                    .append(Component.text("You have been fed by ", NamedTextColor.GRAY))
                    .append(Component.text(actor.getName(), NamedTextColor.BLUE)));
        }
    }

    @Command({"trash", "cu trash"})
    @CommandPermission("CraftUtils.trash")
    public void trash(Player player) {
        Inventory trashInventory = Bukkit.createInventory(null, InventoryType.CHEST, "$ctrash");
        player.openInventory(trashInventory);
    }
}




