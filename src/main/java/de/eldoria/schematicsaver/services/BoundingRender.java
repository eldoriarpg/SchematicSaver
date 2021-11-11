package de.eldoria.schematicsaver.services;

import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
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

public class BoundingRender implements Runnable {

    private final Plugin plugin;
    private final Queue<Color> colors = new ArrayDeque<>();
    private final Map<UUID, List<RenderTask>> tasks = new HashMap<>();

    public BoundingRender(Plugin plugin) {
        this.plugin = plugin;
        buildColors();
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

    private void highlightBoundings(Player player, BoundingBox box) {
        var max = box.getMax();
        var min = box.getMin();

        // upper corners
        var u1 = getVector(min, max, min);
        var u2 = getVector(min, max, max);
        var u3 = getVector(max, max, max);
        var u4 = getVector(max, max, min);

        // lower corners
        var l1 = getVector(min, min, min);
        var l2 = getVector(min, min, max);
        var l3 = getVector(max, min, max);
        var l4 = getVector(max, min, min);

        var color = getNextColor();

        addCircle(player, color, u1, u2, u3, u4);
        addCircle(player, color, l1, l2, l3, l4);

        addTask(player, l1, u1, color);
        addTask(player, l2, u2, color);
        addTask(player, l3, u3, color);
        addTask(player, l4, u4, color);

        // cross implementation
        addTask(player, u1, u3, color);
        addTask(player, u2, u4, color);

        addTask(player, l1, l3, color);
        addTask(player, l2, l4, color);

        addTask(player, u1, l4, color);
        addTask(player, u4, l1, color);

        addTask(player, u1, l2, color);
        addTask(player, u2, l1, color);

        addTask(player, u2, l3, color);
        addTask(player, u3, l2, color);

        addTask(player, u3, l4, color);
        addTask(player, u4, l3, color);
    }

    private void addCircle(Player player, Color color, Vector vec1, Vector vec2, Vector vec3, Vector vec4) {
        addTask(player, vec1, vec2, color);
        addTask(player, vec2, vec3, color);
        addTask(player, vec3, vec4, color);
        addTask(player, vec4, vec1, color);
    }

    private void addTask(Player player, Vector origin, Vector direction, Color color) {
        tasks.computeIfAbsent(player.getUniqueId(), key -> new ArrayList<>()).add(RenderTask.of(origin, direction, color, 40));
    }

    private Vector getVector(Vector x, Vector y, Vector z) {
        return new Vector(x.getBlockX(), y.getBlockY(), z.getBlockZ())
                .add(new Vector(0.5, 0.5, 0.5));
    }

    private Color getNextColor() {
        var color = colors.remove();
        colors.add(color);
        return color;
    }

    public void renderBox(Player player, BoundingBox box) {
        highlightBoundings(player, box);
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
        private final Vector origin;
        private final int steps;
        private final Vector step;
        private final Color color;
        private int duration;

        private RenderTask(Vector origin, int duration, int steps, Vector step, Color color) {
            this.origin = origin;
            this.duration = duration;
            this.steps = steps;
            this.step = step;
            this.color = color;
        }

        private static RenderTask of(Vector origin, Vector target, Color color, int duration) {
            var direction = target.clone().subtract(origin);
            var steps = (int) (direction.length() / 0.5);
            var step = direction.normalize().multiply(0.5);
            return new RenderTask(origin, duration, steps, step, color);
        }

        public boolean draw(Player player) {
            duration--;
            var loc = origin.clone();
            for (var i = 0; i < steps; i++) {
                player.spawnParticle(Particle.REDSTONE, loc.getX(), loc.getY(), loc.getZ(), 0, new Particle.DustOptions(color, 1));
                loc.add(step);
            }
            return duration <= 0;
        }
    }
}
