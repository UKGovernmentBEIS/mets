#!/bin/bash

#This script adds post.logout.redirect.uris for camunda-identity-service client

SCRIPT_NAME=$(basename -- "$0")

set -e
KEYCLOAK_ADMIN_ACCESS_TOKEN=$(getKeycloakAdminAccessToken)

UPDATE_CAMUNDA_IDENTITY_SERVICE_CLIENT_URL="$BASE_URL/admin/realms/master/clients/9d233e9a-ed07-4e83-b572-fd97aede8bdb"
CAMUNDA_IDENTITY_SERVICE_CLIENT=$(curl -s -L -X PUT "$UPDATE_CAMUNDA_IDENTITY_SERVICE_CLIENT_URL" \
-H 'Content-Type: application/json' \
-H "Authorization: Bearer $KEYCLOAK_ADMIN_ACCESS_TOKEN" \
--data-raw '
  {
    "attributes": {
    		"post.logout.redirect.uris": "'$PMRV_ADMIN_APP_URL'/*"
    	}
  }')

if [ -z "$CAMUNDA_IDENTITY_SERVICE_CLIENT" ]
then
	echo " $SCRIPT_NAME executed successfully"
else
	echo " $SCRIPT_NAME failed $CAMUNDA_IDENTITY_SERVICE_CLIENT"
fi

#Add script name as user to changelog realm for tracking purposes
ADD_SCRIPT_TO_CHANGELOG=$(addUserToChangeLogRealm "$SCRIPT_NAME")

if [ -z "$ADD_SCRIPT_TO_CHANGELOG" ]
then
	echo " Script $SCRIPT_NAME added to changelog"
else
	echo " Script $SCRIPT_NAME was not to added to changelog. Reason: $ADD_SCRIPT_TO_CHANGELOG"
fi