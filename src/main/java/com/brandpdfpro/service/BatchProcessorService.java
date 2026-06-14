package com.brandpdfpro.service;

import java.io.File;

/**
 * Service responsible for managing batch processing tasks.
 * Iterates through directories to discover target PDF assets and delegates them
 * individually to the foundational processing service for brand decoration.
 */
public class BatchProcessorService {

    /** Core engine instance utilized to apply branding modifications to singular files. */
    private final PdfProcessorService pdfProcessorService = new PdfProcessorService();

    /**
     * Discovers all individual PDF documents located within a given directory and subjects them
     * sequentially to the downstream processing pipeline with uniform customization settings.
     *
     * @param headerFile            the physical image file applied as the template header layer
     * @param footerFile            the physical image file applied as the template footer layer
     * @param inputFolder           the origin directory target queried for individual PDF matches
     * @param outputFolder          the destination directory target where modifications are saved
     * @param addPageNumbers        toggle flag specifying whether dynamic page indices are stamped
     * @param addDocumentTag        toggle flag specifying whether dynamic security tags are stamped
     * @param documentTag           the descriptive text classification metadata tag applied to files
     * @param preventOverlap        toggle flag specifying whether bounding overflow logic is run
     * @param scaleTheContent       toggle flag specifying content layout scaling adjustments
     * @param compressTheContent     toggle flag specifying content structure compression rules
     * @return the total count of successfully targeted and processed PDF documents
     * @throws IllegalArgumentException if the targeted directory contains zero matching PDF assets
     * @throws Exception                 if an unhandled structural file error occurs during writing
     */
    public int processFolder(File headerFile, File footerFile, File inputFolder, File outputFolder, boolean addPageNumbers,
                             boolean addDocumentTag, String documentTag, boolean preventOverlap, boolean scaleTheContent,
                             boolean compressTheContent,boolean increasePageSize, ProgressCallback callback) throws Exception {

        File[] pdfFiles = inputFolder.listFiles(file -> file.isFile() && file.getName().toLowerCase().endsWith(".pdf"));

        if (pdfFiles == null || pdfFiles.length == 0) {
            throw new IllegalArgumentException("No PDF files found in selected folder.");
        }

        int processed = 0;
        for (File pdfFile : pdfFiles) {
            processed++;
            if (callback != null) {
                callback.updateProgress(processed, pdfFiles.length, pdfFile.getName());
            }
            System.out.println("Processing : " + pdfFile.getName());
            // Note: Original code logic maps 'scaleTheContent' to both of the trailing boolean parameters.
            pdfProcessorService.processPdf(headerFile, footerFile, pdfFile, outputFolder, addPageNumbers, addDocumentTag, documentTag, preventOverlap, scaleTheContent, compressTheContent,increasePageSize);
        }

        return pdfFiles.length;
    }
}