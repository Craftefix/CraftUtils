package dev.craftefix.craftUtils.suggestions;

import dev.craftefix.craftUtils.Main;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.autocomplete.SuggestionProvider;
import revxrsal.commands.node.ExecutionContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class LanguageSuggestionProvider implements SuggestionProvider {
    private final Main plugin;
    private final List<String> availableLanguages;

    public LanguageSuggestionProvider(Main plugin) {
        this.plugin = plugin;
        this.availableLanguages = new ArrayList<>();
        reloadLanguages();
    }

    public void reloadLanguages() {
        availableLanguages.clear();
        availableLanguages.addAll(plugin.getLanguageManager().getAvailableLanguages());
    }


    @Override
    public @NotNull Collection<String> getSuggestions(@NotNull ExecutionContext context) {
        return List.of(a);
    }
}
