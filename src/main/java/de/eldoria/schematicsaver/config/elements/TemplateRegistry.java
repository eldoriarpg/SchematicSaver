/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicsaver.config.elements;

import de.eldoria.eldoutilities.commands.command.util.CommandAssertions;
import de.eldoria.eldoutilities.commands.exceptions.CommandException;
import de.eldoria.eldoutilities.serialization.SerializationUtil;
import de.eldoria.schematicsaver.config.elements.template.Namespace;
import de.eldoria.schematicsaver.config.elements.template.Template;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TemplateRegistry implements ConfigurationSerializable {
    private final Map<String, Template> templates = new HashMap<>();
    private final Map<String, Namespace> namespaces = new HashMap<>();

    public TemplateRegistry(Map<String, Object> objectMap) {
        var map = SerializationUtil.mapOf(objectMap);
        List<Template> templates = map.getValue("templates");
        templates.forEach(template -> {
            this.templates.put(template.name().toLowerCase(Locale.ROOT), template);
        });
        List<Namespace> namespaces = map.getValue("namespaces");
        namespaces.forEach(namespace -> {
            this.namespaces.put(namespace.name().toLowerCase(Locale.ROOT), namespace);
        });
    }

    public TemplateRegistry() {
    }
    @Override
    @NotNull
    public Map<String, Object> serialize() {
        return SerializationUtil.newBuilder()
                .add("templates",new ArrayList<>(templates.values()))
                .add("namespaces", new ArrayList<>(namespaces.values()))
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

    public Collection<String> namespaceNames() {
        return Collections.unmodifiableSet(namespaces.keySet());
    }

    public void addNamespace(String name, String template) throws CommandException {
        if(namespaces.containsKey(name)){
            throw CommandException.message("error.namespaceUsed");
        }
        namespaces.put(name.toLowerCase(Locale.ROOT), new Namespace(name, getTemplate(template).name()));
    }

    public Namespace getNamespace(String name) throws CommandException {
        CommandAssertions.isTrue(namespaces.containsKey(name.toLowerCase(Locale.ROOT)), "error.unkownNamespace");
        return namespaces.get(name.toLowerCase(Locale.ROOT));
    }

    public boolean isUsed(Template template){
        return namespaces.values().stream().anyMatch(namespace -> namespace.template().equalsIgnoreCase(template.name()));
    }

    public void removeTemplate(String name) throws CommandException {
        getTemplate(name);
        templates.remove(name.toLowerCase(Locale.ROOT));
    }

    public boolean removeNamespace(String name) {
        return namespaces.remove(name.toLowerCase(Locale.ROOT)) != null;
    }
}
