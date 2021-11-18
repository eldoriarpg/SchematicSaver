/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicsaver.config.elements;

import de.eldoria.eldoutilities.serialization.SerializationUtil;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class PasteSettings implements ConfigurationSerializable {
    private int multiOffset;
    private int columnOffset;
    private int rowOffset;
    private PasteGrouping pasteGrouping;

    public PasteSettings(Map<String, Object> objectMap) {
        var map = SerializationUtil.mapOf(objectMap);
        multiOffset = map.getValueOrDefault("multiOffset", 3);
        columnOffset = map.getValueOrDefault("columnOffset", 1);
        rowOffset = map.getValueOrDefault("rowOffset", 1);
        pasteGrouping = map.getValueOrDefault("pasteGrouping", PasteGrouping.TYPES, PasteGrouping.class);
    }

    public PasteSettings() {
        multiOffset = 3;
        columnOffset = 1;
        rowOffset = 1;
        pasteGrouping = PasteGrouping.TYPES;
    }

    @Override
    @NotNull
    public Map<String, Object> serialize() {
        return SerializationUtil.newBuilder()
                .add("multiOffset", multiOffset)
                .add("columnOffset", columnOffset)
                .add("rowOffset", rowOffset)
                .add("pasteGrouping", pasteGrouping)
                .build();
    }

    public int multiOffset() {
        return multiOffset;
    }

    public int columnOffset() {
        return columnOffset;
    }

    public int rowOffset() {
        return rowOffset;
    }

    public PasteGrouping pasteGrouping() {
        return pasteGrouping;
    }

    public void multiOffset(int multiOffset) {
        this.multiOffset = multiOffset;
    }

    public void columnOffset(int columnOffset) {
        this.columnOffset = columnOffset;
    }

    public void rowOffset(int rowOffset) {
        this.rowOffset = rowOffset;
    }

    public void pasteGrouping(PasteGrouping pasteGrouping) {
        this.pasteGrouping = pasteGrouping;
    }

    public enum PasteGrouping {
        ID,
        TYPES
    }
}
