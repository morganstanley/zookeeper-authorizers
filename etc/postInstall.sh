rm -f /usr/lib/zookeeper/zookeeper.jar
ln -s /usr/lib/zookeeper/zookeeper-ldap-roles-all.jar /usr/lib/zookeeper/zookeeper.jar
(
cat <<EOF
authProvider.1=org.treadmill.zk.plugin.KerberosAuthProvider
jaasLoginRenew=3600000
standaloneEnabled=true
EOF
) >> /etc/zookeeper/conf/zoo.cfg