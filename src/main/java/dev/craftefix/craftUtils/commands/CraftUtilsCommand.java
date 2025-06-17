package dev.craftefix.craftUtils.commands;

import dev.craftefix.craftUtils.Main;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;


public class CraftUtilsCommand {
    private final Main plugin;
    private final YamlConfiguration lang;

    /**
     * Constructs a new CraftUtilsCommand with the specified plugin instance.
     *
     * Initializes the command handler and loads the language configuration from the provided plugin.
     */
    public CraftUtilsCommand(Main plugin) {
        this.plugin = plugin;
        this.lang = plugin.getLang();
    }

    /**
     * Sends the player an informational message about the CraftUtils plugin, including its name, description, version, and clickable links to GitHub, Modrinth, and Discord.
     *
     * The displayed text is localized using the plugin's language configuration, with default values as fallbacks.
     */
    @Command({"craftutils", "cu"})
    public void craftutils(Player actor) {
        String pluginName = lang.getString("plugin.name", "~~~ CraftUtils ~~~");
        String description = lang.getString("plugin.description", "The utils plugin that has everything you need.");
        String versionPrefix = lang.getString("plugin.version-prefix", "Version: ");

        actor.sendMessage(Component.text()
                .append(Component.text(pluginName, NamedTextColor.DARK_PURPLE).decorate(TextDecoration.BOLD))
                .append(Component.text("\n» ", NamedTextColor.DARK_GRAY).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE))
                .append(Component.text(description, NamedTextColor.BLUE).decorate(TextDecoration.ITALIC))
                .append(Component.text("\n» ", NamedTextColor.DARK_GRAY).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE))
                .append(Component.text(versionPrefix, NamedTextColor.BLUE))
                .append(Component.text(plugin.getDescription().getVersion(), NamedTextColor.YELLOW)));
        actor.sendMessage(Component.text()
                .append(Component.text("» ", NamedTextColor.DARK_GRAY).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE))
                .append(Component.text("CraftUtils on Github", NamedTextColor.DARK_BLUE))
                        .clickEvent(ClickEvent.openUrl("https://github.com/Craftefix/CraftUtils")));
        actor.sendMessage(Component.text()
                .append(Component.text("» ", NamedTextColor.DARK_GRAY).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE))
                .append(Component.text("CraftUtils on Modrinth", NamedTextColor.YELLOW))
                        .clickEvent(ClickEvent.openUrl("https://modrinth.com/plugin/craftutils")));
        actor.sendMessage(Component.text()
                .append(Component.text("» ", NamedTextColor.DARK_GRAY).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE))
                .append(Component.text("Our Discord", NamedTextColor.BLUE))
                        .clickEvent(ClickEvent.openUrl("https://discord.gg/GbKQUqp6yG")));

    }
}
