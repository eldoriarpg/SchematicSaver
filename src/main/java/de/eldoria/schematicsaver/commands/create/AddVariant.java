/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicsaver.commands.create;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import de.eldoria.eldoutilities.commands.command.AdvancedCommand;
import de.eldoria.eldoutilities.commands.command.CommandMeta;
import de.eldoria.eldoutilities.commands.command.util.Arguments;
import de.eldoria.eldoutilities.commands.exceptions.CommandException;
import de.eldoria.eldoutilities.commands.executor.IPlayerTabExecutor;
import de.eldoria.schematicsaver.commands.util.WorldEditSelection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;

public class AddVariant extends AdvancedCommand implements IPlayerTabExecutor {
    private final Sessions sessions;
    private final WorldEdit worldEdit = WorldEdit.getInstance();

    public AddVariant(Plugin plugin, Sessions sessions) {
        super(plugin, CommandMeta.builder("addVariant")
                .addUnlocalizedArgument("type_name", true)
                .addUnlocalizedArgument("variant_name", true)
                .build());
        this.sessions = sessions;
    }

    @Override
    public void onCommand(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) throws CommandException {
        var session = sessions.getSession(player);
        var type = session.getType(args.asString(0));

        var box = WorldEditSelection.getSelectionBoundings(player);

        session.assertOverlap(box);

        var variant = type.addVariant(args.asString(1), box);
        sessions.render(player, variant);
    }
}
