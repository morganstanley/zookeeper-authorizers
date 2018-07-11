package com.ms.zookeeper;

import junit.framework.TestCase;

import org.junit.Test;

import com.ms.zookeeper.auth.PluggableSASLAuthenticationProvider;

public class TestPluggableSASLAuthenticationProvider extends TestCase {
    @Test
    public void testMatches() {
        PluggableSASLAuthenticationProvider auth = new PluggableSASLAuthenticationProvider();
        boolean result = auth.matches("test", "test");
        assertTrue(result);
        result = auth.matches("test", "file://src/test/java/com/ms/zookeeper/testauthfileall");
        assertTrue(result);
    }

}
