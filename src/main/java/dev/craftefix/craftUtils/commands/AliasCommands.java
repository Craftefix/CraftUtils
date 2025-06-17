package dev.craftefix.craftUtils.commands;

import dev.craftefix.craftUtils.Main;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.GameMode;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Optional;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public class AliasCommands {
    private final YamlConfiguration lang;

    public AliasCommands() {
        this.lang = Main.getInstance().getLang();
    }

    private void setGameMode(Player actor, Player target, GameMode gameMode) {
        if (target != null) {
            target.setGameMode(gameMode);
            String message = lang.getString("alias.gamemode.changed-other", "Changed {player}'s gamemode to {gamemode}.")
                    .replace("{player}", target.getName())
                    .replace("{gamemode}", gameMode.toString().toLowerCase());
            actor.sendMessage(Component.text(message, NamedTextColor.GRAY));
        } else {
            actor.setGameMode(gameMode);
            String message = lang.getString("alias.gamemode.changed-self", "Changed gamemode to {gamemode}.")
                    .replace("{gamemode}", gameMode.toString().toLowerCase());
            actor.sendMessage(Component.text(message, NamedTextColor.GRAY));
        }
    }

    @Command({"gmc"})
    @CommandPermission("CraftUtils.Gamemode.Creative")
    public void gmc(Player actor, @Optional Player target) {
        setGameMode(actor, target, GameMode.CREATIVE);
    }

    @Command("gms")
    @CommandPermission("CraftUtils.Gamemode.Survival")
    public void gms(Player actor, @Optional Player target) {
        setGameMode(actor, target, GameMode.SURVIVAL);
    }

    @Command("gma")
    @CommandPermission("CraftUtils.Gamemode.Adventure")
    public void gma(Player actor, @Optional Player target) {
        setGameMode(actor, target, GameMode.ADVENTURE);
    }

    @Command("gmsp")
    @CommandPermission("CraftUtils.Gamemode.Spectator")
    public void gmsp(Player actor, @Optional Player target) {
        setGameMode(actor, target, GameMode.SPECTATOR);
    }
}
