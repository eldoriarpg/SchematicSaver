package de.eldoria.schematicsaver.commands.create;

import de.eldoria.eldoutilities.localization.MessageComposer;
import de.eldoria.messageblocker.blocker.MessageBlocker;
import de.eldoria.schematicsaver.commands.builder.TemplateBuilder;
import de.eldoria.schematicsaver.commands.builder.TypeBuilder;
import de.eldoria.schematicsaver.commands.builder.VariantBuilder;
import de.eldoria.schematicsaver.config.Configuration;
import de.eldoria.schematicsaver.config.elements.template.Template;
import de.eldoria.schematicsaver.util.Colors;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class Sessions {
    private final BukkitAudiences audiences;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();
    private final MessageBlocker messageBlocker;
    private final Map<UUID, TemplateBuilder> sessions = new HashMap<>();
    private final Configuration configuration;

    public Sessions(Plugin plugin, MessageBlocker messageBlocker, Configuration configuration) {
        audiences = BukkitAudiences.create(plugin);
        this.messageBlocker = messageBlocker;
        this.configuration = configuration;
    }

    public TemplateBuilder create(Player player, String name) {
        var builder = findSession(name).orElse(configuration.templateRegistry().getTemplate(name).map(Template::toBuilder).orElse(new TemplateBuilder(name)));
        sessions.put(player.getUniqueId(), builder);
        return builder;
    }

    public TemplateBuilder edit(Player player, Template template) {
        var templateBuilder = findSession(template.name()).orElse(template.toBuilder());
        sessions.put(player.getUniqueId(), templateBuilder);
        return templateBuilder;
    }

    private Optional<TemplateBuilder> findSession(String name) {
        return sessions.values().stream()
                .filter(templateBuilder -> templateBuilder.name().equalsIgnoreCase(name))
                .findFirst();
    }

    public void renderTemplate(Player player, TemplateBuilder template) {
        var text = MessageComposer.create()
                .text("<%s>%s%", Colors.HEADING, template.name())
                .newLine()
                .text("<%s>Types: <%s><click:suggesT_command:'/schemsave addType '>[Add]</click>", Colors.NAME, Colors.ADD)
                .newLine();
        var types = template.types().stream()
                .map(type -> String.format("  <%s>%s <%s><click:run_command:'/schemsave showType %s'>[Change]</click> <%s><click:run_command:'/schemsave removeType %s'>[Remove]</click>",
                        Colors.NAME, type.name(), Colors.CHANGE, type.name(), Colors.REMOVE, type.name()))
                .collect(Collectors.toList());
        text.text(types)
                .newLine()
                .text("<%s><click:run_command:'schemsave save'>[Save]</click>", Colors.ADD);
        messageBlocker.ifEnabled(() -> text.text(" <click:run_command:'/schemsave messageblock false'><%s>[x]</click>", Colors.REMOVE));
        messageBlocker.announce(player, "[x]");
        audiences.player(player).sendMessage(miniMessage.parse(text.build()));
    }

    public void renderType(Player player, TypeBuilder type) {
        var text = MessageComposer.create()
                .text("<%s>%s%", Colors.HEADING, type.name())
                .newLine()
                .text("<%s>Types: <%s><click:suggest_command:'/schemsave addType '>[Add]</click>", Colors.NAME, Colors.ADD)
                .newLine();
        var types = type.variants().stream()
                .map(variant -> String.format("  <%s>%s <%s><click:run_command:'/schemsave showVariant %s %s'>[Change]</click> <%s><click:run_command:'/schemsave removeVariant %s %s'>[Remove]</click>",
                        Colors.NAME, variant.name(), Colors.CHANGE, type.name(), variant.name(), Colors.REMOVE, type.name(), variant.name()))
                .collect(Collectors.toList());
        text.text(types)
                .newLine()
                .text("<%s><click:run_command:'schemsave save'>[Save]</click> <click:run_command:'/schemsave show'>[Back]</click>", Colors.ADD, Colors.CHANGE);
        messageBlocker.ifEnabled(() -> text.text(" <click:run_command:'/schemsave messageblock false'><%s>[x]</click>", Colors.REMOVE));
        messageBlocker.announce(player, "[x]");
        audiences.player(player).sendMessage(miniMessage.parse(text.build()));
    }

    public void renderTemplate(Player player, VariantBuilder variant) {

    }
}
