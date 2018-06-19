# Makefile for zookeeper-ldap-roles RPM.

PWD=$(shell pwd)

BLD=$(PWD)/build
RPMDIR=$(BLD)/rpms

REPO_TAG=$(shell git describe --tags HEAD --long)
REPO_TAGFMT=$(shell echo ${REPO_TAG} | sed 's/-g/-/')
REPO_VER=$(shell echo ${REPO_TAGFMT} | cut -d '-' -f 1)
REPO_REL=$(shell echo ${REPO_TAGFMT} | cut -d '-' -f 2,3 | sed 's/-/_/')

clean:
	rm -rf $(BLD)

install: 
	bash gradlew Jar

rpm:
	mkdir -p $(RPMDIR)/SOURCES
	mkdir -p $(RPMDIR)/SRPMS
	mkdir -p $(RPMDIR)/RPMS
	mkdir -p $(RPMDIR)/BUILD
	mkdir -p $(RPMDIR)/BUILDROOT
	cp build/libs/zookeeper-ldap-roles.jar $(RPMDIR)/BUILD/
	rpmbuild -bb                                                     \
		-D "_topdir $(RPMDIR)"                                       \
		-D "_version $(REPO_VER)"                             \
		-D "_release $(REPO_REL)"                             \
		zookeeper-ldap-roles.spec

install_rpm:
	[ ! -z $(rpm_install_dir) ] || echo must run make rpm_install_dir=...
	mkdir -p $(rpm_install_dir)
	rsync -r $(RPMDIR)/RPMS $(rpm_install_dir)
	rsync -r $(RPMDIR)/SRPMS $(rpm_install_dir)
