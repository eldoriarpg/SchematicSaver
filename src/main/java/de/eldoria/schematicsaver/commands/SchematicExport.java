/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicsaver.commands;

import de.eldoria.eldoutilities.commands.command.AdvancedCommand;
import de.eldoria.eldoutilities.commands.command.CommandMeta;
import de.eldoria.schematicsaver.commands.export.Export;
import de.eldoria.schematicsaver.commands.export.Paste;
import de.eldoria.schematicsaver.commands.export.RegisterNamespace;
import de.eldoria.schematicsaver.commands.export.ShowNamespace;
import de.eldoria.schematicsaver.commands.export.ShowTemplate;
import de.eldoria.schematicsaver.config.Configuration;
import de.eldoria.schematicsaver.services.BoundingRender;
import org.bukkit.plugin.Plugin;

public class SchematicExport extends AdvancedCommand {
    public SchematicExport(Plugin plugin, Configuration configuration, BoundingRender render) {
        super(plugin, CommandMeta.builder("schematicExport")
                .withSubCommand(new Export(plugin, configuration))
                .withSubCommand(new Paste(plugin, configuration))
                .withSubCommand(new RegisterNamespace(plugin, configuration))
                .withSubCommand(new ShowNamespace(plugin, render, configuration))
                .withSubCommand(new ShowTemplate(plugin, render, configuration))
                .build());
    }
}
