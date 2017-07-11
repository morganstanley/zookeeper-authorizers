package org.treadmill.zk.plugin.matcher;

import com.unboundid.ldap.sdk.LDAPException;
import org.junit.Test;
import org.treadmill.zk.plugin.TestBase;

import java.io.IOException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class UserMatcherTest extends TestBase {

  @Test
  public void shouldMatchIfIdIsSameAsAcl() throws IOException, LDAPException {
    UserMatcher matcher = new UserMatcher();
    String aclExpr = "someUser";
    String id = "someUser@TREADMILL";
    assertTrue(matcher.matches(id, aclExpr));
  }

  @Test
  public void shouldNotMatchIfIdIdIsDifferentFromAcl() throws IOException, LDAPException {
    UserMatcher matcher = new UserMatcher();
    String aclExpr = "someUser";
    String id = "someOtherUser@TREADMILL";
    assertFalse(matcher.matches(id, aclExpr));
  }

}