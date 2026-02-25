package org.datadog.jmxfetch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.datadog.jmxfetch.util.InstanceTelemetry;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class TestStatus {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    // === Positive Tests ===

    @Test
    public void testDefaultConstructor() {
        Status status = new Status();
        assertFalse(status.isEnabled());
        assertNull(status.getStatusFileLocation());
    }

    @Test
    public void testConstructorWithFileLocation() throws IOException {
        File statusFile = tempFolder.newFile("status.yaml");
        Status status = new Status(statusFile.getAbsolutePath());
        assertTrue(status.isEnabled());
        assertEquals(statusFile.getAbsolutePath(), status.getStatusFileLocation());
    }

    @Test
    public void testFlushWithFileLocation() throws IOException {
        File statusFile = new File(tempFolder.getRoot(), "status.yaml");
        Status status = new Status(statusFile.getAbsolutePath());
        status.addInstanceStats("testCheck", "testInstance", 10, 2,
                "OK message", Status.STATUS_OK, null);
        status.flush();
        assertTrue(statusFile.exists());
        String content = new String(Files.readAllBytes(statusFile.toPath()));
        assertNotNull(content);
        assertTrue(content.length() > 0);
    }

    @Test
    public void testAddInstanceStats() throws IOException {
        File statusFile = new File(tempFolder.getRoot(), "status.yaml");
        Status status = new Status(statusFile.getAbsolutePath());
        status.addInstanceStats("checkName", "instance1", 5, 1,
                "All good", Status.STATUS_OK, null);
        status.flush();
        String content = new String(Files.readAllBytes(statusFile.toPath()));
        assertTrue(content.contains("checkName"));
    }

    @Test
    public void testAddInitFailedCheck() throws IOException {
        File statusFile = new File(tempFolder.getRoot(), "status.yaml");
        Status status = new Status(statusFile.getAbsolutePath());
        status.addInitFailedCheck("failedCheck", "Connection refused",
                Status.STATUS_ERROR);
        status.flush();
        String content = new String(Files.readAllBytes(statusFile.toPath()));
        assertTrue(content.contains("failedCheck"));
    }

    @Test
    public void testAddErrorStats() throws IOException {
        File statusFile = new File(tempFolder.getRoot(), "status.yaml");
        Status status = new Status(statusFile.getAbsolutePath());
        status.addErrorStats(5);
        status.flush();
        String content = new String(Files.readAllBytes(statusFile.toPath()));
        assertTrue(content.contains("errors"));
    }

    @Test
    public void testStatusConstants() {
        assertEquals("WARNING", Status.STATUS_WARNING);
        assertEquals("OK", Status.STATUS_OK);
        assertEquals("ERROR", Status.STATUS_ERROR);
    }

    // === Negative Tests ===

    @Test
    public void testConstructorWithNull() {
        Status status = new Status(null);
        assertFalse(status.isEnabled());
    }

    @Test
    public void testFlushWhenDisabled() {
        Status status = new Status();
        // Should not throw any exception
        status.flush();
    }

    // === Boundary Tests ===

    @Test
    public void testAddInstanceStatsWithNullInstance() throws IOException {
        File statusFile = new File(tempFolder.getRoot(), "status.yaml");
        Status status = new Status(statusFile.getAbsolutePath());
        status.addInstanceStats("check", null, -1, -1, "msg", Status.STATUS_OK, null);
        status.flush();
        assertTrue(statusFile.exists());
    }

    @Test
    public void testAddInstanceStatsWithNegativeMetricCount() throws IOException {
        File statusFile = new File(tempFolder.getRoot(), "status.yaml");
        Status status = new Status(statusFile.getAbsolutePath());
        status.addInstanceStats("check", "inst", -1, -1, "msg",
                Status.STATUS_WARNING, null);
        status.flush();
        String content = new String(Files.readAllBytes(statusFile.toPath()));
        assertFalse(content.contains("metric_count"));
    }

    @Test
    public void testMultipleFlushes() throws IOException {
        File statusFile = new File(tempFolder.getRoot(), "status.yaml");
        Status status = new Status(statusFile.getAbsolutePath());
        status.addInstanceStats("check1", "inst1", 5, 1, "msg1",
                Status.STATUS_OK, null);
        status.flush();
        status.addInstanceStats("check2", "inst2", 3, 0, "msg2",
                Status.STATUS_OK, null);
        status.flush();
        assertTrue(statusFile.exists());
    }

    @Test
    public void testAddInstanceStatsWithTelemetry() throws IOException {
        File statusFile = new File(tempFolder.getRoot(), "status.yaml");
        Status status = new Status(statusFile.getAbsolutePath());
        InstanceTelemetry telemetry = new InstanceTelemetry();
        telemetry.setBeansFetched(10);
        telemetry.setTopLevelAttributeCount(20);
        telemetry.setMetricCount(15);
        telemetry.setWildcardDomainQueryCount(2);
        telemetry.setBeanMatchRatio(0.5);
        status.addInstanceStats("check", "inst", 10, 2, "msg",
                Status.STATUS_OK, telemetry);
        status.flush();
        String content = new String(Files.readAllBytes(statusFile.toPath()));
        assertTrue(content.contains("instance_bean_count"));
    }
}
