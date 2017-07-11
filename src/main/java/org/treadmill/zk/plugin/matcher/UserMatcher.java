package org.treadmill.zk.plugin.matcher;

import com.google.inject.Singleton;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

@Singleton
public class UserMatcher extends Matcher {

  private static final Logger logger = getLogger(UserMatcher.class);

  @Override
  public boolean matchAcl(String principal, String aclExpr) {
    logger.info("UserMatcher {}, id={}, aclExpr={}", this, principal, aclExpr);
    return principal.equals(aclExpr);
  }
}
