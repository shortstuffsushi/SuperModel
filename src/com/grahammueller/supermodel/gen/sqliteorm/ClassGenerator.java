package com.grahammueller.supermodel.gen.sqliteorm;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import com.grahammueller.supermodel.entity.Attribute;
import com.grahammueller.supermodel.entity.AttributeType;
import com.grahammueller.supermodel.entity.Entity;
import com.grahammueller.supermodel.entity.EntityManager;
import com.grahammueller.supermodel.entity.Relationship;

/**
 * Class generator for SQLiteORM
 * 
 * @author gmueller
 */
public class ClassGenerator {
    /**
     * Generates the files for a list of Entities, based on the path to the desired destination directory.
     * 
     * @param path The path to the directory the files should be output to.
     * @param overwrite Whether or not to overwrite existing files.
     * @throws Exception Writing issues, as well as if the files exist and overwrite is false.
     */
    public static void generateEntitiesFiles(String path, boolean overwrite) throws Exception {
        generateEntitiesFiles(new File(path), overwrite);
    }

    /**
     * Generates the files for a list of Entities, based on the directory in which the files will be written.
     * 
     * @param dir The directory the files should be output to.
     * @param overwrite Whether or not to overwrite existing files.
     * @throws Exception Writing issues, as well as if the files exist and overwrite is false.
     */
    public static void generateEntitiesFiles(File dir, boolean overwrite) throws Exception {
        List<Entity> entities = EntityManager.getAllEntities();
        Map<Entity, Map<String, StringBuilder>> entityMap = new HashMap<Entity, Map<String, StringBuilder>>();

        // Loop through once, create all the Entities
        // and Maps, ignoring Relationships for now.
        for (Entity entity : entities) {
            Map<String, StringBuilder> builders = generateBuilders();

            parseEntityWithBuilders(entity, builders);

            entityMap.put(entity, builders);
        }

        // Now actually handle the Relationships
        for (Entity entity : entities) {
            parseRelationships(entity, entityMap);
        }

        // Finally, write all the files
        for (Entry<Entity, Map<String, StringBuilder>> entry : entityMap.entrySet()) {
            generateEntityFile(entry.getKey(), entry.getValue(), dir, overwrite);
        }
    }

    ////////////////////
    // Entity Parsing //
    ////////////////////
    private static void parseEntityWithBuilders(Entity entity, Map<String, StringBuilder> builders) {
        StringBuilder importBuilder = builders.get("import");
        StringBuilder staticMethodBuilder = builders.get("static");

        importBuilder.append(LIST_IMPORT).append(DATA_CONN_EX_IMPORT).append(SQL_STATEMENT_IMPORT);

        staticMethodBuilder.append(String.format(GET_ALL_FORMAT, entity.getName())).append(String.format(GET_ALL_BODY_FORMAT, entity.getName()));

        parseAttributes(entity, builders);
    }

    private static void parseAttributes(Entity entity, Map<String, StringBuilder> builders) {
        StringBuilder importBuilder = builders.get("import");
        StringBuilder fieldBuilder = builders.get("field");
        StringBuilder fieldMethodBuilder = builders.get("fieldmethod");
        StringBuilder staticMethodBuilder = builders.get("static");

        for (Attribute attr : entity.getAttributes()) {
            if (attr.getType() == AttributeType.UNDEFINED) {
                throw new IllegalArgumentException(attr.getName() + " does not have a valid type");
            }

            if (attr.isPrimaryKey()) {
                importBuilder.append(AUTO_INCREMENT_ANNOTATION_IMPORT);
                importBuilder.append(PRIMARY_KEY_ANNOTATION_IMPORT);
                fieldBuilder.append(PRIMARY_KEY_ANNOTATION);
                staticMethodBuilder.append(String.format(GET_BY_PRIMARY_KEY_FORMAT, entity.getName(), upCasedName(attr.getName()), attr.getType().toJavaString(), attr.getName()));
                staticMethodBuilder.append(String.format(GET_BY_PRIMARY_KEY_BODY_FORMAT, entity.getName(), attr.getName(), attr.getName()));
            }
            else {
                staticMethodBuilder.append(String.format(GET_BY_FIELD_FORMAT, entity.getName(), upCasedName(attr.getName()), attr.getType().toJavaString(), attr.getName()));
                staticMethodBuilder.append(String.format(GET_BY_FIELD_BODY_FORMAT, entity.getName(), attr.getName(), attr.getName()));
            }

            fieldBuilder.append(String.format(FIELD_DECLARATION_FORMAT, attr.getType().toJavaString(), attr.getName()));

            fieldMethodBuilder.append(String.format(DECLARATION_FORMAT, attr.getName()));
            fieldMethodBuilder.append(String.format(GET_FORMAT, attr.getType().toJavaString(), upCasedName(attr.getName()), attr.getName()));
            fieldMethodBuilder.append(String.format(SET_FORMAT, upCasedName(attr.getName()), attr.getType().toJavaString(), attr.getName(), attr.getName(), attr.getName()));
        }
    }

    private static void parseRelationships(Entity entity, Map<Entity, Map<String, StringBuilder>> entityBuilderMaps) {
        if (entity.getRelationships().size() > 0) {
            StringBuilder entityImportBuilder = entityBuilderMaps.get(entity).get("import");
            entityImportBuilder.append(ONE_TO_MANY_ANNOTATION_IMPORT);
        }

        for (Relationship rltn : entity.getRelationships()) {
            StringBuilder entityFieldBuilder = entityBuilderMaps.get(entity).get("field");
            StringBuilder entityFieldMethodBuilder = entityBuilderMaps.get(entity).get("fieldmethod");
            StringBuilder rltnEntityFieldBuilder = entityBuilderMaps.get(rltn.getEntity()).get("field");
            StringBuilder rltnEntityFieldMethodBuilder = entityBuilderMaps.get(rltn.getEntity()).get("fieldmethod");
            StringBuilder rltnEntityStaticMethodBuilder = entityBuilderMaps.get(rltn.getEntity()).get("static");

            Attribute primaryKey = entity.getPrimaryKey();
            entityFieldBuilder.append(String.format(ONE_TO_MANY_ANNOTATION_FORMAT, entity.getName().toLowerCase(), upCasedName(primaryKey.getName())));
            entityFieldBuilder.append(String.format(FIELD_LIST_DECLARATION_FORMAT, rltn.getEntity().getName(), rltn.getName()));
            entityFieldMethodBuilder.append(String.format(DECLARATION_FORMAT, rltn.getName()));
            entityFieldMethodBuilder.append(String.format(GET_LIST_FORMAT, rltn.getEntity().getName(), upCasedName(rltn.getName()), rltn.getName()));
            entityFieldMethodBuilder.append(String.format(SET_LIST_FORMAT, upCasedName(rltn.getName()), rltn.getEntity().getName(), rltn.getName(), rltn.getName(), rltn.getName()));

            rltnEntityFieldBuilder.append(String.format(FIELD_DECLARATION_FORMAT, primaryKey.getType().toJavaString(), mergedEntityAndKeyString(entity, false)));
            rltnEntityFieldMethodBuilder.append(String.format(FOREIGN_KEY_DECLARATION_FORMAT, mergedEntityAndKeyString(entity, false)));
            rltnEntityFieldMethodBuilder.append(String.format(GET_FORMAT, primaryKey.getType().toJavaString(), mergedEntityAndKeyString(entity, true), mergedEntityAndKeyString(entity, false)));
            rltnEntityFieldMethodBuilder.append(String.format(SET_FORMAT, mergedEntityAndKeyString(entity, true), primaryKey.getType().toJavaString(), mergedEntityAndKeyString(entity, false), mergedEntityAndKeyString(entity, false), mergedEntityAndKeyString(entity, false)));
            rltnEntityStaticMethodBuilder.append(String.format(GET_BY_FIELD_FORMAT, rltn.getEntity().getName(), mergedEntityAndKeyString(entity, true), primaryKey.getType().toJavaString(), mergedEntityAndKeyString(entity, false)));
            rltnEntityStaticMethodBuilder.append(String.format(GET_BY_FIELD_BODY_FORMAT, rltn.getEntity().getName(), mergedEntityAndKeyString(entity, false), mergedEntityAndKeyString(entity, false)));
        }
    }

    /////////////////
    // File Output //
    /////////////////
    public static void generateEntityFile(Entity entity, Map<String, StringBuilder> builders, File dir, boolean overwrite) throws Exception {
        String fullPath = String.format("%s%s%s.java", dir.getAbsolutePath(), File.separator, entity.getName());

        Writer writer = createWriter(fullPath, overwrite);

        writeEntityFromStringBuilders(entity, builders, writer);
    }

    private static Writer createWriter(String fullPath, boolean overwrite) throws Exception {
        File outFile = new File(fullPath);

        if (outFile.exists()) {
            if (!overwrite) {
                throw new IllegalArgumentException("File already exists");
            }
        }
        else {
            outFile.createNewFile();
        }

        if (!outFile.canWrite()) {
            throw new IllegalArgumentException("Missing permissions to write to file.");
        }

        return new BufferedWriter(new FileWriter(outFile));
    }

    private static void writeEntityFromStringBuilders(Entity entity, Map<String, StringBuilder> builders, Writer writer) throws Exception {
        StringBuilder importBuilder = builders.get("import");
        StringBuilder fieldBuilder = builders.get("field");
        StringBuilder fieldMethodBuilder = builders.get("fieldmethod");
        StringBuilder staticMethodBuilder = builders.get("static");

        try {
            writer.write(PACKAGE_DECLARATION);
            writer.write(importBuilder.toString());
            writer.append('\n');
            writer.write(String.format(CLASS_DECLARATION_FORMAT, entity.getName()));
            writer.write(staticMethodBuilder.toString());
            writer.write(fieldMethodBuilder.toString());
            writer.write(fieldBuilder.toString());
            writer.write("}\n");
        }
        finally {
            writer.close();
        }
    }

    /////////////
    // Utility //
    /////////////
    private static Map<String, StringBuilder> generateBuilders() {
        Map<String, StringBuilder> builders = new HashMap<String, StringBuilder>();

        builders.put("import", new StringBuilder());
        builders.put("field", new StringBuilder());
        builders.put("fieldmethod", new StringBuilder());
        builders.put("static", new StringBuilder());

        return builders;
    }

    private static String mergedEntityAndKeyString(Entity entity, boolean uppercase) {
        String entityName = uppercase ? entity.getName() : entity.getName().toLowerCase();
        String primaryKeyName = upCasedName(entity.getPrimaryKey().getName());
        return entityName + primaryKeyName;
    }

    private static String upCasedName(String name) {
        return String.format("%s%s", name.substring(0, 1).toUpperCase(), name.substring(1));
    }

    // Constants
    private static final String PACKAGE_DECLARATION = "package com.yourdomain.model;\n\n";
    private static final String PRIMARY_KEY_ANNOTATION = "    @AutoIncrement\n    @PrimaryKey\n";
    private static final String ONE_TO_MANY_ANNOTATION_FORMAT = "    @OneToMany(\"%s%s\")\n";
    private static final String LIST_IMPORT = "import java.util.List;\n";
    private static final String DATA_CONN_EX_IMPORT = "import com.njkremer.Sqlite.DataConnectionException;\n";
    private static final String SQL_STATEMENT_IMPORT = "import com.njkremer.Sqlite.SqlStatement;\n";
    private static final String AUTO_INCREMENT_ANNOTATION_IMPORT = "import com.njkremer.Sqlite.Annotations.AutoIncrement;\n";
    private static final String PRIMARY_KEY_ANNOTATION_IMPORT = "import com.njkremer.Sqlite.Annotations.PrimaryKey;\n";
    private static final String ONE_TO_MANY_ANNOTATION_IMPORT = "import com.njkremer.Sqlite.Annotations.OneToMany;\n";

    // Formats
    private static final String CLASS_DECLARATION_FORMAT = "public class %s {\n";
    private static final String FIELD_DECLARATION_FORMAT = "    private %s %s;\n";
    private static final String FIELD_LIST_DECLARATION_FORMAT = "    private List<%s> %s;\n";
    private static final String GET_FORMAT = "    public %s get%s() { return this.%s; }\n";
    private static final String GET_LIST_FORMAT = "    public List<%s> get%s() { return this.%s; }\n";
    private static final String SET_FORMAT = "    public void set%s(%s %s) { this.%s = %s; }\n\n";
    private static final String SET_LIST_FORMAT = "    public void set%s(List<%s> %s) { this.%s = %s; }\n\n";
    private static final String DECLARATION_FORMAT = "    // %s methods\n";
    private static final String FOREIGN_KEY_DECLARATION_FORMAT = "    // foreign key %s methods\n";
    private static final String GET_ALL_FORMAT = "    public static List<%s> getAll() throws DataConnectionException {\n";
    private static final String GET_ALL_BODY_FORMAT = "        return SqlStatement.select(%s.class).getList();\n    }\n\n";
    private static final String GET_BY_FIELD_FORMAT = "    public static List<%s> getBy%s(%s %s) throws DataConnectionException {\n";
    private static final String GET_BY_FIELD_BODY_FORMAT = "        return SqlStatement.select(%s.class).where(\"%s\").eq(%s).getList();\n    }\n\n";
    private static final String GET_BY_PRIMARY_KEY_FORMAT = "    public static %s getBy%s(%s %s) throws DataConnectionException {\n";
    private static final String GET_BY_PRIMARY_KEY_BODY_FORMAT = "        return SqlStatement.select(%s.class).where(\"%s\").eq(%s).getFirst();\n    }\n\n";
}
