package com.brandpdfpro.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class TemplateService {

    private static final String TEMPLATE_DIR = "templates";
    private static final String HEADER_FILE = "header.png";
    private static final String FOOTER_FILE = "footer.png";

    public TemplateService() {

        File directory = new File(TEMPLATE_DIR);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    public void saveHeaderTemplate(File sourceFile) throws IOException {
        File destination = new File(TEMPLATE_DIR, HEADER_FILE);
        Files.copy(sourceFile.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    public void saveFooterTemplate(File sourceFile) throws IOException {
        File destination = new File(TEMPLATE_DIR, FOOTER_FILE);
        Files.copy(sourceFile.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    public File getHeaderTemplate() {
        File file = new File(TEMPLATE_DIR, HEADER_FILE);
        return file.exists() ? file : null;
    }

    public File getFooterTemplate() {
        File file = new File(TEMPLATE_DIR, FOOTER_FILE);
        return file.exists() ? file : null;
    }
}