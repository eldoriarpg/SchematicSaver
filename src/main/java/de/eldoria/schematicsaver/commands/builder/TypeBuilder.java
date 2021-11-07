package de.eldoria.schematicsaver.commands.builder;

import de.eldoria.schematicsaver.config.elements.template.Type;
import de.eldoria.schematicsaver.config.elements.template.Variant;
import org.bukkit.util.BoundingBox;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public class TypeBuilder implements Buildable<Type>, PathComponent {
    private final TemplateBuilder template;
    private String name;
    private Map<String, VariantBuilder> variants = new LinkedHashMap<>();

    public TypeBuilder(TemplateBuilder template, String name) {
        this.template = template;
        this.name = name;
    }

    public VariantBuilder addVariant(String name, BoundingBox box) {
        variants.computeIfAbsent(name.toLowerCase(Locale.ROOT), k -> new VariantBuilder(this, name, box));
        return variants.get(name.toLowerCase(Locale.ROOT));
    }

    public String name() {
        return name;
    }

    public boolean removeVariant(String name) {
        return variants.remove(name.toLowerCase(Locale.ROOT)) != null;
    }

    @Override
    public Type build() {
        return null;
    }

    @Override
    public String path() {
        return name;
    }

    public void addVariant(Variant variant) {
        variants.put(variant.name(), variant.toBuilder(this));
    }

    public Collection<VariantBuilder> variants() {
        return variants.values();
    }
}
