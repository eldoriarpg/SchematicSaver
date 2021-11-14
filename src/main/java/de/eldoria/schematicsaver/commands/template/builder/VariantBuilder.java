/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicsaver.commands.template.builder;

import com.sk89q.worldedit.util.Direction;
import de.eldoria.eldoutilities.localization.MessageComposer;
import de.eldoria.schematicsaver.config.elements.template.Variant;
import de.eldoria.schematicsaver.util.TextColors;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.Nullable;

public class VariantBuilder implements Buildable<Variant>, PathComponent {
    private final TypeBuilder type;
    private String name;
    private int rotation = 0;
    private Direction flip = null;
    private BoundingBox boundings;

    public VariantBuilder(TypeBuilder type, String name, BoundingBox boundings) {
        this.type = type;
        this.name = name;
        this.boundings = boundings;
    }

    public VariantBuilder(TypeBuilder type, String name, int rotation, Direction flip, BoundingBox boundings) {
        this.type = type;
        this.name = name;
        this.rotation = rotation;
        this.flip = flip;
        this.boundings = boundings;
    }

    public String name() {
        return name;
    }

    public void name(String name) {
        this.name = name;
    }

    public int rotation() {
        return rotation;
    }

    public void rotation(int rotation) {
        this.rotation = rotation;
    }

    @Nullable
    public Direction flip() {
        return flip;
    }

    public void flip(@Nullable Direction flip) {
        this.flip = flip;
    }

    public BoundingBox boundings() {
        return boundings;
    }

    public void boundings(BoundingBox boundings) {
        this.boundings = boundings;
    }

    @Override
    public Variant build() {
        return new Variant(name, rotation, flip, relative());
    }

    public BoundingBox relative() {
        return boundings.clone().shift(type.template().origin().clone().multiply(-1));
    }

    @Override
    public String path() {
        return type.path() + " " + name;
    }

    public TypeBuilder type() {
        return type;
    }

    public MessageComposer asComponent() {
        return MessageComposer.create()
                .text("<%s>%s", TextColors.HEADING, name())
                .newLine()
                .text("<%s>Boundings", TextColors.NAME)
                .text(" <click:run_command:'/schemtemp renderVariantSelection %s'><%s>[Show]</click>", path(), TextColors.CHANGE)
                .text(" <click:run_command:'/schemtemp selectVariantRegion %s'><%s>[Select]</click>", path(), TextColors.CHANGE)
                .text(" <click:run_command:'/schemtemp modifyVariant %s selection'><%s>[Update]</click>", path(), TextColors.CHANGE)
                .newLine()
                .text("<%s>Rotation:<%s> %s <%s><click:suggest_command:'/schemtemp modifyVariant %s rotation '>[Change]</click>",
                        TextColors.NAME, TextColors.VALUE, rotation(), TextColors.CHANGE, path())
                .newLine()
                .text("<%s>Flip:<%s> %s <%s><click:suggest_command:'/schemtemp modifyVariant %s flip '>[Change]</click>",
                        TextColors.NAME, TextColors.VALUE, flipToString(flip()), TextColors.CHANGE, path())
                .newLine()
                .text("<%s><click:run_command:'/schemtemp showType %s'>[Back]</click>", TextColors.CHANGE, type().name());

    }

    private String flipToString(Direction direction) {
        if (direction != null) {
            return direction.name();
        }
        return "none";
    }
}
