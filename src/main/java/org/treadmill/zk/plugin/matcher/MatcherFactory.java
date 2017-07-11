package org.treadmill.zk.plugin.matcher;

import com.google.inject.Inject;
import org.treadmill.zk.plugin.matcher.role.AdminRoleMatcher;
import org.treadmill.zk.plugin.matcher.role.ReaderRoleMatcher;
import org.treadmill.zk.plugin.matcher.role.ServerRoleMatcher;

import java.io.IOException;

public class MatcherFactory {
  @Inject
  AdminRoleMatcher adminRoleMatcher;

  @Inject
  ServerRoleMatcher serverRoleMatcher;

  @Inject
  ReaderRoleMatcher readerRoleMatcher;

  @Inject
  UserMatcher userMatcher;

  public Matcher getMatcher(String aclExpr) throws IOException {
    switch (aclExpr) {
      case "role/admin":
        return adminRoleMatcher;
      case "role/servers":
        return serverRoleMatcher;
      case "role/readers":
        return readerRoleMatcher;
      default:
        return userMatcher;
    }
  }

}
