package uk.co.bensproule.photoorganiser.dao;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static java.io.File.separator;
import static java.lang.Integer.MAX_VALUE;
import static java.nio.file.Files.*;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.join;

@Slf4j
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
                    (contentType.equalsIgnoreCase("image/jpg") ||
                            contentType.equalsIgnoreCase("image/jpeg") ||
                            contentType.equalsIgnoreCase("image/tiff"))) {
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

        if (Files.exists(newPath)) {
            String pathName = path.getFileName().toString();
            String[] pathNames = pathName.split("\\.");
            pathNames[pathNames.length - 2] = pathNames[pathNames.length - 2] + RandomUtils.nextInt(0, MAX_VALUE);
            newPath = new File(outputDirectory + separator + join(pathNames)).toPath();
        }

        move(path, newPath);
    }
}
