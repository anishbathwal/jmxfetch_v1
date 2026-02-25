package org.datadog.jmxfetch.util;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class TestFileHelper {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    // === Positive Tests ===

    @Test
    public void testTouchCreatesNewFile() throws IOException {
        File file = new File(tempFolder.getRoot(), "newfile.txt");
        FileHelper.touch(file);
        assertTrue(file.exists());
    }

    @Test
    public void testTouchExistingFile() throws IOException {
        File file = tempFolder.newFile("existing.txt");
        long originalModified = file.lastModified();
        // Wait a bit to ensure timestamp differs
        FileHelper.touch(file, originalModified + 1000);
        assertTrue(file.lastModified() >= originalModified);
    }

    @Test
    public void testTouchWithTimestamp() throws IOException {
        File file = new File(tempFolder.getRoot(), "timestamped.txt");
        long timestamp = 1700000000000L;
        FileHelper.touch(file, timestamp);
        assertTrue(file.exists());
        assertTrue(file.lastModified() == timestamp);
    }

    // === Boundary Tests ===

    @Test
    public void testTouchFileMultipleTimes() throws IOException {
        File file = new File(tempFolder.getRoot(), "multi.txt");
        FileHelper.touch(file);
        assertTrue(file.exists());
        FileHelper.touch(file);
        assertTrue(file.exists());
    }
}
