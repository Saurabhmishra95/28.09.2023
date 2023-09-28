FROM artifactory.experianhealth.com/devops/eh-alpine-openjdk:v17
LABEL maintainer=SharedServicesDev@experianhealth.com
EXPOSE 8080
ENV JAVA_OPTS -XX:MaxRAMPercentage=80
COPY target/scim-api-*.jar scimapi-docker.jar
CMD [ "java", "-jar", "scimapi-docker.jar" ]

