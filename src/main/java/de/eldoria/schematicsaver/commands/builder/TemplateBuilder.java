/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicsaver.commands.builder;

import de.eldoria.eldoutilities.commands.command.util.CommandAssertions;
import de.eldoria.eldoutilities.commands.exceptions.CommandException;
import de.eldoria.schematicsaver.config.elements.template.Template;
import de.eldoria.schematicsaver.config.elements.template.Type;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class TemplateBuilder implements Buildable<Template> {
    private String name;
    private Vector origin;
    private Map<String, TypeBuilder> types = new LinkedHashMap<>();

    public String name() {
        return name;
    }

    public TemplateBuilder(String name) {
        this.name = name;
    }

    public TypeBuilder addType(String name) throws CommandException {
        CommandAssertions.isFalse(types.containsKey(name.toLowerCase(Locale.ROOT)), "error.typeExists");
        types.computeIfAbsent(name.toLowerCase(Locale.ROOT), k -> new TypeBuilder(this, name));
        return types.get(name.toLowerCase(Locale.ROOT));
    }

    public boolean removeType(String name) {
        return types.remove(name.toLowerCase(Locale.ROOT)) != null;
    }

    public void assertOverlap(BoundingBox box) throws CommandException {
        for (var value : types.values()) {
            value.assertOverlap(box);
        }
    }

    @Override
    public Template build() {
        return new Template();
    }

    public void addType(Type value) {
        types.put(value.name().toLowerCase(Locale.ROOT), value.toBuilder(this));
    }

    public Collection<TypeBuilder> types() {
        return types.values();
    }

    public TypeBuilder getType(String name) throws CommandException {
        CommandAssertions.isTrue(types.containsKey(name.toLowerCase(Locale.ROOT)), "error.unkownType");
        return types.get(name.toLowerCase(Locale.ROOT));
    }

    public Vector origin() {
        return origin;
    }

    public void origin(Vector origin) {
        this.origin = origin;
    }
}
