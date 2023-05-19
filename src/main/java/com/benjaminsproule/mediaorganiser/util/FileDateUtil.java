package com.benjaminsproule.mediaorganiser.util;

import com.benjaminsproule.mediaorganiser.exception.InvalidDateException;
import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.mp4.media.Mp4MetaDirectory;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

import static java.time.Instant.ofEpochMilli;

@Slf4j
public class FileDateUtil {

    /**
     * Get the date from the given file, from the metadata of the file if it
     * exists, the name if there isn't any metadata or null if it can't find
     * one.
     *
     * @param file the file to extract the date from
     * @return the {@link ZonedDateTime} of the file
     */
    public static ZonedDateTime getDateFromFile(File file) throws InvalidDateException {
        try {
            Metadata metadata = ImageMetadataReader.readMetadata(file);
            Date dateTime = null;
            ExifSubIFDDirectory exifSubIFDDirectory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
            if (exifSubIFDDirectory != null) {
                dateTime = exifSubIFDDirectory.getDateOriginal();
                if (dateTime != null) {
                    return ZonedDateTime.ofInstant(dateTime.toInstant(), ZoneId.of("UTC"));
                }
            }
            Mp4MetaDirectory mp4MetaDirectory = metadata.getFirstDirectoryOfType(Mp4MetaDirectory.class);
            if (mp4MetaDirectory != null) {
                dateTime = mp4MetaDirectory.getDate(Mp4MetaDirectory.TAG_CREATION_TIME);
            }
            if (dateTime != null) {
                return ZonedDateTime.ofInstant(dateTime.toInstant(), ZoneId.of("UTC"));
            }
        } catch (ImageProcessingException | IOException e) {
            log.error(e.getLocalizedMessage(), e);
        }

        String fileName = file.getName().split("\\.")[0];
        ZonedDateTime zonedDateTime = getDateByEpochMilli(fileName);

        if (zonedDateTime == null) {
            zonedDateTime = getDateByDateUnderscoreTime(fileName);
        }

        if (zonedDateTime == null) {
            zonedDateTime = getDateByImgUnderscoreDateUnderscoreTime(fileName);
        }

        if (zonedDateTime == null) {
            zonedDateTime = getDateByDateHyphenatedUnderscoreTimeHyphenated(fileName);
        }

        if (zonedDateTime == null) {
            zonedDateTime = getDateByScreenshotUnderscoreDateHyphenatedTimeHyphenated(fileName);
        }

        if (zonedDateTime == null) {
            zonedDateTime = getDateByDateUnderscoreTimeUnderscoreiOS(fileName);
        }

        if (zonedDateTime == null) {
            zonedDateTime = getDateByPXLUnderscoreDateUnderscoreTime(fileName);
        }

        if (zonedDateTime == null) {
            throw new InvalidDateException("Could not get a timestamp for the file " + fileName);
        }

        return zonedDateTime;
    }

    private static ZonedDateTime getDateByEpochMilli(String fileName) {
        try {
            long epochMilli = Long.parseLong(fileName);
            return ZonedDateTime.ofInstant(ofEpochMilli(epochMilli), ZoneId.of("UTC"));
        } catch (NumberFormatException e) {
            log.info(e.getLocalizedMessage(), e);
            return null;
        }
    }

    private static ZonedDateTime getDateByDateUnderscoreTime(String fileName) {
        try {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmssZ");
            return ZonedDateTime.parse(fileName + "+0000", dateTimeFormatter);
        } catch (DateTimeParseException e) {
            log.info(e.getLocalizedMessage(), e);
            return null;
        }
    }

    private static ZonedDateTime getDateByImgUnderscoreDateUnderscoreTime(String fileName) {
        try {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmssZ");
            return ZonedDateTime.parse(fileName.replaceFirst("(?i)IMG_", "") + "+0000", dateTimeFormatter);
        } catch (DateTimeParseException e) {
            log.info(e.getLocalizedMessage(), e);
            return null;
        }
    }

    private static ZonedDateTime getDateByDateHyphenatedUnderscoreTimeHyphenated(String fileName) {
        try {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ssZ");
            return ZonedDateTime.parse(fileName + "+0000", dateTimeFormatter);
        } catch (DateTimeParseException e) {
            log.info(e.getLocalizedMessage(), e);
            return null;
        }
    }

    private static ZonedDateTime getDateByScreenshotUnderscoreDateHyphenatedTimeHyphenated(String fileName) {
        try {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ssZ");
            return ZonedDateTime.parse(fileName.replaceFirst("(?i)Screenshot_", "") + "+0000", dateTimeFormatter);
        } catch (DateTimeParseException e) {
            log.info(e.getLocalizedMessage(), e);
            return null;
        }
    }

    private static ZonedDateTime getDateByDateUnderscoreTimeUnderscoreiOS(String fileName) {
        try {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmssSSSZ");
            return ZonedDateTime.parse(fileName.replaceFirst("(?i)_iOS", "") + "+0000", dateTimeFormatter);
        } catch (DateTimeParseException e) {
            log.info(e.getLocalizedMessage(), e);
            return null;
        }
    }

    private static ZonedDateTime getDateByPXLUnderscoreDateUnderscoreTime(String fileName) {
        try {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmssSSSZ");
            return ZonedDateTime.parse(fileName.replaceFirst("(?i)PXL_", "") + "+0000", dateTimeFormatter);
        } catch (DateTimeParseException e) {
            log.info(e.getLocalizedMessage(), e);
            return null;
        }
    }
}
