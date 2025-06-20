package dev.craftefix.craftUtils.suggestions;

import dev.craftefix.craftUtils.database.HomeManager;
import org.bukkit.entity.Player;
import revxrsal.commands.autocomplete.SuggestionProvider;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.node.ExecutionContext;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class HomeSuggestionProvider implements SuggestionProvider<BukkitCommandActor> {
    private final HomeManager homeManager = new HomeManager();

    @Override
    public List<String> getSuggestions(ExecutionContext<BukkitCommandActor> context) {
        try {
            Player player = context.actor().asPlayer();
            List<HomeManager.Home> homes = homeManager.getAllHomes(player.getUniqueId().toString());
            return homes.stream()
                    .map(HomeManager.Home::getHomeName)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}