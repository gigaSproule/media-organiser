package com.benjaminsproule.photoorganiser;

import com.benjaminsproule.photoorganiser.domain.DateConstants;
import com.benjaminsproule.photoorganiser.test.Constants;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.io.File.separator;
import static java.nio.file.Files.copy;
import static java.nio.file.Files.exists;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class MainITest {
    private String inputDirectory;
    private String outputDirectory;

    @Before
    public void setup() throws IOException {
        inputDirectory = Files.createTempDirectory(Constants.SOURCE_PATH).toString();
        outputDirectory = Files.createTempDirectory(Constants.DESTINATION_PATH).toString();
    }

    @Test
    public void testMainMovesImageIntoCorrectPlaceYYYYMMDD() throws Exception {
        Path inputDirectoryPath = new File(inputDirectory + separator + "image.jpg").toPath();
        Path staticPath = new File(Constants.RESOURCES_DIRECTORY + separator + "image.jpg").toPath();
        copy(staticPath, inputDirectoryPath);

        String[] args = new String[]{"-id", inputDirectory, "-od", outputDirectory, "-of", DateConstants.YYYY_MM_DD};
        Main.main(args);

        Path expectedFile = new File(outputDirectory + separator + "2015" + separator + "02" + separator + "15" + separator + "image.jpg").toPath();
        assertThat(exists(expectedFile), is(true));
    }

    @Test
    public void testMainMovesImageIntoCorrectPlaceYYYYMMMMDD() throws Exception {
        Path inputDirectoryPath = new File(inputDirectory + separator + "image.jpg").toPath();
        Path staticPath = new File(Constants.RESOURCES_DIRECTORY + separator + "image.jpg").toPath();
        copy(staticPath, inputDirectoryPath);

        String[] args = new String[]{"-id", inputDirectory, "-od", outputDirectory, "-of", DateConstants.YYYY_MMMM_DD};
        Main.main(args);

        Path expectedFile = new File(outputDirectory + separator + "2015" + separator + "February" + separator + "15" + separator + "image.jpg").toPath();
        assertThat(exists(expectedFile), is(true));
    }

    @Test
    public void testMainMovesImageIntoCorrectPlaceYYYYMMMMMMDD() throws Exception {
        Path inputDirectoryPath = new File(inputDirectory + separator + "image.jpg").toPath();
        Path staticPath = new File(Constants.RESOURCES_DIRECTORY + separator + "image.jpg").toPath();
        copy(staticPath, inputDirectoryPath);

        String[] args = new String[]{"-id", inputDirectory, "-od", outputDirectory, "-of", DateConstants.YYYY_MM_MMMM_DD};
        Main.main(args);

        Path expectedFile = new File(outputDirectory + separator + "2015" + separator + "02 - February" + separator + "15" + separator + "image.jpg").toPath();
        assertThat(exists(expectedFile), is(true));
    }

    @Test
    public void testMainMovesImageWithoutImageMetadataIntoCorrectPlace() throws Exception {
        Path inputDirectoryPath = new File(inputDirectory + separator + "1425748958422.jpg").toPath();
        Path staticPath = new File(Constants.RESOURCES_DIRECTORY + separator + "1425748958422.jpg").toPath();
        copy(staticPath, inputDirectoryPath);

        String[] args = new String[]{"-id", inputDirectory, "-od", outputDirectory, "-of", DateConstants.YYYY_MM_DD};
        Main.main(args);

        Path expectedFile = new File(outputDirectory + separator + "2015" + separator + "03" + separator + "07" + separator + "1425748958422.jpg").toPath();
        assertThat(exists(expectedFile), is(true));
    }
}
