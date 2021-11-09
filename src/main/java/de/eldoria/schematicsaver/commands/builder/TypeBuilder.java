/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicsaver.commands.builder;

import de.eldoria.eldoutilities.commands.command.util.CommandAssertions;
import de.eldoria.eldoutilities.commands.exceptions.CommandException;
import de.eldoria.schematicsaver.config.elements.template.Type;
import de.eldoria.schematicsaver.config.elements.template.Variant;
import org.bukkit.util.BoundingBox;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class TypeBuilder implements Buildable<Type>, PathComponent {
    private final TemplateBuilder template;
    private String name;
    private Map<String, VariantBuilder> variants = new LinkedHashMap<>();

    public TypeBuilder(TemplateBuilder template, String name) {
        this.template = template;
        this.name = name;
    }

    public VariantBuilder addVariant(String name, BoundingBox box) throws CommandException {
        CommandAssertions.isFalse(variants.containsKey(name.toLowerCase(Locale.ROOT)), "error.typeExists");
        assertEqualSize(box);
        variants.computeIfAbsent(name.toLowerCase(Locale.ROOT), k -> new VariantBuilder(this, name, box));
        return variants.get(name.toLowerCase(Locale.ROOT));
    }

    public void assertOverlap(BoundingBox box) throws CommandException {
        for (var value : variants.values()) {
            CommandAssertions.isFalse(value.boundings().overlaps(box), "error.regionOverlap");
        }
    }

    public void assertEqualSize(BoundingBox box) throws CommandException {
        var volume = box.getVolume();
        for (var value : variants.values()) {
            CommandAssertions.isTrue(value.boundings().getVolume() == volume, "error.regionSizeMissmatch");
            break;
        }
    }

    public String name() {
        return name;
    }

    public void removeVariant(String name) throws CommandException {
       CommandAssertions.isTrue(variants.remove(name.toLowerCase(Locale.ROOT)) != null, "error.unkownVariant");
    }

    @Override
    public Type build() {
        return new Type(name, variants.values().stream().map(VariantBuilder::build).collect(Collectors.toList()));
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

    public TemplateBuilder template() {
        return template;
    }

    public VariantBuilder getVariant(String name) throws CommandException {
        CommandAssertions.isTrue(variants.containsKey(name.toLowerCase(Locale.ROOT)), "error.unkownVariant");
        return variants.get(name.toLowerCase(Locale.ROOT));
    }
}
