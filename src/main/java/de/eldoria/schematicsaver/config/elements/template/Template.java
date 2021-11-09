/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicsaver.config.elements.template;

import de.eldoria.eldoutilities.serialization.SerializationUtil;
import de.eldoria.schematicsaver.commands.builder.TemplateBuilder;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Template implements ConfigurationSerializable {
    private final String name;
    private final Map<String, Type> types;

    public Template(Map<String, Object> objectMap) {
        var map = SerializationUtil.mapOf(objectMap);
        name = map.getValue("name");
        types = map.getMap("types", (key, val) -> key);
    }

    @Override
    @NotNull
    public Map<String, Object> serialize() {
        return SerializationUtil.newBuilder()
                .add("name", name)
                .addMap("types", types, (key, val) -> key)
                .build();
    }


    public Template(String name, List<Type> types) {
        this.name = name;
        this.types = new HashMap<>();
        types.forEach(type -> this.types.put(type.name().toLowerCase(Locale.ROOT), type));
    }

    public String name() {
        return name;
    }

    public TemplateBuilder toBuilder() {
        var templateBuilder = new TemplateBuilder(name);
        types.values().forEach(templateBuilder::addType);
        return templateBuilder;
    }
}
