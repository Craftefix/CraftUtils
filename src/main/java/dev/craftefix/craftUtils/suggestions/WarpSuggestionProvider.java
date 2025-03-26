package dev.craftefix.craftUtils.suggestions;

import dev.craftefix.craftUtils.database.WarpManager;
import dev.craftefix.craftUtils.database.WarpManager.Warp;
import revxrsal.commands.autocomplete.SuggestionProvider;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.node.ExecutionContext;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class WarpSuggestionProvider implements SuggestionProvider<BukkitCommandActor> {

    private final WarpManager warpManager = new WarpManager();

    @Override
    public List<String> getSuggestions(ExecutionContext<BukkitCommandActor> context) {
        try {
            List<Warp> warps = warpManager.listWarps();
            return warps.stream()
                    .map(Warp::getWarpName)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}