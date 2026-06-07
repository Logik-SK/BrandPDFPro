package com.brandpdfpro.app;

/**
 * Secondary launch entry point for the application.
 * This class serves as a safe bypass launcher to prevent runtime issues
 * when running JavaFX applications packaged into executable JAR files
 * without modular arguments.
 */
public class Launcher {

    /**
     * Delegates application execution directly to the main JavaFX application class.
     *
     * @param args command-line arguments passed to the application
     */
    public static void main(String[] args) {
        BrandPDFProApp.main(args);
    }
}