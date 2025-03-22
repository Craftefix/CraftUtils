package dev.craftefix.craftUtils.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
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


    @Command({"msg", "message", "tell", "cu msg", "w", "whisper"})
    @CommandPermission("CraftUtils.message")
    public void msg(Player actor, @Named("player") Player target, @Named("message") String message) {
        if (actor.equals(target)) {
            actor.sendMessage(Component.text("You can't message yourself", NamedTextColor.RED));
        } else {
            sendMessages(actor, target, message);

            if (actor.isOnline() && target.isOnline()) {
                messagers.put(actor.getUniqueId(), target.getUniqueId());
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
         messagers.remove(e.getPlayer().getUniqueId());
    }

    public void sendMessages(Player actor, Player target, String message) {
        actor.sendMessage(Component.text()
                .append(Component.text("MSG ", NamedTextColor.GREEN).decorate(TextDecoration.BOLD)) // Bold only for "MSG"
                .append(Component.text("» ", NamedTextColor.DARK_GRAY).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE)) // Explicitly disable bold
                .append(Component.text("You", NamedTextColor.BLUE))
                .append(Component.text(" ➠ ", NamedTextColor.DARK_GRAY))
                .append(Component.text(target.getName(), NamedTextColor.GREEN))
                .append(Component.text(": ", NamedTextColor.DARK_GRAY))
                .append(Component.text(message, NamedTextColor.GRAY))
                .build());

        target.sendMessage(Component.text()
                .append(Component.text("MSG ", NamedTextColor.GREEN).decorate(TextDecoration.BOLD)) // Bold only for "MSG"
                .append(Component.text("» ", NamedTextColor.DARK_GRAY).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE)) // Explicitly disable bold
                .append(Component.text(target.getName(), NamedTextColor.BLUE))
                .append(Component.text(" ➠ ", NamedTextColor.DARK_GRAY))
                .append(Component.text("You", NamedTextColor.GREEN))
                .append(Component.text(": ", NamedTextColor.DARK_GRAY))
                .append(Component.text(message, NamedTextColor.GRAY))
                .build());
    }

    @Command({"r", "reply", "cu reply"})
    @CommandPermission("CraftUtils.reply")
    public void reply(Player actor, @Named("message") String message) {
        UUID targetUUID = messagers.get(actor.getUniqueId());
        if (targetUUID != null) {
            Player target = getServer().getPlayer(targetUUID);
            if (target != null && target.isOnline()) {
                sendMessages(actor, target, message);
            } else {
                actor.sendMessage(Component.text("The player you are replying to is no longer online.", NamedTextColor.RED));
            }
        } else {
            actor.sendMessage(Component.text("You don't have anyone to reply to.", NamedTextColor.RED));
        }
    }
}