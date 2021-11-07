package de.eldoria.schematicsaver.commands;

import de.eldoria.eldoutilities.commands.command.AdvancedCommand;
import de.eldoria.eldoutilities.commands.command.CommandMeta;
import org.bukkit.plugin.Plugin;

public class SchematicSaver extends AdvancedCommand {
    public SchematicSaver(Plugin plugin) {
        super(plugin, CommandMeta.builder("schematicSaver")
                .build());
    }
}
