/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicsaver.commands.create;

import com.sk89q.worldedit.util.Direction;
import de.eldoria.eldoutilities.commands.command.util.CommandAssertions;
import de.eldoria.eldoutilities.commands.exceptions.CommandException;
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
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

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
        var builder = findSession(name).orElse(new TemplateBuilder(name));
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

    public void render(Player player, TemplateBuilder template) {
        var text = MessageComposer.create()
                .text("<%s>%s%", Colors.HEADING, template.name())
                .newLine()
                .text("<%s>Types: <%s><click:suggest_command:'/schemsave addType '>[Add]</click>", Colors.NAME, Colors.ADD)
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

    public void render(Player player, TypeBuilder type) {
        var text = MessageComposer.create()
                .text("<%s>%s%", Colors.HEADING, type.name())
                .newLine()
                .text("<%s>Types: <%s><click:suggest_command:'/schemsave addVariant '>[Add]</click>", Colors.NAME, Colors.ADD, type.name())
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

    public void render(Player player, VariantBuilder variant) {
        var text = MessageComposer.create()
                .text("<%s>%s%", Colors.HEADING, variant.name())
                .newLine()
                .text("<%s>Boundings:<%s> %s <%s><click:run_command:'/schemsave modifyVariant %s'>[Change]</click>",
                        Colors.NAME, Colors.VALUE, boundingBoxToString(variant.boundings()), Colors.CHANGE, variant.path())
                .newLine()
                .text("<%s>Rotation:<%s> %s <%s><click:run_command:'/schemsave modifyVariant %s rotation'>[Change]</click>",
                        Colors.NAME, Colors.VALUE, variant.rotation(), Colors.CHANGE, variant.path())
                .newLine()
                .text("<%s>Flip:<%s> %s <%s><click:run_command:'/schemsave modifyVariant %s flip'>[Change]</click>",
                        Colors.NAME, Colors.VALUE, flipToString(variant.flip()), Colors.CHANGE, variant.path())
                .newLine()
                .text("<%s><click:run_command:'/schemsave showType %s'>[Back]</click>", Colors.ADD, Colors.CHANGE, variant.type().name());
        messageBlocker.ifEnabled(() -> text.text(" <click:run_command:'/schemsave messageblock false'><%s>[x]</click>", Colors.REMOVE));
        messageBlocker.announce(player, "[x]");
        audiences.player(player).sendMessage(miniMessage.parse(text.build()));
    }

    private String boundingBoxToString(BoundingBox box) {
        return String.format("%s %s", vectorToString(box.getMin()), vectorToString(box.getMax()));
    }

    private String vectorToString(Vector vector) {
        return String.format("[%s,%s,%s]", vector.getBlockX(), vector.getBlockY(), vector.getBlockZ());
    }

    private String flipToString(Direction direction) {
        if (direction != null) {
            return direction.name();
        }
        return "none";
    }

    public TemplateBuilder getSession(Player player) throws CommandException {
        var templateBuilder = sessions.get(player.getUniqueId());
        CommandAssertions.isTrue(templateBuilder != null, "error.noSession");
        return templateBuilder;
    }
}
