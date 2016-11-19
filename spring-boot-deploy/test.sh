#!/bin/sh

EMBEDDED_CATALINA_BASE=target/embedded-tomcat

mkdir $EMBEDDED_CATALINA_BASE
mkdir $EMBEDDED_CATALINA_BASE/webapps
cp -R ../cms/target/cms $EMBEDDED_CATALINA_BASE/webapps/
cp -R ../site/target/site $EMBEDDED_CATALINA_BASE/webapps/

java -Drepo.path="target/storage" \
    -jar target/hippo-on-spring-boot-spring-boot-deploy-0.1.0-SNAPSHOT.jar \
    --embedded.catalina.base=$EMBEDDED_CATALINA_BASE
