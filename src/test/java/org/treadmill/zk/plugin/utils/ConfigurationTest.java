package org.treadmill.zk.plugin.utils;

import org.junit.Test;
import org.treadmill.zk.plugin.TestBase;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.treadmill.zk.plugin.utils.Configuration.get;

public class ConfigurationTest extends TestBase {
  @Test
  public void shouldLoadBundleIfFileIsPassedAsSystemProperty() throws IOException {
    Configuration.loadBundle();

    assertEquals("ou=cells,ou=treadmill,dc=suffix", get("base_dn_for_admin_role"));
  }
}