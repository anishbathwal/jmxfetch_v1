package org.datadog.jmxfetch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class TestExitWatcher {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Test
    public void testDefaultConstructorIsDisabled() {
        ExitWatcher watcher = new ExitWatcher();
        assertFalse(watcher.isEnabled());
        assertNull(watcher.getExitFileLocation());
    }

    @Test
    public void testConstructorWithNullIsDisabled() {
        ExitWatcher watcher = new ExitWatcher(null);
        assertFalse(watcher.isEnabled());
    }

    @Test
    public void testConstructorWithPathIsEnabled() {
        ExitWatcher watcher = new ExitWatcher("/tmp/exit_file");
        assertTrue(watcher.isEnabled());
        assertEquals("/tmp/exit_file", watcher.getExitFileLocation());
    }

    @Test
    public void testShouldExitWhenDisabled() {
        ExitWatcher watcher = new ExitWatcher();
        assertFalse(watcher.shouldExit());
    }

    @Test
    public void testShouldExitWhenFileDoesNotExist() {
        ExitWatcher watcher = new ExitWatcher(
                "/tmp/nonexistent_exit_file_12345");
        assertTrue(watcher.isEnabled());
        assertFalse(watcher.shouldExit());
    }

    @Test
    public void testShouldExitWhenFileExists() throws IOException {
        File exitFile = tempFolder.newFile("exit_trigger");
        ExitWatcher watcher = new ExitWatcher(
                exitFile.getAbsolutePath());
        assertTrue(watcher.isEnabled());
        assertTrue(watcher.shouldExit());
    }

    @Test
    public void testShouldExitWhenPathIsDirectory()
            throws IOException {
        File directory = tempFolder.newFolder("exit_dir");
        ExitWatcher watcher = new ExitWatcher(
                directory.getAbsolutePath());
        assertTrue(watcher.isEnabled());
        // Directories should not trigger exit
        assertFalse(watcher.shouldExit());
    }

    @Test
    public void testToString() {
        ExitWatcher watcher = new ExitWatcher("/tmp/exit_file");
        assertEquals("/tmp/exit_file", watcher.toString());
    }

    @Test
    public void testToStringNull() {
        ExitWatcher watcher = new ExitWatcher();
        assertNull(watcher.toString());
    }
}
