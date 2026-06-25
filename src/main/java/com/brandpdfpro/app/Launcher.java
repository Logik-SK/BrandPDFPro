package com.brandpdfpro.app;

import com.brandpdfpro.enums.StartupStatus;
import com.brandpdfpro.model.license.StartupResult;
import com.brandpdfpro.service.license.LicenseStartupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.JOptionPane;

/**
 * Secondary launch entry point for the application.
 */
public class Launcher {

    private static final Logger logger = LoggerFactory.getLogger(Launcher.class);

    public static void main(String[] args) {

        logger.info("Initializing JVM bootstrap protocol [System Version: 2026.1]");

        long startTime = System.currentTimeMillis();

        try {

            // =========================================================
            // Startup License Validation
            // =========================================================

            LicenseStartupService startupService = new LicenseStartupService();

            StartupResult startupResult = startupService.validateStartup();

            logger.info("Startup validation result: {}", startupResult.getStatus());

            if (startupResult.getStatus() == StartupStatus.ACTIVATION_REQUIRED) {

                logger.warn("Application startup blocked. Activation required.");

                JOptionPane.showMessageDialog(null, "Your trial period has expired.\n\n" + "Please activate BrandPDF Pro to continue.", "Activation Required", JOptionPane.WARNING_MESSAGE);

                System.exit(0);
            }

            if (startupResult.getStatus() == StartupStatus.TRIAL_ACTIVE) {

                logger.info("Trial active. Remaining days={}", startupResult.getRemainingDays());
            }

            if (startupResult.getStatus() == StartupStatus.LICENSE_ACTIVE) {

                logger.info("Licensed application startup approved.");
            }

            if (startupResult.getStatus() == StartupStatus.ACTIVATION_REQUIRED) {

                JOptionPane.showMessageDialog(null,"Please activate BrandPDF Pro...");

                System.exit(0);
            }

            // =========================================================
            // Launch Application
            // =========================================================

            logger.info("Handing off thread control to JavaFX runtime engine...");

            BrandPDFProApp.main(args);

        } catch (Exception ex) {

            long duration = System.currentTimeMillis() - startTime;

            logger.error("Application lifecycle aborted after {}ms due to fatal error: {}", duration, ex.getMessage(), ex);

            JOptionPane.showMessageDialog(null, "Application failed to start.\n\n" + ex.getMessage(), "Startup Error", JOptionPane.ERROR_MESSAGE);

            System.exit(1);
        }
    }
}