/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicsaver.services;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.command.tool.SelectionWand;
import de.eldoria.eldoutilities.commands.exceptions.CommandException;
import de.eldoria.schematicsaver.commands.template.Sessions;
import de.eldoria.schematicsaver.commands.util.WorldEditSelection;
import de.eldoria.schematicsaver.config.Configuration;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;

public class BoundingRenderer implements Runnable, Listener {
    private static final double MIN_OFFSET = -0.05;
    private static final double MAX_OFFSET = 1.05;
    private final Plugin plugin;
    private final Configuration configuration;
    private final Sessions sessions;
    private final Queue<Color> colors = new ArrayDeque<>();
    private final Map<UUID, List<RenderTask>> tasks = new HashMap<>();
    private final Map<UUID, BoundingBox> lastRender = new HashMap<>();
    private int refreshRate;

    private BoundingRenderer(Plugin plugin, Configuration configuration, Sessions sessions) {
        this.plugin = plugin;
        this.configuration = configuration;
        this.sessions = sessions;
    }

    public static BoundingRenderer create(Plugin plugin, Configuration configuration, Sessions sessions) {
        var render = new BoundingRenderer(plugin, configuration, sessions);
        render.buildColors();
        render.schedule(configuration.renderSettings().refreshRate());
        plugin.getServer().getPluginManager().registerEvents(render, plugin);
        return render;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInteract(PlayerInteractEvent event) {
        var player = event.getPlayer();
        if (!sessions.hasSession(player)) return;
        if (event.getAction() != Action.LEFT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        var session = WorldEdit.getInstance().getSessionManager().get(BukkitAdapter.adapt(player));
        var type = player.getInventory().getItemInMainHand().getType();
        if (!(session.getTool(BukkitAdapter.asItemType(type)) instanceof SelectionWand)) {
            return;
        }

        BoundingBox box;
        try {
            box = WorldEditSelection.getSelectionBoundings(player);
        } catch (CommandException e) {
            // discard
            return;
        }

        clearPlayer(player);
        renderBox(player, box);
    }

    private void schedule(int refreshRate) {
        this.refreshRate = refreshRate;
        plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, this, 5, refreshRate);
    }

    private void buildColors() {
        var startColor = 105;
        var step = 50;
        for (var val = startColor; val < 255; val += step) {
            colors.add(Color.fromBGR(val, 255, 0));
            colors.add(Color.fromBGR(255, val, 0));
            colors.add(Color.fromBGR(255, 0, val));
            colors.add(Color.fromBGR(val, 0, 255));
            colors.add(Color.fromBGR(0, val, 255));
            colors.add(Color.fromBGR(0, 255, val));
        }
    }

    private void highlightBoundings(Player player, BoundingBox box, int seconds) {
        var max = box.getMax();
        var min = box.getMin();

        // upper corners
        var u1 = getVector(min, max, min).add(new Vector(MIN_OFFSET, MAX_OFFSET, MIN_OFFSET));
        var u2 = getVector(min, max, max).add(new Vector(MIN_OFFSET, MAX_OFFSET, MAX_OFFSET));
        var u3 = getVector(max, max, max).add(new Vector(MAX_OFFSET, MAX_OFFSET, MAX_OFFSET));
        var u4 = getVector(max, max, min).add(new Vector(MAX_OFFSET, MAX_OFFSET, MIN_OFFSET));

        // lower corners
        var l1 = getVector(min, min, min).add(new Vector(MIN_OFFSET, MIN_OFFSET, MIN_OFFSET));
        var l2 = getVector(min, min, max).add(new Vector(MIN_OFFSET, MIN_OFFSET, MAX_OFFSET));
        var l3 = getVector(max, min, max).add(new Vector(MAX_OFFSET, MIN_OFFSET, MAX_OFFSET));
        var l4 = getVector(max, min, min).add(new Vector(MAX_OFFSET, MIN_OFFSET, MIN_OFFSET));

        var task = new RenderTask(seconds * (20 / refreshRate), getNextColor());

        task.addCircle(u1, u2, u3, u4);
        task.addCircle(l1, l2, l3, l4);

        task.addTask(l1, u1);
        task.addTask(l2, u2);
        task.addTask(l3, u3);
        task.addTask(l4, u4);

        // cross implementation
        task.addCross(u1, u3, u2, u4);
        task.addCross(l1, l3, l2, l4);
        task.addCross(u1, l4, u4, l1);
        task.addCross(u1, l2, u2, l1);
        task.addCross(u2, l3, u3, l2);
        task.addCross(u3, l4, u4, l3);

        tasks.computeIfAbsent(player.getUniqueId(), k -> new ArrayList<>()).add(task);
    }

    private Vector getVector(Vector x, Vector y, Vector z) {
        return new Vector(x.getBlockX(), y.getBlockY(), z.getBlockZ());
    }

    private Color getNextColor() {
        var color = colors.remove();
        colors.add(color);
        return color;
    }

    public void renderBox(Player player, BoundingBox box) {
        renderBox(player, box, configuration.renderSettings().displayTime());
    }

    public void renderBox(Player player, BoundingBox box, int seconds) {
        if (box.getVolume() > configuration.renderSettings().maxRegionVolume()) return;
        if (seconds == -1) {
            seconds = configuration.renderSettings().displayTime();
        }
        highlightBoundings(player, box, Math.min(configuration.renderSettings().maxDisplayTime(), seconds));
    }

    public void clearPlayer(Player player) {
        tasks.remove(player.getUniqueId());
    }

    @Override
    public void run() {
        var iterator = tasks.entrySet().iterator();
        while (iterator.hasNext()) {
            var entry = iterator.next();
            var player = plugin.getServer().getPlayer(entry.getKey());
            if (player == null || entry.getValue().isEmpty()) {
                iterator.remove();
                continue;
            }

            entry.getValue().removeIf(next -> next.draw(player));
        }
    }

    private static class RenderTask {
        private final Color color;
        private final List<RenderSubTask> subTasks = new ArrayList<>();
        private int duration;

        private RenderTask(int duration, Color color) {
            this.duration = duration;
            this.color = color;
        }

        public boolean draw(Player player) {
            duration--;
            for (var subTask : subTasks) {
                subTask.draw(player, color);
            }
            return duration <= 0;
        }

        public void addTask(Vector origin, Vector direction) {
            subTasks.add(RenderSubTask.of(origin, direction));
        }

        private void addCircle(Vector vec1, Vector vec2, Vector vec3, Vector vec4) {
            addTask(vec1, vec2);
            addTask(vec2, vec3);
            addTask(vec3, vec4);
            addTask(vec4, vec1);
        }

        private void addCross(Vector vec1, Vector vec2, Vector vec3, Vector vec4) {
            addTask(vec1, vec2);
            addTask(vec4, vec3);
        }
    }

    private static class RenderSubTask {
        private final Vector origin;
        private final int steps;
        private final Vector step;

        private RenderSubTask(Vector origin, int steps, Vector step) {
            this.origin = origin;
            this.steps = steps;
            this.step = step;
        }

        private static RenderSubTask of(Vector origin, Vector target) {
            var direction = target.clone().subtract(origin);
            var steps = (int) (direction.length() / 0.5);
            var step = direction.normalize().multiply(0.5);
            return new RenderSubTask(origin, steps, step);
        }

        public void draw(Player player, Color color) {
            var loc = origin.clone();
            for (var i = 0; i < steps; i++) {
                player.spawnParticle(Particle.REDSTONE, loc.getX(), loc.getY(), loc.getZ(), 0, new Particle.DustOptions(color, 1));
                loc.add(step);
            }
        }
    }
}
