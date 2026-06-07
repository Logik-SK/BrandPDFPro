package com.brandpdfpro.service;

import com.brandpdfpro.service.SettingsService;

import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.multipdf.LayerUtility;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.util.Matrix;

import java.awt.geom.AffineTransform;

/**
 * Service responsible for processing, scaling, and adding branding assets to PDF files.
 * Handles normal processing as well as advanced layout shifting to prevent text content
 * from clipping behind overlaid headers and footers.
 */
public class PdfProcessorService {

    // Programmatic Static Spacing Layout Rules
    private static final float PAGE_NUMBER_FONT_SIZE = 8f;
    private static final float PAGE_NUMBER_Y_POSITION = 20f;
    private static final float DOCUMENT_TAG_X_POSITION = 10f;
    private static final float DOCUMENT_TAG_Y_POSITION = 50f;
    private static final float DOCUMENT_TAG_FONT_SIZE = 8f;

    /**
     * Configuration instance tracking dimensions and company layout labels.
     */
    private final SettingsService settingsService = new SettingsService();

    /**
     * Entry-point method routing inbound PDF requests directly to either normal branding injection
     * streams or spatial adjustment transformations depending on structural overlap flags.
     *
     * @param headerFile         the physical image file applied as the template header layer
     * @param footerFile         the physical image file applied as the template footer layer
     * @param pdfFile            the origin source PDF document file reference
     * @param outputFolder       the destination directory target where modifications are saved
     * @param addPageNumbers     toggle flag specifying whether dynamic page indices are stamped
     * @param addDocumentTag     toggle flag specifying whether dynamic security tags are stamped
     * @param documentTag        the descriptive text classification metadata tag applied to files
     * @param preventOverlap     toggle flag specifying whether bounding overflow logic is run
     * @param scaleTheContent    toggle flag specifying content layout scaling adjustments
     * @param compressTheContent toggle flag specifying content structure compression rules
     * @throws IOException if file read/write mutations on file streams fail
     */
    public void processPdf(File headerFile, File footerFile, File pdfFile, File outputFolder, boolean addPageNumbers,
                           boolean addDocumentTag, String documentTag, boolean preventOverlap, boolean scaleTheContent, boolean compressTheContent) throws IOException {

        if (preventOverlap) {
            processPdfWithOverlapPrevention(headerFile, footerFile, pdfFile, outputFolder, addPageNumbers, addDocumentTag, documentTag, scaleTheContent, compressTheContent);
        } else {
            processPdfNormally(headerFile, footerFile, pdfFile, outputFolder, addPageNumbers, addDocumentTag, documentTag);
        }
    }

    /**
     * Executes advanced layer matrix manipulations, capturing underlying PDF canvas space, shifting
     * or compressing dimensions inside affine spaces to prevent content overlap with headers and footers.
     */
    private void processPdfWithOverlapPrevention(File headerFile, File footerFile, File pdfFile, File outputFolder, boolean addPageNumbers, boolean addDocumentTag, String documentTag, boolean scaleTheContent, boolean compressTheContent) throws IOException {
        validateRequest(headerFile, footerFile, pdfFile, outputFolder);

        if (scaleTheContent == compressTheContent) {
            throw new IllegalArgumentException("Select either Scale Content or Compress Content.");
        }

        try (PDDocument sourceDocument = Loader.loadPDF(pdfFile); PDDocument outputDocument = new PDDocument()) {
            System.out.println("Overlap Prevention Mode Enabled");

            settingsService.loadSettings();
            float headerHeight = settingsService.getHeaderHeight();
            float footerHeight = settingsService.getFooterHeight();

            // ==================================================
            // Overlap Prevention Settings
            // ==================================================
            float contentMargin = 0f;
            float headerSafetyGap = 1f;

            // 0 = No footer overlap, 20 = Slight overlap, 50 = Aggressive overlap
            float footerOverlapAllowance = 10f;
            // ==================================================

            LayerUtility layerUtility = new LayerUtility(outputDocument);
            PDImageXObject headerImage = PDImageXObject.createFromFile(headerFile.getAbsolutePath(), outputDocument);
            PDImageXObject footerImage = PDImageXObject.createFromFile(footerFile.getAbsolutePath(), outputDocument);

            int totalPages = sourceDocument.getNumberOfPages();
            int currentPage = 1;

            for (PDPage sourcePage : sourceDocument.getPages()) {
                PDRectangle pageSize = sourcePage.getMediaBox();
                float pageWidth = pageSize.getWidth();
                float pageHeight = pageSize.getHeight();

                float availableContentHeight = pageHeight - headerHeight - footerHeight - (contentMargin * 2) + footerOverlapAllowance;
                float contentScaleFactor = availableContentHeight / pageHeight;

                PDPage newPage = new PDPage(pageSize);
                outputDocument.addPage(newPage);

                PDFormXObject pageForm = layerUtility.importPageAsForm(sourceDocument, sourcePage);
                float scaledContentWidth = pageWidth * contentScaleFactor;
                float horizontalOffset = (pageWidth - scaledContentWidth) / 2;
                float scaledContentHeight = pageHeight * contentScaleFactor;
                float usableContentHeight = pageHeight - headerHeight - footerHeight + footerOverlapAllowance;
                float unusedVerticalSpace = usableContentHeight - scaledContentHeight;

                float desiredContentYOffset = footerHeight + (unusedVerticalSpace * 0.20f);
                float maximumSafeYOffset = pageHeight - headerHeight - scaledContentHeight - headerSafetyGap;
                float contentYOffset = Math.min(desiredContentYOffset, maximumSafeYOffset);

                try (PDPageContentStream contentStream = new PDPageContentStream(outputDocument, newPage)) {
                    contentStream.saveGraphicsState();
                    AffineTransform transform = new AffineTransform();

                    if (scaleTheContent) {
                        transform.translate(horizontalOffset, contentYOffset);
                        transform.scale(contentScaleFactor, contentScaleFactor);
                    }
                    if (compressTheContent) {
                        transform.translate(1.0, contentYOffset);
                        transform.scale(1.0, contentScaleFactor);
                    }

                    contentStream.transform(new Matrix(transform));
                    contentStream.drawForm(pageForm);
                    contentStream.restoreGraphicsState();
                }

                addHeader(outputDocument, newPage, headerImage);
                addFooter(outputDocument, newPage, footerImage);

                if (addPageNumbers) {
                    addPageNumber(outputDocument, newPage, currentPage, totalPages);
                }
                if (addDocumentTag) {
                    addDocumentTag(outputDocument, newPage, documentTag);
                }
                currentPage++;
            }

            String outputFileName = removeExtension(pdfFile.getName()) + "_" + settingsService.getCompanyName() + ".pdf";
            File outputFile = getUniqueOutputFile(outputFolder, outputFileName);

            outputDocument.save(outputFile);
            System.out.println("PDF Generated Successfully : " + outputFile.getAbsolutePath());
        }
    }

    /**
     * Executes standard branding injections, stamping asset graphic overlays directly
     * on top of original data layouts without modifying original dimensions.
     */
    private void processPdfNormally(File headerFile, File footerFile, File pdfFile, File outputFolder, boolean addPageNumbers, boolean addDocumentTag, String documentTag) throws IOException {
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
                if (addDocumentTag) {
                    addDocumentTag(document, page, documentTag);
                }
                currentPage++;
            }

            String outputFileName = removeExtension(pdfFile.getName()) + "_" + settingsService.getCompanyName() + ".pdf";
            File outputFile = getUniqueOutputFile(outputFolder, outputFileName);

            document.save(outputFile);
            System.out.println("PDF Generated Successfully : " + outputFile.getAbsolutePath());
        }
    }

    /**
     * Appends header template images to the top coordinate space of the page layout.
     */
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

    /**
     * Appends footer template images to the baseline origin coordinates of the page layout.
     */
    private void addFooter(PDDocument document, PDPage page, PDImageXObject image) throws IOException {
        PDRectangle pageSize = page.getMediaBox();
        float pageWidth = pageSize.getWidth();
        settingsService.loadSettings();

        try (PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, true)) {
            contentStream.drawImage(image, 0, 0, pageWidth, settingsService.getFooterHeight());
        }
    }

    /**
     * Calculates textual tracking metrics to center-align pagination indices strings onto bottom pages.
     */
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

    /**
     * Verifies existence assertions across file inputs and directories, throwing descriptive errors on breach.
     */
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

    /**
     * Simple utility trimmer parsing extension dots to isolate base naming tokens from filenames.
     */
    private String removeExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0) {
            return fileName.substring(0, lastDotIndex);
        }
        return fileName;
    }

    /**
     * Applies security tags into fixed lower boundary alignment zones using proper sentence casing.
     */
    private void addDocumentTag(PDDocument document, PDPage page, String documentTag) throws IOException {
        PDType1Font font = new PDType1Font(Standard14Fonts.FontName.HELVETICA_OBLIQUE);

        try (PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, true)) {
            String displayTag = documentTag.substring(0, 1) + documentTag.substring(1).toLowerCase();
            contentStream.beginText();
            contentStream.setFont(font, DOCUMENT_TAG_FONT_SIZE);
            contentStream.newLineAtOffset(DOCUMENT_TAG_X_POSITION, DOCUMENT_TAG_Y_POSITION);
            contentStream.showText(displayTag);
            contentStream.endText();
        }
    }

    /**
     * Resolves write conflicts incrementally by appending indexing tokens to output filenames.
     */
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

    /**
     * Calculates the uniform height ratio multiplier required to compress vertical space.
     */
    private float calculateScaleFactor(float pageHeight, float headerHeight, float footerHeight) {
        float availableHeight = pageHeight - headerHeight - footerHeight;
        return availableHeight / pageHeight;
    }

}