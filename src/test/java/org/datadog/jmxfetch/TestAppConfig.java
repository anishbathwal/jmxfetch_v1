package org.datadog.jmxfetch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

public class TestAppConfig {

    // === Positive Tests ===

    @Test
    public void testBuilderDefaults() {
        AppConfig config = AppConfig.builder().build();
        assertEquals("INFO", config.getLogLevel());
        assertEquals(15000, config.getCheckPeriod());
        assertEquals(3, config.getThreadPoolSize());
        assertEquals(3, config.getReconnectionThreadPoolSize());
        assertEquals(60, config.getCollectionTimeout());
        assertEquals(60, config.getReconnectionTimeout());
        assertEquals(4096, config.getStatsdQueueSize());
        assertFalse(config.isLogFormatRfc3339());
        assertFalse(config.isDaemon());
        assertFalse(config.isEmbedded());
        assertFalse(config.isStatsdNonBlocking());
        assertEquals(0, config.getStatsdBufferSize());
        assertEquals(0, config.getSocketTimeout());
        assertEquals("/tmp", config.getTmpDirectory());
    }

    @Test
    public void testBuilderCustomValues() {
        AppConfig config = AppConfig.builder()
                .logLevel("DEBUG")
                .checkPeriod(30000)
                .threadPoolSize(5)
                .collectionTimeout(120)
                .daemon(true)
                .embedded(true)
                .build();
        assertEquals("DEBUG", config.getLogLevel());
        assertEquals(30000, config.getCheckPeriod());
        assertEquals(5, config.getThreadPoolSize());
        assertEquals(120, config.getCollectionTimeout());
        assertTrue(config.isDaemon());
        assertTrue(config.isEmbedded());
    }

    @Test
    public void testGetActionWithAction() {
        AppConfig config = AppConfig.builder()
                .action(Arrays.asList("collect"))
                .build();
        assertEquals("collect", config.getAction());
    }

    @Test
    public void testUpdateStatusWithFileLocation() {
        AppConfig config = AppConfig.builder()
                .statusLocation("/tmp/status.yaml")
                .build();
        assertTrue(config.updateStatus());
    }

    @Test
    public void testUpdateStatusWithIpcHostAndPort() {
        AppConfig config = AppConfig.builder()
                .ipcHost("localhost")
                .ipcPort(5000)
                .build();
        assertTrue(config.updateStatus());
    }

    @Test
    public void testUpdateStatusWithNoConfig() {
        AppConfig config = AppConfig.builder().build();
        assertFalse(config.updateStatus());
    }

    @Test
    public void testRemoteEnabled() {
        AppConfig config = AppConfig.builder()
                .ipcHost("localhost")
                .ipcPort(5000)
                .build();
        assertTrue(config.remoteEnabled());
    }

    @Test
    public void testRemoteDisabledNoHost() {
        AppConfig config = AppConfig.builder()
                .ipcPort(5000)
                .build();
        assertFalse(config.remoteEnabled());
    }

    @Test
    public void testRemoteDisabledNoPort() {
        AppConfig config = AppConfig.builder()
                .ipcHost("localhost")
                .build();
        assertFalse(config.remoteEnabled());
    }

    @Test
    public void testIsConsoleReporter() {
        AppConfig config = AppConfig.builder()
                .reporterString("console")
                .build();
        assertTrue(config.isConsoleReporter());
        assertFalse(config.isJsonReporter());
    }

    @Test
    public void testIsJsonReporter() {
        AppConfig config = AppConfig.builder()
                .reporterString("json")
                .build();
        assertTrue(config.isJsonReporter());
        assertFalse(config.isConsoleReporter());
    }

    @Test
    public void testGetVersion() {
        AppConfig config = AppConfig.builder().build();
        assertNotNull(config.getVersion());
    }

    @Test
    public void testGetJmxfetchTelemetryDomain() {
        AppConfig config = AppConfig.builder().build();
        assertEquals("jmx_fetch", config.getJmxfetchTelemetryDomain());
    }

    @Test
    public void testGetAutoDiscoveryPipe() {
        AppConfig config = AppConfig.builder()
                .tmpDirectory("/tmp")
                .build();
        String pipe = config.getAutoDiscoveryPipe();
        assertNotNull(pipe);
        assertTrue(pipe.contains("dd-auto_discovery"));
    }

    @Test
    public void testGetJmxLaunchFile() {
        AppConfig config = AppConfig.builder()
                .tmpDirectory("/tmp")
                .build();
        assertEquals("/tmp/jmx.launch", config.getJmxLaunchFile());
    }

    @Test
    public void testActionsContainsExpectedValues() {
        assertTrue(AppConfig.ACTIONS.contains(AppConfig.ACTION_COLLECT));
        assertTrue(AppConfig.ACTIONS.contains(AppConfig.ACTION_LIST_JVMS));
        assertTrue(AppConfig.ACTIONS.contains(AppConfig.ACTION_LIST_EVERYTHING));
        assertTrue(AppConfig.ACTIONS.contains(AppConfig.ACTION_LIST_COLLECTED));
        assertTrue(AppConfig.ACTIONS.contains(AppConfig.ACTION_LIST_MATCHING));
        assertTrue(AppConfig.ACTIONS.contains(AppConfig.ACTION_LIST_WITH_METRICS));
        assertTrue(AppConfig.ACTIONS.contains(AppConfig.ACTION_LIST_WITH_RATE_METRICS));
        assertTrue(AppConfig.ACTIONS.contains(AppConfig.ACTION_LIST_NOT_MATCHING));
        assertTrue(AppConfig.ACTIONS.contains(AppConfig.ACTION_LIST_LIMITED));
        assertTrue(AppConfig.ACTIONS.contains(AppConfig.ACTION_HELP));
        assertTrue(AppConfig.ACTIONS.contains(AppConfig.ACTION_VERSION));
    }

    // === Negative Tests ===

    @Test
    public void testGetActionWhenNull() {
        AppConfig config = AppConfig.builder().build();
        assertNull(config.getAction());
    }

    @Test
    public void testGetActionWhenEmpty() {
        AppConfig config = AppConfig.builder()
                .action(new ArrayList<String>())
                .build();
        assertNull(config.getAction());
    }

    @Test
    public void testGetReporterWhenNull() {
        AppConfig config = AppConfig.builder().build();
        assertNull(config.getReporter());
    }

    // === Boundary Tests ===

    @Test
    public void testExitWatcherDefault() {
        AppConfig config = AppConfig.builder().build();
        assertNotNull(config.getExitWatcher());
        assertFalse(config.getExitWatcher().isEnabled());
    }

    @Test
    public void testConnectionFactoryDefault() {
        AppConfig config = AppConfig.builder().build();
        assertNotNull(config.getConnectionFactory());
        assertTrue(config.getConnectionFactory() instanceof DefaultConnectionFactory);
    }

    @Test
    public void testStatusDefault() {
        AppConfig config = AppConfig.builder().build();
        assertNotNull(config.getStatus());
    }

    @Test
    public void testAutoDiscoveryPipeDisabledByDefault() {
        AppConfig config = AppConfig.builder().build();
        assertFalse(config.getAutoDiscoveryPipeEnabled());
    }

    @Test
    public void testTargetDirectInstancesDefault() {
        AppConfig config = AppConfig.builder().build();
        assertFalse(config.isTargetDirectInstances());
    }
}
