package de.eldoria.schematicsaver.commands.create;

import de.eldoria.eldoutilities.commands.command.AdvancedCommand;
import de.eldoria.eldoutilities.commands.command.util.Arguments;
import de.eldoria.eldoutilities.commands.exceptions.CommandException;
import de.eldoria.eldoutilities.commands.executor.IPlayerTabExecutor;
import de.eldoria.schematicsaver.services.BoundingRender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class ShowSelections extends AdvancedCommand implements IPlayerTabExecutor {
    private final Sessions sessions;
    private final BoundingRender render;

    public ShowSelections(Plugin plugin, Sessions sessions, BoundingRender render) {
        super(plugin);
        this.sessions = sessions;
        this.render = render;
    }

    @Override
    public void onCommand(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) throws CommandException {
        var session = sessions.getSession(player);
        var boundings = session.getBoundings();
        for (var bounding : boundings) {
            render.renderBox(player, bounding);
        }
    }
}
