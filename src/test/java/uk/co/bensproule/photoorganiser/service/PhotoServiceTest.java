package uk.co.bensproule.photoorganiser.service;

import org.apache.commons.imaging.ImageReadException;
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

import static java.util.Arrays.asList;
import static org.apache.commons.imaging.formats.tiff.constants.ExifTagConstants.EXIF_TAG_DATE_TIME_ORIGINAL;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.startsWith;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Imaging.class)
@PowerMockIgnore("javax.management.*")
public class PhotoServiceTest {
    @Mock
    private PhotoDao photoDao;
    @Mock
    private Path path;
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
    public void setup() throws IOException, ImageReadException {
        initMocks(this);
        mockStatic(Imaging.class);
        when(Imaging.getMetadata(any(File.class))).thenReturn(jpegImageMetadata);
        when(jpegImageMetadata.findEXIFValueWithExactMatch(EXIF_TAG_DATE_TIME_ORIGINAL)).thenReturn(tiffField);
        when(tiffField.getValue()).thenReturn("2015:01:01 00:00:00");
    }

    @Test
    public void testOrganiseCallsGetFilesWithInputDirectory() throws IOException, ImageReadException {
        when(photoDao.getFiles(anyString())).thenReturn(asList(path));
        photoService.organise("inputDirectory", "outputDirectory");
        verify(photoDao).getFiles("inputDirectory");
    }

    @Test(expected = NullPointerException.class)
    public void testOrganiseGetsTheMetadataFromTheFileIsNotExifFormat() throws IOException, ImageReadException {
        when(photoDao.getFiles(anyString())).thenReturn(asList(path));
        when(Imaging.getMetadata(any(File.class))).thenReturn(imageMetadata);
        photoService.organise("inputDirectory", "outputDirectory");
    }

    @Test
    public void testOrganiseGetsTheMetadataFromTheFileIsJpegFormat() throws IOException, ImageReadException {
        when(photoDao.getFiles(anyString())).thenReturn(asList(path));
        photoService.organise("inputDirectory", "outputDirectory");
        verify(photoDao).saveFile("outputDirectory/2015/01 - January/01", path);
        verifyStatic();
        Imaging.getMetadata(path.toFile());
    }

    // TODO: Allow test to run once able to get the date the photo was taken from Tiff files
    @Ignore
    @Test
    public void testOrganiseGetsTheMetadataFromTheFileIsTiffFormat() throws IOException, ImageReadException {
        when(photoDao.getFiles(anyString())).thenReturn(asList(path));
        when(Imaging.getMetadata(any(File.class))).thenReturn(tiffImageMetadata);
        photoService.organise("inputDirectory", "outputDirectory");
        verify(photoDao).saveFile("outputDirectory/2015/01/01", path);
        verifyStatic();
        Imaging.getMetadata(path.toFile());
    }

    @Test
    public void testOrganisePassesTheOutputDirectoryPathWithTheZonedDateTimeIntoSaveFiles() throws IOException, ImageReadException {
        when(photoDao.getFiles(anyString())).thenReturn(asList(path));

        photoService.organise("inputDirectory", "outputDirectory");

        verify(photoDao).saveFile("outputDirectory/2015/01 - January/01", path);
    }

    @Test
    public void testOrganiseDoesNotCallSaveFileIfNoFilesReturned() throws IOException, ImageReadException {
        when(photoDao.getFiles(anyString())).thenReturn(new ArrayList<>());
        photoService.organise("inputDirectory", "outputDirectory");
        verify(photoDao).getFiles("inputDirectory");
        verify(photoDao, never()).saveFile(anyString(), any(Path.class));
    }

    @Test
    public void testOrganiseCallsSaveFileWithOutputDirectory() throws IOException, ImageReadException {
        when(photoDao.getFiles(anyString())).thenReturn(asList(path));
        photoService.organise("inputDirectory", "outputDirectory");
        verify(photoDao).saveFile(startsWith("outputDirectory"), eq(path));
    }

    @Test
    public void testOrganiseThrowsIoExceptionIfDaoThrowsIoException() throws IOException, ImageReadException {
        doThrow(IOException.class).when(photoDao).saveFile(anyString(), any(Path.class));
        try {
            photoService.organise("inputDirectory", "outputDirectory");
        } catch (IOException e) {
            verify(photoDao).saveFile(startsWith("outputDirectory"), eq(path));
        }
    }
}