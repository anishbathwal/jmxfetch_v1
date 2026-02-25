package org.datadog.jmxfetch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.junit.Test;

public class FilterTest {

    // === Positive tests ===

    @Test
    public void testConstructorWithNullCreatesEmptyFilter() {
        Filter filter = new Filter(null);
        assertTrue(filter.isEmptyFilter());
        assertTrue(filter.isEmptyBeanName());
    }

    @Test
    public void testConstructorWithValidMap() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("domain", "org.datadog");
        Filter filter = new Filter(map);
        assertFalse(filter.isEmptyFilter());
        assertEquals("org.datadog", filter.getDomain());
    }

    @Test
    public void testGetBeanNamesWithSingleBean() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("bean", "org.datadog:type=Test");
        Filter filter = new Filter(map);
        List<String> beanNames = filter.getBeanNames();
        assertEquals(1, beanNames.size());
        assertEquals("org.datadog:type=Test", beanNames.get(0));
    }

    @Test
    public void testGetBeanNamesWithBeanNameKey() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("bean_name", "org.datadog:type=Alt");
        Filter filter = new Filter(map);
        List<String> beanNames = filter.getBeanNames();
        assertEquals(1, beanNames.size());
        assertEquals("org.datadog:type=Alt", beanNames.get(0));
    }

    @Test
    public void testGetBeanNamesWithList() {
        Map<String, Object> map = new HashMap<String, Object>();
        List<String> beans = Arrays.asList(
                "org.datadog:type=A", "org.datadog:type=B");
        map.put("bean", beans);
        Filter filter = new Filter(map);
        List<String> beanNames = filter.getBeanNames();
        assertEquals(2, beanNames.size());
        assertEquals("org.datadog:type=A", beanNames.get(0));
        assertEquals("org.datadog:type=B", beanNames.get(1));
    }

    @Test
    public void testGetBeanRegexesWithSingleRegex() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("bean_regex", "org\\.datadog.*");
        Filter filter = new Filter(map);
        List<Pattern> regexes = filter.getBeanRegexes();
        assertEquals(1, regexes.size());
        assertTrue(regexes.get(0).matcher(
                "org.datadog.test").matches());
    }

    @Test
    public void testGetBeanRegexesWithList() {
        Map<String, Object> map = new HashMap<String, Object>();
        List<String> regexList = Arrays.asList(
                "org\\.datadog.*", "com\\.example.*");
        map.put("bean_regex", regexList);
        Filter filter = new Filter(map);
        List<Pattern> regexes = filter.getBeanRegexes();
        assertEquals(2, regexes.size());
    }

    @Test
    public void testGetBeanRegexesCaching() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("bean_regex", "org\\.datadog.*");
        Filter filter = new Filter(map);
        List<Pattern> first = filter.getBeanRegexes();
        List<Pattern> second = filter.getBeanRegexes();
        // Should return the same cached list
        assertTrue(first == second);
    }

    @Test
    public void testGetExcludeTagsWithList() {
        Map<String, Object> map = new HashMap<String, Object>();
        List<String> tags = Arrays.asList("tag1", "tag2");
        map.put("exclude_tags", tags);
        Filter filter = new Filter(map);
        List<String> excludeTags = filter.getExcludeTags();
        assertEquals(2, excludeTags.size());
        assertEquals("tag1", excludeTags.get(0));
    }

    @Test
    public void testGetExcludeTagsCaching() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("exclude_tags", "single_tag");
        Filter filter = new Filter(map);
        List<String> first = filter.getExcludeTags();
        List<String> second = filter.getExcludeTags();
        assertTrue(first == second);
    }

    @Test
    public void testGetAdditionalTags() {
        Map<String, Object> map = new HashMap<String, Object>();
        Map<String, String> tags = new HashMap<String, String>();
        tags.put("env", "prod");
        tags.put("service", "myapp");
        map.put("tags", tags);
        Filter filter = new Filter(map);
        Map<String, String> additionalTags =
                filter.getAdditionalTags();
        assertEquals(2, additionalTags.size());
        assertEquals("prod", additionalTags.get("env"));
    }

    @Test
    public void testGetDomainRegex() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("domain_regex", "org\\.datadog\\..*");
        Filter filter = new Filter(map);
        Pattern regex = filter.getDomainRegex();
        assertNotNull(regex);
        assertTrue(regex.matcher(
                "org.datadog.jmxfetch").matches());
    }

    @Test
    public void testGetDomainRegexCaching() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("domain_regex", "org\\.datadog\\..*");
        Filter filter = new Filter(map);
        Pattern first = filter.getDomainRegex();
        Pattern second = filter.getDomainRegex();
        assertTrue(first == second);
    }

    @Test
    public void testGetClassName() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("class", "com.example.MyClass");
        Filter filter = new Filter(map);
        assertEquals(
                "com.example.MyClass", filter.getClassName());
    }

    @Test
    public void testGetClassNameRegex() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("class_regex", "com\\.example\\..*");
        Filter filter = new Filter(map);
        Pattern regex = filter.getClassNameRegex();
        assertNotNull(regex);
        assertTrue(regex.matcher(
                "com.example.Foo").matches());
    }

    @Test
    public void testGetClassNameRegexCaching() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("class_regex", "com\\.example\\..*");
        Filter filter = new Filter(map);
        Pattern first = filter.getClassNameRegex();
        Pattern second = filter.getClassNameRegex();
        assertTrue(first == second);
    }

    @Test
    public void testGetAttribute() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("attribute", "HeapMemoryUsage");
        Filter filter = new Filter(map);
        assertEquals("HeapMemoryUsage", filter.getAttribute());
    }

    @Test
    public void testGetParameterValues() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("type", "GarbageCollector");
        Filter filter = new Filter(map);
        List<String> values = filter.getParameterValues("type");
        assertEquals(1, values.size());
        assertEquals("GarbageCollector", values.get(0));
    }

    @Test
    public void testGetParameterValuesAsList() {
        Map<String, Object> map = new HashMap<String, Object>();
        List<String> types = Arrays.asList("TypeA", "TypeB");
        map.put("type", types);
        Filter filter = new Filter(map);
        List<String> values = filter.getParameterValues("type");
        assertEquals(2, values.size());
    }

    @Test
    public void testToString() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("domain", "test");
        Filter filter = new Filter(map);
        assertEquals(map.toString(), filter.toString());
    }

    @Test
    public void testKeySet() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("domain", "test");
        map.put("bean", "org:type=T");
        Filter filter = new Filter(map);
        Set<String> keys = filter.keySet();
        assertEquals(2, keys.size());
        assertTrue(keys.contains("domain"));
        assertTrue(keys.contains("bean"));
    }

    // === Negative / Boundary tests ===

    @Test
    public void testGetBeanNamesWhenEmpty() {
        Filter filter = new Filter(null);
        List<String> beanNames = filter.getBeanNames();
        assertTrue(beanNames.isEmpty());
    }

    @Test
    public void testGetBeanRegexesWhenNull() {
        Filter filter = new Filter(null);
        List<Pattern> regexes = filter.getBeanRegexes();
        assertTrue(regexes.isEmpty());
    }

    @Test
    public void testGetExcludeTagsWhenNull() {
        Filter filter = new Filter(null);
        List<String> tags = filter.getExcludeTags();
        assertTrue(tags.isEmpty());
    }

    @Test
    public void testGetAdditionalTagsWhenNull() {
        Filter filter = new Filter(null);
        Map<String, String> tags = filter.getAdditionalTags();
        assertNotNull(tags);
        assertTrue(tags.isEmpty());
    }

    @Test
    public void testGetDomainReturnsNullWhenMissing() {
        Filter filter = new Filter(null);
        assertNull(filter.getDomain());
    }

    @Test
    public void testGetDomainRegexReturnsNullWhenMissing() {
        Filter filter = new Filter(null);
        assertNull(filter.getDomainRegex());
    }

    @Test
    public void testGetClassNameReturnsNullWhenMissing() {
        Filter filter = new Filter(null);
        assertNull(filter.getClassName());
    }

    @Test
    public void testGetClassNameRegexReturnsNullWhenMissing() {
        Filter filter = new Filter(null);
        assertNull(filter.getClassNameRegex());
    }

    @Test
    public void testGetAttributeReturnsNullWhenMissing() {
        Filter filter = new Filter(null);
        assertNull(filter.getAttribute());
    }

    @Test
    public void testIsEmptyBeanNameWithBothKeysNull() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("domain", "test");
        Filter filter = new Filter(map);
        assertTrue(filter.isEmptyBeanName());
    }

    @Test
    public void testIsEmptyFilterWithEmptyMap() {
        Map<String, Object> map = new HashMap<String, Object>();
        Filter filter = new Filter(map);
        assertTrue(filter.isEmptyFilter());
    }

    @Test
    public void testIsEmptyFilterWithPopulatedMap() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("domain", "test");
        Filter filter = new Filter(map);
        assertFalse(filter.isEmptyFilter());
    }

    @Test
    public void testBeanKeyPrefersBean() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("bean", "org.datadog:type=Primary");
        map.put("bean_name", "org.datadog:type=Secondary");
        Filter filter = new Filter(map);
        assertFalse(filter.isEmptyBeanName());
        List<String> beanNames = filter.getBeanNames();
        assertEquals("org.datadog:type=Primary",
                beanNames.get(0));
    }
}
