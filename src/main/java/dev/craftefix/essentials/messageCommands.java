package dev.craftefix.essentials;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.HashMap;
import java.util.UUID;

public class messageCommands extends JavaPlugin {

    private final HashMap<UUID, UUID> messagers = new HashMap<>();

    // Message templates
    private String sendMessageTemplate;
    private String receiveMessageTemplate;
    private String noTargetMessageTemplate;
    private String offlineTargetMessageTemplate;


    public void loadConfig() {
        FileConfiguration config = getConfig();

        // Load message templates
        sendMessageTemplate = config.getString("msg.messages.send-message", "%yellow%%actor% %gray%to %aqua%%recipient%: %italic%%gray%%message%");
        receiveMessageTemplate = config.getString("msg.messages.receive-message", "%yellow%%actor% sent you: %italic%%gray%%message%");
        noTargetMessageTemplate = config.getString("msg.messages.no-target", "%red%You don't have anyone to reply to.");
        offlineTargetMessageTemplate = config.getString("msg.messages.offline-target", "%red%The player you are replying to is offline.");
    }

    /**
     * Formats a message by replacing color and formatting placeholders.
     *
     * @param template The template with placeholders.
     * @param actor    The player who sends the message.
     * @param recipient The player who receives the message (can be null for errors).
     * @param message  The actual message content.
     * @return A formatted Component with the final message.
     */
    private Component formatMessage(String template, Player actor, Player recipient, String message) {
        // Replace placeholders with values dynamically
        String processedMessage = template
                .replace("%actor%", actor.getName())
                .replace("%recipient%", recipient != null ? recipient.getName() : "")
                .replace("%message%", message != null ? message : "")
                // Replace color placeholders
                .replace("%red%", NamedTextColor.RED.toString())
                .replace("%yellow%", NamedTextColor.YELLOW.toString())
                .replace("%aqua%", NamedTextColor.AQUA.toString())
                .replace("%gray%", NamedTextColor.GRAY.toString())
                .replace("%green%", NamedTextColor.GREEN.toString())
                .replace("%blue%", NamedTextColor.BLUE.toString())
                // Replace formatting placeholders
                .replace("%italic%", TextDecoration.ITALIC.toString())
                .replace("%bold%", TextDecoration.BOLD.toString())
                .replace("%underlined%", TextDecoration.UNDERLINED.toString());

        // Return as an Adventure Component
        return Component.text()
                .content(processedMessage)
                .build();
    }

    @Command({"msg", "message", "tell"})
    @CommandPermission("CraftNet.essentials.message")
    public void msg(Player actor, Player recipient, String message) {
        // Format the components
        Component sendMessage = formatMessage(sendMessageTemplate, actor, recipient, message);
        Component receiveMessage = formatMessage(receiveMessageTemplate, actor, recipient, message);

        // Send messages
        actor.sendMessage(sendMessage);
        recipient.sendMessage(receiveMessage);

        // Store the latest messaging pair
        if (actor.isOnline() && recipient.isOnline()) {
            messagers.put(actor.getUniqueId(), recipient.getUniqueId());
        }
    }

    @Command({"r", "reply"})
    @CommandPermission("CraftNet.essentials.reply")
    public void reply(Player actor, String message) {
        UUID targetUUID = messagers.get(actor.getUniqueId()); // Get the last messaged player

        if (targetUUID != null) {
            Player recipient = getServer().getPlayer(targetUUID);
            if (recipient != null && recipient.isOnline()) {
                // Format the messages
                Component sendMessage = formatMessage(sendMessageTemplate, actor, recipient, message);
                Component receiveMessage = formatMessage(receiveMessageTemplate, actor, recipient, message);

                // Send the message
                actor.sendMessage(sendMessage);
                recipient.sendMessage(receiveMessage);

            } else {
                // Recipient is offline
                actor.sendMessage(formatMessage(offlineTargetMessageTemplate, actor, null, null));
            }
        } else {
            // No recent recipient
            actor.sendMessage(formatMessage(noTargetMessageTemplate, actor, null, null));
        }
    }
}