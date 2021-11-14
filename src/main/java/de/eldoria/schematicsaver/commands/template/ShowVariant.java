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
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class ShowVariant extends AdvancedCommand implements IPlayerTabExecutor {
    private final Sessions sessions;

    public ShowVariant(Plugin plugin, Sessions sessions) {
        super(plugin, CommandMeta.builder("showVariant")
                .addUnlocalizedArgument("type", true)
                .addUnlocalizedArgument("variant", true)
                .build());
        this.sessions = sessions;
    }

    @Override
    public void onCommand(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) throws CommandException {
        sessions.render(player, sessions.getSession(player).getType(args.asString(0)).getVariant(args.asString(1)));
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) throws CommandException {
        return switch (args.size()) {
            case 1 -> TabCompleteUtil.complete(args.asString(0), sessions.getSession(player).typeNames());
            case 2 -> TabCompleteUtil.complete(args.asString(1), sessions.getSession(player).getType(args.asString(0)).variantNames());
            default -> Collections.emptyList();
        };
    }
}
