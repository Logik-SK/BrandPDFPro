package com.brandpdfpro.util;

import java.util.List;

public final class AppConstants {

    public static final String APP_NAME = "BrandPDF Pro";

    // =========================================================
    // Application Information
    // =========================================================
    public static final String APP_VERSION = "1.5.0";
    public static final String COMPANY_NAME = "Logik SK";
    public static final String COPYRIGHT = "© 2026 Logik SK. All Rights Reserved.";
    public static final String SETTINGS_FILE = "settings.properties";

    // =========================================================
    // Storage Locations
    // =========================================================
    public static final String PROFILE_FOLDER = "profiles";
    public static final String LOG_FOLDER = "logs";
    public static final String TEMPLATE_FOLDER = "templates";
    public static final String OUTPUT_FOLDER = "output";
    public static final int DEFAULT_HEADER_HEIGHT = 80;

    // =========================================================
    // Default UI Values
    // =========================================================
    public static final int DEFAULT_FOOTER_HEIGHT = 80;
    public static final int DEFAULT_PREVIEW_WIDTH = 700;
    public static final int DEFAULT_PREVIEW_HEIGHT = 80;
    public static final List<String> DOCUMENT_TAGS = List.of("CONFIDENTIAL", "OUTSOURCED", "INTERNAL", "DRAFT", "RESTRICTED");

    // =========================================================
    // Supported Document Tags
    // =========================================================
    public static final String STATUS_READY = "Ready";

    // =========================================================
    // Status Messages
    // =========================================================
    public static final String STATUS_PROCESSING = "Processing...";
    public static final String STATUS_SUCCESS = "Processing Completed Successfully";
    public static final String STATUS_FAILED = "Processing Failed";
    public static final String LIGHT_THEME = "LIGHT";

    // =========================================================
    // Theme Names
    // =========================================================
    public static final String DARK_THEME = "DARK";

    private AppConstants() {
        throw new IllegalStateException("Utility class");
    }
}