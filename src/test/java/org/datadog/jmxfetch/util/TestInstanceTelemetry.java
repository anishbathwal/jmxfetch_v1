package org.datadog.jmxfetch.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestInstanceTelemetry {

    @Test
    public void testInitialCountsAreZero() {
        InstanceTelemetry telemetry = new InstanceTelemetry();
        assertEquals(0, telemetry.getBeansFetched());
        assertEquals(0, telemetry.getTopLevelAttributeCount());
        assertEquals(0, telemetry.getMetricCount());
        assertEquals(
                0, telemetry.getWildcardDomainQueryCount());
        assertEquals(0.0, telemetry.getBeanMatchRatio(), 0.001);
    }

    @Test
    public void testSetBeansFetched() {
        InstanceTelemetry telemetry = new InstanceTelemetry();
        telemetry.setBeansFetched(42);
        assertEquals(42, telemetry.getBeansFetched());
    }

    @Test
    public void testSetTopLevelAttributeCount() {
        InstanceTelemetry telemetry = new InstanceTelemetry();
        telemetry.setTopLevelAttributeCount(15);
        assertEquals(15, telemetry.getTopLevelAttributeCount());
    }

    @Test
    public void testSetMetricCount() {
        InstanceTelemetry telemetry = new InstanceTelemetry();
        telemetry.setMetricCount(100);
        assertEquals(100, telemetry.getMetricCount());
    }

    @Test
    public void testSetWildcardDomainQueryCount() {
        InstanceTelemetry telemetry = new InstanceTelemetry();
        telemetry.setWildcardDomainQueryCount(7);
        assertEquals(7, telemetry.getWildcardDomainQueryCount());
    }

    @Test
    public void testSetBeanMatchRatio() {
        InstanceTelemetry telemetry = new InstanceTelemetry();
        telemetry.setBeanMatchRatio(0.75);
        assertEquals(0.75, telemetry.getBeanMatchRatio(), 0.001);
    }

    @Test
    public void testSetBeanMatchRatioZero() {
        InstanceTelemetry telemetry = new InstanceTelemetry();
        telemetry.setBeanMatchRatio(0.0);
        assertEquals(0.0, telemetry.getBeanMatchRatio(), 0.001);
    }

    @Test
    public void testSetBeanMatchRatioOne() {
        InstanceTelemetry telemetry = new InstanceTelemetry();
        telemetry.setBeanMatchRatio(1.0);
        assertEquals(1.0, telemetry.getBeanMatchRatio(), 0.001);
    }

    @Test
    public void testOverwriteValues() {
        InstanceTelemetry telemetry = new InstanceTelemetry();
        telemetry.setBeansFetched(10);
        assertEquals(10, telemetry.getBeansFetched());
        telemetry.setBeansFetched(20);
        assertEquals(20, telemetry.getBeansFetched());
    }

    @Test
    public void testAllFieldsIndependent() {
        InstanceTelemetry telemetry = new InstanceTelemetry();
        telemetry.setBeansFetched(1);
        telemetry.setTopLevelAttributeCount(2);
        telemetry.setMetricCount(3);
        telemetry.setWildcardDomainQueryCount(4);
        telemetry.setBeanMatchRatio(0.5);

        assertEquals(1, telemetry.getBeansFetched());
        assertEquals(2, telemetry.getTopLevelAttributeCount());
        assertEquals(3, telemetry.getMetricCount());
        assertEquals(
                4, telemetry.getWildcardDomainQueryCount());
        assertEquals(
                0.5, telemetry.getBeanMatchRatio(), 0.001);
    }
}
