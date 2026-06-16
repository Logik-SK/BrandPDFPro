package com.brandpdfpro.service;

import com.brandpdfpro.controller.MainController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/**
 * Service responsible for managing background layout graphics templates.
 */
public class TemplateService {

    private static final Logger logger = LoggerFactory.getLogger(TemplateService.class);

    private static final String TEMPLATE_DIR = "templates";
    private static final String HEADER_FILE = "header.png";
    private static final String FOOTER_FILE = "footer.png";

    public TemplateService() {
        File directory = new File(TEMPLATE_DIR);
        if (!directory.exists()) {
            logger.info("Templates storage directory absent. Generating folder context at: /{}", TEMPLATE_DIR);
            directory.mkdirs();
        }
    }

    /**
     * Copies a targeted image file from its source directory location directly into the workspace templates cache.
     *
     * @throws IOException if a file copy stream error or write restriction occurs
     */
    public void saveHeaderTemplate(File sourceFile) throws IOException {
        logger.info("Initiating persistence copy stream for new top header asset: {}", sourceFile.getName());
        File destination = new File(TEMPLATE_DIR, HEADER_FILE);
        Files.copy(sourceFile.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
        logger.info("Header template asset committed successfully to workspace cache.");
    }

    /**
     * Copies a targeted image file from its source directory location directly into the workspace templates cache.
     *
     * @throws IOException if a file copy stream error or write restriction occurs
     */
    public void saveFooterTemplate(File sourceFile) throws IOException {
        logger.info("Initiating persistence copy stream for new bottom footer asset: {}", sourceFile.getName());
        File destination = new File(TEMPLATE_DIR, FOOTER_FILE);
        Files.copy(sourceFile.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
        logger.info("Footer template asset committed successfully to workspace cache.");
    }

    // =========================================================================
    // Cache Lookups
    // =========================================================================

    public File getHeaderTemplate() {
        logger.debug("Querying workspace template cache for an active header instance.");
        File file = new File(TEMPLATE_DIR, HEADER_FILE);
        return file.exists() ? file : null;
    }

    public File getFooterTemplate() {
        logger.debug("Querying workspace template cache for an active footer instance.");
        File file = new File(TEMPLATE_DIR, FOOTER_FILE);
        return file.exists() ? file : null;
    }
}