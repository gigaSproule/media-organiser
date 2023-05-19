package com.benjaminsproule.mediaorganiser.util;

import com.benjaminsproule.mediaorganiser.exception.InvalidDateException;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.ZonedDateTime;

import static com.benjaminsproule.mediaorganiser.test.FileResource.getFile;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FileDateUtilITest {

    @Test
    public void testGetDateFromFile_UsesImageMetadata_jpg() throws Exception {
        File file = getFile("image.jpg");
        ZonedDateTime zonedDateTime = FileDateUtil.getDateFromFile(file);

        assertThat(zonedDateTime.getYear(), is(2015));
        assertThat(zonedDateTime.getMonthValue(), is(2));
        assertThat(zonedDateTime.getDayOfMonth(), is(15));
        assertThat(zonedDateTime.getHour(), is(19));
        assertThat(zonedDateTime.getMinute(), is(41));
        assertThat(zonedDateTime.getSecond(), is(23));
        assertThat(zonedDateTime.getNano(), is(0));
    }

    @Disabled("Find a tiff file with a created timestamp")
    public void testGetDateFromFile_UsesImageMetadata_tif() throws Exception {
        File file = getFile("image.tif");
        ZonedDateTime zonedDateTime = FileDateUtil.getDateFromFile(file);

        assertThat(zonedDateTime.getYear(), is(2015));
        assertThat(zonedDateTime.getMonthValue(), is(2));
        assertThat(zonedDateTime.getDayOfMonth(), is(15));
        assertThat(zonedDateTime.getHour(), is(19));
        assertThat(zonedDateTime.getMinute(), is(41));
        assertThat(zonedDateTime.getSecond(), is(23));
        assertThat(zonedDateTime.getNano(), is(0));
    }

    @Test
    public void testGetDateFromFile_UsesImageMetadata_png() throws Exception {
        File file = getFile("image.png");
        ZonedDateTime zonedDateTime = FileDateUtil.getDateFromFile(file);

        assertThat(zonedDateTime.getYear(), is(2023));
        assertThat(zonedDateTime.getMonthValue(), is(4));
        assertThat(zonedDateTime.getDayOfMonth(), is(23));
        assertThat(zonedDateTime.getHour(), is(18));
        assertThat(zonedDateTime.getMinute(), is(13));
        assertThat(zonedDateTime.getSecond(), is(30));
        assertThat(zonedDateTime.getNano(), is(457000000));
    }

    @Test
    public void testGetDateFromFile_UsesImageMetadata_heic() throws Exception {
        File file = getFile("image.heic");
        ZonedDateTime zonedDateTime = FileDateUtil.getDateFromFile(file);

        assertThat(zonedDateTime.getYear(), is(2023));
        assertThat(zonedDateTime.getMonthValue(), is(3));
        assertThat(zonedDateTime.getDayOfMonth(), is(25));
        assertThat(zonedDateTime.getHour(), is(11));
        assertThat(zonedDateTime.getMinute(), is(2));
        assertThat(zonedDateTime.getSecond(), is(42));
        assertThat(zonedDateTime.getNano(), is(428000000));
    }

    @Test
    public void testGetDateFromFile_UsesVideoMetadata_mp4() throws Exception {
        File file = getFile("video.mp4");
        ZonedDateTime zonedDateTime = FileDateUtil.getDateFromFile(file);

        assertThat(zonedDateTime.getYear(), is(2023));
        assertThat(zonedDateTime.getMonthValue(), is(5));
        assertThat(zonedDateTime.getDayOfMonth(), is(19));
        assertThat(zonedDateTime.getHour(), is(22));
        assertThat(zonedDateTime.getMinute(), is(38));
        assertThat(zonedDateTime.getSecond(), is(49));
        assertThat(zonedDateTime.getNano(), is(0));
    }

    @Disabled("Find an avi file with a created timestamp")
    public void testGetDateFromFile_UsesVideoMetadata_avi() throws Exception {
        File file = getFile("video.avi");
        ZonedDateTime zonedDateTime = FileDateUtil.getDateFromFile(file);

        assertThat(zonedDateTime.getYear(), is(2023));
        assertThat(zonedDateTime.getMonthValue(), is(5));
        assertThat(zonedDateTime.getDayOfMonth(), is(19));
        assertThat(zonedDateTime.getHour(), is(22));
        assertThat(zonedDateTime.getMinute(), is(38));
        assertThat(zonedDateTime.getSecond(), is(49));
        assertThat(zonedDateTime.getNano(), is(0));
    }

    @Test
    public void testGetDateFromFile_UsesTheFileNameIfNoImageMetadata_FileNameEpochMillis() throws Exception {
        File file = getFile("3661100.jpg");

        ZonedDateTime zonedDateTime = FileDateUtil.getDateFromFile(file);

        assertThat(zonedDateTime.getYear(), is(1970));
        assertThat(zonedDateTime.getMonthValue(), is(1));
        assertThat(zonedDateTime.getDayOfMonth(), is(1));
        assertThat(zonedDateTime.getHour(), is(1));
        assertThat(zonedDateTime.getMinute(), is(1));
        assertThat(zonedDateTime.getSecond(), is(1));
        assertThat(zonedDateTime.getNano(), is(100000000));
    }

    @Test
    public void testGetDateFromFile_UsesTheFileNameIfNoImageMetadata_FileNameDateUnderscoreTime() throws Exception {
        File file = getFile("19700101_010101.jpg");

        ZonedDateTime zonedDateTime = FileDateUtil.getDateFromFile(file);

        assertThat(zonedDateTime.getYear(), is(1970));
        assertThat(zonedDateTime.getMonthValue(), is(1));
        assertThat(zonedDateTime.getDayOfMonth(), is(1));
        assertThat(zonedDateTime.getHour(), is(1));
        assertThat(zonedDateTime.getMinute(), is(1));
        assertThat(zonedDateTime.getSecond(), is(1));
        assertThat(zonedDateTime.getNano(), is(0));
    }

    @Test
    public void testGetDateFromFile_UsesTheFileNameIfNoImageMetadata_FileNameImgUnderscoreDateUnderscoreTime()
        throws Exception {
        File file = getFile("IMG_19700101_010101.jpg");

        ZonedDateTime zonedDateTime = FileDateUtil.getDateFromFile(file);

        assertThat(zonedDateTime.getYear(), is(1970));
        assertThat(zonedDateTime.getMonthValue(), is(1));
        assertThat(zonedDateTime.getDayOfMonth(), is(1));
        assertThat(zonedDateTime.getHour(), is(1));
        assertThat(zonedDateTime.getMinute(), is(1));
        assertThat(zonedDateTime.getSecond(), is(1));
        assertThat(zonedDateTime.getNano(), is(0));
    }

    @Test
    public void testGetDateFromFile_UsesTheFileNameIfNoImageMetadata_FileNameDateHyphenatedUnderscoreTimeHyphenated()
        throws Exception {
        File file = getFile("1970-01-01_01-01-01.jpg");

        ZonedDateTime zonedDateTime = FileDateUtil.getDateFromFile(file);

        assertThat(zonedDateTime.getYear(), is(1970));
        assertThat(zonedDateTime.getMonthValue(), is(1));
        assertThat(zonedDateTime.getDayOfMonth(), is(1));
        assertThat(zonedDateTime.getHour(), is(1));
        assertThat(zonedDateTime.getMinute(), is(1));
        assertThat(zonedDateTime.getSecond(), is(1));
        assertThat(zonedDateTime.getNano(), is(0));
    }

    @Test
    public void testGetDateFromFile_UsesTheFileNameIfNoImageMetadata_FileNameScreenshotUnderscoreDateHyphenatedTimeHyphenated()
        throws Exception {
        File file = getFile("Screenshot_1970-01-01_01-01-01.jpg");

        ZonedDateTime zonedDateTime = FileDateUtil.getDateFromFile(file);

        assertThat(zonedDateTime.getYear(), is(1970));
        assertThat(zonedDateTime.getMonthValue(), is(1));
        assertThat(zonedDateTime.getDayOfMonth(), is(1));
        assertThat(zonedDateTime.getHour(), is(1));
        assertThat(zonedDateTime.getMinute(), is(1));
        assertThat(zonedDateTime.getSecond(), is(1));
        assertThat(zonedDateTime.getNano(), is(0));
    }

    @Test
    public void testGetDateFromFile_UsesTheFileNameIfNoImageMetadata_FileNameDateUnderscoreTimeUnderscoreiOS()
        throws Exception {
        File file = getFile("20230325_110242428_iOS.heic");

        ZonedDateTime zonedDateTime = FileDateUtil.getDateFromFile(file);

        assertThat(zonedDateTime.getYear(), is(2023));
        assertThat(zonedDateTime.getMonthValue(), is(3));
        assertThat(zonedDateTime.getDayOfMonth(), is(25));
        assertThat(zonedDateTime.getHour(), is(11));
        assertThat(zonedDateTime.getMinute(), is(2));
        assertThat(zonedDateTime.getSecond(), is(42));
        assertThat(zonedDateTime.getNano(), is(428000000));
    }

    @Test
    public void testGetDateFromFile_UsesTheFileNameIfNoImageMetadata_FileNamePXLUnderscoreDateUnderscoreTime()
        throws Exception {
        File file = getFile("PXL_20221227_152002772.jpg");

        ZonedDateTime zonedDateTime = FileDateUtil.getDateFromFile(file);

        assertThat(zonedDateTime.getYear(), is(2022));
        assertThat(zonedDateTime.getMonthValue(), is(12));
        assertThat(zonedDateTime.getDayOfMonth(), is(27));
        assertThat(zonedDateTime.getHour(), is(15));
        assertThat(zonedDateTime.getMinute(), is(20));
        assertThat(zonedDateTime.getSecond(), is(2));
        assertThat(zonedDateTime.getNano(), is(772000000));
    }

    @Test
    public void testGetDateFromFile_NoMetadataAndUnknownFileNamePattern_ThrowsInvalidDateException() throws Exception {
        File file = getFile("file");

        assertThrows(InvalidDateException.class, () -> FileDateUtil.getDateFromFile(file), "Could not get a timestamp for the file " + file.getName());
    }
}
