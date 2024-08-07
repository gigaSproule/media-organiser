package com.benjaminsproule.mediaorganiser;

import static java.util.Collections.emptyList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;

import com.benjaminsproule.mediaorganiser.domain.DateConstants;
import com.benjaminsproule.mediaorganiser.service.MediaService;
import com.benjaminsproule.mediaorganiser.test.Constants;
import com.benjaminsproule.mediaorganiser.util.MimeTypesUtil;

public class MainTest {
    private MockedStatic<MimeTypesUtil> mockedMimeTypesUtil;
    private MockedConstruction<MediaService> mockedMediaService;

    @BeforeEach
    public void setup() throws Exception {
        mockedMimeTypesUtil = mockStatic(MimeTypesUtil.class);
        mockedMimeTypesUtil.when(MimeTypesUtil::requiresMimeTypesFile).thenReturn(false);

        mockedMediaService = mockConstruction(MediaService.class, (mock, context) -> {
            when(mock.organise(anyString(), anyString(), anyString())).thenReturn(emptyList());
        });
    }

    @AfterEach
    public void tearDown() {
        mockedMimeTypesUtil.close();
        mockedMediaService.close();
    }

    @Test
    public void testMainDoesNotCallCreateMimeTypesFileIfRequiresMimeTypesFileFalse() throws Exception {
        String[] args = new String[] { "-id", Constants.SOURCE_PATH, "-od", Constants.DESTINATION_PATH, "-of",
                DateConstants.YYYY_MM_DD };
        Main.main(args);

        mockedMimeTypesUtil.verify(MimeTypesUtil::requiresMimeTypesFile);
        mockedMimeTypesUtil.verify(MimeTypesUtil::createMimeTypesFile, never());
    }

    @Test
    public void testMainCallsCreateMimeTypesFileIfRequiresMimeTypesFileTrue() throws Exception {
        mockedMimeTypesUtil.when(MimeTypesUtil::requiresMimeTypesFile).thenReturn(true);

        String[] args = new String[] { "-id", Constants.SOURCE_PATH, "-od", Constants.DESTINATION_PATH, "-of",
                DateConstants.YYYY_MM_DD };
        Main.main(args);

        mockedMimeTypesUtil.verify(MimeTypesUtil::requiresMimeTypesFile);
        mockedMimeTypesUtil.verify(MimeTypesUtil::createMimeTypesFile);
    }

    @Test
    public void testMainThrowsIllegalArgumentExceptionWhenInputFileArgumentMissing() throws Exception {
        String[] args = new String[] { "-od", Constants.DESTINATION_PATH, "-of", DateConstants.YYYY_MM_DD };
        try {
            Main.main(args);
            fail("IllegalArgumentException should have been thrown");
        } catch (IllegalArgumentException e) {
            assertThat(mockedMediaService.constructed(), empty());
        }
    }

    @Test
    public void testMainThrowsIllegalArgumentExceptionWhenDESTINATION_PATHArgumentMissing() throws Exception {
        String[] args = new String[] { "-id", Constants.SOURCE_PATH, "-of", DateConstants.YYYY_MM_DD };
        try {
            Main.main(args);
            fail("IllegalArgumentException should have been thrown");
        } catch (IllegalArgumentException e) {
            assertThat(mockedMediaService.constructed(), empty());
        }
    }

    @Test
    public void testMainThrowsIllegalArgumentExceptionWhenOutputFormatArgumentMissing() throws Exception {
        String[] args = new String[] { "-id", Constants.SOURCE_PATH, "-od", Constants.DESTINATION_PATH };
        try {
            Main.main(args);
            fail("IllegalArgumentException should have been thrown");
        } catch (IllegalArgumentException e) {
            assertThat(mockedMediaService.constructed(), empty());
        }
    }

    @Test
    public void testMainDoesNotThrowIllegalArgumentExceptionWhenArgumentsPassed() throws Exception {
        String[] args = new String[] { "-id", Constants.SOURCE_PATH, "-od", Constants.DESTINATION_PATH, "-of",
                DateConstants.YYYY_MM_DD };
        Main.main(args);
        verify(mockedMediaService.constructed().get(0)).organise(anyString(), anyString(), anyString());
    }
}
