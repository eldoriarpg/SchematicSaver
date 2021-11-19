/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicsaver.commands.util;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extension.platform.permission.ActorSelectorLimits;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import de.eldoria.eldoutilities.commands.exceptions.CommandException;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;

public final class WorldEditSelection {
    private static final WorldEdit WORLD_EDIT = WorldEdit.getInstance();

    private WorldEditSelection() {
        throw new UnsupportedOperationException("This is a utility class.");
    }

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

    public static void setSelectionBoundings(Player player, BoundingBox box) throws CommandException {
        var localSession = WORLD_EDIT.getSessionManager().get(BukkitAdapter.adapt(player));
        var actor = BukkitAdapter.adapt(player);
        var world = actor.getWorld();
        var selector = localSession.getRegionSelector(world);
        selector.selectPrimary(BukkitAdapter.adapt(box.getMin().toLocation(player.getWorld())).toVector().toBlockPoint(), ActorSelectorLimits.forActor(actor));
        selector.selectSecondary(BukkitAdapter.adapt(box.getMax().toLocation(player.getWorld())).toVector().toBlockPoint(), ActorSelectorLimits.forActor(actor));
    }
}
