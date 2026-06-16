package com.brandpdfpro.service;

import com.brandpdfpro.controller.MainController;
import com.brandpdfpro.model.ProcessingRequest;
import com.brandpdfpro.service.SettingsService;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.multipdf.LayerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.util.Matrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service responsible for processing, scaling, and adding branding assets to PDF files.
 */
public class PdfProcessorService {

    private static final Logger logger = LoggerFactory.getLogger(PdfProcessorService.class);

    private static final float PAGE_NUMBER_FONT_SIZE = 8f;
    private static final float PAGE_NUMBER_Y_POSITION = 20f;
    private static final float TAG_PADDING_X = 6f;
    private static final float TAG_PADDING_Y = 3f;

    private final SettingsService settingsService = new SettingsService();

    /**
     * Unpacks a unified processing request capsule and routes it to the core execution engine.
     *
     * @throws IOException if source file resolution or file save mutations fail
     */
    public void processPdf(ProcessingRequest request) throws IOException {
        processPdf(
                request.getHeaderFile(),
                request.getFooterFile(),
                request.getPdfFile(),
                request.getOutputFolder(),
                request.isAddPageNumbers(),
                request.isAddDocumentTag(),
                request.getDocumentTag(),
                request.isPreventOverlap(),
                request.isScaleTheContent(),
                request.isCompressTheContent(),
                request.isIncreasePageSize()
        );
    }

    /**
     * Routes incoming execution parameters into standard branding injection streams or spatial modification pipelines.
     *
     * @throws IOException if source file read actions or destination file writes fail
     */
    public void processPdf(
            File headerFile, File footerFile, File pdfFile, File outputFolder,
            boolean addPageNumbers, boolean addDocumentTag, String documentTag,
            boolean preventOverlap, boolean scaleTheContent, boolean compressTheContent,
            boolean increasePageHeight
    ) throws IOException {

        if (preventOverlap) {
            if (increasePageHeight) {
                processPdfWithPageExpansion(headerFile, footerFile, pdfFile, outputFolder, addPageNumbers, addDocumentTag, documentTag);
            } else {
                processPdfWithOverlapPrevention(headerFile, footerFile, pdfFile, outputFolder, addPageNumbers, addDocumentTag, documentTag, scaleTheContent, compressTheContent);
            }
        } else {
            processPdfNormally(headerFile, footerFile, pdfFile, outputFolder, addPageNumbers, addDocumentTag, documentTag);
        }
    }

    /**
     * Compresses or uniformly scales down the original content stream, clearing safe margins to block overlap conflicts.
     */
    private void processPdfWithOverlapPrevention(
            File headerFile, File footerFile, File pdfFile, File outputFolder,
            boolean addPageNumbers, boolean addDocumentTag, String documentTag,
            boolean scaleTheContent, boolean compressTheContent
    ) throws IOException {

        validateRequest(headerFile, footerFile, pdfFile, outputFolder);

        if (scaleTheContent == compressTheContent) {
            logger.error("Invalid adjustment option selected: scaleTheContent={}; compressTheContent={}", scaleTheContent, compressTheContent);
            throw new IllegalArgumentException("Select either Scale Content or Compress Content.");
        }

        logger.info("Initiating PDF rendering workspace under Overlap Prevention Mode.");
        try (PDDocument sourceDocument = Loader.loadPDF(pdfFile);
             PDDocument outputDocument = new PDDocument()) {

            settingsService.loadSettings();

            float headerHeight = settingsService.getHeaderHeight();
            float footerHeight = settingsService.getFooterHeight();
            float contentMargin = 0f;
            float headerSafetyGap = 1f;
            float footerOverlapAllowance = 10f;

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
            logger.info("Modified PDF successfully generated and flushed onto path: {}", outputFile.getAbsolutePath());
        }
    }

    /**
     * Executes standard template overlay rendering without modifying underlying canvas dimensions.
     */
    private void processPdfNormally(
            File headerFile, File footerFile, File pdfFile, File outputFolder,
            boolean addPageNumbers, boolean addDocumentTag, String documentTag
    ) throws IOException {

        validateRequest(headerFile, footerFile, pdfFile, outputFolder);
        logger.info("Initiating PDF rendering workspace under Standard Overlay Mode.");

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
            logger.info("Branded PDF successfully generated and flushed onto path: {}", outputFile.getAbsolutePath());
        }
    }

    /**
     * Blits a header template image onto the upper bounds of the target page media layout box.
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
     * Blits a footer template image onto the zero-origin baseline bounds of the target page layout box.
     */
    private void addFooter(PDDocument document, PDPage page, PDImageXObject image) throws IOException {
        PDRectangle pageSize = page.getMediaBox();
        float pageWidth = pageSize.getWidth();

        settingsService.loadSettings();
        float footerHeight = settingsService.getFooterHeight();

        try (PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, true)) {
            contentStream.drawImage(image, 0, 0, pageWidth, footerHeight);
        }
    }

    /**
     * Calculates textual tracking offsets to push a dynamic center-aligned pagination footer string onto a page.
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
     * Injects an uppercase text classification metadata string encapsulated by a black border box layout.
     */
    private void addDocumentTag(PDDocument document, PDPage page, String documentTag) throws IOException {
        if (documentTag == null || documentTag.trim().isEmpty()) {
            return;
        }

        PDType1Font font = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
        String displayTag = documentTag.toUpperCase();
        float fontSize = 8f;

        float textWidth = font.getStringWidth(displayTag) / 1000 * fontSize;
        float boxWidth = textWidth + (TAG_PADDING_X * 2);
        float boxHeight = fontSize + (TAG_PADDING_Y * 2);

        float x = 10f;
        float y = 45f;

        try (PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, true)) {
            contentStream.addRect(x, y, boxWidth, boxHeight);
            contentStream.stroke();

            contentStream.beginText();
            contentStream.setFont(font, fontSize);
            contentStream.newLineAtOffset(x + TAG_PADDING_X, y + TAG_PADDING_Y + 1);
            contentStream.showText(displayTag);
            contentStream.endText();
        }
    }

    /**
     * Processes a PDF document by extending the physical media bounding box boundaries to clear room for graphic assets.
     */
    private void processPdfWithPageExpansion(
            File headerFile, File footerFile, File pdfFile, File outputFolder,
            boolean addPageNumbers, boolean addDocumentTag, String documentTag
    ) throws IOException {

        validateRequest(headerFile, footerFile, pdfFile, outputFolder);
        logger.info("Initiating PDF rendering workspace under Canvas Page Expansion Mode.");

        try (PDDocument sourceDocument = Loader.loadPDF(pdfFile);
             PDDocument outputDocument = new PDDocument()) {

            settingsService.loadSettings();

            float headerHeight = settingsService.getHeaderHeight();
            float footerHeight = settingsService.getFooterHeight();

            PDImageXObject headerImage = PDImageXObject.createFromFile(headerFile.getAbsolutePath(), outputDocument);
            PDImageXObject footerImage = PDImageXObject.createFromFile(footerFile.getAbsolutePath(), outputDocument);

            LayerUtility layerUtility = new LayerUtility(outputDocument);
            int totalPages = sourceDocument.getNumberOfPages();
            int currentPage = 1;

            for (PDPage sourcePage : sourceDocument.getPages()) {
                PDRectangle originalPageSize = sourcePage.getMediaBox();
                float pageWidth = originalPageSize.getWidth();
                float pageHeight = originalPageSize.getHeight();

                float newPageHeight = pageHeight + headerHeight + footerHeight;
                PDRectangle expandedPageSize = new PDRectangle(pageWidth, newPageHeight);

                PDPage newPage = new PDPage(expandedPageSize);
                outputDocument.addPage(newPage);

                PDFormXObject pageForm = layerUtility.importPageAsForm(sourceDocument, sourcePage);

                try (PDPageContentStream contentStream = new PDPageContentStream(outputDocument, newPage)) {
                    contentStream.saveGraphicsState();

                    AffineTransform transform = new AffineTransform();
                    transform.translate(0, footerHeight);
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
            logger.info("Expanded canvas PDF successfully generated and flushed onto path: {}", outputFile.getAbsolutePath());
        }
    }

    /**
     * Assures context integrity validation rules before instantiating document transformations.
     */
    private void validateRequest(File headerFile, File footerFile, File pdfFile, File outputFolder) {
        if (headerFile == null || !headerFile.exists()) {
            logger.error("Validation failed: Header image resource missing or empty.");
            throw new IllegalArgumentException("Header template not found.");
        }
        if (footerFile == null || !footerFile.exists()) {
            logger.error("Validation failed: Footer image resource missing or empty.");
            throw new IllegalArgumentException("Footer template not found.");
        }
        if (pdfFile == null || !pdfFile.exists()) {
            logger.error("Validation failed: Input source PDF file pointer unresolved.");
            throw new IllegalArgumentException("PDF file not found.");
        }
        if (outputFolder == null || !outputFolder.exists()) {
            logger.error("Validation failed: Output storage location folder target not found.");
            throw new IllegalArgumentException("Output folder not found.");
        }
    }

    private String removeExtension(String fileName) {
        if (fileName == null) {
            return "";
        }
        int lastDotIndex = fileName.lastIndexOf('.');
        return (lastDotIndex > 0) ? fileName.substring(0, lastDotIndex) : fileName;
    }

    /**
     * Resolves write execution race conditions inside directory trees by incrementally index-tagging filenames.
     */
    private File getUniqueOutputFile(File outputFolder, String outputFileName) {
        File outputFile = new File(outputFolder, outputFileName);
        if (!outputFile.exists()) {
            return outputFile;
        }

        String baseName = removeExtension(outputFileName);
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