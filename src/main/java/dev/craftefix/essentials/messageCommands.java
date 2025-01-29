package dev.craftefix.essentials;

import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;

import java.util.HashMap;
import java.util.UUID;

public class messageCommands {
    private final HashMap<UUID, UUID> messagers = new HashMap<>();
    public HashMap<UUID, UUID> getMessagers;
    @Command({"msg", "message", "tell"})
    public void msg(Player actor, Player target,  String message) {
        actor.sendMessage("You to " + NamedTextColor.YELLOW + target.getName() + " :" + NamedTextColor.GRAY+ message);
        target.sendMessage(NamedTextColor.YELLOW + target.getName() + "to you: " + NamedTextColor.GRAY+ message);
        if (actor.isOnline() && target.isOnline()) {getMessagers.put(actor.getUniqueId(), target.getUniqueId());}
    }

    @Command({"r", "reply"})
    public void reply() {}



}
