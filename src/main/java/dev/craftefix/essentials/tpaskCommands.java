package dev.craftefix.essentials;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public class tpaskCommands {
    @Command({"tpask", "tpa", "cc tpask"})
    @CommandPermission("CraftNet.essentials.tpask")
    public void tpask(Player actor, Player target) {
        target.sendMessage(Component.text(actor.getName(), NamedTextColor.GOLD)
                .append(Component.text(" wants to teleport to you. Do you accept? ", NamedTextColor.GRAY))
                .append(Component.text("/tpaccept ", NamedTextColor.GOLD))
                .append(Component.text("or ", NamedTextColor.GRAY))
                .append(Component.text("/tpdeny", NamedTextColor.GOLD)));

        if (actor.equals(target)) {
            actor.sendMessage(Component.text("You can't teleport to yourself!", NamedTextColor.RED));
            return;
        }
        if (actor.isOnline() && target.isOnline()) {
            actor.sendMessage(Component.text("You have sent a teleport request to ", NamedTextColor.GRAY)
                    .append(Component.text(target.getName(), NamedTextColor.GOLD)));
        } else {
            actor.sendMessage(Component.text("The player you are trying to teleport to is not online!", NamedTextColor.RED));
            return;
        }
    }
}
