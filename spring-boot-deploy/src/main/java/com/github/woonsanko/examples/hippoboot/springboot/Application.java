package com.github.woonsanko.examples.hippoboot.springboot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Configuration
@RestController
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Autowired
    private CatalinaConfiguration catalinaConfiguration;

    @RequestMapping("/echo")
    public String getEcho(@RequestParam(name = "message", required = false) String message) {
        return message;
    }

    @Bean
    public EmbeddedServletContainerFactory servletContainerFactory() {
        return new MultiWarTomcatEmbeddedServletContainerFactory(catalinaConfiguration);
    }

}
