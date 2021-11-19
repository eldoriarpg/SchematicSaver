/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicsaver.commands.export;

import de.eldoria.eldoutilities.commands.command.AdvancedCommand;
import de.eldoria.eldoutilities.commands.command.CommandMeta;
import de.eldoria.schematicsaver.commands.export.namespace.AddNamespace;
import de.eldoria.schematicsaver.commands.export.namespace.RemoveNamespace;
import de.eldoria.schematicsaver.commands.export.namespace.ShowNamespace;
import de.eldoria.schematicsaver.config.Configuration;
import de.eldoria.schematicsaver.services.BoundingRenderer;
import de.eldoria.schematicsaver.util.Permissions;
import org.bukkit.plugin.Plugin;

public class Namespace extends AdvancedCommand {
    public Namespace(Plugin plugin, BoundingRenderer renderer, Configuration configuration) {
        super(plugin, CommandMeta.builder("namespace")
                .withPermission(Permissions.SchematicExport.NAMESPACE)
                .withSubCommand(new AddNamespace(plugin, configuration))
                .withSubCommand(new RemoveNamespace(plugin, configuration))
                .withSubCommand(new ShowNamespace(plugin, renderer, configuration))
                .build());
    }
}
