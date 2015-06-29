package com.benjaminsproule.photoorganiser.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class MimeTypesUtil {

    public static final String IMAGE_JPG = "image/jpg";
    public static final String IMAGE_JPEG = "image/jpeg";
    public static final String IMAGE_TIFF = "image/tiff";

    public static final String IMAGES_JPG_MAPPING = IMAGE_JPG + "\tjpg jpeg";
    public static final String IMAGES_TIFF = IMAGE_TIFF + "\ttif tiff";

    public static void createMimeTypesFile() throws IOException {
        Path mimeTypes = new File(System.getProperty("user.home") + "/.mime.types").toPath();
        if (!Files.exists(mimeTypes)) {
            Files.createFile(mimeTypes);
        }

        List<String> lines = Files.readAllLines(mimeTypes);
        if (!lines.contains(IMAGES_JPG_MAPPING)) {
            lines.add(IMAGES_JPG_MAPPING);
        }

        if (!lines.contains(IMAGES_TIFF)) {
            lines.add(IMAGES_TIFF);
        }

        Files.write(mimeTypes, lines);
    }

    public static boolean requiresMimeTypesFile() {
        String osName = System.getProperty("os.name").toLowerCase();
        return osName.contains("mac") || osName.contains("linux");

    }
}
