package org.datadog.jmxfetch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class TestDynamicTag {

    // === Positive Tests ===

    @Test
    public void testParseValidConfig() {
        Map<String, Object> config = new HashMap<String, Object>();
        config.put("tag_name", "cluster_id");
        config.put("bean_name", "kafka.server:type=KafkaServer,name=ClusterId");
        config.put("attribute", "Value");

        DynamicTag tag = DynamicTag.parse(config);
        assertNotNull(tag);
        assertEquals("cluster_id", tag.getTagName());
        assertEquals("kafka.server:type=KafkaServer,name=ClusterId", tag.getBeanName());
        assertEquals("Value", tag.getAttributeName());
    }

    @Test
    public void testGetBeanAttributeKey() {
        Map<String, Object> config = new HashMap<String, Object>();
        config.put("tag_name", "cluster_id");
        config.put("bean_name", "kafka.server:type=KafkaServer");
        config.put("attribute", "Value");

        DynamicTag tag = DynamicTag.parse(config);
        assertNotNull(tag);
        assertEquals("kafka.server:type=KafkaServer#Value", tag.getBeanAttributeKey());
    }

    @Test
    public void testToString() {
        Map<String, Object> config = new HashMap<String, Object>();
        config.put("tag_name", "test_tag");
        config.put("bean_name", "test:type=Bean");
        config.put("attribute", "Attr");

        DynamicTag tag = DynamicTag.parse(config);
        assertNotNull(tag);
        String str = tag.toString();
        assertTrue(str.contains("test_tag"));
        assertTrue(str.contains("test:type=Bean"));
        assertTrue(str.contains("Attr"));
    }

    // === Negative Tests ===

    @Test
    public void testParseNull() {
        assertNull(DynamicTag.parse(null));
    }

    @Test
    public void testParseNonMap() {
        assertNull(DynamicTag.parse("not a map"));
    }

    @Test
    public void testParseMissingTagName() {
        Map<String, Object> config = new HashMap<String, Object>();
        config.put("bean_name", "test:type=Bean");
        config.put("attribute", "Value");

        assertNull(DynamicTag.parse(config));
    }

    @Test
    public void testParseMissingBeanName() {
        Map<String, Object> config = new HashMap<String, Object>();
        config.put("tag_name", "test");
        config.put("attribute", "Value");

        assertNull(DynamicTag.parse(config));
    }

    @Test
    public void testParseMissingAttribute() {
        Map<String, Object> config = new HashMap<String, Object>();
        config.put("tag_name", "test");
        config.put("bean_name", "test:type=Bean");

        assertNull(DynamicTag.parse(config));
    }

    @Test
    public void testParseMissingAllFields() {
        Map<String, Object> config = new HashMap<String, Object>();
        assertNull(DynamicTag.parse(config));
    }

    @Test
    public void testParseEmptyMap() {
        assertNull(DynamicTag.parse(new HashMap<String, Object>()));
    }

    // === Boundary Tests ===

    @Test
    public void testParseWithIntegerValues() {
        Map<String, Object> config = new HashMap<String, Object>();
        config.put("tag_name", Integer.valueOf(123));
        config.put("bean_name", "test:type=Bean");
        config.put("attribute", "Value");

        DynamicTag tag = DynamicTag.parse(config);
        assertNotNull(tag);
        assertEquals("123", tag.getTagName());
    }

    @Test
    public void testParseWithEmptyStrings() {
        Map<String, Object> config = new HashMap<String, Object>();
        config.put("tag_name", "");
        config.put("bean_name", "");
        config.put("attribute", "");

        DynamicTag tag = DynamicTag.parse(config);
        assertNotNull(tag);
        assertEquals("", tag.getTagName());
    }
}
