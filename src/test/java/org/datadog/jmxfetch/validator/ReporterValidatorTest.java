package org.datadog.jmxfetch.validator;

import com.beust.jcommander.ParameterException;

import org.junit.Test;

public class ReporterValidatorTest {

    private final ReporterValidator validator =
            new ReporterValidator();

    // === Positive tests ===

    @Test
    public void testConsoleReporter() {
        validator.validate("--reporter", "console");
    }

    @Test
    public void testJsonReporter() {
        validator.validate("--reporter", "json");
    }

    @Test
    public void testStatsdReporterHostPort() {
        validator.validate(
                "--reporter", "statsd:localhost:8125");
    }

    @Test
    public void testStatsdReporterIpPort() {
        validator.validate(
                "--reporter", "statsd:127.0.0.1:8125");
    }

    @Test
    public void testStatsdReporterUnixSocket() {
        validator.validate(
                "--reporter",
                "statsd:unix:///var/run/dogstatsd.sock");
    }

    // === Negative tests ===

    @Test(expected = ParameterException.class)
    public void testEmptyStringThrowsException() {
        validator.validate("--reporter", "");
    }

    @Test(expected = ParameterException.class)
    public void testInvalidReporterThrowsException() {
        validator.validate("--reporter", "invalid");
    }

    @Test(expected = ParameterException.class)
    public void testStatsdWithoutHostThrowsException() {
        validator.validate("--reporter", "statsd:");
    }

    @Test(expected = ParameterException.class)
    public void testRandomStringThrowsException() {
        validator.validate("--reporter", "foobar");
    }

    // === Boundary tests ===

    @Test
    public void testStatsdReporterMinimalHost() {
        validator.validate("--reporter", "statsd:a:1");
    }

    @Test
    public void testStatsdReporterLargePort() {
        validator.validate(
                "--reporter", "statsd:localhost:65535");
    }
}
