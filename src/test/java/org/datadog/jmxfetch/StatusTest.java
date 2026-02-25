package org.datadog.jmxfetch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

public class StatusTest {

    // === Positive tests ===

    @Test
    public void testDefaultConstructorDisabled() {
        Status status = new Status();
        assertFalse(status.isEnabled());
        assertNull(status.getStatusFileLocation());
    }

    @Test
    public void testConstructorWithFileLocation() {
        Status status = new Status("/tmp/jmx_status.yaml");
        assertTrue(status.isEnabled());
        assertEquals(
                "/tmp/jmx_status.yaml",
                status.getStatusFileLocation());
    }

    @Test
    public void testConstructorWithNullDisabled() {
        Status status = new Status(null);
        assertFalse(status.isEnabled());
    }

    @Test
    public void testAddInstanceStats() {
        Status status = new Status("/tmp/jmx_status.yaml");
        // Should not throw
        status.addInstanceStats(
                "mycheck", "instance1", 10, 2,
                "OK message", Status.STATUS_OK, null);
    }

    @Test
    public void testAddInstanceStatsWithTelemetry() {
        Status status = new Status("/tmp/jmx_status.yaml");
        // Should not throw even with null telemetry
        status.addInstanceStats(
                "mycheck", "instance1", 5, 1,
                "All good", Status.STATUS_OK, null);
    }

    @Test
    public void testAddInitFailedCheck() {
        Status status = new Status("/tmp/jmx_status.yaml");
        // Should not throw
        status.addInitFailedCheck(
                "failedcheck", "Connection refused",
                Status.STATUS_ERROR);
    }

    @Test
    public void testAddErrorStats() {
        Status status = new Status("/tmp/jmx_status.yaml");
        status.addErrorStats(5);
        // Should not throw
    }

    @Test
    public void testFlushWithFileLocation() throws Exception {
        File tempFile = File.createTempFile(
                "jmx_status_test", ".yaml");
        tempFile.deleteOnExit();

        Status status = new Status(tempFile.getAbsolutePath());
        assertTrue(status.isEnabled());

        status.addInstanceStats(
                "testcheck", "inst1", 3, 1,
                "OK", Status.STATUS_OK, null);
        status.flush();

        // File should exist and have content
        assertTrue(tempFile.exists());
        assertTrue(tempFile.length() > 0);
    }

    @Test
    public void testFlushWhenDisabled() {
        Status status = new Status();
        assertFalse(status.isEnabled());
        // Should not throw when flushing disabled status
        status.flush();
    }

    @Test
    public void testMultipleInstanceStats() {
        Status status = new Status("/tmp/jmx_status.yaml");
        status.addInstanceStats(
                "check1", "inst1", 10, 2,
                "OK", Status.STATUS_OK, null);
        status.addInstanceStats(
                "check1", "inst2", 5, 1,
                "OK", Status.STATUS_OK, null);
        status.addInstanceStats(
                "check2", "inst3", 3, 0,
                "Warning", Status.STATUS_WARNING, null);
        // Should not throw
    }

    // === Boundary tests ===

    @Test
    public void testAddInstanceStatsNullInstance() {
        Status status = new Status("/tmp/jmx_status.yaml");
        // Null instance is valid (used in addInitFailedCheck)
        status.addInstanceStats(
                "check", null, -1, -1,
                "msg", Status.STATUS_ERROR, null);
    }

    @Test
    public void testAddInstanceStatsZeroMetrics() {
        Status status = new Status("/tmp/jmx_status.yaml");
        status.addInstanceStats(
                "check", "inst", 0, 0,
                "msg", Status.STATUS_OK, null);
    }

    @Test
    public void testAddInstanceStatsNegativeOneCountsSkipped() {
        Status status = new Status("/tmp/jmx_status.yaml");
        // -1 means "don't include count in output"
        status.addInstanceStats(
                "check", "inst", -1, -1,
                "msg", Status.STATUS_OK, null);
    }

    // === Status constants tests ===

    @Test
    public void testStatusConstants() {
        assertEquals("WARNING", Status.STATUS_WARNING);
        assertEquals("OK", Status.STATUS_OK);
        assertEquals("ERROR", Status.STATUS_ERROR);
    }

    // === Error handling tests ===

    @Test
    public void testFlushWithInvalidPath() {
        Status status = new Status(
                "/nonexistent/path/status.yaml");
        assertTrue(status.isEnabled());
        // Should not throw, error is caught internally
        status.flush();
    }
}
