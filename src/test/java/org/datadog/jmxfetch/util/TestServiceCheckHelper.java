package org.datadog.jmxfetch.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestServiceCheckHelper {

    @Test
    public void testFormatLowercaseOnly() {
        // lowercase letters are kept, underscores are stripped
        String result = ServiceCheckHelper
                .formatServiceCheckPrefix("mycheck");
        assertEquals("mycheck", result);
    }

    @Test
    public void testFormatRemovesUnderscoresFromFirstChunk() {
        // underscores match [A-Z0-9:_\-] and are removed
        String result = ServiceCheckHelper
                .formatServiceCheckPrefix("my_check");
        assertEquals("mycheck", result);
    }

    @Test
    public void testFormatRemovesDashesFromFirstChunk() {
        // hyphens match [A-Z0-9:_\-] and are removed
        String result = ServiceCheckHelper
                .formatServiceCheckPrefix("my-check-name");
        assertEquals("mycheckname", result);
    }

    @Test
    public void testFormatWithDots() {
        // Only first chunk is transformed; subsequent chunks
        // are untouched
        String result = ServiceCheckHelper
                .formatServiceCheckPrefix("my.check.name");
        assertEquals("my.check.name", result);
    }

    @Test
    public void testFormatRemovesUppercaseFromFirstChunk() {
        // Uppercase letters are stripped from first chunk
        // A,M,Q removed; _,5,8 removed => "ctive"
        String result = ServiceCheckHelper
                .formatServiceCheckPrefix("ActiveMQ_58");
        assertEquals("ctive", result);
    }

    @Test
    public void testFormatActivemqUnderscoreNum() {
        // Realistic case: activemq_58 -> activemq
        String result = ServiceCheckHelper
                .formatServiceCheckPrefix("activemq_58");
        assertEquals("activemq", result);
    }

    @Test
    public void testFormatEmpty() {
        String result = ServiceCheckHelper
                .formatServiceCheckPrefix("");
        assertEquals("", result);
    }

    @Test
    public void testFormatWithDotsAndUppercase() {
        // First chunk: "My" -> remove M -> "y"
        // Second chunk: "Check" stays as-is (not first chunk)
        String result = ServiceCheckHelper
                .formatServiceCheckPrefix("My.Check");
        assertEquals("y.Check", result);
    }
}
