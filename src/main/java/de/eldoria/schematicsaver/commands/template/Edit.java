/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicsaver.commands.template;

import de.eldoria.eldoutilities.commands.command.AdvancedCommand;
import de.eldoria.eldoutilities.commands.command.CommandMeta;
import de.eldoria.eldoutilities.commands.command.util.Arguments;
import de.eldoria.eldoutilities.commands.command.util.CommandAssertions;
import de.eldoria.eldoutilities.commands.exceptions.CommandException;
import de.eldoria.eldoutilities.commands.executor.IPlayerTabExecutor;
import de.eldoria.eldoutilities.simplecommands.TabCompleteUtil;
import de.eldoria.schematicsaver.config.Configuration;
import de.eldoria.schematicsaver.util.Permissions;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class Edit extends AdvancedCommand implements IPlayerTabExecutor {
    private final Sessions sessions;
    private final Configuration configuration;

    public Edit(Plugin plugin, Sessions sessions, Configuration configuration) {
        super(plugin, CommandMeta.builder("edit")
                .addUnlocalizedArgument("name", true)
                .withPermission(Permissions.SchematicTemplate.EDIT)
                .build());
        this.sessions = sessions;
        this.configuration = configuration;
    }

    @Override
    public void onCommand(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) throws CommandException {
        var template = configuration.templateRegistry().getTemplate(args.asString(0));

        if (configuration.templateRegistry().isUsed(template) && !args.flags().has("f")) {
            throw CommandException.message("error.usedTeamplate");
        }

        if (args.flags().has("f")) {
            CommandAssertions.permission(player, false, Permissions.SchematicTemplate.Edit.OVERRIDE);
        }

        var edit = sessions.edit(player, template);
        sessions.render(player, edit);
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) throws CommandException {
        if (args.sizeIs(1)) {
            return TabCompleteUtil.complete(args.asString(0), configuration.templateRegistry().templateNames());
        }
        return Collections.emptyList();
    }
}
