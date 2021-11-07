package de.eldoria.schematicsaver.commands.builder;

import de.eldoria.schematicsaver.config.elements.template.Template;
import de.eldoria.schematicsaver.config.elements.template.Type;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class TemplateBuilder implements Buildable<Template> {
    private String name;
    private Map<String, TypeBuilder> types = new LinkedHashMap<>();

    public String name() {
        return name;
    }

    public TemplateBuilder(String name) {
        this.name = name;
    }

    public TypeBuilder addType(String name) {
        types.computeIfAbsent(name.toLowerCase(Locale.ROOT), k -> new TypeBuilder(this, name));
        return types.get(name.toLowerCase(Locale.ROOT));
    }

    public boolean removeType(String name) {
        return types.remove(name.toLowerCase(Locale.ROOT)) != null;
    }

    @Override
    public Template build() {
        return new Template();
    }

    public void addType(Type value) {
        types.put(value.name().toLowerCase(Locale.ROOT), value.toBuilder(this));
    }

    public Collection<TypeBuilder> types(){
        return types.values();
    }
}
