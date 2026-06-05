package com.brandpdfpro.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class AppConfigService {

    private static final String CONFIG_DIR = "config";
    private static final String APPLICATION_FILE = "application.properties";

    private final Properties properties = new Properties();

    public AppConfigService() {
        initializeConfig();
    }

    private void initializeConfig() {
        try {
            createConfigDirectory();

            File applicationFile = getApplicationFile();
            if (!applicationFile.exists()) {
                createDefaultApplicationConfig(applicationFile);
            }

            loadProperties();
        } catch (IOException ex) {
            throw new RuntimeException("Unable to initialize application configuration.", ex);
        }
    }

    private void createConfigDirectory() {
        File directory = new File(CONFIG_DIR);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    private File getApplicationFile() {
        return new File(CONFIG_DIR, APPLICATION_FILE);
    }

    private void createDefaultApplicationConfig(File applicationFile) throws IOException {
        Properties defaults = new Properties();

        defaults.setProperty("app.name", "BrandPDF Pro");
        defaults.setProperty("app.version", "1.1.0");
        defaults.setProperty("app.width", "950");
        defaults.setProperty("app.height", "950");
        defaults.setProperty("preview.width", "400");
        defaults.setProperty("preview.height", "80");
        defaults.setProperty("default.header.height", "80");
        defaults.setProperty("default.footer.height", "80");

        try (FileOutputStream outputStream = new FileOutputStream(applicationFile)) {
            defaults.store(outputStream, "BrandPDF Pro Application Configuration");
        }
    }

    private void loadProperties() throws IOException {
        try (FileInputStream inputStream = new FileInputStream(getApplicationFile())) {
            properties.load(inputStream);
        }
    }

    // ==================================================
    // Application Information
    // ==================================================

    public String getAppName() {
        return properties.getProperty("app.name", "BrandPDF Pro");
    }

    public String getAppVersion() {
        return properties.getProperty("app.version", "1.1.0");
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