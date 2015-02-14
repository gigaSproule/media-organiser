package uk.co.bensproule.photoorganiser.dao;

import org.junit.Before;
import org.junit.Test;
import uk.co.bensproule.photoorganiser.test.DeleteFileVisitor;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class PhotoDaoITest {
    public static final String DYNAMIC_RESOURCES_DIRECTORY = System.getProperty("user.dir") + "/src/test/resources/dynamic";
    public static final String STATIC_RESOURCES_DIRECTORY = System.getProperty("user.dir") + "/src/test/resources/static";
    private PhotoDao photoDao;

    @Before
    public void setup() throws URISyntaxException, IOException {
        photoDao = new PhotoDao();
        Path directory = new File(DYNAMIC_RESOURCES_DIRECTORY).toPath();

        DeleteFileVisitor pf = new DeleteFileVisitor();
        Files.walkFileTree(directory, pf);
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
        File file = new File(DYNAMIC_RESOURCES_DIRECTORY + "/file.jpg");
        Files.createFile(file.toPath());

        photoDao.getFiles(file.getAbsolutePath());
    }

    @Test
    public void testGetFilesReturnsEmptyListIfNoFilesInDirectory() throws IOException {
        List<File> files = photoDao.getFiles(DYNAMIC_RESOURCES_DIRECTORY);
        assertThat(files.size(), is(0));
    }

    @Test
    public void testGetFilesDoesNotReturnNonImageFiles() throws IOException {
        File file = new File(DYNAMIC_RESOURCES_DIRECTORY + "/file.txt");
        Files.createFile(file.toPath());

        List<File> files = photoDao.getFiles(DYNAMIC_RESOURCES_DIRECTORY);
        assertThat(files.size(), is(0));
    }

    @Test
    public void testGetFilesReturnsJpgFile() throws IOException {
        File file = new File(DYNAMIC_RESOURCES_DIRECTORY + "/file.jpg");
        Files.createFile(file.toPath());

        List<File> files = photoDao.getFiles(DYNAMIC_RESOURCES_DIRECTORY);
        assertThat(files.size(), is(1));
        assertThat(files.get(0), is(file));
    }

    @Test
    public void testGetFilesReturnsJpegFile() throws IOException {
        File file = new File(DYNAMIC_RESOURCES_DIRECTORY + "/file.jpeg");
        Files.createFile(file.toPath());

        List<File> files = photoDao.getFiles(DYNAMIC_RESOURCES_DIRECTORY);
        assertThat(files.size(), is(1));
        assertThat(files.get(0), is(file));
    }

    @Test
    public void testGetFilesReturnsPngFile() throws IOException {
        File file = new File(DYNAMIC_RESOURCES_DIRECTORY + "/file.png");
        Files.createFile(file.toPath());

        List<File> files = photoDao.getFiles(DYNAMIC_RESOURCES_DIRECTORY);
        assertThat(files.size(), is(1));
        assertThat(files.get(0), is(file));
    }

    @Test
    public void testGetFilesReturnsGifFile() throws IOException {
        File file = new File(DYNAMIC_RESOURCES_DIRECTORY + "/file.gif");
        Files.createFile(file.toPath());

        List<File> files = photoDao.getFiles(DYNAMIC_RESOURCES_DIRECTORY);
        assertThat(files.size(), is(1));
        assertThat(files.get(0), is(file));
    }

    @Test
    public void testGetFilesReturnsBmpFile() throws IOException {
        File file = new File(DYNAMIC_RESOURCES_DIRECTORY + "/file.bmp");
        Files.createFile(file.toPath());

        List<File> files = photoDao.getFiles(DYNAMIC_RESOURCES_DIRECTORY);
        assertThat(files.size(), is(1));
        assertThat(files.get(0), is(file));
    }

    @Test
    public void testGetFilesReturnsTifFile() throws IOException {
        File file = new File(DYNAMIC_RESOURCES_DIRECTORY + "/file.tif");
        Files.createFile(file.toPath());

        List<File> files = photoDao.getFiles(DYNAMIC_RESOURCES_DIRECTORY);
        assertThat(files.size(), is(1));
        assertThat(files.get(0), is(file));
    }

    @Test
    public void testGetFilesReturnsFilesWithinSubDirectories() throws IOException {
        String directory = DYNAMIC_RESOURCES_DIRECTORY + "/directory";
        Path createdDirectory = new File(directory).toPath();
        Files.createDirectory(createdDirectory);

        File file = new File(directory + "/file.jpg");
        Files.createFile(file.toPath());

        List<File> files = photoDao.getFiles(DYNAMIC_RESOURCES_DIRECTORY);
        assertThat(files.size(), is(1));
        assertThat(files.get(0), is(file));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSaveFileThrowsIllegalArgumentExceptionIfOutputDirectoryIsNull() throws IOException {
        Path path = new File(DYNAMIC_RESOURCES_DIRECTORY + "/file.jpg").toPath();
        photoDao.saveFile(null, path);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSaveFileThrowsIllegalArgumentExceptionIfOutputDirectoryIsEmpty() throws IOException {
        Path path = new File(DYNAMIC_RESOURCES_DIRECTORY + "/file.jpg").toPath();
        photoDao.saveFile("", path);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSaveFileThrowsIllegalArgumentExceptionIfOutputDirectoryIsBlank() throws IOException {
        Path path = new File(DYNAMIC_RESOURCES_DIRECTORY + "/file.jpg").toPath();
        photoDao.saveFile(" ", path);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSaveFileThrowsIllegalArgumentExceptionIfFileIsNull() throws IOException {
        photoDao.saveFile(DYNAMIC_RESOURCES_DIRECTORY, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSaveFileThrowsIllegalArgumentExceptionIfOutputDirectoryIsNotADirectory() throws IOException {
        Path path = new File(DYNAMIC_RESOURCES_DIRECTORY + "/file.jpg").toPath();

        Files.createFile(path);

        photoDao.saveFile(path.toString(), path);
    }

    @Test
    public void testSaveFileCreatesFile() throws IOException {
        Path staticPath = new File(STATIC_RESOURCES_DIRECTORY + "/image.jpg").toPath();

        String sourceDirectory = DYNAMIC_RESOURCES_DIRECTORY + "/source/";
        Files.createDirectory(new File(sourceDirectory).toPath());
        Path sourcePath = new File(sourceDirectory + "image.jpg").toPath();
        Files.copy(staticPath, sourcePath);

        String destinationDirectory = DYNAMIC_RESOURCES_DIRECTORY + "/destination";
        Files.createDirectory(new File(destinationDirectory).toPath());
        Path destinationPath = new File(destinationDirectory + "/image.jpg").toPath();

        photoDao.saveFile(destinationDirectory, sourcePath);

        assertThat(Files.exists(destinationPath), is(true));
    }

    @Test
    public void testSaveFileCreatesDirectoriesIfTheyDoNotExist() throws IOException {
        Path staticPath = new File(STATIC_RESOURCES_DIRECTORY + "/image.jpg").toPath();

        String sourceDirectory = DYNAMIC_RESOURCES_DIRECTORY + "/source/";
        Files.createDirectory(new File(sourceDirectory).toPath());
        Path sourcePath = new File(sourceDirectory + "image.jpg").toPath();
        Files.copy(staticPath, sourcePath);

        String destinationDirectory = DYNAMIC_RESOURCES_DIRECTORY + "/destination";
        Files.createDirectory(new File(destinationDirectory).toPath());
        String outputDirectory = destinationDirectory + "/directory";
        Path destinationPath = new File(outputDirectory + "/image.jpg").toPath();

        photoDao.saveFile(outputDirectory, sourcePath);

        assertThat(Files.exists(destinationPath), is(true));
    }

    @Test
    public void testSaveFileDeletesOldFile() throws IOException {
        Path staticPath = new File(STATIC_RESOURCES_DIRECTORY + "/image.jpg").toPath();

        String sourceDirectory = DYNAMIC_RESOURCES_DIRECTORY + "/source/";
        Files.createDirectory(new File(sourceDirectory).toPath());
        Path sourcePath = new File(sourceDirectory + "/image.jpg").toPath();
        Files.copy(staticPath, sourcePath);

        String destinationDirectory = DYNAMIC_RESOURCES_DIRECTORY + "/destination";
        Files.createDirectory(new File(destinationDirectory).toPath());

        photoDao.saveFile(destinationDirectory, sourcePath);

        assertThat(Files.notExists(sourcePath), is(true));
    }

    @Test
    public void testSaveFileCreatesNewFileWithTheSameDataAsTheOldFile() throws IOException {
        Path staticPath = new File(STATIC_RESOURCES_DIRECTORY + "/image.jpg").toPath();

        String sourceDirectory = DYNAMIC_RESOURCES_DIRECTORY + "/source/";
        Files.createDirectory(new File(sourceDirectory).toPath());
        Path sourcePath = new File(sourceDirectory + "/image.jpg").toPath();
        Files.copy(staticPath, sourcePath);

        String destinationDirectory = DYNAMIC_RESOURCES_DIRECTORY + "/destination";
        Files.createDirectory(new File(destinationDirectory).toPath());
        Path destinationPath = new File(destinationDirectory + "/image.jpg").toPath();

        photoDao.saveFile(destinationDirectory, sourcePath);

        assertThat(Files.getAttribute(destinationPath, "size"), is(Files.getAttribute(staticPath, "size")));
    }

}