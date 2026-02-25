package org.datadog.jmxfetch.reporter;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.datadog.jmxfetch.AppConfig;
import org.junit.Test;

public class TestReporterFactory {

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

    @Test
    public void testGetStatsdReporterHostPort() {
        AppConfig config = AppConfig.builder()
                .reporterString("statsd:localhost:8125")
                .build();
        Reporter reporter = ReporterFactory.getReporter(config);
        assertNotNull(reporter);
        assertTrue(reporter instanceof StatsdReporter);
    }

    @Test
    public void testGetStatsdReporterUnixSocket() {
        AppConfig config = AppConfig.builder()
                .reporterString("statsd:unix:///var/run/statsd.sock")
                .build();
        Reporter reporter = ReporterFactory.getReporter(config);
        assertNotNull(reporter);
        assertTrue(reporter instanceof StatsdReporter);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetReporterInvalidType() {
        AppConfig config = AppConfig.builder()
                .reporterString("invalid_reporter")
                .build();
        ReporterFactory.getReporter(config);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetReporterNullType() {
        AppConfig config = AppConfig.builder()
                .reporterString(null)
                .build();
        ReporterFactory.getReporter(config);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetReporterEmptyType() {
        AppConfig config = AppConfig.builder()
                .reporterString("")
                .build();
        ReporterFactory.getReporter(config);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetReporterStatsdMalformed() {
        // statsd: prefix but no valid host:port or unix://
        AppConfig config = AppConfig.builder()
                .reporterString("statsd:badformat")
                .build();
        ReporterFactory.getReporter(config);
    }

    @Test
    public void testGetStatsdReporterIpv4() {
        AppConfig config = AppConfig.builder()
                .reporterString("statsd:192.168.1.1:8125")
                .build();
        Reporter reporter = ReporterFactory.getReporter(config);
        assertNotNull(reporter);
        assertTrue(reporter instanceof StatsdReporter);
    }
}
