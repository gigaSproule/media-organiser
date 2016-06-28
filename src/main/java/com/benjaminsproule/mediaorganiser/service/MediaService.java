package com.benjaminsproule.mediaorganiser.service;

import com.benjaminsproule.mediaorganiser.dao.MediaDao;
import com.benjaminsproule.mediaorganiser.domain.Progress;
import com.benjaminsproule.mediaorganiser.exception.InvalidDateException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.benjaminsproule.mediaorganiser.util.FileDateUtil.getDateFromFile;

@Slf4j
public class MediaService {
    private static final DecimalFormat decimalFormat = new DecimalFormat("#.00");

    private MediaDao mediaDao;

    public MediaService() {
        mediaDao = new MediaDao();
    }

    /**
     * Organises the files in the inputDirectory into the outputDirectory in the
     * format of the outputFormat
     * 
     * @param inputDirectory
     *            the directory of the files to organise
     * @param outputDirectory
     *            the directory to move the files into
     * @param outputFormat
     *            the format of the folder names
     * @return a list of errors
     * @throws IOException
     *             if there is an issue with the file being read
     * @throws InterruptedException
     *             if the thread is interrupted
     */
    public List<String> organise(String inputDirectory, String outputDirectory, String outputFormat)
            throws IOException, InterruptedException {
        Progress.reset();
        List<Path> paths = mediaDao.getFiles(inputDirectory);
        Progress.setTotalNumberOfFiles(paths.size());
        List<String> errors = new ArrayList<>();

        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        for (Path path : paths) {
            executorService.submit(() -> {
                try {
                    ZonedDateTime zonedDateTime = getDateFromFile(path.toFile());
                    DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern(outputFormat);
                    mediaDao.saveFile(outputDirectory + "/" + zonedDateTime.format(outputFormatter), path);
                } catch (IOException | InvalidDateException e) {
                    log.error(e.getLocalizedMessage(), e);
                    errors.add(e.getLocalizedMessage());
                }

                Progress.inc();
            });
        }

        executorService.shutdown();
        while (!executorService.isTerminated()) {
            double progress = (Progress.getNumberOfFilesProcessed() / Progress.getTotalNumberOfFiles()) * 100;
            log.debug("Progress: " + decimalFormat.format(progress) + "%");
        }

        return errors;
    }
}
