package org.treadmill.zk.plugin.matcher;

import com.unboundid.ldap.sdk.LDAPException;

import java.io.IOException;

public class UserMatcher extends Matcher {

  @Override
  public boolean matchAcl(String id, String aclExpr) throws IOException, LDAPException {
    return id.split("@")[0].equals(aclExpr);
  }
}
