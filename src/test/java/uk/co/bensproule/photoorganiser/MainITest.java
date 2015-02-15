package uk.co.bensproule.photoorganiser;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static java.nio.file.Files.createTempDirectory;

public class MainITest {
    private String inputDirectory;
    private String outputDirectory;

    @Before
    public void setup() throws IOException {
        inputDirectory = createTempDirectory("testInput").toString();
        outputDirectory = createTempDirectory("testOutput").toString();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMainThrowsIllegalArgumentExceptionWhenInputFileArgumentMissing() throws Exception {
        String[] args = new String[]{"-od", outputDirectory};
        Main.main(args);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMainThrowsIllegalArgumentExceptionWhenOutputDirectoryArgumentMissing() throws Exception {
        String[] args = new String[]{"-id", inputDirectory};
        Main.main(args);
    }

    @Test
    public void testMainDoesNotThrowIllegalArgumentExceptionWhenArgumentsPassed() throws Exception {
        String[] args = new String[]{"-id", inputDirectory, "-od", outputDirectory};
        Main.main(args);
    }
}