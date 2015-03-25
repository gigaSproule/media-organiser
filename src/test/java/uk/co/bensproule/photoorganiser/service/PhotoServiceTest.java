package uk.co.bensproule.photoorganiser.service;

import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.apache.commons.imaging.formats.tiff.TiffField;
import org.apache.commons.imaging.formats.tiff.TiffImageMetadata;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import uk.co.bensproule.photoorganiser.dao.PhotoDao;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;

import static java.io.File.separator;
import static java.util.Collections.singletonList;
import static org.apache.commons.imaging.formats.tiff.constants.ExifTagConstants.EXIF_TAG_DATE_TIME_ORIGINAL;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.startsWith;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static uk.co.bensproule.photoorganiser.domain.DateConstants.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Imaging.class)
@PowerMockIgnore("javax.management.*")
public class PhotoServiceTest {
    @Mock
    private PhotoDao photoDao;
    @Mock
    private Path path;
    @Mock
    private File file;
    @Mock
    private JpegImageMetadata jpegImageMetadata;
    @Mock
    private TiffImageMetadata tiffImageMetadata;
    @Mock
    private ImageMetadata imageMetadata;
    @Mock
    private TiffField tiffField;

    @InjectMocks
    private PhotoService photoService;

    @Before
    public void setup() throws Exception {
        initMocks(this);
        mockStatic(Imaging.class);
        when(path.toFile()).thenReturn(file);
        when(Imaging.getMetadata(any(File.class))).thenReturn(jpegImageMetadata);
        when(jpegImageMetadata.findEXIFValueWithExactMatch(EXIF_TAG_DATE_TIME_ORIGINAL)).thenReturn(tiffField);
        when(tiffField.getValue()).thenReturn("2015:01:01 00:00:00");
    }

    @Test
    public void testOrganiseCallsGetFilesWithInputDirectory() throws Exception {
        when(photoDao.getFiles(anyString())).thenReturn(singletonList(path));
        photoService.organise("inputDirectory", "outputDirectory", YYYY_MM_DD);
        verify(photoDao).getFiles("inputDirectory");
    }

    @Test(expected = NullPointerException.class)
    public void testOrganiseGetsTheMetadataFromTheFileIsNotExifFormat() throws Exception {
        when(photoDao.getFiles(anyString())).thenReturn(singletonList(path));
        when(Imaging.getMetadata(any(File.class))).thenReturn(imageMetadata);
        photoService.organise("inputDirectory", "outputDirectory", YYYY_MM_DD);
    }

    @Test
    public void testOrganiseGetsTheMetadataFromTheFileIsJpegFormat() throws Exception {
        when(photoDao.getFiles(anyString())).thenReturn(singletonList(path));
        photoService.organise("inputDirectory", "outputDirectory", YYYY_MM_DD);
        verify(photoDao).saveFile("outputDirectory/2015/01/01", path);
        verifyStatic();
        Imaging.getMetadata(path.toFile());
    }

    // TODO: Allow test to run once able to get the date the photo was taken from Tiff files
    @Ignore
    @Test
    public void testOrganiseGetsTheMetadataFromTheFileIsTiffFormat() throws Exception {
        when(photoDao.getFiles(anyString())).thenReturn(singletonList(path));
        when(Imaging.getMetadata(any(File.class))).thenReturn(tiffImageMetadata);
        photoService.organise("inputDirectory", "outputDirectory", YYYY_MM_DD);
        verify(photoDao).saveFile("outputDirectory/2015/01/01", path);
        verifyStatic();
        Imaging.getMetadata(path.toFile());
    }

    @Test
    public void testOrganisePassesTheOutputDirectoryPathWithTheZonedDateTimeIntoSaveFiles() throws Exception {
        when(photoDao.getFiles(anyString())).thenReturn(singletonList(path));
        photoService.organise("inputDirectory", "outputDirectory", YYYY_MM_DD);
        verify(photoDao).saveFile("outputDirectory/2015/01/01", path);
    }

    @Test
    public void testOrganiseDoesNotCallSaveFileIfNoFilesReturned() throws Exception {
        when(photoDao.getFiles(anyString())).thenReturn(new ArrayList<>());
        photoService.organise("inputDirectory", "outputDirectory", YYYY_MM_DD);
        verify(photoDao).getFiles("inputDirectory");
        verify(photoDao, never()).saveFile(anyString(), any(Path.class));
    }

    @Test
    public void testOrganiseCallsSaveFileWithOutputDirectory() throws Exception {
        when(photoDao.getFiles(anyString())).thenReturn(singletonList(path));
        photoService.organise("inputDirectory", "outputDirectory", YYYY_MM_DD);
        verify(photoDao).saveFile(startsWith("outputDirectory"), eq(path));
    }

    @Test
    public void testOrganiseCallsSaveFileWithCorrectOutputFormatYYYYMMDD() throws Exception {
        when(photoDao.getFiles(anyString())).thenReturn(singletonList(path));
        photoService.organise("inputDirectory", "outputDirectory", YYYY_MM_DD);
        verify(photoDao).saveFile("outputDirectory/2015/01/01", path);
    }

    @Test
    public void testOrganiseCallsSaveFileWithCorrectOutputFormatYYYYMMMMDD() throws Exception {
        when(photoDao.getFiles(anyString())).thenReturn(singletonList(path));
        photoService.organise("inputDirectory", "outputDirectory", YYYY_MMMM_DD);
        verify(photoDao).saveFile("outputDirectory/2015/January/01", path);
    }

    @Test
    public void testOrganiseCallsSaveFileWithCorrectOutputFormatYYYYMMMMMMDD() throws Exception {
        when(photoDao.getFiles(anyString())).thenReturn(singletonList(path));
        photoService.organise("inputDirectory", "outputDirectory", YYYY_MM_MMMM_DD);
        verify(photoDao).saveFile("outputDirectory/2015/01 - January/01", path);
    }

    @Test
    public void testOrganiseThrowsIoExceptionIfDaoThrowsIoException() throws Exception {
        when(photoDao.getFiles(anyString())).thenReturn(singletonList(path));
        doThrow(IOException.class).when(photoDao).saveFile(anyString(), any(Path.class));
        try {
            photoService.organise("inputDirectory", "outputDirectory", YYYY_MM_DD);
            fail("IOException should have been thrown");
        } catch (IOException e) {
            verify(photoDao).saveFile(startsWith("outputDirectory"), eq(path));
        }
    }

    @Test
    public void testOrganiseTrysToUseTheFileNameIfNoImageMetadata() throws Exception {
        when(photoDao.getFiles(anyString())).thenReturn(singletonList(path));
        when(Imaging.getMetadata(file)).thenReturn(null);
        when(file.getName()).thenReturn("1");
        photoService.organise("inputDirectory", "outputDirectory", YYYY_MM_DD);
        verify(photoDao).saveFile("outputDirectory/1970/01/01", path);
    }

    @Test
    public void testOrganiseThrowsNullPointerExceptionWithFileNameWhenZonedDateTimeUnavailable() throws Exception {
        String expectedFileName = separator + "expected" + separator + "file" + separator + "name";
        when(photoDao.getFiles("inputDirectory")).thenReturn(singletonList(path));
        when(Imaging.getMetadata(any(File.class))).thenReturn(null);
        when(file.getName()).thenReturn("name");
        when(file.getAbsolutePath()).thenReturn(expectedFileName);

        try {
            photoService.organise("inputDirectory", "outputDirectory", YYYY_MM_DD);
            fail("NullPointerException should have been thrown");
        } catch (NullPointerException e) {
            assertThat(e.getMessage(), containsString(expectedFileName));
            verify(path).toFile();
            verify(file).getName();
            verify(file).getAbsolutePath();
            verify(photoDao, never()).saveFile(anyString(), any(Path.class));
            verifyStatic();
            Imaging.getMetadata(path.toFile());
        }
    }
}
