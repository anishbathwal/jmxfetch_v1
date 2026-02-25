package org.datadog.jmxfetch.reporter;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestLoggingErrorHandler {

    @Test
    public void testInitialErrorCountIsZero() {
        LoggingErrorHandler handler = new LoggingErrorHandler();
        assertEquals(0, handler.getErrors());
    }

    @Test
    public void testHandleIncrementsErrors() {
        LoggingErrorHandler handler = new LoggingErrorHandler();
        handler.handle(new RuntimeException("test error"));
        assertEquals(1, handler.getErrors());
    }

    @Test
    public void testMultipleHandleCalls() {
        LoggingErrorHandler handler = new LoggingErrorHandler();
        handler.handle(new RuntimeException("error 1"));
        handler.handle(new RuntimeException("error 2"));
        handler.handle(new RuntimeException("error 3"));
        assertEquals(3, handler.getErrors());
    }

    @Test
    public void testHandleWithNullPointerException() {
        LoggingErrorHandler handler = new LoggingErrorHandler();
        handler.handle(new NullPointerException("null"));
        assertEquals(1, handler.getErrors());
    }

    @Test
    public void testHandleWithIoException() {
        LoggingErrorHandler handler = new LoggingErrorHandler();
        handler.handle(
                new java.io.IOException("io error"));
        assertEquals(1, handler.getErrors());
    }
}
