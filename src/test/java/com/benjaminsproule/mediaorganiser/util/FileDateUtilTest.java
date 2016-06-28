package com.benjaminsproule.mediaorganiser.util;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.Property;
import org.apache.tika.parser.AutoDetectParser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.io.FileInputStream;
import java.time.ZonedDateTime;
import java.util.Date;

import static com.benjaminsproule.mediaorganiser.util.FileDateUtil.EXIF_DATE_TIME_ORIGINAL;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PrepareForTest(FileDateUtil.class)
@PowerMockIgnore("javax.management.*")
public class FileDateUtilTest {
    @Mock
    private File file;
    @Mock
    private FileInputStream fileInputStream;
    @Mock
    private Metadata metadata;
    @Mock
    private AutoDetectParser autoDetectParser;

    @Before
    public void setup() throws Exception {
        initMocks(this);
        whenNew(FileInputStream.class).withAnyArguments().thenReturn(fileInputStream);
        whenNew(Metadata.class).withNoArguments().thenReturn(metadata);
        whenNew(AutoDetectParser.class).withNoArguments().thenReturn(autoDetectParser);
        when(file.getName()).thenReturn("1420070400000");
        when(file.getAbsolutePath()).thenReturn("/path/to/file/1420070400000.jpg");
        when(metadata.getDate(any(Property.class))).thenReturn(new Date(1420070400000L));
    }

    @Test
    public void testGetDateFromFile_UsesImageMetadata_ExifDateTimeOriginal() throws Exception {
        ZonedDateTime zonedDateTime = FileDateUtil.getDateFromFile(file);

        verify(metadata).getDate(any(Property.class));

        assertThat(zonedDateTime.getYear(), is(2015));
        assertThat(zonedDateTime.getMonthValue(), is(1));
        assertThat(zonedDateTime.getDayOfMonth(), is(1));
        assertThat(zonedDateTime.getHour(), is(0));
        assertThat(zonedDateTime.getMinute(), is(0));
        assertThat(zonedDateTime.getSecond(), is(0));
        assertThat(zonedDateTime.getNano(), is(0));
    }

    @Test
    public void testGetDateFromFile_UsesImageMetadata_MetaCreationDate() throws Exception {
        when(metadata.getDate(Property.get(EXIF_DATE_TIME_ORIGINAL))).thenReturn(null)
                .thenReturn(new Date(1420070400000L));

        ZonedDateTime zonedDateTime = FileDateUtil.getDateFromFile(file);

        verify(metadata, times(2)).getDate(any(Property.class));

        assertThat(zonedDateTime.getYear(), is(2015));
        assertThat(zonedDateTime.getMonthValue(), is(1));
        assertThat(zonedDateTime.getDayOfMonth(), is(1));
        assertThat(zonedDateTime.getHour(), is(0));
        assertThat(zonedDateTime.getMinute(), is(0));
        assertThat(zonedDateTime.getSecond(), is(0));
        assertThat(zonedDateTime.getNano(), is(0));
    }

    @Test
    public void testGetDateFromFile_UsesTheFileNameIfNoImageMetadata_FileNameEpochMillis() throws Exception {
        when(file.getName()).thenReturn("1");
        when(metadata.getDate(any(Property.class))).thenReturn(null);

        ZonedDateTime zonedDateTime = FileDateUtil.getDateFromFile(file);

        verify(metadata, times(2)).getDate(any(Property.class));

        assertThat(zonedDateTime.getYear(), is(1970));
        assertThat(zonedDateTime.getMonthValue(), is(1));
        assertThat(zonedDateTime.getDayOfMonth(), is(1));
        assertThat(zonedDateTime.getHour(), is(0));
        assertThat(zonedDateTime.getMinute(), is(0));
        assertThat(zonedDateTime.getSecond(), is(0));
        assertThat(zonedDateTime.getNano(), is(1000000));
    }

    @Test
    public void testGetDateFromFile_UsesTheFileNameIfNoImageMetadata_FileNameDateUnderscoreTime() throws Exception {
        when(file.getName()).thenReturn("19700101_010101");
        when(metadata.getDate(any(Property.class))).thenReturn(null);

        ZonedDateTime zonedDateTime = FileDateUtil.getDateFromFile(file);

        verify(metadata, times(2)).getDate(any(Property.class));

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
        when(file.getName()).thenReturn("IMG_19700101_010101");
        when(metadata.getDate(any(Property.class))).thenReturn(null);

        ZonedDateTime zonedDateTime = FileDateUtil.getDateFromFile(file);

        verify(metadata, times(2)).getDate(any(Property.class));

        assertThat(zonedDateTime.getYear(), is(1970));
        assertThat(zonedDateTime.getMonthValue(), is(1));
        assertThat(zonedDateTime.getDayOfMonth(), is(1));
        assertThat(zonedDateTime.getHour(), is(1));
        assertThat(zonedDateTime.getMinute(), is(1));
        assertThat(zonedDateTime.getSecond(), is(1));
        assertThat(zonedDateTime.getNano(), is(0));
    }

    @Test
    public void testGetDateFromFile_NoMetadataAndUnknownFileNamePattern_ThrowsInvalidDateException() throws Exception {
        when(file.getName()).thenReturn("file");
        when(metadata.getDate(any(Property.class))).thenReturn(null);

        ZonedDateTime zonedDateTime = FileDateUtil.getDateFromFile(file);

        verify(metadata, times(2)).getDate(any(Property.class));

        assertThat(zonedDateTime, is(nullValue()));
    }
}
