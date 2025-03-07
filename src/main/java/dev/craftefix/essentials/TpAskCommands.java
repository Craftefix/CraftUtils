package dev.craftefix.essentials;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
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
    public void tpask(Player actor, @Named("<Player>")  Player target) {
        if (actor.isOnline() && target.isOnline()) {
            if (actor.equals(target)) {
                actor.sendMessage(Component.text("You can't teleport to yourself!", NamedTextColor.RED));
                return;
            }
            if (tpRequests.get(target.getUniqueId()) == null) {
                tpRequests.put(target.getUniqueId(), actor.getUniqueId());
                target.sendMessage(Component.text(actor.getName(), NamedTextColor.GOLD)
                        .append(Component.text(" wants to teleport to you. \n", NamedTextColor.GRAY))
                        .append(Component.text("[Accept] ", NamedTextColor.GREEN)
                                .clickEvent(ClickEvent.runCommand("/tpaccept")))
                        .append(Component.text("[Deny]", NamedTextColor.RED)
                                .clickEvent(ClickEvent.runCommand("/tpdeny"))));
                actor.sendMessage(Component.text("You have sent a teleport request to ", NamedTextColor.GRAY)
                        .append(Component.text(target.getName(), NamedTextColor.GOLD))
                        .append(Component.text("[Cancel]", NamedTextColor.RED)
                                .clickEvent(ClickEvent.runCommand("/tpacancel"))));
            } else {
                actor.sendMessage(Component.text("You already have a pending request to this player!", NamedTextColor.RED));
            }
        }
    }

    @Command({"tpaccept", "cc tpaccept"})
    @CommandPermission("CraftNet.essentials.tpaccept")
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

    @Command({"tpdeny", "cc tpdeny"})
    @CommandPermission("CraftNet.essentials.tpdeny")
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
