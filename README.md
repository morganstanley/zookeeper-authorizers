build
------
./gradlew jar


usage
----------
* copy build/libs/zookeeper-authorizers.jar to ZOOKEEPER_HOME/lib directory

* create a configuration in zoo.cfg.

  zookeeper.msAuthorizers=user,file # this is the default; adding it is not necessary
  authProvider.1=com.ms.zookeeper.auth.PluggableSASLAuthenticationProvider

* start zookeeper





