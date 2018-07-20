package com.ms.zookeeper.authz;

public interface ZkAuthorizer {
    boolean authorize(String user, String expr);
}
