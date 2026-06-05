package com.brandpdfpro.service;

import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;

public class FileService {

    public File chooseImage(Stage stage, String title) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle(title);
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        return chooser.showOpenDialog(stage);
    }

    public File choosePdf(Stage stage) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select PDF File");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        return chooser.showOpenDialog(stage);
    }

    public File chooseDirectory(Stage stage) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Select Output Folder");
        return chooser.showDialog(stage);
    }

    public File getDefaultOutputFolder() {
        String documentsPath = System.getProperty("user.home") + File.separator + "Downloads";
        File outputFolder = new File(documentsPath, "BrandPDFPro_Pdfs");
        if (!outputFolder.exists()) {
            outputFolder.mkdirs();
        }
        return outputFolder;
    }
}