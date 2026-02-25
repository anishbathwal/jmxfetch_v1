package org.datadog.jmxfetch;

import org.junit.Test;

import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestAppConfigUnit {

    // === Actions Constants Tests ===

    @Test
    public void testActionsContainsAllExpected() {
        HashSet<String> actions = AppConfig.ACTIONS;
        assertTrue(actions.contains(AppConfig.ACTION_COLLECT));
        assertTrue(actions.contains(AppConfig.ACTION_LIST_JVMS));
        assertTrue(actions.contains(AppConfig.ACTION_LIST_EVERYTHING));
        assertTrue(actions.contains(AppConfig.ACTION_LIST_COLLECTED));
        assertTrue(actions.contains(AppConfig.ACTION_LIST_MATCHING));
        assertTrue(actions.contains(AppConfig.ACTION_LIST_WITH_METRICS));
        assertTrue(actions.contains(AppConfig.ACTION_LIST_WITH_RATE_METRICS));
        assertTrue(actions.contains(AppConfig.ACTION_LIST_NOT_MATCHING));
        assertTrue(actions.contains(AppConfig.ACTION_LIST_LIMITED));
        assertTrue(actions.contains(AppConfig.ACTION_HELP));
        assertTrue(actions.contains(AppConfig.ACTION_VERSION));
    }

    @Test
    public void testActionConstants() {
        assertEquals("collect", AppConfig.ACTION_COLLECT);
        assertEquals("list_jvms", AppConfig.ACTION_LIST_JVMS);
        assertEquals("list_everything", AppConfig.ACTION_LIST_EVERYTHING);
        assertEquals("list_collected_attributes", AppConfig.ACTION_LIST_COLLECTED);
        assertEquals("list_matching_attributes", AppConfig.ACTION_LIST_MATCHING);
        assertEquals("list_with_metrics", AppConfig.ACTION_LIST_WITH_METRICS);
        assertEquals("list_with_rate_metrics", AppConfig.ACTION_LIST_WITH_RATE_METRICS);
        assertEquals("list_not_matching_attributes", AppConfig.ACTION_LIST_NOT_MATCHING);
        assertEquals("list_limited_attributes", AppConfig.ACTION_LIST_LIMITED);
        assertEquals("help", AppConfig.ACTION_HELP);
        assertEquals("version", AppConfig.ACTION_VERSION);
    }

    @Test
    public void testActionsCount() {
        assertEquals(11, AppConfig.ACTIONS.size());
    }

    @Test
    public void testActionsDoesNotContainInvalid() {
        assertFalse(AppConfig.ACTIONS.contains("invalid_action"));
        assertFalse(AppConfig.ACTIONS.contains(""));
        assertFalse(AppConfig.ACTIONS.contains(null));
    }

    @Test
    public void testActionSetIsNotEmpty() {
        assertFalse(AppConfig.ACTIONS.isEmpty());
    }
}
