package com.brandpdfpro.service;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class AppConfigService {

    private static final String CONFIG_FILE = "config/application.properties";
    private final Properties properties = new Properties();

    public AppConfigService() {
        loadProperties();
    }

    private void loadProperties() {
        try (FileInputStream inputStream = new FileInputStream(CONFIG_FILE)) {
            properties.load(inputStream);
        } catch (IOException ex) {
            throw new RuntimeException("Unable to load application configuration.", ex);
        }
    }

    // ==================================================
    // Application Information
    // ==================================================

    public String getAppName() {
        return properties.getProperty("app.name", "BrandPDF Pro");
    }

    public String getAppVersion() {
        return properties.getProperty("app.version", "1.0.0");
    }

    public String getAppTitle() {
        return getAppName() + " v" + getAppVersion();
    }

    // ==================================================
    // Window Settings
    // ==================================================

    public double getAppWidth() {
        return Double.parseDouble(properties.getProperty("app.width", "950"));
    }

    public double getAppHeight() {
        return Double.parseDouble(properties.getProperty("app.height", "950"));
    }

    // ==================================================
    // Preview Settings
    // ==================================================

    public double getPreviewWidth() {
        return Double.parseDouble(properties.getProperty("preview.width", "400"));
    }

    public double getPreviewHeight() {
        return Double.parseDouble(properties.getProperty("preview.height", "80"));
    }

    // ==================================================
    // Default PDF Settings
    // ==================================================

    public float getDefaultHeaderHeight() {
        return Float.parseFloat(properties.getProperty("default.header.height", "80"));
    }

    public float getDefaultFooterHeight() {
        return Float.parseFloat(properties.getProperty("default.footer.height", "80"));
    }
}