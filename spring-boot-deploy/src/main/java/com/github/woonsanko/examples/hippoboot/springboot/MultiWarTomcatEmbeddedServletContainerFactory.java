package com.github.woonsanko.examples.hippoboot.springboot;

import java.io.File;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.ServletException;

import org.apache.catalina.Context;
import org.apache.catalina.loader.WebappLoader;
import org.apache.catalina.startup.Tomcat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;

public class MultiWarTomcatEmbeddedServletContainerFactory extends TomcatEmbeddedServletContainerFactory {

    private static Logger log = LoggerFactory.getLogger(MultiWarTomcatEmbeddedServletContainerFactory.class);

    private File catalinaBaseDirectory;

    @Autowired
    public MultiWarTomcatEmbeddedServletContainerFactory(CatalinaConfiguration catalinaConfiguration) {
        super();

        final String base = catalinaConfiguration.getBase();

        if (base != null && !base.isEmpty()) {
            catalinaBaseDirectory = new File(base);
            catalinaBaseDirectory.mkdirs();
            log.info("Embedded catalog base directory: {}", catalinaBaseDirectory.getAbsolutePath());
            setBaseDirectory(catalinaBaseDirectory);
        }
    }

    protected File getBaseDirectory() {
        return catalinaBaseDirectory;
    }

    protected TomcatEmbeddedServletContainer getTomcatEmbeddedServletContainer(Tomcat tomcat) {
        try {
            String contextPath;
            String basePath;

            for (Map.Entry<String, String> entry : getWebappPathsMap().entrySet()) {
                contextPath = entry.getKey();
                basePath = entry.getValue();
                Context context = tomcat.addWebapp(contextPath, basePath);
                WebappLoader webappLoader = new WebappLoader(Thread.currentThread().getContextClassLoader());
                context.setLoader(webappLoader);
            }
        } catch (ServletException ex) {
            throw new IllegalStateException("Failed to add webapp", ex);
        }

        return super.getTomcatEmbeddedServletContainer(tomcat);
    }

    private Map<String, String> getWebappPathsMap() {
        Map<String, String> webappPathsMap = null;

        if (getBaseDirectory() != null) {
            webappPathsMap = new LinkedHashMap<>();
            File webappsDir = new File(getBaseDirectory(), "webapps");

            if (webappsDir.isDirectory()) {
                String contextPath;
                String basePath;

                for (File file : webappsDir.listFiles()) {
                    contextPath = "/" + file.getName();
                    basePath = file.getAbsolutePath();

                    if (file.isDirectory() || (file.isFile() && file.getName().endsWith(".war"))) {
                        webappPathsMap.put(contextPath, basePath);
                    }
                }
            }
        }

        if (webappPathsMap == null) {
            return Collections.emptyMap();
        }

        return webappPathsMap;
    }
}
