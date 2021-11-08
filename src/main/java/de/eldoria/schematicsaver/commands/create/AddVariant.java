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

        var localSession = worldEdit.getSessionManager().get(BukkitAdapter.adapt(player));
        Region selection;
        try {
            selection = localSession.getSelection(BukkitAdapter.adapt(player.getWorld()));
        } catch (IncompleteRegionException e) {
            throw CommandException.message("error.incompleteSelection");
        }

        if (!(selection instanceof CuboidRegion region)) {
            throw CommandException.message("error.nonCuboidSelection");
        }

        var box = BoundingBox.of(
                BukkitAdapter.adapt(player.getWorld(), region.getMinimumPoint()),
                BukkitAdapter.adapt(player.getWorld(), region.getMaximumPoint()));

        session.assertOverlap(box);

        var typeBuilder = type.addVariant(args.asString(1), box);
        sessions.render(player, typeBuilder);

    }
}
