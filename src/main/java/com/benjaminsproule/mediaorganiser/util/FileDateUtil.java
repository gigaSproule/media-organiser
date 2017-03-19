package com.benjaminsproule.mediaorganiser.util;

import com.benjaminsproule.mediaorganiser.exception.InvalidDateException;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.Property;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

import static java.time.Instant.ofEpochMilli;

@Slf4j
public class FileDateUtil {
    public static final String EXIF_DATE_TIME_ORIGINAL = "exif:DateTimeOriginal";
    public static final String META_CREATION_DATE = "meta:creation-date";

    /**
     * Get the date from the given file, from the metadata of the file if it
     * exists, the name if there isn't any metadata or null if it can't find
     * one.
     *
     * @param file
     *            the file to extract the date from
     * @return the {@link ZonedDateTime} of the file
     * @throws InvalidDateException
     */
    public static ZonedDateTime getDateFromFile(File file) throws InvalidDateException {
        Metadata metadata = new Metadata();

        try (InputStream input = new FileInputStream(file)) {
            ContentHandler handler = new DefaultHandler();
            Parser parser = new AutoDetectParser();
            ParseContext parseCtx = new ParseContext();
            parser.parse(input, handler, metadata, parseCtx);
        } catch (SAXException | TikaException | IOException e) {
            log.error(e.getLocalizedMessage(), e);
        }

        Date dateTime = metadata.getDate(Property.get(EXIF_DATE_TIME_ORIGINAL));
        if (dateTime == null) {
            dateTime = metadata.getDate(Property.get(META_CREATION_DATE));
        }

        ZonedDateTime zonedDateTime = null;
        if (dateTime != null) {
            zonedDateTime = ZonedDateTime.ofInstant(dateTime.toInstant(), ZoneId.of("UTC"));
        }

        String fileName = file.getName().split("\\.")[0];
        if (zonedDateTime == null) {
            zonedDateTime = getDateByEpochMilli(fileName);
        }

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
            return ZonedDateTime.parse(fileName.substring(4) + "+0000", dateTimeFormatter);
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
            return ZonedDateTime.parse(fileName.substring(11) + "+0000", dateTimeFormatter);
        } catch (DateTimeParseException e) {
            log.info(e.getLocalizedMessage(), e);
            return null;
        }
    }
}
