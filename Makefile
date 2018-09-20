# Makefile for zookeeper-authorizers RPM.

PWD=$(shell pwd)

BLD=$(PWD)/build
RPMDIR=$(BLD)/rpms
JARCACHE=$(BLD)/m2/repository

VERSION=$(shell grep version build.gradle | cut -d "'" -f 2)
RELEASE=1

clean:
	rm -rf $(BLD)

upload_jarcache: 
	[ ! -z $(jarcache_dir) ] || echo must run make upload_jarcache jarcache_dir=... 
	aws s3 sync $(jarcache_dir) $(S3_JAR_CACHE) 

sync_jarcache: 
	aws s3 sync $(S3_JAR_CACHE) $(JARCACHE) 

install: 
	GRADLE_OPTS="-Dmaven.repo.local=$(JARCACHE)" bash gradlew Jar

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
