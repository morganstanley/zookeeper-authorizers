package org.treadmill.zk.plugin;

import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.treadmill.zk.plugin.utils.Configuration;

import static java.lang.System.getProperty;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@PrepareForTest(Configuration.class)
@RunWith(PowerMockRunner.class)
public class TestBase {

  public TestBase() {
    mockConfiguration();
  }

  public static void mockConfiguration() {
    String dir = getProperty("user.dir");
    String testFilePath = dir + "/src/test/java/resources/configs-test.properties";

    mockStatic(System.class);
    when(getProperty("org.treadmill.zk.plugin.configuration")).thenReturn(testFilePath);
  }
}
