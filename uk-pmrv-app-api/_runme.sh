#!/bin/bash

docker-compose -p uk-pmrv-env-development up -d
mvn spring-boot:run