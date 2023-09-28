TAGS?="not @Ignore"
export AM_BASEURL?=https://ehfops.idmworks.net
export SCIM_BASEURL?=http://localhost:8089
export AT_LOGALL?=false

ifeq ($(FEATURE),)
else
	FEATURE_OPT="-Dcucumber.features=src/test/resources/features/$(FEATURE)"
endif
clean:
	mvn clean
test:
	mvn test -Dcucumber.publish.quiet=true -Dcucumber.filter.tags=$(TAGS) $(FEATURE_OPT)
