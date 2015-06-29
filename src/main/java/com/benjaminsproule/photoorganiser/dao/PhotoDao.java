package com.benjaminsproule.photoorganiser.dao;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static com.benjaminsproule.photoorganiser.util.MimeTypesUtil.*;
import static java.io.File.separator;
import static java.nio.file.Files.*;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.join;

public class PhotoDao {

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

        List<Path> images = new ArrayList<>();
        getFiles(directory, images);

        return images;
    }

    private void getFiles(Path directory, List<Path> images) throws IOException {
        List<Path> paths = new ArrayList<>();
        list(directory).forEach(paths::add);
        for (Path path : paths) {
            if (isDirectory(path)) {
                getFiles(path, images);
                continue;
            }

            String contentType = probeContentType(path);
            if (contentType != null &&
                (contentType.equalsIgnoreCase(IMAGE_JPG) ||
                    contentType.equalsIgnoreCase(IMAGE_JPEG) ||
                    contentType.equalsIgnoreCase(IMAGE_TIFF))) {
                images.add(path);
            }
        }
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

        createDirectories(directory);
        Path newPath = new File(outputDirectory + separator + path.getFileName().toString()).toPath();

        if (exists(newPath)) {
            int i = 0;
            do {
                newPath = incrementFilenameIndex(outputDirectory, path, i);
                i++;
            } while (exists(newPath));
        }

        move(path, newPath);
    }

    private Path incrementFilenameIndex(String outputDirectory, Path path, int index) {
        Path newPath;
        String pathName = path.getFileName().toString();
        String[] pathNames = pathName.split("\\.");
        pathNames[pathNames.length - 2] = pathNames[pathNames.length - 2] + index;
        newPath = new File(outputDirectory + separator + join(pathNames, ".")).toPath();
        return newPath;
    }
}
