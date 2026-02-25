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
import java.util.regex.Pattern;

import org.junit.Test;

public class TestFilter {

    @Test
    public void testConstructorWithNullFilter() {
        Filter filter = new Filter(null);
        assertTrue(filter.isEmptyFilter());
        assertTrue(filter.isEmptyBeanName());
    }

    @Test
    public void testConstructorWithEmptyMap() {
        Filter filter = new Filter(new HashMap<String, Object>());
        assertTrue(filter.isEmptyFilter());
        assertTrue(filter.isEmptyBeanName());
    }

    @Test
    public void testGetDomain() {
        Map<String, Object> filterMap = new HashMap<String, Object>();
        filterMap.put("domain", "org.datadog.test");
        Filter filter = new Filter(filterMap);
        assertEquals("org.datadog.test", filter.getDomain());
    }

    @Test
    public void testGetDomainNull() {
        Filter filter = new Filter(new HashMap<String, Object>());
        assertNull(filter.getDomain());
    }

    @Test
    public void testGetDomainRegex() {
        Map<String, Object> filterMap = new HashMap<String, Object>();
        filterMap.put("domain_regex", "org\\.datadog\\..*");
        Filter filter = new Filter(filterMap);
        Pattern regex = filter.getDomainRegex();
        assertNotNull(regex);
        assertTrue(regex.matcher("org.datadog.test").matches());
        assertFalse(regex.matcher("com.example.test").matches());
    }

    @Test
    public void testGetDomainRegexNull() {
        Filter filter = new Filter(new HashMap<String, Object>());
        assertNull(filter.getDomainRegex());
    }

    @Test
    public void testGetDomainRegexCaching() {
        Map<String, Object> filterMap = new HashMap<String, Object>();
        filterMap.put("domain_regex", "org\\.datadog\\..*");
        Filter filter = new Filter(filterMap);
        Pattern first = filter.getDomainRegex();
        Pattern second = filter.getDomainRegex();
        // Should return the same cached instance
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
        Filter filter = new Filter(new HashMap<String, Object>());
        assertNull(filter.getClassName());
    }

    @Test
    public void testGetClassNameRegex() {
        Map<String, Object> filterMap = new HashMap<String, Object>();
        filterMap.put("class_regex", ".*MyClass.*");
        Filter filter = new Filter(filterMap);
        Pattern regex = filter.getClassNameRegex();
        assertNotNull(regex);
        assertTrue(regex.matcher("com.example.MyClass").matches());
    }

    @Test
    public void testGetClassNameRegexNull() {
        Filter filter = new Filter(new HashMap<String, Object>());
        assertNull(filter.getClassNameRegex());
    }

    @Test
    public void testGetClassNameRegexCaching() {
        Map<String, Object> filterMap = new HashMap<String, Object>();
        filterMap.put("class_regex", ".*MyClass.*");
        Filter filter = new Filter(filterMap);
        Pattern first = filter.getClassNameRegex();
        Pattern second = filter.getClassNameRegex();
        assertTrue(first == second);
    }

    @Test
    public void testGetBeanNamesAsList() {
        Map<String, Object> filterMap = new HashMap<String, Object>();
        filterMap.put("bean", Arrays.asList("bean1:type=A", "bean2:type=B"));
        Filter filter = new Filter(filterMap);
        List<String> names = filter.getBeanNames();
        assertEquals(2, names.size());
        assertEquals("bean1:type=A", names.get(0));
        assertEquals("bean2:type=B", names.get(1));
    }

    @Test
    public void testGetBeanNamesAsSingleString() {
        Map<String, Object> filterMap = new HashMap<String, Object>();
        filterMap.put("bean", "single.bean:type=Test");
        Filter filter = new Filter(filterMap);
        List<String> names = filter.getBeanNames();
        assertEquals(1, names.size());
        assertEquals("single.bean:type=Test", names.get(0));
    }

    @Test
    public void testGetBeanNamesUsingBeanNameKey() {
        Map<String, Object> filterMap = new HashMap<String, Object>();
        filterMap.put("bean_name", "alt.bean:type=Test");
        Filter filter = new Filter(filterMap);
        List<String> names = filter.getBeanNames();
        assertEquals(1, names.size());
        assertEquals("alt.bean:type=Test", names.get(0));
    }

    @Test
    public void testGetBeanNamesEmpty() {
        Filter filter = new Filter(new HashMap<String, Object>());
        List<String> names = filter.getBeanNames();
        assertTrue(names.isEmpty());
    }

    @Test
    public void testGetBeanRegexesAsList() {
        Map<String, Object> filterMap = new HashMap<String, Object>();
        filterMap.put("bean_regex", Arrays.asList("org\\..*", "com\\..*"));
        Filter filter = new Filter(filterMap);
        List<Pattern> regexes = filter.getBeanRegexes();
        assertEquals(2, regexes.size());
    }

    @Test
    public void testGetBeanRegexesAsSingleString() {
        Map<String, Object> filterMap = new HashMap<String, Object>();
        filterMap.put("bean_regex", "org\\.datadog\\..*");
        Filter filter = new Filter(filterMap);
        List<Pattern> regexes = filter.getBeanRegexes();
        assertEquals(1, regexes.size());
        assertTrue(
                regexes.get(0)
                        .matcher("org.datadog.test")
                        .matches());
    }

    @Test
    public void testGetBeanRegexesEmpty() {
        Filter filter = new Filter(new HashMap<String, Object>());
        List<Pattern> regexes = filter.getBeanRegexes();
        assertTrue(regexes.isEmpty());
    }

    @Test
    public void testGetBeanRegexesCaching() {
        Map<String, Object> filterMap = new HashMap<String, Object>();
        filterMap.put("bean_regex", "org\\.datadog\\..*");
        Filter filter = new Filter(filterMap);
        List<Pattern> first = filter.getBeanRegexes();
        List<Pattern> second = filter.getBeanRegexes();
        assertTrue(first == second);
    }

    @Test
    public void testGetExcludeTagsAsList() {
        Map<String, Object> filterMap = new HashMap<String, Object>();
        filterMap.put(
                "exclude_tags",
                Arrays.asList("tag1", "tag2"));
        Filter filter = new Filter(filterMap);
        List<String> tags = filter.getExcludeTags();
        assertEquals(2, tags.size());
        assertEquals("tag1", tags.get(0));
    }

    @Test
    public void testGetExcludeTagsAsSingleString() {
        Map<String, Object> filterMap = new HashMap<String, Object>();
        filterMap.put("exclude_tags", "single_tag");
        Filter filter = new Filter(filterMap);
        List<String> tags = filter.getExcludeTags();
        assertEquals(1, tags.size());
        assertEquals("single_tag", tags.get(0));
    }

    @Test
    public void testGetExcludeTagsEmpty() {
        Filter filter = new Filter(new HashMap<String, Object>());
        List<String> tags = filter.getExcludeTags();
        assertTrue(tags.isEmpty());
    }

    @Test
    public void testGetExcludeTagsCaching() {
        Map<String, Object> filterMap = new HashMap<String, Object>();
        filterMap.put(
                "exclude_tags",
                Arrays.asList("tag1", "tag2"));
        Filter filter = new Filter(filterMap);
        List<String> first = filter.getExcludeTags();
        List<String> second = filter.getExcludeTags();
        assertTrue(first == second);
    }

    @Test
    public void testGetAdditionalTags() {
        Map<String, Object> filterMap = new HashMap<String, Object>();
        Map<String, String> tags = new HashMap<String, String>();
        tags.put("env", "prod");
        tags.put("region", "us-east");
        filterMap.put("tags", tags);
        Filter filter = new Filter(filterMap);
        Map<String, String> result = filter.getAdditionalTags();
        assertEquals(2, result.size());
        assertEquals("prod", result.get("env"));
    }

    @Test
    public void testGetAdditionalTagsEmpty() {
        Filter filter = new Filter(new HashMap<String, Object>());
        Map<String, String> tags = filter.getAdditionalTags();
        assertNotNull(tags);
        assertTrue(tags.isEmpty());
    }

    @Test
    public void testGetAdditionalTagsCaching() {
        Filter filter = new Filter(new HashMap<String, Object>());
        Map<String, String> first = filter.getAdditionalTags();
        Map<String, String> second = filter.getAdditionalTags();
        assertTrue(first == second);
    }

    @Test
    public void testGetAttribute() {
        Map<String, Object> filterMap = new HashMap<String, Object>();
        filterMap.put("attribute", "SomeAttribute");
        Filter filter = new Filter(filterMap);
        assertEquals("SomeAttribute", filter.getAttribute());
    }

    @Test
    public void testGetAttributeNull() {
        Filter filter = new Filter(new HashMap<String, Object>());
        assertNull(filter.getAttribute());
    }

    @Test
    public void testGetParameterValues() {
        Map<String, Object> filterMap = new HashMap<String, Object>();
        filterMap.put("type", Arrays.asList("TypeA", "TypeB"));
        Filter filter = new Filter(filterMap);
        List<String> values = filter.getParameterValues("type");
        assertEquals(2, values.size());
        assertEquals("TypeA", values.get(0));
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

    @Test
    public void testKeySet() {
        Map<String, Object> filterMap = new HashMap<String, Object>();
        filterMap.put("domain", "test");
        filterMap.put("type", "MyType");
        Filter filter = new Filter(filterMap);
        assertEquals(2, filter.keySet().size());
        assertTrue(filter.keySet().contains("domain"));
        assertTrue(filter.keySet().contains("type"));
    }

    @Test
    public void testToStringMethod() {
        Map<String, Object> filterMap = new HashMap<String, Object>();
        filterMap.put("domain", "test");
        Filter filter = new Filter(filterMap);
        assertNotNull(filter.toString());
        assertTrue(filter.toString().contains("domain"));
    }

    @Test
    public void testIsEmptyFilterFalse() {
        Map<String, Object> filterMap = new HashMap<String, Object>();
        filterMap.put("domain", "test");
        Filter filter = new Filter(filterMap);
        assertFalse(filter.isEmptyFilter());
    }

    @Test
    public void testIsEmptyBeanNameFalse() {
        Map<String, Object> filterMap = new HashMap<String, Object>();
        filterMap.put("bean", "some.bean:type=Test");
        Filter filter = new Filter(filterMap);
        assertFalse(filter.isEmptyBeanName());
    }

    @Test
    public void testBeanKeyPrefersBean() {
        // When both "bean" and "bean_name" are present, "bean" wins
        Map<String, Object> filterMap = new HashMap<String, Object>();
        filterMap.put("bean", "primary.bean:type=A");
        filterMap.put("bean_name", "secondary.bean:type=B");
        Filter filter = new Filter(filterMap);
        List<String> names = filter.getBeanNames();
        assertEquals(1, names.size());
        assertEquals("primary.bean:type=A", names.get(0));
    }
}
