package org.datadog.jmxfetch.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestAppTelemetry {

    @Test
    public void testInitialCountsAreZero() {
        AppTelemetry telemetry = new AppTelemetry();
        assertEquals(0, telemetry.getRunningInstanceCount());
        assertEquals(0, telemetry.getBrokenInstanceCount());
        assertEquals(0, telemetry.getBrokenInstanceEventCount());
    }

    @Test
    public void testSetRunningInstanceCount() {
        AppTelemetry telemetry = new AppTelemetry();
        telemetry.setRunningInstanceCount(5);
        assertEquals(5, telemetry.getRunningInstanceCount());
    }

    @Test
    public void testSetBrokenInstanceCount() {
        AppTelemetry telemetry = new AppTelemetry();
        telemetry.setBrokenInstanceCount(3);
        assertEquals(3, telemetry.getBrokenInstanceCount());
    }

    @Test
    public void testIncrementBrokenInstanceEventCount() {
        AppTelemetry telemetry = new AppTelemetry();
        telemetry.incrementBrokenInstanceEventCount();
        assertEquals(1, telemetry.getBrokenInstanceEventCount());

        telemetry.incrementBrokenInstanceEventCount();
        assertEquals(2, telemetry.getBrokenInstanceEventCount());
    }

    @Test
    public void testSetRunningInstanceCountOverwrite() {
        AppTelemetry telemetry = new AppTelemetry();
        telemetry.setRunningInstanceCount(10);
        assertEquals(10, telemetry.getRunningInstanceCount());
        telemetry.setRunningInstanceCount(0);
        assertEquals(0, telemetry.getRunningInstanceCount());
    }

    @Test
    public void testSetBrokenInstanceCountOverwrite() {
        AppTelemetry telemetry = new AppTelemetry();
        telemetry.setBrokenInstanceCount(7);
        assertEquals(7, telemetry.getBrokenInstanceCount());
        telemetry.setBrokenInstanceCount(2);
        assertEquals(2, telemetry.getBrokenInstanceCount());
    }

    @Test
    public void testMultipleIncrementBrokenEvents() {
        AppTelemetry telemetry = new AppTelemetry();
        for (int i = 0; i < 100; i++) {
            telemetry.incrementBrokenInstanceEventCount();
        }
        assertEquals(100, telemetry.getBrokenInstanceEventCount());
    }

    @Test
    public void testCountersAreIndependent() {
        AppTelemetry telemetry = new AppTelemetry();
        telemetry.setRunningInstanceCount(5);
        telemetry.setBrokenInstanceCount(3);
        telemetry.incrementBrokenInstanceEventCount();

        assertEquals(5, telemetry.getRunningInstanceCount());
        assertEquals(3, telemetry.getBrokenInstanceCount());
        assertEquals(1, telemetry.getBrokenInstanceEventCount());
    }
}
