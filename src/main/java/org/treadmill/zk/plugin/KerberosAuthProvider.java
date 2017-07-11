package org.treadmill.zk.plugin;


import com.google.inject.Injector;
import com.unboundid.ldap.sdk.LDAPException;
import org.apache.zookeeper.server.auth.SASLAuthenticationProvider;
import org.treadmill.zk.plugin.matcher.MatcherFactory;
import org.slf4j.Logger;

import java.io.IOException;

import static java.lang.String.format;
import static org.slf4j.LoggerFactory.getLogger;


public class KerberosAuthProvider extends SASLAuthenticationProvider {

  Injector injector;

  private static final Logger logger = getLogger(KerberosAuthProvider.class);

  public KerberosAuthProvider() {
    injector = KrbGuiceModule.injector();
  }

  @Override
  public boolean matches(String id, String aclExpr) {
    logger.info(format("matching id=%s, acl=%s", id, aclExpr));
    try {
      MatcherFactory factory = injector.getInstance(MatcherFactory.class);
      return factory.getMatcher(aclExpr).matches(id, aclExpr);
    } catch (IOException | LDAPException e) {
      logger.warn("cannot authorize " + id, e);
      return false;
    }
  }
}
