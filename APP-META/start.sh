#!/bin/sh
set -eo pipefail

/usr/bin/java ${JAVA_OPTS} -jar /usr/src/app/${APP_NAME}.jar --spring.profiles.active=${STAGE}
