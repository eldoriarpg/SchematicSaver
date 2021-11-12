/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicsaver.config.elements.template;

import com.sk89q.worldedit.util.Direction;
import de.eldoria.eldoutilities.serialization.SerializationUtil;
import de.eldoria.schematicsaver.commands.template.builder.TypeBuilder;
import de.eldoria.schematicsaver.commands.template.builder.VariantBuilder;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class Variant implements ConfigurationSerializable, Comparable<Variant> {
    private final String name;
    private final int rotation;
    private final Direction flip;
    private final BoundingBox boundings;
    private Type parent;

    public Variant(Map<String, Object> objectMap) {
        var map = SerializationUtil.mapOf(objectMap);
        name = map.getValue("name");
        rotation = map.getValue("rotation");
        flip = map.getValueOrDefault("flip", (Direction) null, Direction.class);
        boundings = map.getValue("boundings");
    }

    @Override
    @NotNull
    public Map<String, Object> serialize() {
        return SerializationUtil.newBuilder()
                .add("name", name)
                .add("rotation", rotation)
                .add("flip", flip == null ? null : flip.name())
                .add("boundings", boundings)
                .build();
    }

    void link(Type parent) {
        this.parent = parent;
    }

    public Variant(String name, int rotation, Direction flip, BoundingBox boundings) {
        this.name = name;
        this.rotation = rotation;
        this.flip = flip;
        this.boundings = boundings;
    }

    public String name() {
        return name;
    }

    public int rotation() {
        return rotation;
    }

    public Direction flip() {
        return flip;
    }

    public BoundingBox boundings() {
        return boundings;
    }

    public VariantBuilder toBuilder(TypeBuilder typeBuilder) {
        return new VariantBuilder(typeBuilder, name, rotation, flip, boundings.clone().shift(typeBuilder.template().origin()));
    }

    public BoundingBox relative(Player player) {
        var origin = player.getLocation();
        return boundings.clone().shift(new Vector(origin.getBlockX(), origin.getBlockY(), origin.getBlockZ()));
    }

    public int getMaxWidth() {
        return (int) Math.round(Math.max(boundings.getWidthZ(), boundings.getWidthX()));
    }

    public Type parent() {
        return parent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Variant)) return false;

        var variant = (Variant) o;

        return name.equals(variant.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public int compareTo(@NotNull Variant o) {
        return name.compareTo(o.name);
    }
}
