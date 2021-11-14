/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicsaver.commands.export;

import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public final class Schematics {
    private Schematics() {
        throw new UnsupportedOperationException("This is a utility class.");
    }

    public static List<Path> getSchematics(Plugin plugin, String name) throws IOException {
        var schematics = plugin.getDataFolder().toPath().resolve(Path.of("schematics"));

        Files.createDirectories(schematics);

        List<Path> collect;
        try (var paths = Files.walk(schematics)) {
            collect = paths
                    .filter(path -> path.getFileName().toString().startsWith(name))
                    .collect(Collectors.toList());
        }
        return collect;
    }
}
