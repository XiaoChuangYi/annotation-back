FROM openjdk:8-jre-alpine
MAINTAINER Jack Feng <feng.ji@malgo.cn>

RUN mkdir -p /usr/src/app
WORKDIR /usr/src/app

ADD APP-META/start.sh /usr/src/app/

ENV APP_NAME annotation-service

ARG STAGE
ENV STAGE ${STAGE:-prod}

ENV JAVA_OPTS "-server -Xmx2g -Xms1g -Xmn512m -Xss256k -XX:+DisableExplicitGC  -XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled -XX:LargePageSizeInBytes=128m -XX:+UseFastAccessorMethods -XX:+UseCMSInitiatingOccupancyOnly -XX:CMSInitiatingOccupancyFraction=70 -Duser.timezone=GMT+8"

ARG JAR_FILE
ADD target/${JAR_FILE} /usr/src/app/${APP_NAME}.jar

CMD ["/usr/src/app/start.sh"]
