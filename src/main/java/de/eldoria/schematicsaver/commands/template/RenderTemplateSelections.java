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
import de.eldoria.schematicsaver.services.BoundingRender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class RenderTemplateSelections extends AdvancedCommand implements IPlayerTabExecutor {
    private final Sessions sessions;
    private final BoundingRender render;

    public RenderTemplateSelections(Plugin plugin, Sessions sessions, BoundingRender render) {
        super(plugin, CommandMeta.builder("renderTemplateSelections")
                .build());
        this.sessions = sessions;
        this.render = render;
    }

    @Override
    public void onCommand(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) throws CommandException {
        render.clearPlayer(player);
        var session = sessions.getSession(player);
        var boundings = session.getBoundings();
        for (var bounding : boundings) {
            render.renderBox(player, bounding);
        }
    }
}