package com.brandpdfpro.service;

import com.brandpdfpro.controller.MainController;
import com.brandpdfpro.util.AppConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Service responsible for managing global application configurations.
 */
public class AppConfigService {

    private static final Logger logger = LoggerFactory.getLogger(AppConfigService.class);

    private static final String CONFIG_DIR = "config";
    private static final String APPLICATION_FILE = "application.properties";

    private final Properties properties = new Properties();

    public AppConfigService() {
        initializeConfig();
    }

    /**
     * Orchestrates the initialization lifecycle for application properties.
     */
    private void initializeConfig() {
        logger.info("Initializing application configuration environment.");
        try {
            createConfigDirectory();

            File applicationFile = getApplicationFile();
            if (!applicationFile.exists()) {
                createDefaultApplicationConfig(applicationFile);
            }

            loadProperties();
        } catch (IOException ex) {
            logger.error("Fatal failure preparing application properties layout: {}", ex.getMessage(), ex);
            throw new RuntimeException("Unable to initialize application configuration.", ex);
        }
    }

    /**
     * Creates the configuration storage directory if it does not exist.
     */
    private void createConfigDirectory() {
        File directory = new File(CONFIG_DIR);
        if (!directory.exists()) {
            logger.info("Configuration registry directory absent. Creating directory context at: /{}", CONFIG_DIR);
            directory.mkdirs();
        }
    }

    private File getApplicationFile() {
        return new File(CONFIG_DIR, APPLICATION_FILE);
    }

    /**
     * Generates a fallback properties file populated with template system records.
     */
    private void createDefaultApplicationConfig(File applicationFile) throws IOException {
        logger.info("Generating standard fallback configuration properties profile.");
        Properties defaults = new Properties();

        defaults.setProperty("app.name", AppConstants.APP_NAME);
        defaults.setProperty("app.version", AppConstants.APP_VERSION);
        defaults.setProperty("app.width", "950");
        defaults.setProperty("app.height", "750");
        defaults.setProperty("preview.width", "400");
        defaults.setProperty("preview.height", "80");
        defaults.setProperty("default.header.height", "80");
        defaults.setProperty("default.footer.height", "80");

        try (FileOutputStream outputStream = new FileOutputStream(applicationFile)) {
            defaults.store(outputStream, "BrandPDF Pro Application Configuration");
            logger.info("Persistent layout configurations flushed down onto path: {}", applicationFile.getPath());
        }
    }

    /**
     * Streams local property file keys directly into the runtime memory cache.
     */
    private void loadProperties() throws IOException {
        File appFile = getApplicationFile();
        logger.info("Streaming physical key-value properties from storage entry target: {}", appFile.getPath());
        try (FileInputStream inputStream = new FileInputStream(appFile)) {
            properties.load(inputStream);
            logger.info("Configuration registry attributes loaded into system cache successfully.");
        }
    }

    // =========================================================================
    // Application Information
    // =========================================================================

    public String getAppName() {
        return properties.getProperty("app.name", AppConstants.APP_NAME);
    }

    public String getAppVersion() {
        return properties.getProperty("app.version", AppConstants.APP_VERSION);
    }

    public String getAppTitle() {
        return getAppName() + " v" + getAppVersion();
    }

    // =========================================================================
    // Window Settings
    // =========================================================================

    public double getAppWidth() {
        return Double.parseDouble(properties.getProperty("app.width", "950"));
    }

    public double getAppHeight() {
        return Double.parseDouble(properties.getProperty("app.height", "750"));
    }

    // =========================================================================
    // Preview Settings
    // =========================================================================

    public double getPreviewWidth() {
        return Double.parseDouble(properties.getProperty("preview.width", "400"));
    }

    public double getPreviewHeight() {
        return Double.parseDouble(properties.getProperty("preview.height", "80"));
    }

    // =========================================================================
    // Default PDF Settings
    // =========================================================================

    public float getDefaultHeaderHeight() {
        return Float.parseFloat(properties.getProperty("default.header.height", "80"));
    }

    public float getDefaultFooterHeight() {
        return Float.parseFloat(properties.getProperty("default.footer.height", "80"));
    }
}