package org.datadog.jmxfetch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

public class ExitWatcherTest {

    // === Positive tests ===

    @Test
    public void testDefaultConstructor() {
        ExitWatcher watcher = new ExitWatcher();
        assertFalse(watcher.isEnabled());
        assertNull(watcher.getExitFileLocation());
        assertFalse(watcher.shouldExit());
    }

    @Test
    public void testConstructorWithPath() {
        ExitWatcher watcher = new ExitWatcher("/tmp/exit_file");
        assertTrue(watcher.isEnabled());
        assertEquals("/tmp/exit_file",
                watcher.getExitFileLocation());
    }

    @Test
    public void testShouldExitWhenFileExists() throws IOException {
        File tempFile = File.createTempFile(
                "exit_test", ".tmp");
        tempFile.deleteOnExit();

        ExitWatcher watcher = new ExitWatcher(
                tempFile.getAbsolutePath());
        assertTrue(watcher.isEnabled());
        assertTrue(watcher.shouldExit());
    }

    @Test
    public void testShouldNotExitWhenFileNotExists() {
        ExitWatcher watcher = new ExitWatcher(
                "/tmp/nonexistent_exit_file_" + System.nanoTime());
        assertTrue(watcher.isEnabled());
        assertFalse(watcher.shouldExit());
    }

    @Test
    public void testShouldNotExitWhenDirectory() throws IOException {
        File tempDir = new File(
                System.getProperty("java.io.tmpdir"),
                "exit_test_dir_" + System.nanoTime());
        tempDir.mkdirs();
        tempDir.deleteOnExit();

        ExitWatcher watcher = new ExitWatcher(
                tempDir.getAbsolutePath());
        assertTrue(watcher.isEnabled());
        assertFalse(watcher.shouldExit());
    }

    @Test
    public void testToString() {
        ExitWatcher watcher = new ExitWatcher("/tmp/exit");
        assertEquals("/tmp/exit", watcher.toString());
    }

    @Test
    public void testToStringNull() {
        ExitWatcher watcher = new ExitWatcher();
        assertNull(watcher.toString());
    }

    // === Negative tests ===

    @Test
    public void testConstructorWithNull() {
        ExitWatcher watcher = new ExitWatcher(null);
        assertFalse(watcher.isEnabled());
        assertFalse(watcher.shouldExit());
    }

    @Test
    public void testShouldExitWhenDisabled() {
        ExitWatcher watcher = new ExitWatcher();
        assertFalse(watcher.shouldExit());
    }
}
