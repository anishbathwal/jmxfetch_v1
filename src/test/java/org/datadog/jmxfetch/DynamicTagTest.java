package org.datadog.jmxfetch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class DynamicTagTest {

    // === Positive tests ===

    @Test
    public void testParseValidConfig() {
        Map<String, Object> config = new HashMap<String, Object>();
        config.put("tag_name", "cluster_id");
        config.put("bean_name",
                "kafka.server:type=KafkaServer,name=ClusterId");
        config.put("attribute", "Value");

        DynamicTag tag = DynamicTag.parse(config);
        assertNotNull(tag);
        assertEquals("cluster_id", tag.getTagName());
        assertEquals(
                "kafka.server:type=KafkaServer,name=ClusterId",
                tag.getBeanName());
        assertEquals("Value", tag.getAttributeName());
    }

    @Test
    public void testGetBeanAttributeKey() {
        Map<String, Object> config = new HashMap<String, Object>();
        config.put("tag_name", "my_tag");
        config.put("bean_name", "org:type=T");
        config.put("attribute", "Attr");

        DynamicTag tag = DynamicTag.parse(config);
        assertNotNull(tag);
        assertEquals("org:type=T#Attr", tag.getBeanAttributeKey());
    }

    @Test
    public void testToStringFormat() {
        Map<String, Object> config = new HashMap<String, Object>();
        config.put("tag_name", "my_tag");
        config.put("bean_name", "org:type=T");
        config.put("attribute", "Attr");

        DynamicTag tag = DynamicTag.parse(config);
        assertNotNull(tag);
        String expected = "DynamicTag{name='my_tag',"
                + " bean='org:type=T', attribute='Attr'}";
        assertEquals(expected, tag.toString());
    }

    // === Negative tests ===

    @Test
    public void testParseNullReturnsNull() {
        assertNull(DynamicTag.parse(null));
    }

    @Test
    public void testParseNonMapReturnsNull() {
        assertNull(DynamicTag.parse("not a map"));
    }

    @Test
    public void testParseIntegerReturnsNull() {
        assertNull(DynamicTag.parse(Integer.valueOf(42)));
    }

    @Test
    public void testParseMissingTagNameReturnsNull() {
        Map<String, Object> config = new HashMap<String, Object>();
        config.put("bean_name", "org:type=T");
        config.put("attribute", "Attr");
        assertNull(DynamicTag.parse(config));
    }

    @Test
    public void testParseMissingBeanNameReturnsNull() {
        Map<String, Object> config = new HashMap<String, Object>();
        config.put("tag_name", "my_tag");
        config.put("attribute", "Attr");
        assertNull(DynamicTag.parse(config));
    }

    @Test
    public void testParseMissingAttributeReturnsNull() {
        Map<String, Object> config = new HashMap<String, Object>();
        config.put("tag_name", "my_tag");
        config.put("bean_name", "org:type=T");
        assertNull(DynamicTag.parse(config));
    }

    @Test
    public void testParseEmptyMapReturnsNull() {
        Map<String, Object> config = new HashMap<String, Object>();
        assertNull(DynamicTag.parse(config));
    }

    @Test
    public void testParseMissingAllFieldsReturnsNull() {
        Map<String, Object> config = new HashMap<String, Object>();
        config.put("unrelated_key", "value");
        assertNull(DynamicTag.parse(config));
    }

    // === Boundary tests ===

    @Test
    public void testParseWithIntegerValues() {
        Map<String, Object> config = new HashMap<String, Object>();
        config.put("tag_name", Integer.valueOf(123));
        config.put("bean_name", Integer.valueOf(456));
        config.put("attribute", Integer.valueOf(789));

        DynamicTag tag = DynamicTag.parse(config);
        assertNotNull(tag);
        assertEquals("123", tag.getTagName());
        assertEquals("456", tag.getBeanName());
        assertEquals("789", tag.getAttributeName());
    }

    @Test
    public void testParseWithSpecialCharacters() {
        Map<String, Object> config = new HashMap<String, Object>();
        config.put("tag_name", "tag-with-dashes");
        config.put("bean_name",
                "org.apache:type=Special,name=Test/Value");
        config.put("attribute", "My.Attribute");

        DynamicTag tag = DynamicTag.parse(config);
        assertNotNull(tag);
        assertEquals("tag-with-dashes", tag.getTagName());
    }
}
