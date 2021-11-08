/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicsaver.config.elements;

import de.eldoria.schematicsaver.config.elements.template.Template;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public class TemplateRegistry {
    private final Map<String, Template> templates = new HashMap<>();

    public Optional<Template> getTemplate(String name) {
        return Optional.ofNullable(templates.get(name.toLowerCase(Locale.ROOT)));
    }
}
