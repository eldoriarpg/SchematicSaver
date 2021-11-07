package de.eldoria.schematicsaver.config.elements.template;

import de.eldoria.schematicsaver.commands.builder.TemplateBuilder;
import de.eldoria.schematicsaver.commands.builder.TypeBuilder;

import java.util.Map;

public class Type {
    String name;
    Map<String, Variant> variants;

    public String name() {
        return name;
    }

    public TypeBuilder toBuilder(TemplateBuilder template) {
        var typeBuilder = new TypeBuilder(template, name);
        variants.values().forEach(typeBuilder::addVariant);
        return typeBuilder;
    }
}
