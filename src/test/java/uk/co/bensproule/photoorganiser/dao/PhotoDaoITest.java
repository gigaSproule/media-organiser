package uk.co.bensproule.photoorganiser.dao;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static java.io.File.separator;
import static java.nio.file.Files.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static uk.co.bensproule.photoorganiser.test.Constants.*;

public class PhotoDaoITest {
    private PhotoDao photoDao;
    private Path staticPath;
    private Path sourceImagePath;
    private Path destinationPath;
    private String destinationDirectory;

    @Before
    public void setup() throws URISyntaxException, IOException {
        photoDao = new PhotoDao();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetFilesThrowsIllegalArgumentExceptionIfInputDirectoryIsNull() throws IOException {
        photoDao.getFiles(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetFilesThrowsIllegalArgumentExceptionIfInputDirectoryIsEmpty() throws IOException {
        photoDao.getFiles("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetFilesThrowsIllegalArgumentExceptionIfInputDirectoryIsBlank() throws IOException {
        photoDao.getFiles(" ");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetFilesThrowsIllegalArgumentExceptionIfInputDirectoryDoesNotExist() throws IOException {
        photoDao.getFiles("doesNotExist");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetFilesThrowsIllegalArgumentExceptionIfInputDirectoryIsNotADirectory() throws IOException {
        Path tempPath = createTempDirectory("test");
        Path path = createTempFile(tempPath, "test", ".jpg");
        photoDao.getFiles(path.toString());
    }

    @Test
    public void testGetFilesReturnsEmptyListIfNoFilesInDirectory() throws IOException {
        List<Path> paths = photoDao.getFiles(createTempDirectory("test").toString());
        assertThat(paths.size(), is(0));
    }

    @Test
    public void testGetFilesDoesNotReturnNonImageFiles() throws IOException {
        Path tempPath = createTempDirectory("test");
        createTempFile(tempPath, "test", ".txt");
        List<Path> paths = photoDao.getFiles(tempPath.toString());
        assertThat(paths.size(), is(0));
    }

    @Test
    public void testGetFilesDoesNotReturnPngFile() throws IOException {
        Path tempPath = createTempDirectory("test");
        createTempFile(tempPath, "test", ".png");
        List<Path> paths = photoDao.getFiles(tempPath.toString());
        assertThat(paths.size(), is(0));
    }

    @Test
    public void testGetFilesDoesNotReturnGifFile() throws IOException {
        Path tempPath = createTempDirectory("test");
        createTempFile(tempPath, "test", ".gif");
        List<Path> paths = photoDao.getFiles(tempPath.toString());
        assertThat(paths.size(), is(0));
    }

    @Test
    public void testGetFilesDoesNotReturnBmpFile() throws IOException {
        Path tempPath = createTempDirectory("test");
        createTempFile(tempPath, "test", ".bmp");
        List<Path> paths = photoDao.getFiles(tempPath.toString());
        assertThat(paths.size(), is(0));
    }

    @Test
    public void testGetFilesReturnsJpgFile() throws IOException {
        Path tempPath = createTempDirectory("test");
        Path path = createTempFile(tempPath, "test", ".jpg");

        List<Path> paths = photoDao.getFiles(tempPath.toString());
        assertThat(paths.size(), is(1));
        assertThat(paths.get(0), is(path));
    }

    @Test
    public void testGetFilesReturnsJpegFile() throws IOException {
        Path tempPath = createTempDirectory("test");
        Path path = createTempFile(tempPath, "test", ".jpg");

        List<Path> paths = photoDao.getFiles(tempPath.toString());
        assertThat(paths.size(), is(1));
        assertThat(paths.get(0), is(path));
    }

    @Test
    public void testGetFilesReturnsTifFile() throws IOException {
        Path tempPath = createTempDirectory("test");
        Path path = createTempFile(tempPath, "test", ".tif");

        List<Path> paths = photoDao.getFiles(tempPath.toString());
        assertThat(paths.size(), is(1));
        assertThat(paths.get(0), is(path));
    }

    @Test
    public void testGetFilesReturnsTiffFile() throws IOException {
        Path tempPath = createTempDirectory("test");
        Path path = createTempFile(tempPath, "test", ".tiff");

        List<Path> paths = photoDao.getFiles(tempPath.toString());
        assertThat(paths.size(), is(1));
        assertThat(paths.get(0), is(path));
    }

    @Test
    public void testGetFilesReturnsFilesWithinSubDirectories() throws IOException {
        Path tempPath = createTempDirectory("test");
        Path directory = createTempDirectory(tempPath, "test");
        Path path = createTempFile(directory, "test", ".jpg");

        List<Path> paths = photoDao.getFiles(directory.toString());
        assertThat(paths.size(), is(1));
        assertThat(paths.get(0), is(path));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSaveFileThrowsIllegalArgumentExceptionIfOutputDirectoryIsNull() throws IOException {
        Path tempPath = createTempDirectory("test");
        Path path = createTempFile(tempPath, "test", ".jpg");
        photoDao.saveFile(null, path);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSaveFileThrowsIllegalArgumentExceptionIfOutputDirectoryIsEmpty() throws IOException {
        Path tempPath = createTempDirectory("test");
        Path path = createTempFile(tempPath, "test", ".jpg");
        photoDao.saveFile("", path);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSaveFileThrowsIllegalArgumentExceptionIfOutputDirectoryIsBlank() throws IOException {
        Path tempPath = createTempDirectory("test");
        Path path = createTempFile(tempPath, "test", ".jpg");
        photoDao.saveFile(" ", path);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSaveFileThrowsIllegalArgumentExceptionIfFileIsNull() throws IOException {
        photoDao.saveFile(createTempDirectory("test").toString(), null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSaveFileThrowsIllegalArgumentExceptionIfOutputDirectoryIsNotADirectory() throws IOException {
        Path tempPath = createTempDirectory("test");
        Path path = createTempFile(tempPath, "test", ".jpg");
        photoDao.saveFile(path.toString(), path);
    }

    @Test
    public void testSaveFileCreatesFile() throws IOException {
        createImageInTempDirectory();
        photoDao.saveFile(destinationDirectory, sourceImagePath);
        assertThat(exists(destinationPath), is(true));
        checkFilesExist(new File(destinationDirectory + separator + "image.jpg").toPath());
    }

    @Test
    public void testSaveFileCreatesDirectoriesIfTheyDoNotExist() throws IOException {
        createImageInTempDirectory();
        photoDao.saveFile(destinationDirectory + separator + "directory", sourceImagePath);
        assertThat(exists(new File(destinationDirectory + separator + "directory").toPath()), is(true));
    }

    @Test
    public void testSaveFileDeletesOldFile() throws IOException {
        createImageInTempDirectory();
        photoDao.saveFile(destinationDirectory, sourceImagePath);
        assertThat(notExists(sourceImagePath), is(true));
    }

    @Test
    public void testSaveFileCreatesNewFileWithTheSameDataAsTheOldFile() throws IOException {
        createImageInTempDirectory();
        photoDao.saveFile(destinationDirectory, sourceImagePath);
        assertThat(getAttribute(new File(destinationDirectory + separator + "image.jpg").toPath(), "size"), is(getAttribute(staticPath, "size")));
    }

    @Test
    public void testSaveFileRenamesFileIfFileInOutputAlreadyHasThatName() throws IOException {
        createImageInTempDirectory();
        Path preCreatedFile = addFileToDestination("image.jpg");

        photoDao.saveFile(destinationDirectory, sourceImagePath);

        checkFilesExist(preCreatedFile, new File(destinationDirectory + separator + "image0.jpg").toPath());
    }

    @Test
    public void testSaveFileIncrementsRenamedFileIndexIfFileInOutputAlreadyHasThatName() throws IOException {
        createImageInTempDirectory();
        Path preCreatedFile = addFileToDestination("image.jpg");
        Path preCreatedFileIncremented = addFileToDestination("image0.jpg");

        photoDao.saveFile(destinationDirectory, sourceImagePath);

        checkFilesExist(preCreatedFile, preCreatedFileIncremented, new File(destinationDirectory + separator + "image1.jpg").toPath());
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

    private void createImageInTempDirectory() throws IOException {
        staticPath = new File(RESOURCES_DIRECTORY + separator + "image.jpg").toPath();
        sourceImagePath = new File(createTempDirectory(SOURCE_PATH).toString() + separator + "image.jpg").toPath();
        destinationPath = createTempDirectory(DESTINATION_PATH);
        destinationDirectory = destinationPath.toString();
        copy(staticPath, sourceImagePath);
    }
}