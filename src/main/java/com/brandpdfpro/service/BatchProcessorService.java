package com.brandpdfpro.service;

import java.io.File;

public class BatchProcessorService {

    private final PdfProcessorService pdfProcessorService = new PdfProcessorService();

    public int processFolder(File headerFile, File footerFile, File inputFolder, File outputFolder, boolean addPageNumbers) throws Exception {
        File[] pdfFiles = inputFolder.listFiles(file -> file.isFile() && file.getName().toLowerCase().endsWith(".pdf"));

        if (pdfFiles == null || pdfFiles.length == 0) {
            throw new IllegalArgumentException("No PDF files found in selected folder.");
        }

        for (File pdfFile : pdfFiles) {
            System.out.println("Processing : " + pdfFile.getName());
            pdfProcessorService.processPdf(headerFile, footerFile, pdfFile, outputFolder, addPageNumbers);
        }

        return pdfFiles.length;
    }
}