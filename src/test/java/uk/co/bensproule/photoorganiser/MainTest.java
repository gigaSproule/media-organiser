package uk.co.bensproule.photoorganiser;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import uk.co.bensproule.photoorganiser.service.PhotoService;
import uk.co.bensproule.photoorganiser.util.MimeTypesUtil;

import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;
import static uk.co.bensproule.photoorganiser.domain.DateConstants.YYYY_MM_DD;
import static uk.co.bensproule.photoorganiser.test.Constants.DESTINATION_PATH;
import static uk.co.bensproule.photoorganiser.test.Constants.SOURCE_PATH;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Main.class, MimeTypesUtil.class})
@PowerMockIgnore("org.apache.logging.log4j.core.jmx.*")
public class MainTest {
    @Mock
    private PhotoService photoService;

    @Before
    public void setup() throws Exception {
        mockStatic(MimeTypesUtil.class);

        whenNew(PhotoService.class).withNoArguments().thenReturn(photoService);
        doNothing().when(photoService).organise(anyString(), anyString(), anyString());

        when(MimeTypesUtil.requiresMimeTypesFile()).thenReturn(false);
    }

    @Test
    public void testMainDoesNotCallCreateMimeTypesFileIfRequiresMimeTypesFileFalse() throws Exception {
        String[] args = new String[]{"-id", SOURCE_PATH, "-od", DESTINATION_PATH, "-of", YYYY_MM_DD};
        Main.main(args);

        verifyStatic();
        MimeTypesUtil.requiresMimeTypesFile();
        verifyStatic(never());
        MimeTypesUtil.createMimeTypesFile();
    }

    @Test
    public void testMainCallsCreateMimeTypesFileIfRequiresMimeTypesFileTrue() throws Exception {
        when(MimeTypesUtil.requiresMimeTypesFile()).thenReturn(true);

        String[] args = new String[]{"-id", SOURCE_PATH, "-od", DESTINATION_PATH, "-of", YYYY_MM_DD};
        Main.main(args);

        verifyStatic();
        MimeTypesUtil.requiresMimeTypesFile();
        MimeTypesUtil.createMimeTypesFile();
    }

    @Test
    public void testMainThrowsIllegalArgumentExceptionWhenInputFileArgumentMissing() throws Exception {
        String[] args = new String[]{"-od", DESTINATION_PATH, "-of", YYYY_MM_DD};
        try {
            Main.main(args);
            fail("IllegalArgumentException should have been thrown");
        } catch (IllegalArgumentException e) {
            verify(photoService, never()).organise(anyString(), anyString(), anyString());
        }
    }

    @Test
    public void testMainThrowsIllegalArgumentExceptionWhenDESTINATION_PATHArgumentMissing() throws Exception {
        String[] args = new String[]{"-id", SOURCE_PATH, "-of", YYYY_MM_DD};
        try {
            Main.main(args);
            fail("IllegalArgumentException should have been thrown");
        } catch (IllegalArgumentException e) {
            verify(photoService, never()).organise(anyString(), anyString(), anyString());
        }
    }

    @Test
    public void testMainThrowsIllegalArgumentExceptionWhenOutputFormatArgumentMissing() throws Exception {
        String[] args = new String[]{"-id", SOURCE_PATH, "-od", DESTINATION_PATH};
        try {
            Main.main(args);
            fail("IllegalArgumentException should have been thrown");
        } catch (IllegalArgumentException e) {
            verify(photoService, never()).organise(anyString(), anyString(), anyString());
        }
    }

    @Test
    public void testMainDoesNotThrowIllegalArgumentExceptionWhenArgumentsPassed() throws Exception {
        String[] args = new String[]{"-id", SOURCE_PATH, "-od", DESTINATION_PATH, "-of", YYYY_MM_DD};
        Main.main(args);
        verify(photoService).organise(anyString(), anyString(), anyString());
    }
}
