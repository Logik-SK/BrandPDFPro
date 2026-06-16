package com.brandpdfpro.service;

import com.brandpdfpro.controller.MainController;
import com.brandpdfpro.exception.ValidationException;
import com.brandpdfpro.model.ProcessingRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Service responsible for enforcing business validation constraints across file system assets.
 */
public class ValidationService {

    private static final Logger logger = LoggerFactory.getLogger(ValidationService.class);

    /**
     * Enforces asset integration validation rules on individually unpacked file components.
     *
     * @throws ValidationException if any resource constraints or system folder paths fail integrity checks
     */
    public void validateProcessingRequest(File headerFile, File footerFile, File pdfFile, File outputFolder, File inputFolder, boolean batchMode) {
        logger.debug("Executing operational lifecycle validations. Mode details: batchMode={}", batchMode);

        validateHeader(headerFile);
        validateFooter(footerFile);
        validateOutputFolder(outputFolder);

        if (batchMode) {
            validateInputFolder(inputFolder);
        } else {
            validatePdf(pdfFile);
        }
    }

    /**
     * Evaluates a capsule data model container request structure against strict runtime constraints.
     *
     * @throws ValidationException if any resource constraints or system folder paths fail integrity checks
     */
    public void validate(ProcessingRequest request) {
        logger.debug("Executing operational lifecycle validations across encapsulated request container.");

        validateHeader(request.getHeaderFile());
        validateFooter(request.getFooterFile());
        validateOutputFolder(request.getOutputFolder());

        if (request.isBatchMode()) {
            validateInputFolder(request.getInputFolder());
        } else {
            validatePdf(request.getPdfFile());
        }
    }

    // =========================================================================
    // Core Internal Validation Rules
    // =========================================================================

    private void validateHeader(File headerFile) {
        if (headerFile == null || !headerFile.exists()) {
            logger.error("Validation violation caught: Top branding header graphic template is unselected or missing.");
            throw new ValidationException("Header template not selected.");
        }
    }

    private void validateFooter(File footerFile) {
        if (footerFile == null || !footerFile.exists()) {
            logger.error("Validation violation caught: Baseline branding footer graphic template is unselected or missing.");
            throw new ValidationException("Footer template not selected.");
        }
    }

    private void validatePdf(File pdfFile) {
        if (pdfFile == null || !pdfFile.exists()) {
            logger.error("Validation violation caught: Processing execution target source PDF data file is missing.");
            throw new ValidationException("PDF file not selected.");
        }
        if (!pdfFile.getName().toLowerCase().endsWith(".pdf")) {
            logger.error("Validation violation caught: Specified source target fails format requirements: {}", pdfFile.getName());
            throw new ValidationException("Selected file is not a PDF.");
        }
    }

    private void validateInputFolder(File inputFolder) {
        if (inputFolder == null || !inputFolder.exists()) {
            logger.error("Validation violation caught: Batch processing execution directory pointer is unassigned or empty.");
            throw new ValidationException("Input folder not selected.");
        }
        if (!inputFolder.isDirectory()) {
            logger.error("Validation violation caught: Targeted input source path is not a file directory tree node: {}", inputFolder.getPath());
            throw new ValidationException("Input path is not a folder.");
        }
    }

    private void validateOutputFolder(File outputFolder) {
        if (outputFolder == null || !outputFolder.exists()) {
            logger.error("Validation violation caught: Compiled export destination folder context target is missing.");
            throw new ValidationException("Output folder not selected.");
        }
        if (!outputFolder.isDirectory()) {
            logger.error("Validation violation caught: Targeted output export location path is not a file directory tree node: {}", outputFolder.getPath());
            throw new ValidationException("Output path is not a folder.");
        }
    }
}