package org.datadog.jmxfetch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class TestYamlParser {

    private InputStream toStream(String yaml) {
        return new ByteArrayInputStream(yaml.getBytes(StandardCharsets.UTF_8));
    }

    // === Positive Tests ===

    @Test
    public void testParseYamlWithInstances() {
        String yaml = "instances:\n  - host: localhost\n    port: 9999\n";
        YamlParser parser = new YamlParser(toStream(yaml));
        assertNotNull(parser.getYamlInstances());
        List<Map<String, Object>> instances =
                (List<Map<String, Object>>) parser.getYamlInstances();
        assertEquals(1, instances.size());
        assertEquals("localhost", instances.get(0).get("host"));
    }

    @Test
    public void testParseYamlWithInitConfig() {
        String yaml = "init_config:\n  is_jmx: true\n";
        YamlParser parser = new YamlParser(toStream(yaml));
        assertNotNull(parser.getInitConfig());
    }

    @Test
    public void testGetParsedYaml() {
        String yaml = "key: value\n";
        YamlParser parser = new YamlParser(toStream(yaml));
        assertNotNull(parser.getParsedYaml());
    }

    @Test
    public void testCopyConstructor() {
        String yaml = "instances:\n  - host: localhost\n";
        YamlParser original = new YamlParser(toStream(yaml));
        YamlParser copy = new YamlParser(original);
        assertNotNull(copy.getParsedYaml());
        assertNotNull(copy.getYamlInstances());
    }

    // === Negative Tests ===

    @Test
    public void testGetYamlInstancesWhenMissing() {
        String yaml = "key: value\n";
        YamlParser parser = new YamlParser(toStream(yaml));
        assertNull(parser.getYamlInstances());
    }

    @Test
    public void testGetInitConfigWhenMissing() {
        String yaml = "key: value\n";
        YamlParser parser = new YamlParser(toStream(yaml));
        assertNull(parser.getInitConfig());
    }

    // === Boundary Tests ===

    @Test
    public void testParseYamlWithMultipleInstances() {
        String yaml =
                "instances:\n  - host: host1\n    port: 9999\n"
                + "  - host: host2\n    port: 8888\n";
        YamlParser parser = new YamlParser(toStream(yaml));
        List<Map<String, Object>> instances =
                (List<Map<String, Object>>) parser.getYamlInstances();
        assertEquals(2, instances.size());
    }

    @Test
    public void testParseYamlWithBothSections() {
        String yaml =
                "init_config:\n  is_jmx: true\n"
                + "instances:\n  - host: localhost\n";
        YamlParser parser = new YamlParser(toStream(yaml));
        assertNotNull(parser.getInitConfig());
        assertNotNull(parser.getYamlInstances());
    }
}
