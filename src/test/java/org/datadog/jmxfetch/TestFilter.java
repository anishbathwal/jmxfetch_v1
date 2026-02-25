package org.datadog.jmxfetch;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class TestFilter {

    // === Positive Tests ===

    @Test
    public void testConstructorWithValidMap() {
        Map<String, Object> filterMap = new HashMap<String, Object>();
        filterMap.put("domain", "org.datadog");
        Filter filter = new Filter(filterMap);
        assertEquals("org.datadog", filter.getDomain());
    }

    @Test
    public void testConstructorWithNull() {
        Filter filter = new Filter(null);
        assertTrue(filter.isEmptyFilter());
    }

    @Test
    public void testGetDomain() {
        Map<String, Object> filterMap = new HashMap<String, Object>();
        filterMap.put("domain", "test.domain");
        Filter filter = new Filter(filterMap);
        assertEquals("test.domain", filter.getDomain());
    }

    @Test
    public void testGetDomainNull() {
        Filter filter = new Filter(null);
        assertNull(filter.getDomain());
    }

    @Test
    public void testGetBeanNamesWithBeanKey() {
        Map<String, Object> filterMap = new HashMap<String, Object>();
        filterMap.put("bean", Arrays.asList("bean1", "bean2"));
        Filter filter = new Filter(filterMap);
        List<String> beanNames = filter.getBeanNames();
        assertEquals(2, beanNames.size());
        assertTrue(beanNames.contains("bean1"));
        assertTrue(beanNames.contains("bean2"));
    }

    @Test
    public void testGetBeanNamesWithBeanNameKey() {
        Map<String, Object> filterMap = new HashMap<String, Object>();
        filterMap.put("bean_name", "singleBean");
        Filter filter = new Filter(filterMap);
        List<String> beanNames = filter.getBeanNames();
        assertEquals(1, beanNames.size());
        assertEquals("singleBean", beanNames.get(0));
    }

    @Test
    public void testGetBeanNamesEmpty() {
        Filter filter = new Filter(null);
        List<String> beanNames = filter.getBeanNames();
        assertTrue(beanNames.isEmpty());
    }

    @Test
    public void testGetBeanRegexesWithValues() {
        Map<String, Object> filterMap = new HashMap<String, Object>();
        filterMap.put("bean_regex", Arrays.asList("org\\.test\\..*", "com\\.example\\..*"));
        Filter filter = new Filter(filterMap);
        List<Pattern> regexes = filter.getBeanRegexes();
        assertEquals(2, regexes.size());
    }

    @Test
    public void testGetBeanRegexesEmpty() {
        Filter filter = new Filter(null);
        List<Pattern> regexes = filter.getBeanRegexes();
        assertTrue(regexes.isEmpty());
    }

    @Test
    public void testGetBeanRegexesSingleValue() {
        Map<String, Object> filterMap = new HashMap<String, Object>();
        filterMap.put("bean_regex", "org\\.test\\..*");
        Filter filter = new Filter(filterMap);
        List<Pattern> regexes = filter.getBeanRegexes();
        assertEquals(1, regexes.size());
    }

    @Test
    public void testGetBeanRegexesCaching() {
        Map<String, Object> filterMap = new HashMap<String, Object>();
        filterMap.put("bean_regex", "org\\.test\\..*");
        Filter filter = new Filter(filterMap);
        List<Pattern> first = filter.getBeanRegexes();
        List<Pattern> second = filter.getBeanRegexes();
        assertTrue(first == second);
    }

    @Test
    public void testGetExcludeTagsWithValues() {
        Map<String, Object> filterMap = new HashMap<String, Object>();
        filterMap.put("exclude_tags", Arrays.asList("tag1", "tag2"));
        Filter filter = new Filter(filterMap);
        List<String> tags = filter.getExcludeTags();
        assertEquals(2, tags.size());
    }

    @Test
    public void testGetExcludeTagsEmpty() {
        Filter filter = new Filter(null);
        List<String> tags = filter.getExcludeTags();
        assertTrue(tags.isEmpty());
    }

    @Test
    public void testGetExcludeTagsSingleValue() {
        Map<String, Object> filterMap = new HashMap<String, Object>();
        filterMap.put("exclude_tags", "singleTag");
        Filter filter = new Filter(filterMap);
        List<String> tags = filter.getExcludeTags();
        assertEquals(1, tags.size());
        assertEquals("singleTag", tags.get(0));
    }

    @Test
    public void testGetAdditionalTagsWithValues() {
        Map<String, Object> filterMap = new HashMap<String, Object>();
        Map<String, String> tags = new HashMap<String, String>();
        tags.put("env", "production");
        tags.put("team", "backend");
        filterMap.put("tags", tags);
        Filter filter = new Filter(filterMap);
        Map<String, String> additionalTags = filter.getAdditionalTags();
        assertEquals(2, additionalTags.size());
        assertEquals("production", additionalTags.get("env"));
    }

    @Test
    public void testGetAdditionalTagsEmpty() {
        Filter filter = new Filter(null);
        Map<String, String> tags = filter.getAdditionalTags();
        assertNotNull(tags);
        assertTrue(tags.isEmpty());
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
    public void testGetDomainRegexNull() {
        Filter filter = new Filter(null);
        assertNull(filter.getDomainRegex());
    }

    @Test
    public void testGetDomainRegexCaching() {
        Map<String, Object> filterMap = new HashMap<String, Object>();
        filterMap.put("domain_regex", "test\\..*");
        Filter filter = new Filter(filterMap);
        Pattern first = filter.getDomainRegex();
        Pattern second = filter.getDomainRegex();
        assertTrue(first == second);
    }

    @Test
    public void testGetClassName() {
        Map<String, Object> filterMap = new HashMap<String, Object>();
        filterMap.put("class", "com.example.MyClass");
        Filter filter = new Filter(filterMap);
        assertEquals("com.example.MyClass", filter.getClassName());
    }

    @Test
    public void testGetClassNameNull() {
        Filter filter = new Filter(null);
        assertNull(filter.getClassName());
    }

    @Test
    public void testGetClassNameRegex() {
        Map<String, Object> filterMap = new HashMap<String, Object>();
        filterMap.put("class_regex", "com\\.example\\..*");
        Filter filter = new Filter(filterMap);
        Pattern regex = filter.getClassNameRegex();
        assertNotNull(regex);
        assertTrue(regex.matcher("com.example.MyClass").matches());
    }

    @Test
    public void testGetClassNameRegexNull() {
        Filter filter = new Filter(null);
        assertNull(filter.getClassNameRegex());
    }

    @Test
    public void testGetClassNameRegexCaching() {
        Map<String, Object> filterMap = new HashMap<String, Object>();
        filterMap.put("class_regex", "com\\..*");
        Filter filter = new Filter(filterMap);
        Pattern first = filter.getClassNameRegex();
        Pattern second = filter.getClassNameRegex();
        assertTrue(first == second);
    }

    @Test
    public void testGetAttribute() {
        Map<String, Object> filterMap = new HashMap<String, Object>();
        filterMap.put("attribute", "myAttribute");
        Filter filter = new Filter(filterMap);
        assertEquals("myAttribute", filter.getAttribute());
    }

    @Test
    public void testGetParameterValues() {
        Map<String, Object> filterMap = new HashMap<String, Object>();
        filterMap.put("type", Arrays.asList("typeA", "typeB"));
        Filter filter = new Filter(filterMap);
        List<String> values = filter.getParameterValues("type");
        assertEquals(2, values.size());
    }

    @Test
    public void testGetParameterValuesSingleString() {
        Map<String, Object> filterMap = new HashMap<String, Object>();
        filterMap.put("type", "singleType");
        Filter filter = new Filter(filterMap);
        List<String> values = filter.getParameterValues("type");
        assertEquals(1, values.size());
        assertEquals("singleType", values.get(0));
    }

    @Test
    public void testIsEmptyBeanNameTrue() {
        Filter filter = new Filter(null);
        assertTrue(filter.isEmptyBeanName());
    }

    @Test
    public void testIsEmptyBeanNameFalseWithBean() {
        Map<String, Object> filterMap = new HashMap<String, Object>();
        filterMap.put("bean", "testBean");
        Filter filter = new Filter(filterMap);
        assertFalse(filter.isEmptyBeanName());
    }

    @Test
    public void testIsEmptyBeanNameFalseWithBeanName() {
        Map<String, Object> filterMap = new HashMap<String, Object>();
        filterMap.put("bean_name", "testBean");
        Filter filter = new Filter(filterMap);
        assertFalse(filter.isEmptyBeanName());
    }

    @Test
    public void testIsEmptyFilter() {
        Filter filter = new Filter(null);
        assertTrue(filter.isEmptyFilter());
    }

    @Test
    public void testIsNotEmptyFilter() {
        Map<String, Object> filterMap = new HashMap<String, Object>();
        filterMap.put("domain", "test");
        Filter filter = new Filter(filterMap);
        assertFalse(filter.isEmptyFilter());
    }

    @Test
    public void testToString() {
        Map<String, Object> filterMap = new HashMap<String, Object>();
        filterMap.put("domain", "test");
        Filter filter = new Filter(filterMap);
        assertNotNull(filter.toString());
        assertTrue(filter.toString().contains("domain"));
    }

    @Test
    public void testKeySet() {
        Map<String, Object> filterMap = new HashMap<String, Object>();
        filterMap.put("domain", "test");
        filterMap.put("bean", "myBean");
        Filter filter = new Filter(filterMap);
        Set<String> keys = filter.keySet();
        assertEquals(2, keys.size());
        assertTrue(keys.contains("domain"));
        assertTrue(keys.contains("bean"));
    }
}
