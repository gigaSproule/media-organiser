package com.benjaminsproule.mediaorganiser.domain;

import lombok.Getter;
import lombok.Setter;

public class Progress {
    @Getter
    @Setter
    private static int totalNumberOfFiles;
    @Getter
    private static int numberOfFilesProcessed;

    /**
     * Set the total number of files and number of files processed to zero
     */
    public static void reset() {
        totalNumberOfFiles = 0;
        numberOfFilesProcessed = 0;
    }

    /**
     * Increment the number of files processed by one
     */
    public static void inc() {
        numberOfFilesProcessed++;
    }
}
