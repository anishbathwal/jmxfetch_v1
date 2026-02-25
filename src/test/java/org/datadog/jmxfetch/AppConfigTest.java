package org.datadog.jmxfetch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class AppConfigTest {

    // === Positive tests ===

    @Test
    public void testDefaultBuilderValues() {
        AppConfig config = AppConfig.builder().build();
        assertEquals("INFO", config.getLogLevel());
        assertEquals("/tmp", config.getTmpDirectory());
        assertEquals(15000, config.getCheckPeriod());
        assertEquals(3, config.getThreadPoolSize());
        assertEquals(3, config.getReconnectionThreadPoolSize());
        assertEquals(60, config.getCollectionTimeout());
        assertEquals(60, config.getReconnectionTimeout());
        assertEquals(4096, config.getStatsdQueueSize());
        assertFalse(config.isLogFormatRfc3339());
        assertFalse(config.isStatsdNonBlocking());
        assertEquals(0, config.getStatsdBufferSize());
        assertEquals(0, config.getSocketTimeout());
        assertFalse(config.isDaemon());
        assertFalse(config.isEmbedded());
        assertFalse(config.isTargetDirectInstances());
    }

    @Test
    public void testBuilderWithCustomValues() {
        AppConfig config = AppConfig.builder()
                .logLevel("DEBUG")
                .tmpDirectory("/var/tmp")
                .checkPeriod(30000)
                .threadPoolSize(5)
                .collectionTimeout(120)
                .build();

        assertEquals("DEBUG", config.getLogLevel());
        assertEquals("/var/tmp", config.getTmpDirectory());
        assertEquals(30000, config.getCheckPeriod());
        assertEquals(5, config.getThreadPoolSize());
        assertEquals(120, config.getCollectionTimeout());
    }

    @Test
    public void testGetActionWithNullAction() {
        AppConfig config = AppConfig.builder().build();
        assertNull(config.getAction());
    }

    @Test
    public void testGetActionWithValidAction() {
        AppConfig config = AppConfig.builder()
                .action(Arrays.asList("collect"))
                .build();
        assertEquals("collect", config.getAction());
    }

    @Test
    public void testGetActionWithEmptyList() {
        List<String> emptyList = new ArrayList<String>();
        AppConfig config = AppConfig.builder()
                .action(emptyList)
                .build();
        assertNull(config.getAction());
    }

    @Test
    public void testUpdateStatusWithFileLocation() {
        AppConfig config = AppConfig.builder()
                .statusLocation("/tmp/status.yaml")
                .build();
        assertTrue(config.updateStatus());
    }

    @Test
    public void testUpdateStatusWithIpc() {
        AppConfig config = AppConfig.builder()
                .ipcHost("localhost")
                .ipcPort(5001)
                .build();
        assertTrue(config.updateStatus());
    }

    @Test
    public void testUpdateStatusReturnsFalseNoConfig() {
        AppConfig config = AppConfig.builder().build();
        assertFalse(config.updateStatus());
    }

    @Test
    public void testRemoteEnabled() {
        AppConfig config = AppConfig.builder()
                .ipcHost("localhost")
                .ipcPort(5001)
                .build();
        assertTrue(config.remoteEnabled());
    }

    @Test
    public void testRemoteDisabled() {
        AppConfig config = AppConfig.builder().build();
        assertFalse(config.remoteEnabled());
    }

    @Test
    public void testRemoteDisabledZeroPort() {
        AppConfig config = AppConfig.builder()
                .ipcHost("localhost")
                .ipcPort(0)
                .build();
        assertFalse(config.remoteEnabled());
    }

    @Test
    public void testReporterStringConsole() {
        AppConfig config = AppConfig.builder()
                .reporterString("console")
                .build();
        assertEquals("console", config.getReporterString());
    }

    @Test
    public void testReporterStringJson() {
        AppConfig config = AppConfig.builder()
                .reporterString("json")
                .build();
        assertEquals("json", config.getReporterString());
    }

    @Test
    public void testReporterStringNull() {
        AppConfig config = AppConfig.builder().build();
        assertNull(config.getReporterString());
    }

    @Test
    public void testAutoDiscoveryPipe() {
        AppConfig config = AppConfig.builder()
                .tmpDirectory("/tmp")
                .build();
        String pipePath = config.getAutoDiscoveryPipe();
        assertNotNull(pipePath);
        assertTrue(pipePath.contains("dd-auto_discovery"));
    }

    @Test
    public void testJmxLaunchFile() {
        AppConfig config = AppConfig.builder()
                .tmpDirectory("/tmp")
                .build();
        assertEquals(
                "/tmp/jmx.launch", config.getJmxLaunchFile());
    }

    @Test
    public void testGetJmxfetchTelemetryDomain() {
        AppConfig config = AppConfig.builder().build();
        assertEquals(
                "jmx_fetch",
                config.getJmxfetchTelemetryDomain());
    }

    // === Static constants tests ===

    @Test
    public void testActionConstants() {
        assertEquals("collect", AppConfig.ACTION_COLLECT);
        assertEquals("list_jvms", AppConfig.ACTION_LIST_JVMS);
        assertEquals("list_everything",
                AppConfig.ACTION_LIST_EVERYTHING);
        assertEquals("list_collected_attributes",
                AppConfig.ACTION_LIST_COLLECTED);
        assertEquals("list_matching_attributes",
                AppConfig.ACTION_LIST_MATCHING);
        assertEquals("list_with_metrics",
                AppConfig.ACTION_LIST_WITH_METRICS);
        assertEquals("list_with_rate_metrics",
                AppConfig.ACTION_LIST_WITH_RATE_METRICS);
        assertEquals("list_not_matching_attributes",
                AppConfig.ACTION_LIST_NOT_MATCHING);
        assertEquals("list_limited_attributes",
                AppConfig.ACTION_LIST_LIMITED);
        assertEquals("help", AppConfig.ACTION_HELP);
        assertEquals("version", AppConfig.ACTION_VERSION);
    }

    @Test
    public void testActionsSetContainsAll() {
        HashSet<String> actions = AppConfig.ACTIONS;
        assertTrue(actions.contains("collect"));
        assertTrue(actions.contains("list_jvms"));
        assertTrue(actions.contains("list_everything"));
        assertTrue(actions.contains("help"));
        assertTrue(actions.contains("version"));
        assertEquals(11, actions.size());
    }

    // === Boundary tests ===

    @Test
    public void testBuilderWithZeroValues() {
        AppConfig config = AppConfig.builder()
                .statsdBufferSize(0)
                .statsdSocketTimeout(0)
                .ipcPort(0)
                .build();
        assertEquals(0, config.getStatsdBufferSize());
        assertEquals(0, config.getSocketTimeout());
        assertEquals(0, config.getIpcPort());
    }

    @Test
    public void testGlobalTags() {
        Map<String, String> tags = new HashMap<String, String>();
        tags.put("env", "staging");
        tags.put("region", "us-east");

        AppConfig config = AppConfig.builder()
                .globalTags(tags)
                .build();
        assertNotNull(config.getGlobalTags());
        assertEquals(2, config.getGlobalTags().size());
        assertEquals("staging",
                config.getGlobalTags().get("env"));
    }

    @Test
    public void testNullGlobalTags() {
        AppConfig config = AppConfig.builder().build();
        assertNull(config.getGlobalTags());
    }

    @Test
    public void testYamlFileList() {
        AppConfig config = AppConfig.builder()
                .yamlFileList(Arrays.asList(
                        "check1.yaml", "check2.yaml"))
                .build();
        assertEquals(2, config.getYamlFileList().size());
    }

    @Test
    public void testNullYamlFileList() {
        AppConfig config = AppConfig.builder().build();
        assertNull(config.getYamlFileList());
    }

    @Test
    public void testConnectionFactory() {
        AppConfig config = AppConfig.builder().build();
        assertNotNull(config.getConnectionFactory());
    }

    @Test
    public void testExitWatcher() {
        AppConfig config = AppConfig.builder().build();
        assertNotNull(config.getExitWatcher());
    }

    @Test
    public void testStatus() {
        AppConfig config = AppConfig.builder().build();
        assertNotNull(config.getStatus());
    }

    @Test
    public void testDaemonMode() {
        AppConfig config = AppConfig.builder()
                .daemon(true)
                .build();
        assertTrue(config.isDaemon());
    }

    @Test
    public void testEmbeddedMode() {
        AppConfig config = AppConfig.builder()
                .embedded(true)
                .build();
        assertTrue(config.isEmbedded());
    }
}
