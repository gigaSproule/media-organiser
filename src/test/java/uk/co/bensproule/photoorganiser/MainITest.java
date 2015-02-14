package uk.co.bensproule.photoorganiser;

import org.junit.Test;

public class MainITest {
    @Test(expected = IllegalArgumentException.class)
    public void testMainThrowsIllegalArgumentExceptionWhenInputFileArgumentMissing() throws Exception {
        String[] args = new String[]{"-od", "outputDirectory"};
        Main.main(args);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMainThrowsIllegalArgumentExceptionWhenOutputDirectoryArgumentMissing() throws Exception {
        String[] args = new String[]{"-id", "inputDirectory"};
        Main.main(args);
    }

    @Test
    public void testMainDoesNotThrowIllegalArgumentExceptionWhenArgumentsPassed() throws Exception {
        String[] args = new String[]{"-id", "inputDirectory", "-od", "outputDirectory"};
        Main.main(args);
    }
}