package com.brandpdfpro.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Service responsible for managing user-modifiable application preferences.
 * Handles reading and writing attributes like header/footer heights, pagination
 * settings, and organizational labeling metadata directly into local property registries.
 */
public class SettingsService {

    /** Directory name where configuration and settings files are stored. */
    private static final String CONFIG_DIR = "config";

    /** Filename of the target user preferences properties file. */
    private static final String SETTINGS_FILE = "settings.properties";

    // Property Key Constants Map Keys
    private static final String HEADER_HEIGHT = "header.height";
    private static final String FOOTER_HEIGHT = "footer.height";
    private static final String ADD_PAGE_NUMBERS = "add.page.numbers";
    private static final String COMPANY_NAME = "company.name";

    /** Internal properties registry holding active runtime user settings. */
    private final Properties properties = new Properties();

    /**
     * Constructs a new SettingsService and initializes runtime settings
     * by verifying directory mappings and loading underlying workspace fields.
     */
    public SettingsService() {
        initializeSettings();
    }

    /**
     * Prepares configuration locations on disk. Spawns default property placeholder
     * profiles if missing, before parsing file bytes into operational memory objects.
     *
     * @throws RuntimeException if an IOException occurs during settings environment initialization
     */
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
     * Instantiates a generic File pointing to the target settings file location.
     *
     * @return a File representation of the standard settings configuration path
     */
    private File getSettingsFile() {
        return new File(CONFIG_DIR, SETTINGS_FILE);
    }

    /**
     * Seeds base metadata configuration parameters into properties layouts
     * and exports entries onto local disk segments.
     *
     * @param settingsFile the destination file handle where defaults should be stored
     * @throws IOException if an error occurs while writing properties to the output stream
     */
    private void createDefaultSettings(File settingsFile) throws IOException {
        Properties defaults = new Properties();
        defaults.setProperty(HEADER_HEIGHT, "80");
        defaults.setProperty(FOOTER_HEIGHT, "80");
        defaults.setProperty(ADD_PAGE_NUMBERS, "true");
        defaults.setProperty(COMPANY_NAME, "BrandPDFPro");

        try (FileOutputStream outputStream = new FileOutputStream(settingsFile)) {
            defaults.store(outputStream, "BrandPDF Pro Settings");
        }
    }

    /**
     * Streams and maps active parameters directly out of the targeted configuration file
     * on the system disk into active execution memory instances.
     *
     * @throws IOException if an error occurs while reading from the file input stream
     */
    public void loadSettings() throws IOException {
        try (FileInputStream inputStream = new FileInputStream(getSettingsFile())) {
            properties.load(inputStream);
        }
    }

    /**
     * flushes current memory-mapped property definitions back down onto the system file layers.
     *
     * @throws IOException if an error occurs while writing to the file output stream
     */
    public void saveSettings() throws IOException {
        try (FileOutputStream outputStream = new FileOutputStream(getSettingsFile())) {
            properties.store(outputStream, "BrandPDF Pro Settings");
        }
    }

    /**
     * Retrieves the structural dimension preference for header boundary height tracking.
     *
     * @return the header margin height value as a float, defaulting to 80.0f
     */
    public float getHeaderHeight() {
        return Float.parseFloat(properties.getProperty(HEADER_HEIGHT, "80"));
    }

    /**
     * Retrieves the structural dimension preference for footer boundary height tracking.
     *
     * @return the footer margin height value as a float, defaulting to 80.0f
     */
    public float getFooterHeight() {
        return Float.parseFloat(properties.getProperty(FOOTER_HEIGHT, "80"));
    }

    /**
     * Evaluates state configurations to determine if dynamic pagination stamping is globally requested.
     *
     * @return true if page number inclusion options remain toggled active, otherwise false
     */
    public boolean isPageNumberEnabled() {
        return Boolean.parseBoolean(properties.getProperty(ADD_PAGE_NUMBERS, "true"));
    }

    /**
     * Assigns custom scale dimensions directly into top header preference values.
     *
     * @param value new floating point vertical spacing constraint rule
     */
    public void setHeaderHeight(float value) {
        properties.setProperty(HEADER_HEIGHT, String.valueOf(value));
    }

    /**
     * Assigns custom scale dimensions directly into baseline footer preference values.
     *
     * @param value new floating point vertical spacing constraint rule
     */
    public void setFooterHeight(float value) {
        properties.setProperty(FOOTER_HEIGHT, String.valueOf(value));
    }

    /**
     * Assigns runtime pagination indexing display toggle behaviors globally.
     *
     * @param value flag state controlling document footer indexing behaviors
     */
    public void setPageNumberEnabled(boolean value) {
        properties.setProperty(ADD_PAGE_NUMBERS, String.valueOf(value));
    }

    /**
     * Assigns the targeted operational company name identity string directly into data blocks.
     *
     * @param value the target organizational entity name
     */
    public void setCompanyName(String value) {
        properties.setProperty(COMPANY_NAME, String.valueOf(value));
    }

    /**
     * Retrieves the active branding label definition applied during compiled build exports.
     *
     * @return organizational identity tracking label, defaulting to "BrandPDFPro"
     */
    public String getCompanyName() {
        return properties.getProperty(COMPANY_NAME, "BrandPDFPro");
    }
}