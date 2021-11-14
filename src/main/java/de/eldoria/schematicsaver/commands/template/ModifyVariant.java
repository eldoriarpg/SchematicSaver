/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicsaver.commands.template;

import com.sk89q.worldedit.util.Direction;
import de.eldoria.eldoutilities.commands.command.AdvancedCommand;
import de.eldoria.eldoutilities.commands.command.CommandMeta;
import de.eldoria.eldoutilities.commands.command.util.Arguments;
import de.eldoria.eldoutilities.commands.exceptions.CommandException;
import de.eldoria.eldoutilities.commands.executor.IPlayerTabExecutor;
import de.eldoria.eldoutilities.simplecommands.TabCompleteUtil;
import de.eldoria.schematicsaver.commands.util.WorldEditSelection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
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
                session.assertOverlap(box, variant);
                type.assertEqualSize(box);
                variant.boundings(box);
            }
            case "flip" -> variant.flip(args.asEnum(3, Direction.class));
            case "rotation" -> variant.rotation(args.asInt(3));
        }
        sessions.render(player, variant);
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) throws CommandException {
        return switch (args.size()) {
            case 1 -> TabCompleteUtil.complete(args.asString(0), sessions.getSession(player).typeNames());
            case 2 -> TabCompleteUtil.complete(args.asString(1), sessions.getSession(player).getType(args.asString(0)).variantNames());
            case 3 -> TabCompleteUtil.complete(args.asString(2), "selection", "rotation", "flip");
            case 4 -> switch (args.asString(2).toLowerCase(Locale.ROOT)) {
                case "rotation" -> TabCompleteUtil.complete(args.asString(3), "0", "90", "180", "270");
                case "flip" -> TabCompleteUtil.complete(args.asString(3), Direction.class);
                default -> Collections.emptyList();
            };
            default -> Collections.emptyList();
        };
    }
}
