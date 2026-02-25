package org.datadog.jmxfetch.validator;

import com.beust.jcommander.ParameterException;
import org.junit.Test;

import static org.junit.Assert.fail;

public class TestValidators {

    // ============ LogLevelValidator Tests ============

    // === Positive Tests ===

    @Test
    public void testLogLevelValidatorAcceptsAllLevels() {
        LogLevelValidator validator = new LogLevelValidator();
        String[] validLevels = {
            "ALL", "DEBUG", "ERROR", "FATAL", "INFO", "OFF", "TRACE", "LEVEL", "WARN"
        };
        for (String level : validLevels) {
            validator.validate("--log_level", level);
        }
    }

    @Test
    public void testLogLevelValidatorCaseInsensitive() {
        LogLevelValidator validator = new LogLevelValidator();
        validator.validate("--log_level", "debug");
        validator.validate("--log_level", "Info");
        validator.validate("--log_level", "wArN");
    }

    // === Negative Tests ===

    @Test(expected = ParameterException.class)
    public void testLogLevelValidatorRejectsInvalid() {
        LogLevelValidator validator = new LogLevelValidator();
        validator.validate("--log_level", "INVALID");
    }

    @Test(expected = ParameterException.class)
    public void testLogLevelValidatorRejectsEmpty() {
        LogLevelValidator validator = new LogLevelValidator();
        validator.validate("--log_level", "");
    }

    // ============ PositiveIntegerValidator Tests ============

    // === Positive Tests ===

    @Test
    public void testPositiveIntegerValidatorAcceptsPositive() {
        PositiveIntegerValidator validator = new PositiveIntegerValidator();
        validator.validate("--port", "1");
        validator.validate("--port", "100");
        validator.validate("--port", "65535");
    }

    // === Negative Tests ===

    @Test(expected = ParameterException.class)
    public void testPositiveIntegerValidatorRejectsZero() {
        PositiveIntegerValidator validator = new PositiveIntegerValidator();
        validator.validate("--port", "0");
    }

    @Test(expected = ParameterException.class)
    public void testPositiveIntegerValidatorRejectsNegative() {
        PositiveIntegerValidator validator = new PositiveIntegerValidator();
        validator.validate("--port", "-1");
    }

    @Test(expected = ParameterException.class)
    public void testPositiveIntegerValidatorRejectsNonNumber() {
        PositiveIntegerValidator validator = new PositiveIntegerValidator();
        validator.validate("--port", "abc");
    }

    @Test(expected = ParameterException.class)
    public void testPositiveIntegerValidatorRejectsFloat() {
        PositiveIntegerValidator validator = new PositiveIntegerValidator();
        validator.validate("--port", "1.5");
    }

    @Test(expected = ParameterException.class)
    public void testPositiveIntegerValidatorRejectsEmpty() {
        PositiveIntegerValidator validator = new PositiveIntegerValidator();
        validator.validate("--port", "");
    }

    // === Boundary Tests ===

    @Test
    public void testPositiveIntegerValidatorAcceptsMaxValue() {
        PositiveIntegerValidator validator = new PositiveIntegerValidator();
        validator.validate("--port", String.valueOf(Integer.MAX_VALUE));
    }

    @Test
    public void testPositiveIntegerValidatorAcceptsOne() {
        PositiveIntegerValidator validator = new PositiveIntegerValidator();
        validator.validate("--port", "1");
    }

    // ============ ReporterValidator Tests ============

    // === Positive Tests ===

    @Test
    public void testReporterValidatorAcceptsConsole() {
        ReporterValidator validator = new ReporterValidator();
        validator.validate("--reporter", "console");
    }

    @Test
    public void testReporterValidatorAcceptsJson() {
        ReporterValidator validator = new ReporterValidator();
        validator.validate("--reporter", "json");
    }

    @Test
    public void testReporterValidatorAcceptsStatsdHostPort() {
        ReporterValidator validator = new ReporterValidator();
        validator.validate("--reporter", "statsd:localhost:8125");
    }

    @Test
    public void testReporterValidatorAcceptsStatsdUnixSocket() {
        ReporterValidator validator = new ReporterValidator();
        validator.validate("--reporter", "statsd:unix:///var/run/statsd.sock");
    }

    // === Negative Tests ===

    @Test(expected = ParameterException.class)
    public void testReporterValidatorRejectsInvalid() {
        ReporterValidator validator = new ReporterValidator();
        validator.validate("--reporter", "invalid");
    }

    @Test(expected = ParameterException.class)
    public void testReporterValidatorRejectsEmpty() {
        ReporterValidator validator = new ReporterValidator();
        validator.validate("--reporter", "");
    }

    @Test(expected = ParameterException.class)
    public void testReporterValidatorRejectsStatsdNoHost() {
        ReporterValidator validator = new ReporterValidator();
        validator.validate("--reporter", "statsd:");
    }
}
