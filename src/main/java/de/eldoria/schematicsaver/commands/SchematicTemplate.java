/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicsaver.commands;

import de.eldoria.eldoutilities.commands.command.AdvancedCommand;
import de.eldoria.eldoutilities.commands.command.CommandMeta;
import de.eldoria.messageblocker.blocker.MessageBlocker;
import de.eldoria.schematicsaver.commands.template.AddType;
import de.eldoria.schematicsaver.commands.template.AddVariant;
import de.eldoria.schematicsaver.commands.template.Close;
import de.eldoria.schematicsaver.commands.template.Create;
import de.eldoria.schematicsaver.commands.template.Edit;
import de.eldoria.schematicsaver.commands.template.MessageBlock;
import de.eldoria.schematicsaver.commands.template.ModifyTemplate;
import de.eldoria.schematicsaver.commands.template.ModifyVariant;
import de.eldoria.schematicsaver.commands.template.RemoveTemplate;
import de.eldoria.schematicsaver.commands.template.RemoveType;
import de.eldoria.schematicsaver.commands.template.RemoveVariant;
import de.eldoria.schematicsaver.commands.template.RenderCurrentSelection;
import de.eldoria.schematicsaver.commands.template.RenderOrigin;
import de.eldoria.schematicsaver.commands.template.RenderTemplateSelections;
import de.eldoria.schematicsaver.commands.template.RenderTypeSelections;
import de.eldoria.schematicsaver.commands.template.RenderVariantSelection;
import de.eldoria.schematicsaver.commands.template.Save;
import de.eldoria.schematicsaver.commands.template.SelectVariantRegion;
import de.eldoria.schematicsaver.commands.template.Sessions;
import de.eldoria.schematicsaver.commands.template.Show;
import de.eldoria.schematicsaver.commands.template.ShowType;
import de.eldoria.schematicsaver.commands.template.ShowVariant;
import de.eldoria.schematicsaver.config.Configuration;
import de.eldoria.schematicsaver.services.BoundingRenderer;
import de.eldoria.schematicsaver.util.Permissions;
import org.bukkit.plugin.Plugin;

public class SchematicTemplate extends AdvancedCommand {
    public SchematicTemplate(Plugin plugin, MessageBlocker messageBlocker, BoundingRenderer boundingRenderer, Configuration configuration) {
        super(plugin, CommandMeta.builder("schematictemplate")
                .buildSubCommands((cmds, builder) -> {
                    var sessions = new Sessions(plugin, messageBlocker);
                    cmds.add(new AddType(plugin, sessions));
                    cmds.add(new AddVariant(plugin, sessions));
                    cmds.add(new Close(plugin, sessions));
                    cmds.add(new Create(plugin, sessions, configuration));
                    cmds.add(new Edit(plugin, sessions, configuration));
                    cmds.add(new MessageBlock(plugin, messageBlocker));
                    cmds.add(new ModifyTemplate(plugin, sessions));
                    cmds.add(new ModifyVariant(plugin, sessions));
                    cmds.add(new RemoveTemplate(plugin, configuration));
                    cmds.add(new RemoveType(plugin, sessions));
                    cmds.add(new RemoveVariant(plugin, sessions));
                    cmds.add(new RenderCurrentSelection(plugin, boundingRenderer));
                    cmds.add(new RenderOrigin(plugin, sessions, boundingRenderer));
                    cmds.add(new RenderTemplateSelections(plugin, sessions, boundingRenderer));
                    cmds.add(new RenderTypeSelections(plugin, sessions, boundingRenderer));
                    cmds.add(new RenderVariantSelection(plugin, sessions, boundingRenderer));
                    cmds.add(new Save(plugin, sessions, configuration));
                    cmds.add(new SelectVariantRegion(plugin, sessions));
                    cmds.add(new Show(plugin, sessions));
                    cmds.add(new ShowType(plugin, sessions));
                    cmds.add(new ShowVariant(plugin, sessions));
                })
                .withPermission(Permissions.SchematicTemplate.CREATE, Permissions.SchematicTemplate.EDIT)
                .build());
    }
}
