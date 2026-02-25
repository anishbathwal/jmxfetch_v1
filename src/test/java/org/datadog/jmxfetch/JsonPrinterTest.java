package org.datadog.jmxfetch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class JsonPrinterTest {

    // === Positive tests ===

    @Test
    public void testPrintNull() {
        String result = JsonPrinter.toString(null);
        assertEquals("null", result);
    }

    @Test
    public void testPrintBoolean() {
        assertEquals("true", JsonPrinter.toString(true));
        assertEquals("false", JsonPrinter.toString(false));
    }

    @Test
    public void testPrintInteger() {
        assertEquals("42", JsonPrinter.toString(42));
    }

    @Test
    public void testPrintLong() {
        assertEquals("9999999999",
                JsonPrinter.toString(9999999999L));
    }

    @Test
    public void testPrintDouble() {
        assertEquals("3.14", JsonPrinter.toString(3.14));
    }

    @Test
    public void testPrintString() {
        assertEquals("\"hello\"",
                JsonPrinter.toString("hello"));
    }

    @Test
    public void testPrintStringWithEscapes() {
        String result = JsonPrinter.toString("line1\nline2");
        assertTrue(result.contains("\\n"));
    }

    @Test
    public void testPrintStringWithQuotes() {
        String result = JsonPrinter.toString("say \"hi\"");
        assertTrue(result.contains("\\\""));
    }

    @Test
    public void testPrintStringWithBackslash() {
        String result = JsonPrinter.toString("path\\file");
        assertTrue(result.contains("\\\\"));
    }

    @Test
    public void testPrintStringWithTab() {
        String result = JsonPrinter.toString("col1\tcol2");
        assertTrue(result.contains("\\t"));
    }

    @Test
    public void testPrintStringWithCarriageReturn() {
        String result = JsonPrinter.toString("line\r");
        assertTrue(result.contains("\\r"));
    }

    @Test
    public void testPrintList() {
        List<Object> list = new ArrayList<Object>();
        list.add(1);
        list.add(2);
        list.add(3);
        String result = JsonPrinter.toString(list);
        assertTrue(result.contains("1"));
        assertTrue(result.contains("2"));
        assertTrue(result.contains("3"));
    }

    @Test
    public void testPrintEmptyList() {
        List<Object> list = new ArrayList<Object>();
        String result = JsonPrinter.toString(list);
        assertNotNull(result);
    }

    @Test
    public void testPrintMap() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("key", "value");
        String result = JsonPrinter.toString(map);
        assertTrue(result.contains("\"key\""));
        assertTrue(result.contains("\"value\""));
    }

    @Test
    public void testPrintEmptyMap() {
        Map<String, Object> map = new HashMap<String, Object>();
        String result = JsonPrinter.toString(map);
        assertNotNull(result);
    }

    @Test
    public void testPrintStringArray() {
        String[] arr = new String[]{"a", "b", "c"};
        String result = JsonPrinter.toString(arr);
        assertTrue(result.contains("\"a\""));
        assertTrue(result.contains("\"b\""));
        assertTrue(result.contains("\"c\""));
    }

    @Test
    public void testPrintNestedMap() {
        Map<String, Object> inner =
                new HashMap<String, Object>();
        inner.put("nested", "val");
        Map<String, Object> outer =
                new HashMap<String, Object>();
        outer.put("inner", inner);
        String result = JsonPrinter.toString(outer);
        assertTrue(result.contains("\"nested\""));
        assertTrue(result.contains("\"val\""));
    }

    // === Boundary tests ===

    @Test
    public void testPrintNanDouble() {
        String result = JsonPrinter.toString(Double.NaN);
        assertEquals("\"NaN\"", result);
    }

    @Test
    public void testPrintPositiveInfinity() {
        String result =
                JsonPrinter.toString(Double.POSITIVE_INFINITY);
        assertEquals("\"Inf\"", result);
    }

    @Test
    public void testPrintNegativeInfinity() {
        String result =
                JsonPrinter.toString(Double.NEGATIVE_INFINITY);
        assertEquals("\"-Inf\"", result);
    }

    @Test
    public void testPrintZero() {
        assertEquals("0", JsonPrinter.toString(0));
    }

    @Test
    public void testPrintNegativeInteger() {
        assertEquals("-1", JsonPrinter.toString(-1));
    }

    @Test
    public void testPrintEmptyString() {
        assertEquals("\"\"", JsonPrinter.toString(""));
    }

    @Test
    public void testPrintByte() {
        assertEquals("1", JsonPrinter.toString((byte) 1));
    }

    @Test
    public void testPrintShort() {
        assertEquals("100",
                JsonPrinter.toString((short) 100));
    }

    @Test
    public void testPrintControlCharInString() {
        // Control character < 32 should be encoded
        String input = "test" + (char) 1 + "end";
        String result = JsonPrinter.toString(input);
        assertTrue(result.contains("\\u0001"));
    }

    @Test
    public void testPrintStringWithFormFeed() {
        String result = JsonPrinter.toString("test\fend");
        assertTrue(result.contains("\\f"));
    }

    @Test
    public void testPrintStringWithBackspace() {
        String result = JsonPrinter.toString("test\bend");
        assertTrue(result.contains("\\b"));
    }

    // === Writer variant test ===

    @Test
    public void testPrettyPrintToWriter() {
        StringWriter sw = new StringWriter();
        JsonPrinter.prettyPrint(sw, "test");
        assertEquals("\"test\"", sw.toString());
    }

    // === Error handling ===

    @Test(expected = RuntimeException.class)
    public void testUnsupportedTypeThrows() {
        JsonPrinter.toString(new Object());
    }

    @Test(expected = RuntimeException.class)
    public void testNonStringMapKeyThrows() {
        Map<Integer, Object> map =
                new HashMap<Integer, Object>();
        map.put(1, "value");
        JsonPrinter.toString(map);
    }
}
