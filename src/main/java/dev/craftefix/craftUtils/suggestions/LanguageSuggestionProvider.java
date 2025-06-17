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

    /**
     * Constructs a LanguageSuggestionProvider with the specified plugin instance.
     * <p>
     * Initializes the list of available languages by loading them from the plugin's language manager.
     */
    public LanguageSuggestionProvider(Main plugin) {
        this.plugin = plugin;
        this.availableLanguages = new ArrayList<>();
        reloadLanguages();
    }

    /**
     * Reloads the list of available languages from the plugin's language manager.
     *
     * Clears the current language list and repopulates it with the latest available languages.
     */
    public void reloadLanguages() {
        availableLanguages.clear();
        availableLanguages.addAll(plugin.getLanguageManager().getAvailableLanguages());
    }


    /**
     * Returns a collection of language suggestion strings based on the provided execution context.
     *
     * @param context the current execution context for which suggestions are requested
     * @return a collection of suggested language strings
     */
    @Override
    public @NotNull Collection<String> getSuggestions(@NotNull ExecutionContext context) {
        return List.of(a);
    }
}
