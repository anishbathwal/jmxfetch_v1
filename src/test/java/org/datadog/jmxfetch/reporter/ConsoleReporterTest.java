package org.datadog.jmxfetch.reporter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.datadog.jmxfetch.Metric;
import org.junit.Before;
import org.junit.Test;

public class ConsoleReporterTest {

    private ConsoleReporter reporter;

    @Before
    public void setUp() {
        reporter = new ConsoleReporter();
    }

    // === Metric reporting tests ===

    @Test
    public void testSendAndGetMetrics() {
        String[] tags = new String[]{"env:prod"};
        Metric metric = new Metric(
                "jvm.heap", "gauge", tags, "check1");
        metric.setValue(100.0);

        List<Metric> metrics = new ArrayList<Metric>();
        metrics.add(metric);
        reporter.clearRatesAggregator("inst1");
        reporter.sendMetrics(metrics, "inst1", false);

        List<Map<String, Object>> reported =
                reporter.getMetrics();
        assertEquals(1, reported.size());
        assertEquals("jvm.heap", reported.get(0).get("name"));
        assertEquals(100.0, reported.get(0).get("value"));
        assertEquals("gauge", reported.get(0).get("type"));
    }

    @Test
    public void testGetMetricsClearsAfterRead() {
        String[] tags = new String[]{};
        Metric metric = new Metric(
                "test.m", "gauge", tags, "check");
        metric.setValue(1.0);

        List<Metric> metrics = new ArrayList<Metric>();
        metrics.add(metric);
        reporter.clearRatesAggregator("inst");
        reporter.sendMetrics(metrics, "inst", false);

        // First call returns metrics
        List<Map<String, Object>> first =
                reporter.getMetrics();
        assertEquals(1, first.size());

        // Second call returns empty (cleared)
        List<Map<String, Object>> second =
                reporter.getMetrics();
        assertTrue(second.isEmpty());
    }

    @Test
    public void testSendMultipleMetrics() {
        List<Metric> metrics = new ArrayList<Metric>();
        for (int i = 0; i < 5; i++) {
            Metric metric = new Metric(
                    "metric." + i, "gauge",
                    new String[]{}, "check");
            metric.setValue(i * 10.0);
            metrics.add(metric);
        }
        reporter.clearRatesAggregator("inst");
        reporter.sendMetrics(metrics, "inst", false);

        List<Map<String, Object>> reported =
                reporter.getMetrics();
        assertEquals(5, reported.size());
    }

    // === Service check tests ===

    @Test
    public void testSendServiceCheck() {
        reporter.doSendServiceCheck(
                "mycheck.can_connect", "OK",
                "All good", new String[]{"env:prod"});

        List<Map<String, Object>> checks =
                reporter.getServiceChecks();
        assertEquals(1, checks.size());
        assertEquals(
                "mycheck.can_connect", checks.get(0).get("name"));
        assertEquals("OK", checks.get(0).get("status"));
        assertEquals("All good", checks.get(0).get("message"));
    }

    @Test
    public void testGetServiceChecksClearsAfterRead() {
        reporter.doSendServiceCheck(
                "check", "OK", "msg", new String[]{});

        List<Map<String, Object>> first =
                reporter.getServiceChecks();
        assertEquals(1, first.size());

        List<Map<String, Object>> second =
                reporter.getServiceChecks();
        assertTrue(second.isEmpty());
    }

    @Test
    public void testSendServiceCheckWithNullTags() {
        reporter.doSendServiceCheck(
                "check", "OK", "msg", null);

        List<Map<String, Object>> checks =
                reporter.getServiceChecks();
        assertEquals(1, checks.size());
    }

    @Test
    public void testSendServiceCheckWithEmptyTags() {
        reporter.doSendServiceCheck(
                "check", "OK", "msg", new String[]{});

        List<Map<String, Object>> checks =
                reporter.getServiceChecks();
        assertEquals(1, checks.size());
    }

    // === NaN / Infinite value tests ===

    @Test
    public void testNanMetricIsSkipped() {
        Metric metric = new Metric(
                "test.nan", "gauge", new String[]{}, "check");
        metric.setValue(Double.NaN);

        List<Metric> metrics = new ArrayList<Metric>();
        metrics.add(metric);
        reporter.clearRatesAggregator("inst");
        reporter.sendMetrics(metrics, "inst", false);

        List<Map<String, Object>> reported =
                reporter.getMetrics();
        assertTrue(reported.isEmpty());
    }

    @Test
    public void testInfiniteMetricIsSkipped() {
        Metric metric = new Metric(
                "test.inf", "gauge", new String[]{}, "check");
        metric.setValue(Double.POSITIVE_INFINITY);

        List<Metric> metrics = new ArrayList<Metric>();
        metrics.add(metric);
        reporter.clearRatesAggregator("inst");
        reporter.sendMetrics(metrics, "inst", false);

        List<Map<String, Object>> reported =
                reporter.getMetrics();
        assertTrue(reported.isEmpty());
    }

    @Test
    public void testNegativeInfiniteMetricIsSkipped() {
        Metric metric = new Metric(
                "test.inf", "gauge", new String[]{}, "check");
        metric.setValue(Double.NEGATIVE_INFINITY);

        List<Metric> metrics = new ArrayList<Metric>();
        metrics.add(metric);
        reporter.clearRatesAggregator("inst");
        reporter.sendMetrics(metrics, "inst", false);

        List<Map<String, Object>> reported =
                reporter.getMetrics();
        assertTrue(reported.isEmpty());
    }

    // === Histogram metric test ===

    @Test
    public void testHistogramMetricSentDirectly() {
        Metric metric = new Metric(
                "test.hist", "histogram",
                new String[]{}, "check");
        metric.setValue(50.0);

        List<Metric> metrics = new ArrayList<Metric>();
        metrics.add(metric);
        reporter.clearRatesAggregator("inst");
        reporter.sendMetrics(metrics, "inst", false);

        List<Map<String, Object>> reported =
                reporter.getMetrics();
        assertEquals(1, reported.size());
        assertEquals("histogram", reported.get(0).get("type"));
    }

    // === Monotonic count tests ===

    @Test
    public void testMonotonicCountFirstValueSkipped() {
        Metric metric = new Metric(
                "test.mono", "monotonic_count",
                new String[]{}, "check");
        metric.setValue(100.0);

        List<Metric> metrics = new ArrayList<Metric>();
        metrics.add(metric);
        reporter.clearCountersAggregator("inst");
        reporter.clearRatesAggregator("inst");
        reporter.sendMetrics(metrics, "inst", false);

        // First value is baseline, not reported
        List<Map<String, Object>> reported =
                reporter.getMetrics();
        assertTrue(reported.isEmpty());
    }

    @Test
    public void testMonotonicCountDeltaReported() {
        reporter.clearCountersAggregator("inst");
        reporter.clearRatesAggregator("inst");

        // First send: baseline
        Metric metric1 = new Metric(
                "test.mono", "monotonic_count",
                new String[]{}, "check");
        metric1.setValue(100.0);
        List<Metric> metrics1 = new ArrayList<Metric>();
        metrics1.add(metric1);
        reporter.sendMetrics(metrics1, "inst", false);
        reporter.getMetrics(); // clear

        // Second send: delta should be reported
        Metric metric2 = new Metric(
                "test.mono", "monotonic_count",
                new String[]{}, "check");
        metric2.setValue(150.0);
        List<Metric> metrics2 = new ArrayList<Metric>();
        metrics2.add(metric2);
        reporter.sendMetrics(metrics2, "inst", false);

        List<Map<String, Object>> reported =
                reporter.getMetrics();
        assertEquals(1, reported.size());
        assertEquals(50.0,
                ((Number) reported.get(0).get("value"))
                        .doubleValue(), 0.001);
    }

    // === Rate metric tests ===

    @Test
    public void testRateFirstValueSkipped() {
        reporter.clearRatesAggregator("inst");
        reporter.clearCountersAggregator("inst");

        Metric metric = new Metric(
                "test.rate", "rate",
                new String[]{}, "check");
        metric.setValue(100.0);
        List<Metric> metrics = new ArrayList<Metric>();
        metrics.add(metric);
        reporter.sendMetrics(metrics, "inst", false);

        List<Map<String, Object>> reported =
                reporter.getMetrics();
        assertTrue(reported.isEmpty());
    }

    // === Service check count tests ===

    @Test
    public void testServiceCheckCount() {
        assertEquals(0, reporter.getServiceCheckCount("check"));
        reporter.incrementServiceCheckCount("check");
        assertEquals(1, reporter.getServiceCheckCount("check"));
        reporter.incrementServiceCheckCount("check");
        assertEquals(2, reporter.getServiceCheckCount("check"));
    }

    @Test
    public void testResetServiceCheckCount() {
        reporter.incrementServiceCheckCount("check");
        reporter.incrementServiceCheckCount("check");
        assertEquals(2, reporter.getServiceCheckCount("check"));
        reporter.resetServiceCheckCount("check");
        assertEquals(0, reporter.getServiceCheckCount("check"));
    }

    @Test
    public void testServiceCheckCountForUnknownCheck() {
        assertEquals(
                0, reporter.getServiceCheckCount("unknown"));
    }

    @Test
    public void testSendServiceCheckIncrementsCount() {
        reporter.sendServiceCheck(
                "mycheck", "mycheck.can_connect",
                "OK", "msg", new String[]{});
        assertEquals(
                1, reporter.getServiceCheckCount("mycheck"));
    }

    // === Display methods (should not throw) ===

    @Test
    public void testDisplayMetricReached() {
        reporter.displayMetricReached();
    }

    // === Empty metrics list test ===

    @Test
    public void testSendEmptyMetricsList() {
        List<Metric> metrics = new ArrayList<Metric>();
        reporter.clearRatesAggregator("inst");
        reporter.sendMetrics(metrics, "inst", false);
        assertTrue(reporter.getMetrics().isEmpty());
    }
}
