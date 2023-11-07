#!/bin/bash

CURRENT_PATH="$(dirname "$0")"
source "$CURRENT_PATH/migrations/migrate_keycloak_vars.sh"

/opt/keycloak/bin/kc.sh $STARTUP_COMMAND &
until $(curl --output /dev/null --silent --head --fail $BASE_URL/realms/master); do
    sleep 3
done
/opt/keycloak/tools/migrations/migrate_keycloak.sh
sleep infinity
