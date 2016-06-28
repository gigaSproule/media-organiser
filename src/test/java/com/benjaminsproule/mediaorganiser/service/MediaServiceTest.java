package com.benjaminsproule.mediaorganiser.service;

import com.benjaminsproule.mediaorganiser.dao.MediaDao;
import com.benjaminsproule.mediaorganiser.domain.DateConstants;
import com.benjaminsproule.mediaorganiser.domain.Progress;
import com.benjaminsproule.mediaorganiser.util.FileDateUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.jayway.awaitility.Awaitility.await;
import static java.util.Collections.singletonList;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(FileDateUtil.class)
@PowerMockIgnore("javax.management.*")
public class MediaServiceTest {
    @Mock
    private MediaDao mediaDao;
    @Mock
    private Path path;
    @Mock
    private File file;
    @Mock
    private FileDateUtil fileDateUtil;

    @InjectMocks
    private MediaService mediaService;

    @Before
    public void setup() throws Exception {
        initMocks(this);
        mockStatic(FileDateUtil.class);
        when(FileDateUtil.getDateFromFile(any(File.class)))
                .thenReturn(ZonedDateTime.ofInstant(Instant.ofEpochMilli(1420070400000L), ZoneId.of("UTC")));
        when(path.toFile()).thenReturn(file);
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
        verify(mediaDao).saveFile("outputDirectory/2015/01/01", path);
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
        verify(mediaDao).saveFile("outputDirectory/2015/01/01", path);
    }

    @Test
    public void testOrganise_CallsSaveFileWithCorrectOutputFormat_YYYYMMMMDD() throws Exception {
        when(mediaDao.getFiles(anyString())).thenReturn(singletonList(path));
        mediaService.organise("inputDirectory", "outputDirectory", DateConstants.YYYY_MMMM_DD);
        verify(mediaDao).saveFile("outputDirectory/2015/January/01", path);
    }

    @Test
    public void testOrganise_CallsSaveFileWithCorrectOutputFormat_YYYYMMMMMMDD() throws Exception {
        when(mediaDao.getFiles(anyString())).thenReturn(singletonList(path));
        mediaService.organise("inputDirectory", "outputDirectory", DateConstants.YYYY_MM_MMMM_DD);
        verify(mediaDao).saveFile("outputDirectory/2015/01 - January/01", path);
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
