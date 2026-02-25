package org.datadog.jmxfetch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.datadog.jmxfetch.reporter.ConsoleReporter;
import org.junit.Before;
import org.junit.Test;

public class TestConsoleReporter {

    private ConsoleReporter reporter;

    @Before
    public void setUp() {
        reporter = new ConsoleReporter();
    }

    // === Positive Tests ===

    @Test
    public void testDoSendServiceCheck() {
        reporter.doSendServiceCheck("jmx.can_connect", "OK", "All good",
                new String[]{"instance:test"});
        List<Map<String, Object>> checks = reporter.getServiceChecks();
        assertEquals(1, checks.size());
        Map<String, Object> check = checks.get(0);
        assertEquals("jmx.can_connect", check.get("name"));
        assertEquals("OK", check.get("status"));
        assertEquals("All good", check.get("message"));
    }

    @Test
    public void testMultipleServiceChecks() {
        reporter.doSendServiceCheck("check1", "OK", "msg1", new String[]{});
        reporter.doSendServiceCheck("check2", "WARNING", "msg2",
                new String[]{});
        reporter.doSendServiceCheck("check3", "ERROR", "msg3", new String[]{});
        List<Map<String, Object>> checks = reporter.getServiceChecks();
        assertEquals(3, checks.size());
    }

    @Test
    public void testGetServiceChecksClearsServiceChecks() {
        reporter.doSendServiceCheck("check", "OK", "msg", new String[]{});
        List<Map<String, Object>> first = reporter.getServiceChecks();
        assertEquals(1, first.size());
        List<Map<String, Object>> second = reporter.getServiceChecks();
        assertEquals(0, second.size());
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
        reporter.resetServiceCheckCount("testCheck");
        assertEquals(0, reporter.getServiceCheckCount("testCheck"));
    }

    @Test
    public void testSendServiceCheckIncrementsCount() {
        reporter.sendServiceCheck("myCheck", "jmx.can_connect", "OK",
                "msg", new String[]{});
        assertEquals(1, reporter.getServiceCheckCount("myCheck"));
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
    public void testSendMetricsWithGaugeMetric() {
        List<Metric> metrics = new ArrayList<Metric>();
        Metric m = new Metric("jvm.heap", "gauge",
                new String[]{"instance:test"}, "mycheck");
        m.setValue(42.0);
        metrics.add(m);

        reporter.sendMetrics(metrics, "testInstance", false);
        List<Map<String, Object>> reported = reporter.getMetrics();
        assertEquals(1, reported.size());
        assertEquals("jvm.heap", reported.get(0).get("name"));
    }

    @Test
    public void testSendMetricsWithHistogramMetric() {
        List<Metric> metrics = new ArrayList<Metric>();
        Metric m = new Metric("jvm.gc.time", "histogram",
                new String[]{}, "mycheck");
        m.setValue(100.0);
        metrics.add(m);

        reporter.sendMetrics(metrics, "testInstance", false);
        List<Map<String, Object>> reported = reporter.getMetrics();
        assertEquals(1, reported.size());
    }

    @Test
    public void testSendMetricsSkipsNaN() {
        List<Metric> metrics = new ArrayList<Metric>();
        Metric m = new Metric("metric", "gauge", new String[]{}, "check");
        m.setValue(Double.NaN);
        metrics.add(m);

        reporter.sendMetrics(metrics, "testInstance", false);
        List<Map<String, Object>> reported = reporter.getMetrics();
        assertEquals(0, reported.size());
    }

    @Test
    public void testSendMetricsSkipsInfinity() {
        List<Metric> metrics = new ArrayList<Metric>();
        Metric m = new Metric("metric", "gauge", new String[]{}, "check");
        m.setValue(Double.POSITIVE_INFINITY);
        metrics.add(m);

        reporter.sendMetrics(metrics, "testInstance", false);
        List<Map<String, Object>> reported = reporter.getMetrics();
        assertEquals(0, reported.size());
    }

    @Test
    public void testSendMetricsMonotonicCountFirstCall() {
        List<Metric> metrics = new ArrayList<Metric>();
        Metric m = new Metric("counter.metric", "monotonic_count",
                new String[]{}, "check");
        m.setValue(100.0);
        metrics.add(m);

        reporter.sendMetrics(metrics, "testInstance", false);
        List<Map<String, Object>> reported = reporter.getMetrics();
        assertEquals(0, reported.size());
    }

    @Test
    public void testSendMetricsMonotonicCountSecondCall() {
        Metric m1 = new Metric("counter.metric", "monotonic_count",
                new String[]{}, "check");
        m1.setValue(100.0);
        List<Metric> metrics1 = new ArrayList<Metric>();
        metrics1.add(m1);
        reporter.sendMetrics(metrics1, "testInstance", false);

        Metric m2 = new Metric("counter.metric", "monotonic_count",
                new String[]{}, "check");
        m2.setValue(150.0);
        List<Metric> metrics2 = new ArrayList<Metric>();
        metrics2.add(m2);
        reporter.sendMetrics(metrics2, "testInstance", false);

        List<Map<String, Object>> reported = reporter.getMetrics();
        assertEquals(1, reported.size());
    }

    @Test
    public void testSendMetricsRateFirstCall() {
        List<Metric> metrics = new ArrayList<Metric>();
        Metric m = new Metric("rate.metric", "rate",
                new String[]{}, "check");
        m.setValue(100.0);
        metrics.add(m);

        reporter.sendMetrics(metrics, "testInstance", false);
        List<Map<String, Object>> reported = reporter.getMetrics();
        assertEquals(0, reported.size());
    }

    // === Negative Tests ===

    @Test
    public void testGetMetricsWhenEmpty() {
        List<Map<String, Object>> metrics = reporter.getMetrics();
        assertNotNull(metrics);
        assertTrue(metrics.isEmpty());
    }

    @Test
    public void testGetServiceChecksWhenEmpty() {
        List<Map<String, Object>> checks = reporter.getServiceChecks();
        assertNotNull(checks);
        assertTrue(checks.isEmpty());
    }

    @Test
    public void testServiceCheckCountForUnknownCheck() {
        assertEquals(0, reporter.getServiceCheckCount("unknown"));
    }

    @Test
    public void testDoSendServiceCheckWithNullTags() {
        reporter.doSendServiceCheck("check", "OK", "msg", null);
        List<Map<String, Object>> checks = reporter.getServiceChecks();
        assertEquals(1, checks.size());
    }

    @Test
    public void testDoSendServiceCheckWithEmptyTags() {
        reporter.doSendServiceCheck("check", "OK", "msg", new String[]{});
        List<Map<String, Object>> checks = reporter.getServiceChecks();
        assertEquals(1, checks.size());
    }

    @Test
    public void testGetHandlerIsNull() {
        assertNull(reporter.getHandler());
    }

    // === Boundary Tests ===

    @Test
    public void testSendMetricsWithEmptyList() {
        List<Metric> metrics = new ArrayList<Metric>();
        reporter.sendMetrics(metrics, "testInstance", false);
        List<Map<String, Object>> reported = reporter.getMetrics();
        assertTrue(reported.isEmpty());
    }

    @Test
    public void testGetServiceChecksReturnsCopies() {
        reporter.doSendServiceCheck("check", "OK", "msg", new String[]{});
        List<Map<String, Object>> checks = reporter.getServiceChecks();
        checks.get(0).put("name", "modified");
        List<Map<String, Object>> next = reporter.getServiceChecks();
        assertTrue(next.isEmpty());
    }

    @Test
    public void testSendMetricsMonotonicCountNegativeDelta() {
        Metric m1 = new Metric("counter", "monotonic_count",
                new String[]{}, "check");
        m1.setValue(100.0);
        List<Metric> metrics1 = new ArrayList<Metric>();
        metrics1.add(m1);
        reporter.sendMetrics(metrics1, "testInstance", false);
        reporter.getMetrics();

        Metric m2 = new Metric("counter", "monotonic_count",
                new String[]{}, "check");
        m2.setValue(50.0);
        List<Metric> metrics2 = new ArrayList<Metric>();
        metrics2.add(m2);
        reporter.sendMetrics(metrics2, "testInstance", false);
        List<Map<String, Object>> reported = reporter.getMetrics();
        assertEquals(0, reported.size());
    }
}
