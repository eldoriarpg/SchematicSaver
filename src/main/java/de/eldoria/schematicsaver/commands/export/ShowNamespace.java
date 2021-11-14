/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicsaver.commands.export;

import de.eldoria.eldoutilities.commands.command.AdvancedCommand;
import de.eldoria.eldoutilities.commands.command.CommandMeta;
import de.eldoria.eldoutilities.commands.command.util.Arguments;
import de.eldoria.eldoutilities.commands.exceptions.CommandException;
import de.eldoria.eldoutilities.commands.executor.IPlayerTabExecutor;
import de.eldoria.eldoutilities.simplecommands.TabCompleteUtil;
import de.eldoria.schematicsaver.config.Configuration;
import de.eldoria.schematicsaver.services.BoundingRenderer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class ShowNamespace extends AdvancedCommand implements IPlayerTabExecutor {
    private final BoundingRenderer boundingRenderer;
    private final Configuration configuration;

    public ShowNamespace(Plugin plugin, BoundingRenderer boundingRenderer, Configuration configuration) {
        super(plugin, CommandMeta.builder("showNamespace")
                .addUnlocalizedArgument("namespace", true)
                .addUnlocalizedArgument("duration", false)
                .build());
        this.boundingRenderer = boundingRenderer;
        this.configuration = configuration;
    }

    @Override
    public void onCommand(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) throws CommandException {
        var namespace = configuration.templateRegistry().getNamespace(args.asString(0));
        var template = configuration.templateRegistry().getTemplate(namespace.template());

        for (var variant : template.variants()) {
            boundingRenderer.renderBox(player, variant.relative(player), args.asInt(1, 10));
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) throws CommandException {
        return switch (args.size()) {
            case 1 -> TabCompleteUtil.complete(args.asString(0), configuration.templateRegistry().namespaceNames());
            case 2 -> TabCompleteUtil.completeInt(args.asString(1), 0, 60);
            default -> Collections.emptyList();
        };
    }
}
