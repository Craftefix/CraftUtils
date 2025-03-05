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


public class tpaskCommands {

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
                                .clickEvent(ClickEvent.runCommand("/tpdeny"))));
            } else {
                actor.sendMessage(Component.text("You already have a pending request to this player!", NamedTextColor.RED));
            }
        }
    }
}
