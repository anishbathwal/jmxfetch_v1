package org.datadog.jmxfetch;

import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class TestJmxAttributeUtils {

    // === convertMetricName Tests ===

    @Test
    public void testConvertMetricNameCamelCase() {
        assertEquals("camel_case", JmxAttribute.convertMetricName("CamelCase"));
    }

    @Test
    public void testConvertMetricNameAllCaps() {
        assertEquals("all_caps", JmxAttribute.convertMetricName("ALLCaps"));
    }

    @Test
    public void testConvertMetricNameAlreadySnakeCase() {
        assertEquals("snake_case", JmxAttribute.convertMetricName("snake_case"));
    }

    @Test
    public void testConvertMetricNameLowercase() {
        assertEquals("lowercase", JmxAttribute.convertMetricName("lowercase"));
    }

    @Test
    public void testConvertMetricNameWithDot() {
        assertEquals("some.metric.name", JmxAttribute.convertMetricName("some.metric.name"));
    }

    @Test
    public void testConvertMetricNameMixedCamelAndDot() {
        assertEquals("heap_memory.used", JmxAttribute.convertMetricName("HeapMemory.Used"));
    }

    @Test
    public void testConvertMetricNameWithNumbers() {
        assertEquals("value123", JmxAttribute.convertMetricName("Value123"));
    }

    @Test
    public void testConvertMetricNameSpecialChars() {
        String result = JmxAttribute.convertMetricName("my-metric/name");
        assertNotNull(result);
        // Special chars get replaced with _
        assertTrue(result.contains("_"));
    }

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
    public void testConvertMetricNameConsecutiveCaps() {
        String result = JmxAttribute.convertMetricName("HTTPSConnection");
        assertNotNull(result);
        assertTrue(result.contains("_"));
    }

    @Test
    public void testConvertMetricNameDotUnderscore() {
        // dot_underscore pattern: _*._ * => .
        String result = JmxAttribute.convertMetricName("foo_.bar");
        assertEquals("foo.bar", result);
    }

    // === getBeanParametersHash Tests ===

    @Test
    public void testGetBeanParametersHashSimple() {
        Map<String, String> params = JmxAttribute.getBeanParametersHash("type=Cache,name=Test");
        assertEquals(2, params.size());
        assertEquals("Cache", params.get("type"));
        assertEquals("Test", params.get("name"));
    }

    @Test
    public void testGetBeanParametersHashSingleParam() {
        Map<String, String> params = JmxAttribute.getBeanParametersHash("type=Cache");
        assertEquals(1, params.size());
        assertEquals("Cache", params.get("type"));
    }

    @Test
    public void testGetBeanParametersHashNoValue() {
        Map<String, String> params = JmxAttribute.getBeanParametersHash("type");
        assertEquals(1, params.size());
        assertEquals("", params.get("type"));
    }

    @Test
    public void testGetBeanParametersHashMultipleParams() {
        Map<String, String> params = JmxAttribute.getBeanParametersHash(
                "type=ColumnFamily,keyspace=system,scope=peers,name=LiveDiskSpaceUsed");
        assertEquals(4, params.size());
        assertEquals("ColumnFamily", params.get("type"));
        assertEquals("system", params.get("keyspace"));
        assertEquals("peers", params.get("scope"));
        assertEquals("LiveDiskSpaceUsed", params.get("name"));
    }

    @Test
    public void testGetBeanParametersHashEmptyValue() {
        Map<String, String> params = JmxAttribute.getBeanParametersHash("type=,name=Test");
        assertEquals(2, params.size());
        assertEquals("", params.get("type"));
        assertEquals("Test", params.get("name"));
    }

    @Test
    public void testGetBeanParametersHashWithQuotedValue() {
        Map<String, String> params = JmxAttribute.getBeanParametersHash(
                "name=\"some.bean.0.0.0.0:80.metric\"");
        assertEquals(1, params.size());
    }
}
