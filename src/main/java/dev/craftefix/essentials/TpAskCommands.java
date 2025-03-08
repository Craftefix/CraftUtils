package dev.craftefix.essentials;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Named;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.HashMap;
import java.util.UUID;

public class TpAskCommands {
    // HashMap to store the requests
    // Target UUID, Actor UUID
    HashMap<UUID, UUID> tpRequests = new HashMap<>();

    @Command({"tpask", "tpa", "cc tpask"})
    @CommandPermission("CraftNet.essentials.tpask")
    public void tpask(Player actor, @Named("Player") Player target) {
        if (actor.isOnline() && target.isOnline()) {
            if (actor.equals(target)) {
                actor.sendMessage(Component.text("You can't teleport to yourself!", NamedTextColor.RED));
                return;
            }
            if (tpRequests.get(target.getUniqueId()) == null) {
                tpRequests.put(target.getUniqueId(), actor.getUniqueId());
                actor.sendMessage(Component.text()
                        .append(Component.text("TPA ", NamedTextColor.GREEN).decorate(TextDecoration.BOLD))
                        .append(Component.text("» ", NamedTextColor.DARK_GRAY).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE))
                        .append(Component.text("Sent teleport request to ", NamedTextColor.GRAY))
                        .append(Component.text(target.getName(), NamedTextColor.BLUE))
                        .append(Component.text(". \n", NamedTextColor.DARK_GRAY))
                        .append(Component.text(" [Cancel] ", NamedTextColor.DARK_RED))
                        .clickEvent(ClickEvent.runCommand("/cc tpacancel")));

                target.sendMessage(Component.text()
                        .append(Component.text("TPA ", NamedTextColor.GREEN).decorate(TextDecoration.BOLD))
                        .append(Component.text("» ", NamedTextColor.DARK_GRAY).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE))
                        .append(Component.text(target.getName(), NamedTextColor.GREEN))
                        .append(Component.text(" wants to teleport to ", NamedTextColor.GRAY))
                        .append(Component.text("you.", NamedTextColor.BLUE)));
                target.sendMessage(Component.text()
                        .append(Component.text("TPA ", NamedTextColor.GREEN).decorate(TextDecoration.BOLD))
                        .append(Component.text("» ", NamedTextColor.DARK_GRAY).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE))
                        .append(Component.text("Accept]", NamedTextColor.GREEN))
                                .clickEvent(ClickEvent.runCommand("/cc tpaaccept")));
                target.sendMessage(Component.text()
                        .append(Component.text("TPA ", NamedTextColor.GREEN).decorate(TextDecoration.BOLD))
                        .append(Component.text("» ", NamedTextColor.DARK_GRAY).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE))
                        .append(Component.text("[Deny]", NamedTextColor.RED))
                                .clickEvent(ClickEvent.runCommand("/cc tpadeny")));
            } else {
                actor.sendMessage(Component.text("You already have a pending request to this player!", NamedTextColor.RED));
            }
        }
    }

    @Command({"tpaaccept", "cc tpaaccept"})
    @CommandPermission("CraftNet.essentials.tpaaccept")
    public void tpaccept(Player target) {
        if (tpRequests.get(target.getUniqueId()) != null) {
            Player actor = target.getServer().getPlayer(tpRequests.get(target.getUniqueId()));
            if (actor != null) {
                actor.teleport(target);
                actor.sendMessage(Component.text("Teleport request accepted!", NamedTextColor.GREEN));
                target.sendMessage(Component.text("Teleport request accepted!", NamedTextColor.GREEN));
                tpRequests.remove(target.getUniqueId());
            }
        }
    }

    @Command({"tpadeny", "cc tpadeny"})
    @CommandPermission("CraftNet.essentials.tpadeny")
    public void tpdeny(Player target) {
        if (tpRequests.get(target.getUniqueId()) != null) {
            Player actor = target.getServer().getPlayer(tpRequests.get(target.getUniqueId()));
            if (actor != null) {
                actor.sendMessage(Component.text("Teleport request denied!", NamedTextColor.RED));
                target.sendMessage(Component.text("Teleport request denied!", NamedTextColor.RED));
                tpRequests.remove(target.getUniqueId());
            }
        }
    }

    @Command({"tpacancel", "cc tpacancel"})
    @CommandPermission("CraftNet.essentials.tpacancel")
    public void tpcancel(Player actor) {
        if (tpRequests.containsValue(actor.getUniqueId())) {
            tpRequests.entrySet().removeIf(entry -> {
                if (entry.getValue().equals(actor.getUniqueId())) {
                    Player target = actor.getServer().getPlayer(entry.getKey());
                    if (target != null) {
                        target.sendMessage(Component.text("Teleport request canceled!", NamedTextColor.RED));
                    }
                    actor.sendMessage(Component.text("Teleport request canceled!", NamedTextColor.RED));
                    return true;
                }
                return false;
            });
        }
    }
}