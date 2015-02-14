package uk.co.bensproule.photoorganiser.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import uk.co.bensproule.photoorganiser.dao.PhotoDao;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;

import static java.util.Arrays.asList;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.startsWith;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class PhotoServiceTest {
    @Mock
    private PhotoDao photoDao;
    @Mock
    private File file;
    @Mock
    private Path path;

    @InjectMocks
    private PhotoService photoService;

    @Before
    public void setup() {
        initMocks(this);
        when(file.getName()).thenReturn("20150101_000000");
        when(file.toPath()).thenReturn(path);
    }

    @Test
    public void testOrganiseCallsGetFilesWithInputDirectory() throws IOException {
        when(photoDao.getFiles(anyString())).thenReturn(asList(file));
        photoService.organise("inputDirectory", "outputDirectory");
        verify(photoDao).getFiles("inputDirectory");
    }

    @Test
    public void testOrganiseGetsTheFileNameFromTheFile() throws IOException {
        when(photoDao.getFiles(anyString())).thenReturn(asList(file));
        photoService.organise("inputDirectory", "outputDirectory");
        verify(file).getName();
    }

    @Test
    public void testOrganisePassesTheOutputDirectoryPathWithTheZonedDateTimeIntoSaveFiles() throws IOException {
        when(photoDao.getFiles(anyString())).thenReturn(asList(file));

        photoService.organise("inputDirectory", "outputDirectory");

        verify(file).getName();
        verify(photoDao).saveFile("outputDirectory/2015/01/01", file.toPath());
    }

    @Test
    public void testOrganiseDoesNotCallSaveFileIfNoFilesReturned() throws IOException {
        when(photoDao.getFiles(anyString())).thenReturn(new ArrayList<>());
        photoService.organise("inputDirectory", "outputDirectory");
        verify(photoDao).getFiles("inputDirectory");
        verify(photoDao, never()).saveFile(anyString(), any(Path.class));
    }

    @Test
    public void testOrganiseCallsSaveFileWithOutputDirectory() throws IOException {
        when(photoDao.getFiles(anyString())).thenReturn(asList(file));
        photoService.organise("inputDirectory", "outputDirectory");
        verify(photoDao).saveFile(startsWith("outputDirectory"), eq(path));
    }

    @Test
    public void testOrganiseThrowsIoExceptionIfDaoThrowsIoException() throws IOException {
        doThrow(IOException.class).when(photoDao).saveFile(anyString(), any(Path.class));
        try {
            photoService.organise("inputDirectory", "outputDirectory");
        } catch (IOException e) {
            verify(photoDao).saveFile(startsWith("outputDirectory"), eq(path));
        }

    }

}