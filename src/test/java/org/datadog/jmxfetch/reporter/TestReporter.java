package org.datadog.jmxfetch.reporter;

import static org.junit.Assert.assertEquals;

import org.datadog.jmxfetch.Metric;
import org.junit.Test;

public class TestReporter {

    @Test
    public void testGenerateId() {
        ConsoleReporter reporter = new ConsoleReporter();
        Metric metric = new Metric(
                "test.metric", "gauge",
                new String[]{"env:prod", "region:us"}, "check");
        String id = reporter.generateId(metric);
        assertEquals("test.metricenv:prodregion:us", id);
    }

    @Test
    public void testGenerateIdEmptyTags() {
        ConsoleReporter reporter = new ConsoleReporter();
        Metric metric = new Metric(
                "test.metric", "gauge",
                new String[]{}, "check");
        String id = reporter.generateId(metric);
        assertEquals("test.metric", id);
    }

    @Test
    public void testServiceCheckCount() {
        ConsoleReporter reporter = new ConsoleReporter();
        assertEquals(0, reporter.getServiceCheckCount("mycheck"));

        reporter.incrementServiceCheckCount("mycheck");
        assertEquals(1, reporter.getServiceCheckCount("mycheck"));

        reporter.incrementServiceCheckCount("mycheck");
        assertEquals(2, reporter.getServiceCheckCount("mycheck"));
    }

    @Test
    public void testResetServiceCheckCount() {
        ConsoleReporter reporter = new ConsoleReporter();
        reporter.incrementServiceCheckCount("mycheck");
        reporter.incrementServiceCheckCount("mycheck");
        assertEquals(2, reporter.getServiceCheckCount("mycheck"));

        reporter.resetServiceCheckCount("mycheck");
        assertEquals(0, reporter.getServiceCheckCount("mycheck"));
    }

    @Test
    public void testServiceCheckCountMultipleChecks() {
        ConsoleReporter reporter = new ConsoleReporter();
        reporter.incrementServiceCheckCount("check1");
        reporter.incrementServiceCheckCount("check1");
        reporter.incrementServiceCheckCount("check2");

        assertEquals(2, reporter.getServiceCheckCount("check1"));
        assertEquals(1, reporter.getServiceCheckCount("check2"));
        assertEquals(0, reporter.getServiceCheckCount("check3"));
    }

    @Test
    public void testClearRatesAggregator() {
        ConsoleReporter reporter = new ConsoleReporter();
        // Should not throw
        reporter.clearRatesAggregator("instance1");
    }

    @Test
    public void testClearCountersAggregator() {
        ConsoleReporter reporter = new ConsoleReporter();
        // Should not throw
        reporter.clearCountersAggregator("instance1");
    }

    @Test
    public void testSendServiceCheckDelegates() {
        ConsoleReporter reporter = new ConsoleReporter();
        reporter.sendServiceCheck(
                "mycheck", "service.check", "OK",
                "all good", new String[]{"tag:val"});
        assertEquals(1, reporter.getServiceCheckCount("mycheck"));
        assertEquals(1, reporter.getServiceChecks().size());
    }

    @Test
    public void testGetServiceCheckCountMapNotNull() {
        ConsoleReporter reporter = new ConsoleReporter();
        reporter.incrementServiceCheckCount("check");
        assertEquals(
                1,
                reporter.getServiceCheckCountMap()
                        .get("check")
                        .intValue());
    }
}
