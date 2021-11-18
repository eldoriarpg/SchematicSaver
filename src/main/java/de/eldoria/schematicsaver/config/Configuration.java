/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicsaver.config;

import de.eldoria.eldoutilities.configuration.EldoConfig;
import de.eldoria.schematicsaver.config.elements.PasteSettings;
import de.eldoria.schematicsaver.config.elements.RenderSettings;
import de.eldoria.schematicsaver.config.elements.TemplateRegistry;
import org.bukkit.plugin.Plugin;

public class Configuration extends EldoConfig {
    private static final String TEMPLATE_FILE = "templates";
    private TemplateRegistry templateRegistry;
    private PasteSettings pasteSettings;
    private RenderSettings renderSettings;

    public Configuration(Plugin plugin) {
        super(plugin);
    }

    @Override
    protected void reloadConfigs() {
        pasteSettings = getConfig().getObject("pasteSettings", PasteSettings.class, new PasteSettings());
        renderSettings = getConfig().getObject("pasteSettings", RenderSettings.class, new RenderSettings());
        templateRegistry = loadConfig(TEMPLATE_FILE, null, false).getObject("templates", TemplateRegistry.class, new TemplateRegistry());
    }

    @Override
    protected void saveConfigs() {
        getConfig().set("pasteSettings", pasteSettings);
        getConfig().set("renderSettings", renderSettings);
        loadConfig(TEMPLATE_FILE, null, false).set("templates", templateRegistry);
    }

    public TemplateRegistry templateRegistry() {
        return templateRegistry;
    }

    public PasteSettings pasteSettings() {
        return pasteSettings;
    }

    public RenderSettings renderSettings() {
        return renderSettings;
    }
}
