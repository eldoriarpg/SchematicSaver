/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicsaver.config.elements;

import de.eldoria.eldoutilities.commands.command.util.CommandAssertions;
import de.eldoria.eldoutilities.commands.exceptions.CommandException;
import de.eldoria.eldoutilities.serialization.SerializationUtil;
import de.eldoria.schematicsaver.config.elements.template.Template;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class TemplateRegistry implements ConfigurationSerializable {
    private final Map<String, Template> templates;

    public TemplateRegistry(Map<String, Object> objectMap) {
        var map = SerializationUtil.mapOf(objectMap);
        templates = map.getMap("templates", (key, val) -> key);
    }

    public TemplateRegistry() {
        templates = new HashMap<>();
    }

    @Override
    @NotNull
    public Map<String, Object> serialize() {
        return SerializationUtil.newBuilder()
                .addMap("templates", templates, (key, val) -> key)
                .build();
    }


    public Template getTemplate(String name) throws CommandException {
        CommandAssertions.isTrue(templates.containsKey(name.toLowerCase(Locale.ROOT)), "error.unkownTemplate");
        return templates.get(name.toLowerCase(Locale.ROOT));
    }
    public boolean templateExists(String name) throws CommandException {
        return templates.containsKey(name.toLowerCase(Locale.ROOT));
    }

    public void addTemplate(Template template) {
        templates.put(template.name().toLowerCase(Locale.ROOT), template);
    }

    public Collection<String> templateNames() {
        return Collections.unmodifiableCollection(templates.keySet());
    }

    public void removeTemplate(String name) throws CommandException {
        getTemplate(name);
        templates.remove(name.toLowerCase(Locale.ROOT));
    }
}
