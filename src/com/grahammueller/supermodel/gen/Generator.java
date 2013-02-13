package com.grahammueller.supermodel.gen;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.List;
import com.grahammueller.supermodel.entity.Attribute;
import com.grahammueller.supermodel.entity.AttributeType;
import com.grahammueller.supermodel.entity.Entity;

public class Generator {
    /**
     * Generates the files for a list of Entities, based on
     * the path to the desired destination directory.
     * 
     * @param entities The Entity objects to generate files for.
     * @param path The path to the directory the files should be output to.
     * @param overwrite Whether or not to overwrite existing files.
     * @throws Exception Writing issues, as well as if the files exist and overwrite is false.
     */
    public static void generateEntitiesFiles(List<Entity> entities, String path, boolean overwrite) throws Exception {
        generateEntitiesFiles(entities, new File(path), overwrite);
    }

    /**
     * Generates the files for a list of Entities, based on
     * the directory in which the files will be written.
     * 
     * @param entities The Entity objects to generate files for.
     * @param dir The directory the files should be output to.
     * @param overwrite Whether or not to overwrite existing files.
     * @throws Exception Writing issues, as well as if the files exist and overwrite is false.
     */
    public static void generateEntitiesFiles(List<Entity> entities, File dir, boolean overwrite) throws Exception {
        for (Entity e : entities) {
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
        methodBuilder = new StringBuilder();

        parseAttributes(entity.getAttributes());

        try {
            writer.write(PACKAGE_DECLARATION);
            writer.write(importBuilder.toString());
            writer.write(String.format(CLASS_DECLARATION_FORMAT, entity.getName()));
            writer.write(methodBuilder.toString());
            writer.write('\n');
            writer.write(fieldBuilder.toString());
            writer.write("}\n");
        }
        finally {
            writer.close();
        }
    }

    private static void parseAttributes(List<Attribute> attrs) throws Exception {
        for (Attribute attr : attrs) {
            if (attr.getType() == AttributeType.INTEGER_PRIMARY_KEY) {
                importBuilder.append(PRIMARY_KEY_ANNOTATION_IMPORT);
                fieldBuilder.append(PRIMARY_KEY_ANNOTATION);
            }

            fieldBuilder.append(String.format(FIELD_DECLARATION_FORMAT, TAB, attr.getType().toJavaString(), attr.getName()));

            methodBuilder.append(String.format(DECLARATION_FORMAT, TAB, attr.getName()));
            methodBuilder.append(String.format(GET_FORMAT, TAB, attr.getType().toJavaString(), upCasedName(attr.getName()), attr.getName()));
            methodBuilder.append(String.format(SET_FORMAT, TAB, upCasedName(attr.getName()), attr.getType().toJavaString(), attr.getName(), attr.getName(), attr.getName()));
        }
    }

    private static String upCasedName(String name) {
        return String.format("%s%s", name.substring(0, 1).toUpperCase(), name.substring(1));
    }

    // Builders
    private static StringBuilder importBuilder;
    private static StringBuilder fieldBuilder;
    private static StringBuilder methodBuilder;

    // Constants
    private static final String TAB = "    ";
    private static final String PACKAGE_DECLARATION = "package com.yourdomain.model;\n\n";
    private static final String PRIMARY_KEY_ANNOTATION = String.format("%s@AutoIncrement\n%s@PrimaryKey\n", TAB, TAB);
    private static final String PRIMARY_KEY_ANNOTATION_IMPORT = "import com.kremerk.Sqlite.Annotations.AutoIncrement;\n" +
                                                                "import com.kremerk.Sqlite.Annotations.PrimaryKey;\n\n";

    // Formats
    private static final String CLASS_DECLARATION_FORMAT = "public class %s {\n";
    private static final String FIELD_DECLARATION_FORMAT = "%sprivate %s %s;\n";
    private static final String GET_FORMAT = "%spublic %s get%s() { return this.%s; }\n";
    private static final String SET_FORMAT = "%spublic void set%s(%s %s) { this.%s = %s; }\n";
    private static final String DECLARATION_FORMAT = "\n%s// %s methods\n";
}
