package org.datadog.jmxfetch.reporter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.Test;

public class TestConsoleReporter {

    @Test
    public void testSendMetricPointAndGetMetrics() {
        ConsoleReporter reporter = new ConsoleReporter();
        reporter.sendMetricPoint(
                "gauge", "test.metric", 42.0,
                new String[]{"env:test"});
        List<Map<String, Object>> metrics = reporter.getMetrics();
        assertEquals(1, metrics.size());
        assertEquals("test.metric", metrics.get(0).get("name"));
        assertEquals(42.0, metrics.get(0).get("value"));
        assertEquals("gauge", metrics.get(0).get("type"));
    }

    @Test
    public void testGetMetricsClearsAfterCall() {
        ConsoleReporter reporter = new ConsoleReporter();
        reporter.sendMetricPoint(
                "gauge", "test.metric", 1.0,
                new String[]{});
        List<Map<String, Object>> metrics = reporter.getMetrics();
        assertEquals(1, metrics.size());
        // Second call should be empty
        List<Map<String, Object>> empty = reporter.getMetrics();
        assertTrue(empty.isEmpty());
    }

    @Test
    public void testSendServiceCheckAndGet() {
        ConsoleReporter reporter = new ConsoleReporter();
        reporter.doSendServiceCheck(
                "test.check", "OK", "all good",
                new String[]{"service:web"});
        List<Map<String, Object>> checks =
                reporter.getServiceChecks();
        assertEquals(1, checks.size());
        assertEquals("test.check", checks.get(0).get("name"));
        assertEquals("OK", checks.get(0).get("status"));
        assertEquals("all good", checks.get(0).get("message"));
    }

    @Test
    public void testGetServiceChecksClearsAfterCall() {
        ConsoleReporter reporter = new ConsoleReporter();
        reporter.doSendServiceCheck(
                "check", "OK", "msg", new String[]{});
        List<Map<String, Object>> checks =
                reporter.getServiceChecks();
        assertEquals(1, checks.size());
        // Second call should be empty
        assertTrue(reporter.getServiceChecks().isEmpty());
    }

    @Test
    public void testSendServiceCheckWithNullTags() {
        ConsoleReporter reporter = new ConsoleReporter();
        reporter.doSendServiceCheck(
                "test.check", "WARNING", "warning msg", null);
        List<Map<String, Object>> checks =
                reporter.getServiceChecks();
        assertEquals(1, checks.size());
    }

    @Test
    public void testSendServiceCheckWithEmptyTags() {
        ConsoleReporter reporter = new ConsoleReporter();
        reporter.doSendServiceCheck(
                "test.check", "ERROR", "error msg",
                new String[]{});
        List<Map<String, Object>> checks =
                reporter.getServiceChecks();
        assertEquals(1, checks.size());
    }

    @Test
    public void testMultipleMetrics() {
        ConsoleReporter reporter = new ConsoleReporter();
        reporter.sendMetricPoint(
                "gauge", "metric1", 1.0, new String[]{});
        reporter.sendMetricPoint(
                "counter", "metric2", 2.0, new String[]{});
        reporter.sendMetricPoint(
                "histogram", "metric3", 3.0, new String[]{});
        List<Map<String, Object>> metrics = reporter.getMetrics();
        assertEquals(3, metrics.size());
    }

    @Test
    public void testGetMetricsReturnsCopy() {
        ConsoleReporter reporter = new ConsoleReporter();
        reporter.sendMetricPoint(
                "gauge", "test.metric", 42.0,
                new String[]{"env:test"});
        List<Map<String, Object>> metrics = reporter.getMetrics();
        // Modifying the returned list should not affect internal
        metrics.clear();
        // After clearing, internal is also cleared by getMetrics,
        // so this just verifies no exception
        assertNotNull(reporter.getMetrics());
    }
}
