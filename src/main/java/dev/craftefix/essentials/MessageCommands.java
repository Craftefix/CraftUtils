package dev.craftefix.essentials;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Named;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.HashMap;
import java.util.UUID;

public class MessageCommands extends JavaPlugin implements Listener {

    private final HashMap<UUID, UUID> messagers = new HashMap<>();

    private void logDebug(String message) {
        // Debug logging is disabled since config support is removed
    }

    @Command({"msg", "message", "tell", "cc msg"})
    @CommandPermission("CraftNet.essentials.message")
    public void msg(Player actor, @Named("player") Player target, @Named("message") String message) {
        logDebug("Command /msg executed by: " + actor.getName());
        logDebug("Message target: " + target.getName());
        logDebug("Message content: " + message);

    if (actor.equals(target)) {
        actor.sendMessage(Component.text("You whispered to yourself: ", NamedTextColor.GRAY)
                .append(Component.text(message, NamedTextColor.GRAY)));
    } else {
        actor.sendMessage(Component.text("You to ")
                .append(Component.text(target.getName(), NamedTextColor.YELLOW))
                .append(Component.text(": ", NamedTextColor.GRAY))
                .append(Component.text(message, NamedTextColor.GRAY)));
        target.sendMessage(Component.text(actor.getName(), NamedTextColor.YELLOW)
                .append(Component.text(" to you: ", NamedTextColor.GRAY))
                .append(Component.text(message, NamedTextColor.GRAY)));

                if (actor.isOnline() && target.isOnline()) {
                messagers.put(actor.getUniqueId(), target.getUniqueId());
                }
        }
    }
    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        logDebug("Player quit: " + e.getPlayer().getName());
        messagers.remove(e.getPlayer().getUniqueId());
    }

    @Command({"r", "reply", "cc reply"})
    @CommandPermission("CraftNet.essentials.reply")
    public void reply(Player actor, @Named("message") String message) {
        logDebug("Command /reply executed by: " + actor.getName());
        logDebug("Reply message content: " + message);

        UUID targetUUID = messagers.get(actor.getUniqueId());
        if (targetUUID != null) {
            Player target = getServer().getPlayer(targetUUID);
            if (target != null && target.isOnline()) {
                target.sendMessage(Component.text(actor.getName(), NamedTextColor.YELLOW)
                        .append(Component.text(" to you: ", NamedTextColor.GRAY))
                        .append(Component.text(message, NamedTextColor.GRAY)));
                actor.sendMessage(Component.text("You to ")
                        .append(Component.text(target.getName(), NamedTextColor.YELLOW))
                        .append(Component.text(": ", NamedTextColor.GRAY))
                        .append(Component.text(message, NamedTextColor.GRAY)));
            } else {
                logDebug("The player being replied to is offline: UUID=" + targetUUID);
                actor.sendMessage(Component.text("The player you are replying to is no longer online.", NamedTextColor.RED));
            }
        } else {
            logDebug("Player attempted to reply but has no recent message target.");
            actor.sendMessage(Component.text("You don't have anyone to reply to.", NamedTextColor.RED));
        }
    }
}