package org.datadog.jmxfetch.validator;

import com.beust.jcommander.ParameterException;

import org.junit.Test;

public class TestLogLevelValidator {

    private final LogLevelValidator validator =
            new LogLevelValidator();

    @Test
    public void testValidLevelInfo() {
        validator.validate("--log_level", "INFO");
    }

    @Test
    public void testValidLevelDebug() {
        validator.validate("--log_level", "DEBUG");
    }

    @Test
    public void testValidLevelError() {
        validator.validate("--log_level", "ERROR");
    }

    @Test
    public void testValidLevelWarn() {
        validator.validate("--log_level", "WARN");
    }

    @Test
    public void testValidLevelTrace() {
        validator.validate("--log_level", "TRACE");
    }

    @Test
    public void testValidLevelAll() {
        validator.validate("--log_level", "ALL");
    }

    @Test
    public void testValidLevelOff() {
        validator.validate("--log_level", "OFF");
    }

    @Test
    public void testValidLevelFatal() {
        validator.validate("--log_level", "FATAL");
    }

    @Test
    public void testValidLevelCaseInsensitive() {
        validator.validate("--log_level", "info");
    }

    @Test
    public void testValidLevelMixedCase() {
        validator.validate("--log_level", "Debug");
    }

    @Test(expected = ParameterException.class)
    public void testInvalidLevel() {
        validator.validate("--log_level", "INVALID");
    }

    @Test(expected = ParameterException.class)
    public void testEmptyLevel() {
        validator.validate("--log_level", "");
    }

    @Test
    public void testValidLevelLegacyLevel() {
        // "LEVEL" is a legacy valid value
        validator.validate("--log_level", "LEVEL");
    }
}
