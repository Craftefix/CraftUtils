package dev.craftefix.essentials;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.autocomplete.SuggestionProvider;
import revxrsal.commands.command.CommandActor;
import revxrsal.commands.node.ExecutionContext;

import java.util.Collection;
import java.util.stream.Collectors;

public class suggestionProviders implements SuggestionProvider<CommandActor> {

    public @NotNull Collection<String> getSuggestions(@NotNull ExecutionContext<CommandActor> executionContext) {
        // Fetch and return online players' names as a list
        return Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)   // Get the names of all online players
                .collect(Collectors.toList());  // Collect them into a List
    }
}