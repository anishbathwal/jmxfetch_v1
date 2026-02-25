package org.datadog.jmxfetch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.datadog.jmxfetch.tasks.TaskStatusHandler;
import org.junit.Test;

public class TestTaskStatusHandler {

    // === Positive Tests ===

    @Test
    public void testDefaultConstructor() {
        TaskStatusHandler handler = new TaskStatusHandler();
        assertNull(handler.getData());
    }

    @Test
    public void testConstructorWithThrowable() {
        Exception ex = new RuntimeException("test error");
        TaskStatusHandler handler = new TaskStatusHandler(ex);
        assertNull(handler.getData());
    }

    @Test
    public void testConstructorWithDataAndThrowable() {
        String data = "result data";
        Exception ex = new RuntimeException("test error");
        TaskStatusHandler handler = new TaskStatusHandler(data, ex);
        assertEquals("result data", handler.getData());
    }

    @Test
    public void testSetAndGetData() {
        TaskStatusHandler handler = new TaskStatusHandler();
        handler.setData("test data");
        assertEquals("test data", handler.getData());
    }

    @Test
    public void testSetThrowableStatus() {
        TaskStatusHandler handler = new TaskStatusHandler();
        handler.setThrowableStatus(new RuntimeException("error"));
        try {
            handler.raiseForStatus();
            throw new AssertionError("Expected exception");
        } catch (Throwable t) {
            assertEquals("error", t.getMessage());
        }
    }

    @Test
    public void testRaiseForStatusWhenNull() throws Throwable {
        TaskStatusHandler handler = new TaskStatusHandler();
        // Should not throw
        handler.raiseForStatus();
    }

    @Test(expected = RuntimeException.class)
    public void testRaiseForStatusThrows() throws Throwable {
        TaskStatusHandler handler =
                new TaskStatusHandler(new RuntimeException("fail"));
        handler.raiseForStatus();
    }

    // === Negative Tests ===

    @Test
    public void testSetDataToNull() {
        TaskStatusHandler handler = new TaskStatusHandler();
        handler.setData("something");
        handler.setData(null);
        assertNull(handler.getData());
    }

    @Test
    public void testSetThrowableToNull() throws Throwable {
        TaskStatusHandler handler =
                new TaskStatusHandler(new RuntimeException("err"));
        handler.setThrowableStatus(null);
        // Should not throw since we cleared it
        handler.raiseForStatus();
    }

    // === Boundary Tests ===

    @Test
    public void testDataWithDifferentTypes() {
        TaskStatusHandler handler = new TaskStatusHandler();
        handler.setData(Integer.valueOf(42));
        assertEquals(42, handler.getData());

        handler.setData(Boolean.TRUE);
        assertEquals(Boolean.TRUE, handler.getData());
    }
}
