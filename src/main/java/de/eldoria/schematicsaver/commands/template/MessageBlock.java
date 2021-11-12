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
import de.eldoria.messageblocker.blocker.MessageBlocker;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class MessageBlock extends AdvancedCommand implements IPlayerTabExecutor {
    private final MessageBlocker messageBlocker;

    public MessageBlock(Plugin plugin, MessageBlocker messageBlocker) {
        super(plugin, CommandMeta.builder("messageBlock")
                .addUnlocalizedArgument("state", true)
                .hidden()
                .build());
        this.messageBlocker = messageBlocker;
    }

    @Override
    public void onCommand(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) throws CommandException {
        if (args.asBoolean(0)) {
            messageBlocker.blockPlayer(player);
        } else {
            messageBlocker.unblockPlayer(player);
        }
    }
}
