package org.datadog.jmxfetch;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class MetricTest {

    // === Positive tests ===

    @Test
    public void testMetricConstruction() {
        String[] tags = new String[]{"env:prod", "service:web"};
        Metric metric = new Metric(
                "jvm.heap_memory", "gauge", tags, "mycheck");
        assertEquals("jvm.heap_memory", metric.getAlias());
        assertEquals("gauge", metric.getMetricType());
        assertArrayEquals(tags, metric.getTags());
        assertEquals("mycheck", metric.getCheckName());
    }

    @Test
    public void testSetAndGetValue() {
        Metric metric = new Metric(
                "test.metric", "counter", new String[]{}, "check");
        assertEquals(0.0, metric.getValue(), 0.001);
        metric.setValue(42.5);
        assertEquals(42.5, metric.getValue(), 0.001);
    }

    @Test
    public void testSetValueOverwrite() {
        Metric metric = new Metric(
                "test.metric", "gauge", new String[]{}, "check");
        metric.setValue(10.0);
        assertEquals(10.0, metric.getValue(), 0.001);
        metric.setValue(20.0);
        assertEquals(20.0, metric.getValue(), 0.001);
    }

    // === Boundary tests ===

    @Test
    public void testMetricWithEmptyTags() {
        String[] emptyTags = new String[]{};
        Metric metric = new Metric(
                "test.metric", "gauge", emptyTags, "check");
        assertNotNull(metric.getTags());
        assertEquals(0, metric.getTags().length);
    }

    @Test
    public void testMetricWithLargeValue() {
        Metric metric = new Metric(
                "test.metric", "gauge", new String[]{}, "check");
        metric.setValue(Double.MAX_VALUE);
        assertEquals(Double.MAX_VALUE, metric.getValue(), 0.0);
    }

    @Test
    public void testMetricWithNegativeValue() {
        Metric metric = new Metric(
                "test.metric", "gauge", new String[]{}, "check");
        metric.setValue(-100.5);
        assertEquals(-100.5, metric.getValue(), 0.001);
    }

    @Test
    public void testMetricWithZeroValue() {
        Metric metric = new Metric(
                "test.metric", "gauge", new String[]{}, "check");
        metric.setValue(0.0);
        assertEquals(0.0, metric.getValue(), 0.001);
    }

    @Test
    public void testDefaultValueIsZero() {
        Metric metric = new Metric(
                "test.metric", "gauge", new String[]{}, "check");
        assertEquals(0.0, metric.getValue(), 0.0);
    }

    // === Edge case tests ===

    @Test
    public void testMetricWithManyTags() {
        String[] tags = new String[100];
        for (int i = 0; i < 100; i++) {
            tags[i] = "tag" + i + ":value" + i;
        }
        Metric metric = new Metric(
                "test.metric", "gauge", tags, "check");
        assertEquals(100, metric.getTags().length);
    }

    @Test
    public void testMetricTypesVariety() {
        String[] types = {
            "gauge", "counter", "rate",
            "histogram", "monotonic_count"
        };
        for (String type : types) {
            Metric metric = new Metric(
                    "test.metric", type, new String[]{}, "check");
            assertEquals(type, metric.getMetricType());
        }
    }
}
