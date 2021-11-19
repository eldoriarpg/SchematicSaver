/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicsaver.util;

public final class Permissions {
    private static final String BASE = "schematicsaver";

    private static String perm(String... perms) {
        return String.join(".", perms);
    }

    public static final class SchematicTemplate {
        private static final String SCHEMATIC_TEMPLATE = "schematictemplate";
        public static final String CREATE = perm(SCHEMATIC_TEMPLATE, "create");
        public static final String EDIT = perm(SCHEMATIC_TEMPLATE, "edit");

        public static final class Edit {
            public static final String OVERRIDE = perm(EDIT, "override");
        }
    }

    public static final class SchematicExport {
        private static final String SCHEMATIC_EXPORT = perm(BASE, "schematicexport");
        public static final String DELETE = perm(SCHEMATIC_EXPORT, "delete");
        public static final String EXPORT = perm(SCHEMATIC_EXPORT, "export");
        public static final String NAMESPACE = perm(SCHEMATIC_EXPORT, "namespace");
        public static final String PASTE = perm(SCHEMATIC_EXPORT, "paste");
        public static final String A = perm(SCHEMATIC_EXPORT, "");

        public static final class Export {
            public static final String OVERRIDE = perm(EXPORT, "override");
        }


        public static final class Namespace {
            public static final String ADD = perm(NAMESPACE, "add");
            public static final String REMOVE = perm(NAMESPACE, "remove");
            public static final String SHOW = perm(NAMESPACE, "show");
        }

        public static final class Delete {
            public static final String ALL = perm(DELETE, "all");
            public static final String ID = perm(DELETE, "id");
            public static final String NAMESPACE = perm(DELETE, "namespace");
            public static final String TYPE = perm(DELETE, "type");
            public static final String VARIANT = perm(DELETE, "variant");
        }
    }
}
