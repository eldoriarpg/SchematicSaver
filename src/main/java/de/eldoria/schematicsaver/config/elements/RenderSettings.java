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

public class RenderSettings implements ConfigurationSerializable {

    private int displayTime = 10;
    private int maxDisplayTime = 60;
    private int maxRegionVolume = 125000;
    private int refreshRate = 5;

    public RenderSettings(Map<String, Object> objectMap) {
        var map = SerializationUtil.mapOf(objectMap);
        displayTime = map.getValueOrDefault("displayTime", displayTime);
        maxDisplayTime = map.getValueOrDefault("maxDisplayTime", maxDisplayTime);
        maxRegionVolume = map.getValueOrDefault("maxRegionVolume", maxRegionVolume);
        refreshRate = map.getValueOrDefault("refreshRate", refreshRate);
    }

    public RenderSettings() {
    }

    public int displayTime() {
        return displayTime;
    }

    public void displayTime(int displayTime) {
        this.displayTime = displayTime;
    }

    public int maxDisplayTime() {
        return maxDisplayTime;
    }

    public void maxDisplayTime(int maxDisplayTime) {
        this.maxDisplayTime = maxDisplayTime;
    }

    public int maxRegionVolume() {
        return maxRegionVolume;
    }

    public void maxRegionVolume(int maxRegionVolume) {
        this.maxRegionVolume = maxRegionVolume;
    }

    public int refreshRate() {
        return refreshRate;
    }

    public void refreshRate(int refreshRate) {
        this.refreshRate = refreshRate;
    }

    @Override
    @NotNull
    public Map<String, Object> serialize() {
        return SerializationUtil.newBuilder()
                .add("displayTime", displayTime)
                .add("maxDisplayTime", maxDisplayTime)
                .add("maxRegionVolume", maxRegionVolume)
                .add("refreshRate", refreshRate)
                .build();
    }

}
