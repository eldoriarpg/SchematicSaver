/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicsaver.config.elements.template;

import com.sk89q.worldedit.util.Direction;
import de.eldoria.schematicsaver.commands.builder.TypeBuilder;
import de.eldoria.schematicsaver.commands.builder.VariantBuilder;
import org.bukkit.util.BoundingBox;

public class Variant {
    String name;
    int rotation;
    Direction flip;
    BoundingBox boundings;

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
        return new VariantBuilder(typeBuilder, name, rotation, flip, boundings);
    }
}
