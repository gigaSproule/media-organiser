package com.benjaminsproule.mediaorganiser.service;

import com.benjaminsproule.mediaorganiser.dao.MediaDao;
import com.benjaminsproule.mediaorganiser.domain.DateConstants;
import com.benjaminsproule.mediaorganiser.domain.Progress;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static com.jayway.awaitility.Awaitility.await;
import static java.util.Collections.singletonList;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

public class MediaServiceTest {
    @Mock
    private MediaDao mediaDao;

    @InjectMocks
    private MediaService mediaService;
    private Path path;

    @BeforeEach
    public void setup() throws Exception {
        openMocks(this).close();
        URL url = getClass().getClassLoader().getResource("image.jpg");
        path = new File(url.toURI()).toPath();
    }

    @Test
    public void testOrganise_CallsGetFilesWithInputDirectory() throws Exception {
        when(mediaDao.getFiles(anyString())).thenReturn(singletonList(path));
        mediaService.organise("inputDirectory", "outputDirectory", DateConstants.YYYY_MM_DD);
        verify(mediaDao).getFiles("inputDirectory");
    }

    @Test
    public void testOrganise_PassesTheOutputDirectoryPathWithTheZonedDateTimeIntoSaveFiles() throws Exception {
        when(mediaDao.getFiles(anyString())).thenReturn(singletonList(path));
        mediaService.organise("inputDirectory", "outputDirectory", DateConstants.YYYY_MM_DD);
        verify(mediaDao).saveFile("outputDirectory/2015/02/15", path);
    }

    @Test
    public void testOrganise_DoesNotCallSaveFile_NoFilesReturned() throws Exception {
        when(mediaDao.getFiles(anyString())).thenReturn(new ArrayList<>());
        mediaService.organise("inputDirectory", "outputDirectory", DateConstants.YYYY_MM_DD);
        verify(mediaDao).getFiles("inputDirectory");
        verify(mediaDao, never()).saveFile(anyString(), any(Path.class));
    }

    @Test
    public void testOrganise_CallsSaveFileWithCorrectOutputFormat_YYYYMMDD() throws Exception {
        when(mediaDao.getFiles(anyString())).thenReturn(singletonList(path));
        mediaService.organise("inputDirectory", "outputDirectory", DateConstants.YYYY_MM_DD);
        verify(mediaDao).saveFile("outputDirectory/2015/02/15", path);
    }

    @Test
    public void testOrganise_CallsSaveFileWithCorrectOutputFormat_YYYYMMMMDD() throws Exception {
        when(mediaDao.getFiles(anyString())).thenReturn(singletonList(path));
        mediaService.organise("inputDirectory", "outputDirectory", DateConstants.YYYY_MMMM_DD);
        verify(mediaDao).saveFile("outputDirectory/2015/February/15", path);
    }

    @Test
    public void testOrganise_CallsSaveFileWithCorrectOutputFormat_YYYYMMMMMMDD() throws Exception {
        when(mediaDao.getFiles(anyString())).thenReturn(singletonList(path));
        mediaService.organise("inputDirectory", "outputDirectory", DateConstants.YYYY_MM_MMMM_DD);
        verify(mediaDao).saveFile("outputDirectory/2015/02 - February/15", path);
    }

    @Test
    public void testOrganise_MediaDaoThrowsIoException() throws Exception {
        when(mediaDao.getFiles(anyString())).thenReturn(singletonList(path));
        doThrow(new IOException("IOException that was thrown")).when(mediaDao).saveFile(anyString(), any(Path.class));
        List<String> errors = mediaService.organise("inputDirectory", "outputDirectory", DateConstants.YYYY_MM_DD);
        await().atMost(5, SECONDS)
            .until(() -> Progress.getNumberOfFilesProcessed() == Progress.getTotalNumberOfFiles());

        assertThat(errors, hasSize(1));
        assertThat(errors.get(0), is("IOException that was thrown"));
    }
}
