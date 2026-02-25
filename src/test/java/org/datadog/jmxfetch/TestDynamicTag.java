package org.datadog.jmxfetch;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class TestDynamicTag {

    // === Positive Tests ===

    @Test
    public void testParseValidConfig() {
        Map<String, Object> config = new HashMap<String, Object>();
        config.put("tag_name", "cluster_id");
        config.put("bean_name", "kafka.server:type=KafkaServer");
        config.put("attribute", "ClusterId");

        DynamicTag tag = DynamicTag.parse(config);
        assertNotNull(tag);
        assertEquals("cluster_id", tag.getTagName());
        assertEquals("kafka.server:type=KafkaServer", tag.getBeanName());
        assertEquals("ClusterId", tag.getAttributeName());
    }

    @Test
    public void testGetBeanAttributeKey() {
        Map<String, Object> config = new HashMap<String, Object>();
        config.put("tag_name", "version");
        config.put("bean_name", "com.example:type=Info");
        config.put("attribute", "Version");

        DynamicTag tag = DynamicTag.parse(config);
        assertNotNull(tag);
        assertEquals("com.example:type=Info#Version", tag.getBeanAttributeKey());
    }

    @Test
    public void testToString() {
        Map<String, Object> config = new HashMap<String, Object>();
        config.put("tag_name", "env");
        config.put("bean_name", "com.example:type=Config");
        config.put("attribute", "Environment");

        DynamicTag tag = DynamicTag.parse(config);
        assertNotNull(tag);
        String str = tag.toString();
        assertTrue(str.contains("env"));
        assertTrue(str.contains("com.example:type=Config"));
        assertTrue(str.contains("Environment"));
    }

    // === Negative Tests ===

    @Test
    public void testParseNull() {
        DynamicTag tag = DynamicTag.parse(null);
        assertNull(tag);
    }

    @Test
    public void testParseNonMapObject() {
        DynamicTag tag = DynamicTag.parse("not_a_map");
        assertNull(tag);
    }

    @Test
    public void testParseIntegerObject() {
        DynamicTag tag = DynamicTag.parse(42);
        assertNull(tag);
    }

    @Test
    public void testParseMissingTagName() {
        Map<String, Object> config = new HashMap<String, Object>();
        config.put("bean_name", "com.example:type=Info");
        config.put("attribute", "Version");

        DynamicTag tag = DynamicTag.parse(config);
        assertNull(tag);
    }

    @Test
    public void testParseMissingBeanName() {
        Map<String, Object> config = new HashMap<String, Object>();
        config.put("tag_name", "version");
        config.put("attribute", "Version");

        DynamicTag tag = DynamicTag.parse(config);
        assertNull(tag);
    }

    @Test
    public void testParseMissingAttribute() {
        Map<String, Object> config = new HashMap<String, Object>();
        config.put("tag_name", "version");
        config.put("bean_name", "com.example:type=Info");

        DynamicTag tag = DynamicTag.parse(config);
        assertNull(tag);
    }

    @Test
    public void testParseMissingAllRequired() {
        Map<String, Object> config = new HashMap<String, Object>();
        DynamicTag tag = DynamicTag.parse(config);
        assertNull(tag);
    }

    @Test
    public void testParseMissingTagNameAndAttribute() {
        Map<String, Object> config = new HashMap<String, Object>();
        config.put("bean_name", "com.example:type=Info");

        DynamicTag tag = DynamicTag.parse(config);
        assertNull(tag);
    }

    // === Boundary Tests ===

    @Test
    public void testParseEmptyStrings() {
        Map<String, Object> config = new HashMap<String, Object>();
        config.put("tag_name", "");
        config.put("bean_name", "");
        config.put("attribute", "");

        DynamicTag tag = DynamicTag.parse(config);
        assertNotNull(tag);
        assertEquals("", tag.getTagName());
    }

    @Test
    public void testParseNonStringValues() {
        Map<String, Object> config = new HashMap<String, Object>();
        config.put("tag_name", 123);
        config.put("bean_name", true);
        config.put("attribute", 45.6);

        DynamicTag tag = DynamicTag.parse(config);
        assertNotNull(tag);
        assertEquals("123", tag.getTagName());
        assertEquals("true", tag.getBeanName());
        assertEquals("45.6", tag.getAttributeName());
    }

    @Test
    public void testParseSpecialCharacters() {
        Map<String, Object> config = new HashMap<String, Object>();
        config.put("tag_name", "my-tag_name.v1");
        config.put("bean_name", "org.apache.cassandra.metrics:type=ColumnFamily,keyspace=system,scope=peers,name=LiveDiskSpaceUsed");
        config.put("attribute", "Value");

        DynamicTag tag = DynamicTag.parse(config);
        assertNotNull(tag);
        assertEquals("my-tag_name.v1", tag.getTagName());
    }

    @Test
    public void testBeanAttributeKeyUniqueness() {
        Map<String, Object> config1 = new HashMap<String, Object>();
        config1.put("tag_name", "tag1");
        config1.put("bean_name", "bean1");
        config1.put("attribute", "attr1");

        Map<String, Object> config2 = new HashMap<String, Object>();
        config2.put("tag_name", "tag2");
        config2.put("bean_name", "bean1");
        config2.put("attribute", "attr2");

        DynamicTag tag1 = DynamicTag.parse(config1);
        DynamicTag tag2 = DynamicTag.parse(config2);

        assertNotNull(tag1);
        assertNotNull(tag2);
        assertTrue(!tag1.getBeanAttributeKey().equals(tag2.getBeanAttributeKey()));
    }

    @Test
    public void testBeanAttributeKeySameForSameConfig() {
        Map<String, Object> config1 = new HashMap<String, Object>();
        config1.put("tag_name", "tag1");
        config1.put("bean_name", "bean1");
        config1.put("attribute", "attr1");

        Map<String, Object> config2 = new HashMap<String, Object>();
        config2.put("tag_name", "tag2");
        config2.put("bean_name", "bean1");
        config2.put("attribute", "attr1");

        DynamicTag tag1 = DynamicTag.parse(config1);
        DynamicTag tag2 = DynamicTag.parse(config2);

        assertNotNull(tag1);
        assertNotNull(tag2);
        assertEquals(tag1.getBeanAttributeKey(), tag2.getBeanAttributeKey());
    }
}
