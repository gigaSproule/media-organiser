package com.benjaminsproule.photoorganiser.util;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.benjaminsproule.photoorganiser.util.MimeTypesUtil.*;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class MimeTypesUtilITest {

    private Path mimeTypes;
    private Path mimeTypesBackup;

    @Before
    public void setup() throws Exception {
        if (!MimeTypesUtil.requiresMimeTypesFile()) {
            return;
        }

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
        if (!MimeTypesUtil.requiresMimeTypesFile()) {
            return;
        }

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

        assertThat(Files.readAllLines(mimeTypes), hasItems(IMAGES_JPG_MAPPING, IMAGES_TIFF, VIDEOS_MP4));
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

        populateMimeTypesFile(IMAGES_JPG_MAPPING, IMAGES_TIFF, VIDEOS_MP4);

        MimeTypesUtil.createMimeTypesFile();

        assertThat(Files.readAllLines(mimeTypes), containsInAnyOrder(IMAGES_JPG_MAPPING, IMAGES_TIFF, VIDEOS_MP4));
    }

    private void populateMimeTypesFile(final String... str) throws IOException {
        Files.createFile(mimeTypes);
        Files.write(mimeTypes, asList(str));
    }
}
