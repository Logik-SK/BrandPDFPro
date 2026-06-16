package com.brandpdfpro.service;

import com.brandpdfpro.controller.MainController;
import com.brandpdfpro.model.ProcessingRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Service responsible for managing batch processing tasks across directory trees.
 */
public class BatchProcessorService {

    private static final Logger logger = LoggerFactory.getLogger(BatchProcessorService.class);

    private final PdfProcessorService pdfProcessorService = new PdfProcessorService();

    /**
     * Unpacks a batch processing request capsule and routes it directly to the multi-file execution engine.
     *
     * @throws Exception if an unhandled structural file error occurs during batch execution loops
     */
    public int processFolder(ProcessingRequest request, ProgressCallback callback) throws Exception {
        return processFolder(
                request.getHeaderFile(),
                request.getFooterFile(),
                request.getInputFolder(),
                request.getOutputFolder(),
                request.isAddPageNumbers(),
                request.isAddDocumentTag(),
                request.getDocumentTag(),
                request.isPreventOverlap(),
                request.isScaleTheContent(),
                request.isCompressTheContent(),
                request.isIncreasePageSize(),
                callback
        );
    }

    /**
     * Discovers all individual PDF documents located within a given directory and processes them sequentially.
     *
     * @throws IllegalArgumentException if the targeted directory contains zero matching PDF assets
     * @throws Exception                 if an unhandled structural file error occurs during writing
     */
    public int processFolder(
            File headerFile, File footerFile, File inputFolder, File outputFolder,
            boolean addPageNumbers, boolean addDocumentTag, String documentTag,
            boolean preventOverlap, boolean scaleTheContent, boolean compressTheContent,
            boolean increasePageSize, ProgressCallback callback
    ) throws Exception {

        logger.info("Scanning directory for processing targets: {}", inputFolder.getAbsolutePath());

        File[] pdfFiles = inputFolder.listFiles(file ->
                file.isFile() && file.getName().toLowerCase().endsWith(".pdf")
        );

        if (pdfFiles == null || pdfFiles.length == 0) {
            logger.warn("Batch execution aborted. Target directory holds no matching PDF structures: {}", inputFolder.getPath());
            throw new IllegalArgumentException("No PDF files found in selected folder.");
        }

        logger.info("Batch discovery finished. Total files matched for pipeline branding: {}", pdfFiles.length);

        int processed = 0;
        for (File pdfFile : pdfFiles) {
            processed++;

            if (callback != null) {
                callback.updateProgress(processed, pdfFiles.length, pdfFile.getName());
            }

            logger.info("Processing file [{}/{}]: {}", processed, pdfFiles.length, pdfFile.getName());

            pdfProcessorService.processPdf(
                    headerFile, footerFile, pdfFile, outputFolder, addPageNumbers,
                    addDocumentTag, documentTag, preventOverlap, scaleTheContent,
                    compressTheContent, increasePageSize
            );
        }

        logger.info("Batch processing sequence finished successfully. Total modified: {}", pdfFiles.length);
        return pdfFiles.length;
    }
}