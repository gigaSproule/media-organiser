package com.benjaminsproule.mediaorganiser.service;

import com.benjaminsproule.mediaorganiser.dao.MediaDao;
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
import java.nio.file.Path;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

import static java.time.Instant.ofEpochMilli;
import static org.apache.commons.lang3.Validate.notNull;

public class MediaService {
    private static final String EXIF_DATE_TIME_ORIGINAL = "exif:DateTimeOriginal";
    private static final String META_CREATION_DATE = "meta:creation-date";
    private MediaDao mediaDao;

    public MediaService() {
        mediaDao = new MediaDao();
    }

    public void organise(String inputDirectory, String outputDirectory, String outputFormat) throws IOException {
        Progress.reset();
        List<Path> paths = mediaDao.getFiles(inputDirectory);
        Progress.setTotalNumberOfFiles(paths.size());

        for (Path path : paths) {
            File file = path.toFile();

            ZonedDateTime zonedDateTime = null;

            Date dateTime = getDateFromFile(file);
            if (dateTime == null) {
                try {
                    long epochMilli = Long.parseLong(file.getName().split("\\.")[0]);
                    zonedDateTime = ZonedDateTime.ofInstant(ofEpochMilli(epochMilli), ZoneId.of("UTC"));
                } catch (NumberFormatException e) {
                    // no-op
                }
            } else {
                zonedDateTime = ZonedDateTime.ofInstant(dateTime.toInstant(), ZoneId.of("UTC"));
            }

            notNull(zonedDateTime, "Could not get the ZonedDateTime from the file %s", file.getAbsolutePath());

            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern(outputFormat);
            mediaDao.saveFile(outputDirectory + "/" + zonedDateTime.format(outputFormatter), path);
            Progress.inc();
        }
    }

    private Date getDateFromFile(File file) {
        Metadata metadata = new Metadata();

        try (InputStream input = new FileInputStream(file)) {
            ContentHandler handler = new DefaultHandler();
            Parser parser = new AutoDetectParser();
            ParseContext parseCtx = new ParseContext();
            parser.parse(input, handler, metadata, parseCtx);
        } catch (SAXException | TikaException | IOException e) {
            e.printStackTrace();
        }

        Date dateTime = metadata.getDate(Property.get(EXIF_DATE_TIME_ORIGINAL));
        if (dateTime == null) {
            dateTime = metadata.getDate(Property.get(META_CREATION_DATE));
        }
        return dateTime;
    }
}
