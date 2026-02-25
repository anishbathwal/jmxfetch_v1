package org.datadog.jmxfetch.validator;

import com.beust.jcommander.ParameterException;

import org.junit.Test;

public class TestReporterValidator {

    private final ReporterValidator validator = new ReporterValidator();

    @Test
    public void testValidConsoleReporter() {
        validator.validate("--reporter", "console");
    }

    @Test
    public void testValidJsonReporter() {
        validator.validate("--reporter", "json");
    }

    @Test
    public void testValidStatsdReporterHostPort() {
        validator.validate(
                "--reporter", "statsd:localhost:8125");
    }

    @Test
    public void testValidStatsdReporterUnixSocket() {
        validator.validate(
                "--reporter",
                "statsd:unix:///var/run/statsd.sock");
    }

    @Test(expected = ParameterException.class)
    public void testInvalidReporter() {
        validator.validate("--reporter", "invalid");
    }

    @Test(expected = ParameterException.class)
    public void testEmptyStringReporter() {
        validator.validate("--reporter", "");
    }

    @Test
    public void testValidStatsdReporterIpAddress() {
        validator.validate(
                "--reporter", "statsd:192.168.1.1:9999");
    }

    @Test(expected = ParameterException.class)
    public void testReporterWithRandomText() {
        validator.validate("--reporter", "foobar");
    }

    @Test
    public void testStatsdWithOnlyPrefix() {
        // "statsd:" followed by something matches ^statsd:.+$
        validator.validate(
                "--reporter", "statsd:something");
    }
}
