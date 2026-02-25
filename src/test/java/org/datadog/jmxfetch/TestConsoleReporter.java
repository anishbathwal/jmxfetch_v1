package org.datadog.jmxfetch;

import org.datadog.jmxfetch.reporter.ConsoleReporter;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class TestConsoleReporter {

    private ConsoleReporter reporter;

    @Before
    public void setUp() {
        reporter = new ConsoleReporter();
    }

    // === Positive Tests ===

    @Test
    public void testGetMetricsEmpty() {
        List<Map<String, Object>> metrics = reporter.getMetrics();
        assertNotNull(metrics);
        assertTrue(metrics.isEmpty());
    }

    @Test
    public void testGetServiceChecksEmpty() {
        List<Map<String, Object>> serviceChecks = reporter.getServiceChecks();
        assertNotNull(serviceChecks);
        assertTrue(serviceChecks.isEmpty());
    }

    @Test
    public void testServiceCheckCount() {
        assertEquals(0, reporter.getServiceCheckCount("testCheck"));
        reporter.incrementServiceCheckCount("testCheck");
        assertEquals(1, reporter.getServiceCheckCount("testCheck"));
        reporter.incrementServiceCheckCount("testCheck");
        assertEquals(2, reporter.getServiceCheckCount("testCheck"));
    }

    @Test
    public void testResetServiceCheckCount() {
        reporter.incrementServiceCheckCount("testCheck");
        reporter.incrementServiceCheckCount("testCheck");
        assertEquals(2, reporter.getServiceCheckCount("testCheck"));
        reporter.resetServiceCheckCount("testCheck");
        assertEquals(0, reporter.getServiceCheckCount("testCheck"));
    }

    @Test
    public void testMultipleCheckCounts() {
        reporter.incrementServiceCheckCount("check1");
        reporter.incrementServiceCheckCount("check1");
        reporter.incrementServiceCheckCount("check2");

        assertEquals(2, reporter.getServiceCheckCount("check1"));
        assertEquals(1, reporter.getServiceCheckCount("check2"));
        assertEquals(0, reporter.getServiceCheckCount("check3"));
    }

    @Test
    public void testClearRatesAggregator() {
        // Should not throw
        reporter.clearRatesAggregator("testInstance");
    }

    @Test
    public void testClearCountersAggregator() {
        // Should not throw
        reporter.clearCountersAggregator("testInstance");
    }

    @Test
    public void testSendServiceCheck() {
        String[] tags = new String[]{"env:test"};
        reporter.sendServiceCheck("myCheck", "myServiceCheck", Status.STATUS_OK, "All good", tags);

        assertEquals(1, reporter.getServiceCheckCount("myCheck"));
        List<Map<String, Object>> checks = reporter.getServiceChecks();
        assertEquals(1, checks.size());
        assertEquals("myServiceCheck", checks.get(0).get("name"));
        assertEquals(Status.STATUS_OK, checks.get(0).get("status"));
    }

    @Test
    public void testSendServiceCheckWithNullTags() {
        reporter.sendServiceCheck("myCheck", "myServiceCheck", Status.STATUS_OK, "OK", null);

        List<Map<String, Object>> checks = reporter.getServiceChecks();
        assertEquals(1, checks.size());
    }

    @Test
    public void testSendServiceCheckWithEmptyTags() {
        reporter.sendServiceCheck("myCheck", "myServiceCheck", Status.STATUS_OK, "OK", new String[]{});

        List<Map<String, Object>> checks = reporter.getServiceChecks();
        assertEquals(1, checks.size());
    }

    @Test
    public void testGetServiceChecksClearsAfterReturn() {
        reporter.sendServiceCheck("c", "sc", Status.STATUS_OK, "OK", new String[]{});
        List<Map<String, Object>> first = reporter.getServiceChecks();
        assertEquals(1, first.size());

        List<Map<String, Object>> second = reporter.getServiceChecks();
        assertTrue(second.isEmpty());
    }

    // === Boundary Tests ===

    @Test
    public void testGetServiceCheckCountForUnknownCheck() {
        assertEquals(0, reporter.getServiceCheckCount("nonExistent"));
    }

    @Test
    public void testResetServiceCheckCountForUnknownCheck() {
        // Should not throw
        reporter.resetServiceCheckCount("nonExistent");
        assertEquals(0, reporter.getServiceCheckCount("nonExistent"));
    }
}
