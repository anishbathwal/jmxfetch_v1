package org.datadog.jmxfetch;

import org.datadog.jmxfetch.reporter.ConsoleReporter;
import org.datadog.jmxfetch.tasks.TaskProcessor;
import org.junit.Test;

import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestTaskProcessorUnit {

    // === Positive Tests ===

    @Test
    public void testReadyWithNullExecutor() {
        ConsoleReporter reporter = new ConsoleReporter();
        TaskProcessor processor = new TaskProcessor(null, reporter);
        // When executor is null, assumes embedded mode and returns true
        assertTrue(processor.ready());
    }

    @Test
    public void testReadyWithActiveExecutor() {
        ConsoleReporter reporter = new ConsoleReporter();
        ExecutorService executor = Executors.newFixedThreadPool(2);
        TaskProcessor processor = new TaskProcessor(executor, reporter);
        assertTrue(processor.ready());
        executor.shutdownNow();
    }

    @Test
    public void testStopWithNullExecutor() {
        ConsoleReporter reporter = new ConsoleReporter();
        TaskProcessor processor = new TaskProcessor(null, reporter);
        // Should not throw
        processor.stop();
    }

    @Test
    public void testStopWithActiveExecutor() {
        ConsoleReporter reporter = new ConsoleReporter();
        ExecutorService executor = Executors.newFixedThreadPool(2);
        TaskProcessor processor = new TaskProcessor(executor, reporter);
        processor.stop();
        assertTrue(executor.isShutdown());
    }

    @Test
    public void testSetThreadPoolExecutor() {
        ConsoleReporter reporter = new ConsoleReporter();
        TaskProcessor processor = new TaskProcessor(null, reporter);
        assertTrue(processor.ready()); // null executor => embedded mode

        ExecutorService executor = Executors.newFixedThreadPool(2);
        processor.setThreadPoolExecutor(executor);
        assertTrue(processor.ready());
        executor.shutdownNow();
    }

    // === Boundary Tests ===

    @Test
    public void testReadyAfterStop() {
        ConsoleReporter reporter = new ConsoleReporter();
        ExecutorService executor = Executors.newFixedThreadPool(2);
        TaskProcessor processor = new TaskProcessor(executor, reporter);
        processor.stop();
        assertFalse(processor.ready());
    }

    @Test
    public void testStopIdempotent() {
        ConsoleReporter reporter = new ConsoleReporter();
        ExecutorService executor = Executors.newFixedThreadPool(2);
        TaskProcessor processor = new TaskProcessor(executor, reporter);
        processor.stop();
        // Calling stop again should not throw
        processor.stop();
    }
}
