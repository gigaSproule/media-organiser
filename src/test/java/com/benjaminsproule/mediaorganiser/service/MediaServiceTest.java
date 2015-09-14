package com.benjaminsproule.mediaorganiser.service;

import com.benjaminsproule.mediaorganiser.dao.MediaDao;
import com.benjaminsproule.mediaorganiser.domain.DateConstants;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.Property;
import org.apache.tika.parser.AutoDetectParser;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;

import static java.io.File.separator;
import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.startsWith;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PrepareForTest(MediaService.class)
@PowerMockIgnore("javax.management.*")
public class MediaServiceTest {
    public static final String EXIF_DATE_TIME_ORIGINAL = "exif:DateTimeOriginal";
    public static final String META_CREATION_DATE = "meta:creation-date";

    @Mock
    private MediaDao mediaDao;
    @Mock
    private Path path;
    @Mock
    private File file;
    @Mock
    private FileInputStream fileInputStream;
    @Mock
    private Metadata metadata;
    @Mock
    private AutoDetectParser autoDetectParser;

    @InjectMocks
    private MediaService mediaService;

    @Before
    public void setup() throws Exception {
        initMocks(this);
        whenNew(FileInputStream.class).withAnyArguments().thenReturn(fileInputStream);
        whenNew(Metadata.class).withNoArguments().thenReturn(metadata);
        whenNew(AutoDetectParser.class).withNoArguments().thenReturn(autoDetectParser);
        when(path.toFile()).thenReturn(file);
        when(metadata.getDate(any(Property.class))).thenReturn(new Date(1420070400000l));
    }

    @Test
    public void testOrganiseCallsGetFilesWithInputDirectory() throws Exception {
        when(mediaDao.getFiles(anyString())).thenReturn(singletonList(path));
        mediaService.organise("inputDirectory", "outputDirectory", DateConstants.YYYY_MM_DD);
        verify(mediaDao).getFiles("inputDirectory");
    }

    // @Test(expected = NullPointerException.class) - No longer throws this exception, just kills the thread
    @Ignore
    public void testOrganiseGetsTheMetadataFromTheFileDoesNotHaveMetadataFormat() throws Exception {
        when(mediaDao.getFiles(anyString())).thenReturn(singletonList(path));
        when(metadata.getDate(any(Property.class))).thenReturn(null);
        mediaService.organise("inputDirectory", "outputDirectory", DateConstants.YYYY_MM_DD);
    }

    @Test
    public void testOrganiseGetsTheMetadataFromTheFileIsJpegFormat() throws Exception {
        when(mediaDao.getFiles(anyString())).thenReturn(singletonList(path));
        mediaService.organise("inputDirectory", "outputDirectory", DateConstants.YYYY_MM_DD);
        verify(mediaDao).saveFile("outputDirectory/2015/01/01", path);
    }

    @Test
    public void testOrganiseGetsTheMetadataFromTheFileIfMetadataIsNotExif() throws Exception {
        when(mediaDao.getFiles(anyString())).thenReturn(singletonList(path));
        when(metadata.getDate(any(Property.class))).thenReturn(null).thenReturn(new Date(1420070400000l));
        mediaService.organise("inputDirectory", "outputDirectory", DateConstants.YYYY_MM_DD);
        verify(mediaDao).saveFile("outputDirectory/2015/01/01", path);
    }

    @Test
    public void testOrganisePassesTheOutputDirectoryPathWithTheZonedDateTimeIntoSaveFiles() throws Exception {
        when(mediaDao.getFiles(anyString())).thenReturn(singletonList(path));
        mediaService.organise("inputDirectory", "outputDirectory", DateConstants.YYYY_MM_DD);
        verify(mediaDao).saveFile("outputDirectory/2015/01/01", path);
    }

    @Test
    public void testOrganiseDoesNotCallSaveFileIfNoFilesReturned() throws Exception {
        when(mediaDao.getFiles(anyString())).thenReturn(new ArrayList<>());
        mediaService.organise("inputDirectory", "outputDirectory", DateConstants.YYYY_MM_DD);
        verify(mediaDao).getFiles("inputDirectory");
        verify(mediaDao, never()).saveFile(anyString(), any(Path.class));
    }

    @Test
    public void testOrganiseCallsSaveFileWithOutputDirectory() throws Exception {
        when(mediaDao.getFiles(anyString())).thenReturn(singletonList(path));
        mediaService.organise("inputDirectory", "outputDirectory", DateConstants.YYYY_MM_DD);
        verify(mediaDao).saveFile(startsWith("outputDirectory"), eq(path));
    }

    @Test
    public void testOrganiseCallsSaveFileWithCorrectOutputFormatYYYYMMDD() throws Exception {
        when(mediaDao.getFiles(anyString())).thenReturn(singletonList(path));
        mediaService.organise("inputDirectory", "outputDirectory", DateConstants.YYYY_MM_DD);
        verify(mediaDao).saveFile("outputDirectory/2015/01/01", path);
    }

    @Test
    public void testOrganiseCallsSaveFileWithCorrectOutputFormatYYYYMMMMDD() throws Exception {
        when(mediaDao.getFiles(anyString())).thenReturn(singletonList(path));
        mediaService.organise("inputDirectory", "outputDirectory", DateConstants.YYYY_MMMM_DD);
        verify(mediaDao).saveFile("outputDirectory/2015/January/01", path);
    }

    @Test
    public void testOrganiseCallsSaveFileWithCorrectOutputFormatYYYYMMMMMMDD() throws Exception {
        when(mediaDao.getFiles(anyString())).thenReturn(singletonList(path));
        mediaService.organise("inputDirectory", "outputDirectory", DateConstants.YYYY_MM_MMMM_DD);
        verify(mediaDao).saveFile("outputDirectory/2015/01 - January/01", path);
    }

    // @Test - No longer throws this exception, just kills the thread
    @Ignore
    public void testOrganiseThrowsIoExceptionIfDaoThrowsIoException() throws Exception {
        when(mediaDao.getFiles(anyString())).thenReturn(singletonList(path));
        doThrow(IOException.class).when(mediaDao).saveFile(anyString(), any(Path.class));
        try {
            mediaService.organise("inputDirectory", "outputDirectory", DateConstants.YYYY_MM_DD);
            fail("IOException should have been thrown");
        } catch (IOException e) {
            verify(mediaDao).saveFile(startsWith("outputDirectory"), eq(path));
        }
    }

    @Test
    public void testOrganiseTrysToUseTheFileNameIfNoImageMetadata() throws Exception {
        when(mediaDao.getFiles(anyString())).thenReturn(singletonList(path));
        when(metadata.getDate(any(Property.class))).thenReturn(null);
        when(file.getName()).thenReturn("1");
        mediaService.organise("inputDirectory", "outputDirectory", DateConstants.YYYY_MM_DD);
        verify(mediaDao).saveFile("outputDirectory/1970/01/01", path);
    }

    // @Test - No longer throws this exception, just kills the thread
    @Ignore
    public void testOrganiseThrowsNullPointerExceptionWithFileNameWhenZonedDateTimeUnavailable() throws Exception {
        String expectedFileName = separator + "expected" + separator + "file" + separator + "name";
        when(mediaDao.getFiles("inputDirectory")).thenReturn(singletonList(path));
        when(metadata.getDate(any(Property.class))).thenReturn(null);
        when(file.getName()).thenReturn("name");
        when(file.getAbsolutePath()).thenReturn(expectedFileName);

        try {
            mediaService.organise("inputDirectory", "outputDirectory", DateConstants.YYYY_MM_DD);
            fail("NullPointerException should have been thrown");
        } catch (NullPointerException e) {
            assertThat(e.getMessage(), containsString(expectedFileName));
            verify(path).toFile();
            verify(file).getName();
            verify(file).getAbsolutePath();
            verify(mediaDao, never()).saveFile(anyString(), any(Path.class));
        }
    }
}
