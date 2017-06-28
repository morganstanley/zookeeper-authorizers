package org.treadmill.zk.plugin.matcher;

import org.junit.Before;
import org.junit.Test;
import org.treadmill.zk.plugin.TestBase;
import org.treadmill.zk.plugin.matcher.role.AdminRoleMatcher;
import org.treadmill.zk.plugin.matcher.role.ReaderRoleMatcher;
import org.treadmill.zk.plugin.matcher.role.ServerRoleMatcher;

import java.io.IOException;

import static org.junit.Assert.assertTrue;

public class MatcherFactoryTest extends TestBase {
  MatcherFactory factory;

  @Before
  public void before() {
    factory = new MatcherFactory();
    factory.readerRoleMatcher = new ReaderRoleMatcher();
    factory.adminRoleMatcher = new AdminRoleMatcher();
    factory.serverRoleMatcher = new ServerRoleMatcher();
    factory.userMatcher = new UserMatcher();
  }

  @Test
  public void shouldParseReadersRoleFromAcl() throws IOException {
    assertTrue(factory.getMatcher("role/readers") instanceof ReaderRoleMatcher);
  }

  @Test
  public void shouldParseAdminRoleFromAcl() throws IOException {
    assertTrue(factory.getMatcher("role/admin") instanceof AdminRoleMatcher);
  }

  @Test
  public void shouldParseServersRoleFromAcl() throws IOException {
    assertTrue(factory.getMatcher("role/servers") instanceof ServerRoleMatcher);
  }

  public void shouldDefaultToUserMatcher() throws IOException {
    assertTrue(factory.getMatcher("random") instanceof UserMatcher);
  }

}