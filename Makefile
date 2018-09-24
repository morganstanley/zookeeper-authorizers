# Makefile for zookeeper-authorizers RPM.

# if proxy is needed, edit: .gradle/gradle.properties
#
# systemProp.http.proxyHost=$proxy_host
# systemProp.https.proxyHost=$proxy_host
# systemProp.http.proxyPort=$proxy_port
# systemProp.https.proxyPort=$proxy_port
#
# Makefile is designed to first build the cache, then use it for subsequent
# builds.
#
# For locked environments that by default do not have access to maven, it is
# possible to first build all dependencies in s3 and then use them via s3 sync
# for each build.
#
# The s3 bucket to be used as "shared" cache is controlled by environment
# variable: S3_JAR_CACHE.
#
# Typical usage:
#
#  - edit proxy settings and build local cache:
#  make build_cache
#
#  - upload to s3:
#  export S3_JAR_CACHE=...
#  make s3_cache
#
#  - jar and rpm using s3 cache or by downloading cache:
#  make install rpm

PWD=$(shell pwd)

BLD=$(PWD)/build
RPMDIR=$(BLD)/rpms
JARCACHE=~/.gradle/caches

VERSION=$(shell grep version build.gradle | cut -d "'" -f 2)
RELEASE=1

clean:
	rm -rf $(BLD)

clean_cache:
	rm -rf $(JARCACHE)

build_cache: clean_cache
	./gradlew jar --refresh-dependencies

s3_cache: build_cache
	aws s3 sync --delete $(JARCACHE) $(S3_JAR_CACHE) 

cache:
	@if [ "$(S3_JAR_CACHE)" == "" ];               \
		then ./gradlew jar --refresh-dependencies; \
	else                                           \
		echo using S3_JAR_CACHE=$(S3_JAR_CACHE);   \
		aws s3 sync --delete $(S3_JAR_CACHE) $(JARCACHE);   \
	fi

install: cache
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
		-D "_version $(VERSION)"                                     \
		-D "_release $(RELEASE)"                                     \
		zookeeper-authorizers.spec

install_rpm:
	[ ! -z $(rpm_install_dir) ] || echo must run make rpm_install_dir=...
	mkdir -p $(rpm_install_dir)
	rsync -r $(RPMDIR)/RPMS $(rpm_install_dir)
	rsync -r $(RPMDIR)/SRPMS $(rpm_install_dir)
