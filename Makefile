# Makefile for zookeeper-authorizers RPM.

# if proxy is needed for gradle than gradle/gradle.properties should have the following:
# systemProp.http.proxyHost=$proxy_host
# systemProp.https.proxyHost=$proxy_host
# systemProp.http.proxyPort=$proxy_port
# systemProp.https.proxyPort=$proxy_port

# For the cache operations and install S3_JAR_CACHE should be set like s3://$s3_path

PWD=$(shell pwd)

BLD=$(PWD)/build
RPMDIR=$(BLD)/rpms
JARCACHE=~/.gradle/caches

VERSION=$(shell grep version build.gradle | cut -d "'" -f 2)
RELEASE=1

clean:
	rm -rf $(BLD)

remove_jarcache:
	rm -rf $(JARCACHE)

upload_jarcache: remove_jarcache refresh_cache
	aws s3 sync $(JARCACHE) $(S3_JAR_CACHE) 

sync_jarcache: 
	aws s3 sync $(S3_JAR_CACHE) $(JARCACHE) 

refresh_cache:
	./gradlew jar --refresh-dependencies

install: sync_jarcache
	./gradlew jar --offline 

rpm:
	mkdir -p $(RPMDIR)/SOURCES
	mkdir -p $(RPMDIR)/SRPMS
	mkdir -p $(RPMDIR)/RPMS
	mkdir -p $(RPMDIR)/BUILD
	mkdir -p $(RPMDIR)/BUILDROOT
	cp build/libs/zookeeper-authorizers-$(VERSION).jar $(RPMDIR)/BUILD/
	rpmbuild -bb                                                     \
		-D "_topdir $(RPMDIR)"                                       \
		-D "_version $(VERSION)"                             \
		-D "_release $(RELEASE)"                             \
		zookeeper-authorizers.spec

install_rpm:
	[ ! -z $(rpm_install_dir) ] || echo must run make rpm_install_dir=...
	mkdir -p $(rpm_install_dir)
	rsync -r $(RPMDIR)/RPMS $(rpm_install_dir)
	rsync -r $(RPMDIR)/SRPMS $(rpm_install_dir)
