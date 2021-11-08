/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicsaver.config;

import de.eldoria.schematicsaver.config.elements.TemplateRegistry;

public class Configuration {
    private TemplateRegistry templateRegistry;

    public TemplateRegistry templateRegistry() {
        return templateRegistry;
    }
}
