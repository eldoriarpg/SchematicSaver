/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicsaver.commands.create;

import de.eldoria.eldoutilities.commands.command.AdvancedCommand;
import de.eldoria.eldoutilities.commands.command.CommandMeta;
import de.eldoria.eldoutilities.commands.command.util.Arguments;
import de.eldoria.eldoutilities.commands.command.util.CommandAssertions;
import de.eldoria.eldoutilities.commands.exceptions.CommandException;
import de.eldoria.eldoutilities.commands.executor.IPlayerTabExecutor;
import de.eldoria.schematicsaver.config.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class Edit extends AdvancedCommand implements IPlayerTabExecutor {
    private final Sessions sessions;
    private final Configuration configuration;

    public Edit(Plugin plugin, Sessions sessions, Configuration configuration) {
        super(plugin, CommandMeta.builder("edit")
                .addUnlocalizedArgument("name", true)
                .build());
        this.sessions = sessions;
        this.configuration = configuration;
    }

    @Override
    public void onCommand(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) throws CommandException {
        var template = configuration.templateRegistry().getTemplate(args.asString(0));
        CommandAssertions.isTrue(template.isPresent(), "error.unkownTemplate");
        var edit = sessions.edit(player, template.get());
        sessions.render(player, edit);
    }
}
