package com.brandpdfpro.util;

import java.io.File;

public final class AppPaths {

    private AppPaths() {
    }

    public static final String APP_DATA_FOLDER =
            System.getenv("ProgramData")
                    + File.separator
                    + "BrandPDFPro";

    public static final String LICENSE_FOLDER =
            APP_DATA_FOLDER
                    + File.separator
                    + "licenses";

    public static final String PROFILE_FOLDER =
            APP_DATA_FOLDER
                    + File.separator
                    + "profiles";

    public static final String LOG_FOLDER =
            APP_DATA_FOLDER
                    + File.separator
                    + "logs";

    public static final String SETTINGS_FOLDER =
            APP_DATA_FOLDER
                    + File.separator
                    + "config";

    public static final String LICENSE_FILE =
            LICENSE_FOLDER
                    + File.separator
                    + "license.dat";

    public static final String TRIAL_FILE =
            LICENSE_FOLDER
                    + File.separator
                    + "trial.dat";

    public static final String SETTINGS_FILE =
            SETTINGS_FOLDER
                    + File.separator
                    + "settings.properties";
}