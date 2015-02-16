package uk.co.bensproule.photoorganiser.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.apache.commons.imaging.formats.tiff.TiffField;
import org.apache.commons.imaging.formats.tiff.TiffImageMetadata;
import uk.co.bensproule.photoorganiser.dao.PhotoDao;

import java.io.IOException;
import java.nio.file.Path;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static java.time.format.TextStyle.FULL;
import static java.util.Locale.ENGLISH;
import static org.apache.commons.imaging.formats.tiff.constants.ExifTagConstants.EXIF_TAG_DATE_TIME_ORIGINAL;
import static org.apache.commons.lang3.Validate.notNull;

@Slf4j
public class PhotoService {
    private PhotoDao photoDao;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss").withZone(ZoneId.of("UTC"));

    public PhotoService() {
        photoDao = new PhotoDao();
    }

    public void organise(String inputDirectory, String outputDirectory) throws IOException, ImageReadException {
        List<Path> paths = photoDao.getFiles(inputDirectory);

        for (Path path : paths) {
            ImageMetadata metadata = Imaging.getMetadata(path.toFile());
            ZonedDateTime zonedDateTime = null;
            if (metadata instanceof JpegImageMetadata) {
                TiffField dateTime = ((JpegImageMetadata) metadata).findEXIFValueWithExactMatch(EXIF_TAG_DATE_TIME_ORIGINAL);
                zonedDateTime = ZonedDateTime.parse(dateTime.getValue().toString(), formatter);
            } else if (metadata instanceof TiffImageMetadata) {
                // TODO: Find a way to get the time stamp data from tiff images
//                TiffField dateTime = ((TiffImageMetadata) metadata).findEXIFValueWithExactMatch(EXIF_TAG_DATE_TIME_ORIGINAL);
//                zonedDateTime = ZonedDateTime.parse(dateTime.getValue().toString(), formatter);
            }

            notNull(zonedDateTime, "Could not get the ZonedDateTime from the file");

            photoDao.saveFile(outputDirectory + "/" + zonedDateTime.getYear() + "/" + getMonth(zonedDateTime) + "/" + getDay(zonedDateTime), path);
        }
    }

    private String getDay(ZonedDateTime zonedDateTime) {
        return format(zonedDateTime.getDayOfMonth());
    }

    private String getMonth(ZonedDateTime zonedDateTime) {
        return format(zonedDateTime.getMonthValue()) + " - " + zonedDateTime.getMonth().getDisplayName(FULL, ENGLISH);
    }

    private String format(int value) {
        if (value < 10) {
            return "0" + value;
        }

        return String.valueOf(value);
    }
}
