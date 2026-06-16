package com.brandpdfpro.model;

import java.io.File;

public class ProcessingRequest {

    // Files
    private File headerFile;
    private File footerFile;
    private File pdfFile;
    private File inputFolder;
    private File outputFolder;

    // Processing Mode
    private boolean batchMode;

    // PDF Options
    private boolean addPageNumbers;
    private boolean addDocumentTag;
    private String documentTag;

    // Overlap Prevention
    private boolean preventOverlap;
    private boolean scaleTheContent;
    private boolean compressTheContent;
    private boolean increasePageSize;

    public ProcessingRequest() {
    }

    public File getHeaderFile() {
        return headerFile;
    }

    public void setHeaderFile(File headerFile) {
        this.headerFile = headerFile;
    }

    public File getFooterFile() {
        return footerFile;
    }

    public void setFooterFile(File footerFile) {
        this.footerFile = footerFile;
    }

    public File getPdfFile() {
        return pdfFile;
    }

    public void setPdfFile(File pdfFile) {
        this.pdfFile = pdfFile;
    }

    public File getInputFolder() {
        return inputFolder;
    }

    public void setInputFolder(File inputFolder) {
        this.inputFolder = inputFolder;
    }

    public File getOutputFolder() {
        return outputFolder;
    }

    public void setOutputFolder(File outputFolder) {
        this.outputFolder = outputFolder;
    }

    public boolean isBatchMode() {
        return batchMode;
    }

    public void setBatchMode(boolean batchMode) {
        this.batchMode = batchMode;
    }

    public boolean isAddPageNumbers() {
        return addPageNumbers;
    }

    public void setAddPageNumbers(boolean addPageNumbers) {
        this.addPageNumbers = addPageNumbers;
    }

    public boolean isAddDocumentTag() {
        return addDocumentTag;
    }

    public void setAddDocumentTag(boolean addDocumentTag) {
        this.addDocumentTag = addDocumentTag;
    }

    public String getDocumentTag() {
        return documentTag;
    }

    public void setDocumentTag(String documentTag) {
        this.documentTag = documentTag;
    }

    public boolean isPreventOverlap() {
        return preventOverlap;
    }

    public void setPreventOverlap(boolean preventOverlap) {
        this.preventOverlap = preventOverlap;
    }

    public boolean isScaleTheContent() {
        return scaleTheContent;
    }

    public void setScaleTheContent(boolean scaleTheContent) {
        this.scaleTheContent = scaleTheContent;
    }

    public boolean isCompressTheContent() {
        return compressTheContent;
    }

    public void setCompressTheContent(boolean compressTheContent) {
        this.compressTheContent = compressTheContent;
    }

    public boolean isIncreasePageSize() {
        return increasePageSize;
    }

    public void setIncreasePageSize(boolean increasePageSize) {
        this.increasePageSize = increasePageSize;
    }

    @Override
    public String toString() {
        return "ProcessingRequest{" +
                "batchMode=" + batchMode +
                ", addPageNumbers=" + addPageNumbers +
                ", addDocumentTag=" + addDocumentTag +
                ", documentTag='" + documentTag + '\'' +
                ", preventOverlap=" + preventOverlap +
                ", scaleTheContent=" + scaleTheContent +
                ", compressTheContent=" + compressTheContent +
                ", increasePageSize=" + increasePageSize +
                '}';
    }
}