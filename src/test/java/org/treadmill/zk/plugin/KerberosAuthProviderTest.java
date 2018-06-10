package org.treadmill.zk.plugin;

import org.junit.Before;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.treadmill.zk.plugin.matcher.RoleMatcher;
import org.treadmill.zk.plugin.matcher.UserMatcher;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@PrepareForTest({KerberosAuthProvider.class, KrbGuiceModule.class})
public class KerberosAuthProviderTest extends TestBase {
  RoleMatcher mockedRoleMatcher;
  UserMatcher mockedUserMatcher;

  @Before
  public void setup() {
    mockStatic(KrbGuiceModule.class);
    mockedRoleMatcher = mock(RoleMatcher.class);
    mockedUserMatcher = mock(UserMatcher.class);
    when(KrbGuiceModule.getInstance(RoleMatcher.class)).thenReturn(mockedRoleMatcher);
    when(KrbGuiceModule.getInstance(UserMatcher.class)).thenReturn(mockedUserMatcher);

  }

  @Test
  public void shouldMatchUsingUserMatcher() throws IOException, ExecutionException {
    when(mockedUserMatcher.matches("someUser@SOME_DOMAIN", "someUser")).thenReturn(true);

    KerberosAuthProvider provider = new KerberosAuthProvider();
    assertTrue(provider.matches("someUser@SOME_DOMAIN", "someUser"));
    verify(mockedUserMatcher).matches("someUser@SOME_DOMAIN", "someUser");
    verify(mockedRoleMatcher, never()).matchAcl(anyString(), anyString());
  }

  @Test
  public void shouldMatchUsingRoleMatcher() throws IOException, ExecutionException {
    when(mockedRoleMatcher.matches("host/someUser@SOME_DOMAIN", "someRole")).thenReturn(true);

    assertTrue(new KerberosAuthProvider().matches("host/someUser@SOME_DOMAIN", "role/someRole"));
    verify(mockedRoleMatcher).matches("host/someUser@SOME_DOMAIN", "someRole");
    verify(mockedUserMatcher, never()).matchAcl(anyString(), anyString());
  }

  @Test
  public void matchShouldFailUsingUserMatcher() {
    KerberosAuthProvider provider = new KerberosAuthProvider();
    assertFalse(provider.matches("someUser@SOME_DOMAIN", "someOtherUser"));

  }
}