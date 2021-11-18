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
import de.eldoria.schematicsaver.config.elements.template.Template;
import de.eldoria.schematicsaver.config.elements.template.Type;
import de.eldoria.schematicsaver.util.TextColors;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class TemplateBuilder implements Buildable<Template> {
    private final String name;
    @NotNull
    private Vector origin;
    private final Map<String, TypeBuilder> types = new LinkedHashMap<>();

    public TemplateBuilder(String name, Vector origin) {
        this.name = name;
        this.origin = new Vector(origin.getBlockX(), origin.getBlockY(), origin.getBlockZ());
    }

    public String name() {
        return name;
    }

    public TypeBuilder addType(String name) throws CommandException {
        Verifier.checkName(name);
        CommandAssertions.isFalse(types.containsKey(name.toLowerCase(Locale.ROOT)), "error.typeExists");
        types.computeIfAbsent(name.toLowerCase(Locale.ROOT), k -> new TypeBuilder(this, name));
        return types.get(name.toLowerCase(Locale.ROOT));
    }

    public void removeType(String name) throws CommandException {
        CommandAssertions.isTrue(types.remove(name.toLowerCase(Locale.ROOT)) != null, "error.unkownType");
    }

    public void assertOverlap(BoundingBox box, VariantBuilder variant) throws CommandException {
        for (var value : types.values()) {
            value.assertOverlap(box, variant);
        }
    }

    @Override
    public Template build() {
        var collect = types.values().stream().map(TypeBuilder::build).collect(Collectors.toList());
        return new Template(name, collect);
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
        this.origin = new Vector(origin.getBlockX(), origin.getBlockY(), origin.getBlockZ());
    }

    public List<BoundingBox> getBoundings() {
        return types.values().stream().flatMap(typeBuilder -> typeBuilder.getBoundings().stream()).collect(Collectors.toList());
    }

    public Collection<String> typeNames() {
        return Collections.unmodifiableCollection(types.keySet());
    }

    public MessageComposer asComponent() {
        var text = MessageComposer.create()
                .text("<%s>%s", TextColors.HEADING, name())
                .newLine()
                .text("<%s>Origin: <%s>%s",
                        TextColors.NAME, TextColors.VALUE, origin())
                .space()
                .text("<click:run_command:'/schemtemp renderOrigin'><%s>[Show]</click>", TextColors.CHANGE)
                .space()
                .text("<click:run_command:'/schemtemp modifyTemplate origin'><%s>[Update]</click>", TextColors.CHANGE)
                .newLine()
                .text("<%s>Types: <%s><click:suggest_command:'/schemtemp addType '>[Add]</click>", TextColors.NAME, TextColors.ADD)
                .text(" <click:run_command:'/schemtemp renderTemplateSelections'><%s>[Show Selections]</click>", TextColors.CHANGE)
                .newLine();
        var types = types().stream()
                .map(type -> String.format("  <%s>%s <%s><click:run_command:'/schemtemp showType %s'>[Change]</click> <%s><click:run_command:'/schemtemp removeType %s'>[Remove]</click>",
                        TextColors.NAME, type.name(), TextColors.CHANGE, type.name(), TextColors.REMOVE, type.name()))
                .collect(Collectors.toList());
        text.text(types)
                .newLine()
                .text("<%s><click:run_command:'/schemtemp save'>[Save]</click>", TextColors.ADD);
        return text;
    }
}
