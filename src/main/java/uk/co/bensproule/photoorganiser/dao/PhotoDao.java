package uk.co.bensproule.photoorganiser.dao;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
public class PhotoDao {
    public List<File> getFiles(String inputDirectory) throws IOException {
        if (isBlank(inputDirectory)) {
            throw new IllegalArgumentException("An input directory should be provided");
        }

        File directory = new File(inputDirectory);
        if (Files.notExists(directory.toPath())) {
            throw new IllegalArgumentException("The input directory does not exist");
        }

        if (!Files.isDirectory(directory.toPath())) {
            throw new IllegalArgumentException("The input directory is not a directory");
        }

        List<File> images = new ArrayList<>();
        getFiles(directory, images);

        return images;
    }

    private void getFiles(File directory, List<File> images) throws IOException {
        File[] files = directory.listFiles();
        // TODO: Figure out how to test this
        if (files == null) {
            throw new IllegalArgumentException("Failed to get the list of files");
        }

        for (File file : files) {
            if (Files.isDirectory(file.toPath())) {
                getFiles(file, images);
                continue;
            }

            if (Files.probeContentType(file.toPath()).startsWith("image/")) {
                images.add(file);
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
        if (Files.isRegularFile(directory)) {
            throw new IllegalArgumentException("The output directory is a file");
        }

        createDirectories(directory);
        Path newFile = new File(outputDirectory + "/" + path.getFileName().toString()).toPath();
        Files.move(path, newFile);
    }

    private void createDirectories(Path path) throws IOException {
        if (Files.exists(path)) {
            return;
        }

        Path parentPath = path.getParent();
        if (Files.notExists(parentPath)) {
            createDirectories(parentPath);
        }

        Files.createDirectory(path);
    }
}
