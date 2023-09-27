package com.benjaminsproule.mediaorganiser.dao;

import lombok.extern.slf4j.Slf4j;
import org.overviewproject.mime_types.GetBytesException;
import org.overviewproject.mime_types.MimeTypeDetector;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static com.benjaminsproule.mediaorganiser.util.MimeTypesUtil.*;
import static java.io.File.separator;
import static java.nio.file.Files.*;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.join;

@Slf4j
public class MediaDao {

    private final MimeTypeDetector mimeTypeDetector;

    public MediaDao() {
        this.mimeTypeDetector = new MimeTypeDetector();
    }

    /**
     * Get the files from the given inputDirectory
     *
     * @param inputDirectory the directory to get the media files from
     * @return a list of {@link Path}s of the media files
     * @throws IOException              if there is an issue getting the files
     * @throws IllegalArgumentException if inputDirectory is not provided, doesn't exist or is not a directory
     */
    public List<Path> getFiles(String inputDirectory) throws IOException {
        if (isBlank(inputDirectory)) {
            throw new IllegalArgumentException("An input directory should be provided");
        }

        Path directory = new File(inputDirectory).toPath();
        if (notExists(directory)) {
            throw new IllegalArgumentException("The input directory does not exist");
        }

        if (!isDirectory(directory)) {
            throw new IllegalArgumentException("The input directory is not a directory");
        }

        log.info("Getting files under " + directory);
        List<Path> images = new ArrayList<>();
        getFiles(directory, images);
        log.info("Found a total of " + images.size() + " files under " + directory);
        return images;
    }

    private void getFiles(Path directory, List<Path> images) throws IOException {
        log.info("Getting files under " + directory);
        List<Path> paths;
        try (Stream<Path> entries = list(directory)) {
            paths = entries.collect(toList());
        }
        log.info("Found a total of " + paths.size() + " entries under " + directory);
        for (Path path : paths) {
            if (isDirectory(path)) {
                getFiles(path, images);
                continue;
            }

            try {
                String contentType = mimeTypeDetector.detectMimeType(path);
                if (contentType != null && (contentType.equalsIgnoreCase(IMAGE_PNG)
                    || contentType.equalsIgnoreCase(IMAGE_JPG) || contentType.equalsIgnoreCase(IMAGE_JPEG)
                    || contentType.equalsIgnoreCase(IMAGE_HEIC) || contentType.equalsIgnoreCase(IMAGE_HEIF)
                    || contentType.equalsIgnoreCase(IMAGE_TIFF)
                    || contentType.equalsIgnoreCase(VIDEO_MP4) || contentType.equalsIgnoreCase(VIDEO_AVI)
                    || contentType.equalsIgnoreCase(VIDEO_QUICKTIME))) {
                    images.add(path);
                } else {
                    log.debug(contentType + " is not a valid file type.");
                }
            } catch (GetBytesException exception) {
                log.error("Exception thrown whilst trying to detect the mime type.", exception);
                return;
            }
        }
        log.info("Finished traversing " + directory);
    }

    public void saveFile(String outputDirectory, Path path) throws IOException {
        if (isBlank(outputDirectory)) {
            throw new IllegalArgumentException("An output directory should be provided");
        }

        if (path == null) {
            throw new IllegalArgumentException("A path should be provided");
        }

        Path directory = new File(outputDirectory).toPath();
        if (isRegularFile(directory)) {
            throw new IllegalArgumentException("The output directory is a file");
        }

        log.info("Ensuring output directory " + directory + " exists");
        createDirectories(directory);
        Path newPath = new File(outputDirectory + separator + path.getFileName().toString()).toPath();

        if (exists(newPath)) {
            if (Files.size(newPath) == Files.size(path)) {
                log.info("File " + path.getFileName() + " with the same name and file size already exists, suggesting this is a duplicate and so won't be moved");
                return;
            }
            int i = 0;
            do {
                log.info(newPath + " already exists, trying to increment filename index with " + i);
                newPath = incrementFilenameIndex(outputDirectory, path, i);
                i++;
            } while (exists(newPath));
        }

        log.info("Moving " + newPath);
        move(path, newPath);
        log.info("Moved " + newPath);
    }

    private Path incrementFilenameIndex(String outputDirectory, Path path, int index) {
        Path newPath;
        String pathName = path.getFileName().toString();
        String[] pathNames = pathName.split("\\.");
        pathNames[pathNames.length - 2] = pathNames[pathNames.length - 2] + index;
        newPath = new File(outputDirectory + separator + join(pathNames, ".")).toPath();
        return newPath;
    }

    public void deleteEmptyDirectory(Path path) throws IOException {
        Path parent = path.getParent();
        log.info("Trying to delete " + parent + " if it is empty");
        try (Stream<Path> entries = list(parent)) {
            if (entries.allMatch((entry) -> {
                String fileName = entry.getFileName().toString();
                return fileName.equals("Thumbs.db") || fileName.equals(".DS_Store");
            })) {
                log.info("Deleting " + parent);
                Files.deleteIfExists(parent.resolve("Thumbs.db"));
                Files.deleteIfExists(parent.resolve(".DS_Store"));
                Files.delete(parent);
                log.info("Deleted " + parent);
            } else {
                log.info(parent + " is not empty");
            }
        }
    }
}
