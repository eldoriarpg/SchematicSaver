/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicsaver.commands.template;

import de.eldoria.eldoutilities.commands.command.AdvancedCommand;
import de.eldoria.eldoutilities.commands.command.CommandMeta;
import de.eldoria.eldoutilities.commands.command.util.Arguments;
import de.eldoria.eldoutilities.commands.exceptions.CommandException;
import de.eldoria.eldoutilities.commands.executor.IPlayerTabExecutor;
import de.eldoria.eldoutilities.simplecommands.TabCompleteUtil;
import de.eldoria.schematicsaver.services.BoundingRenderer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class RenderTypeSelections extends AdvancedCommand implements IPlayerTabExecutor {
    private final Sessions sessions;
    private final BoundingRenderer render;

    public RenderTypeSelections(Plugin plugin, Sessions sessions, BoundingRenderer render) {
        super(plugin, CommandMeta.builder("renderTypeSelections")
                .addUnlocalizedArgument("type", true)
                .addUnlocalizedArgument("seconds", false)
                .build());
        this.sessions = sessions;
        this.render = render;
    }

    @Override
    public void onCommand(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) throws CommandException {
        render.clearPlayer(player);
        var session = sessions.getSession(player);
        var boundings = session.getType(args.asString(0)).getBoundings();
        for (var bounding : boundings) {
            render.renderBox(player, bounding, args.asInt(1, -1));
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) throws CommandException {
        return switch (args.size()) {
            case 1 -> TabCompleteUtil.complete(args.asString(0), sessions.getSession(player).typeNames());
            case 2 -> TabCompleteUtil.completeInt(args.asString(1), 0, 60);
            default -> Collections.emptyList();
        };
    }
}
