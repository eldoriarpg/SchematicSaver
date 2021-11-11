/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicsaver.commands.create;

import de.eldoria.eldoutilities.commands.command.AdvancedCommand;
import de.eldoria.eldoutilities.commands.command.CommandMeta;
import de.eldoria.eldoutilities.commands.command.util.Arguments;
import de.eldoria.eldoutilities.commands.exceptions.CommandException;
import de.eldoria.eldoutilities.commands.executor.IPlayerTabExecutor;
import de.eldoria.eldoutilities.simplecommands.TabCompleteUtil;
import de.eldoria.schematicsaver.services.BoundingRender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ShowTypeSelections extends AdvancedCommand implements IPlayerTabExecutor {
    private final Sessions sessions;
    private final BoundingRender render;

    public ShowTypeSelections(Plugin plugin, Sessions sessions, BoundingRender render) {
        super(plugin, CommandMeta.builder("showTypeSelections")
                .addUnlocalizedArgument("type", true)
                .build());
        this.sessions = sessions;
        this.render = render;
    }

    @Override
    public void onCommand(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) throws CommandException {
        var session = sessions.getSession(player);
        var boundings = session.getType(args.asString(0)).getBoundings();
        for (var bounding : boundings) {
            render.renderBox(player, bounding);
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) throws CommandException {
        return TabCompleteUtil.complete(args.asString(0), sessions.getSession(player).typeNames());
    }
}
