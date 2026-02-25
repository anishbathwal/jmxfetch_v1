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

    private Map<String, Object> buildConf(
            Map<String, Object> include, Map<String, Object> exclude) {
        Map<String, Object> conf = new HashMap<String, Object>();
        if (include != null) {
            conf.put("include", include);
        }
        if (exclude != null) {
            conf.put("exclude", exclude);
        }
        return conf;
    }

    @Test
    public void testBasicConstructor() {
        Map<String, Object> include = new HashMap<String, Object>();
        include.put("domain", "org.datadog.test");
        Map<String, Object> conf = buildConf(include, null);

        Configuration config = new Configuration(conf);
        assertNotNull(config.getInclude());
        assertNotNull(config.getExclude());
        assertEquals(
                "org.datadog.test",
                config.getInclude().getDomain());
    }

    @Test
    public void testConstructorWithExclude() {
        Map<String, Object> include = new HashMap<String, Object>();
        include.put("domain", "org.datadog.test");
        Map<String, Object> exclude = new HashMap<String, Object>();
        exclude.put("bean", "excluded.bean:type=X");
        Map<String, Object> conf = buildConf(include, exclude);

        Configuration config = new Configuration(conf);
        assertNotNull(config.getExclude());
        assertFalse(config.getExclude().isEmptyBeanName());
    }

    @Test
    public void testConstructorWithNullIncludeExclude() {
        Map<String, Object> conf = new HashMap<String, Object>();
        Configuration config = new Configuration(conf);
        assertNotNull(config.getInclude());
        assertNotNull(config.getExclude());
        assertTrue(config.getInclude().isEmptyFilter());
        assertTrue(config.getExclude().isEmptyFilter());
    }

    @Test
    public void testGetConf() {
        Map<String, Object> include = new HashMap<String, Object>();
        include.put("domain", "test");
        Map<String, Object> conf = buildConf(include, null);
        Configuration config = new Configuration(conf);
        assertNotNull(config.getConf());
        assertTrue(config.getConf().containsKey("include"));
    }

    @Test
    public void testToString() {
        Map<String, Object> include = new HashMap<String, Object>();
        include.put("domain", "test");
        Map<String, Object> conf = buildConf(include, null);
        Configuration config = new Configuration(conf);
        String str = config.toString();
        assertNotNull(str);
        assertTrue(str.contains("include"));
        assertTrue(str.contains("exclude"));
    }

    @Test
    public void testGetDynamicTagsEmpty() {
        Map<String, Object> conf = buildConf(
                new HashMap<String, Object>(), null);
        Configuration config = new Configuration(conf);
        assertNotNull(config.getDynamicTags());
        assertTrue(config.getDynamicTags().isEmpty());
    }

    @Test
    public void testGetDynamicTagsWithValidTags() {
        Map<String, Object> tagConfig = new HashMap<String, Object>();
        tagConfig.put("tag_name", "cluster_id");
        tagConfig.put("bean_name", "kafka.server:type=KafkaServer");
        tagConfig.put("attribute", "Value");

        List<Object> dynamicTags = new ArrayList<Object>();
        dynamicTags.add(tagConfig);

        Map<String, Object> conf = new HashMap<String, Object>();
        conf.put("include", new HashMap<String, Object>());
        conf.put("dynamic_tags", dynamicTags);

        Configuration config = new Configuration(conf);
        assertEquals(1, config.getDynamicTags().size());
        assertEquals(
                "cluster_id",
                config.getDynamicTags().get(0).getTagName());
    }

    @Test
    public void testParseDynamicTagsNonListIgnored() {
        Map<String, Object> conf = new HashMap<String, Object>();
        conf.put("include", new HashMap<String, Object>());
        conf.put("dynamic_tags", "not a list");

        Configuration config = new Configuration(conf);
        assertNotNull(config.getDynamicTags());
        assertTrue(config.getDynamicTags().isEmpty());
    }

    @Test
    public void testParseDynamicTagsWithInvalidEntry() {
        List<Object> dynamicTags = new ArrayList<Object>();
        dynamicTags.add(null);
        dynamicTags.add("not a map");

        Map<String, Object> conf = new HashMap<String, Object>();
        conf.put("include", new HashMap<String, Object>());
        conf.put("dynamic_tags", dynamicTags);

        Configuration config = new Configuration(conf);
        assertTrue(config.getDynamicTags().isEmpty());
    }

    @Test
    public void testGetGreatestCommonScopesEmpty() {
        List<Configuration> emptyList =
                new ArrayList<Configuration>();
        List<String> scopes =
                Configuration.getGreatestCommonScopes(emptyList);
        assertNotNull(scopes);
        assertTrue(scopes.isEmpty());
    }

    @Test
    public void testGetGreatestCommonScopesSingleConfig() {
        Map<String, Object> include = new HashMap<String, Object>();
        include.put("domain", "org.test");
        include.put("type", "MyType");
        Map<String, Object> conf = buildConf(include, null);
        Configuration config = new Configuration(conf);

        List<Configuration> configList =
                new ArrayList<Configuration>();
        configList.add(config);
        List<String> scopes =
                Configuration.getGreatestCommonScopes(configList);
        assertNotNull(scopes);
        assertFalse(scopes.isEmpty());
    }

    @Test
    public void testGetGreatestCommonScopesNoInclude() {
        // Configuration without include should be filtered out
        Map<String, Object> conf = new HashMap<String, Object>();
        Configuration config = new Configuration(conf);

        List<Configuration> configList =
                new ArrayList<Configuration>();
        configList.add(config);
        List<String> scopes =
                Configuration.getGreatestCommonScopes(configList);
        assertNotNull(scopes);
        assertTrue(scopes.isEmpty());
    }

    @Test
    public void testGetGreatestCommonScopesWithBeanNames() {
        Map<String, Object> include = new HashMap<String, Object>();
        include.put(
                "bean",
                Arrays.asList(
                        "org.test:type=A,name=X",
                        "org.test:type=B,name=Y"));
        Map<String, Object> conf = buildConf(include, null);
        Configuration config = new Configuration(conf);

        List<Configuration> configList =
                new ArrayList<Configuration>();
        configList.add(config);
        List<String> scopes =
                Configuration.getGreatestCommonScopes(configList);
        assertNotNull(scopes);
        // Should produce scope(s) based on common domain
        assertFalse(scopes.isEmpty());
    }

    @Test
    public void testGetGreatestCommonScopesMultipleConfigs() {
        Map<String, Object> include1 = new HashMap<String, Object>();
        include1.put("domain", "org.test");
        include1.put("type", "MyType");
        Map<String, Object> conf1 = buildConf(include1, null);

        Map<String, Object> include2 = new HashMap<String, Object>();
        include2.put("domain", "org.test");
        include2.put("type", "OtherType");
        Map<String, Object> conf2 = buildConf(include2, null);

        List<Configuration> configList =
                new ArrayList<Configuration>();
        configList.add(new Configuration(conf1));
        configList.add(new Configuration(conf2));
        List<String> scopes =
                Configuration.getGreatestCommonScopes(configList);
        assertNotNull(scopes);
    }
}
