package org.datadog.jmxfetch.validator;

import com.beust.jcommander.ParameterException;

import org.junit.Test;

public class PositiveIntegerValidatorTest {

    private final PositiveIntegerValidator validator =
            new PositiveIntegerValidator();

    // === Positive tests ===

    @Test
    public void testValidPositiveInteger() {
        validator.validate("--port", "8080");
    }

    @Test
    public void testValidPositiveIntegerOne() {
        validator.validate("--count", "1");
    }

    @Test
    public void testValidLargePositiveInteger() {
        validator.validate("--size", "1000000");
    }

    // === Negative tests ===

    @Test(expected = ParameterException.class)
    public void testZeroThrowsException() {
        validator.validate("--port", "0");
    }

    @Test(expected = ParameterException.class)
    public void testNegativeIntegerThrowsException() {
        validator.validate("--port", "-1");
    }

    @Test(expected = ParameterException.class)
    public void testNonIntegerThrowsException() {
        validator.validate("--port", "abc");
    }

    @Test(expected = ParameterException.class)
    public void testFloatThrowsException() {
        validator.validate("--port", "3.14");
    }

    @Test(expected = ParameterException.class)
    public void testEmptyStringThrowsException() {
        validator.validate("--port", "");
    }

    // === Boundary tests ===

    @Test
    public void testMaxIntegerValue() {
        validator.validate(
                "--val", String.valueOf(Integer.MAX_VALUE));
    }

    @Test(expected = ParameterException.class)
    public void testLargeNegativeThrowsException() {
        validator.validate(
                "--val", String.valueOf(Integer.MIN_VALUE));
    }

    @Test(expected = ParameterException.class)
    public void testOverflowThrowsException() {
        validator.validate(
                "--val", "99999999999999999999");
    }
}
