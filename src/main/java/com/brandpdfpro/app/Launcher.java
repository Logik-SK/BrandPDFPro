package com.brandpdfpro.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Secondary launch entry point for the application.
 */
public class Launcher {

    private static final Logger logger = LoggerFactory.getLogger(Launcher.class);

    public static void main(String[] args) {
        logger.info("Initializing JVM bootstrap protocol [System Version: 2026.1]");

        long startTime = System.currentTimeMillis();

        try {
            logger.info("Handing off thread control to JavaFX runtime engine...");
            BrandPDFProApp.main(args);

        } catch (Exception ex) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("Application lifecycle aborted after {}ms due to fatal error: {}", duration, ex.getMessage(), ex);
            System.exit(1);
        }
    }
}