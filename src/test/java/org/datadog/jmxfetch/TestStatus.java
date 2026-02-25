package org.datadog.jmxfetch;

import org.datadog.jmxfetch.util.InstanceTelemetry;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class TestStatus {

    // === Positive Tests ===

    @Test
    public void testDefaultConstructor() {
        Status status = new Status();
        assertFalse(status.isEnabled());
        assertNull(status.getStatusFileLocation());
    }

    @Test
    public void testConstructorWithFileLocation() {
        Status status = new Status("/tmp/test_status.yaml");
        assertTrue(status.isEnabled());
        assertEquals("/tmp/test_status.yaml", status.getStatusFileLocation());
    }

    @Test
    public void testAddInstanceStats() {
        Status status = new Status("/tmp/test_status.yaml");
        // Should not throw
        status.addInstanceStats(
                "testCheck", "testInstance", 10, 2,
                "OK message", Status.STATUS_OK, null);
    }

    @Test
    public void testAddInstanceStatsWithTelemetry() {
        Status status = new Status("/tmp/test_status.yaml");
        InstanceTelemetry telemetry = new InstanceTelemetry();
        telemetry.setBeansFetched(5);
        telemetry.setTopLevelAttributeCount(10);
        telemetry.setMetricCount(15);
        telemetry.setWildcardDomainQueryCount(2);
        telemetry.setBeanMatchRatio(0.8);

        status.addInstanceStats(
                "testCheck", "testInstance", 10, 2,
                "OK message", Status.STATUS_OK, telemetry);
    }

    @Test
    public void testAddErrorStats() {
        Status status = new Status("/tmp/test_status.yaml");
        status.addErrorStats(5);
    }

    @Test
    public void testAddInitFailedCheck() {
        Status status = new Status("/tmp/test_status.yaml");
        status.addInitFailedCheck("failedCheck", "Connection refused", Status.STATUS_ERROR);
    }

    @Test
    public void testFlushToFile() throws IOException {
        File tempFile = File.createTempFile("jmxfetch_status_test", ".yaml");
        tempFile.deleteOnExit();

        Status status = new Status(tempFile.getAbsolutePath());
        status.addInstanceStats(
                "myCheck", "myInstance", 5, 1,
                "All good", Status.STATUS_OK, null);
        status.flush();

        // Verify file was written
        String content = new String(Files.readAllBytes(tempFile.toPath()));
        assertTrue(content.length() > 0);
        assertTrue(content.contains("checks"));
    }

    @Test
    public void testFlushMultipleTimes() throws IOException {
        File tempFile = File.createTempFile("jmxfetch_status_multi", ".yaml");
        tempFile.deleteOnExit();

        Status status = new Status(tempFile.getAbsolutePath());
        status.addInstanceStats("check1", "inst1", 5, 1, "OK", Status.STATUS_OK, null);
        status.flush();

        status.addInstanceStats("check2", "inst2", 10, 2, "OK", Status.STATUS_OK, null);
        status.flush();

        String content = new String(Files.readAllBytes(tempFile.toPath()));
        assertTrue(content.length() > 0);
    }

    @Test
    public void testFlushClearsStats() throws IOException {
        File tempFile = File.createTempFile("jmxfetch_status_clear", ".yaml");
        tempFile.deleteOnExit();

        Status status = new Status(tempFile.getAbsolutePath());
        status.addInstanceStats("check1", "inst1", 5, 1, "OK", Status.STATUS_OK, null);
        status.flush();

        // After flush, stats should be cleared, so next flush writes clean state
        status.flush();

        String content = new String(Files.readAllBytes(tempFile.toPath()));
        assertTrue(content.length() > 0);
    }

    // === Negative Tests ===

    @Test
    public void testConstructorWithNullLocation() {
        Status status = new Status(null);
        assertFalse(status.isEnabled());
    }

    @Test
    public void testFlushWhenDisabled() {
        Status status = new Status();
        // Should not throw even though disabled
        status.flush();
    }

    @Test
    public void testFlushWithInvalidPath() {
        Status status = new Status("/nonexistent/path/that/does/not/exist/status.yaml");
        // Should not throw - errors are caught internally
        status.addInstanceStats("check1", "inst1", 5, 1, "OK", Status.STATUS_OK, null);
        status.flush();
    }

    @Test
    public void testAddInstanceStatsNullInstance() {
        Status status = new Status("/tmp/test_status.yaml");
        // Should not throw with null instance name
        status.addInstanceStats("check1", null, -1, -1, "message", Status.STATUS_OK, null);
    }

    // === Boundary Tests ===

    @Test
    public void testAddInstanceStatsWithNegativeMetricCount() {
        Status status = new Status("/tmp/test_status.yaml");
        status.addInstanceStats(
                "testCheck", "testInstance", -1, -1,
                "No metrics", Status.STATUS_WARNING, null);
    }

    @Test
    public void testAddMultipleChecks() throws IOException {
        File tempFile = File.createTempFile("jmxfetch_multi_check", ".yaml");
        tempFile.deleteOnExit();

        Status status = new Status(tempFile.getAbsolutePath());
        status.addInstanceStats("check1", "inst1", 5, 1, "OK", Status.STATUS_OK, null);
        status.addInstanceStats("check1", "inst2", 10, 2, "OK", Status.STATUS_OK, null);
        status.addInstanceStats("check2", "inst3", 15, 3, "Warning", Status.STATUS_WARNING, null);
        status.addInitFailedCheck("check3", "Failed to connect", Status.STATUS_ERROR);
        status.addErrorStats(1);
        status.flush();

        String content = new String(Files.readAllBytes(tempFile.toPath()));
        assertTrue(content.length() > 0);
        assertTrue(content.contains("checks"));
    }

    // === Constants Tests ===

    @Test
    public void testStatusConstants() {
        assertEquals("WARNING", Status.STATUS_WARNING);
        assertEquals("OK", Status.STATUS_OK);
        assertEquals("ERROR", Status.STATUS_ERROR);
    }
}
