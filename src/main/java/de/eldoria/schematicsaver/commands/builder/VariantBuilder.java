/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicsaver.commands.builder;

import com.sk89q.worldedit.util.Direction;
import de.eldoria.schematicsaver.config.elements.template.Variant;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.Nullable;

public class VariantBuilder implements Buildable<Variant>, PathComponent {
    private final TypeBuilder type;
    private String name;
    private int rotation = 0;
    private Direction flip = null;
    private BoundingBox boundings;

    public VariantBuilder(TypeBuilder type, String name, BoundingBox boundings) {
        this.type = type;
        this.name = name;
        this.boundings = boundings;
    }

    public VariantBuilder(TypeBuilder type, String name, int rotation, Direction flip, BoundingBox boundings) {
        this.type = type;
        this.name = name;
        this.rotation = rotation;
        this.flip = flip;
        this.boundings = boundings;
    }

    public String name() {
        return name;
    }

    public void name(String name) {
        this.name = name;
    }

    public int rotation() {
        return rotation;
    }

    public void rotation(int rotation) {
        this.rotation = rotation;
    }

    @Nullable
    public Direction flip() {
        return flip;
    }

    public void flip(@Nullable Direction flip) {
        this.flip = flip;
    }

    public BoundingBox boundings() {
        return boundings;
    }

    public void boundings(BoundingBox boundings) {
        this.boundings = boundings;
    }

    @Override
    public Variant build() {
        return null;
    }

    public BoundingBox relative() {
        return boundings.clone().shift(type.template().origin().clone().multiply(-1));
    }

    @Override
    public String path() {
        return type.path() + name;
    }

    public TypeBuilder type() {
        return type;
    }
}
