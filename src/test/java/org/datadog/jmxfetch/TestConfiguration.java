package org.datadog.jmxfetch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class TestConfiguration {

    private Configuration createConfig(
            Map<String, Object> include, Map<String, Object> exclude) {
        Map<String, Object> conf = new HashMap<String, Object>();
        if (include != null) {
            conf.put("include", include);
        }
        if (exclude != null) {
            conf.put("exclude", exclude);
        }
        return new Configuration(conf);
    }

    // === Positive Tests ===

    @Test
    public void testConstructorWithIncludeAndExclude() {
        Map<String, Object> include = new HashMap<String, Object>();
        include.put("domain", "org.datadog");
        Map<String, Object> exclude = new HashMap<String, Object>();
        exclude.put("domain", "org.excluded");
        Configuration config = createConfig(include, exclude);

        assertNotNull(config.getInclude());
        assertNotNull(config.getExclude());
        assertEquals("org.datadog", config.getInclude().getDomain());
        assertEquals("org.excluded", config.getExclude().getDomain());
    }

    @Test
    public void testToString() {
        Map<String, Object> include = new HashMap<String, Object>();
        include.put("domain", "test");
        Configuration config = createConfig(include, null);
        String result = config.toString();
        assertNotNull(result);
        assertTrue(result.contains("include"));
        assertTrue(result.contains("exclude"));
    }

    @Test
    public void testGetConf() {
        Map<String, Object> include = new HashMap<String, Object>();
        include.put("domain", "test");
        Configuration config = createConfig(include, null);
        assertNotNull(config.getConf());
    }

    @Test
    public void testGetDynamicTagsDefault() {
        Configuration config = createConfig(null, null);
        assertNotNull(config.getDynamicTags());
        assertTrue(config.getDynamicTags().isEmpty());
    }

    @Test
    public void testGetDynamicTagsWithValidConfig() {
        Map<String, Object> conf = new HashMap<String, Object>();
        conf.put("include", new HashMap<String, Object>());

        Map<String, Object> tagConfig = new HashMap<String, Object>();
        tagConfig.put("tag_name", "cluster_id");
        tagConfig.put("bean_name", "kafka.server:type=KafkaServer");
        tagConfig.put("attribute", "Value");

        List<Object> dynamicTagsList = new ArrayList<Object>();
        dynamicTagsList.add(tagConfig);
        conf.put("dynamic_tags", dynamicTagsList);

        Configuration config = new Configuration(conf);
        List<DynamicTag> tags = config.getDynamicTags();
        assertEquals(1, tags.size());
        assertEquals("cluster_id", tags.get(0).getTagName());
    }

    @Test
    public void testParseDynamicTagsWithInvalidType() {
        Map<String, Object> conf = new HashMap<String, Object>();
        conf.put("include", new HashMap<String, Object>());
        conf.put("dynamic_tags", "not_a_list");

        Configuration config = new Configuration(conf);
        assertTrue(config.getDynamicTags().isEmpty());
    }

    @Test
    public void testParseDynamicTagsWithNullEntry() {
        Map<String, Object> conf = new HashMap<String, Object>();
        conf.put("include", new HashMap<String, Object>());

        List<Object> dynamicTagsList = new ArrayList<Object>();
        dynamicTagsList.add(null);
        conf.put("dynamic_tags", dynamicTagsList);

        Configuration config = new Configuration(conf);
        assertTrue(config.getDynamicTags().isEmpty());
    }

    // === getGreatestCommonScopes tests ===

    @Test
    public void testGetGreatestCommonScopesEmptyList() {
        List<Configuration> configList = new ArrayList<Configuration>();
        List<String> scopes = Configuration.getGreatestCommonScopes(configList);
        assertNotNull(scopes);
        assertTrue(scopes.isEmpty());
    }

    @Test
    public void testGetGreatestCommonScopesWithIncludeFilter() {
        Map<String, Object> include = new HashMap<String, Object>();
        include.put("domain", "org.datadog");
        Configuration config = createConfig(include, null);
        List<Configuration> configList = new ArrayList<Configuration>();
        configList.add(config);
        List<String> scopes = Configuration.getGreatestCommonScopes(configList);
        assertNotNull(scopes);
    }

    @Test
    public void testGetGreatestCommonScopesWithBeanNames() {
        Map<String, Object> include = new HashMap<String, Object>();
        include.put("bean", "org.datadog:type=Test,name=Foo");
        Configuration config = createConfig(include, null);

        List<Configuration> configList = new ArrayList<Configuration>();
        configList.add(config);
        List<String> scopes = Configuration.getGreatestCommonScopes(configList);
        assertNotNull(scopes);
        assertFalse(scopes.isEmpty());
    }

    @Test
    public void testGetGreatestCommonScopesWithMultipleBeanNames() {
        Map<String, Object> include1 = new HashMap<String, Object>();
        include1.put("bean", "org.datadog:type=Test,name=Foo");
        Configuration config1 = createConfig(include1, null);

        Map<String, Object> include2 = new HashMap<String, Object>();
        include2.put("bean", "org.datadog:type=Test,name=Bar");
        Configuration config2 = createConfig(include2, null);

        List<Configuration> configList = new ArrayList<Configuration>();
        configList.add(config1);
        configList.add(config2);
        List<String> scopes = Configuration.getGreatestCommonScopes(configList);
        assertNotNull(scopes);
    }

    // === Negative Tests ===

    @Test
    public void testConfigWithNullInclude() {
        Configuration config = createConfig(null, null);
        assertNotNull(config.getInclude());
        assertNotNull(config.getExclude());
    }

    @Test
    public void testGetGreatestCommonScopesNoInclude() {
        Map<String, Object> conf = new HashMap<String, Object>();
        Configuration config = new Configuration(conf);
        List<Configuration> configList = new ArrayList<Configuration>();
        configList.add(config);
        List<String> scopes = Configuration.getGreatestCommonScopes(configList);
        assertNotNull(scopes);
        assertTrue(scopes.isEmpty());
    }
}
