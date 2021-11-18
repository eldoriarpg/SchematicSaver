/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicsaver.commands.template;

import de.eldoria.eldoutilities.commands.command.util.CommandAssertions;
import de.eldoria.eldoutilities.commands.exceptions.CommandException;
import de.eldoria.messageblocker.blocker.MessageBlocker;
import de.eldoria.schematicsaver.commands.template.builder.TemplateBuilder;
import de.eldoria.schematicsaver.commands.template.builder.TypeBuilder;
import de.eldoria.schematicsaver.commands.template.builder.VariantBuilder;
import de.eldoria.schematicsaver.commands.util.Verifier;
import de.eldoria.schematicsaver.config.elements.template.Template;
import de.eldoria.schematicsaver.util.TextColors;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class Sessions {
    private final BukkitAudiences audiences;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();
    private final MessageBlocker messageBlocker;
    private final Map<UUID, TemplateBuilder> sessions = new HashMap<>();

    public Sessions(Plugin plugin, MessageBlocker messageBlocker) {
        audiences = BukkitAudiences.create(plugin);
        this.messageBlocker = messageBlocker;
    }

    public TemplateBuilder create(Player player, String name) throws CommandException {
        Verifier.checkName(name);
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
        var text = template.asComponent();
        messageBlocker.ifEnabled(() -> text.text(" <click:run_command:'/schemtemp close'><%s>[x]</click>", TextColors.REMOVE));
        messageBlocker.announce(player, "[x]");
        audiences.player(player).sendMessage(miniMessage.parse(text.prependLines(20).build()));
    }

    public void render(Player player, TypeBuilder type) {
        messageBlocker.blockPlayer(player);
        var text = type.asComponent();
        messageBlocker.ifEnabled(() -> text.text(" <click:run_command:'/schemtemp close'><%s>[x]</click>", TextColors.REMOVE));
        messageBlocker.announce(player, "[x]");
        audiences.player(player).sendMessage(miniMessage.parse(text.prependLines(20).build()));
    }

    public void render(Player player, VariantBuilder variant) {
        messageBlocker.blockPlayer(player);
        var text = variant.asComponent();
        messageBlocker.ifEnabled(() -> text.text(" <click:run_command:'/schemtemp close'><%s>[x]</click>", TextColors.REMOVE));
        messageBlocker.announce(player, "[x]");
        audiences.player(player).sendMessage(miniMessage.parse(text.prependLines(20).build()));
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

    public boolean hasSession(Player player) {
        return sessions.containsKey(player.getUniqueId());
    }
}
