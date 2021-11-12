package de.eldoria.schematicsaver.commands.export;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.BuiltInClipboardFormat;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.transform.AffineTransform;
import com.sk89q.worldedit.regions.CuboidRegion;
import de.eldoria.eldoutilities.commands.command.AdvancedCommand;
import de.eldoria.eldoutilities.commands.command.CommandMeta;
import de.eldoria.eldoutilities.commands.command.util.Arguments;
import de.eldoria.eldoutilities.commands.exceptions.CommandException;
import de.eldoria.eldoutilities.commands.executor.IPlayerTabExecutor;
import de.eldoria.eldoutilities.simplecommands.TabCompleteUtil;
import de.eldoria.schematicsaver.config.Configuration;
import de.eldoria.schematicsaver.worldedit.ClipboardTransformBaker;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Level;

public class Export extends AdvancedCommand implements IPlayerTabExecutor {
    private final Configuration configuration;
    private final WorldEdit worldEdit = WorldEdit.getInstance();

    public Export(Plugin plugin, Configuration configuration) {
        super(plugin, CommandMeta.builder("export")
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

        int nextid;
        if (args.size() == 1) {
            nextid = namespace.getNextid();
            configuration.save();
        } else {
            nextid = args.asInt(1);
        }

        try {
            Files.createDirectories(schematics);
        } catch (IOException e) {
            plugin().getLogger().log(Level.SEVERE, "Coult not create schematic directory", e);
            return;
        }

        for (var variant : template.variants()) {
            var relative = variant.relative(player);
            var region = convert(player, relative);

            Clipboard clipboard = new BlockArrayClipboard(region);
            try (var session = worldEdit.newEditSession(BukkitAdapter.adapt(player.getWorld()))) {
                var copy = new ForwardExtentCopy(session, region, clipboard, region.getMinimumPoint());
                Operations.complete(copy);

                var transform = new AffineTransform();
                transform = transform.rotateY(variant.rotation());
                if (variant.flip() != null) {
                    transform = transform.scale(variant.flip().toVector().abs().multiply(-2.0).add(1.0, 1.0, 1.0));
                }
                clipboard = ClipboardTransformBaker.bakeTransform(clipboard, transform);

                var schemFile = schematics.resolve(Path.of(String.format("%s.%s.%s.%s.schem", namespace.name(), nextid, variant.parent().name(), variant.name()))).toFile();
                try (var writer = BuiltInClipboardFormat.SPONGE_SCHEMATIC.getWriter(new FileOutputStream(schemFile))) {
                    writer.write(clipboard);
                }
            } catch (WorldEditException e) {
                plugin().getLogger().log(Level.SEVERE, "Could not save player schematic.", e);
            } catch (FileNotFoundException e) {
                plugin().getLogger().log(Level.SEVERE, "Schematic file not found.", e);
            } catch (IOException e) {
                plugin().getLogger().log(Level.SEVERE, "Could not write player schematic.", e);
            }
        }
        messageSender().sendMessage(player, "Export done.");
    }

    private CuboidRegion convert(Player player, BoundingBox box) {
        var min = BukkitAdapter.adapt(box.getMin().toLocation(player.getWorld())).toVector().toBlockPoint();
        var max = BukkitAdapter.adapt(box.getMax().toLocation(player.getWorld())).toVector().toBlockPoint();

        return new CuboidRegion(min, max);
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) throws CommandException {
        return TabCompleteUtil.complete(args.asString(0), configuration.templateRegistry().namespaceNames());
    }
}
