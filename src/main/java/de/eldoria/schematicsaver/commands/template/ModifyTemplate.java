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
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class ModifyTemplate extends AdvancedCommand implements IPlayerTabExecutor {
    private final Sessions sessions;

    public ModifyTemplate(Plugin plugin, Sessions sessions) {
        super(plugin, CommandMeta.builder("modifyTemplate")
                .addUnlocalizedArgument("field", true)
                .addUnlocalizedArgument("value", false)
                .hidden()
                .build());
        this.sessions = sessions;
    }

    @Override
    public void onCommand(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) throws CommandException {
        var session = sessions.getSession(player);
        if ("origin".equalsIgnoreCase(args.asString(0))) {
            session.origin(player.getLocation().toVector());
        }
        sessions.render(player, session);
    }
}
