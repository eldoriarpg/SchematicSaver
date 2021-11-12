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
import de.eldoria.schematicsaver.config.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RemoveTemplate extends AdvancedCommand implements IPlayerTabExecutor {
    private final Configuration configuration;

    public RemoveTemplate(Plugin plugin, Configuration configuration) {
        super(plugin, CommandMeta.builder("removeTemplate")
                .addUnlocalizedArgument("template", true)
                .build());
        this.configuration = configuration;
    }

    @Override
    public void onCommand(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) throws CommandException {
        configuration.templateRegistry().removeTemplate(args.asString(0));
        configuration.save();
        messageSender().sendMessage(player, "Template deleted.");
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) throws CommandException {
        return TabCompleteUtil.complete(args.asString(0), configuration.templateRegistry().templateNames());
    }
}
