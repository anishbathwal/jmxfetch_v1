package org.datadog.jmxfetch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class ConfigurationTest {

    // === Positive tests ===

    @Test
    public void testBasicConfiguration() {
        Map<String, Object> include =
                new HashMap<String, Object>();
        include.put("domain", "org.datadog");
        Map<String, Object> conf = new HashMap<String, Object>();
        conf.put("include", include);
        conf.put("exclude", null);

        Configuration config = new Configuration(conf);
        assertNotNull(config.getInclude());
        assertNotNull(config.getExclude());
        assertEquals(
                "org.datadog", config.getInclude().getDomain());
    }

    @Test
    public void testConfigurationWithBothFilters() {
        Map<String, Object> include =
                new HashMap<String, Object>();
        include.put("domain", "org.datadog");
        Map<String, Object> exclude =
                new HashMap<String, Object>();
        exclude.put("bean", "org.datadog:type=Excluded");

        Map<String, Object> conf = new HashMap<String, Object>();
        conf.put("include", include);
        conf.put("exclude", exclude);

        Configuration config = new Configuration(conf);
        assertFalse(config.getInclude().isEmptyFilter());
        assertFalse(config.getExclude().isEmptyFilter());
    }

    @Test
    public void testToString() {
        Map<String, Object> conf = new HashMap<String, Object>();
        conf.put("include", null);
        conf.put("exclude", null);
        Configuration config = new Configuration(conf);
        String result = config.toString();
        assertTrue(result.contains("include:"));
        assertTrue(result.contains("exclude:"));
    }

    @Test
    public void testGetConf() {
        Map<String, Object> conf = new HashMap<String, Object>();
        conf.put("include", null);
        conf.put("exclude", null);
        Configuration config = new Configuration(conf);
        assertNotNull(config.getConf());
        assertTrue(config.getConf().containsKey("include"));
    }

    // === Dynamic tags tests ===

    @Test
    public void testDynamicTagsParsing() {
        Map<String, Object> tagConfig =
                new HashMap<String, Object>();
        tagConfig.put("tag_name", "cluster_id");
        tagConfig.put("bean_name",
                "kafka.server:type=KafkaServer,name=ClusterId");
        tagConfig.put("attribute", "Value");

        List<Object> dynamicTagsList = new ArrayList<Object>();
        dynamicTagsList.add(tagConfig);

        Map<String, Object> conf = new HashMap<String, Object>();
        conf.put("include", null);
        conf.put("exclude", null);
        conf.put("dynamic_tags", dynamicTagsList);

        Configuration config = new Configuration(conf);
        List<DynamicTag> tags = config.getDynamicTags();
        assertEquals(1, tags.size());
        assertEquals("cluster_id", tags.get(0).getTagName());
    }

    @Test
    public void testDynamicTagsWithMultipleTags() {
        Map<String, Object> tag1 = new HashMap<String, Object>();
        tag1.put("tag_name", "tag1");
        tag1.put("bean_name", "org:type=T1");
        tag1.put("attribute", "Attr1");

        Map<String, Object> tag2 = new HashMap<String, Object>();
        tag2.put("tag_name", "tag2");
        tag2.put("bean_name", "org:type=T2");
        tag2.put("attribute", "Attr2");

        List<Object> dynamicTagsList = new ArrayList<Object>();
        dynamicTagsList.add(tag1);
        dynamicTagsList.add(tag2);

        Map<String, Object> conf = new HashMap<String, Object>();
        conf.put("include", null);
        conf.put("exclude", null);
        conf.put("dynamic_tags", dynamicTagsList);

        Configuration config = new Configuration(conf);
        assertEquals(2, config.getDynamicTags().size());
    }

    @Test
    public void testDynamicTagsNullConfig() {
        Map<String, Object> conf = new HashMap<String, Object>();
        conf.put("include", null);
        conf.put("exclude", null);
        // no dynamic_tags key

        Configuration config = new Configuration(conf);
        assertNotNull(config.getDynamicTags());
        assertTrue(config.getDynamicTags().isEmpty());
    }

    @Test
    public void testDynamicTagsInvalidType() {
        Map<String, Object> conf = new HashMap<String, Object>();
        conf.put("include", null);
        conf.put("exclude", null);
        conf.put("dynamic_tags", "not a list");

        Configuration config = new Configuration(conf);
        assertNotNull(config.getDynamicTags());
        assertTrue(config.getDynamicTags().isEmpty());
    }

    @Test
    public void testDynamicTagsWithInvalidEntry() {
        List<Object> dynamicTagsList = new ArrayList<Object>();
        dynamicTagsList.add(null);
        dynamicTagsList.add("invalid");

        Map<String, Object> conf = new HashMap<String, Object>();
        conf.put("include", null);
        conf.put("exclude", null);
        conf.put("dynamic_tags", dynamicTagsList);

        Configuration config = new Configuration(conf);
        // Both null and invalid string entries are skipped
        assertTrue(config.getDynamicTags().isEmpty());
    }

    // === getGreatestCommonScopes tests ===

    @Test
    public void testGetGreatestCommonScopesEmptyList() {
        List<Configuration> configs = Collections.emptyList();
        List<String> scopes =
                Configuration.getGreatestCommonScopes(configs);
        assertNotNull(scopes);
        assertTrue(scopes.isEmpty());
    }

    @Test
    public void testGetGreatestCommonScopesNoIncludes() {
        Map<String, Object> conf = new HashMap<String, Object>();
        conf.put("include", null);
        conf.put("exclude", null);
        Configuration config = new Configuration(conf);

        List<String> scopes =
                Configuration.getGreatestCommonScopes(
                        Collections.singletonList(config));
        assertTrue(scopes.isEmpty());
    }

    @Test
    public void testGetGreatestCommonScopesSingleConfig() {
        Map<String, Object> include =
                new HashMap<String, Object>();
        include.put("domain", "org.datadog");
        include.put("type", "TestType");

        Map<String, Object> conf = new HashMap<String, Object>();
        conf.put("include", include);
        conf.put("exclude", null);

        Configuration config = new Configuration(conf);
        List<String> scopes =
                Configuration.getGreatestCommonScopes(
                        Collections.singletonList(config));
        assertNotNull(scopes);
        assertFalse(scopes.isEmpty());
    }

    @Test
    public void testGetGreatestCommonScopesWithBeanName() {
        Map<String, Object> include =
                new HashMap<String, Object>();
        include.put("bean",
                "org.datadog:type=TestType,name=TestName");

        Map<String, Object> conf = new HashMap<String, Object>();
        conf.put("include", include);
        conf.put("exclude", null);

        Configuration config = new Configuration(conf);
        List<String> scopes =
                Configuration.getGreatestCommonScopes(
                        Collections.singletonList(config));
        assertNotNull(scopes);
    }
}
