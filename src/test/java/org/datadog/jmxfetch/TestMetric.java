package org.datadog.jmxfetch;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestMetric {

    @Test
    public void testConstructorAndGetters() {
        String[] tags = new String[]{"env:prod", "region:us"};
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
    public void testSetValueNegative() {
        Metric metric = new Metric(
                "test.metric", "gauge", new String[]{}, "check");
        metric.setValue(-100.0);
        assertEquals(-100.0, metric.getValue(), 0.001);
    }

    @Test
    public void testSetValueZero() {
        Metric metric = new Metric(
                "test.metric", "gauge", new String[]{}, "check");
        metric.setValue(0.0);
        assertEquals(0.0, metric.getValue(), 0.001);
    }

    @Test
    public void testSetValueLargeNumber() {
        Metric metric = new Metric(
                "test.metric", "gauge", new String[]{}, "check");
        metric.setValue(Double.MAX_VALUE);
        assertEquals(Double.MAX_VALUE, metric.getValue(), 0.0);
    }

    @Test
    public void testEmptyTags() {
        String[] tags = new String[]{};
        Metric metric = new Metric(
                "test.metric", "gauge", tags, "check");
        assertEquals(0, metric.getTags().length);
    }

    @Test
    public void testMultipleTags() {
        String[] tags = new String[]{
            "env:prod", "region:us-east-1",
            "service:web", "version:1.0"
        };
        Metric metric = new Metric(
                "test.metric", "histogram", tags, "mycheck");
        assertEquals(4, metric.getTags().length);
        assertEquals("histogram", metric.getMetricType());
    }
}
