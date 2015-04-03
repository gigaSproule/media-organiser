package uk.co.bensproule.photoorganiser.util;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static uk.co.bensproule.photoorganiser.util.MimeTypesUtil.IMAGES_JPG_MAPPING;
import static uk.co.bensproule.photoorganiser.util.MimeTypesUtil.IMAGES_TIFF;

public class MimeTypesUtilITest {

    private Path mimeTypes;
    private Path mimeTypesBackup;

    @Before
    public void setup() throws Exception {
        String home = System.getProperty("user.home");
        mimeTypes = new File(home + "/.mime.types").toPath();
        mimeTypesBackup = new File(home + "/.mime.types.backup").toPath();

        if (Files.exists(mimeTypes)) {
            Files.deleteIfExists(mimeTypesBackup);
            Files.move(mimeTypes, mimeTypesBackup);
        }
    }

    @After
    public void tearDown() throws Exception {
        Files.delete(mimeTypes);

        if (Files.exists(mimeTypesBackup)) {
            Files.move(mimeTypesBackup, mimeTypes);
            Files.deleteIfExists(mimeTypesBackup);
        }
    }

    @Test
    public void testMimeTypesFileCreatedIfItDoesNotExist() throws Exception {
        if (System.getProperty("os.name").startsWith("Windows")) {
            return;
        }

        MimeTypesUtil.createMimeTypesFile();

        assertThat(Files.exists(mimeTypes), is(true));
    }

    @Test
    public void testMimeTypesFileUpdatedIfItDoesExist() throws Exception {
        if (System.getProperty("os.name").startsWith("Windows")) {
            return;
        }

        Files.createFile(mimeTypes);

        MimeTypesUtil.createMimeTypesFile();

        assertThat(Files.readAllLines(mimeTypes), hasItems(IMAGES_JPG_MAPPING, IMAGES_TIFF));
    }

    @Test
    public void testMimeTypesFileUpdatedIfItDoesExist_DoesNotDeleteOldLines() throws Exception {
        if (System.getProperty("os.name").startsWith("Windows")) {
            return;
        }

        populateMimeTypesFile("test");

        MimeTypesUtil.createMimeTypesFile();

        assertThat(Files.readAllLines(mimeTypes), hasItem("test"));
    }

    @Test
    public void testMimeTypesFileUpdatedIfItDoesExist_IgnoresAlreadyExisting() throws Exception {
        if (System.getProperty("os.name").startsWith("Windows")) {
            return;
        }

        populateMimeTypesFile(IMAGES_JPG_MAPPING, IMAGES_TIFF);

        MimeTypesUtil.createMimeTypesFile();

        assertThat(Files.readAllLines(mimeTypes), containsInAnyOrder(IMAGES_JPG_MAPPING, IMAGES_TIFF));
    }

    private void populateMimeTypesFile(final String... str) throws IOException {
        Files.createFile(mimeTypes);
        Files.write(mimeTypes, asList(str));
    }
}
