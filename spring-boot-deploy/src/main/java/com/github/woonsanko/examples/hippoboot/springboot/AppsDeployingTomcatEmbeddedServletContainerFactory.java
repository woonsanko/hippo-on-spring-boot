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
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;

public class AppsDeployingTomcatEmbeddedServletContainerFactory extends TomcatEmbeddedServletContainerFactory {

    private static Logger log = LoggerFactory.getLogger(AppsDeployingTomcatEmbeddedServletContainerFactory.class);

    private File appBaseDirectory;

    public AppsDeployingTomcatEmbeddedServletContainerFactory(EmbeddedCatalinaConfiguration embeddedCatalinaConfiguration) {
        super();

        final String base = embeddedCatalinaConfiguration.getBase();

        if (base != null && !base.isEmpty()) {
            File baseDir = new File(base);

            if (!baseDir.isDirectory()) {
                baseDir.mkdirs();
            }

            setBaseDirectory(baseDir);
        }

        final String appBase = embeddedCatalinaConfiguration.getAppBase();

        if (appBase != null && !appBase.isEmpty()) {
            File appBaseDir = new File(appBase);

            if (appBaseDir.isDirectory()) {
                log.info("Embedded catalog appBase: {}", appBaseDir.getAbsolutePath());
                setAppBaseDirectory(appBaseDir);
            }
        }
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

    protected File getAppBaseDirectory() {
        return appBaseDirectory;
    }

    protected void setAppBaseDirectory(File appBaseDirectory) {
        this.appBaseDirectory = appBaseDirectory;
    }

    private Map<String, String> getWebappPathsMap() {
        Map<String, String> webappPathsMap = null;

        if (getAppBaseDirectory() != null) {
            webappPathsMap = new LinkedHashMap<>();
            File webappsDir = getAppBaseDirectory();

            if (webappsDir.isDirectory()) {
                String fileName;
                String baseFileName;
                String contextPath;
                String basePath;

                for (File file : webappsDir.listFiles()) {
                    fileName = file.getName();
                    int offset = fileName.lastIndexOf('.');
                    baseFileName = (offset != -1) ? fileName.substring(0, offset) : fileName;

                    if ("ROOT".equals(baseFileName)) {
                        contextPath = "";
                    } else {
                        contextPath = "/" + baseFileName;
                    }

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
