/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.woonsanko.examples.hippoboot.springboot;

import org.onehippo.forge.hipshoot.spring.boot.support.AppsDeployingTomcatEmbeddedServletContainerFactory;
import org.onehippo.forge.hipshoot.spring.boot.support.config.embedded.CatalinaConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * An example Spring Boot application with an extended {@link TomcatEmbeddedServletContainerFactory},
 * {@link AppsDeployingTomcatEmbeddedServletContainerFactory}, in order to deploy additional wars packaged in
 * the jar file under <code>classpath:META-INF/hipshoot/embedded-catalina/webapps/</code>.
 * @see {@link AppsDeployingTomcatEmbeddedServletContainerFactory}
 */
@Configuration
@SpringBootApplication
@ComponentScan(basePackages = { "org.onehippo.forge.hipshoot.spring.boot.support.config.embedded" })
public class Application {

    private static Logger log = LoggerFactory.getLogger(Application.class);

    /**
     * Just typical, simple main of a Spring Boot application.
     * @param args
     */
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    /**
     * Embedded tomcat configuration.
     */
    @Autowired
    private CatalinaConfiguration catalinaConfiguration;

    /**
     * Customize the {@link EmbeddedServletContainerFactory} bean.
     * Produce an {@link AppsDeployingTomcatEmbeddedServletContainerFactory} instance instead of the default
     * {@link TomcatEmbeddedServletContainerFactory} in order to be able to deploy additional wars packaged in the jar
     * at <code>classpath:META-INF/hipshoot/embedded-catalina/webapps/</code>.
     * @return
     * @see {@link AppsDeployingTomcatEmbeddedServletContainerFactory}
     * @see <a href="https://docs.spring.io/spring-boot/docs/current/reference/html/howto-embedded-servlet-containers.html">https://docs.spring.io/spring-boot/docs/current/reference/html/howto-embedded-servlet-containers.html</a>
     */
    @Bean
    public EmbeddedServletContainerFactory servletContainerFactory() {
        AppsDeployingTomcatEmbeddedServletContainerFactory factory = new AppsDeployingTomcatEmbeddedServletContainerFactory(
                catalinaConfiguration);
        // change the default root context path to /boot (helpful when deploying SITE as ROOT.war).
        //factory.setContextPath("/boot");
        return factory;
    }

}
