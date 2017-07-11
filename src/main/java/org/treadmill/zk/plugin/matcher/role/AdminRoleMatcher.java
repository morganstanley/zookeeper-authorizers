package org.treadmill.zk.plugin.matcher.role;

import com.google.inject.Inject;
import com.unboundid.ldap.sdk.Attribute;
import com.unboundid.ldap.sdk.LDAPException;
import org.slf4j.Logger;
import org.treadmill.zk.plugin.matcher.Matcher;
import org.treadmill.zk.plugin.utils.LdapQuery;

import java.io.IOException;
import java.util.Collection;

import static org.slf4j.LoggerFactory.getLogger;
import static org.treadmill.zk.plugin.utils.Configuration.get;

public class AdminRoleMatcher extends Matcher {

  public static final String MASTER_HOSTNAME = "master-hostname;";
  public static final String FILTER_FOR_ADMIN_ROLE = "filter_for_admin_role";
  public static final String BASE_DN_FOR_ADMIN_ROLE = "base_dn_for_admin_role";

  private static final Logger logger = getLogger(AdminRoleMatcher.class);

  @Inject
  LdapQuery ldapQuery;

  @Override
  public boolean matchAcl(String id, String aclExpr) throws IOException, LDAPException {
    logger.info("matching id={} against aclExpr={}", id, aclExpr);

    String hostName = id.split("@")[0].replace(HOST_PREFIX, "");

    String baseDN = get(BASE_DN_FOR_ADMIN_ROLE);
    String filter = get(FILTER_FOR_ADMIN_ROLE);

    Collection<Attribute> attributes = ldapQuery.getAttributes(baseDN, filter);

    return attributes.stream().anyMatch(
      a ->
        a.getName().startsWith(MASTER_HOSTNAME)
          && a.getValue().equals(hostName));
  }
}