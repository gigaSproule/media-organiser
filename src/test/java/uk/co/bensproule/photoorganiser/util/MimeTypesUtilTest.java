package uk.co.bensproule.photoorganiser.util;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(MimeTypesUtil.class)
@PowerMockIgnore("org.apache.logging.log4j.core.jmx.*")
public class MimeTypesUtilTest {
    @Before
    public void setup() {
        mockStatic(System.class);
    }

    @Test
    public void requiresMimeTypesFile_ReturnsFalse_Windows() {
        when(System.getProperty("os.name")).thenReturn("Windows");
        assertThat(MimeTypesUtil.requiresMimeTypesFile(), is(false));
    }

    @Test
    public void requiresMimeTypesFile_ReturnsFalse_Unkown() {
        when(System.getProperty("os.name")).thenReturn("Unkown");
        assertThat(MimeTypesUtil.requiresMimeTypesFile(), is(false));
    }

    @Test
    public void requiresMimeTypesFile_ReturnsTrue_Mac() {
        when(System.getProperty("os.name")).thenReturn("Mac OS X");
        assertThat(MimeTypesUtil.requiresMimeTypesFile(), is(true));
    }

    @Test
    public void requiresMimeTypesFile_ReturnsTrue_Linux() {
        when(System.getProperty("os.name")).thenReturn("Linux");
        assertThat(MimeTypesUtil.requiresMimeTypesFile(), is(true));
    }
}
