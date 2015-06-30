package com.benjaminsproule.mediaorganiser.service;

public class Progress {
    private static int totalNumberOfFiles;
    private static int numberOfFilesProcessed;

    public static void reset() {
        totalNumberOfFiles = 0;
        numberOfFilesProcessed = 0;
    }

    public static int getTotalNumberOfFiles() {
        return totalNumberOfFiles;
    }

    public static int getNumberOfFilesProcessed() {
        return numberOfFilesProcessed;
    }

    public static void inc() {
        numberOfFilesProcessed++;
    }

    public static void setTotalNumberOfFiles(int numberOfFiles) {
        totalNumberOfFiles = numberOfFiles;
    }
}
