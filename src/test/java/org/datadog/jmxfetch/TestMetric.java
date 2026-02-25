package org.datadog.jmxfetch;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class TestMetric {

    // === Positive Tests ===

    @Test
    public void testConstructorAndGetters() {
        String[] tags = new String[]{"env:prod", "service:web"};
        Metric metric = new Metric("jmx.heap_memory", "gauge", tags, "myCheck");

        assertEquals("jmx.heap_memory", metric.getAlias());
        assertEquals("gauge", metric.getMetricType());
        assertArrayEquals(tags, metric.getTags());
        assertEquals("myCheck", metric.getCheckName());
    }

    @Test
    public void testSetAndGetValue() {
        Metric metric = new Metric("test.metric", "gauge", new String[]{}, "check");
        metric.setValue(42.5);
        assertEquals(42.5, metric.getValue(), 0.001);
    }

    @Test
    public void testSetValueMultipleTimes() {
        Metric metric = new Metric("test.metric", "gauge", new String[]{}, "check");
        metric.setValue(1.0);
        assertEquals(1.0, metric.getValue(), 0.001);
        metric.setValue(2.0);
        assertEquals(2.0, metric.getValue(), 0.001);
    }

    @Test
    public void testDifferentMetricTypes() {
        Metric gauge = new Metric("m1", "gauge", new String[]{}, "c");
        assertEquals("gauge", gauge.getMetricType());

        Metric counter = new Metric("m2", "counter", new String[]{}, "c");
        assertEquals("counter", counter.getMetricType());

        Metric rate = new Metric("m3", "rate", new String[]{}, "c");
        assertEquals("rate", rate.getMetricType());

        Metric histogram = new Metric("m4", "histogram", new String[]{}, "c");
        assertEquals("histogram", histogram.getMetricType());

        Metric monotonic = new Metric("m5", "monotonic_count", new String[]{}, "c");
        assertEquals("monotonic_count", monotonic.getMetricType());
    }

    @Test
    public void testMultipleTags() {
        String[] tags = new String[]{"env:prod", "service:web", "region:us-east-1", "team:backend"};
        Metric metric = new Metric("test.metric", "gauge", tags, "check");
        assertEquals(4, metric.getTags().length);
    }

    // === Boundary Tests ===

    @Test
    public void testEmptyTags() {
        Metric metric = new Metric("test.metric", "gauge", new String[]{}, "check");
        assertEquals(0, metric.getTags().length);
    }

    @Test
    public void testDefaultValue() {
        Metric metric = new Metric("test.metric", "gauge", new String[]{}, "check");
        assertEquals(0.0, metric.getValue(), 0.001);
    }

    @Test
    public void testNegativeValue() {
        Metric metric = new Metric("test.metric", "gauge", new String[]{}, "check");
        metric.setValue(-100.5);
        assertEquals(-100.5, metric.getValue(), 0.001);
    }

    @Test
    public void testLargeValue() {
        Metric metric = new Metric("test.metric", "gauge", new String[]{}, "check");
        metric.setValue(Double.MAX_VALUE);
        assertEquals(Double.MAX_VALUE, metric.getValue(), 0.0);
    }

    @Test
    public void testZeroValue() {
        Metric metric = new Metric("test.metric", "gauge", new String[]{}, "check");
        metric.setValue(0.0);
        assertEquals(0.0, metric.getValue(), 0.001);
    }

    @Test
    public void testNaNValue() {
        Metric metric = new Metric("test.metric", "gauge", new String[]{}, "check");
        metric.setValue(Double.NaN);
        assertEquals(Double.NaN, metric.getValue(), 0.0);
    }

    @Test
    public void testInfiniteValue() {
        Metric metric = new Metric("test.metric", "gauge", new String[]{}, "check");
        metric.setValue(Double.POSITIVE_INFINITY);
        assertEquals(Double.POSITIVE_INFINITY, metric.getValue(), 0.0);
    }

    // === Negative Tests ===

    @Test
    public void testNullTags() {
        Metric metric = new Metric("test.metric", "gauge", null, "check");
        assertNull(metric.getTags());
    }

    @Test
    public void testNullAlias() {
        Metric metric = new Metric(null, "gauge", new String[]{}, "check");
        assertNull(metric.getAlias());
    }

    @Test
    public void testNullMetricType() {
        Metric metric = new Metric("test", null, new String[]{}, "check");
        assertNull(metric.getMetricType());
    }

    @Test
    public void testNullCheckName() {
        Metric metric = new Metric("test", "gauge", new String[]{}, null);
        assertNull(metric.getCheckName());
    }
}
