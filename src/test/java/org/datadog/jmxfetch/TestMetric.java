package org.datadog.jmxfetch;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestMetric {

    // === Positive Tests ===

    @Test
    public void testMetricConstructorAndGetters() {
        String[] tags = new String[]{"tag1:value1", "tag2:value2"};
        Metric metric = new Metric("jvm.heap_memory", "gauge", tags, "jmx_check");

        assertEquals("jvm.heap_memory", metric.getAlias());
        assertEquals("gauge", metric.getMetricType());
        assertArrayEquals(tags, metric.getTags());
        assertEquals("jmx_check", metric.getCheckName());
    }

    @Test
    public void testSetValue() {
        Metric metric = new Metric("test", "gauge", new String[]{}, "check");
        metric.setValue(42.5);
        assertEquals(42.5, metric.getValue(), 0.001);
    }

    @Test
    public void testDefaultValue() {
        Metric metric = new Metric("test", "gauge", new String[]{}, "check");
        assertEquals(0.0, metric.getValue(), 0.001);
    }

    // === Boundary Tests ===

    @Test
    public void testMetricWithEmptyTags() {
        Metric metric = new Metric("test", "gauge", new String[]{}, "check");
        assertEquals(0, metric.getTags().length);
    }

    @Test
    public void testMetricWithMaxDoubleValue() {
        Metric metric = new Metric("test", "gauge", new String[]{}, "check");
        metric.setValue(Double.MAX_VALUE);
        assertEquals(Double.MAX_VALUE, metric.getValue(), 0.0);
    }

    @Test
    public void testMetricWithNegativeValue() {
        Metric metric = new Metric("test", "gauge", new String[]{}, "check");
        metric.setValue(-100.5);
        assertEquals(-100.5, metric.getValue(), 0.001);
    }

    @Test
    public void testMetricWithZeroValue() {
        Metric metric = new Metric("test", "gauge", new String[]{}, "check");
        metric.setValue(0.0);
        assertEquals(0.0, metric.getValue(), 0.001);
    }

    @Test
    public void testMetricTypes() {
        Metric gauge = new Metric("m1", "gauge", new String[]{}, "c");
        assertEquals("gauge", gauge.getMetricType());

        Metric counter = new Metric("m2", "counter", new String[]{}, "c");
        assertEquals("counter", counter.getMetricType());

        Metric rate = new Metric("m3", "rate", new String[]{}, "c");
        assertEquals("rate", rate.getMetricType());

        Metric histogram = new Metric("m4", "histogram", new String[]{}, "c");
        assertEquals("histogram", histogram.getMetricType());

        Metric monoCount =
                new Metric("m5", "monotonic_count", new String[]{}, "c");
        assertEquals("monotonic_count", monoCount.getMetricType());
    }

    @Test
    public void testMetricWithManyTags() {
        String[] tags = new String[100];
        for (int i = 0; i < 100; i++) {
            tags[i] = "tag" + i + ":value" + i;
        }
        Metric metric = new Metric("test", "gauge", tags, "check");
        assertEquals(100, metric.getTags().length);
    }
}
