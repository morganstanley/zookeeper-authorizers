package org.treadmill.zk.plugin.utils;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.unboundid.ldap.sdk.*;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.toSet;
import static org.slf4j.LoggerFactory.getLogger;
import static org.treadmill.zk.plugin.utils.Configuration.get;

@Singleton
public class LdapQuery {

  private static final Logger logger = getLogger(LdapQuery.class);
  Provider<LDAPInterface> ldapProvider;

  @Inject
  public LdapQuery(Provider<LDAPInterface> ldapProvider) throws IOException {
    this.ldapProvider = ldapProvider;
  }

  public Set<String> searchServers() throws IOException, LDAPSearchException {
    String baseDN = get("base_dn_for_server_role");
    String filter = get("filter_for_server_role");
    List<SearchResultEntry> searchEntries = ldapProvider.get()
      .search(baseDN, SearchScope.SUB, filter).getSearchEntries();

    if (searchEntries == null) return emptySet();

    logger.info("found {} attributes for baseDN={}, filter={}", searchEntries.size(), baseDN, filter);

    return searchEntries.stream()
      .map(s -> s.getAttribute("server").getValue())
      .collect(toSet());
  }

  public Set<String> searchAdmins() throws IOException, LDAPSearchException {
    String baseDN = get("base_dn_for_admin_role");
    String filter = get("filter_for_admin_role");
    SearchResultEntry searchResultEntry = ldapProvider.get()
      .searchForEntry(baseDN, SearchScope.SUB, filter);

    if (searchResultEntry == null) return emptySet();

    logger.info("found {} attributes for baseDN={}, filter={}",
      searchResultEntry.getAttributes().size(), baseDN, filter);

    return searchResultEntry.getAttributes().stream()
      .filter(a -> (a.getName().startsWith("master-hostname;") || a.getName().equals("username")))
      .map(Attribute::getValue).collect(toSet());
  }

}
