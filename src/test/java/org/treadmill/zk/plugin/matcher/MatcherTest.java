package org.treadmill.zk.plugin.matcher;

import com.unboundid.ldap.sdk.LDAPException;
import org.junit.Test;
import org.mockito.Mockito;
import org.treadmill.zk.plugin.TestBase;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class MatcherTest extends TestBase {

  @Test
  public void shouldMatchRealm() throws IOException, LDAPException, ExecutionException {
    assertTrue(trueMatcher().matchRealm("TREADMILL"));
    assertFalse(trueMatcher().matchRealm("NOT_TREADMILL"));
  }


  @Test
  public void shouldNotMatchIfRealmIsDifferent() throws IOException {
    String id = "host/someHost@SOMETHING_OTHER_THAN_MOCKED_REALM";

    assertFalse(falseMatcher().matchRealm(id));
  }

  @Test
  public void shouldNotMatchForIdWithoutRealm() throws IOException {
    String id = "host/someHostWithoutRealm";

    assertFalse(falseMatcher().matchRealm(id));
  }

  @Test
  public void shouldMatchBothRealmAndUser() throws IOException, LDAPException, ExecutionException {
    Matcher spyMatcher = Mockito.spy(falseMatcher());
    doReturn(true).when(spyMatcher).matchRealm("SOME_REALM");
    doReturn(true).when(spyMatcher).matchAcl("host/someHost", "someAcl");

    assertTrue(spyMatcher.matches("host/someHost@SOME_REALM", "someAcl"));

    verify(spyMatcher).matchRealm("SOME_REALM");
    verify(spyMatcher).matchAcl("host/someHost", "someAcl");
  }

  @Test
  public void shouldFailIfOnlyRealmMatches() throws IOException, LDAPException, ExecutionException {
    Matcher spyMatcher = Mockito.spy(falseMatcher());
    doReturn(true).when(spyMatcher).matchRealm("SOME_REALM");
    doReturn(false).when(spyMatcher).matchAcl("host/someHost", "someAcl");

    assertFalse(spyMatcher.matches("host/someHost@SOME_REALM", "someAcl"));

    verify(spyMatcher).matchRealm("SOME_REALM");
    verify(spyMatcher).matchAcl("host/someHost", "someAcl");
  }

  @Test
  public void shouldFailIfOnlyUserMatches() throws IOException, LDAPException, ExecutionException {
    Matcher spyMatcher = Mockito.spy(falseMatcher());
    doReturn(false).when(spyMatcher).matchRealm("SOME_REALM");

    assertFalse(spyMatcher.matches("host/someHost@SOME_REALM", "someAcl"));

    verify(spyMatcher).matchRealm("SOME_REALM");
    verify(spyMatcher, never()).matchAcl("host/someHost", "someAcl");
  }

  Matcher falseMatcher() {
    return new Matcher() {
      @Override
      public boolean matchAcl(String principal, String aclExpr) throws ExecutionException {
        return false;
      }
    };
  }

  Matcher trueMatcher() {
    return new Matcher() {
      @Override
      public boolean matchAcl(String principal, String aclExpr) throws ExecutionException {
        return true;
      }
    };
  }

}