/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicsaver.config;

import de.eldoria.eldoutilities.configuration.EldoConfig;
import de.eldoria.schematicsaver.config.elements.TemplateRegistry;
import org.bukkit.plugin.Plugin;

public class Configuration extends EldoConfig {
    private static final String TEMPLATE_FILE = "templates";
    private TemplateRegistry templateRegistry;

    public Configuration(Plugin plugin) {
        super(plugin);
    }

    @Override
    protected void reloadConfigs() {
        templateRegistry = loadConfig(TEMPLATE_FILE, null, false).getObject("templates", TemplateRegistry.class, new TemplateRegistry());
    }

    @Override
    protected void saveConfigs() {
        loadConfig(TEMPLATE_FILE, null, false).set("templates", templateRegistry);
    }

    public TemplateRegistry templateRegistry() {
        return templateRegistry;
    }
}
