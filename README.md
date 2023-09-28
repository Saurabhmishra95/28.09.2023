CIAM User Portal Services (API) Build Project
==============================

Description
-----------
This repository is for developing the associated custom API objects we are going to need to support the user portal and other services in the environment.  This will be the base application along with docker build project for deploying.

Getting Started
---------------
To build and test this project locally, these dependencies are needed
* docker
* Maven
* Make

To build the docker image you will first need to create an API key for Artifactory. 
Follow the instructions on this confluence article:
[Artifactory: Guide to Authenticating](https://confluence.experianhealth.com/display/DVOPS/Artifactory%3A+Guide+to+Authenticating)

Then set an environment variable ARTIFACTORY_APIKEY with this value. This setting is needed for `docker login`
to sign-in to artifactory. Without it, attempt to build the docker image will fail.

Building and Running the Services
---------------------------------
* `make build` - will build, test, and package the services jar and then build the docker image
* `make start` - will build and start the docker services container
* `make stop` - will stop running containers
* `make clean` - will stop and remove andy running containers and remove built artifacts

**Ensure `make build` is successful before merging any changes**

Dependencies
------------
The Portal Service is dependent upon these subsystems:
* ForgeRock AccessManagement (AM) `ciam-fr-am-build`
* ForgeRock Identity Management (IDM/IM) `ciam-fr-im-build`

To test the service, these dependent services need to be available and referenced using these ENV variables:
* `IDM_BASEURL` 
* 'SCIM_BASE_URL'

Directory Structure
-------------------

- `.gitignore`  - update the .gitigore file to keep only source files and not build files 
- `README.md` - update this README with any information developers need to know when coding, building, testing within this repo
- `/src/` - location of all source code
- `/acceptance-tests` -- automated API acceptance and integration tests. See `./acceptance-tests/README.md`
- `Timfile` - build file for Bamboo
- `pom.xml` - build specs for Maven (compile, build, test, and package the jar file)
- `Makefile` - used to build and run the docker image
- `docker-compose.yml` - used for building and launching the docker image
- 

## Testing Procedures 

* create or update Junit tests for any Java class that is introduced or modified
* Add new Feature files and/or Scenarios to test the API with a running server.
* Tag each Scenario with the Jira ticket number that is affected by your change
* ensure that all new and existing acceptance-tests pass with no errors before creating a Pull Request
* by running `make test` from the `acceptance-tests/` directory
