package org.datadog.jmxfetch;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestJsonPrinterExtended {

    private String print(Object obj) {
        return JsonPrinter.toString(obj);
    }

    // === Null and Boolean Tests ===

    @Test
    public void testPrintNull() {
        assertEquals("null", print(null));
    }

    @Test
    public void testPrintBooleanTrue() {
        assertEquals("true", print(Boolean.TRUE));
    }

    @Test
    public void testPrintBooleanFalse() {
        assertEquals("false", print(Boolean.FALSE));
    }

    // === Integer Type Tests ===

    @Test
    public void testPrintByte() {
        assertEquals("42", print((byte) 42));
    }

    @Test
    public void testPrintShort() {
        assertEquals("1000", print((short) 1000));
    }

    @Test
    public void testPrintInteger() {
        assertEquals("12345", print(12345));
    }

    @Test
    public void testPrintLong() {
        assertEquals("9999999999", print(9999999999L));
    }

    @Test
    public void testPrintNegativeInteger() {
        assertEquals("-42", print(-42));
    }

    @Test
    public void testPrintZero() {
        assertEquals("0", print(0));
    }

    @Test
    public void testPrintIntegerMaxValue() {
        assertEquals(String.valueOf((long) Integer.MAX_VALUE), print(Integer.MAX_VALUE));
    }

    @Test
    public void testPrintLongMaxValue() {
        assertEquals(String.valueOf(Long.MAX_VALUE), print(Long.MAX_VALUE));
    }

    // === Floating Point Tests ===

    @Test
    public void testPrintDouble() {
        assertEquals("3.14", print(3.14));
    }

    @Test
    public void testPrintFloat() {
        String result = print(3.14f);
        assertTrue(result.startsWith("3.14"));
    }

    @Test
    public void testPrintNaN() {
        assertEquals("\"NaN\"", print(Double.NaN));
    }

    @Test
    public void testPrintPositiveInfinity() {
        assertEquals("\"Inf\"", print(Double.POSITIVE_INFINITY));
    }

    @Test
    public void testPrintNegativeInfinity() {
        assertEquals("\"-Inf\"", print(Double.NEGATIVE_INFINITY));
    }

    @Test
    public void testPrintFloatNaN() {
        assertEquals("\"NaN\"", print(Float.NaN));
    }

    @Test
    public void testPrintDoubleZero() {
        assertEquals("0.0", print(0.0));
    }

    @Test
    public void testPrintNegativeDouble() {
        assertEquals("-1.5", print(-1.5));
    }

    // === String Tests ===

    @Test
    public void testPrintSimpleString() {
        assertEquals("\"hello\"", print("hello"));
    }

    @Test
    public void testPrintEmptyString() {
        assertEquals("\"\"", print(""));
    }

    @Test
    public void testPrintStringWithQuotes() {
        assertEquals("\"say \\\"hello\\\"\"", print("say \"hello\""));
    }

    @Test
    public void testPrintStringWithBackslash() {
        assertEquals("\"path\\\\to\\\\file\"", print("path\\to\\file"));
    }

    @Test
    public void testPrintStringWithNewline() {
        assertEquals("\"line1\\nline2\"", print("line1\nline2"));
    }

    @Test
    public void testPrintStringWithTab() {
        assertEquals("\"col1\\tcol2\"", print("col1\tcol2"));
    }

    @Test
    public void testPrintStringWithCarriageReturn() {
        assertEquals("\"a\\rb\"", print("a\rb"));
    }

    @Test
    public void testPrintStringWithBackspace() {
        assertEquals("\"a\\bb\"", print("a\bb"));
    }

    @Test
    public void testPrintStringWithFormFeed() {
        assertEquals("\"a\\fb\"", print("a\fb"));
    }

    @Test
    public void testPrintStringWithControlChar() {
        String result = print("a\u0001b");
        assertEquals("\"a\\u0001b\"", result);
    }

    // === List Tests ===

    @Test
    public void testPrintEmptyList() {
        assertEquals("[  ]", print(new ArrayList<Object>()));
    }

    @Test
    public void testPrintSingleItemList() {
        List<Object> list = new ArrayList<Object>();
        list.add("item");
        assertEquals("[ \"item\" ]", print(list));
    }

    @Test
    public void testPrintMultiItemList() {
        List<Object> list = Arrays.asList((Object) "a", "b", "c");
        assertEquals("[ \"a\", \"b\", \"c\" ]", print(list));
    }

    @Test
    public void testPrintMixedTypeList() {
        List<Object> list = new ArrayList<Object>();
        list.add("text");
        list.add(42);
        list.add(true);
        list.add(null);
        assertEquals("[ \"text\", 42, true, null ]", print(list));
    }

    @Test
    public void testPrintNestedList() {
        List<Object> inner = new ArrayList<Object>();
        inner.add(1);
        inner.add(2);
        List<Object> outer = new ArrayList<Object>();
        outer.add(inner);
        outer.add(3);
        assertEquals("[ [ 1, 2 ], 3 ]", print(outer));
    }

    // === Map Tests ===

    @Test
    public void testPrintEmptyMap() {
        String result = print(new HashMap<String, Object>());
        assertTrue(result.contains("{"));
        assertTrue(result.contains("}"));
    }

    @Test
    public void testPrintSingleEntryMap() {
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        map.put("key", "value");
        String result = print(map);
        assertTrue(result.contains("\"key\""));
        assertTrue(result.contains("\"value\""));
    }

    @Test
    public void testPrintNestedMap() {
        Map<String, Object> inner = new LinkedHashMap<String, Object>();
        inner.put("a", 1);
        Map<String, Object> outer = new LinkedHashMap<String, Object>();
        outer.put("nested", inner);
        String result = print(outer);
        assertTrue(result.contains("\"nested\""));
        assertTrue(result.contains("\"a\""));
        assertTrue(result.contains("1"));
    }

    @Test
    public void testPrintMapWithListValue() {
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        List<Object> list = Arrays.asList((Object) "x", "y");
        map.put("items", list);
        String result = print(map);
        assertTrue(result.contains("\"items\""));
        assertTrue(result.contains("\"x\""));
        assertTrue(result.contains("\"y\""));
    }

    @Test
    public void testPrintMapWithNullValue() {
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        map.put("key", null);
        String result = print(map);
        assertTrue(result.contains("null"));
    }

    // === String Array Tests ===

    @Test
    public void testPrintStringArray() {
        String[] arr = new String[]{"a", "b", "c"};
        String result = print(arr);
        assertEquals("[ \"a\", \"b\", \"c\" ]", result);
    }

    @Test
    public void testPrintEmptyStringArray() {
        String[] arr = new String[]{};
        String result = print(arr);
        assertEquals("[  ]", result);
    }

    // === Output Stream Tests ===

    @Test
    public void testPrettyPrintToOutputStream() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        JsonPrinter.prettyPrint(baos, "test");
        assertEquals("\"test\"", baos.toString());
    }

    @Test
    public void testPrettyPrintToWriter() {
        StringWriter sw = new StringWriter();
        JsonPrinter.prettyPrint(sw, 42);
        assertEquals("42", sw.toString());
    }

    @Test
    public void testPrettyPrintToPrintWriter() {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        JsonPrinter.prettyPrint(pw, true);
        pw.flush();
        assertEquals("true", sw.toString());
    }

    // === Error Handling Tests ===

    @Test(expected = RuntimeException.class)
    public void testPrintUnsupportedType() {
        print(new Object());
    }

    @Test(expected = RuntimeException.class)
    public void testPrintMapWithNonStringKey() {
        Map<Integer, Object> map = new HashMap<Integer, Object>();
        map.put(1, "value");
        print(map);
    }

    // === toString static method ===

    @Test
    public void testToStringMethod() {
        String result = JsonPrinter.toString("hello");
        assertEquals("\"hello\"", result);
    }

    @Test
    public void testToStringWithComplexObject() {
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        map.put("name", "test");
        map.put("count", 5);
        map.put("active", true);
        String result = JsonPrinter.toString(map);
        assertTrue(result.contains("\"name\""));
        assertTrue(result.contains("\"test\""));
        assertTrue(result.contains("5"));
        assertTrue(result.contains("true"));
    }
}
