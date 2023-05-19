package com.benjaminsproule.mediaorganiser;

import com.benjaminsproule.mediaorganiser.dao.MediaDao;
import com.benjaminsproule.mediaorganiser.gui.MainFrame;
import com.benjaminsproule.mediaorganiser.service.MediaService;
import com.benjaminsproule.mediaorganiser.util.MimeTypesUtil;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;

import static javax.swing.SwingUtilities.invokeLater;
import static org.apache.commons.lang3.StringUtils.isBlank;

public class Main {
    public static void main(String[] args) throws Exception {
        if (MimeTypesUtil.requiresMimeTypesFile()) {
            MimeTypesUtil.createMimeTypesFile();
        }

        if (args.length == 0) {
            invokeLater(() -> {
                MainFrame mainFrame = new MainFrame();
                mainFrame.setVisible(true);
            });
        } else {
            Options options = new Options();
            options.addOption("id", "inputdirectory", true, "The directory that contains the media to organise");
            options.addOption("od", "outputdirectory", true, "The directory to put the organised media");
            options.addOption("of", "outputformat", true,
                "The format the output directory should use to put the organised media");
            CommandLineParser parser = new DefaultParser();
            CommandLine cmd = parser.parse(options, args);
            String inputDirectory = cmd.getOptionValue("inputdirectory");

            if (isBlank(inputDirectory)) {
                throw new IllegalArgumentException(
                    "Please provide the directory that contains the media using the -id argument");
            }

            String outputDirectory = cmd.getOptionValue("outputdirectory");
            if (isBlank(outputDirectory)) {
                throw new IllegalArgumentException(
                    "Please provide the output directory to put the organised media using the -od argument");
            }

            String outputFormat = cmd.getOptionValue("outputformat");
            if (isBlank(outputFormat)) {
                throw new IllegalArgumentException("Please provide the output format to define the output path");
            }

            MediaService mediaService = new MediaService(new MediaDao());
            mediaService.organise(inputDirectory, outputDirectory, outputFormat);
        }
    }
}
