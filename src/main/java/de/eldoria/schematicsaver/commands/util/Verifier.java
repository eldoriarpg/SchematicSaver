/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicsaver.commands.util;

import de.eldoria.eldoutilities.commands.command.util.CommandAssertions;
import de.eldoria.eldoutilities.commands.exceptions.CommandException;
import de.eldoria.eldoutilities.localization.Replacement;

import java.util.regex.Pattern;

public final class Verifier {
    private static final Pattern NAME = Pattern.compile("[a-zA-Z1-9_]+");


    private Verifier() {
        throw new UnsupportedOperationException("This is a utility class.");
    }

    public static void checkName(String name) throws CommandException {
        CommandAssertions.isTrue(Verifier.NAME.matcher(name).matches(), "error.invalidName", Replacement.create("ALLOWED", "a-zA-Z1-9_"));
    }
}
