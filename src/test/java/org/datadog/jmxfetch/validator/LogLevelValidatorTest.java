package org.datadog.jmxfetch.validator;

import com.beust.jcommander.ParameterException;

import org.junit.Test;

public class LogLevelValidatorTest {

    private final LogLevelValidator validator =
            new LogLevelValidator();

    // === Positive tests ===

    @Test
    public void testValidDebugLevel() {
        validator.validate("--log_level", "DEBUG");
    }

    @Test
    public void testValidInfoLevel() {
        validator.validate("--log_level", "INFO");
    }

    @Test
    public void testValidWarnLevel() {
        validator.validate("--log_level", "WARN");
    }

    @Test
    public void testValidErrorLevel() {
        validator.validate("--log_level", "ERROR");
    }

    @Test
    public void testValidFatalLevel() {
        validator.validate("--log_level", "FATAL");
    }

    @Test
    public void testValidTraceLevel() {
        validator.validate("--log_level", "TRACE");
    }

    @Test
    public void testValidAllLevel() {
        validator.validate("--log_level", "ALL");
    }

    @Test
    public void testValidOffLevel() {
        validator.validate("--log_level", "OFF");
    }

    @Test
    public void testValidLevelLevel() {
        validator.validate("--log_level", "LEVEL");
    }

    @Test
    public void testCaseInsensitiveLowercase() {
        validator.validate("--log_level", "debug");
    }

    @Test
    public void testCaseInsensitiveMixed() {
        validator.validate("--log_level", "Info");
    }

    // === Negative tests ===

    @Test(expected = ParameterException.class)
    public void testInvalidLevelThrowsException() {
        validator.validate("--log_level", "VERBOSE");
    }

    @Test(expected = ParameterException.class)
    public void testEmptyStringThrowsException() {
        validator.validate("--log_level", "");
    }

    @Test(expected = ParameterException.class)
    public void testRandomStringThrowsException() {
        validator.validate("--log_level", "xyz123");
    }

    @Test(expected = ParameterException.class)
    public void testNumericStringThrowsException() {
        validator.validate("--log_level", "123");
    }
}
