package com.benjaminsproule.mediaorganiser.dao;

import com.benjaminsproule.mediaorganiser.test.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static com.benjaminsproule.mediaorganiser.test.FileResource.getFile;
import static java.io.File.separator;
import static java.nio.file.Files.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MediaDaoITest {
    private MediaDao mediaDao;
    private Path staticPath;
    private Path sourceImagePath;
    private Path destinationPath;
    private String destinationDirectory;

    @BeforeEach
    public void setup() throws URISyntaxException, IOException {
        mediaDao = new MediaDao();
    }

    @Test
    public void testGetFilesThrowsIllegalArgumentExceptionIfInputDirectoryIsNull() throws IOException {
        assertThrows(IllegalArgumentException.class, () -> mediaDao.getFiles(null));
    }

    @Test
    public void testGetFilesThrowsIllegalArgumentExceptionIfInputDirectoryIsEmpty() throws IOException {
        assertThrows(IllegalArgumentException.class, () -> mediaDao.getFiles(""));
    }

    @Test
    public void testGetFilesThrowsIllegalArgumentExceptionIfInputDirectoryIsBlank() throws IOException {
        assertThrows(IllegalArgumentException.class, () -> mediaDao.getFiles(" "));
    }

    @Test
    public void testGetFilesThrowsIllegalArgumentExceptionIfInputDirectoryDoesNotExist() throws IOException {
        assertThrows(IllegalArgumentException.class, () -> mediaDao.getFiles("doesNotExist"));
    }

    @Test
    public void testGetFilesThrowsIllegalArgumentExceptionIfInputDirectoryIsNotADirectory() throws IOException {
        Path tempPath = createTempDirectory("test");
        Path path = createTempFile(tempPath, "test", ".jpg");
        assertThrows(IllegalArgumentException.class, () -> mediaDao.getFiles(path.toString()));
    }

    @Test
    public void testGetFilesReturnsEmptyListIfNoFilesInDirectory() throws IOException {
        List<Path> paths = mediaDao.getFiles(createTempDirectory("test").toString());
        assertThat(paths.size(), is(0));
    }

    @Test
    public void testGetFilesDoesNotReturnNonImageFiles() throws IOException {
        Path tempPath = createTempDirectory("test");
        createTempFile(tempPath, "test", ".txt");
        List<Path> paths = mediaDao.getFiles(tempPath.toString());
        assertThat(paths.size(), is(0));
    }

    @Test
    public void testGetFilesDoesNotReturnPngFile() throws IOException {
        Path tempPath = createTempDirectory("test");
        createTempFile(tempPath, "test", ".png");
        List<Path> paths = mediaDao.getFiles(tempPath.toString());
        assertThat(paths.size(), is(0));
    }

    @Test
    public void testGetFilesDoesNotReturnGifFile() throws IOException {
        Path tempPath = createTempDirectory("test");
        createTempFile(tempPath, "test", ".gif");
        List<Path> paths = mediaDao.getFiles(tempPath.toString());
        assertThat(paths.size(), is(0));
    }

    @Test
    public void testGetFilesDoesNotReturnBmpFile() throws IOException {
        Path tempPath = createTempDirectory("test");
        createTempFile(tempPath, "test", ".bmp");
        List<Path> paths = mediaDao.getFiles(tempPath.toString());
        assertThat(paths.size(), is(0));
    }

    @Test
    public void testGetFilesReturnsJpgFile() throws IOException {
        Path tempPath = createTempDirectory("test");
        Path path = createTempFile(tempPath, "test", ".jpg");

        List<Path> paths = mediaDao.getFiles(tempPath.toString());
        assertThat(paths.size(), is(1));
        assertThat(paths.get(0), is(path));
    }

    @Test
    public void testGetFilesReturnsJpegFile() throws IOException {
        Path tempPath = createTempDirectory("test");
        Path path = createTempFile(tempPath, "test", ".jpg");

        List<Path> paths = mediaDao.getFiles(tempPath.toString());
        assertThat(paths.size(), is(1));
        assertThat(paths.get(0), is(path));
    }

    @Test
    public void testGetFilesReturnsTifFile() throws IOException {
        Path tempPath = createTempDirectory("test");
        Path path = createTempFile(tempPath, "test", ".tif");

        List<Path> paths = mediaDao.getFiles(tempPath.toString());
        assertThat(paths.size(), is(1));
        assertThat(paths.get(0), is(path));
    }

    @Test
    public void testGetFilesReturnsTiffFile() throws IOException {
        Path tempPath = createTempDirectory("test");
        Path path = createTempFile(tempPath, "test", ".tiff");

        List<Path> paths = mediaDao.getFiles(tempPath.toString());
        assertThat(paths.size(), is(1));
        assertThat(paths.get(0), is(path));
    }

    @Test
    public void testGetFilesReturnsHeicFile() throws IOException {
        Path tempPath = createTempDirectory("test");
        Path path = createTempFile(tempPath, "test", ".heic");

        List<Path> paths = mediaDao.getFiles(tempPath.toString());
        assertThat(paths.size(), is(1));
        assertThat(paths.get(0), is(path));
    }

    @Test
    public void testGetFilesReturnsHeifFile() throws IOException {
        Path tempPath = createTempDirectory("test");
        Path path = createTempFile(tempPath, "test", ".heif");

        List<Path> paths = mediaDao.getFiles(tempPath.toString());
        assertThat(paths.size(), is(1));
        assertThat(paths.get(0), is(path));
    }

    @Test
    public void testGetFilesReturnsAviFile() throws IOException {
        Path tempPath = createTempDirectory("test");
        Path path = createTempFile(tempPath, "test", ".avi");

        List<Path> paths = mediaDao.getFiles(tempPath.toString());
        assertThat(paths.size(), is(1));
        assertThat(paths.get(0), is(path));
    }

    @Test
    public void testGetFilesReturnsMovFile() throws IOException {
        Path tempPath = createTempDirectory("test");
        Path path = createTempFile(tempPath, "test", ".mov");

        List<Path> paths = mediaDao.getFiles(tempPath.toString());
        assertThat(paths.size(), is(1));
        assertThat(paths.get(0), is(path));
    }

    @Test
    public void testGetFilesReturnsMp4File() throws IOException {
        Path tempPath = createTempDirectory("test");
        Path path = createTempFile(tempPath, "test", ".mp4");

        List<Path> paths = mediaDao.getFiles(tempPath.toString());
        assertThat(paths.size(), is(1));
        assertThat(paths.get(0), is(path));
    }

    @Test
    public void testGetFilesReturnsFilesWithinSubDirectories() throws IOException {
        Path tempPath = createTempDirectory("test");
        Path directory = createTempDirectory(tempPath, "test");
        Path path = createTempFile(directory, "test", ".jpg");

        List<Path> paths = mediaDao.getFiles(directory.toString());
        assertThat(paths.size(), is(1));
        assertThat(paths.get(0), is(path));
    }

    @Test
    public void testSaveFileThrowsIllegalArgumentExceptionIfOutputDirectoryIsNull() throws IOException {
        Path tempPath = createTempDirectory("test");
        Path path = createTempFile(tempPath, "test", ".jpg");
        assertThrows(IllegalArgumentException.class, () -> mediaDao.saveFile(null, path));
    }

    @Test
    public void testSaveFileThrowsIllegalArgumentExceptionIfOutputDirectoryIsEmpty() throws IOException {
        Path tempPath = createTempDirectory("test");
        Path path = createTempFile(tempPath, "test", ".jpg");
        assertThrows(IllegalArgumentException.class, () -> mediaDao.saveFile("", path));
    }

    @Test
    public void testSaveFileThrowsIllegalArgumentExceptionIfOutputDirectoryIsBlank() throws IOException {
        Path tempPath = createTempDirectory("test");
        Path path = createTempFile(tempPath, "test", ".jpg");
        assertThrows(IllegalArgumentException.class, () -> mediaDao.saveFile(" ", path));
    }

    @Test
    public void testSaveFileThrowsIllegalArgumentExceptionIfFileIsNull() throws IOException {
        assertThrows(IllegalArgumentException.class,
            () -> mediaDao.saveFile(createTempDirectory("test").toString(), null));
    }

    @Test
    public void testSaveFileThrowsIllegalArgumentExceptionIfOutputDirectoryIsNotADirectory() throws IOException {
        Path tempPath = createTempDirectory("test");
        Path path = createTempFile(tempPath, "test", ".jpg");
        assertThrows(IllegalArgumentException.class, () -> mediaDao.saveFile(path.toString(), path));
    }

    @Test
    public void testSaveFileCreatesFile() throws IOException, URISyntaxException {
        createImageInTempDirectory();
        mediaDao.saveFile(destinationDirectory, sourceImagePath);
        assertThat(exists(destinationPath), is(true));
        checkFilesExist(new File(destinationDirectory + separator + "image.jpg").toPath());
    }

    @Test
    public void testSaveFileCreatesDirectoriesIfTheyDoNotExist() throws IOException, URISyntaxException {
        createImageInTempDirectory();
        mediaDao.saveFile(destinationDirectory + separator + "directory", sourceImagePath);
        assertThat(exists(new File(destinationDirectory + separator + "directory").toPath()), is(true));
    }

    @Test
    public void testSaveFileDeletesOldFile() throws IOException, URISyntaxException {
        createImageInTempDirectory();
        mediaDao.saveFile(destinationDirectory, sourceImagePath);
        assertThat(notExists(sourceImagePath), is(true));
    }

    @Test
    public void testSaveFileCreatesNewFileWithTheSameDataAsTheOldFile() throws IOException, URISyntaxException {
        createImageInTempDirectory();
        mediaDao.saveFile(destinationDirectory, sourceImagePath);
        assertThat(getAttribute(new File(destinationDirectory + separator + "image.jpg").toPath(), "size"),
            is(getAttribute(staticPath, "size")));
    }

    @Test
    public void testSaveFileRenamesFileIfFileInOutputAlreadyHasThatName() throws IOException, URISyntaxException {
        createImageInTempDirectory();
        Path preCreatedFile = addFileToDestination("image.jpg");

        mediaDao.saveFile(destinationDirectory, sourceImagePath);

        checkFilesExist(preCreatedFile, new File(destinationDirectory + separator + "image0.jpg").toPath());
    }

    @Test
    public void testSaveFileIncrementsRenamedFileIndexIfFileInOutputAlreadyHasThatName() throws IOException, URISyntaxException {
        createImageInTempDirectory();
        Path preCreatedFile = addFileToDestination("image.jpg");
        Path preCreatedFileIncremented = addFileToDestination("image0.jpg");

        mediaDao.saveFile(destinationDirectory, sourceImagePath);

        checkFilesExist(preCreatedFile, preCreatedFileIncremented,
            new File(destinationDirectory + separator + "image1.jpg").toPath());
    }

    private Path addFileToDestination(String fileName) throws IOException {
        Path preCreatedFile = new File(destinationDirectory + separator + fileName).toPath();
        copy(staticPath, preCreatedFile);
        return preCreatedFile;
    }

    private void checkFilesExist(Path... files) throws IOException {
        assertThat(Files.list(destinationPath).toArray().length, is(files.length));
        for (Path file : files) {
            assertThat(exists(file), is(true));
        }
    }

    private void createImageInTempDirectory() throws IOException, URISyntaxException {
        staticPath = getFile("image.jpg").toPath();
        sourceImagePath = new File(
            Files.createTempDirectory(Constants.SOURCE_PATH).toString() + separator + "image.jpg").toPath();
        destinationPath = Files.createTempDirectory(Constants.DESTINATION_PATH);
        destinationDirectory = destinationPath.toString();
        copy(staticPath, sourceImagePath);
    }
}
