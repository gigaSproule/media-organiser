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
import static uk.co.bensproule.photoorganiser.test.Constants.*;

public class PhotoDaoITest {
    private Path tempPath;
    private PhotoDao photoDao;
    private Path sourcePath;
    private String sourceDirectory;
    private Path destinationPath;
    private String destinationDirectory;

    @Before
    public void setup() throws URISyntaxException, IOException {
        tempPath = createTempDirectory("test");
        photoDao = new PhotoDao();
        walkFileTree(tempPath, new DeleteFileVisitor());
        sourcePath = createTempDirectory(SOURCE_PATH);
        sourceDirectory = sourcePath.toString();
        destinationPath = createTempDirectory(DESTINATION_PATH);
        destinationDirectory = destinationPath.toString();
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
        Path sourceImagePath = new File(sourceDirectory + separator + "image.jpg").toPath();
        Path staticPath = new File(RESOURCES_DIRECTORY + separator + "image.jpg").toPath();
        copy(staticPath, sourceImagePath);

        photoDao.saveFile(destinationDirectory, sourceImagePath);

        assertThat(exists(destinationPath), is(true));
    }

    @Test
    public void testSaveFileCreatesDirectoriesIfTheyDoNotExist() throws IOException {
        Path staticPath = new File(RESOURCES_DIRECTORY + separator + "image.jpg").toPath();
        Path sourceImagePath = new File(sourceDirectory + separator + "image.jpg").toPath();
        copy(staticPath, sourceImagePath);

        photoDao.saveFile(destinationDirectory + separator + "directory", sourceImagePath);
        assertThat(exists(new File(destinationDirectory + separator + "directory").toPath()), is(true));
    }

    @Test
    public void testSaveFileDeletesOldFile() throws IOException {
        Path staticPath = new File(RESOURCES_DIRECTORY + separator + "image.jpg").toPath();
        Path sourceImagePath = new File(sourceDirectory + separator + "image.jpg").toPath();
        copy(staticPath, sourceImagePath);

        photoDao.saveFile(destinationDirectory, sourceImagePath);

        assertThat(notExists(sourceImagePath), is(true));
    }

    @Test
    public void testSaveFileCreatesNewFileWithTheSameDataAsTheOldFile() throws IOException {
        Path staticPath = new File(RESOURCES_DIRECTORY + separator + "image.jpg").toPath();
        Path sourceImagePath = new File(sourceDirectory + separator + "image.jpg").toPath();
        copy(staticPath, sourceImagePath);

        photoDao.saveFile(destinationDirectory, sourceImagePath);

        assertThat(getAttribute(new File(destinationDirectory + separator + "image.jpg").toPath(), "size"), is(getAttribute(staticPath, "size")));
    }

    @Test
    public void testSaveFileRenamesFileIfFileInOutputAlreadyHasThatName() throws IOException {
        Path sourceImagePath = new File(sourceDirectory + separator + "image.jpg").toPath();
        Path staticPath = new File(RESOURCES_DIRECTORY + separator + "image.jpg").toPath();

        copy(staticPath, sourceImagePath);
        Path preCreatedFile = new File(destinationDirectory + separator + "image.jpg").toPath();
        copy(staticPath, preCreatedFile);

        photoDao.saveFile(destinationDirectory, sourceImagePath);

        List<Path> createdFiles = new ArrayList<>();
        Files.list(destinationPath).forEach(createdFiles::add);

        assertThat(createdFiles, hasSize(2));
        assertThat(createdFiles.get(0), is(preCreatedFile));
        assertThat(createdFiles.get(1), is(new File(destinationDirectory + separator + "image0.jpg").toPath()));
    }

    @Test
    public void testSaveFileIncrementsRenamedFileIndexIfFileInOutputAlreadyHasThatName() throws IOException {
        Path sourceImagePath = new File(sourceDirectory + separator + "image.jpg").toPath();
        Path staticPath = new File(RESOURCES_DIRECTORY + separator + "image.jpg").toPath();

        copy(staticPath, sourceImagePath);
        Path preCreatedFile = new File(destinationDirectory + separator + "image.jpg").toPath();
        copy(staticPath, preCreatedFile);
        Path preCreatedFileIncremented = new File(destinationDirectory + separator + "image0.jpg").toPath();
        copy(staticPath, preCreatedFileIncremented);

        photoDao.saveFile(destinationDirectory, sourceImagePath);

        List<Path> createdFiles = new ArrayList<>();
        Files.list(destinationPath).forEach(createdFiles::add);

        assertThat(createdFiles, hasSize(3));
        assertThat(createdFiles.get(0), is(preCreatedFile));
        assertThat(createdFiles.get(1), is(preCreatedFileIncremented));
        assertThat(createdFiles.get(2), is(new File(destinationDirectory + separator + "image1.jpg").toPath()));
    }

}