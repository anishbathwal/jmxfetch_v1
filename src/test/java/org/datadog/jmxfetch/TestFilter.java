package org.datadog.jmxfetch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.junit.Test;

public class TestFilter {

    // === Positive Tests ===

    @Test
    public void testConstructorWithValidMap() {
        Map<String, Object> filterMap = new HashMap<String, Object>();
        filterMap.put("domain", "org.datadog");
        Filter filter = new Filter(filterMap);
        assertNotNull(filter);
        assertEquals("org.datadog", filter.getDomain());
    }

    @Test
    public void testConstructorWithNullCreatesEmptyFilter() {
        Filter filter = new Filter(null);
        assertNotNull(filter);
        assertTrue(filter.isEmptyFilter());
    }

    @Test
    public void testGetBeanNamesSingleBean() {
        Map<String, Object> filterMap = new HashMap<String, Object>();
        filterMap.put("bean", "org.datadog:type=Test");
        Filter filter = new Filter(filterMap);
        List<String> beanNames = filter.getBeanNames();
        assertEquals(1, beanNames.size());
        assertEquals("org.datadog:type=Test", beanNames.get(0));
    }

    @Test
    public void testGetBeanNamesMultipleBeans() {
        Map<String, Object> filterMap = new HashMap<String, Object>();
        filterMap.put("bean", Arrays.asList("bean1:type=A", "bean2:type=B"));
        Filter filter = new Filter(filterMap);
        List<String> beanNames = filter.getBeanNames();
        assertEquals(2, beanNames.size());
    }

    @Test
    public void testGetBeanNamesFallsToBeanName() {
        Map<String, Object> filterMap = new HashMap<String, Object>();
        filterMap.put("bean_name", "org.datadog:type=Fallback");
        Filter filter = new Filter(filterMap);
        List<String> beanNames = filter.getBeanNames();
        assertEquals(1, beanNames.size());
        assertEquals("org.datadog:type=Fallback", beanNames.get(0));
    }

    @Test
    public void testGetBeanRegexesSingleRegex() {
        Map<String, Object> filterMap = new HashMap<String, Object>();
        filterMap.put("bean_regex", "org\\.datadog.*");
        Filter filter = new Filter(filterMap);
        List<Pattern> regexes = filter.getBeanRegexes();
        assertEquals(1, regexes.size());
        assertTrue(regexes.get(0).matcher("org.datadog.test").matches());
    }

    @Test
    public void testGetBeanRegexesMultipleRegexes() {
        Map<String, Object> filterMap = new HashMap<String, Object>();
        filterMap.put("bean_regex", Arrays.asList("org\\.datadog.*", "com\\.example.*"));
        Filter filter = new Filter(filterMap);
        List<Pattern> regexes = filter.getBeanRegexes();
        assertEquals(2, regexes.size());
    }

    @Test
    public void testGetExcludeTagsSingleTag() {
        Map<String, Object> filterMap = new HashMap<String, Object>();
        filterMap.put("exclude_tags", "instance");
        Filter filter = new Filter(filterMap);
        List<String> tags = filter.getExcludeTags();
        assertEquals(1, tags.size());
        assertEquals("instance", tags.get(0));
    }

    @Test
    public void testGetExcludeTagsMultipleTags() {
        Map<String, Object> filterMap = new HashMap<String, Object>();
        filterMap.put("exclude_tags", Arrays.asList("instance", "host"));
        Filter filter = new Filter(filterMap);
        List<String> tags = filter.getExcludeTags();
        assertEquals(2, tags.size());
    }

    @Test
    public void testGetAdditionalTags() {
        Map<String, Object> filterMap = new HashMap<String, Object>();
        Map<String, String> tags = new HashMap<String, String>();
        tags.put("env", "production");
        filterMap.put("tags", tags);
        Filter filter = new Filter(filterMap);
        Map<String, String> additionalTags = filter.getAdditionalTags();
        assertEquals(1, additionalTags.size());
        assertEquals("production", additionalTags.get("env"));
    }

    @Test
    public void testGetDomainRegex() {
        Map<String, Object> filterMap = new HashMap<String, Object>();
        filterMap.put("domain_regex", "org\\.datadog\\..*");
        Filter filter = new Filter(filterMap);
        Pattern regex = filter.getDomainRegex();
        assertNotNull(regex);
        assertTrue(regex.matcher("org.datadog.test").matches());
    }

    @Test
    public void testGetClassName() {
        Map<String, Object> filterMap = new HashMap<String, Object>();
        filterMap.put("class", "MyClass");
        Filter filter = new Filter(filterMap);
        assertEquals("MyClass", filter.getClassName());
    }

    @Test
    public void testGetClassNameRegex() {
        Map<String, Object> filterMap = new HashMap<String, Object>();
        filterMap.put("class_regex", "My.*Class");
        Filter filter = new Filter(filterMap);
        Pattern regex = filter.getClassNameRegex();
        assertNotNull(regex);
        assertTrue(regex.matcher("MyTestClass").matches());
    }

    @Test
    public void testGetAttribute() {
        Map<String, Object> filterMap = new HashMap<String, Object>();
        filterMap.put("attribute", "HeapMemoryUsage");
        Filter filter = new Filter(filterMap);
        assertEquals("HeapMemoryUsage", filter.getAttribute());
    }

    @Test
    public void testGetParameterValues() {
        Map<String, Object> filterMap = new HashMap<String, Object>();
        filterMap.put("type", Arrays.asList("TypeA", "TypeB"));
        Filter filter = new Filter(filterMap);
        List<String> values = filter.getParameterValues("type");
        assertEquals(2, values.size());
    }

    @Test
    public void testKeySet() {
        Map<String, Object> filterMap = new HashMap<String, Object>();
        filterMap.put("domain", "test");
        filterMap.put("bean", "test:type=Test");
        Filter filter = new Filter(filterMap);
        Set<String> keys = filter.keySet();
        assertTrue(keys.contains("domain"));
        assertTrue(keys.contains("bean"));
    }

    @Test
    public void testToString() {
        Map<String, Object> filterMap = new HashMap<String, Object>();
        filterMap.put("domain", "test");
        Filter filter = new Filter(filterMap);
        assertNotNull(filter.toString());
        assertTrue(filter.toString().contains("domain"));
    }

    // === Negative Tests ===

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
        assertTrue(tags.isEmpty());
    }

    @Test
    public void testGetDomainWhenNull() {
        Filter filter = new Filter(null);
        assertNull(filter.getDomain());
    }

    @Test
    public void testGetDomainRegexWhenNull() {
        Filter filter = new Filter(null);
        assertNull(filter.getDomainRegex());
    }

    @Test
    public void testGetClassNameWhenNull() {
        Filter filter = new Filter(null);
        assertNull(filter.getClassName());
    }

    @Test
    public void testGetClassNameRegexWhenNull() {
        Filter filter = new Filter(null);
        assertNull(filter.getClassNameRegex());
    }

    @Test
    public void testGetAttributeWhenNull() {
        Filter filter = new Filter(null);
        assertNull(filter.getAttribute());
    }

    // === Boundary Tests ===

    @Test
    public void testIsEmptyBeanNameWhenBothNull() {
        Filter filter = new Filter(null);
        assertTrue(filter.isEmptyBeanName());
    }

    @Test
    public void testIsEmptyBeanNameWhenBeanSet() {
        Map<String, Object> filterMap = new HashMap<String, Object>();
        filterMap.put("bean", "test:type=Test");
        Filter filter = new Filter(filterMap);
        assertFalse(filter.isEmptyBeanName());
    }

    @Test
    public void testIsEmptyFilterWithEmptyMap() {
        Map<String, Object> filterMap = new HashMap<String, Object>();
        Filter filter = new Filter(filterMap);
        assertTrue(filter.isEmptyFilter());
    }

    @Test
    public void testIsEmptyFilterWithData() {
        Map<String, Object> filterMap = new HashMap<String, Object>();
        filterMap.put("domain", "test");
        Filter filter = new Filter(filterMap);
        assertFalse(filter.isEmptyFilter());
    }

    @Test
    public void testGetBeanRegexesCachesResult() {
        Map<String, Object> filterMap = new HashMap<String, Object>();
        filterMap.put("bean_regex", "test.*");
        Filter filter = new Filter(filterMap);
        List<Pattern> first = filter.getBeanRegexes();
        List<Pattern> second = filter.getBeanRegexes();
        assertTrue(first == second);
    }

    @Test
    public void testGetExcludeTagsCachesResult() {
        Map<String, Object> filterMap = new HashMap<String, Object>();
        filterMap.put("exclude_tags", Arrays.asList("tag1", "tag2"));
        Filter filter = new Filter(filterMap);
        List<String> first = filter.getExcludeTags();
        List<String> second = filter.getExcludeTags();
        assertTrue(first == second);
    }

    @Test
    public void testGetDomainRegexCachesResult() {
        Map<String, Object> filterMap = new HashMap<String, Object>();
        filterMap.put("domain_regex", "test\\..*");
        Filter filter = new Filter(filterMap);
        Pattern first = filter.getDomainRegex();
        Pattern second = filter.getDomainRegex();
        assertTrue(first == second);
    }

    @Test
    public void testGetClassNameRegexCachesResult() {
        Map<String, Object> filterMap = new HashMap<String, Object>();
        filterMap.put("class_regex", "My.*");
        Filter filter = new Filter(filterMap);
        Pattern first = filter.getClassNameRegex();
        Pattern second = filter.getClassNameRegex();
        assertTrue(first == second);
    }

    @Test
    public void testGetParameterValuesSingleValue() {
        Map<String, Object> filterMap = new HashMap<String, Object>();
        filterMap.put("type", "SingleType");
        Filter filter = new Filter(filterMap);
        List<String> values = filter.getParameterValues("type");
        assertEquals(1, values.size());
        assertEquals("SingleType", values.get(0));
    }
}
