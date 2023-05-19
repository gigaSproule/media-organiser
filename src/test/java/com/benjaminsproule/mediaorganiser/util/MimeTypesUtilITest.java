package com.benjaminsproule.mediaorganiser.util;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.benjaminsproule.mediaorganiser.util.MimeTypesUtil.*;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class MimeTypesUtilITest {

    private Path mimeTypes;
    private Path mimeTypesBackup;

    @BeforeEach
    public void setup() throws Exception {
        String home = System.getProperty("user.home");
        mimeTypes = new File(home + "/.mime.types").toPath();
        mimeTypesBackup = new File(home + "/.mime.types.backup").toPath();

        if (Files.exists(mimeTypes)) {
            Files.deleteIfExists(mimeTypesBackup);
            Files.move(mimeTypes, mimeTypesBackup);
        }
    }

    @AfterEach
    public void tearDown() throws Exception {
        if (Files.exists(mimeTypes)) {
            Files.deleteIfExists(mimeTypes);
        }

        if (Files.exists(mimeTypesBackup)) {
            Files.move(mimeTypesBackup, mimeTypes);
            Files.deleteIfExists(mimeTypesBackup);
        }
    }

    @Test
    public void requiresMimeTypesFile_ReturnsFalse_Windows() {
        String originalOsName = System.getProperty("os.name");
        System.setProperty("os.name", "Windows");
        assertThat(MimeTypesUtil.requiresMimeTypesFile(), is(false));
        System.setProperty("os.name", originalOsName);
    }

    @Test
    public void requiresMimeTypesFile_ReturnsFalse_Unkown() {
        String originalOsName = System.getProperty("os.name");
        System.setProperty("os.name", "Unknown");
        assertThat(MimeTypesUtil.requiresMimeTypesFile(), is(false));
        System.setProperty("os.name", originalOsName);
    }

    @Test
    public void requiresMimeTypesFile_ReturnsTrue_Mac() {
        String originalOsName = System.getProperty("os.name");
        System.setProperty("os.name", "Mac OS X");
        assertThat(MimeTypesUtil.requiresMimeTypesFile(), is(true));
        System.setProperty("os.name", originalOsName);
    }

    @Test
    public void requiresMimeTypesFile_ReturnsTrue_Linux() {
        String originalOsName = System.getProperty("os.name");
        System.setProperty("os.name", "Linux");
        assertThat(MimeTypesUtil.requiresMimeTypesFile(), is(true));
        System.setProperty("os.name", originalOsName);
    }

    @Test
    public void testMimeTypesFileCreatedIfItDoesNotExist() throws Exception {
        MimeTypesUtil.createMimeTypesFile();

        assertThat(Files.exists(mimeTypes), is(true));
    }

    @Test
    public void testMimeTypesFileUpdatedIfItDoesExist() throws Exception {
        Files.createFile(mimeTypes);

        MimeTypesUtil.createMimeTypesFile();

        assertThat(Files.readAllLines(mimeTypes), hasItems(IMAGES_JPG_MAPPING, IMAGES_TIFF, VIDEOS_MP4, VIDEOS_AVI));
    }

    @Test
    public void testMimeTypesFileUpdatedIfItDoesExist_DoesNotDeleteOldLines() throws Exception {
        populateMimeTypesFile("test");

        MimeTypesUtil.createMimeTypesFile();

        assertThat(Files.readAllLines(mimeTypes), hasItem("test"));
    }

    @Test
    public void testMimeTypesFileUpdatedIfItDoesExist_IgnoresAlreadyExisting() throws Exception {
        populateMimeTypesFile(IMAGES_JPG_MAPPING, IMAGES_TIFF, VIDEOS_MP4, VIDEOS_AVI);

        MimeTypesUtil.createMimeTypesFile();

        assertThat(Files.readAllLines(mimeTypes),
            containsInAnyOrder(IMAGES_JPG_MAPPING, IMAGES_TIFF, VIDEOS_MP4, VIDEOS_AVI));
    }

    private void populateMimeTypesFile(final String... str) throws IOException {
        Files.createFile(mimeTypes);
        Files.write(mimeTypes, asList(str));
    }
}
