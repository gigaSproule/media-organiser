package uk.co.bensproule.photoorganiser.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.apache.commons.imaging.formats.tiff.TiffField;
import uk.co.bensproule.photoorganiser.dao.PhotoDao;

import java.io.IOException;
import java.nio.file.Path;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.apache.commons.imaging.formats.tiff.constants.ExifTagConstants.EXIF_TAG_DATE_TIME_ORIGINAL;
import static org.apache.commons.lang3.Validate.notNull;

@Slf4j
public class PhotoService {
    private PhotoDao photoDao;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss").withZone(ZoneId.of("UTC"));

    public PhotoService() {
        photoDao = new PhotoDao();
    }

    public void organise(String inputDirectory, String outputDirectory, String outputFormat) throws IOException, ImageReadException {
        List<Path> paths = photoDao.getFiles(inputDirectory);

        for (Path path : paths) {
            ImageMetadata metadata = Imaging.getMetadata(path.toFile());
            ZonedDateTime zonedDateTime = null;
            if (metadata instanceof JpegImageMetadata) {
                TiffField dateTime = ((JpegImageMetadata) metadata).findEXIFValueWithExactMatch(EXIF_TAG_DATE_TIME_ORIGINAL);
                zonedDateTime = ZonedDateTime.parse(dateTime.getValue().toString(), formatter);
            } //else if (metadata instanceof TiffImageMetadata) {
            // TODO: Find a way to get the time stamp data from tiff images
//                TiffField dateTime = ((TiffImageMetadata) metadata).findEXIFValueWithExactMatch(EXIF_TAG_DATE_TIME_ORIGINAL);
//                zonedDateTime = ZonedDateTime.parse(dateTime.getValue().toString(), formatter);
//            }

            notNull(zonedDateTime, "Could not get the ZonedDateTime from the file {}", path.getFileName());

            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern(outputFormat);
            photoDao.saveFile(outputDirectory + "/" + zonedDateTime.format(outputFormatter), path);
        }
    }
}
