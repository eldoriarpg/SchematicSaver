/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicsaver.config.elements.template;

import de.eldoria.eldoutilities.serialization.SerializationUtil;
import de.eldoria.schematicsaver.commands.template.builder.TemplateBuilder;
import de.eldoria.schematicsaver.commands.template.builder.TypeBuilder;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Type implements ConfigurationSerializable, Comparable<Type> {
    private final String name;
    private final Map<String, Variant> variants = new LinkedHashMap<>();
    private Template parent;

    public Type(Map<String, Object> objectMap) {
        var map = SerializationUtil.mapOf(objectMap);
        name = map.getValue("name");
        List<Variant> variants = map.getValue("variants");
        variants.forEach(type -> {
            this.variants.put(type.name().toLowerCase(Locale.ROOT), type);
            type.link(this);
        });
    }

    @Override
    @NotNull
    public Map<String, Object> serialize() {
        return SerializationUtil.newBuilder()
                .add("name", name)
                .add("variants", new ArrayList<>(variants.values()))
                .build();
    }

    void link(Template parent) {
        this.parent = parent;
    }


    public Type(String name, List<Variant> variants) {
        this.name = name;
        variants.forEach(variant -> {
            this.variants.put(variant.name().toLowerCase(Locale.ROOT), variant);
            variant.link(this);
        });
    }

    public String name() {
        return name;
    }

    public TypeBuilder toBuilder(TemplateBuilder template) {
        var typeBuilder = new TypeBuilder(template, name);
        variants.values().forEach(typeBuilder::addVariant);
        return typeBuilder;
    }

    public int getMaxWidth() {
        for (Variant value : variants.values()) {
            return value.getMaxWidth();
        }
        return 0;
    }

    public Collection<String> variantNames() {
        return Collections.unmodifiableCollection(variants.keySet());
    }

    public Collection<Variant> variants() {
        return variants.values();
    }

    public Template parent() {
        return parent;
    }

    public Variant getVariant(String variant) {
        return variants.get(variant.toLowerCase(Locale.ROOT));
    }

    @Override
    public int compareTo(@NotNull Type o) {
        if (getMaxWidth() == o.getMaxWidth()) {
            return name.compareTo(o.name);
        }
        return Integer.compare(getMaxWidth(), o.getMaxWidth());
    }
}
