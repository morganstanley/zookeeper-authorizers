package org.treadmill.zk.plugin.utils;

import com.google.inject.Provider;
import com.unboundid.ldap.sdk.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.treadmill.zk.plugin.TestBase;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({LDAPConnection.class, SearchResult.class, LdapQuery.class, Configuration.class})
public class LdapQueryTest extends TestBase {
  String serverBaseDN, serverFilter, adminBaseDN, adminFilter;
  LDAPConnection mockConnection;
  LdapQuery ldapQuery;


  @Before
  public void setup() throws IOException {
    // same as what is there in test config properties
    serverBaseDN = "ou=servers,ou=treadmill,dc=suffix";
    serverFilter = "(&(objectClass=tmServer)(cell=local))";

    adminBaseDN = "ou=cells,ou=treadmill,dc=suffix";
    adminFilter = "(&(objectClass=tmCell)(cell=local))";

    mockConnection = mock(LDAPConnection.class);
    Provider<LDAPInterface> mockProvider = mock(Provider.class);
    when(mockProvider.get()).thenReturn(mockConnection);
    ldapQuery = new LdapQuery(mockProvider);

  }

  @Test
  public void shouldReturnListOfMastersIfExist() throws LDAPSearchException, IOException {

    SearchResultEntry searchResultEntry = new SearchResultEntry("", asList(
      new Attribute("master-hostname;master#id1", "master1"),
      new Attribute("some-other-attribute", "random"),
      new Attribute("master-hostname;master#id2", "master2")
    ));

    when(mockConnection.searchForEntry(adminBaseDN, SearchScope.SUB, adminFilter)).thenReturn(searchResultEntry);

    Set<String> masters = ldapQuery.searchAdmins();
    assertEquals(2, masters.size());
    assertTrue(masters.contains("master1"));
    assertTrue(masters.contains("master2"));
  }

  @Test
  public void shouldReturnEmptyListIfNoMasterExist() throws LDAPSearchException, IOException {

    SearchResultEntry searchResultEntry = new SearchResultEntry("", singletonList(new Attribute("some-other-attribute", "random")));

    when(mockConnection.searchForEntry(adminBaseDN, SearchScope.SUB, adminFilter)).thenReturn(searchResultEntry);

    Set<String> masters = ldapQuery.searchAdmins();
    assertTrue(masters.isEmpty());
  }

  @Test
  public void shouldReturnEmptyListIfNoMatchingEntryFound() throws LDAPSearchException, IOException {
    final SearchResultEntry nullSearchResult = null;
    when(mockConnection.searchForEntry(adminBaseDN, SearchScope.SUB, adminFilter)).thenReturn(nullSearchResult);

    Set<String> masters = ldapQuery.searchAdmins();
    assertTrue(masters.isEmpty());
  }

  @Test
  public void shouldReturnListOfServersIfExist() throws Exception {
    SearchResult mockSearchResult = mock(SearchResult.class);
    List<SearchResultEntry> searchResultEntry = asList(
      new SearchResultEntry("", singletonList(new Attribute("server", "server1.treadmill"))),
      new SearchResultEntry("", singletonList(new Attribute("server", "server2.treadmill"))));

    when(mockConnection.search(serverBaseDN, SearchScope.SUB, serverFilter)).thenReturn(mockSearchResult);
    when(mockSearchResult.getSearchEntries()).thenReturn(searchResultEntry);

    Set<String> servers = ldapQuery.searchServers();

    assertEquals(2, servers.size());
    assertTrue(servers.contains("server1.treadmill"));
    assertTrue(servers.contains("server2.treadmill"));
  }

  @Test
  public void shouldReturnEmptyListIfThereIsNoServer() throws Exception {
    SearchResult mockSearchResult = mock(SearchResult.class);
    List<SearchResultEntry> searchResultEntry = emptyList();

    when(mockConnection.search(serverBaseDN, SearchScope.SUB, serverFilter)).thenReturn(mockSearchResult);
    when(mockSearchResult.getSearchEntries()).thenReturn(searchResultEntry);

    Set<String> servers = ldapQuery.searchServers();

    assertTrue(servers.isEmpty());
  }

  @Test
  public void shouldReturnEmptyServerListIfNoMatchingEntryIsFound() throws Exception {
    SearchResult mockSearchResult = mock(SearchResult.class);
    final List<SearchResultEntry> nullSearchResult = null;

    when(mockConnection.search(serverBaseDN, SearchScope.SUB, serverFilter)).thenReturn(mockSearchResult);
    when(mockSearchResult.getSearchEntries()).thenReturn(nullSearchResult);

    Set<String> servers = ldapQuery.searchServers();

    assertTrue(servers.isEmpty());
  }

  @Test
  public void shouldReturnListOfAdminsIfThereIsUsernameAttribute() throws Exception {
    SearchResultEntry searchResultEntry = new SearchResultEntry("", asList(
      new Attribute("username", "someUser"),
      new Attribute("master-hostname;master#id1", "master1")
    ));

    when(mockConnection.searchForEntry(adminBaseDN, SearchScope.SUB, adminFilter)).thenReturn(searchResultEntry);

    Set<String> masters = ldapQuery.searchAdmins();

    assertEquals(2, masters.size());
    assertTrue(masters.contains("someUser"));
    assertTrue(masters.contains("master1"));
  }
}
