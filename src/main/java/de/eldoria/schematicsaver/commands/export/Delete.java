/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicsaver.commands.export;

import de.eldoria.eldoutilities.commands.command.AdvancedCommand;
import de.eldoria.eldoutilities.commands.command.CommandMeta;
import de.eldoria.eldoutilities.commands.command.util.Arguments;
import de.eldoria.eldoutilities.commands.command.util.CommandAssertions;
import de.eldoria.eldoutilities.commands.exceptions.CommandException;
import de.eldoria.eldoutilities.commands.executor.IPlayerTabExecutor;
import de.eldoria.eldoutilities.simplecommands.TabCompleteUtil;
import de.eldoria.schematicsaver.config.Configuration;
import de.eldoria.schematicsaver.util.Permissions;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

public class Delete extends AdvancedCommand implements IPlayerTabExecutor {
    private final Configuration configuration;

    public Delete(Plugin plugin, Configuration configuration) {
        super(plugin, CommandMeta.builder("delete")
                .addUnlocalizedArgument("namespace", true)
                .addUnlocalizedArgument("id", false)
                .addUnlocalizedArgument("type", false)
                .addUnlocalizedArgument("variant", false)
                .withPermission(Permissions.SchematicExport.DELETE)
                .build());
        this.configuration = configuration;
    }

    @Override
    public void onCommand(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) throws CommandException {
        List<String> names = new ArrayList<>();
        var namespace = configuration.templateRegistry().getNamespace(args.asString(0));
        var template = configuration.templateRegistry().getTemplate(namespace.template());

        names.add(namespace.name());

        if (args.hasArg(3)) {
            names.add(String.valueOf(args.asInt(1)));
            names.add(template.getType(args.asString(2)).name());
            names.add(template.getType(args.asString(2)).getVariant(args.asString(3)).name());
            CommandAssertions.permission(player, false, Permissions.SchematicExport.Delete.VARIANT, Permissions.SchematicExport.Delete.ALL);
        } else if (args.hasArg(2)) {
            names.add(String.valueOf(args.asInt(1)));
            names.add(template.getType(args.asString(2)).name());
            CommandAssertions.permission(player, false, Permissions.SchematicExport.Delete.TYPE, Permissions.SchematicExport.Delete.ALL);
        } else if (args.hasArg(1)) {
            names.add(String.valueOf(args.asInt(1)));
            CommandAssertions.permission(player, false, Permissions.SchematicExport.Delete.ID, Permissions.SchematicExport.Delete.ALL);
        } else {
            CommandAssertions.permission(player, false, Permissions.SchematicExport.Delete.NAMESPACE, Permissions.SchematicExport.Delete.ALL);
        }

        List<Path> schematics;
        try {
            schematics = Schematics.getSchematics(plugin(), String.join(".", names));
        } catch (IOException e) {
            plugin().getLogger().log(Level.SEVERE, "Could not read schematics", e);
            messageSender().sendError(player, "Could not read schematics");
            return;
        }

        for (var schematic : schematics) {
            try {
                Files.delete(schematic);
            } catch (IOException e) {
                plugin().getLogger().log(Level.SEVERE, "Could not delete " + schematic.toString() + ".", e);
                messageSender().sendError(player, "Could not delete " + schematic.toString() + ".");
            }
        }

        messageSender().sendMessage(player, "Deleted " + schematics.size() + " schematics.");
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) throws CommandException {
        if (args.sizeIs(1)) {
            return TabCompleteUtil.complete(args.asString(0), configuration.templateRegistry().namespaceNames());
        }

        if (args.sizeIs(2)) {
            return TabCompleteUtil.completeMinInt(args.asString(1), 0);
        }

        var namespace = configuration.templateRegistry().getNamespace(args.asString(0));
        var template = configuration.templateRegistry().getTemplate(namespace.template());

        if (args.sizeIs(3)) {
            return TabCompleteUtil.complete(args.asString(2), template.typeNames());
        }

        if (args.sizeIs(4)) {
            return TabCompleteUtil.complete(args.asString(3), template.getType(args.asString(2)).variantNames());
        }
        return Collections.emptyList();
    }
}
