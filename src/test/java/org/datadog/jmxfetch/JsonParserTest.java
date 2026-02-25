package org.datadog.jmxfetch;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class JsonParserTest {

    private JsonParser parse(String json)
            throws JsonParser.JsonException {
        return new JsonParser(json.getBytes(UTF_8));
    }

    // === Positive tests ===

    @Test
    public void testParseSimpleObject()
            throws JsonParser.JsonException {
        String json = "{\"key\": \"value\"}";
        JsonParser parser = parse(json);
        assertNotNull(parser.getParsedJson());
        Map<String, Object> parsed =
                (Map<String, Object>) parser.getParsedJson();
        assertEquals("value", parsed.get("key"));
    }

    @Test
    public void testParseNestedObject()
            throws JsonParser.JsonException {
        String json = "{\"outer\": {\"inner\": \"val\"}}";
        JsonParser parser = parse(json);
        Map<String, Object> parsed =
                (Map<String, Object>) parser.getParsedJson();
        Map<String, Object> outer =
                (Map<String, Object>) parsed.get("outer");
        assertEquals("val", outer.get("inner"));
    }

    @Test
    public void testParseWithNumbers()
            throws JsonParser.JsonException {
        String json = "{\"int\": 42, \"float\": 3.14}";
        JsonParser parser = parse(json);
        Map<String, Object> parsed =
                (Map<String, Object>) parser.getParsedJson();
        assertEquals(42, parsed.get("int"));
        assertEquals(3.14, (Double) parsed.get("float"), 0.001);
    }

    @Test
    public void testParseWithBoolean()
            throws JsonParser.JsonException {
        String json = "{\"flag\": true, \"other\": false}";
        JsonParser parser = parse(json);
        Map<String, Object> parsed =
                (Map<String, Object>) parser.getParsedJson();
        assertEquals(true, parsed.get("flag"));
        assertEquals(false, parsed.get("other"));
    }

    @Test
    public void testParseWithNull()
            throws JsonParser.JsonException {
        String json = "{\"val\": null}";
        JsonParser parser = parse(json);
        Map<String, Object> parsed =
                (Map<String, Object>) parser.getParsedJson();
        assertNull(parsed.get("val"));
    }

    @Test
    public void testParseWithArray()
            throws JsonParser.JsonException {
        String json = "{\"arr\": [1, 2, 3]}";
        JsonParser parser = parse(json);
        Map<String, Object> parsed =
                (Map<String, Object>) parser.getParsedJson();
        assertNotNull(parsed.get("arr"));
    }

    @Test
    public void testParseEmptyObject()
            throws JsonParser.JsonException {
        String json = "{}";
        JsonParser parser = parse(json);
        Map<String, Object> parsed =
                (Map<String, Object>) parser.getParsedJson();
        assertTrue(parsed.isEmpty());
    }

    @Test
    public void testParseWithStringEscapes()
            throws JsonParser.JsonException {
        String json = "{\"msg\": \"hello\\nworld\"}";
        JsonParser parser = parse(json);
        Map<String, Object> parsed =
                (Map<String, Object>) parser.getParsedJson();
        assertEquals("hello\nworld", parsed.get("msg"));
    }

    @Test
    public void testParseWithUnicodeEscape()
            throws JsonParser.JsonException {
        String json = "{\"char\": \"\\u0041\"}";
        JsonParser parser = parse(json);
        Map<String, Object> parsed =
                (Map<String, Object>) parser.getParsedJson();
        assertEquals("A", parsed.get("char"));
    }

    @Test
    public void testParseNegativeNumber()
            throws JsonParser.JsonException {
        String json = "{\"val\": -42}";
        JsonParser parser = parse(json);
        Map<String, Object> parsed =
                (Map<String, Object>) parser.getParsedJson();
        assertEquals(-42, parsed.get("val"));
    }

    @Test
    public void testParseExponentialNumber()
            throws JsonParser.JsonException {
        String json = "{\"val\": 1.5e2}";
        JsonParser parser = parse(json);
        Map<String, Object> parsed =
                (Map<String, Object>) parser.getParsedJson();
        assertEquals(150.0,
                ((Number) parsed.get("val")).doubleValue(), 0.001);
    }

    @Test
    public void testParseWithWhitespace()
            throws JsonParser.JsonException {
        String json = "  {  \"key\"  :  \"value\"  }  ";
        JsonParser parser = parse(json);
        Map<String, Object> parsed =
                (Map<String, Object>) parser.getParsedJson();
        assertEquals("value", parsed.get("key"));
    }

    @Test
    public void testCopyConstructor()
            throws JsonParser.JsonException {
        String json = "{\"key\": \"value\"}";
        JsonParser original = parse(json);
        JsonParser copy = new JsonParser(original);
        assertNotNull(copy.getParsedJson());
    }

    @Test
    public void testGetJsonConfigs()
            throws JsonParser.JsonException {
        String json = "{\"configs\": {\"check1\": {}}}";
        JsonParser parser = parse(json);
        assertNotNull(parser.getJsonConfigs());
    }

    @Test
    public void testGetJsonTimestamp()
            throws JsonParser.JsonException {
        String json = "{\"timestamp\": 1234567890}";
        JsonParser parser = parse(json);
        assertEquals(1234567890, parser.getJsonTimestamp());
    }

    @Test
    public void testParseLongNumber()
            throws JsonParser.JsonException {
        String json = "{\"big\": 9999999999}";
        JsonParser parser = parse(json);
        Map<String, Object> parsed =
                (Map<String, Object>) parser.getParsedJson();
        assertEquals(9999999999L, parsed.get("big"));
    }

    @Test
    public void testParseZero()
            throws JsonParser.JsonException {
        String json = "{\"zero\": 0}";
        JsonParser parser = parse(json);
        Map<String, Object> parsed =
                (Map<String, Object>) parser.getParsedJson();
        assertEquals(0, parsed.get("zero"));
    }

    @Test
    public void testParseEmptyArray()
            throws JsonParser.JsonException {
        String json = "{\"arr\": []}";
        JsonParser parser = parse(json);
        Map<String, Object> parsed =
                (Map<String, Object>) parser.getParsedJson();
        assertNotNull(parsed.get("arr"));
    }

    @Test
    public void testParseEmptyString()
            throws JsonParser.JsonException {
        String json = "{\"empty\": \"\"}";
        JsonParser parser = parse(json);
        Map<String, Object> parsed =
                (Map<String, Object>) parser.getParsedJson();
        assertEquals("", parsed.get("empty"));
    }

    // === Negative / Error handling tests ===

    @Test(expected = JsonParser.JsonException.class)
    public void testParseInvalidJsonThrows()
            throws JsonParser.JsonException {
        parse("{invalid}");
    }

    @Test(expected = JsonParser.JsonException.class)
    public void testParseEmptyInputThrows()
            throws JsonParser.JsonException {
        parse("");
    }

    @Test(expected = JsonParser.JsonException.class)
    public void testParseArrayAtTopLevelThrows()
            throws JsonParser.JsonException {
        parse("[1, 2, 3]");
    }

    @Test(expected = JsonParser.JsonException.class)
    public void testParseStringAtTopLevelThrows()
            throws JsonParser.JsonException {
        parse("\"just a string\"");
    }

    @Test(expected = JsonParser.JsonException.class)
    public void testParseUnterminatedObjectThrows()
            throws JsonParser.JsonException {
        parse("{\"key\": \"value\"");
    }

    @Test(expected = JsonParser.JsonException.class)
    public void testParseUnterminatedStringThrows()
            throws JsonParser.JsonException {
        parse("{\"key\": \"value");
    }

    @Test(expected = JsonParser.JsonException.class)
    public void testParseTrailingCommaThrows()
            throws JsonParser.JsonException {
        parse("{\"key\": \"value\",}");
    }

    @Test(expected = JsonParser.JsonException.class)
    public void testParseDuplicateColonThrows()
            throws JsonParser.JsonException {
        parse("{\"key\":: \"value\"}");
    }

    // === JsonException tests ===

    @Test
    public void testJsonExceptionMessage() {
        JsonParser.JsonException ex =
                new JsonParser.JsonException("test error");
        assertEquals("test error", ex.getMessage());
    }
}
