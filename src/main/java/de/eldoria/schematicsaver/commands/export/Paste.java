/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicsaver.commands.export;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.util.Direction;
import de.eldoria.eldoutilities.commands.command.AdvancedCommand;
import de.eldoria.eldoutilities.commands.command.CommandMeta;
import de.eldoria.eldoutilities.commands.command.util.Arguments;
import de.eldoria.eldoutilities.commands.command.util.Input;
import de.eldoria.eldoutilities.commands.exceptions.CommandException;
import de.eldoria.eldoutilities.commands.executor.IPlayerTabExecutor;
import de.eldoria.eldoutilities.simplecommands.TabCompleteUtil;
import de.eldoria.schematicsaver.config.Configuration;
import de.eldoria.schematicsaver.config.elements.template.Template;
import de.eldoria.schematicsaver.config.elements.template.Type;
import de.eldoria.schematicsaver.config.elements.template.Variant;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class Paste extends AdvancedCommand implements IPlayerTabExecutor {
    private final Configuration configuration;

    public Paste(Plugin plugin, Configuration configuration) {
        super(plugin, CommandMeta.builder("paste")
                .addUnlocalizedArgument("namespace", true)
                .addUnlocalizedArgument("id", false)
                .build());
        this.configuration = configuration;
    }

    @Override
    public void onCommand(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) throws CommandException {
        var namespace = configuration.templateRegistry().getNamespace(args.asString(0));
        var template = configuration.templateRegistry().getTemplate(namespace.template());

        var schematics = plugin().getDataFolder().toPath().resolve(Path.of("schematics"));

        var name = namespace.name() + (args.size() > 1 ? "." + args.asInt(1) : "");

        List<Path> collect;
        try (var paths = Files.walk(schematics)) {
            collect = paths
                    // coarse filtering
                    .filter(path -> path.getFileName().toString().startsWith(name))
                    // fine filtering
                    .collect(Collectors.toList());
        } catch (IOException e) {
            plugin().getLogger().log(Level.SEVERE, "Could not filter schematics.", e);
            messageSender().sendMessage(player, "Something went wrong while filtering.");
            return;
        }

        if (collect.isEmpty()) {
            messageSender().sendError(player, "No matching schematics found.");
            return;
        }
        var actor = BukkitAdapter.adapt(player);

        var groupType = args.flags()
                .getIfPresent("g")
                .or(() -> args.flags().getIfPresent("grouping"))
                .map(Input::asString)
                .orElse("types");

        var optRowOffset = args.flags().getIfPresent("r");
        var rowOffset = optRowOffset.isPresent() ? optRowOffset.get().asInt() : 1;

        var optColOffset = args.flags().getIfPresent("c");
        var colOffset = optColOffset.isPresent() ? optColOffset.get().asInt() : 1;

        var optMultiOffset = args.flags().getIfPresent("m");
        var multiOffset = optMultiOffset.isPresent() ? optMultiOffset.get().asInt() - rowOffset : 2;


        var localSession = WorldEdit.getInstance().getSessionManager().get(actor);
        var session = WorldEdit.getInstance().newEditSessionBuilder().world(actor.getWorld()).actor(actor).build();
        try (session) {
            switch (groupType.toLowerCase(Locale.ROOT)) {
                case "types" -> pasteTypesGrouped(collect, template, player, session, colOffset, rowOffset);
                case "id" -> pasteTypesAndIdsGrouped(collect, template, player, session, colOffset, rowOffset, multiOffset);
                default -> throw CommandException.message("Invalid paste grouping. Use types or id");
            }
        } catch (WorldEditException e) {
            messageSender().sendError(player, e.getMessage());
        }
        localSession.remember(session);
        messageSender().sendMessage(player, "Pasted " + collect.size() + " schematics.");
    }

    private void pasteTypesGrouped(List<Path> collect, Template template, Player player, EditSession session, int colOffset, int rowOffset) throws WorldEditException {
        Map<Type, Map<Variant, List<Path>>> types = new TreeMap<>();

        for (var path : collect) {
            var variant = template.findVariant(path.getFileName().toString());
            if (variant == null) continue;
            types.computeIfAbsent(variant.parent(), key -> new TreeMap<>()).computeIfAbsent(variant, key -> new ArrayList<>()).add(path);
        }

        var actor = BukkitAdapter.adapt(player);

        var rowDirection = Direction.findClosest(actor.getCardinalDirection().toVector(), Direction.Flag.CARDINAL);
        var columnDirection = rotateClockwise(rowDirection);


        var xOrigin = actor.getLocation().toVector().toBlockPoint();
        for (var typeEntry : types.entrySet()) {
            var maxWidth = typeEntry.getKey().getMaxWidth();
            var currLocation = xOrigin;
            currLocation = currLocation.add(rowDirection.toBlockVector().normalize().multiply(maxWidth + 1 + rowOffset));
            for (var value : typeEntry.getValue().values()) {
                for (var path : value) {
                    paste(path, currLocation, columnDirection, session);
                    currLocation = currLocation.add(rowDirection.toBlockVector().normalize().multiply(maxWidth + 1 + rowOffset));
                }
            }
            xOrigin = xOrigin.add(columnDirection.toBlockVector().normalize().multiply(maxWidth + 1 + colOffset));
        }
    }

    private void paste(Path path, BlockVector3 position, Direction columnDirection, EditSession session) throws WorldEditException {
        Clipboard clipboard;
        try {
            clipboard = loadSchematic(path);
        } catch (IOException e) {
            plugin().getLogger().log(Level.SEVERE, "Could not load schematic", e);
            return;
        }
        clipboard.setOrigin(getOrigin(clipboard, columnDirection));
        var clipboardHolder = new ClipboardHolder(clipboard);
        var paste = clipboardHolder.createPaste(session)
                .to(position)
                .build();
        Operations.complete(paste);
    }

    private void pasteTypesAndIdsGrouped(List<Path> collect, Template template, Player player, EditSession session, int colOffset, int rowOffset, int multiOffset) throws WorldEditException {
        Map<Integer, Map<Type, Map<Variant, List<Path>>>> ids = new TreeMap<>();

        for (var path : collect) {
            var id = Integer.parseInt(path.getFileName().toString().split("\\.")[1]);
            var variant = template.findVariant(path.getFileName().toString());
            if (variant == null) continue;
            ids.computeIfAbsent(id, key -> new TreeMap<>())
                    .computeIfAbsent(variant.parent(), key -> new TreeMap<>())
                    .computeIfAbsent(variant, key -> new ArrayList<>())
                    .add(path);
        }

        var actor = BukkitAdapter.adapt(player);

        var rowDirection = Direction.findClosest(actor.getCardinalDirection().toVector(), Direction.Flag.CARDINAL);
        var columnDirection = rotateClockwise(rowDirection);


        var origin = actor.getLocation().toVector().toBlockPoint();
        var rowOrigin = actor.getLocation().toVector().toBlockPoint();
        for (var types : ids.values()) {
            for (var typeEntry : types.entrySet()) {
                var maxWidth = typeEntry.getKey().getMaxWidth();
                var currLocation = origin;
                currLocation = currLocation.add(rowDirection.toBlockVector().normalize().multiply(maxWidth + 1 + rowOffset));
                for (var value : typeEntry.getValue().values()) {
                    for (var path : value) {
                        paste(path, currLocation, columnDirection, session);
                        currLocation = currLocation.add(rowDirection.toBlockVector().normalize().multiply(maxWidth + 1 + rowOffset));
                    }
                    var prevLoc = currLocation.subtract(rowDirection.toBlockVector().normalize().multiply(maxWidth + 1 + rowOffset));
                    rowOrigin = switch (rowDirection) {
                        case NORTH -> rowOrigin.withZ(Math.min(rowOrigin.getBlockZ(), prevLoc.getBlockZ()));
                        case EAST -> rowOrigin.withX(Math.max(rowOrigin.getBlockX(), prevLoc.getBlockX()));
                        case SOUTH -> rowOrigin.withZ(Math.max(rowOrigin.getBlockZ(), prevLoc.getBlockZ()));
                        case WEST -> rowOrigin.withX(Math.min(rowOrigin.getBlockX(), prevLoc.getBlockX()));
                        default -> rowOrigin;
                    };
                }
                origin = origin.add(columnDirection.toBlockVector().normalize().multiply(maxWidth + 1 + colOffset));
            }

            // row offset
            rowOrigin = switch (rowDirection) {
                case NORTH -> rowOrigin.withZ(rowOrigin.getBlockZ() - multiOffset);
                case EAST -> rowOrigin.withX(rowOrigin.getBlockX() + multiOffset);
                case SOUTH -> rowOrigin.withZ(rowOrigin.getBlockZ() + multiOffset);
                case WEST -> rowOrigin.withX(rowOrigin.getBlockX() - multiOffset);
                default -> rowOrigin;
            };
            origin = rowOrigin;
        }
    }

    private BlockVector3 getOrigin(Clipboard clipboard, Direction pasteDirection) {
        var min = clipboard.getMinimumPoint();
        var max = clipboard.getMaximumPoint();
        return switch (pasteDirection) {
            case NORTH -> min.withZ(max.getBlockZ());
            case EAST -> min;
            case SOUTH -> min.withX(max.getBlockX());
            case WEST -> max.withY(min.getBlockY());
            default -> min;
        };
    }

    private Direction rotateClockwise(Direction direction) {
        return switch (direction) {
            case NORTH -> Direction.EAST;
            case EAST -> Direction.SOUTH;
            case SOUTH -> Direction.WEST;
            case WEST -> Direction.NORTH;
            default -> Direction.NORTH;
        };
    }

    private Clipboard loadSchematic(Path path) throws IOException {
        var format = ClipboardFormats.findByFile(path.toFile());
        if (format == null) {
            throw new IOException("Unkown schematic format");
        }
        try (var reader = format.getReader(new FileInputStream(path.toFile()))) {
            return reader.read();
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) throws CommandException {
        return switch (args.size()) {
            case 1 -> TabCompleteUtil.complete(args.asString(0), configuration.templateRegistry().namespaceNames());
            case 2 -> Collections.singletonList("<id>");
            default -> Collections.emptyList();
        };
    }
}
