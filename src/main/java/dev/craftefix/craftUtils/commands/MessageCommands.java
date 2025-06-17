package dev.craftefix.craftUtils.commands;

import dev.craftefix.craftUtils.Main;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Named;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.HashMap;
import java.util.UUID;

public class MessageCommands implements Listener {
    private final HashMap<UUID, UUID> messagers = new HashMap<>();
    private final YamlConfiguration lang;
    private final Main plugin;

    public MessageCommands() {
        this.plugin = Main.getInstance();
        this.lang = plugin.getLang(null); // null for default language
    }

    @Command({"msg", "message", "tell", "cu msg", "w", "whisper"})
    @CommandPermission("CraftUtils.message")
    public void msg(Player actor, @Named("player") Player target, @Named("message") String message) {
        if (actor.equals(target)) {
            String errorMsg = lang.getString("messaging.error.self-message", "You can't message yourself");
            actor.sendMessage(Component.text(errorMsg, plugin.getColorManager().getTextColor("error")));
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
        String toFormat = lang.getString("messaging.format.to", "To {player}: {message}")
                .replace("{player}", target.getName())
                .replace("{message}", message);
        actor.sendMessage(Component.text()
                .append(Component.text("MSG ", plugin.getColorManager().getPrefixColor("msg")).decorate(TextDecoration.BOLD))
                .append(Component.text("» ", plugin.getColorManager().getBorderColor("separator")).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE))
                .append(Component.text(toFormat, plugin.getColorManager().getTextColor("normal"))));

        String fromFormat = lang.getString("messaging.format.from", "From {player}: {message}")
                .replace("{player}", actor.getName())
                .replace("{message}", message);
        target.sendMessage(Component.text()
                .append(Component.text("MSG ", plugin.getColorManager().getPrefixColor("msg")).decorate(TextDecoration.BOLD))
                .append(Component.text("» ", plugin.getColorManager().getBorderColor("separator")).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE))
                .append(Component.text(fromFormat, plugin.getColorManager().getTextColor("normal"))));
    }

    @Command({"r", "reply", "cu reply"})
    @CommandPermission("CraftUtils.reply")
    public void reply(Player actor, @Named("message") String message) {
        UUID targetUUID = messagers.get(actor.getUniqueId());
        if (targetUUID != null) {
            Player target = actor.getServer().getPlayer(targetUUID);
            if (target != null && target.isOnline()) {
                sendMessages(actor, target, message);
            } else {
                String errorMsg = lang.getString("messaging.error.player-offline", "The player you are replying to is no longer online.");
                actor.sendMessage(Component.text(errorMsg, plugin.getColorManager().getTextColor("error")));
            }
        } else {
            String errorMsg = lang.getString("messaging.error.no-reply-target", "You don't have anyone to reply to.");
            actor.sendMessage(Component.text(errorMsg, plugin.getColorManager().getTextColor("error")));
        }
    }
}