package org.treadmill.zk.plugin.matcher;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static org.treadmill.zk.plugin.utils.Configuration.get;

public abstract class Matcher {

  public boolean matches(String id, String aclExpr) throws IOException, ExecutionException {
    String[] splits = id.split("@", 2);
    String principal = splits[0];
    String realm = splits[1];

    return splits.length == 2
      && matchRealm(realm)
      && matchAcl(principal, aclExpr);
  }

  public abstract boolean matchAcl(String principal, String aclExpr) throws ExecutionException;

  boolean matchRealm(String realm) throws IOException {
    return get("realm").equalsIgnoreCase(realm);
  }
}
