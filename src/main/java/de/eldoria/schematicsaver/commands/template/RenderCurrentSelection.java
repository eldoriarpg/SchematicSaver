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
import de.eldoria.schematicsaver.commands.util.WorldEditSelection;
import de.eldoria.schematicsaver.services.BoundingRender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class RenderCurrentSelection extends AdvancedCommand implements IPlayerTabExecutor {
    private final BoundingRender boundingRender;

    public RenderCurrentSelection(Plugin plugin, BoundingRender boundingRender) {
        super(plugin, CommandMeta.builder("renderCurrentSelection")
                .build());
        this.boundingRender = boundingRender;
    }

    @Override
    public void onCommand(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) throws CommandException {
        var selectionBoundings = WorldEditSelection.getSelectionBoundings(player);
        boundingRender.clearPlayer(player);
        boundingRender.renderBox(player, selectionBoundings);
    }
}
