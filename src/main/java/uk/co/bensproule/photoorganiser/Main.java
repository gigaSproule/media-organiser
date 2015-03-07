package uk.co.bensproule.photoorganiser;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Options;
import uk.co.bensproule.photoorganiser.gui.MainFrame;
import uk.co.bensproule.photoorganiser.service.PhotoService;

import static javax.swing.SwingUtilities.invokeLater;
import static org.apache.commons.lang3.StringUtils.isBlank;

public class Main {
    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            invokeLater(() -> {
                MainFrame mainFrame = new MainFrame();
                mainFrame.setVisible(true);
            });
        } else {
            Options options = new Options();
            options.addOption("id", "inputdirectory", true, "The directory that contains the photos to organise");
            options.addOption("od", "outputdirectory", true, "The directory to put the organised photos");
            options.addOption("of", "outputformat", true, "The format the output directory should use to put the organised photos");
            CommandLineParser parser = new GnuParser();
            CommandLine cmd = parser.parse(options, args);
            String inputDirectory = cmd.getOptionValue("inputdirectory");

            if (isBlank(inputDirectory)) {
                throw new IllegalArgumentException("Please provide the directory that contains the photos using the -id argument");
            }

            String outputDirectory = cmd.getOptionValue("outputdirectory");
            if (isBlank(outputDirectory)) {
                throw new IllegalArgumentException("Please provide the output directory to put the organised photos using the -od argument");
            }

            String outputFormat = cmd.getOptionValue("outputformat");
            if (isBlank(outputFormat)) {
                throw new IllegalArgumentException("Please provide the output format to define the output path");
            }

            PhotoService photoService = new PhotoService();
            photoService.organise(inputDirectory, outputDirectory, outputFormat);
        }
    }
}
