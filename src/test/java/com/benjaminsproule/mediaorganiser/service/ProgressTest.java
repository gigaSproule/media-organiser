package com.benjaminsproule.mediaorganiser.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

import com.benjaminsproule.mediaorganiser.domain.Progress;

public class ProgressTest {

    @Test
    public void testReset_setsTotalNumberOfFiles_toZero() {
        Progress.reset();
        assertThat(Progress.getTotalNumberOfFiles(), is(0));
    }

    @Test
    public void testReset_setsNumberOfFilesProcessed_toZero() {
        Progress.reset();
        assertThat(Progress.getNumberOfFilesProcessed(), is(0));
    }

    @Test
    public void testInc_IncrementsNumberOfFilesProcessed() {
        Progress.reset();
        Progress.inc();
        assertThat(Progress.getNumberOfFilesProcessed(), is(1));
    }
}
