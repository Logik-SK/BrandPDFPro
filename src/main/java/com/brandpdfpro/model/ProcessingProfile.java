package com.brandpdfpro.model;

public class ProcessingProfile {

    private String profileName;

    private String headerTemplatePath;
    private String footerTemplatePath;

    private boolean addPageNumbers;

    private boolean addDocumentTag;
    private String documentTag;

    private boolean preventOverlap;
    private boolean scaleContent;
    private boolean compressContent;
    private boolean increasePageSize;

    public ProcessingProfile() {
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public String getHeaderTemplatePath() {
        return headerTemplatePath;
    }

    public void setHeaderTemplatePath(String headerTemplatePath) {
        this.headerTemplatePath = headerTemplatePath;
    }

    public String getFooterTemplatePath() {
        return footerTemplatePath;
    }

    public void setFooterTemplatePath(String footerTemplatePath) {
        this.footerTemplatePath = footerTemplatePath;
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

    public boolean isScaleContent() {
        return scaleContent;
    }

    public void setScaleContent(boolean scaleContent) {
        this.scaleContent = scaleContent;
    }

    public boolean isCompressContent() {
        return compressContent;
    }

    public void setCompressContent(boolean compressContent) {
        this.compressContent = compressContent;
    }

    public boolean isIncreasePageSize() {
        return increasePageSize;
    }

    public void setIncreasePageSize(boolean increasePageSize) {
        this.increasePageSize = increasePageSize;
    }
}