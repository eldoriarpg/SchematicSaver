package de.eldoria.schematicsaver.commands.builder;

import de.eldoria.schematicsaver.config.elements.template.Variant;
import de.eldoria.schematicsaver.util.Vectors;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

public class VariantBuilder implements Buildable<Variant>, PathComponent {
    private final TypeBuilder type;
    private String name;
    private int rotation = 0;
    private Vector flip = Vectors.ZERO;
    private BoundingBox boundings;

    public VariantBuilder(TypeBuilder type, String name, BoundingBox boundings) {
        this.type = type;
        this.name = name;
        this.boundings = boundings;
    }

    public VariantBuilder(TypeBuilder type, String name, int rotation, Vector flip, BoundingBox boundings) {
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

    public Vector flip() {
        return flip;
    }

    public void flip(Vector flip) {
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

    @Override
    public String path() {
        return type.path() + name;
    }
}
