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

    public class messageCommands {

        private final HashMap<UUID, UUID> messagers = new HashMap<>();

        // Message templates
        private String sendMessageTemplate;
        private String receiveMessageTemplate;
        private String noTargetMessageTemplate;
        private String offlineTargetMessageTemplate;

        public void loadConfig(JavaPlugin plugin) {
            FileConfiguration config = plugin.getConfig();

            // Load message templates
            sendMessageTemplate = config.getString("msg.messages.send-message", "%yellow%%actor% %gray%to %aqua%%recipient%: %italic%%gray%%message%");
            receiveMessageTemplate = config.getString("msg.messages.receive-message", "%yellow%%actor% sent you: %italic%%gray%%message%");
            noTargetMessageTemplate = config.getString("msg.messages.no-target", "%red%You don't have anyone to reply to.");
            offlineTargetMessageTemplate = config.getString("msg.messages.offline-target", "%red%The player you are replying to is offline.");
        }

        private Component formatMessage(String template, Player actor, Player recipient, String message) {
            String processedMessage = template
                    .replace("%actor%", actor.getName())
                    .replace("%recipient%", recipient != null ? recipient.getName() : "")
                    .replace("%message%", message != null ? message : "")
                    .replace("%red%", NamedTextColor.RED.toString())
                    .replace("%yellow%", NamedTextColor.YELLOW.toString())
                    .replace("%aqua%", NamedTextColor.AQUA.toString())
                    .replace("%gray%", NamedTextColor.GRAY.toString())
                    .replace("%green%", NamedTextColor.GREEN.toString())
                    .replace("%blue%", NamedTextColor.BLUE.toString())
                    .replace("%italic%", TextDecoration.ITALIC.toString())
                    .replace("%bold%", TextDecoration.BOLD.toString())
                    .replace("%underlined%", TextDecoration.UNDERLINED.toString());

            return Component.text()
                    .content(processedMessage)
                    .build();
        }

        @Command({"msg", "message", "tell"})
        @CommandPermission("CraftNet.essentials.message")
        public void msg(Player actor, Player recipient, String message) {
            Component sendMessage = formatMessage(sendMessageTemplate, actor, recipient, message);
            Component receiveMessage = formatMessage(receiveMessageTemplate, actor, recipient, message);

            actor.sendMessage(sendMessage);
            recipient.sendMessage(receiveMessage);

            if (actor.isOnline() && recipient.isOnline()) {
                messagers.put(actor.getUniqueId(), recipient.getUniqueId());
            }
        }

        @Command({"r", "reply"})
        @CommandPermission("CraftNet.essentials.reply")
        public void reply(Player actor, String message) {
            UUID targetUUID = messagers.get(actor.getUniqueId());

            if (targetUUID != null) {
                Player recipient = actor.getServer().getPlayer(targetUUID);
                if (recipient != null && recipient.isOnline()) {
                    Component sendMessage = formatMessage(sendMessageTemplate, actor, recipient, message);
                    Component receiveMessage = formatMessage(receiveMessageTemplate, actor, recipient, message);

                    actor.sendMessage(sendMessage);
                    recipient.sendMessage(receiveMessage);
                } else {
                    actor.sendMessage(formatMessage(offlineTargetMessageTemplate, actor, null, null));
                }
            } else {
                actor.sendMessage(formatMessage(noTargetMessageTemplate, actor, null, null));
            }
        }
    }