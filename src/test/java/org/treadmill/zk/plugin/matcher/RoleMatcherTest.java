package org.treadmill.zk.plugin.matcher;

import com.google.common.cache.LoadingCache;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.LDAPSearchException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.treadmill.zk.plugin.TestBase;
import org.treadmill.zk.plugin.ZkListener;
import org.treadmill.zk.plugin.utils.LdapQuery;

import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.ExecutionException;

import static com.google.common.collect.Sets.newHashSet;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.*;


@RunWith(PowerMockRunner.class)
@PrepareForTest({ZkListener.class, RoleMatcher.class})
public class RoleMatcherTest extends TestBase {
  LdapQuery mockedQuery;
  RoleMatcher matcher;

  @Before
  public void setup() throws Exception {
    mockStatic(ZkListener.class);
    PowerMockito
            .doNothing()
            .when(ZkListener.class,
                    "listen",
                    Mockito.any(LoadingCache.class),
                    Mockito.anyString());
    mockedQuery = mock(LdapQuery.class);
    matcher = new RoleMatcher(mockedQuery);
  }

  @Test
  public void shouldAlwaysMatchForReaderRole() throws IOException, LDAPException, ExecutionException {
    assertTrue(matcher.matchAcl("any principal", "readers"));
  }

  @Test
  public void shouldReturnTrueIfAdminFoundInLdap() throws IOException, ExecutionException, LDAPSearchException {
    when(mockedQuery.searchAdmins()).thenReturn(newHashSet("some-host", "some-other-host"));

    assertTrue(matcher.matchAcl("host/some-host", "admins"));

    verify(mockedQuery).searchAdmins();
  }

  @Test
  public void shouldReturnTrueIfServerFoundInLdap() throws IOException, ExecutionException, LDAPSearchException {
    when(mockedQuery.searchServers()).thenReturn(newHashSet("some-host", "some-other-host"));

    assertTrue(matcher.matchAcl("host/some-host", "servers"));
    verify(mockedQuery).searchServers();
  }

  @Test
  public void shouldReturnFalseIfAdminNotFound() throws IOException, ExecutionException, LDAPSearchException {
    when(mockedQuery.searchAdmins()).thenReturn(Collections.<String>emptySet());

    assertFalse(matcher.matchAcl("host/some-host", "admins"));

    verify(mockedQuery).searchAdmins();
  }

  @Test
  public void shouldReturnFalseIfServersNotFound() throws IOException, ExecutionException, LDAPSearchException {
    when(mockedQuery.searchServers()).thenReturn(Collections.<String>emptySet());

    assertFalse(matcher.matchAcl("host/some-host", "servers"));

    verify(mockedQuery).searchServers();
  }

  @Test
  public void shouldServerMemberFromCache() throws IOException, ExecutionException, LDAPSearchException {
    when(mockedQuery.searchServers()).thenReturn(newHashSet("some-host")).thenReturn(null);

    assertTrue(matcher.matchAcl("host/some-host", "servers"));
    assertTrue(matcher.matchAcl("host/some-host", "servers"));
    assertFalse(matcher.matchAcl("host/some-other-host", "servers"));

    verify(mockedQuery, times(1)).searchServers();
  }


}