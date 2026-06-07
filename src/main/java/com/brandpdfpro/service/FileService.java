package com.brandpdfpro.service;

import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;

/**
 * Service responsible for managing file system operations.
 * Handles OS-native file dialog prompts for selecting images, PDFs, or
 * directories, and sets up the application's default data output fallback paths.
 */
public class FileService {

    /**
     * Displays an OS-native file picker dialog restricted to standard raster image formats.
     *
     * @param stage the parent JavaFX window scene context
     * @param title the title text to display on the file modal window header
     * @return a File reference pointing to the selected image, or null if canceled
     */
    public File chooseImage(Stage stage, String title) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle(title);
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        return chooser.showOpenDialog(stage);
    }

    /**
     * Displays an OS-native file picker dialog restricted to PDF documents.
     *
     * @param stage the parent JavaFX window scene context
     * @return a File reference pointing to the selected PDF, or null if canceled
     */
    public File choosePdf(Stage stage) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select PDF File");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        return chooser.showOpenDialog(stage);
    }

    /**
     * Displays an OS-native directory browser window framework allowing folder selection.
     *
     * @param stage the parent JavaFX window scene context
     * @return a File reference representing the targeted directory, or null if canceled
     */
    public File chooseDirectory(Stage stage) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Select Output Folder");
        return chooser.showDialog(stage);
    }

    /**
     * Evaluates system path properties to resolve or safely construct a dedicated local
     * application downloads directory placeholder for compiled PDF assets.
     *
     * @return a File handle mapping out the standard application folder path location
     */
    public File getDefaultOutputFolder() {
        String documentsPath = System.getProperty("user.home") + File.separator + "Downloads";
        File outputFolder = new File(documentsPath, "BrandPDFPro_Pdfs");
        if (!outputFolder.exists()) {
            outputFolder.mkdirs();
        }
        return outputFolder;
    }
}