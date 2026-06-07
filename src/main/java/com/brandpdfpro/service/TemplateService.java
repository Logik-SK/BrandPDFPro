package com.brandpdfpro.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/**
 * Service responsible for managing background layout graphics templates.
 * Handles copying, storing, and loading physical header and footer image
 * assets into dedicated application template workspace directories.
 */
public class TemplateService {

    /** Directory name where background template image assets are cached. */
    private static final String TEMPLATE_DIR = "templates";

    /** Static storage filename assigned to the saved top header layout graphic. */
    private static final String HEADER_FILE = "header.png";

    /** Static storage filename assigned to the saved bottom footer layout graphic. */
    private static final String FOOTER_FILE = "footer.png";

    /**
     * Constructs a new TemplateService and ensures the destination template storage
     * folder structure physically exists on the disk.
     */
    public TemplateService() {
        File directory = new File(TEMPLATE_DIR);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    /**
     * Copies a targeted image file from its source directory location directly into
     * the workspace templates cache, overwriting any pre-existing header asset.
     *
     * @param sourceFile  the original image file selected by the user
     * @throws IOException if a file copy stream error or write restriction occurs
     */
    public void saveHeaderTemplate(File sourceFile) throws IOException {
        File destination = new File(TEMPLATE_DIR, HEADER_FILE);
        Files.copy(sourceFile.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    /**
     * Copies a targeted image file from its source directory location directly into
     * the workspace templates cache, overwriting any pre-existing footer asset.
     *
     * @param sourceFile  the original image file selected by the user
     * @throws IOException if a file copy stream error or write restriction occurs
     */
    public void saveFooterTemplate(File sourceFile) throws IOException {
        File destination = new File(TEMPLATE_DIR, FOOTER_FILE);
        Files.copy(sourceFile.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    /**
     * Searches the local workspace template repository for an existing header template graphic.
     *
     * @return a File pointing to the active header template if present, otherwise null
     */
    public File getHeaderTemplate() {
        File file = new File(TEMPLATE_DIR, HEADER_FILE);
        return file.exists() ? file : null;
    }

    /**
     * Searches the local workspace template repository for an existing footer template graphic.
     *
     * @return a File pointing to the active footer template if present, otherwise null
     */
    public File getFooterTemplate() {
        File file = new File(TEMPLATE_DIR, FOOTER_FILE);
        return file.exists() ? file : null;
    }
}