package com.brandpdfpro.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Service responsible for managing global application configurations.
 * Handles creating default configuration files, reading application properties,
 * and providing accessors for UI dimension frameworks and window states.
 */
public class AppConfigService {

    /** Directory name where configuration files are located. */
    private static final String CONFIG_DIR = "config";

    /** Filename of the primary application properties file. */
    private static final String APPLICATION_FILE = "application.properties";

    /** Internal properties registry holding active runtime configurations. */
    private final Properties properties = new Properties();

    /**
     * Constructs a new AppConfigService and initializes the application configurations
     * by creating directories, missing files, and loading values into memory.
     */
    public AppConfigService() {
        initializeConfig();
    }

    /**
     * Orchestrates the initialization lifecycle. Ensures directory paths and target property
     * files exist on the filesystem before attempting to parse them into runtime attributes.
     *
     * @throws RuntimeException if an IOException occurs during configuration environment preparation
     */
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

    /**
     * Checks for the presence of the configuration directory on the filesystem
     * and attempts to create it if it does not exist.
     */
    private void createConfigDirectory() {
        File directory = new File(CONFIG_DIR);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    /**
     * Instantiates a generic File pointing to the target properties file location.
     *
     * @return a File representation of the standard application configuration path
     */
    private File getApplicationFile() {
        return new File(CONFIG_DIR, APPLICATION_FILE);
    }

    /**
     * Generates a template schema profile containing foundational fallback operational values
     * and saves it physically onto the disk as an initialization placeholder.
     *
     * @param applicationFile the destination file handle where defaults should be stored
     * @throws IOException if an error occurs while writing properties to the output stream
     */
    private void createDefaultApplicationConfig(File applicationFile) throws IOException {
        Properties defaults = new Properties();

        defaults.setProperty("app.name", "BrandPDF Pro");
        defaults.setProperty("app.version", "1.2.1");
        defaults.setProperty("app.width", "950");
        defaults.setProperty("app.height", "750");
        defaults.setProperty("preview.width", "400");
        defaults.setProperty("preview.height", "80");
        defaults.setProperty("default.header.height", "80");
        defaults.setProperty("default.footer.height", "80");

        try (FileOutputStream outputStream = new FileOutputStream(applicationFile)) {
            defaults.store(outputStream, "BrandPDF Pro Application Configuration");
        }
    }

    /**
     * Establishes a read channel pipeline to stream physical values out from the
     * active configuration file directly into the memory-mapped properties cache.
     *
     * @throws IOException if an error occurs while reading from the file input stream
     */
    private void loadProperties() throws IOException {
        try (FileInputStream inputStream = new FileInputStream(getApplicationFile())) {
            properties.load(inputStream);
        }
    }

    // ==================================================
    // Application Information
    // ==================================================

    /**
     * Retrieves the custom application name from configuration properties.
     *
     * @return the application name string, defaulting to "BrandPDF Pro"
     */
    public String getAppName() {
        return properties.getProperty("app.name", "BrandPDF Pro");
    }

    /**
     * Retrieves the current version identifier of the application.
     *
     * @return the application version string, defaulting to "1.1.0"
     */
    public String getAppVersion() {
        return properties.getProperty("app.version", "1.2.1");
    }

    /**
     * Constructs the window display title string by appending the app name and version string.
     *
     * @return a formatted title string suitable for the JavaFX Stage title
     */
    public String getAppTitle() {
        return getAppName() + " v" + getAppVersion();
    }

    // ==================================================
    // Window Settings
    // ==================================================

    /**
     * Retrieves the configured application window width.
     *
     * @return the application width as a double, defaulting to 950.0
     */
    public double getAppWidth() {
        return Double.parseDouble(properties.getProperty("app.width", "950"));
    }

    /**
     * Retrieves the configured application window height.
     *
     * @return the application height as a double, defaulting to 750.0 if not specified
     */
    public double getAppHeight() {
        return Double.parseDouble(properties.getProperty("app.height", "750"));
    }

    // ==================================================
    // Preview Settings
    // ==================================================

    /**
     * Retrieves the layout width used for displaying image preview components.
     *
     * @return the preview viewport width as a double, defaulting to 400.0
     */
    public double getPreviewWidth() {
        return Double.parseDouble(properties.getProperty("preview.width", "400"));
    }

    /**
     * Retrieves the layout height used for displaying image preview components.
     *
     * @return the preview viewport height as a double, defaulting to 80.0
     */
    public double getPreviewHeight() {
        return Double.parseDouble(properties.getProperty("preview.height", "80"));
    }

    // ==================================================
    // Default PDF Settings
    // ==================================================

    /**
     * Retrieves the standard baseline structural boundary margin constraint for document top headers.
     *
     * @return the fallback header height as a float value, defaulting to 80.0f
     */
    public float getDefaultHeaderHeight() {
        return Float.parseFloat(properties.getProperty("default.header.height", "80"));
    }

    /**
     * Retrieves the standard baseline structural boundary margin constraint for document bottom footers.
     *
     * @return the fallback footer height as a float value, defaulting to 80.0f
     */
    public float getDefaultFooterHeight() {
        return Float.parseFloat(properties.getProperty("default.footer.height", "80"));
    }
}