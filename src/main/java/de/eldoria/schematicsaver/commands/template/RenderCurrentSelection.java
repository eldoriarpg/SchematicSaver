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
import de.eldoria.schematicsaver.services.BoundingRenderer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class RenderCurrentSelection extends AdvancedCommand implements IPlayerTabExecutor {
    private final BoundingRenderer boundingRenderer;

    public RenderCurrentSelection(Plugin plugin, BoundingRenderer boundingRenderer) {
        super(plugin, CommandMeta.builder("renderCurrentSelection")
                .addUnlocalizedArgument("seconds", false)
                .build());
        this.boundingRenderer = boundingRenderer;
    }

    @Override
    public void onCommand(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) throws CommandException {
        var selectionBoundings = WorldEditSelection.getSelectionBoundings(player);
        boundingRenderer.clearPlayer(player);
        boundingRenderer.renderBox(player, selectionBoundings, args.asInt(0, -1));
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) throws CommandException {
        if (args.sizeIs(1)) {
            return Collections.singletonList("[seconds]");
        }
        return Collections.emptyList();
    }
}
