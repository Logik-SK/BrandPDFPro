package com.brandpdfpro.service;

import com.brandpdfpro.controller.MainController;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Service responsible for managing file system operations.
 */
public class FileService {

    private static final Logger logger = LoggerFactory.getLogger(FileService.class);

    /**
     * Displays an OS-native file picker dialog restricted to standard raster image formats.
     */
    public File chooseImage(Stage stage, String title) {
        logger.info("Opening system file picker context for raster image selections.");
        FileChooser chooser = new FileChooser();
        chooser.setTitle(title);
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));

        File selectedFile = chooser.showOpenDialog(stage);
        if (selectedFile != null) {
            logger.info("Target image asset successfully resolved: {}", selectedFile.getName());
        } else {
            logger.debug("Image file picker session dismissed by the user.");
        }
        return selectedFile;
    }

    /**
     * Displays an OS-native file picker dialog restricted to PDF documents.
     */
    public File choosePdf(Stage stage) {
        logger.info("Opening system file picker context for source PDF document selection.");
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select PDF File");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));

        File selectedFile = chooser.showOpenDialog(stage);
        if (selectedFile != null) {
            logger.info("Target source PDF document successfully resolved: {}", selectedFile.getName());
        } else {
            logger.debug("PDF document file picker session dismissed by the user.");
        }
        return selectedFile;
    }

    /**
     * Displays an OS-native directory browser window framework allowing folder selection.
     */
    public File chooseDirectory(Stage stage) {
        logger.info("Opening system directory chooser workspace frame.");
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Select Output Folder");

        File selectedDir = chooser.showDialog(stage);
        if (selectedDir != null) {
            logger.info("Target file system folder location selected: {}", selectedDir.getAbsolutePath());
        } else {
            logger.debug("Directory layout browsing prompt dismissed by the user.");
        }
        return selectedDir;
    }

    /**
     * Resolves or builds a dedicated application downloads folder location for compiled assets.
     */
    public File getDefaultOutputFolder() {
        String documentsPath = System.getProperty("user.home") + File.separator + "Downloads";
        File outputFolder = new File(documentsPath, "BrandPDFPro_Pdfs");

        if (!outputFolder.exists()) {
            logger.info("Default local application workspace path absent. Generating structure: {}", outputFolder.getPath());
            outputFolder.mkdirs();
        }
        return outputFolder;
    }
}