package org.treadmill.zk.plugin;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.unboundid.ldap.sdk.*;
import org.slf4j.Logger;

import java.io.IOException;

import static com.google.inject.Guice.createInjector;
import static org.slf4j.LoggerFactory.getLogger;
import static org.treadmill.zk.plugin.utils.Configuration.get;

public class KrbGuiceModule extends AbstractModule {

  private static final Logger logger = getLogger(KrbGuiceModule.class);
  private static Injector injector;

  private KrbGuiceModule() {
  }

  public static <T> T getInstance(Class<T> tClass) {
    if (injector == null) injector = createInjector(new KrbGuiceModule());

    return injector.getInstance(tClass);
  }

  @Override
  protected void configure() {
  }

  @Provides
  LDAPInterface provideLDAPInterface() throws LDAPException, IOException {
    LDAPURL ldapUrl = new LDAPURL(get("treadmill_ldap"));
    logger.info("creating new LDAP connection to {}", ldapUrl);
    LDAPConnection connection = new LDAPConnection(ldapUrl.getHost(), ldapUrl.getPort());
    String password = null;
    GSSAPIBindRequestProperties gssapiProperties = new GSSAPIBindRequestProperties(null, password);
    gssapiProperties.setJAASClientName("Server");
    GSSAPIBindRequest bindRequest = new GSSAPIBindRequest(gssapiProperties);
    BindResult bindResult = connection.bind(bindRequest);
    if (bindResult.getResultCode().equals(ResultCode.SUCCESS)) {
      return connection;
    }
    throw new LDAPException(bindResult.getResultCode());
  }
}