/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicsaver.config.elements.template;

import de.eldoria.eldoutilities.serialization.SerializationUtil;
import de.eldoria.schematicsaver.commands.template.builder.TemplateBuilder;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Template implements ConfigurationSerializable {
    private final String name;
    private final Map<String, Type> types = new LinkedHashMap<>();
    private static final Pattern TYPE_VARIANT = Pattern.compile(".+\\.(?<type>.+?)\\.(?<variant>.+?)\\.schem");


    public Template(Map<String, Object> objectMap) {
        var map = SerializationUtil.mapOf(objectMap);
        name = map.getValue("name");
        List<Type> types = map.getValue("types");
        types.forEach(type -> {
            this.types.put(type.name().toLowerCase(Locale.ROOT), type);
            type.link(this);
        });
    }

    @Override
    @NotNull
    public Map<String, Object> serialize() {
        return SerializationUtil.newBuilder()
                .add("name", name)
                .add("types", new ArrayList<>(types.values()))
                .build();
    }

    public Template(String name, List<Type> types) {
        this.name = name;
        types.forEach(type -> {
            this.types.put(type.name().toLowerCase(Locale.ROOT), type);
            type.link(this);
        });
    }

    public String name() {
        return name;
    }

    public TemplateBuilder toBuilder(Vector vector) {
        var templateBuilder = new TemplateBuilder(name, vector);
        types.values().forEach(templateBuilder::addType);
        return templateBuilder;
    }

    public Collection<String> typeNames() {
        return Collections.unmodifiableCollection(types.keySet());
    }

    public Collection<Variant> variants() {
        return types.values().stream().flatMap(t -> t.variants().stream()).collect(Collectors.toList());
    }

    public List<Pattern> matcher(String namespace) {
        List<Pattern> pattern = new ArrayList<>();
        for (var variant : variants()) {
            pattern.add(Pattern.compile(String.format("%s\\.[0-9]+?\\.%s\\.%s\\.schem", namespace, variant.parent().name(), variant.name())));
        }
        return pattern;
    }

    public List<Pattern> matcher(String namespace, int id) {
        List<Pattern> pattern = new ArrayList<>();
        for (var variant : variants()) {
            pattern.add(Pattern.compile(String.format("%s\\.%s\\.%s\\.%s\\.schem", namespace, id, variant.parent().name(), variant.name())));
        }
        return pattern;
    }

    public Variant findVariant(String name) {
        var matcher = TYPE_VARIANT.matcher(name);
        if (matcher.matches()) {
            var type = types.get(matcher.group("type").toLowerCase(Locale.ROOT));
            if (type == null) {
                return null;
            }
            return type.getVariant(matcher.group("variant"));
        }
        return null;
    }
}
