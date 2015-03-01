package uk.co.bensproule.photoorganiser.dao;

import org.junit.Before;
import org.junit.Test;
import uk.co.bensproule.photoorganiser.test.DeleteFileVisitor;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static java.io.File.separator;
import static java.nio.file.Files.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

public class PhotoDaoITest {
    private static final String RESOURCES_DIRECTORY = System.getProperty("user.dir") + separator +
            "src" + separator + "test" + separator + "resources";
    public static final String DESTINATION_PATH = "test-destination";
    public static final String SOURCE_PATH = "test-source";
    private Path tempPath;
    private PhotoDao photoDao;

    @Before
    public void setup() throws URISyntaxException, IOException {
        tempPath = createTempDirectory("test");
        photoDao = new PhotoDao();
        walkFileTree(tempPath, new DeleteFileVisitor());
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
        createTempFile(tempPath, "test", ".txt");
        List<Path> paths = photoDao.getFiles(tempPath.toString());
        assertThat(paths.size(), is(0));
    }

    @Test
    public void testGetFilesDoesNotReturnPngFile() throws IOException {
        createTempFile(tempPath, "test", ".png");
        List<Path> paths = photoDao.getFiles(tempPath.toString());
        assertThat(paths.size(), is(0));
    }

    @Test
    public void testGetFilesDoesNotReturnGifFile() throws IOException {
        createTempFile(tempPath, "test", ".gif");
        List<Path> paths = photoDao.getFiles(tempPath.toString());
        assertThat(paths.size(), is(0));
    }

    @Test
    public void testGetFilesDoesNotReturnBmpFile() throws IOException {
        createTempFile(tempPath, "test", ".bmp");
        List<Path> paths = photoDao.getFiles(tempPath.toString());
        assertThat(paths.size(), is(0));
    }

    @Test
    public void testGetFilesReturnsJpgFile() throws IOException {
        Path path = createTempFile(tempPath, "test", ".jpg");

        List<Path> paths = photoDao.getFiles(tempPath.toString());
        assertThat(paths.size(), is(1));
        assertThat(paths.get(0), is(path));
    }

    @Test
    public void testGetFilesReturnsJpegFile() throws IOException {
        Path path = createTempFile(tempPath, "test", ".jpg");

        List<Path> paths = photoDao.getFiles(tempPath.toString());
        assertThat(paths.size(), is(1));
        assertThat(paths.get(0), is(path));
    }

    @Test
    public void testGetFilesReturnsTifFile() throws IOException {
        Path path = createTempFile(tempPath, "test", ".tif");

        List<Path> paths = photoDao.getFiles(tempPath.toString());
        assertThat(paths.size(), is(1));
        assertThat(paths.get(0), is(path));
    }

    @Test
    public void testGetFilesReturnsTiffFile() throws IOException {
        Path path = createTempFile(tempPath, "test", ".tiff");

        List<Path> paths = photoDao.getFiles(tempPath.toString());
        assertThat(paths.size(), is(1));
        assertThat(paths.get(0), is(path));
    }

    @Test
    public void testGetFilesReturnsFilesWithinSubDirectories() throws IOException {
        Path directory = createTempDirectory(tempPath, "test");
        Path path = createTempFile(directory, "test", ".jpg");

        List<Path> paths = photoDao.getFiles(directory.toString());
        assertThat(paths.size(), is(1));
        assertThat(paths.get(0), is(path));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSaveFileThrowsIllegalArgumentExceptionIfOutputDirectoryIsNull() throws IOException {
        Path path = createTempFile(tempPath, "test", ".jpg");
        photoDao.saveFile(null, path);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSaveFileThrowsIllegalArgumentExceptionIfOutputDirectoryIsEmpty() throws IOException {
        Path path = createTempFile(tempPath, "test", ".jpg");
        photoDao.saveFile("", path);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSaveFileThrowsIllegalArgumentExceptionIfOutputDirectoryIsBlank() throws IOException {
        Path path = createTempFile(tempPath, "test", ".jpg");
        photoDao.saveFile(" ", path);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSaveFileThrowsIllegalArgumentExceptionIfFileIsNull() throws IOException {
        photoDao.saveFile(createTempDirectory("test").toString(), null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSaveFileThrowsIllegalArgumentExceptionIfOutputDirectoryIsNotADirectory() throws IOException {
        Path path = createTempFile(tempPath, "test", ".jpg");
        photoDao.saveFile(path.toString(), path);
    }

    @Test
    public void testSaveFileCreatesFile() throws IOException {
        Path sourcePath = new File(createTempDirectory(SOURCE_PATH).toString() + separator + "image.jpg").toPath();
        Path staticPath = new File(RESOURCES_DIRECTORY + separator + "image.jpg").toPath();
        copy(staticPath, sourcePath);

        Path destinationPath = createTempDirectory(DESTINATION_PATH);

        photoDao.saveFile(destinationPath.toString(), sourcePath);

        assertThat(exists(destinationPath), is(true));
    }

    @Test
    public void testSaveFileCreatesDirectoriesIfTheyDoNotExist() throws IOException {
        Path staticPath = new File(RESOURCES_DIRECTORY + separator + "image.jpg").toPath();
        Path sourcePath = new File(createTempDirectory(SOURCE_PATH).toString() + separator + "image.jpg").toPath();
        copy(staticPath, sourcePath);

        Path destinationPath = createTempDirectory(DESTINATION_PATH);

        photoDao.saveFile(destinationPath.toString() + separator + "directory", sourcePath);
        assertThat(exists(new File(destinationPath.toString() + separator + "directory").toPath()), is(true));
    }

    @Test
    public void testSaveFileDeletesOldFile() throws IOException {
        Path staticPath = new File(RESOURCES_DIRECTORY + separator + "image.jpg").toPath();
        Path sourcePath = new File(createTempDirectory(SOURCE_PATH).toString() + separator + "image.jpg").toPath();
        copy(staticPath, sourcePath);

        Path destinationPath = createTempDirectory(DESTINATION_PATH);

        photoDao.saveFile(destinationPath.toString(), sourcePath);

        assertThat(notExists(sourcePath), is(true));
    }

    @Test
    public void testSaveFileCreatesNewFileWithTheSameDataAsTheOldFile() throws IOException {
        Path staticPath = new File(RESOURCES_DIRECTORY + separator + "image.jpg").toPath();
        Path sourcePath = new File(createTempDirectory(SOURCE_PATH).toString() + separator + "image.jpg").toPath();
        copy(staticPath, sourcePath);

        Path destinationPath = createTempDirectory(DESTINATION_PATH);

        photoDao.saveFile(destinationPath.toString(), sourcePath);

        assertThat(getAttribute(new File(destinationPath.toString() + separator + "image.jpg").toPath(), "size"), is(getAttribute(staticPath, "size")));
    }

    @Test
    public void testSaveFileRenamesFileIfFileInOutputAlreadyHasThatName() throws IOException {
        Path sourcePath = new File(createTempDirectory(SOURCE_PATH).toString() + separator + "image.jpg").toPath();
        Path staticPath = new File(RESOURCES_DIRECTORY + separator + "image.jpg").toPath();
        Path destinationPath = createTempDirectory(DESTINATION_PATH);

        copy(staticPath, sourcePath);
        Path preCreatedFile = new File(destinationPath.toString() + separator + "image.jpg").toPath();
        copy(staticPath, preCreatedFile);

        photoDao.saveFile(destinationPath.toString(), sourcePath);

        List<Path> createdFiles = new ArrayList<>();
        Files.list(destinationPath).forEach(createdFiles::add);

        assertThat(createdFiles, hasSize(2));
        assertThat(createdFiles.get(0), is(preCreatedFile));
        assertThat(createdFiles.get(1), is(new File(destinationPath.toString() + separator + "image0.jpg").toPath()));
    }

    @Test
    public void testSaveFileIncrementsRenamedFileIndexIfFileInOutputAlreadyHasThatName() throws IOException {
        Path sourcePath = new File(createTempDirectory(SOURCE_PATH).toString() + separator + "image.jpg").toPath();
        Path staticPath = new File(RESOURCES_DIRECTORY + separator + "image.jpg").toPath();
        Path destinationPath = createTempDirectory(DESTINATION_PATH);

        copy(staticPath, sourcePath);
        Path preCreatedFile = new File(destinationPath.toString() + separator + "image.jpg").toPath();
        copy(staticPath, preCreatedFile);
        Path preCreatedFileIncremented = new File(destinationPath.toString() + separator + "image0.jpg").toPath();
        copy(staticPath, preCreatedFileIncremented);

        photoDao.saveFile(destinationPath.toString(), sourcePath);

        List<Path> createdFiles = new ArrayList<>();
        Files.list(destinationPath).forEach(createdFiles::add);

        assertThat(createdFiles, hasSize(3));
        assertThat(createdFiles.get(0), is(preCreatedFile));
        assertThat(createdFiles.get(1), is(preCreatedFileIncremented));
        assertThat(createdFiles.get(2), is(new File(destinationPath.toString() + separator + "image1.jpg").toPath()));
    }

}