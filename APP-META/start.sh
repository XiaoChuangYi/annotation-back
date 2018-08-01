#!/bin/sh
set -eo pipefail

/usr/bin/java ${JAVA_OPTS} -Xmx${MAX_MEMORY} -jar /usr/src/app/${APP_NAME}.jar --spring.profiles.active=${STAGE}
