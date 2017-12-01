FROM java:8-jre
VOLUME /tmp
MAINTAINER Alexander Muravya (aka kyberorg) <asm@virtalab.net>
ADD ./target/yals.jar /app/
COPY ./COMMIT /app/
RUN sh -c 'touch /app/yals.jar'
RUN sh -c 'cat /app/COMMIT'
ENV JAVA_OPTS=""
ENTRYPOINT exec java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app/yals.jar
EXPOSE 8080