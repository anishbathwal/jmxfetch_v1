package org.datadog.jmxfetch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class TestDynamicTag {

    @Test
    public void testParseValidConfig() {
        Map<String, Object> config = new HashMap<String, Object>();
        config.put("tag_name", "cluster_id");
        config.put("bean_name", "kafka.server:type=KafkaServer");
        config.put("attribute", "Value");

        DynamicTag tag = DynamicTag.parse(config);
        assertNotNull(tag);
        assertEquals("cluster_id", tag.getTagName());
        assertEquals("kafka.server:type=KafkaServer", tag.getBeanName());
        assertEquals("Value", tag.getAttributeName());
    }

    @Test
    public void testParseNullConfig() {
        assertNull(DynamicTag.parse(null));
    }

    @Test
    public void testParseNonMapConfig() {
        assertNull(DynamicTag.parse("not a map"));
    }

    @Test
    public void testParseMissingTagName() {
        Map<String, Object> config = new HashMap<String, Object>();
        config.put("bean_name", "some.bean:type=Test");
        config.put("attribute", "Value");

        assertNull(DynamicTag.parse(config));
    }

    @Test
    public void testParseMissingBeanName() {
        Map<String, Object> config = new HashMap<String, Object>();
        config.put("tag_name", "my_tag");
        config.put("attribute", "Value");

        assertNull(DynamicTag.parse(config));
    }

    @Test
    public void testParseMissingAttribute() {
        Map<String, Object> config = new HashMap<String, Object>();
        config.put("tag_name", "my_tag");
        config.put("bean_name", "some.bean:type=Test");

        assertNull(DynamicTag.parse(config));
    }

    @Test
    public void testParseEmptyMap() {
        Map<String, Object> config = new HashMap<String, Object>();
        assertNull(DynamicTag.parse(config));
    }

    @Test
    public void testGetBeanAttributeKey() {
        Map<String, Object> config = new HashMap<String, Object>();
        config.put("tag_name", "cluster_id");
        config.put("bean_name", "kafka.server:type=KafkaServer");
        config.put("attribute", "Value");

        DynamicTag tag = DynamicTag.parse(config);
        assertNotNull(tag);
        assertEquals(
                "kafka.server:type=KafkaServer#Value",
                tag.getBeanAttributeKey());
    }

    @Test
    public void testToString() {
        Map<String, Object> config = new HashMap<String, Object>();
        config.put("tag_name", "cluster_id");
        config.put("bean_name", "kafka.server:type=KafkaServer");
        config.put("attribute", "Value");

        DynamicTag tag = DynamicTag.parse(config);
        assertNotNull(tag);
        String str = tag.toString();
        assertNotNull(str);
        assertEquals(
                "DynamicTag{name='cluster_id', "
                        + "bean='kafka.server:type=KafkaServer', "
                        + "attribute='Value'}",
                str);
    }

    @Test
    public void testParseWithIntegerValues() {
        // Values can be non-String objects; parse should call toString()
        Map<String, Object> config = new HashMap<String, Object>();
        config.put("tag_name", Integer.valueOf(42));
        config.put("bean_name", "some.bean:type=Test");
        config.put("attribute", "Attr");

        DynamicTag tag = DynamicTag.parse(config);
        assertNotNull(tag);
        assertEquals("42", tag.getTagName());
    }

    @Test
    public void testResolveWithMalformedBeanName() {
        Map<String, Object> config = new HashMap<String, Object>();
        config.put("tag_name", "my_tag");
        config.put("bean_name", ":::malformed");
        config.put("attribute", "Value");

        DynamicTag tag = DynamicTag.parse(config);
        assertNotNull(tag);
        // resolve requires a Connection; with a malformed bean name
        // it should return null due to MalformedObjectNameException
        assertNull(tag.resolve(null));
    }
}
