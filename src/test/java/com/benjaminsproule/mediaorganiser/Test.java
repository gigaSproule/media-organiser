package com.benjaminsproule.mediaorganiser;

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
import java.util.Date;

import static java.io.File.separator;

public class Test {

    private static final String EXIF_DATE_TIME_ORIGINAL = "exif:DateTimeOriginal";
    private static final String META_CREATION_DATE = "meta:creation-date";

    private static final String RESOURCES_DIRECTORY = System.getProperty("user.dir") + separator + "src" + separator
            + "test" + separator + "resources";

    @org.junit.Test
    public void test() throws IOException {
        File file = new File(RESOURCES_DIRECTORY + separator + "image.jpg");
        Metadata metadata = new Metadata();

        try (InputStream input = new FileInputStream(file)) {
            ContentHandler handler = new DefaultHandler();
            Parser parser = new AutoDetectParser();
            ParseContext parseCtx = new ParseContext();
            parser.parse(input, handler, metadata, parseCtx);
        } catch (SAXException | TikaException | IOException e) {
            e.printStackTrace();
        }

        for (String name : metadata.names()) {
            System.out.println(name + ": " + metadata.get(name));
        }

        Date dateTime = metadata.getDate(Property.get(EXIF_DATE_TIME_ORIGINAL));
        if (dateTime == null) {
            dateTime = metadata.getDate(Property.get(META_CREATION_DATE));
        }

        System.out.println(dateTime.toString());
    }
}
