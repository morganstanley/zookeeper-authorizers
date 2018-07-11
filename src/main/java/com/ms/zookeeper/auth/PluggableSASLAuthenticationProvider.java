package com.ms.zookeeper.auth;

import java.lang.reflect.Method;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.apache.zookeeper.server.auth.SASLAuthenticationProvider;

import com.ms.zookeeper.authz.ZkAuthorizer;

/**
 * Extending Sasl authentication provider for ZooKeeper with pluggable authorizers.
 * 
 * The plugins to load can be configured with zookeeper.msAuthorizers
 * The plugins must be named using the following pattern: com.ms.zookeeper.authz.Zk$nameAuthorizers
 * This class extends SASLAuthenticationProvider.
 *
 */
public class PluggableSASLAuthenticationProvider extends SASLAuthenticationProvider {

    private static final Logger LOG = Logger.getLogger(PluggableSASLAuthenticationProvider.class);

    private static final HashMap<String, ZkAuthorizer> AUTHORIZERS;
    static {
        AUTHORIZERS = getAuthorizers();
    }

    private static HashMap<String, ZkAuthorizer> getAuthorizers() {
        String authzString = System.getProperty("zookeeper.msAuthorizers", "file");
        LOG.info("authorizers: " + authzString);
        HashMap<String, ZkAuthorizer> authorizers = new HashMap();
        for (String authorizer : authzString.split(",")) {
            try {
                String authorizerUppercase = Character.toUpperCase(authorizer.charAt(0)) + authorizer.substring(1);
                String authorizerName = "com.ms.zookeeper.authz.Zk" + authorizerUppercase + "Authorizer";
                LOG.debug("Found Authorizer : " + authorizerName);
                Class<?> cls = Class.forName(authorizerName);
                // Authorizers are singleton's. To get an instance, we need the getInstance() method first
                Method method = cls.getMethod("getInstance", new Class[0]);
                ZkAuthorizer authz = (ZkAuthorizer) method.invoke(cls, new Object[0]);
                authorizers.put(authorizer, authz);
            } catch (Exception e) {
                LOG.error("Exception in loading " + authorizer + "authorizer", e);
            }
        }
        return authorizers;
    }

    /*
     * id is the ACl expression set by the client
     * If a custom auth/z plugin is used, the id need to be in a particular
     * format: authz_protocol://authz_data
     *
     * If the id matches the pattern:
     * isValid will split the id and check if the first part is in the list
     * of valid authorizers
     *
     * If the id doesn't match the pattern:
     * the base sasl validation will be used
     * 
     * @see
     * org.apache.zookeeper.server.auth.AuthenticationProvider#isValid(java.
     * lang.String)
     */
    @Override
    public boolean isValid(String id) {
        String[] words = id.split("://");
        if (words.length == 1) {
            return super.isValid(id);
        }
        String authorizer = words[0];
        if (AUTHORIZERS.containsKey(authorizer)) {
            return true;
        }
        LOG.info("Invalid authorizer : " + authorizer);
        return false;
    }

    /*
     * id is the user that is trying to access the znode aclExpr is of the form
     * protocol://data. eg - user://john file:///tmp/foo
     *
     * If the authz_protocol://authz_data format is not used. base SASL code will be used
     * 
     * @see
     * org.apache.zookeeper.server.auth.AuthenticationProvider#matches(java.
     * lang.String, java.lang.String)
     */
    @Override
    public boolean matches(String id, String aclExpr) {
        LOG.debug("Authorizing user " + id + " on ACL expr " + aclExpr);

        String[] words = aclExpr.split("://");
        if (words.length == 1) {
            return super.matches(id, aclExpr);
        }

        String authorizer = aclExpr.split("://")[0];
        if (!isValid(authorizer)) {
            return false;
        }

        try {
            return AUTHORIZERS.get(authorizer).authorize(id, aclExpr);
        } catch (Exception e) {
            LOG.error("Authorizer exception", e);
        }

        return false;
    }

}
