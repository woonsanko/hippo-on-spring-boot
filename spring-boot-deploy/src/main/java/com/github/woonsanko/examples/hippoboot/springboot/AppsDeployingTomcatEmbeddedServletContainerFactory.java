package com.github.woonsanko.examples.hippoboot.springboot;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
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
import org.springframework.util.StringUtils;

public class AppsDeployingTomcatEmbeddedServletContainerFactory extends TomcatEmbeddedServletContainerFactory {

    private static Logger log = LoggerFactory.getLogger(AppsDeployingTomcatEmbeddedServletContainerFactory.class);

    private static final String EMBEDDED_CATALINA_WEBAPPS_PATH = "META-INF/embedded-catalina/webapps";

    private static final int BUFFER_SIZE = 4096;

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

            if (!appBaseDir.isDirectory()) {
                appBaseDir.mkdirs();
            }

            log.info("Embedded catalog appBase: {}", appBaseDir.getAbsolutePath());
            setAppBaseDirectory(appBaseDir);

            extractEmbeddedWars(embeddedCatalinaConfiguration);
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

    private void extractEmbeddedWars(EmbeddedCatalinaConfiguration embeddedCatalinaConfiguration) {
        final String wars = embeddedCatalinaConfiguration.getWars();

        if (wars != null && !wars.isEmpty()) {
            String [] tokens = StringUtils.tokenizeToStringArray(wars, ",");
            String warName;

            for (String token : tokens) {
                warName = token.trim();

                if (!warName.isEmpty()) {
                    URL warRes = Thread.currentThread().getContextClassLoader()
                            .getResource(EMBEDDED_CATALINA_WEBAPPS_PATH + "/" + warName);

                    if (warRes != null) {
                        InputStream is = null;
                        BufferedInputStream bis = null;
                        OutputStream os = null;
                        BufferedOutputStream bos = null;

                        try {
                            is = warRes.openStream();
                            bis = new BufferedInputStream(is);
                            os = new FileOutputStream(new File(getAppBaseDirectory(), warName));
                            bos = new BufferedOutputStream(os);
                            copy(bis, bos);
                            log.info("Deployed embedded war, {}.", warName);
                        } catch (IOException e) {
                            log.error("Failed to copy {} to {}.", warName, getAppBaseDirectory(), e);
                        } finally {
                            closeQuietly(bos);
                            closeQuietly(os);
                            closeQuietly(bis);
                            closeQuietly(is);
                        }
                    }
                }
            }
        }
    }

    private void copy(InputStream is, OutputStream os) throws IOException {
        byte [] buffer = new byte[BUFFER_SIZE];
        int readLen = is.read(buffer, 0, BUFFER_SIZE);

        while (readLen != -1) {
            os.write(buffer, 0, readLen);
            readLen = is.read(buffer, 0, BUFFER_SIZE);
        }
    }

    private void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException ignore) {
            }
        }
    }
}
