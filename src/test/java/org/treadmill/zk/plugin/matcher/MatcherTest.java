package org.treadmill.zk.plugin.matcher;

import org.junit.Test;
import org.treadmill.zk.plugin.TestBase;
import org.treadmill.zk.plugin.matcher.role.AdminRoleMatcher;

import java.io.IOException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MatcherTest extends TestBase {

  @Test
  public void shouldMatchRealm() throws IOException {
    Matcher matcher = new AdminRoleMatcher();
    String id = "host/someHost@TREADMILL";

    assertTrue(matcher.matchRealm(id));
  }

  @Test
  public void shouldNotMatchIfRealmIsDifferent() throws IOException {
    Matcher matcher = new AdminRoleMatcher();
    String id = "host/someHost@SOMETHING_OTHER_THAN_MOCKED_REALM";

    assertFalse(matcher.matchRealm(id));
  }

  @Test
  public void shouldNotMatchForIdWithoutRealm() throws IOException {
    Matcher matcher = new AdminRoleMatcher();
    String id = "host/someHostWithoutRealm";

    assertFalse(matcher.matchRealm(id));
  }
}