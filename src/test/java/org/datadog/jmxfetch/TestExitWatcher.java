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

    // === Positive Tests ===

    @Test
    public void testDefaultConstructorDisabled() {
        ExitWatcher watcher = new ExitWatcher();
        assertFalse(watcher.isEnabled());
        assertNull(watcher.getExitFileLocation());
    }

    @Test
    public void testConstructorWithLocation() {
        ExitWatcher watcher = new ExitWatcher("/tmp/exit_file");
        assertTrue(watcher.isEnabled());
        assertEquals("/tmp/exit_file", watcher.getExitFileLocation());
    }

    @Test
    public void testShouldExitWhenFileExists() throws IOException {
        File exitFile = tempFolder.newFile("exit_trigger");
        ExitWatcher watcher = new ExitWatcher(exitFile.getAbsolutePath());
        assertTrue(watcher.isEnabled());
        assertTrue(watcher.shouldExit());
    }

    @Test
    public void testShouldNotExitWhenFileDoesNotExist() {
        ExitWatcher watcher = new ExitWatcher("/tmp/nonexistent_exit_file_12345");
        assertTrue(watcher.isEnabled());
        assertFalse(watcher.shouldExit());
    }

    @Test
    public void testShouldNotExitWhenDisabled() {
        ExitWatcher watcher = new ExitWatcher();
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

    // === Boundary Tests ===

    @Test
    public void testConstructorWithNullDisabled() {
        ExitWatcher watcher = new ExitWatcher(null);
        assertFalse(watcher.isEnabled());
        assertFalse(watcher.shouldExit());
    }

    @Test
    public void testShouldNotExitWhenPathIsDirectory() throws IOException {
        File dir = tempFolder.newFolder("exit_dir");
        ExitWatcher watcher = new ExitWatcher(dir.getAbsolutePath());
        assertTrue(watcher.isEnabled());
        assertFalse(watcher.shouldExit());
    }
}
