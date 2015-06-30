package com.benjaminsproule.mediaorganiser;

import com.benjaminsproule.mediaorganiser.domain.DateConstants;
import com.benjaminsproule.mediaorganiser.service.Progress;
import com.benjaminsproule.mediaorganiser.test.Constants;
import org.junit.Before;
import org.junit.Ignore;
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

    @Ignore // TODO: Find a tiff file with a created timestamp
    @Test
    public void testMainMovesTifImageWithImageMetadataIntoCorrectPlace() throws Exception {
        Path inputDirectoryPath = new File(inputDirectory + separator + "image.tif").toPath();
        Path staticPath = new File(Constants.RESOURCES_DIRECTORY + separator + "image.tif").toPath();
        copy(staticPath, inputDirectoryPath);

        String[] args = new String[]{"-id", inputDirectory, "-od", outputDirectory, "-of", DateConstants.YYYY_MM_DD};
        Main.main(args);

        Path expectedFile = new File(outputDirectory + separator + "2012" + separator + "09" + separator + "07" + separator + "image.tif").toPath();
        assertThat(exists(expectedFile), is(true));
    }

    @Test
    public void testMainMovesMp4VideoWithVideoMetadataIntoCorrectPlace() throws Exception {
        Path inputDirectoryPath = new File(inputDirectory + separator + "video.mp4").toPath();
        Path staticPath = new File(Constants.RESOURCES_DIRECTORY + separator + "video.mp4").toPath();
        copy(staticPath, inputDirectoryPath);

        String[] args = new String[]{"-id", inputDirectory, "-od", outputDirectory, "-of", DateConstants.YYYY_MM_DD};
        Main.main(args);

        Path expectedFile = new File(outputDirectory + separator + "2014" + separator + "11" + separator + "30" + separator + "video.mp4").toPath();
        assertThat(exists(expectedFile), is(true));
    }

    @Ignore // TODO: Find an avi file with a created timestamp
    @Test
    public void testMainMovesAviVideoWithVideoMetadataIntoCorrectPlace() throws Exception {
        Path inputDirectoryPath = new File(inputDirectory + separator + "video.avi").toPath();
        Path staticPath = new File(Constants.RESOURCES_DIRECTORY + separator + "video.avi").toPath();
        copy(staticPath, inputDirectoryPath);

        String[] args = new String[]{"-id", inputDirectory, "-od", outputDirectory, "-of", DateConstants.YYYY_MM_DD};
        Main.main(args);

        Path expectedFile = new File(outputDirectory + separator + "2013" + separator + "10" + separator + "13" + separator + "video.avi").toPath();
        assertThat(exists(expectedFile), is(true));
    }

    @Test
    public void testMainSetsTheProgressDetails_oneImage() throws Exception {
        Path inputDirectoryPath = new File(inputDirectory + separator + "image.jpg").toPath();
        Path staticPath = new File(Constants.RESOURCES_DIRECTORY + separator + "image.jpg").toPath();
        copy(staticPath, inputDirectoryPath);

        String[] args = new String[]{"-id", inputDirectory, "-od", outputDirectory, "-of", DateConstants.YYYY_MM_DD};
        Main.main(args);

        assertThat(Progress.getNumberOfFilesProcessed(), is(1));
        assertThat(Progress.getTotalNumberOfFiles(), is(1));
    }

    @Test
    public void testMainSetsTheProgressDetails_twoImages() throws Exception {
        Path inputDirectoryPath = new File(inputDirectory + separator + "image.jpg").toPath();
        Path staticPath = new File(Constants.RESOURCES_DIRECTORY + separator + "image.jpg").toPath();
        copy(staticPath, inputDirectoryPath);
        inputDirectoryPath = new File(inputDirectory + separator + "1425748958422.jpg").toPath();
        staticPath = new File(Constants.RESOURCES_DIRECTORY + separator + "1425748958422.jpg").toPath();
        copy(staticPath, inputDirectoryPath);

        String[] args = new String[]{"-id", inputDirectory, "-od", outputDirectory, "-of", DateConstants.YYYY_MM_DD};
        Main.main(args);

        assertThat(Progress.getNumberOfFilesProcessed(), is(2));
        assertThat(Progress.getTotalNumberOfFiles(), is(2));
    }

    @Test
    public void testMainSetsTheProgressDetails_resetsProgress() throws Exception {
        Path inputDirectoryPath = new File(inputDirectory + separator + "image.jpg").toPath();
        Path staticPath = new File(Constants.RESOURCES_DIRECTORY + separator + "image.jpg").toPath();
        copy(staticPath, inputDirectoryPath);

        String[] args = new String[]{"-id", inputDirectory, "-od", outputDirectory, "-of", DateConstants.YYYY_MM_DD};
        Main.main(args);

        assertThat(Progress.getNumberOfFilesProcessed(), is(1));
        assertThat(Progress.getTotalNumberOfFiles(), is(1));

        Main.main(args);

        assertThat("Counter either not reset or image still in input directory", Progress.getNumberOfFilesProcessed(), is(0));
        assertThat("Counter either not reset or image still in input directory", Progress.getTotalNumberOfFiles(), is(0));
    }
}
