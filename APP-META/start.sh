#!/bin/sh
set -eo pipefail

/usr/bin/java ${JAVA_OPTS} -XX:NewRatio=${MEMORY_NEW_RATIO} -Xms${MAX_MEMORY} -Xmx${MAX_MEMORY} -jar /usr/src/app/${APP_NAME}.jar --spring.profiles.active=${STAGE}
ß