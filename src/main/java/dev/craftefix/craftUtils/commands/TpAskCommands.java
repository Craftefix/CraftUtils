package dev.craftefix.craftUtils.commands;

import dev.craftefix.craftUtils.Main;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
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

public class TpAskCommands implements Listener {
    private final HashMap<UUID, TpRequest> tpRequests = new HashMap<>();
    private final YamlConfiguration lang;

    /**
     * Initializes the TpAskCommands instance and loads the language configuration for localized messages.
     */
    public TpAskCommands() {
        this.lang = Main.getInstance().getLang();
    }

    /**
     * Sends a teleport request from one player to another.
     *
     * If the target player does not already have a pending request, creates a new teleport request and notifies both players with localized messages and interactive options. Prevents self-requests and duplicate requests to the same target.
     */
    @Command({"tpask", "tpa", "cu tpask"})
    @CommandPermission("CraftUtils.tpask")
    public void tpask(Player actor, @Named("Player") Player target) {
        cleanUpOldRequests();
        if (actor.isOnline() && target.isOnline()) {
            if (actor.equals(target)) {
                String message = lang.getString("teleport.tpa.error.self", "You can't teleport to yourself!");
                actor.sendMessage(Component.text()
                        .append(Component.text("TPA ", NamedTextColor.GREEN).decorate(TextDecoration.BOLD))
                        .append(Component.text("» ", NamedTextColor.DARK_GRAY).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE))
                        .append(Component.text(message, NamedTextColor.RED)));
                return;
            }
            if (tpRequests.get(target.getUniqueId()) == null) {
                tpRequests.put(target.getUniqueId(), new TpRequest(actor.getUniqueId(), System.currentTimeMillis()));

                String sentMessage = lang.getString("teleport.tpa.request.sent", "Sent teleport request to {player}")
                        .replace("{player}", target.getName());
                actor.sendMessage(Component.text()
                        .append(Component.text("TPA ", NamedTextColor.GREEN).decorate(TextDecoration.BOLD))
                        .append(Component.text("» ", NamedTextColor.DARK_GRAY).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE))
                        .append(Component.text(sentMessage, NamedTextColor.GRAY))
                        .append(Component.text("\n", NamedTextColor.DARK_GRAY))
                        .append(Component.text(lang.getString("teleport.tpa.request.cancel", "[Cancel]"), NamedTextColor.RED))
                        .clickEvent(ClickEvent.runCommand("/cu tpacancel")));

                String receivedMessage = lang.getString("teleport.tpa.request.received", "{player} wants to teleport to you.")
                        .replace("{player}", actor.getName());
                target.sendMessage(Component.text()
                        .append(Component.text("TPA ", NamedTextColor.GREEN).decorate(TextDecoration.BOLD))
                        .append(Component.text("» ", NamedTextColor.DARK_GRAY).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE))
                        .append(Component.text(receivedMessage, NamedTextColor.GRAY)));

                target.sendMessage(Component.text()
                        .append(Component.text("TPA ", NamedTextColor.GREEN).decorate(TextDecoration.BOLD))
                        .append(Component.text("» ", NamedTextColor.DARK_GRAY).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE))
                        .append(Component.text(lang.getString("teleport.tpa.request.accept", "[Accept]"), NamedTextColor.GREEN))
                        .clickEvent(ClickEvent.runCommand("/cu tpaaccept")));

                target.sendMessage(Component.text()
                        .append(Component.text("TPA ", NamedTextColor.GREEN).decorate(TextDecoration.BOLD))
                        .append(Component.text("» ", NamedTextColor.DARK_GRAY).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE))
                        .append(Component.text(lang.getString("teleport.tpa.request.deny", "[Deny]"), NamedTextColor.RED))
                        .clickEvent(ClickEvent.runCommand("/cu tpadeny")));
            } else {
                String message = lang.getString("teleport.tpa.error.pending", "You already have a pending request to this player!");
                actor.sendMessage(Component.text(message, NamedTextColor.RED));
            }
        }
    }

    /**
     * Accepts a pending teleport request for the specified player, teleporting the requester to the target.
     *
     * If a valid request exists and the requester is online, the requester is teleported to the target player.
     * Both players receive a localized acceptance message. The request is then removed from the active requests.
     *
     * @param target the player accepting the teleport request
     */
    @Command({"tpaaccept", "cu tpaaccept"})
    @CommandPermission("CraftUtils.tpaaccept")
    public void tpaccept(Player target) {
        cleanUpOldRequests();
        TpRequest request = tpRequests.get(target.getUniqueId());
        if (request != null) {
            Player actor = target.getServer().getPlayer(request.getActorUUID());
            if (actor != null) {
                actor.teleport(target);
                String message = lang.getString("teleport.tpa.result.accepted", "Teleport request accepted!");
                actor.sendMessage(Component.text()
                        .append(Component.text("TPA ", NamedTextColor.GREEN).decorate(TextDecoration.BOLD))
                        .append(Component.text("» ", NamedTextColor.DARK_GRAY).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE))
                        .append(Component.text(message, NamedTextColor.GREEN)));
                target.sendMessage(Component.text()
                        .append(Component.text("TPA ", NamedTextColor.GREEN).decorate(TextDecoration.BOLD))
                        .append(Component.text("» ", NamedTextColor.DARK_GRAY).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE))
                        .append(Component.text(message, NamedTextColor.GREEN)));
                tpRequests.remove(target.getUniqueId());
            }
        }
    }

    /**
     * Denies a pending teleport request received by the specified player.
     *
     * Notifies both the requester and the target player of the denial and removes the request from the active requests list.
     */
    @Command({"tpadeny", "cu tpadeny"})
    @CommandPermission("CraftUtils.tpadeny")
    public void tpdeny(Player target) {
        cleanUpOldRequests();
        TpRequest request = tpRequests.get(target.getUniqueId());
        if (request != null) {
            Player actor = target.getServer().getPlayer(request.getActorUUID());
            if (actor != null) {
                String message = lang.getString("teleport.tpa.result.denied", "Teleport request denied!");
                actor.sendMessage(Component.text()
                        .append(Component.text("TPA ", NamedTextColor.GREEN).decorate(TextDecoration.BOLD))
                        .append(Component.text("» ", NamedTextColor.DARK_GRAY).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE))
                        .append(Component.text(message, NamedTextColor.RED)));
                target.sendMessage(Component.text()
                        .append(Component.text("TPA ", NamedTextColor.GREEN).decorate(TextDecoration.BOLD))
                        .append(Component.text("» ", NamedTextColor.DARK_GRAY).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE))
                        .append(Component.text(message, NamedTextColor.RED)));
                tpRequests.remove(target.getUniqueId());
            }
        }
    }

    /**
     * Cancels all pending teleport requests sent by the specified player.
     *
     * Notifies both the actor and any target players involved that the requests have been cancelled.
     */
    @Command({"tpacancel", "cu tpacancel"})
    @CommandPermission("CraftUtils.tpacancel")
    public void tpcancel(Player actor) {
        cleanUpOldRequests();
        UUID actorId = actor.getUniqueId();
        tpRequests.entrySet().removeIf(entry -> {
            if (entry.getValue().getActorUUID().equals(actorId)) {
                Player target = actor.getServer().getPlayer(entry.getKey());
                String message = lang.getString("teleport.tpa.result.cancelled", "Teleport request cancelled!");
                if (target != null) {
                    target.sendMessage(Component.text()
                            .append(Component.text("TPA ", NamedTextColor.GREEN).decorate(TextDecoration.BOLD))
                            .append(Component.text("» ", NamedTextColor.DARK_GRAY).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE))
                            .append(Component.text(message, NamedTextColor.RED)));
                }
                actor.sendMessage(Component.text()
                        .append(Component.text("TPA ", NamedTextColor.GREEN).decorate(TextDecoration.BOLD))
                        .append(Component.text("» ", NamedTextColor.DARK_GRAY).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE))
                        .append(Component.text(message, NamedTextColor.RED)));
                return true;
            }
            return false;
        });
    }

    /**
     * Removes any pending teleport requests for a player when they leave the server.
     *
     * @param e the player quit event containing the departing player
     */
    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        tpRequests.remove(e.getPlayer().getUniqueId());
    }

    /**
     * Removes expired teleport requests older than two minutes and notifies both the requesting and target players if they are online.
     */
    private void cleanUpOldRequests() {
        long currentTime = System.currentTimeMillis();
        tpRequests.entrySet().removeIf(entry -> {
            if (currentTime - entry.getValue().getTimestamp() > 120000) { // 2 minutes
                Player actor = Bukkit.getPlayer(entry.getValue().getActorUUID());
                Player target = Bukkit.getPlayer(entry.getKey());
                String message = lang.getString("teleport.tpa.result.expired", "Teleport request expired!");

                if (actor != null) {
                    actor.sendMessage(Component.text()
                            .append(Component.text("TPA ", NamedTextColor.GREEN).decorate(TextDecoration.BOLD))
                            .append(Component.text("» ", NamedTextColor.DARK_GRAY).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE))
                            .append(Component.text(message, NamedTextColor.RED)));
                }
                if (target != null) {
                    target.sendMessage(Component.text()
                            .append(Component.text("TPA ", NamedTextColor.GREEN).decorate(TextDecoration.BOLD))
                            .append(Component.text("» ", NamedTextColor.DARK_GRAY).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE))
                            .append(Component.text(message, NamedTextColor.RED)));
                }
                return true;
            }
            return false;
        });
    }

    public static class TpRequest {
        private final UUID actorUUID;
        private final long timestamp;

        public TpRequest(UUID actorUUID, long timestamp) {
            this.actorUUID = actorUUID;
            this.timestamp = timestamp;
        }

        public UUID getActorUUID() {
            return actorUUID;
        }

        public long getTimestamp() {
            return timestamp;
        }
    }
}