package com.benjaminsproule.mediaorganiser.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MimeTypesUtil {

    public static final String IMAGE_JPG = "image/jpg";
    public static final String IMAGE_JPEG = "image/jpeg";
    public static final String IMAGE_TIFF = "image/tiff";
    public static final String IMAGE_HEIC = "image/heic";
    public static final String IMAGE_HEIF = "image/heif";
    public static final String VIDEO_MP4 = "video/mp4";
    public static final String VIDEO_AVI = "video/x-msvideo";
    public static final String VIDEO_QUICKTIME = "video/quicktime";
    public static final String IMAGES_JPG_MAPPING = IMAGE_JPG + "\tjpg jpeg";

    public static final String IMAGES_TIFF = IMAGE_TIFF + "\ttif tiff";
    public static final String VIDEOS_MP4 = VIDEO_MP4 + "\tmp4";
    public static final String VIDEOS_AVI = VIDEO_AVI + "\tavi";

    private static final Set<String> MIME_TYPES = new HashSet<String>();

    static {
        MIME_TYPES.add(IMAGES_JPG_MAPPING);
        MIME_TYPES.add(IMAGES_TIFF);
        MIME_TYPES.add(VIDEOS_MP4);
        MIME_TYPES.add(VIDEOS_AVI);
    }

    /**
     * Creates a mime types file in the home directory if it doesn't already
     * exist, containing the required mime types for this application
     *
     * @throws IOException
     */
    public static void createMimeTypesFile() throws IOException {
        Path mimeTypes = new File(System.getProperty("user.home") + "/.mime.types").toPath();
        if (!Files.exists(mimeTypes)) {
            Files.createFile(mimeTypes);
        }

        List<String> lines = Files.readAllLines(mimeTypes);
        MIME_TYPES.stream().filter(mimeType -> !lines.contains(mimeType)).forEach(lines::add);

        Files.write(mimeTypes, lines);
    }

    /**
     * Return whether the system requires a mime types file.
     *
     * @return true if the system is Mac OS X or Linux, false if it's not
     */
    public static boolean requiresMimeTypesFile() {
        String osName = System.getProperty("os.name").toLowerCase();
        return osName.contains("mac") || osName.contains("linux");

    }
}
