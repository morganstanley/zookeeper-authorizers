package org.treadmill.zk.plugin.matcher.role;

import com.google.inject.Inject;
import com.unboundid.ldap.sdk.LDAPException;
import org.slf4j.Logger;
import org.treadmill.zk.plugin.matcher.Matcher;
import org.treadmill.zk.plugin.utils.LdapQuery;

import java.io.IOException;

import static org.slf4j.LoggerFactory.getLogger;
import static org.treadmill.zk.plugin.utils.Configuration.get;

public class ServerRoleMatcher extends Matcher {

  public static final String FILTER_FOR_SERVER_ROLE = "filter_for_server_role";
  public static final String BASE_DN_FOR_SERVER_ROLE = "base_dn_for_server_role";

  private static final Logger logger = getLogger(ServerRoleMatcher.class);

  @Inject
  LdapQuery ldapQuery;

  @Override
  public boolean matchAcl(String id, String aclExpr) throws IOException, LDAPException {
    logger.info("matching id={} against aclExpr={}", id, aclExpr);

    String hostName = id.split("@")[0].replace(HOST_PREFIX, "");

    String baseDN = get(BASE_DN_FOR_SERVER_ROLE);
    String filter = get(FILTER_FOR_SERVER_ROLE, hostName);

    return !ldapQuery.getAttributes(baseDN, filter).isEmpty();
  }
}
