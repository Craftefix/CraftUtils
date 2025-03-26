package dev.craftefix.craftUtils.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
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

    // Target UUID, TpRequest (Actor UUID and Timestamp)
    HashMap<UUID, TpRequest> tpRequests = new HashMap<>();

    @Command({"tpask", "tpa", "cu tpask"})
    @CommandPermission("CraftUtils.tpask")
    public void tpask(Player actor, @Named("Player") Player target) {
        cleanUpOldRequests();
        if (actor.isOnline() && target.isOnline()) {
            if (actor.equals(target)) {
                actor.sendMessage(Component.text()
                        .append(Component.text("TPA ", NamedTextColor.GREEN).decorate(TextDecoration.BOLD))
                        .append(Component.text("» ", NamedTextColor.DARK_GRAY).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE))
                        .append(Component.text("You can't teleport to yourself!", NamedTextColor.RED)));
                return;
            }
            if (tpRequests.get(target.getUniqueId()) == null) {
                tpRequests.put(target.getUniqueId(), new TpRequest(actor.getUniqueId(), System.currentTimeMillis()));
                actor.sendMessage(Component.text()
                        .append(Component.text("TPA ", NamedTextColor.GREEN).decorate(TextDecoration.BOLD))
                        .append(Component.text("» ", NamedTextColor.DARK_GRAY).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE))
                        .append(Component.text("Sent teleport request to ", NamedTextColor.GRAY))
                        .append(Component.text(target.getName(), NamedTextColor.BLUE))
                        .append(Component.text(". \n", NamedTextColor.DARK_GRAY))
                        .append(Component.text(" [Cancel] ", NamedTextColor.RED))
                        .clickEvent(ClickEvent.runCommand("/cu tpacancel")));

                target.sendMessage(Component.text()
                        .append(Component.text("TPA ", NamedTextColor.GREEN).decorate(TextDecoration.BOLD))
                        .append(Component.text("» ", NamedTextColor.DARK_GRAY).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE))
                        .append(Component.text(actor.getName(), NamedTextColor.GREEN))
                        .append(Component.text(" wants to teleport to ", NamedTextColor.GRAY))
                        .append(Component.text("you.", NamedTextColor.BLUE)));
                target.sendMessage(Component.text()
                        .append(Component.text("TPA ", NamedTextColor.GREEN).decorate(TextDecoration.BOLD))
                        .append(Component.text("» ", NamedTextColor.DARK_GRAY).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE))
                        .append(Component.text("Accept]", NamedTextColor.GREEN))
                                .clickEvent(ClickEvent.runCommand("/cu tpaaccept")));
                target.sendMessage(Component.text()
                        .append(Component.text("TPA ", NamedTextColor.GREEN).decorate(TextDecoration.BOLD))
                        .append(Component.text("» ", NamedTextColor.DARK_GRAY).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE))
                        .append(Component.text("[Deny]", NamedTextColor.RED))
                                .clickEvent(ClickEvent.runCommand("/cu tpadeny")));
            } else {
                actor.sendMessage(Component.text("You already have a pending request to this player!", NamedTextColor.RED));
            }
        }
    }

    @Command({"tpaaccept", "cu tpaaccept"})
    @CommandPermission("CraftUtils.tpaaccept")
    public void tpaccept(Player target) {
        cleanUpOldRequests();
        TpRequest request = tpRequests.get(target.getUniqueId());
        if (request != null) {
            Player actor = target.getServer().getPlayer(request.getActorUUID());
            if (actor != null) {
                actor.teleport(target);
                actor.sendMessage(Component.text()
                        .append(Component.text("TPA ", NamedTextColor.GREEN).decorate(TextDecoration.BOLD))
                        .append(Component.text("» ", NamedTextColor.DARK_GRAY).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE))
                        .append( Component.text("Teleport request accepted!", NamedTextColor.GREEN)));
                target.sendMessage(Component.text()
                        .append(Component.text("TPA ", NamedTextColor.GREEN).decorate(TextDecoration.BOLD))
                        .append(Component.text("» ", NamedTextColor.DARK_GRAY).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE))
                        .append( Component.text("Teleport request accepted!", NamedTextColor.GREEN)));
                tpRequests.remove(target.getUniqueId());
            }
        }
    }

    @Command({"tpadeny", "cu tpadeny"})
    @CommandPermission("CraftUtils.tpadeny")
    public void tpdeny(Player target) {
        cleanUpOldRequests();
        TpRequest request = tpRequests.get(target.getUniqueId());
        if (request != null) {
            Player actor = target.getServer().getPlayer(request.getActorUUID());
            if (actor != null) {
                actor.sendMessage(Component.text()
                        .append(Component.text("TPA ", NamedTextColor.GREEN).decorate(TextDecoration.BOLD))
                        .append(Component.text("» ", NamedTextColor.DARK_GRAY).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE))
                        .append( Component.text("Teleport request denied!", NamedTextColor.GREEN)));
                target.sendMessage(Component.text()
                        .append(Component.text("TPA ", NamedTextColor.GREEN).decorate(TextDecoration.BOLD))
                        .append(Component.text("» ", NamedTextColor.DARK_GRAY).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE))
                        .append( Component.text("Teleport request denied!", NamedTextColor.GREEN)));
                tpRequests.remove(target.getUniqueId());
            }
        }
    }

    @Command({"tpacancel", "cu tpacancel"})
    @CommandPermission("CraftUtils.tpacancel")
    public void tpcancel(Player actor) {
        cleanUpOldRequests();
        TpRequest request = tpRequests.get(actor.getUniqueId());
        if (request == null) {
            actor.sendMessage(Component.text("You have no pending teleport requests to cancel!", NamedTextColor.RED));
        } else {
            tpRequests.entrySet().removeIf(entry -> {
                if (entry.getValue().getActorUUID().equals(actor.getUniqueId())) {
                    Player target = actor.getServer().getPlayer(entry.getKey());
                    if (target != null ){
                        target.sendMessage(Component.text("Teleport request from " + actor + " canceled!", NamedTextColor.RED));
                    }

                    return true;
                }
                return false;
            });
        }
    }

    private void cleanUpOldRequests() {
        long currentTime = System.currentTimeMillis();
        long fiveMinutesInMillis = 5 * 60 * 1000;
        tpRequests.entrySet().removeIf(entry -> currentTime - entry.getValue().getTimestamp() > fiveMinutesInMillis);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        UUID playerUUID = event.getPlayer().getUniqueId();
        tpRequests.remove(playerUUID);
        tpRequests.entrySet().removeIf(entry -> entry.getValue().getActorUUID().equals(playerUUID));
    }

    private static class TpRequest {
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