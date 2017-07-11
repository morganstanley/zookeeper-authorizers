package org.treadmill.zk.plugin.utils;

import com.google.inject.Inject;
import com.unboundid.ldap.sdk.*;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

public class LdapQuery {

  private final LDAPInterface connection;

  private static final Logger logger = getLogger(LdapQuery.class);

  @Inject
  public LdapQuery(LDAPConnection connection) {
    this.connection = connection;
  }

  public Collection<Attribute> getAttributes(String baseDN, String filter) throws LDAPException, IOException {

    List<SearchResultEntry> searchEntries = connection.search(baseDN, SearchScope.ONE, filter).getSearchEntries();
    logger.info("Found {} attributes for baseDN={} and filter={}", searchEntries.size(), baseDN, filter);
    return searchEntries.get(0).getAttributes();
  }
}
