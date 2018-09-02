#!/bin/sh
set -eo pipefail

/usr/bin/java ${JAVA_OPTS} -Xms${MAX_MEMORY} -Xmx${MAX_MEMORY} -jar /usr/src/app/${APP_NAME}.jar --spring.profiles.active=${STAGE}
