/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicsaver.commands.create;

import com.sk89q.worldedit.util.Direction;
import de.eldoria.eldoutilities.commands.command.AdvancedCommand;
import de.eldoria.eldoutilities.commands.command.CommandMeta;
import de.eldoria.eldoutilities.commands.command.util.Arguments;
import de.eldoria.eldoutilities.commands.exceptions.CommandException;
import de.eldoria.eldoutilities.commands.executor.IPlayerTabExecutor;
import de.eldoria.schematicsaver.commands.util.WorldEditSelection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class ModifyVariant extends AdvancedCommand implements IPlayerTabExecutor {
    private final Sessions sessions;

    public ModifyVariant(Plugin plugin, Sessions sessions) {
        super(plugin, CommandMeta.builder("modifyVariant")
                .addUnlocalizedArgument("type_name", true)
                .addUnlocalizedArgument("variant_name", true)
                .addUnlocalizedArgument("field", true)
                .addUnlocalizedArgument("value", false)
                .build());
        this.sessions = sessions;
    }

    @Override
    public void onCommand(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) throws CommandException {
        var session = sessions.getSession(player);
        var type = session.getType(args.asString(0));
        var variant = type.getVariant(args.asString(1));


        switch (args.asString(2).toLowerCase(Locale.ROOT)) {
            case "selection" -> {
                var box = WorldEditSelection.getSelectionBoundings(player);
                session.assertOverlap(box);
                type.assertEqualSize(box);
                variant.boundings(box);
            }
            case "direction" -> variant.flip(args.asEnum(3, Direction.class));
            case "rotation" -> variant.rotation(args.asInt(3));
        }
        sessions.render(player, variant);
    }
}
