build
------
./gradlew fatJar


usage
----------
* copy build/libs/zookeeper-ldap-roles-all.jar to ZOOKEEPER_HOME/lib directory

* create a configuration properties with following properties

   treadmill_ldap=ldap://10.10.10.10:22389
   treadmill_ldap_suffix=dc=suffix
   treadmill_cell=local
   base_dn_for_admin_role=ou=cells,ou=treadmill,{0}
   filter_for_admin_role=(&(objectClass=tmCell)(cell={0}))
   base_dn_for_server_role=ou=servers,ou=treadmill,{0}
   filter_for_server_role=(&(objectClass=tmServer)(cell={0})(server={1})
   realm=TREADMILL

* add following command to ZOOKEEPER_HOME/conf/java.env
  export JVMFLAGS="-Dorg.treadmill.zk.plugin.configuration=CONFIG_FILE_PATH"

* add following property to ZOOKEEPER_HOME/conf/zoo.cfg
  authProvider.1=org.treadmill.zk.plugin.KerberosAuthProvider

* start zookeeper





