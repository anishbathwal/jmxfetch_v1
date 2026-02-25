package org.datadog.jmxfetch.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class TestConfigServiceNameProvider {

    // === Positive Tests ===

    @Test
    public void testServiceFromInstanceConfig() {
        Map<String, Object> instanceMap = new HashMap<String, Object>();
        instanceMap.put("service", "my-service");
        Map<String, Object> initConfig = new HashMap<String, Object>();

        ConfigServiceNameProvider provider =
                new ConfigServiceNameProvider(instanceMap, initConfig, null);
        List<String> names = toList(provider.getServiceNames());
        assertEquals(1, names.size());
        assertEquals("my-service", names.get(0));
    }

    @Test
    public void testServiceFromInitConfig() {
        Map<String, Object> instanceMap = new HashMap<String, Object>();
        Map<String, Object> initConfig = new HashMap<String, Object>();
        initConfig.put("service", "init-service");

        ConfigServiceNameProvider provider =
                new ConfigServiceNameProvider(instanceMap, initConfig, null);
        List<String> names = toList(provider.getServiceNames());
        assertEquals(1, names.size());
        assertEquals("init-service", names.get(0));
    }

    @Test
    public void testInstanceConfigTakesPrecedence() {
        Map<String, Object> instanceMap = new HashMap<String, Object>();
        instanceMap.put("service", "instance-service");
        Map<String, Object> initConfig = new HashMap<String, Object>();
        initConfig.put("service", "init-service");

        ConfigServiceNameProvider provider =
                new ConfigServiceNameProvider(instanceMap, initConfig, null);
        List<String> names = toList(provider.getServiceNames());
        assertEquals(1, names.size());
        assertEquals("instance-service", names.get(0));
    }

    @Test
    public void testServiceListFromConfig() {
        Map<String, Object> instanceMap = new HashMap<String, Object>();
        instanceMap.put("service", Arrays.asList("svc1", "svc2"));
        Map<String, Object> initConfig = new HashMap<String, Object>();

        ConfigServiceNameProvider provider =
                new ConfigServiceNameProvider(instanceMap, initConfig, null);
        List<String> names = toList(provider.getServiceNames());
        assertEquals(2, names.size());
    }

    @Test
    public void testWithAdditionalServiceNames() {
        Map<String, Object> instanceMap = new HashMap<String, Object>();
        instanceMap.put("service", "primary");
        Map<String, Object> initConfig = new HashMap<String, Object>();

        ServiceNameProvider additional = new ServiceNameProvider() {
            @Override
            public Iterable<String> getServiceNames() {
                return Arrays.asList("additional-svc");
            }
        };

        ConfigServiceNameProvider provider =
                new ConfigServiceNameProvider(instanceMap, initConfig, additional);
        List<String> names = toList(provider.getServiceNames());
        assertEquals(2, names.size());
        assertEquals("primary", names.get(0));
        assertEquals("additional-svc", names.get(1));
    }

    // === Negative Tests ===

    @Test
    public void testNoServiceDefined() {
        Map<String, Object> instanceMap = new HashMap<String, Object>();
        Map<String, Object> initConfig = new HashMap<String, Object>();

        ConfigServiceNameProvider provider =
                new ConfigServiceNameProvider(instanceMap, initConfig, null);
        List<String> names = toList(provider.getServiceNames());
        assertEquals(0, names.size());
    }

    @Test
    public void testNullInstanceConfig() {
        Map<String, Object> initConfig = new HashMap<String, Object>();
        initConfig.put("service", "from-init");

        ConfigServiceNameProvider provider =
                new ConfigServiceNameProvider(null, initConfig, null);
        List<String> names = toList(provider.getServiceNames());
        assertEquals(1, names.size());
        assertEquals("from-init", names.get(0));
    }

    @Test
    public void testNullInitConfig() {
        Map<String, Object> instanceMap = new HashMap<String, Object>();
        instanceMap.put("service", "from-instance");

        ConfigServiceNameProvider provider =
                new ConfigServiceNameProvider(instanceMap, null, null);
        List<String> names = toList(provider.getServiceNames());
        assertEquals(1, names.size());
    }

    @Test
    public void testBothConfigsNull() {
        ConfigServiceNameProvider provider =
                new ConfigServiceNameProvider(null, null, null);
        List<String> names = toList(provider.getServiceNames());
        assertEquals(0, names.size());
    }

    @Test
    public void testEmptyServiceString() {
        Map<String, Object> instanceMap = new HashMap<String, Object>();
        instanceMap.put("service", "");
        Map<String, Object> initConfig = new HashMap<String, Object>();

        ConfigServiceNameProvider provider =
                new ConfigServiceNameProvider(instanceMap, initConfig, null);
        List<String> names = toList(provider.getServiceNames());
        assertEquals(0, names.size());
    }

    @Test
    public void testNullServiceValue() {
        Map<String, Object> instanceMap = new HashMap<String, Object>();
        instanceMap.put("service", null);
        Map<String, Object> initConfig = new HashMap<String, Object>();

        ConfigServiceNameProvider provider =
                new ConfigServiceNameProvider(instanceMap, initConfig, null);
        List<String> names = toList(provider.getServiceNames());
        assertEquals(0, names.size());
    }

    // === Boundary Tests ===

    @Test
    public void testWithNullAdditionalProvider() {
        Map<String, Object> instanceMap = new HashMap<String, Object>();
        instanceMap.put("service", "svc");
        ConfigServiceNameProvider provider =
                new ConfigServiceNameProvider(instanceMap, null, null);
        assertNotNull(provider.getServiceNames());
    }

    private static List<String> toList(Iterable<String> iterable) {
        List<String> list = new ArrayList<String>();
        for (String s : iterable) {
            list.add(s);
        }
        return list;
    }
}
