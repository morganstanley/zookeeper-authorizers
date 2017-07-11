package org.treadmill.zk.plugin.matcher;

import com.unboundid.ldap.sdk.LDAPException;

import java.io.IOException;

import static org.treadmill.zk.plugin.utils.Configuration.get;

public abstract class Matcher {

  public static final String HOST_PREFIX = "host/";

  public boolean matches(String id, String aclExpr) throws IOException, LDAPException {
    return matchRealm(id) && matchAcl(id, aclExpr);
  }

  public abstract boolean matchAcl(String id, String aclExpr) throws IOException, LDAPException;

  boolean matchRealm(String id) throws IOException {
    String[] splits = id.split("@", 2);
    return splits.length == 2 && get("realm").equalsIgnoreCase(splits[1]);
  }
}
