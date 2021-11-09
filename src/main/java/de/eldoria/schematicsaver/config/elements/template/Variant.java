/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicsaver.config.elements.template;

import com.sk89q.worldedit.util.Direction;
import de.eldoria.eldoutilities.serialization.SerializationUtil;
import de.eldoria.schematicsaver.commands.builder.TypeBuilder;
import de.eldoria.schematicsaver.commands.builder.VariantBuilder;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class Variant implements ConfigurationSerializable {
    private final String name;
    private final int rotation;
    private final Direction flip;
    private final BoundingBox boundings;

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
}
