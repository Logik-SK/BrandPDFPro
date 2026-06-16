package com.brandpdfpro.controller;

import com.brandpdfpro.exception.ProcessingException;
import com.brandpdfpro.model.ProcessingProfile;
import com.brandpdfpro.model.ProcessingRequest;
import com.brandpdfpro.service.BatchProcessorService;
import com.brandpdfpro.service.PdfProcessorService;
import com.brandpdfpro.service.ProfileService;
import com.brandpdfpro.service.ProgressCallback;
import com.brandpdfpro.service.SettingsService;
import com.brandpdfpro.service.TemplateService;
import com.brandpdfpro.service.ValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Main application controller coordinating UI requests, profiles, and PDF processing.
 */
public class MainController {

    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    private final ValidationService validationService;
    private final PdfProcessorService pdfProcessorService;
    private final BatchProcessorService batchProcessorService;
    private final ProfileService profileService;
    private final SettingsService settingsService;
    private final TemplateService templateService;

    public MainController() {
        this.validationService = new ValidationService();
        this.pdfProcessorService = new PdfProcessorService();
        this.batchProcessorService = new BatchProcessorService();
        this.profileService = new ProfileService();
        this.settingsService = new SettingsService();
        this.templateService = new TemplateService();
    }

    /**
     * Validates and executes a PDF modification request (single or batch mode).
     *
     * @param request  The operational parameters and file targets.
     * @param callback Progress update listener for batch operations.
     * @return Total count of processed PDFs.
     * @throws ProcessingException If validation fails or processing encounters an error.
     */
    public int process(ProcessingRequest request, ProgressCallback callback) {
        logger.info("Initiating PDF processing request. Batch mode: {}", request.isBatchMode());
        validationService.validate(request);

        try {
            if (request.isBatchMode()) {
                int count = batchProcessorService.processFolder(request, callback);
                logger.info("Batch processing completed successfully. Total processed: {}", count);
                return count;
            } else {
                pdfProcessorService.processPdf(request);
                logger.info("Single PDF processing completed successfully.");
                return 1;
            }
        } catch (Exception ex) {
            logger.error("PDF processing pipeline execution failed: {}", ex.getMessage(), ex);
            throw new ProcessingException("PDF processing failed.", ex);
        }
    }

    // =========================================================================
    // Profile Management System
    // =========================================================================

    /**
     * Saves a configuration profile to disk.
     */
    public void saveProfile(ProcessingProfile profile) throws IOException {
        logger.info("Saving configuration profile: {}", profile.getProfileName());
        profileService.saveProfile(profile);
    }

    /**
     * Loads a configuration profile by name.
     */
    public ProcessingProfile loadProfile(String profileName) throws IOException {
        logger.info("Loading configuration profile: {}", profileName);
        return profileService.loadProfile(profileName);
    }

    /**
     * Deletes a configuration profile by name.
     * @return true if successful.
     */
    public boolean deleteProfile(String profileName) {
        logger.warn("Attempting to delete configuration profile: {}", profileName);
        boolean deleted = profileService.deleteProfile(profileName);
        if (deleted) {
            logger.info("Profile '{}' deleted successfully.", profileName);
        } else {
            logger.warn("Profile '{}' could not be found or deleted.", profileName);
        }
        return deleted;
    }

    public List<String> getProfileNames() {
        return profileService.getProfileNames();
    }

    public List<String> getAllProfiles() {
        return profileService.getAllProfiles();
    }

    // =========================================================================
    // Settings Management System
    // =========================================================================

    /**
     * Updates and saves application settings metrics.
     */
    public void saveSettings(double headerHeight, double footerHeight, String companyName) throws IOException {
        logger.info("Updating layout settings for company: {}", companyName);
        settingsService.setHeaderHeight((float) headerHeight);
        settingsService.setFooterHeight((float) footerHeight);
        settingsService.setCompanyName(companyName);
        settingsService.saveSettings();
    }

    /**
     * Flushes the current cached service settings to persistent storage.
     */
    public void saveSettings() throws IOException {
        logger.info("Persisting runtime environment settings down to disk storage.");
        settingsService.saveSettings();
    }

    /**
     * Loads properties from configuration storage into memory.
     */
    public void loadSettings() throws IOException {
        logger.info("Loading environment configuration settings into registry cache.");
        settingsService.loadSettings();
    }

    public String getCompanyName() {
        return settingsService.getCompanyName();
    }

    public float getFooterHeight() {
        return settingsService.getFooterHeight();
    }

    public float getHeaderHeight() {
        return settingsService.getHeaderHeight();
    }

    public void setCompanyName(String companyName) {
        settingsService.setCompanyName(companyName);
    }

    public void setFooterHeight(float footerHeight) {
        settingsService.setFooterHeight(footerHeight);
    }

    public void setHeaderHeight(float headerHeight) {
        settingsService.setHeaderHeight(headerHeight);
    }

    // =========================================================================
    // Template Management System
    // =========================================================================

    /**
     * Copies and persists an external asset file into the systemic header template directory.
     */
    public void saveHeaderTemplate(File headerFile) throws IOException {
        logger.info("Registering new header template asset: {}", headerFile.getName());
        templateService.saveHeaderTemplate(headerFile);
    }

    /**
     * Copies and persists an external asset file into the systemic footer template directory.
     */
    public void saveFooterTemplate(File footerFile) throws IOException {
        logger.info("Registering new footer template asset: {}", footerFile.getName());
        templateService.saveFooterTemplate(footerFile);
    }

    public File getFooterTemplate() {
        return templateService.getFooterTemplate();
    }

    public File getHeaderTemplate() {
        return templateService.getHeaderTemplate();
    }

    // =========================================================================
    // Service Reference Pass-throughs
    // =========================================================================

    public ProfileService getProfileService() {
        return profileService;
    }

    public SettingsService getSettingsService() {
        return settingsService;
    }

    public TemplateService getTemplateService() {
        return templateService;
    }
}