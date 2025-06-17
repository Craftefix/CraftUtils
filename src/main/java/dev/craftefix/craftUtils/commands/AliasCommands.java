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

    /**
     * Initializes the AliasCommands instance and loads the language configuration.
     */
    public AliasCommands() {
        this.lang = Main.getInstance().getLang();
    }

    /**
     * Sets the specified game mode for a player and sends a localized confirmation message to the command executor.
     *
     * If a target player is provided, their game mode is changed and the executor is notified. If no target is provided, the executor's own game mode is changed and they are notified.
     */
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

    /**
     * Sets the game mode to Creative for the specified player or the command executor.
     *
     * If a target player is provided, their game mode is changed; otherwise, the executor's game mode is set to Creative.
     */
    @Command({"gmc"})
    @CommandPermission("CraftUtils.Gamemode.Creative")
    public void gmc(Player actor, @Optional Player target) {
        setGameMode(actor, target, GameMode.CREATIVE);
    }

    /**
     * Sets the game mode of the specified player or the command executor to Survival mode.
     *
     * If a target player is provided, their game mode is changed; otherwise, the executor's game mode is updated.
     */
    @Command("gms")
    @CommandPermission("CraftUtils.Gamemode.Survival")
    public void gms(Player actor, @Optional Player target) {
        setGameMode(actor, target, GameMode.SURVIVAL);
    }

    /**
     * Sets the game mode to Adventure for the specified player or the command executor.
     *
     * If a target player is provided, their game mode is changed; otherwise, the executor's game mode is set to Adventure.
     */
    @Command("gma")
    @CommandPermission("CraftUtils.Gamemode.Adventure")
    public void gma(Player actor, @Optional Player target) {
        setGameMode(actor, target, GameMode.ADVENTURE);
    }

    /**
     * Sets the specified player's game mode to Spectator, or the executor's if no target is provided.
     *
     * @param actor the player executing the command
     * @param target the player whose game mode will be changed, or null to target the executor
     */
    @Command("gmsp")
    @CommandPermission("CraftUtils.Gamemode.Spectator")
    public void gmsp(Player actor, @Optional Player target) {
        setGameMode(actor, target, GameMode.SPECTATOR);
    }
}
