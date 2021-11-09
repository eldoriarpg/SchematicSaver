/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicsaver.config.elements.template;

import de.eldoria.eldoutilities.serialization.SerializationUtil;
import de.eldoria.schematicsaver.commands.builder.TemplateBuilder;
import de.eldoria.schematicsaver.commands.builder.TypeBuilder;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Type implements ConfigurationSerializable {
    private final String name;
    private final Map<String, Variant> variants;

    public Type(Map<String, Object> objectMap) {
        var map = SerializationUtil.mapOf(objectMap);
        name = map.getValue("name");
        variants = map.getMap("variants", (key, val) -> key);
    }

    @Override
    @NotNull
    public Map<String, Object> serialize() {
        return SerializationUtil.newBuilder()
                .add("name", name)
                .addMap("variants", variants, (key, val) -> key)
                .build();
    }


    public Type(String name, List<Variant> variants) {
        this.name = name;
        this.variants = new HashMap<>();
        variants.forEach(variant -> this.variants.put(variant.name().toLowerCase(Locale.ROOT), variant));
    }

    public String name() {
        return name;
    }

    public TypeBuilder toBuilder(TemplateBuilder template) {
        var typeBuilder = new TypeBuilder(template, name);
        variants.values().forEach(typeBuilder::addVariant);
        return typeBuilder;
    }
}
