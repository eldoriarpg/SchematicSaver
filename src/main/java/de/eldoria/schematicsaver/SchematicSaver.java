/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicsaver;

import de.eldoria.eldoutilities.plugin.EldoPlugin;
import de.eldoria.messageblocker.MessageBlockerAPI;
import de.eldoria.schematicsaver.commands.SchematicTemplate;
import de.eldoria.schematicsaver.config.Configuration;

public class SchematicSaver extends EldoPlugin {
    @Override
    public void onPluginEnable() throws Throwable {

        var configuration = new Configuration(this);

        var messageBlocker = MessageBlockerAPI.builder(this).addWhitelisted("§3[SS]").build();
        registerCommand(new SchematicTemplate(this, messageBlocker,configuration));
    }
}
