package dev.craftefix.craftUtils;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Optional;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public class AliasCommands {

    @Command({"gmc"})
    @CommandPermission("CraftUtils.Gamemode.Creative")
    public void gmc(Player actor, @Optional Player target) {
        if (target != null) {
            target.setGameMode(GameMode.CREATIVE);
        } else {
            actor.setGameMode(GameMode.CREATIVE);
        }
    }
    @Command("gms")
    @CommandPermission("CraftUtils.Gamemode.Survival")
    public void gms(Player actor, @Optional Player target) {
        if (target != null) {
            target.setGameMode(GameMode.SURVIVAL);
        } else {
            actor.setGameMode(GameMode.SURVIVAL);
        }
    }
    @Command("gma")
    @CommandPermission("CraftUtils.Gamemode.Adventure")
    public void gma(Player actor, @Optional Player target) {
        if (target != null) {
            target.setGameMode(GameMode.ADVENTURE);
        } else {
            actor.setGameMode(GameMode.ADVENTURE);
        }
    }
}
