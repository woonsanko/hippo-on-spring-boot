@echo off

java -Xms512m -Xmx1024m -Xdebug -Xrunjdwp:server=y,transport=dt_socket,address=8000,suspend=n -Drepo.path="repository" -Drepo.boostrap=true -jar target\hippo-on-spring-boot-spring-boot-deploy-0.1.0-SNAPSHOT.jar
