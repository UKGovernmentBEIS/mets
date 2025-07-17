# UK METS API application

The UK METS API is a Java(SpringBoot) application.

## Structure

## Running the application

You can run the Spring Boot application by typing:

    $ mvn clean spring-boot:run
	
or

    $ ./_runme.sh

You can then access the final jar file that contains the API here :

    uk-pmrv-app-api\target

For the build to succeed uk-pmrv-swagger-coverage-maven-plugin must have been built prior to building the UK METS API application 

### NOTE: Some mandatory properties need to be set in the local environment in order for deployment to succeed: 
https://pmo.trasys.be/confluence/display/PMRV/Initialize+environment

## REST API Documentation

The API is documented using Swagger 3.

After running the application, the documentation is available here:

- http://localhost:8080/api/swagger-ui/index.html (UI)
- http://localhost:8080/api/v3/api-docs (JSON)

### Actuator

Actuator can be accessed in:

```
http://localhost:8080/actuator
```

Note that the actuator is not secured by default because it is not meant to be
exposed to the public internet but only be accessible from the internal
network.

### Feature flags

Feature flag feature-flag.disabledWorkflows for disabling workflows has been implemented and can take as a value comma-separated workflows(RequestType) that need to be disabled. (Only user initiated workflows are taken under consideration)

### Logging

By default, logging to json format is configured through log4j2-json.xml but default console logging can be chosen by setting LOG4J2_CONFIG_FILE env var to log4j2-local.xml.

Unauthenticated API calls are not logged(RestLoggingFilter is applied after security filters in order to be able to inject user related info in authenticated API calls) so explicit logging should be added for these calls. 

## UK METS Camunda admin

### REST API

Camunda rest is used to manage camunda processes. It is unauthenticated and can be accessed at /api/admin/camunda-api.

Documentation can be found at
- https://docs.camunda.org/manual/latest/reference/rest/

### WEB APP

Camunda webapp consists of 3 different web apps:
- cockpit: an administration interface for processes and decisions
- tasklist: provides an interface to process user tasks
- admin: is used to administer users, groups and their authorizations

It is authenticated through keycloak's master realm and can be accessed at /api/admin/camunda-web.

Documentation can be found at
- https://camunda.com/platform-7/cockpit/
- https://camunda.com/platform/tasklist/
- https://github.com/camunda/camunda-bpm-platform/tree/master/webapps

### How to execute an AER workflow manually

```
curl --location --request POST 'http://localhost:8080/api/admin/camunda-api/process-definition/key/PROCESS_AER_INITIATE/start' \
--header 'Content-Type: application/json' \
--data-raw '{
    "variables": {
        "accountIds": {
            "value": ["account-id-1", "account-id-2"]
        }
    }
}'

```
