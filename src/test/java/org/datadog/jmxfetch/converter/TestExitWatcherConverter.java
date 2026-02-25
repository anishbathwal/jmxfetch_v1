package org.datadog.jmxfetch.converter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.datadog.jmxfetch.ExitWatcher;
import org.junit.Test;

public class TestExitWatcherConverter {

    @Test
    public void testConvertValidPath() {
        ExitWatcherConverter converter =
                new ExitWatcherConverter();
        ExitWatcher watcher = converter.convert(
                "/tmp/exit_file");
        assertNotNull(watcher);
        assertTrue(watcher.isEnabled());
        assertEquals(
                "/tmp/exit_file",
                watcher.getExitFileLocation());
    }

    @Test
    public void testConvertAnotherPath() {
        ExitWatcherConverter converter =
                new ExitWatcherConverter();
        ExitWatcher watcher = converter.convert(
                "/var/run/jmx.exit");
        assertNotNull(watcher);
        assertTrue(watcher.isEnabled());
        assertEquals(
                "/var/run/jmx.exit",
                watcher.getExitFileLocation());
    }
}
