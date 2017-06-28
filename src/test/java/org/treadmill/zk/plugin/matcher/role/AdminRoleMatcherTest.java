package org.treadmill.zk.plugin.matcher.role;

import com.unboundid.ldap.sdk.Attribute;
import com.unboundid.ldap.sdk.LDAPException;
import org.junit.Test;
import org.treadmill.zk.plugin.TestBase;
import org.treadmill.zk.plugin.utils.LdapQuery;
import org.powermock.core.classloader.annotations.PrepareForTest;

import java.io.IOException;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

@PrepareForTest({LdapQuery.class, AdminRoleMatcher.class})
public class AdminRoleMatcherTest extends TestBase {

  @Test
  public void shouldMatchAdminRole() throws IOException, LDAPException {
    LdapQuery mockedQuery = mock(LdapQuery.class);
    AdminRoleMatcher adminRoleMatcher = new AdminRoleMatcher();
    adminRoleMatcher.ldapQuery = mockedQuery;

    String baseDN = "ou=cells,ou=treadmill,dc=suffix";
    String filter = "(&(objectClass=tmCell)(cell=local))";

    List<Attribute> attributes = asList(
      new Attribute("master-hostname;tm-master-xyz", "master1.treadmill"),
      new Attribute("master-hostname;tm-master-c4ca42", "master2.treadmill"));
    when(mockedQuery.getAttributes(baseDN, filter)).thenReturn(attributes);

    assertTrue(adminRoleMatcher.matches("host/master2.treadmill@TREADMILL", "role/admin"));
    verify(mockedQuery).getAttributes(baseDN, filter);
  }
}