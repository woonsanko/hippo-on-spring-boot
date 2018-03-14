# How to use hipshoot Spring Boot Support library in your project

This project uses [hipshoot Spring Boot Support library](https://onehippo-forge.github.io/hipshoot/hipshoot-spring-boot-support/index.html)
to wrap a Hippo project into a Spring Boot application package.

Here's step by step guide to apply the module to your Hippo CMS project.

## Step 1. Create **spring-boot-deploy** Maven submodule and Copy files from this demo

Create ```spring-boot-deploy``` Maven submodule in your project like [spring-boot-deploy](spring-boot-deploy).

As [spring-boot-deploy](spring-boot-deploy) submodule consists of very generic configurations, so you can simply copy all the files from this demo project first.
And you can change only some parts to be your project specific afterward, as explained below.

#### Step 1.1. Update the parent in [spring-boot-deploy/pom.xml](spring-boot-deploy/pom.xml) of your project.

#### Step 1.2. Update project informations such as name, descrption, etc. in [spring-boot-deploy/pom.xml](spring-boot-deploy/pom.xml) as well.

#### Step 1.3. Add Spring Framework dependencies in your project root pom.xml like what's done in [pom.xml](pom.xml) in the demo project.

After "Override Spring Framework Dependencies to ..." XML comment.

#### Step 1.4. Update the application name in [spring-boot-deploy/manifest.yml](spring-boot-deploy/manifest.yml) of your project if you want to push to PCF.

#### Step 1.5. Update [spring-boot-deploy/test.sh](spring-boot-deploy/test.sh) and [spring-boot-deploy/test.bat](spring-boot-deploy/test.bat) with the proper JAR artifact file name if you want to use the scripts.

#### Step 1.6. You can customize more in [spring-boot-deploy/src/main/java/com/github/woonsanko/examples/hippoboot/springboot/Application.java](spring-boot-deploy/src/main/java/com/github/woonsanko/examples/hippoboot/springboot/Application.java) if you want.

#### Step 1.7. You can customize more in [spring-boot-deploy/src/main/resources/application.yml](spring-boot-deploy/src/main/resources/application.yml) if you want.

## Step 2. Build and Run

Just do the same thing as explained in [README.md](README.md).
