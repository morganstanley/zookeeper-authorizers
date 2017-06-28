package org.treadmill.zk.plugin.matcher.role;

import com.unboundid.ldap.sdk.Attribute;
import com.unboundid.ldap.sdk.LDAPException;
import org.junit.Test;
import org.treadmill.zk.plugin.TestBase;
import org.treadmill.zk.plugin.utils.LdapQuery;
import org.powermock.core.classloader.annotations.PrepareForTest;

import java.io.IOException;
import java.util.List;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

@PrepareForTest({LdapQuery.class, ServerRoleMatcher.class})
public class ServerRoleMatcherTest extends TestBase {

  @Test
  public void shouldMatchServerRole() throws IOException, LDAPException {
    LdapQuery mockedQuery = mock(LdapQuery.class);
    ServerRoleMatcher serverRoleMatcher = new ServerRoleMatcher();
    serverRoleMatcher.ldapQuery = mockedQuery;

    String baseDN = "ou=servers,ou=treadmill,dc=suffix";
   String filter = "(&(objectClass=tmServer)(cell=local)(server=master2.treadmill))";

    List<Attribute> attributes = singletonList(new Attribute("cell", "cell1"));
    when(mockedQuery.getAttributes(baseDN, filter)).thenReturn(attributes);

    assertTrue(serverRoleMatcher.matches("host/master2.treadmill@TREADMILL", "role/servers"));
    verify(mockedQuery).getAttributes(baseDN, filter);
  }
}
