package org.treadmill.zk.plugin;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.LDAPURL;

import java.io.IOException;

import static com.google.inject.Guice.createInjector;
import static org.treadmill.zk.plugin.utils.Configuration.get;

public class KrbGuiceModule extends AbstractModule {

  public static Injector injector() {
    return createInjector(new KrbGuiceModule());
  }

  @Override
  protected void configure() {
  }

  @Provides
  LDAPConnection provideLDAPConnection() throws LDAPException, IOException {
    LDAPURL ldapUrl = new LDAPURL(get("treadmill_ldap"));
    return new LDAPConnection(ldapUrl.getHost(), ldapUrl.getPort());
  }
}