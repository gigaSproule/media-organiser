package uk.co.bensproule.photoorganiser.service;

import lombok.extern.slf4j.Slf4j;
import uk.co.bensproule.photoorganiser.dao.PhotoDao;

import java.io.IOException;
import java.nio.file.Path;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
public class PhotoService {
    private PhotoDao photoDao;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss").withZone(ZoneId.of("UTC"));

    public PhotoService() {
        photoDao = new PhotoDao();
    }

    public void organise(String inputDirectory, String outputDirectory) throws IOException {
        List<Path> paths = photoDao.getFiles(inputDirectory);

        for (Path path : paths) {
            String fileName = path.getFileName().toString();
            ZonedDateTime zonedDateTime = ZonedDateTime.parse(fileName, formatter);

            photoDao.saveFile(outputDirectory + "/" + zonedDateTime.getYear() + "/" + format(zonedDateTime.getMonthValue()) + "/" + format(zonedDateTime.getDayOfMonth()), path);
        }
    }

    private String format(int value) {
        if (value < 10) {
            return "0" + value;
        }

        return String.valueOf(value);
    }
}
