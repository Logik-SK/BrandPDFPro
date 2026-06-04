package com.brandpdfpro.service;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import com.brandpdfpro.service.SettingsService;

import java.io.File;
import java.io.IOException;

public class PdfProcessorService {

//    private static final float HEADER_HEIGHT = 80f;
//    private static final float FOOTER_HEIGHT = 80f;
    private static final float PAGE_NUMBER_FONT_SIZE = 8f;
    private static final float PAGE_NUMBER_Y_POSITION = 20f;
    private static final float DOCUMENT_TAG_X_POSITION = 10f;
    private static final float DOCUMENT_TAG_Y_POSITION = 50f;
    private static final float DOCUMENT_TAG_FONT_SIZE = 8f;

    private final SettingsService settingsService = new SettingsService();

    public void processPdf(File headerFile, File footerFile, File pdfFile, File outputFolder, boolean addPageNumbers,boolean addDocumentTag ,String documentTag ) throws IOException {
        validateRequest(headerFile, footerFile, pdfFile, outputFolder);

        try (PDDocument document = Loader.loadPDF(pdfFile)) {
            PDImageXObject headerImage = PDImageXObject.createFromFile(headerFile.getAbsolutePath(), document);
            PDImageXObject footerImage = PDImageXObject.createFromFile(footerFile.getAbsolutePath(), document);

            int totalPages = document.getNumberOfPages();
            int currentPage = 1;

            for (PDPage page : document.getPages()) {
                addHeader(document, page, headerImage);
                addFooter(document, page, footerImage);

                if (addPageNumbers) {
                    addPageNumber(document, page, currentPage, totalPages);
                }
                if (addPageNumbers) {
                    addPageNumber(document, page, currentPage, totalPages);
                }

                if (addDocumentTag) {
                    addDocumentTag(document, page, documentTag);
                }
                currentPage++;

            }

            String outputFileName = removeExtension(pdfFile.getName()) + "_BRANDED.pdf";
            File outputFile = getUniqueOutputFile(outputFolder, outputFileName);
            //File outputFile = new File(outputFolder, outputFileName);
            document.save(outputFile);

            System.out.println("PDF Generated Successfully : " + outputFile.getAbsolutePath());
        }
    }

    private void addHeader(PDDocument document, PDPage page, PDImageXObject image) throws IOException {
        PDRectangle pageSize = page.getMediaBox();
        float pageWidth = pageSize.getWidth();
        float pageHeight = pageSize.getHeight();
        settingsService.loadSettings();
        float headerHeight = settingsService.getHeaderHeight();

        try (PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, true)) {
            contentStream.drawImage(image, 0, pageHeight - headerHeight, pageWidth, headerHeight);
        }
    }

    private void addFooter(PDDocument document, PDPage page, PDImageXObject image) throws IOException {
        PDRectangle pageSize = page.getMediaBox();
        float pageWidth = pageSize.getWidth();
        settingsService.loadSettings();
        settingsService.getFooterHeight();

        try (PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, true)) {
            contentStream.drawImage(image, 0, 0, pageWidth, settingsService.getFooterHeight());
        }
    }

    private void addPageNumber(PDDocument document, PDPage page, int currentPage, int totalPages) throws IOException {
        String pageNumberText = String.format("Page %d of %d", currentPage, totalPages);
        PDRectangle pageSize = page.getMediaBox();
        float pageWidth = pageSize.getWidth();

        PDType1Font font = new PDType1Font(Standard14Fonts.FontName.HELVETICA_OBLIQUE);
        float textWidth = font.getStringWidth(pageNumberText) / 1000 * PAGE_NUMBER_FONT_SIZE;
        float xPosition = (pageWidth - textWidth) / 2;

        try (PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, true)) {
            contentStream.beginText();
            contentStream.setFont(font, PAGE_NUMBER_FONT_SIZE);
            contentStream.newLineAtOffset(xPosition, PAGE_NUMBER_Y_POSITION);
            contentStream.showText(pageNumberText);
            contentStream.endText();
        }
    }

    private void validateRequest(File headerFile, File footerFile, File pdfFile, File outputFolder) {
        if (headerFile == null || !headerFile.exists()) {
            throw new IllegalArgumentException("Header template not found.");
        }
        if (footerFile == null || !footerFile.exists()) {
            throw new IllegalArgumentException("Footer template not found.");
        }
        if (pdfFile == null || !pdfFile.exists()) {
            throw new IllegalArgumentException("PDF file not found.");
        }
        if (outputFolder == null || !outputFolder.exists()) {
            throw new IllegalArgumentException("Output folder not found.");
        }
    }

    private String removeExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0) {
            return fileName.substring(0, lastDotIndex);
        }
        return fileName;
    }

    private void addDocumentTag(PDDocument document, PDPage page, String documentTag) throws IOException {
        PDType1Font font = new PDType1Font(Standard14Fonts.FontName.HELVETICA_OBLIQUE);

        try (PDPageContentStream contentStream = new PDPageContentStream(
                document,
                page,
                PDPageContentStream.AppendMode.APPEND,
                true,
                true
        )) {
            String displayTag =  documentTag.substring(0, 1) + documentTag.substring(1).toLowerCase();
            contentStream.beginText();
            contentStream.setFont(font, DOCUMENT_TAG_FONT_SIZE);
            contentStream.newLineAtOffset(DOCUMENT_TAG_X_POSITION, DOCUMENT_TAG_Y_POSITION);
            contentStream.showText(displayTag);
            contentStream.endText();
        }
    }

    private File getUniqueOutputFile(File outputFolder, String outputFileName) {
        File outputFile = new File(outputFolder, outputFileName);
        if (!outputFile.exists()) {
            return outputFile;
        }
        String baseName = outputFileName.substring(0, outputFileName.lastIndexOf(".pdf"));
        int counter = 1;
        while (true) {
            File candidate = new File(outputFolder, baseName + "_" + counter + ".pdf");
            if (!candidate.exists()) {
                return candidate;
            }
            counter++;
        }
    }
}