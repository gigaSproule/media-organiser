package com.benjaminsproule.mediaorganiser.util;

import com.benjaminsproule.mediaorganiser.exception.InvalidDateException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

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

    @ParameterizedTest
    @CsvSource({
        "3661100.jpg,1970-01-01T01:01:01.100Z[UTC]",
        "19700101_010101.jpg,1970-01-01T01:01:01Z",
        "IMG_19700101_010101.jpg,1970-01-01T01:01:01Z",
        "1970-01-01_01-01-01.jpg,1970-01-01T01:01:01Z",
        "Screenshot_1970-01-01_01-01-01.jpg,1970-01-01T01:01:01Z",
        "20230325_110242428_iOS.heic,2023-03-25T11:02:42.428Z",
        "PXL_20221227_152002772.jpg,2022-12-27T15:20:02.772Z",
        "PXL_20221227_152002772.MP.jpg,2022-12-27T15:20:02.772Z",
        "20221227_152002772-COLLAGE.jpg,2022-12-27T15:20:02.772Z",
        "00000IMG_00000_BURST20170430172516.jpg,2017-04-30T17:25:16Z",
        "00000IMG_00000_BURST20170430172516_COVER.jpg,2017-04-30T17:25:16Z",
        "00001IMG_00001_BURST20170430172516.jpg,2017-04-30T17:25:16Z",
        "00001IMG_00001_BURST20170430172516_COVER.jpg,2017-04-30T17:25:16Z",
        "00000XTR_00000_BURST20170430172516.jpg,2017-04-30T17:25:16Z",
        "00000XTR_00000_BURST20170430172516_COVER.jpg,2017-04-30T17:25:16Z",
        "00001XTR_00001_BURST20170430172516.jpg,2017-04-30T17:25:16Z",
        "00001XTR_00001_BURST20170430172516_COVER.jpg,2017-04-30T17:25:16Z",
        "Burst_Cover_GIF_Action_20170401114720.gif,2017-04-01T11:47:20Z",
        "Burst_Cover_Collage_20170430172710.jpg,2017-04-30T17:27:10Z"
    })
    public void testGetDateFromFile_UsesTheFileNameIfNoImageMetadata(String filename, String expectedDateTime) throws Exception {
        File file = getFile(filename);

        ZonedDateTime zonedDateTime = FileDateUtil.getDateFromFile(file);

        assertThat(zonedDateTime, is(ZonedDateTime.parse(expectedDateTime)));
    }

    @Test
    public void testGetDateFromFile_NoMetadataAndUnknownFileNamePattern_ThrowsInvalidDateException() throws Exception {
        File file = getFile("file");

        assertThrows(InvalidDateException.class, () -> FileDateUtil.getDateFromFile(file), "Could not get a timestamp for the file " + file.getName());
    }
}
