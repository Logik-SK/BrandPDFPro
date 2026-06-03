package com.brandpdfpro.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class SettingsService {

    private static final String CONFIG_DIR = "config";
    private static final String SETTINGS_FILE = "settings.properties";
    private static final String HEADER_HEIGHT = "header.height";
    private static final String FOOTER_HEIGHT = "footer.height";
    private static final String ADD_PAGE_NUMBERS = "add.page.numbers";

    private final Properties properties = new Properties();

    public SettingsService() {
        initializeSettings();
    }

    private void initializeSettings() {
        try {
            createConfigDirectory();
            File settingsFile = getSettingsFile();

            if (!settingsFile.exists()) {
                createDefaultSettings(settingsFile);
            }
            loadSettings();
        } catch (IOException ex) {
            throw new RuntimeException("Unable to initialize settings.", ex);
        }
    }

    private void createConfigDirectory() {
        File directory = new File(CONFIG_DIR);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    private File getSettingsFile() {
        return new File(CONFIG_DIR, SETTINGS_FILE);
    }

    private void createDefaultSettings(File settingsFile) throws IOException {
        Properties defaults = new Properties();
        defaults.setProperty(HEADER_HEIGHT, "80");
        defaults.setProperty(FOOTER_HEIGHT, "80");
        defaults.setProperty(ADD_PAGE_NUMBERS, "true");

        try (FileOutputStream outputStream = new FileOutputStream(settingsFile)) {
            defaults.store(outputStream, "BrandPDF Pro Settings");
        }
    }

    public void loadSettings() throws IOException {
        try (FileInputStream inputStream = new FileInputStream(getSettingsFile())) {
            properties.load(inputStream);
        }
    }

    public void saveSettings() throws IOException {
        try (FileOutputStream outputStream = new FileOutputStream(getSettingsFile())) {
            properties.store(outputStream, "BrandPDF Pro Settings");
        }
    }

    public float getHeaderHeight() {
        return Float.parseFloat(properties.getProperty(HEADER_HEIGHT, "80"));
    }

    public float getFooterHeight() {
        return Float.parseFloat(properties.getProperty(FOOTER_HEIGHT, "80"));
    }

    public boolean isPageNumberEnabled() {
        return Boolean.parseBoolean(properties.getProperty(ADD_PAGE_NUMBERS, "true"));
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
}