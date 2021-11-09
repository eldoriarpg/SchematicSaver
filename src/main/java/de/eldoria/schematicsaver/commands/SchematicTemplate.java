/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicsaver.commands;

import de.eldoria.eldoutilities.commands.command.AdvancedCommand;
import de.eldoria.eldoutilities.commands.command.CommandMeta;
import de.eldoria.messageblocker.blocker.MessageBlocker;
import de.eldoria.schematicsaver.commands.create.AddType;
import de.eldoria.schematicsaver.commands.create.AddVariant;
import de.eldoria.schematicsaver.commands.create.Create;
import de.eldoria.schematicsaver.commands.create.Edit;
import de.eldoria.schematicsaver.commands.create.MessageBlock;
import de.eldoria.schematicsaver.commands.create.ModifyVariant;
import de.eldoria.schematicsaver.commands.create.RemoveType;
import de.eldoria.schematicsaver.commands.create.RemoveVariant;
import de.eldoria.schematicsaver.commands.create.Save;
import de.eldoria.schematicsaver.commands.create.Sessions;
import de.eldoria.schematicsaver.commands.create.Show;
import de.eldoria.schematicsaver.commands.create.ShowType;
import de.eldoria.schematicsaver.commands.create.ShowVariant;
import de.eldoria.schematicsaver.config.Configuration;
import org.bukkit.plugin.Plugin;

public class SchematicTemplate extends AdvancedCommand {
    public SchematicTemplate(Plugin plugin, MessageBlocker messageBlocker, Configuration configuration) {
        super(plugin, CommandMeta.builder("schematicSaver")
                .buildSubCommands((cmds, builder) -> {
                    var sessions = new Sessions(plugin, messageBlocker, configuration);
                    cmds.add(new AddType(plugin, sessions));
                    cmds.add(new AddVariant(plugin, sessions));
                    cmds.add(new Create(plugin, sessions, configuration));
                    cmds.add(new Edit(plugin, sessions, configuration));
                    cmds.add(new MessageBlock(plugin, messageBlocker));
                    cmds.add(new ModifyVariant(plugin, sessions));
                    cmds.add(new RemoveType(plugin, sessions));
                    cmds.add(new RemoveVariant(plugin, sessions));
                    cmds.add(new Save(plugin, sessions, configuration));
                    cmds.add(new Show(plugin, sessions));
                    cmds.add(new ShowType(plugin, sessions));
                    cmds.add(new ShowVariant(plugin, sessions));
                })
                .build());
    }
}
