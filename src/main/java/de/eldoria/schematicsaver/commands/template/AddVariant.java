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
import de.eldoria.schematicsaver.commands.util.WorldEditSelection;
import de.eldoria.schematicsaver.services.BoundingRenderer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class AddVariant extends AdvancedCommand implements IPlayerTabExecutor {
    private final Sessions sessions;
    private final BoundingRenderer boundingRenderer;

    public AddVariant(Plugin plugin, Sessions sessions, BoundingRenderer boundingRenderer) {
        super(plugin, CommandMeta.builder("addVariant")
                .addUnlocalizedArgument("type_name", true)
                .addUnlocalizedArgument("variant_name", true)
                .build());
        this.sessions = sessions;
        this.boundingRenderer = boundingRenderer;
    }

    @Override
    public void onCommand(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) throws CommandException {
        var session = sessions.getSession(player);
        var type = session.getType(args.asString(0));

        var box = WorldEditSelection.getSelectionBoundings(player);
        if (!args.flags().has("f")) {
            session.assertOverlap(box, null);
        }

        var variant = type.addVariant(args.asString(1), box);
        boundingRenderer.renderBox(player, box);
        sessions.render(player, variant);
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) throws CommandException {
        if (args.size() == 1) {
            return TabCompleteUtil.complete(args.asString(0), sessions.getSession(player).typeNames());
        }

        return Collections.singletonList("<variant_name>");
    }
}
