package de.eldoria.schematicsaver.commands.export;

import de.eldoria.eldoutilities.commands.command.AdvancedCommand;
import de.eldoria.eldoutilities.commands.command.CommandMeta;
import de.eldoria.eldoutilities.commands.command.util.Arguments;
import de.eldoria.eldoutilities.commands.exceptions.CommandException;
import de.eldoria.eldoutilities.commands.executor.IPlayerTabExecutor;
import de.eldoria.eldoutilities.simplecommands.TabCompleteUtil;
import de.eldoria.schematicsaver.config.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class RegisterNamespace extends AdvancedCommand implements IPlayerTabExecutor {
    private final Configuration configuration;

    public RegisterNamespace(Plugin plugin, Configuration configuration) {
        super(plugin, CommandMeta.builder("registerNamespace")
                .addUnlocalizedArgument("namespace", true)
                .addUnlocalizedArgument("template", true)
                .build());
        this.configuration = configuration;
    }

    @Override
    public void onCommand(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) throws CommandException {
        configuration.templateRegistry().addNamespace(args.asString(0), args.asString(1));
        configuration.save();
        messageSender().sendMessage(player, "Template linked. Use /schemexport export <namespace> to export a structure.");
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) throws CommandException {
        if (args.size() == 1) {
            return Collections.singletonList("<namespace>");
        }
        return TabCompleteUtil.complete(args.asString(1), configuration.templateRegistry().templateNames());
    }
}
