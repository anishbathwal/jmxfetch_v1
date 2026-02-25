package org.datadog.jmxfetch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.datadog.jmxfetch.reporter.ConsoleReporter;
import org.datadog.jmxfetch.reporter.JsonReporter;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class TestAppConfig {

    @Test
    public void testBuilderDefaults() {
        AppConfig config = AppConfig.builder().build();
        assertEquals("INFO", config.getLogLevel());
        assertEquals("/tmp", config.getTmpDirectory());
        assertEquals(15000, config.getCheckPeriod());
        assertEquals(60, config.getCollectionTimeout());
        assertEquals(60, config.getReconnectionTimeout());
        assertFalse(config.isDaemon());
        assertFalse(config.isEmbedded());
        assertFalse(config.isStatsdNonBlocking());
        assertEquals(0, config.getStatsdBufferSize());
        assertEquals(0, config.getSocketTimeout());
        assertEquals(4096, config.getStatsdQueueSize());
    }

    @Test
    public void testGetActionNull() {
        AppConfig config = AppConfig.builder().build();
        assertNull(config.getAction());
    }

    @Test
    public void testGetActionWithValue() {
        AppConfig config = AppConfig.builder()
                .action(Arrays.asList("collect"))
                .build();
        assertEquals("collect", config.getAction());
    }

    @Test
    public void testGetActionEmptyList() {
        AppConfig config = AppConfig.builder()
                .action(Arrays.<String>asList())
                .build();
        assertNull(config.getAction());
    }

    @Test
    public void testUpdateStatusWithStatusLocation() {
        AppConfig config = AppConfig.builder()
                .statusLocation("/tmp/status.yaml")
                .build();
        assertTrue(config.updateStatus());
    }

    @Test
    public void testUpdateStatusWithIpcHostAndPort() {
        AppConfig config = AppConfig.builder()
                .ipcHost("localhost")
                .ipcPort(5001)
                .build();
        assertTrue(config.updateStatus());
    }

    @Test
    public void testUpdateStatusReturnsFalse() {
        AppConfig config = AppConfig.builder().build();
        assertFalse(config.updateStatus());
    }

    @Test
    public void testRemoteEnabledTrue() {
        AppConfig config = AppConfig.builder()
                .ipcHost("localhost")
                .ipcPort(5001)
                .build();
        assertTrue(config.remoteEnabled());
    }

    @Test
    public void testRemoteEnabledFalse() {
        AppConfig config = AppConfig.builder().build();
        assertFalse(config.remoteEnabled());
    }

    @Test
    public void testRemoteEnabledZeroPort() {
        AppConfig config = AppConfig.builder()
                .ipcHost("localhost")
                .ipcPort(0)
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
    public void testIsConsoleReporterNullReporter() {
        AppConfig config = AppConfig.builder().build();
        assertFalse(config.isConsoleReporter());
        assertFalse(config.isJsonReporter());
    }

    @Test
    public void testGetAutoDiscoveryPipe() {
        AppConfig config = AppConfig.builder()
                .tmpDirectory("/var/tmp")
                .build();
        String pipe = config.getAutoDiscoveryPipe();
        assertNotNull(pipe);
        assertTrue(pipe.contains("dd-auto_discovery"));
    }

    @Test
    public void testGetJmxLaunchFile() {
        AppConfig config = AppConfig.builder()
                .tmpDirectory("/var/tmp")
                .build();
        assertEquals(
                "/var/tmp/jmx.launch",
                config.getJmxLaunchFile());
    }

    @Test
    public void testGetJmxfetchTelemetryDomain() {
        AppConfig config = AppConfig.builder().build();
        assertEquals("jmx_fetch",
                config.getJmxfetchTelemetryDomain());
    }

    @Test
    public void testGetVersion() {
        AppConfig config = AppConfig.builder().build();
        // Version may be null in test env but should not throw
        config.getVersion();
    }

    @Test
    public void testAllBooleanFlags() {
        AppConfig config = AppConfig.builder()
                .daemon(true)
                .embedded(true)
                .statsdNonBlocking(true)
                .statsdTelemetry(true)
                .jmxfetchTelemetry(true)
                .targetDirectInstances(true)
                .logFormatRfc3339(true)
                .build();
        assertTrue(config.isDaemon());
        assertTrue(config.isEmbedded());
        assertTrue(config.isStatsdNonBlocking());
        assertTrue(config.getStatsdTelemetry());
        assertTrue(config.getJmxfetchTelemetry());
        assertTrue(config.isTargetDirectInstances());
        assertTrue(config.isLogFormatRfc3339());
    }

    @Test
    public void testGlobalTags() {
        Map<String, String> tags = new HashMap<String, String>();
        tags.put("env", "prod");
        AppConfig config = AppConfig.builder()
                .globalTags(tags)
                .build();
        assertNotNull(config.getGlobalTags());
        assertEquals("prod", config.getGlobalTags().get("env"));
    }

    @Test
    public void testCustomThreadPoolSizes() {
        AppConfig config = AppConfig.builder()
                .threadPoolSize(10)
                .reconnectionThreadPoolSize(5)
                .build();
        assertEquals(10, config.getThreadPoolSize());
        assertEquals(5, config.getReconnectionThreadPoolSize());
    }

    @Test
    public void testConnectionFactory() {
        AppConfig config = AppConfig.builder().build();
        assertNotNull(config.getConnectionFactory());
        assertTrue(
                config.getConnectionFactory()
                        instanceof DefaultConnectionFactory);
    }

    @Test
    public void testStatus() {
        AppConfig config = AppConfig.builder().build();
        assertNotNull(config.getStatus());
    }

    @Test
    public void testExitWatcher() {
        AppConfig config = AppConfig.builder().build();
        assertNotNull(config.getExitWatcher());
    }

    @Test
    public void testActionsSet() {
        assertTrue(AppConfig.ACTIONS.contains("collect"));
        assertTrue(AppConfig.ACTIONS.contains("list_jvms"));
        assertTrue(
                AppConfig.ACTIONS.contains("list_everything"));
        assertTrue(
                AppConfig.ACTIONS.contains(
                        "list_collected_attributes"));
        assertTrue(
                AppConfig.ACTIONS.contains(
                        "list_matching_attributes"));
        assertTrue(
                AppConfig.ACTIONS.contains("list_with_metrics"));
        assertTrue(
                AppConfig.ACTIONS.contains(
                        "list_with_rate_metrics"));
        assertTrue(
                AppConfig.ACTIONS.contains(
                        "list_not_matching_attributes"));
        assertTrue(
                AppConfig.ACTIONS.contains(
                        "list_limited_attributes"));
        assertTrue(AppConfig.ACTIONS.contains("help"));
        assertTrue(AppConfig.ACTIONS.contains("version"));
    }
}
