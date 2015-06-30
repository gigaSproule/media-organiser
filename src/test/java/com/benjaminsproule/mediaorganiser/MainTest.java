package com.benjaminsproule.mediaorganiser;

import com.benjaminsproule.mediaorganiser.domain.DateConstants;
import com.benjaminsproule.mediaorganiser.service.MediaService;
import com.benjaminsproule.mediaorganiser.test.Constants;
import com.benjaminsproule.mediaorganiser.util.MimeTypesUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Main.class, MimeTypesUtil.class})
@PowerMockIgnore("org.apache.logging.log4j.core.jmx.*")
public class MainTest {
    @Mock
    private MediaService mediaService;

    @Before
    public void setup() throws Exception {
        mockStatic(MimeTypesUtil.class);

        whenNew(MediaService.class).withNoArguments().thenReturn(mediaService);
        doNothing().when(mediaService).organise(anyString(), anyString(), anyString());

        when(MimeTypesUtil.requiresMimeTypesFile()).thenReturn(false);
    }

    @Test
    public void testMainDoesNotCallCreateMimeTypesFileIfRequiresMimeTypesFileFalse() throws Exception {
        String[] args = new String[]{"-id", Constants.SOURCE_PATH, "-od", Constants.DESTINATION_PATH, "-of", DateConstants.YYYY_MM_DD};
        Main.main(args);

        verifyStatic();
        MimeTypesUtil.requiresMimeTypesFile();
        verifyStatic(never());
        MimeTypesUtil.createMimeTypesFile();
    }

    @Test
    public void testMainCallsCreateMimeTypesFileIfRequiresMimeTypesFileTrue() throws Exception {
        when(MimeTypesUtil.requiresMimeTypesFile()).thenReturn(true);

        String[] args = new String[]{"-id", Constants.SOURCE_PATH, "-od", Constants.DESTINATION_PATH, "-of", DateConstants.YYYY_MM_DD};
        Main.main(args);

        verifyStatic();
        MimeTypesUtil.requiresMimeTypesFile();
        MimeTypesUtil.createMimeTypesFile();
    }

    @Test
    public void testMainThrowsIllegalArgumentExceptionWhenInputFileArgumentMissing() throws Exception {
        String[] args = new String[]{"-od", Constants.DESTINATION_PATH, "-of", DateConstants.YYYY_MM_DD};
        try {
            Main.main(args);
            fail("IllegalArgumentException should have been thrown");
        } catch (IllegalArgumentException e) {
            verify(mediaService, never()).organise(anyString(), anyString(), anyString());
        }
    }

    @Test
    public void testMainThrowsIllegalArgumentExceptionWhenDESTINATION_PATHArgumentMissing() throws Exception {
        String[] args = new String[]{"-id", Constants.SOURCE_PATH, "-of", DateConstants.YYYY_MM_DD};
        try {
            Main.main(args);
            fail("IllegalArgumentException should have been thrown");
        } catch (IllegalArgumentException e) {
            verify(mediaService, never()).organise(anyString(), anyString(), anyString());
        }
    }

    @Test
    public void testMainThrowsIllegalArgumentExceptionWhenOutputFormatArgumentMissing() throws Exception {
        String[] args = new String[]{"-id", Constants.SOURCE_PATH, "-od", Constants.DESTINATION_PATH};
        try {
            Main.main(args);
            fail("IllegalArgumentException should have been thrown");
        } catch (IllegalArgumentException e) {
            verify(mediaService, never()).organise(anyString(), anyString(), anyString());
        }
    }

    @Test
    public void testMainDoesNotThrowIllegalArgumentExceptionWhenArgumentsPassed() throws Exception {
        String[] args = new String[]{"-id", Constants.SOURCE_PATH, "-od", Constants.DESTINATION_PATH, "-of", DateConstants.YYYY_MM_DD};
        Main.main(args);
        verify(mediaService).organise(anyString(), anyString(), anyString());
    }
}
