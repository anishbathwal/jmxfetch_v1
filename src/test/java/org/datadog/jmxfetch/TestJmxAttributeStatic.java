package org.datadog.jmxfetch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class TestJmxAttributeStatic {

    // === convertMetricName Tests ===

    @Test
    public void testConvertMetricNameCamelCase() {
        assertEquals("heap_memory_usage",
                JmxAttribute.convertMetricName("HeapMemoryUsage"));
    }

    @Test
    public void testConvertMetricNameAllCaps() {
        assertEquals("gc_count", JmxAttribute.convertMetricName("GCCount"));
    }

    @Test
    public void testConvertMetricNameAlreadySnakeCase() {
        assertEquals("heap_memory",
                JmxAttribute.convertMetricName("heap_memory"));
    }

    @Test
    public void testConvertMetricNameWithDots() {
        assertEquals("jvm.heap.usage",
                JmxAttribute.convertMetricName("jvm.heap.usage"));
    }

    @Test
    public void testConvertMetricNameSpecialChars() {
        String result = JmxAttribute.convertMetricName("metric-with-dashes");
        assertNotNull(result);
        // Special chars should be replaced with underscore
        assertTrue(result.contains("_"));
    }

    @Test
    public void testConvertMetricNameMixedCase() {
        assertEquals("thread_count",
                JmxAttribute.convertMetricName("ThreadCount"));
    }

    @Test
    public void testConvertMetricNameWithNumbers() {
        assertEquals("gc_g1_young",
                JmxAttribute.convertMetricName("GcG1Young"));
    }

    // === getBeanParametersHash Tests ===

    @Test
    public void testGetBeanParametersHashSimple() {
        Map<String, String> params =
                JmxAttribute.getBeanParametersHash("type=Test,name=Foo");
        assertEquals(2, params.size());
        assertEquals("Test", params.get("type"));
        assertEquals("Foo", params.get("name"));
    }

    @Test
    public void testGetBeanParametersHashSingleParam() {
        Map<String, String> params =
                JmxAttribute.getBeanParametersHash("type=GarbageCollector");
        assertEquals(1, params.size());
        assertEquals("GarbageCollector", params.get("type"));
    }

    @Test
    public void testGetBeanParametersHashEmptyValue() {
        Map<String, String> params =
                JmxAttribute.getBeanParametersHash("type");
        assertEquals(1, params.size());
        assertEquals("", params.get("type"));
    }

    @Test
    public void testGetBeanParametersHashMultipleParams() {
        Map<String, String> params =
                JmxAttribute.getBeanParametersHash(
                        "type=Cache,keyspace=system,cache=KeyCache");
        assertEquals(3, params.size());
        assertEquals("Cache", params.get("type"));
        assertEquals("system", params.get("keyspace"));
        assertEquals("KeyCache", params.get("cache"));
    }

    // === getExcludedBeanParams Tests ===

    @Test
    public void testGetExcludedBeanParams() {
        List<String> excluded = JmxAttribute.getExcludedBeanParams();
        assertNotNull(excluded);
        assertTrue(excluded.contains("domain"));
        assertTrue(excluded.contains("bean"));
        assertTrue(excluded.contains("bean_name"));
        assertTrue(excluded.contains("bean_regex"));
        assertTrue(excluded.contains("attribute"));
        assertTrue(excluded.contains("exclude_tags"));
        assertTrue(excluded.contains("tags"));
        assertTrue(excluded.contains("domain_regex"));
        assertTrue(excluded.contains("class"));
        assertTrue(excluded.contains("class_regex"));
    }

    // === Boundary Tests ===

    @Test
    public void testConvertMetricNameEmpty() {
        assertEquals("", JmxAttribute.convertMetricName(""));
    }

    @Test
    public void testConvertMetricNameSingleChar() {
        assertEquals("a", JmxAttribute.convertMetricName("a"));
    }

    @Test
    public void testConvertMetricNameSingleUpperChar() {
        assertEquals("a", JmxAttribute.convertMetricName("A"));
    }

    @Test
    public void testGetBeanParametersHashWithEqualsInValue() {
        Map<String, String> params =
                JmxAttribute.getBeanParametersHash("name=a=b");
        // Split on first = only
        assertNotNull(params);
        assertTrue(params.containsKey("name"));
    }
}
