/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicsaver.commands.util;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import de.eldoria.eldoutilities.commands.exceptions.CommandException;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;

public class WorldEditSelection {
    private static final WorldEdit WORLD_EDIT = WorldEdit.getInstance();

    public static BoundingBox getSelectionBoundings(Player player) throws CommandException {
        var localSession = WORLD_EDIT.getSessionManager().get(BukkitAdapter.adapt(player));
        Region selection;
        try {
            selection = localSession.getSelection(BukkitAdapter.adapt(player.getWorld()));
        } catch (IncompleteRegionException e) {
            throw CommandException.message("error.incompleteSelection");
        }

        if (!(selection instanceof CuboidRegion region)) {
            throw CommandException.message("error.nonCuboidSelection");
        }

        return BoundingBox.of(
                BukkitAdapter.adapt(player.getWorld(), region.getMinimumPoint()),
                BukkitAdapter.adapt(player.getWorld(), region.getMaximumPoint()));
    }
}
