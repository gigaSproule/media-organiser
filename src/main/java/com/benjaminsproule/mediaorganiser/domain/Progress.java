package com.benjaminsproule.mediaorganiser.domain;

public class Progress {
    private static int totalNumberOfFiles;
    private static int numberOfFilesProcessed;

    public static int getTotalNumberOfFiles() {
        return totalNumberOfFiles;
    }

    public static int getNumberOfFilesProcessed() {
        return numberOfFilesProcessed;
    }

    public static void setTotalNumberOfFiles(int numberOfFiles) {
        totalNumberOfFiles = numberOfFiles;
    }

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
