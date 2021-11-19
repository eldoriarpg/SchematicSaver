/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicsaver;

import de.eldoria.eldoutilities.localization.ILocalizer;
import de.eldoria.eldoutilities.messages.MessageSender;
import de.eldoria.eldoutilities.plugin.EldoPlugin;
import de.eldoria.messageblocker.MessageBlockerAPI;
import de.eldoria.schematicsaver.commands.SchematicExport;
import de.eldoria.schematicsaver.commands.SchematicTemplate;
import de.eldoria.schematicsaver.commands.template.Sessions;
import de.eldoria.schematicsaver.config.Configuration;
import de.eldoria.schematicsaver.config.elements.PasteSettings;
import de.eldoria.schematicsaver.config.elements.RenderSettings;
import de.eldoria.schematicsaver.config.elements.TemplateRegistry;
import de.eldoria.schematicsaver.config.elements.template.Namespace;
import de.eldoria.schematicsaver.config.elements.template.Template;
import de.eldoria.schematicsaver.config.elements.template.Type;
import de.eldoria.schematicsaver.config.elements.template.Variant;
import de.eldoria.schematicsaver.services.BoundingRenderer;
import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.List;

public class SchematicSaver extends EldoPlugin {
    @Override
    public void onPluginEnable() throws Throwable {

        var localizer = ILocalizer.create(this, "en_US");
        localizer.setLocale("en_US");
        MessageSender.create(this, ChatColor.DARK_AQUA + "[SS]");

        var configuration = new Configuration(this);

        var messageBlocker = MessageBlockerAPI.builder(this)
                .addWhitelisted("[SS]")
                .addWhitelisted("FAWE")
                .addWhitelisted("First position")
                .addWhitelisted("Second position")
                .build();
        var sessions = new Sessions(this, messageBlocker);
        var boundingRender = BoundingRenderer.create(this, configuration, sessions);
        registerCommand(new SchematicTemplate(this, sessions, messageBlocker, boundingRender, configuration));
        registerCommand(new SchematicExport(this, configuration, boundingRender));
    }

    @Override
    public List<Class<? extends ConfigurationSerializable>> getConfigSerialization() {
        return List.of(Template.class, Type.class, Variant.class, TemplateRegistry.class, Namespace.class,
                PasteSettings.class, RenderSettings.class);
    }
}
