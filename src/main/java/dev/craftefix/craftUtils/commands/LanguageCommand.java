package dev.craftefix.craftUtils.commands;

import dev.craftefix.craftUtils.Main;
import dev.craftefix.craftUtils.suggestions.LanguageSuggestionProvider;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Named;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@Command({"language", "lang", "cu language"})
public class LanguageCommand {
    private final Main plugin;

    /**
     * Creates a new LanguageCommand handler for managing player language commands.
     *
     * @param plugin the main plugin instance used for language management and localization
     */
    public LanguageCommand(Main plugin) {
        this.plugin = plugin;
    }

    /**
     * Changes the player's language to the specified language code.
     *
     * If the language code is valid, updates the player's language and sends a green confirmation message. If the language code is invalid, sends a red error message indicating the language is not available.
     *
     * @param langCode the language code to set for the player
     */
    @Subcommand("set")
    @CommandPermission("CraftUtils.language.set")
    public void setLanguage(Player player, @Named("language") @SuggestWith(LanguageSuggestionProvider.class) String langCode) {
        try {
            plugin.getLanguageManager().setPlayerLanguage(player, langCode);
            String message = plugin.getLang(player).getString("language.changed", "Language changed to {language}")
                    .replace("{language}", langCode);
            player.sendMessage(Component.text(message, NamedTextColor.GREEN));
        } catch (IllegalArgumentException e) {
            String message = plugin.getLang(player).getString("language.not-available", "Language {language} is not available")
                    .replace("{language}", langCode);
            player.sendMessage(Component.text(message, NamedTextColor.RED));
        }
    }

    /**
     * Sends the player a list of all available languages, highlighting their current language.
     *
     * The list is prefixed with a localized header message. The player's current language is marked with a green "(current)" label.
     */
    @Subcommand("list")
    @CommandPermission("CraftUtils.language.list")
    public void listLanguages(Player player) {
        String header = plugin.getLang(player).getString("language.available", "Available languages:");
        player.sendMessage(Component.text(header, NamedTextColor.YELLOW));

        for (String lang : plugin.getLanguageManager().getAvailableLanguages()) {
            String current = plugin.getLanguageManager().getPlayerLanguage(player);
            Component langComponent = Component.text("- " + lang);
            if (lang.equals(current)) {
                langComponent = langComponent.append(Component.text(" (current)", NamedTextColor.GREEN));
            }
            player.sendMessage(langComponent);
        }
    }

    /**
     * Displays the player's current language setting.
     *
     * Sends a yellow-colored message to the player indicating their current language code.
     */
    @Subcommand("current")
    @CommandPermission("CraftUtils.language.current")
    public void getCurrentLanguage(Player player) {
        String current = plugin.getLanguageManager().getPlayerLanguage(player);
        String message = plugin.getLang(player).getString("language.current", "Your current language is: {language}")
                .replace("{language}", current);
        player.sendMessage(Component.text(message, NamedTextColor.YELLOW));
    }
}
