package com.ms.zookeeper;
import junit.framework.TestCase;

import org.junit.Test;

import com.ms.zookeeper.authz.ZkFileAuthorizer;

public class TestZkFileAuthorizer extends TestCase {

    @Test
    public void testFile() {
        ZkFileAuthorizer auth = new ZkFileAuthorizer();
        boolean b = auth.authorize("bothejj@REALM", "file://src/test/java/com/ms/zookeeper/testauthfile");
        assertEquals(b, true);
        auth.clearCache();
        b = auth.authorize("bothejj@REALM", "file://src/test/java/com/ms/zookeeper/testauthfile");
        assertEquals(b, true);
        b = auth.authorize("bohejj@REALM", "file://src/test/java/com/ms/zookeeper/testauthfile");
        assertEquals(b, false);
        b = auth.authorize("bothejj@REALM", "file://src/test/java/com/ms/zookeeper/testauthfil");
        assertEquals(b, false);
        b = auth.authorize("foo", "file://src/test/java/com/ms/zookeeper/testauthfileall");
        assertEquals(b, true);
    }

    @Test
    public void testFileBasePath() {
        System.setProperty("authzFileBasePath", "src/test/java/com/ms/zookeeper");
        ZkFileAuthorizer auth = new ZkFileAuthorizer();
        boolean b = auth.authorize("bothejj@REALM", "file://testauthfile");
        assertEquals(b, true);
        System.clearProperty("authzFileBasePath");
    }

    @Test
    public void testCachUpdate() {
        ZkFileAuthorizer auth = new ZkFileAuthorizer();
        assertTrue(auth.getCache().isEmpty());
        auth.authorize("bothejj@REALM", "file://src/test/java/com/ms/zookeeper/testauthfile");
        assertFalse(auth.getCache().isEmpty());
    }
}
