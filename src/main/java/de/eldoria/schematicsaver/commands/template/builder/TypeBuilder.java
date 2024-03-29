/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicsaver.commands.template.builder;

import de.eldoria.eldoutilities.commands.command.util.CommandAssertions;
import de.eldoria.eldoutilities.commands.exceptions.CommandException;
import de.eldoria.eldoutilities.localization.MessageComposer;
import de.eldoria.schematicsaver.commands.util.Verifier;
import de.eldoria.schematicsaver.config.elements.template.Type;
import de.eldoria.schematicsaver.config.elements.template.Variant;
import de.eldoria.schematicsaver.util.TextColors;
import org.bukkit.util.BoundingBox;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class TypeBuilder implements Buildable<Type>, PathComponent {
    private final TemplateBuilder template;
    private final String name;
    private final Map<String, VariantBuilder> variants = new LinkedHashMap<>();

    public TypeBuilder(TemplateBuilder template, String name) {
        this.template = template;
        this.name = name;
    }

    public VariantBuilder addVariant(String name, BoundingBox box) throws CommandException {
        Verifier.checkName(name);
        CommandAssertions.isFalse(variants.containsKey(name.toLowerCase(Locale.ROOT)), "error.typeExists");
        assertEqualSize(box);
        variants.computeIfAbsent(name.toLowerCase(Locale.ROOT), k -> new VariantBuilder(this, name, box));
        return variants.get(name.toLowerCase(Locale.ROOT));
    }

    public void assertOverlap(BoundingBox box, VariantBuilder variant) throws CommandException {
        for (var value : variants.values()) {
            if (value == variant) continue;
            CommandAssertions.isFalse(value.boundings().overlaps(box), "error.regionOverlap");
        }
    }

    public void assertEqualSize(BoundingBox box) throws CommandException {
        var widthX = box.getWidthX();
        var widthZ = box.getWidthZ();
        for (var value : variants.values()) {
            CommandAssertions.isTrue(value.boundings().getWidthX() == widthX && value.boundings().getWidthZ() == widthZ
                                     || value.boundings().getWidthX() == widthZ && value.boundings().getWidthZ() == widthX, "error.regionSizeMissmatch");
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

    public List<BoundingBox> getBoundings() {
        return variants.values().stream().map(VariantBuilder::boundings).collect(Collectors.toList());
    }

    public Collection<String> variantNames() {
        return Collections.unmodifiableCollection(variants.keySet());
    }

    public MessageComposer asComponent() {
        var text = MessageComposer.create()
                .text("<%s>%s", TextColors.HEADING, name())
                .newLine()
                .text("<%s>Variants: <%s><click:suggest_command:'/schemtemp addVariant %s '>[Add]</click>", TextColors.NAME, TextColors.ADD, name())
                .text(" <click:run_command:'/schemtemp renderTypeSelections %s'><%s>[Show Selections]</click>", name(), TextColors.CHANGE)
                .newLine();
        var types = variants().stream()
                .map(variant -> String.format("  <%s>%s <%s><click:run_command:'/schemtemp showVariant %s %s'>[Edit]</click> <%s><click:run_command:'/schemtemp removeVariant %s %s'>[Remove]</click>",
                        TextColors.NAME, variant.name(), TextColors.CHANGE, name(), variant.name(), TextColors.REMOVE, name(), variant.name()))
                .collect(Collectors.toList());
        text.text(types)
                .newLine()
                .text("<click:run_command:'/schemtemp show'><%s>[Back]</click>", TextColors.CHANGE);
        return text;
    }
}
