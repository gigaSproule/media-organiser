package com.benjaminsproule.mediaorganiser;

import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.apache.commons.imaging.formats.tiff.TiffField;

import java.io.File;
import java.io.IOException;

import static java.io.File.separator;
import static org.apache.commons.imaging.formats.tiff.constants.ExifTagConstants.EXIF_TAG_DATE_TIME_ORIGINAL;

public class Test {

    private static final String RESOURCES_DIRECTORY = System.getProperty("user.dir") + separator +
        "src" + separator + "test" + separator + "resources";

    @org.junit.Test
    public void test() throws IOException, ImageReadException {
        File file = new File(RESOURCES_DIRECTORY + separator + "image.jpg");
        ImageMetadata metadata = Imaging.getMetadata(file);
        TiffField dateTime = ((JpegImageMetadata) metadata).findEXIFValueWithExactMatch(EXIF_TAG_DATE_TIME_ORIGINAL);
        System.out.println(dateTime.toString());
    }
}
