package de.eldoria.schematicsaver.config.elements.template;

import de.eldoria.eldoutilities.serialization.SerializationUtil;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class Namespace implements ConfigurationSerializable {

    private final String name;
    private final String template;
    private int nextId;

    public Namespace(String name, String template) {
        this.name = name;
        this.template = template;
        nextId = 0;
    }

    public Namespace(Map<String, Object> objectMap) {
        var map = SerializationUtil.mapOf(objectMap);
        name = map.getValue("name");
        template = map.getValue("template");
        nextId = map.getValue("nextId");
    }

    @Override
    @NotNull
    public Map<String, Object> serialize() {
        return SerializationUtil.newBuilder()
                .add("name", name)
                .add("template", template)
                .add("nextId", nextId)
                .build();
    }

    public int getNextid() {
        return nextId++;
    }

    public String name() {
        return name;
    }

    public String template() {
        return template;
    }
}
