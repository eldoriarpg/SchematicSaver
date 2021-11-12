package de.eldoria.schematicsaver.commands.export;

import de.eldoria.eldoutilities.commands.command.AdvancedCommand;
import de.eldoria.eldoutilities.commands.command.CommandMeta;
import de.eldoria.eldoutilities.commands.command.util.Arguments;
import de.eldoria.eldoutilities.commands.exceptions.CommandException;
import de.eldoria.eldoutilities.commands.executor.IPlayerTabExecutor;
import de.eldoria.eldoutilities.simplecommands.TabCompleteUtil;
import de.eldoria.schematicsaver.config.Configuration;
import de.eldoria.schematicsaver.services.BoundingRender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ShowNamespace extends AdvancedCommand implements IPlayerTabExecutor {
    private final BoundingRender boundingRender;
    private final Configuration configuration;

    public ShowNamespace(Plugin plugin, BoundingRender boundingRender, Configuration configuration) {
        super(plugin, CommandMeta.builder("showNamespace")
                .addUnlocalizedArgument("namespace", true)
                .addUnlocalizedArgument("duration", false)
                .build());
        this.boundingRender = boundingRender;
        this.configuration = configuration;
    }

    @Override
    public void onCommand(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) throws CommandException {
        var namespace = configuration.templateRegistry().getNamespace(args.asString(0));
        var template = configuration.templateRegistry().getTemplate(namespace.name());

        for (var variant : template.variants()) {
            boundingRender.renderBox(player, variant.relative(player), args.asInt(1, 10));
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) throws CommandException {
        if (args.size() == 1) {
            return TabCompleteUtil.complete(args.asString(0), configuration.templateRegistry().namespaceNames());
        }
        return TabCompleteUtil.completeInt(args.asString(1), 0, 60, localizer());
    }
}
