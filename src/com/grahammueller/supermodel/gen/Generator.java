package com.grahammueller.supermodel.gen;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import com.grahammueller.supermodel.entity.Entity;

public class Generator {
    public static void generateEntitiesFiles(List<Entity> entities, String path) throws IOException {
        for (Entity e : entities) {
            generateEntityFile(e, path);
        }
    }

    public static void generateEntityFile(Entity entity, String path) throws IOException {
        String fullPath = String.format("%s%s%s.java", path, File.separator, entity.getName());

        File outFile = new File(fullPath);

        if (outFile.exists()) {
            // TODO prompt for overwrite
            throw new IllegalArgumentException("File already exists");
        }

        outFile.createNewFile();

        BufferedWriter writer = new BufferedWriter(new FileWriter(outFile));

        try {
            writer.write("package com.yourdomain.model;\n\n");
            writer.write("public class " + entity.getName() + " {\n\n}\n");
        }
        finally {
            writer.close();
        }
    }
}
