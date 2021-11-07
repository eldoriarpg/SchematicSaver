package de.eldoria.schematicsaver.config.elements.template;

import de.eldoria.schematicsaver.commands.builder.TypeBuilder;
import de.eldoria.schematicsaver.commands.builder.VariantBuilder;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

public class Variant {
    String name;
    int rotation;
    Vector flip;
    BoundingBox boundings;

    public String name() {
        return name;
    }

    public int rotation() {
        return rotation;
    }

    public Vector flip() {
        return flip;
    }

    public BoundingBox boundings() {
        return boundings;
    }

    public VariantBuilder toBuilder(TypeBuilder typeBuilder) {
        return new VariantBuilder(typeBuilder, name, rotation, flip, boundings);
    }
}
