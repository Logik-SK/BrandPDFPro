package com.brandpdfpro.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Service responsible for managing user-modifiable application preferences.
 */
public class SettingsService {

    private static final Logger logger = LoggerFactory.getLogger(SettingsService.class);

    private static final String CONFIG_DIR = "config";
    private static final String SETTINGS_FILE = "settings.properties";

    private static final String HEADER_HEIGHT = "header.height";
    private static final String FOOTER_HEIGHT = "footer.height";
    private static final String ADD_PAGE_NUMBERS = "add.page.numbers";
    private static final String COMPANY_NAME = "company.name";

    private final Properties properties = new Properties();

    public SettingsService() {
        initializeSettings();
    }

    /**
     * Prepares configuration locations on disk and handles runtime initialization.
     */
    private void initializeSettings() {
        logger.info("Initializing user-modifiable application preferences.");
        try {
            createConfigDirectory();
            File settingsFile = getSettingsFile();

            if (!settingsFile.exists()) {
                createDefaultSettings(settingsFile);
            }
            loadSettings();
        } catch (IOException ex) {
            logger.error("Fatal failure initializing system settings environment: {}", ex.getMessage(), ex);
            throw new RuntimeException("Unable to initialize settings.", ex);
        }
    }

    /**
     * Creates the configuration directory if it does not exist.
     */
    private void createConfigDirectory() {
        File directory = new File(CONFIG_DIR);
        if (!directory.exists()) {
            logger.info("Settings registry directory absent. Creating path context at: /{}", CONFIG_DIR);
            directory.mkdirs();
        }
    }

    private File getSettingsFile() {
        return new File(CONFIG_DIR, SETTINGS_FILE);
    }

    /**
     * Seeds default preference metrics into a fresh properties file schema layout.
     */
    private void createDefaultSettings(File settingsFile) throws IOException {
        logger.info("Generating standard default user preferences profile layout.");
        Properties defaults = new Properties();
        defaults.setProperty(HEADER_HEIGHT, "80");
        defaults.setProperty(FOOTER_HEIGHT, "80");
        defaults.setProperty(ADD_PAGE_NUMBERS, "true");
        defaults.setProperty(COMPANY_NAME, "BrandPDFPro");

        try (FileOutputStream outputStream = new FileOutputStream(settingsFile)) {
            defaults.store(outputStream, "BrandPDF Pro Settings");
            logger.info("Default preference fields successfully written down onto path: {}", settingsFile.getPath());
        }
    }

    /**
     * Streams and maps active parameters directly out of the settings file into memory.
     */
    public void loadSettings() throws IOException {
        File settingsFile = getSettingsFile();
        logger.info("Streaming user preferences attributes from storage entry: {}", settingsFile.getPath());
        try (FileInputStream inputStream = new FileInputStream(settingsFile)) {
            properties.load(inputStream);
            logger.info("Settings properties successfully parsed into memory caches.");
        }
    }

    /**
     * Flushes current memory-mapped property definitions back down onto the system file layers.
     */
    public void saveSettings() throws IOException {
        File settingsFile = getSettingsFile();
        logger.info("Flushing runtime preference modifications back down to path: {}", settingsFile.getPath());
        try (FileOutputStream outputStream = new FileOutputStream(settingsFile)) {
            properties.store(outputStream, "BrandPDF Pro Settings");
            logger.info("Global application properties cache committed successfully.");
        }
    }

    // =========================================================================
    // Accessors & Mutators
    // =========================================================================

    public float getHeaderHeight() {
        return Float.parseFloat(properties.getProperty(HEADER_HEIGHT, "80"));
    }

    public float getFooterHeight() {
        return Float.parseFloat(properties.getProperty(FOOTER_HEIGHT, "80"));
    }

    public boolean isPageNumberEnabled() {
        return Boolean.parseBoolean(properties.getProperty(ADD_PAGE_NUMBERS, "true"));
    }

    public String getCompanyName() {
        return properties.getProperty(COMPANY_NAME, "BrandPDFPro");
    }

    public void setHeaderHeight(float value) {
        properties.setProperty(HEADER_HEIGHT, String.valueOf(value));
    }

    public void setFooterHeight(float value) {
        properties.setProperty(FOOTER_HEIGHT, String.valueOf(value));
    }

    public void setPageNumberEnabled(boolean value) {
        properties.setProperty(ADD_PAGE_NUMBERS, String.valueOf(value));
    }

    public void setCompanyName(String value) {
        properties.setProperty(COMPANY_NAME, String.valueOf(value));
    }
}