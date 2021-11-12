/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicsaver.commands.template;

import com.sk89q.worldedit.util.Direction;
import de.eldoria.eldoutilities.commands.command.util.CommandAssertions;
import de.eldoria.eldoutilities.commands.exceptions.CommandException;
import de.eldoria.eldoutilities.localization.MessageComposer;
import de.eldoria.messageblocker.blocker.MessageBlocker;
import de.eldoria.schematicsaver.commands.template.builder.TemplateBuilder;
import de.eldoria.schematicsaver.commands.template.builder.TypeBuilder;
import de.eldoria.schematicsaver.commands.template.builder.VariantBuilder;
import de.eldoria.schematicsaver.config.elements.template.Template;
import de.eldoria.schematicsaver.util.TextColors;
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

    public Sessions(Plugin plugin, MessageBlocker messageBlocker) {
        audiences = BukkitAudiences.create(plugin);
        this.messageBlocker = messageBlocker;
    }

    public TemplateBuilder create(Player player, String name) {
        var builder = findSession(name).orElse(new TemplateBuilder(name, player.getLocation().toVector()));
        sessions.put(player.getUniqueId(), builder);
        return builder;
    }

    public TemplateBuilder edit(Player player, Template template) {
        var templateBuilder = findSession(template.name()).orElse(template.toBuilder(player.getLocation().toVector()));
        sessions.put(player.getUniqueId(), templateBuilder);
        return templateBuilder;
    }

    private Optional<TemplateBuilder> findSession(String name) {
        return sessions.values().stream()
                .filter(templateBuilder -> templateBuilder.name().equalsIgnoreCase(name))
                .findFirst();
    }

    public void render(Player player, TemplateBuilder template) {
        messageBlocker.blockPlayer(player);
        var text = MessageComposer.create()
                .text("<%s>%s", TextColors.HEADING, template.name())
                .newLine()
                .text("<%s>Types: <%s><click:suggest_command:'/schemtemp addType '>[Add]</click>", TextColors.NAME, TextColors.ADD)
                .text(" <click:run_command:'/schemtemp renderTemplateSelections'><%s>[Show Selections]</click>", TextColors.CHANGE)
                .newLine();
        var types = template.types().stream()
                .map(type -> String.format("  <%s>%s <%s><click:run_command:'/schemtemp showType %s'>[Change]</click> <%s><click:run_command:'/schemtemp removeType %s'>[Remove]</click>",
                        TextColors.NAME, type.name(), TextColors.CHANGE, type.name(), TextColors.REMOVE, type.name()))
                .collect(Collectors.toList());
        text.text(types)
                .newLine()
                .text("<%s><click:run_command:'/schemtemp save'>[Save]</click>", TextColors.ADD);
        messageBlocker.ifEnabled(() -> text.text(" <click:run_command:'/schemtemp messageblock false'><%s>[x]</click>", TextColors.REMOVE));
        messageBlocker.announce(player, "[x]");
        audiences.player(player).sendMessage(miniMessage.parse(text.prependLines(20).build()));
    }

    public void render(Player player, TypeBuilder type) {
        messageBlocker.blockPlayer(player);
        var text = MessageComposer.create()
                .text("<%s>%s", TextColors.HEADING, type.name())
                .newLine()
                .text("<%s>Variants: <%s><click:suggest_command:'/schemtemp addVariant %s '>[Add]</click>", TextColors.NAME, TextColors.ADD, type.name())
                .text(" <click:run_command:'/schemtemp renderTypeSelections %s'><%s>[Show Selections]</click>",type.name(), TextColors.CHANGE)
                .newLine();
        var types = type.variants().stream()
                .map(variant -> String.format("  <%s>%s <%s><click:run_command:'/schemtemp showVariant %s %s'>[Change]</click> <%s><click:run_command:'/schemtemp removeVariant %s %s'>[Remove]</click>",
                        TextColors.NAME, variant.name(), TextColors.CHANGE, type.name(), variant.name(), TextColors.REMOVE, type.name(), variant.name()))
                .collect(Collectors.toList());
        text.text(types)
                .newLine()
                .text("<click:run_command:'/schemtemp show'><%s>[Back]</click>", TextColors.CHANGE);
        messageBlocker.ifEnabled(() -> text.text(" <click:run_command:'/schemtemp messageblock false'><%s>[x]</click>", TextColors.REMOVE));
        messageBlocker.announce(player, "[x]");
        audiences.player(player).sendMessage(miniMessage.parse(text.prependLines(20).build()));
    }

    public void render(Player player, VariantBuilder variant) {
        messageBlocker.blockPlayer(player);
        var text = MessageComposer.create()
                .text("<%s>%s", TextColors.HEADING, variant.name())
                .newLine()
                .text("<%s>Boundings", TextColors.NAME)
                .text(" <click:run_command:'/schemtemp renderVariantSelection %s'><%s>[Show]</click>",variant.path(), TextColors.CHANGE)
                .text(" <click:run_command:'/schemtemp selectVariantRegion %s'><%s>[Select]</click>",variant.path(), TextColors.CHANGE)
                .text(" <click:run_command:'/schemtemp modifyVariant %s selection'><%s>[Update]</click>", variant.path(), TextColors.CHANGE)
                .newLine()
                .text("<%s>Rotation:<%s> %s <%s><click:suggest_command:'/schemtemp modifyVariant %s rotation '>[Change]</click>",
                        TextColors.NAME, TextColors.VALUE, variant.rotation(), TextColors.CHANGE, variant.path())
                .newLine()
                .text("<%s>Flip:<%s> %s <%s><click:suggest_command:'/schemtemp modifyVariant %s flip '>[Change]</click>",
                        TextColors.NAME, TextColors.VALUE, flipToString(variant.flip()), TextColors.CHANGE, variant.path())
                .newLine()
                .text("<%s><click:run_command:'/schemtemp showType %s'>[Back]</click>", TextColors.CHANGE, variant.type().name());
        messageBlocker.ifEnabled(() -> text.text(" <click:run_command:'/schemtemp messageblock false'><%s>[x]</click>", TextColors.REMOVE));
        messageBlocker.announce(player, "[x]");
        audiences.player(player).sendMessage(miniMessage.parse(text.prependLines(20).build()));
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

    public void close(Player player) {
        sessions.remove(player.getUniqueId());
        messageBlocker.unblockPlayer(player);
    }
}
