package org.datadog.jmxfetch.validator;

import com.beust.jcommander.ParameterException;

import org.junit.Test;

public class TestPositiveIntegerValidator {

    private final PositiveIntegerValidator validator =
            new PositiveIntegerValidator();

    @Test
    public void testValidPositiveInteger() {
        validator.validate("--port", "8125");
    }

    @Test
    public void testValidPositiveOne() {
        validator.validate("--port", "1");
    }

    @Test
    public void testValidLargeNumber() {
        validator.validate("--port", "999999");
    }

    @Test(expected = ParameterException.class)
    public void testZeroIsInvalid() {
        validator.validate("--port", "0");
    }

    @Test(expected = ParameterException.class)
    public void testNegativeNumberIsInvalid() {
        validator.validate("--port", "-1");
    }

    @Test(expected = ParameterException.class)
    public void testNonNumericStringIsInvalid() {
        validator.validate("--port", "abc");
    }

    @Test(expected = ParameterException.class)
    public void testEmptyStringIsInvalid() {
        validator.validate("--port", "");
    }

    @Test(expected = ParameterException.class)
    public void testFloatIsInvalid() {
        validator.validate("--port", "3.14");
    }

    @Test(expected = ParameterException.class)
    public void testNegativeLargeNumberIsInvalid() {
        validator.validate("--port", "-999999");
    }
}
