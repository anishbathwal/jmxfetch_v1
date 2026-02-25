package org.datadog.jmxfetch;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.datadog.jmxfetch.reporter.ConsoleReporter;
import org.datadog.jmxfetch.reporter.JsonReporter;
import org.datadog.jmxfetch.reporter.Reporter;
import org.datadog.jmxfetch.reporter.ReporterFactory;
import org.junit.Test;

public class TestReporterFactory {

    // === Positive Tests ===

    @Test
    public void testGetConsoleReporter() {
        AppConfig config = AppConfig.builder()
                .reporterString("console")
                .build();
        Reporter reporter = ReporterFactory.getReporter(config);
        assertNotNull(reporter);
        assertTrue(reporter instanceof ConsoleReporter);
    }

    @Test
    public void testGetJsonReporter() {
        AppConfig config = AppConfig.builder()
                .reporterString("json")
                .build();
        Reporter reporter = ReporterFactory.getReporter(config);
        assertNotNull(reporter);
        assertTrue(reporter instanceof JsonReporter);
    }

    // === Negative Tests ===

    @Test(expected = IllegalArgumentException.class)
    public void testGetReporterWithNullType() {
        AppConfig config = AppConfig.builder()
                .reporterString(null)
                .build();
        ReporterFactory.getReporter(config);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetReporterWithEmptyType() {
        AppConfig config = AppConfig.builder()
                .reporterString("")
                .build();
        ReporterFactory.getReporter(config);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetReporterWithInvalidType() {
        AppConfig config = AppConfig.builder()
                .reporterString("invalid_reporter")
                .build();
        ReporterFactory.getReporter(config);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetReporterWithMalformedStatsd() {
        AppConfig config = AppConfig.builder()
                .reporterString("statsd:malformed")
                .build();
        ReporterFactory.getReporter(config);
    }
}
