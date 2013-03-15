package com.grahammueller.supermodel.gen.sqliteorm;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import com.grahammueller.supermodel.entity.Attribute;
import com.grahammueller.supermodel.entity.AttributeType;
import com.grahammueller.supermodel.entity.Entity;
import com.grahammueller.supermodel.entity.EntityManager;

/**
 * Class generator for SQLiteORM
 * @author gmueller
 */
public class ClassGenerator {
    /**
     * Generates the files for a list of Entities, based on
     * the path to the desired destination directory.
     * 
     * @param path The path to the directory the files should be output to.
     * @param overwrite Whether or not to overwrite existing files.
     * @throws Exception Writing issues, as well as if the files exist and overwrite is false.
     */
    public static void generateEntitiesFiles(String path, boolean overwrite) throws Exception {
        generateEntitiesFiles(new File(path), overwrite);
    }

    /**
     * Generates the files for a list of Entities, based on
     * the directory in which the files will be written.
     * 
     * @param dir The directory the files should be output to.
     * @param overwrite Whether or not to overwrite existing files.
     * @throws Exception Writing issues, as well as if the files exist and overwrite is false.
     */
    public static void generateEntitiesFiles(File dir, boolean overwrite) throws Exception {
        for (Entity e : EntityManager.getAllEntities()) {
            generateEntityFile(e, dir, overwrite);
        }
    }

    /**
     * Generates the file for a single Entity, based on
     * the path to the desired destination directory.
     * 
     * @param entities The Entity objects to generate files for.
     * @param path The path to the directory the files should be output to.
     * @param overwrite Whether or not to overwrite existing files.
     * @throws Exception Writing issues, as well as if the files exist and overwrite is false.
     */
    public static void generateEntityFile(Entity entity, String path, boolean overwrite) throws Exception {
        generateEntityFile(entity, new File(path), overwrite);
    }

    /**
     * Generates the file for a single Entity, based on
     * the directory in which the file will be written.
     * 
     * @param entities The Entity objects to generate files for.
     * @param dir The directory the files should be output to.
     * @param overwrite Whether or not to overwrite existing files.
     * @throws Exception Writing issues, as well as if the files exist and overwrite is false.
     */
    public static void generateEntityFile(Entity entity, File dir, boolean overwrite) throws Exception {
        String fullPath = String.format("%s%s%s.java", dir.getAbsolutePath(), File.separator, entity.getName());

        Writer writer = createWriter(fullPath, overwrite);

        writeEntity(entity, writer);
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

    private static void writeEntity(Entity entity, Writer writer) throws Exception {
        importBuilder = new StringBuilder();
        fieldBuilder = new StringBuilder();
        fieldMethodBuilder = new StringBuilder();
        staticMethodBuilder = new StringBuilder();

        importBuilder.append(LIST_IMPORT)
                     .append(DATA_CONN_EX_IMPORT)
                     .append(SQL_STATEMENT_IMPORT);

        staticMethodBuilder.append(String.format(GET_ALL_FORMAT, entity.getName()))
                           .append(String.format(GET_ALL_BODY_FORMAT, entity.getName()));

        parseAttributes(entity);

        try {
            writer.write(PACKAGE_DECLARATION);
            writer.write(importBuilder.toString());
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

    private static void parseAttributes(Entity entity) throws Exception {
        for (Attribute attr : entity.getAttributes()) {
            if (attr.getType() == AttributeType.INTEGER_PRIMARY_KEY) {
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

    private static String upCasedName(String name) {
        return String.format("%s%s", name.substring(0, 1).toUpperCase(), name.substring(1));
    }

    // Builders
    private static StringBuilder importBuilder;
    private static StringBuilder fieldBuilder;
    private static StringBuilder fieldMethodBuilder;
    private static StringBuilder staticMethodBuilder;

    // Constants
    private static final String PACKAGE_DECLARATION = "package com.yourdomain.model;\n\n";
    private static final String PRIMARY_KEY_ANNOTATION = "    @AutoIncrement\n    @PrimaryKey\n";
    private static final String LIST_IMPORT = "import java.util.list;\n";
    private static final String DATA_CONN_EX_IMPORT = "import com.kremerk.Sqlite.DataConnectionException;\n";
    private static final String SQL_STATEMENT_IMPORT = "import com.kremerk.Sqlite.SqlStatement;\n";
    private static final String AUTO_INCREMENT_ANNOTATION_IMPORT = "import com.kremerk.Sqlite.Annotations.AutoIncrement;\n";
    private static final String PRIMARY_KEY_ANNOTATION_IMPORT = "import com.kremerk.Sqlite.Annotations.PrimaryKey;\n\n";

    // Formats
    private static final String CLASS_DECLARATION_FORMAT = "public class %s {\n";
    private static final String FIELD_DECLARATION_FORMAT = "    private %s %s;\n";
    private static final String GET_FORMAT = "    public %s get%s() { return this.%s; }\n";
    private static final String SET_FORMAT = "    public void set%s(%s %s) { this.%s = %s; }\n\n";
    private static final String DECLARATION_FORMAT = "    // %s methods\n";
    private static final String GET_ALL_FORMAT = "    public static List<%s> getAll() throws DataConnectionException {\n";
    private static final String GET_ALL_BODY_FORMAT = "        return SqlStatement.select(%s.class).getList();\n    }\n\n";
    private static final String GET_BY_FIELD_FORMAT = "    public static List<%s> getBy%s(%s %s) throws DataConnectionException {\n";
    private static final String GET_BY_FIELD_BODY_FORMAT = "        return SqlStatement.select(%s.class).where(\"%s\").eq(%s).getList();\n    }\n\n";
    private static final String GET_BY_PRIMARY_KEY_FORMAT = "    public static %s getBy%s(%s %s) throws DataConnectionException {\n";
    private static final String GET_BY_PRIMARY_KEY_BODY_FORMAT = "        return SqlStatement.select(%s.class).where(\"%s\").eq(%s).getFirst();\n    }\n\n";
}
