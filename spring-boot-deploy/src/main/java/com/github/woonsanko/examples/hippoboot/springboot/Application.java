package com.github.woonsanko.examples.hippoboot.springboot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Autowired
    private EmbeddedCatalinaConfiguration embeddedCatalinaConfiguration;

    @Bean
    public EmbeddedServletContainerFactory servletContainerFactory() {
        TomcatEmbeddedServletContainerFactory factory =
                new AppsDeployingTomcatEmbeddedServletContainerFactory(embeddedCatalinaConfiguration);
        // change the default root context path to /boot (helpful when deploying SITE as ROOT.war).
        factory.setContextPath("/boot");
        return factory;
    }
}
