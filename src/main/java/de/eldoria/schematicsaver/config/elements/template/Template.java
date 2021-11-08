/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicsaver.config.elements.template;

import de.eldoria.schematicsaver.commands.builder.TemplateBuilder;

import java.util.Map;

public class Template {
    private String name;
    private Map<String, Type> types;

    public String name() {
        return name;
    }

    public TemplateBuilder toBuilder() {
        var templateBuilder = new TemplateBuilder(name);
        types.values().forEach(templateBuilder::addType);
        return templateBuilder;
    }
}
