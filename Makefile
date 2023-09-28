export IDM_BASEURL?=https://ehfops.idmworks.net
export AM_BASEURL?=https://ehfops.idmworks.net
export PORTAL_CLIENT_ID?=scim-client
export PORTAL_CLIENT_SECRET=1Password

login:
	docker login --username ${USER} --password ${ARTIFACTORY_APIKEY} https://artifactory.experianhealth.com/
build:
	mvn package
	docker-compose build
start: build
	docker-compose up -d
	docker-compose ps
stop:
	docker-compose stop
clean:
	docker-compose down
	mvn clean
