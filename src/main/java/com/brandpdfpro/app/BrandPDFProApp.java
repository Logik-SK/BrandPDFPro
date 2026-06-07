package com.brandpdfpro.app;

import com.brandpdfpro.service.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javax.swing.text.html.parser.DTDConstants;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

/**
 * Main application class for BrandPDF Pro.
 * Provides a JavaFX graphical user interface for applying consistent company
 * branding (headers, footers, page numbers, and custom document tags) to
 * single PDF files or processing them in batches.
 */
public class BrandPDFProApp extends Application {

    // Services
    private final FileService fileService = new FileService();
    private final TemplateService templateService = new TemplateService();
    private final PdfProcessorService pdfProcessorService = new PdfProcessorService();
    private final SettingsService settingsService = new SettingsService();
    private final BatchProcessorService batchProcessorService = new BatchProcessorService();
    private final AppConfigService appConfigService = new AppConfigService();

    // Image Preview Components
    private ImageView headerPreview;
    private ImageView footerPreview;

    // Text Input Fields
    private TextField headerField;
    private TextField footerField;
    private TextField pdfField;
    private TextField outputField;
    private TextField headerHeightField;
    private TextField companyNameField;
    private TextField footerHeightField;
    private TextField inputFolderField;

    // Control Buttons
    private Button headerBtn;
    private Button footerBtn;
    private Button pdfBtn;
    private Button outputBtn;
    private Button processBtn;
    private Button saveSettingsBtn;
    private Button inputFolderBtn;

    // Checkboxes and ComboBoxes
    private CheckBox pageNumberCheckBox;
    private CheckBox preventOverlapCheckBox;
    private CheckBox batchProcessingCheckBox;
    private CheckBox documentTagCheckBox;
    private ComboBox<String> documentTagComboBox;

    // Radio Buttons (Mutually Exclusive)
    private RadioButton scaleContentRadio;
    private RadioButton compressContentRadio;

    // Status Indicator
    private Label statusLabel;

    /**
     * The main entry point for the application.
     *
     * @param args command-line arguments passed to the application
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Initializes and starts the primary stage of the JavaFX application.
     * Sets up the core layout layout panes, loads user configurations,
     * registers event handlers, and displays the UI window.
     *
     * @param stage the primary stage for this application
     */
    @Override
    public void start(Stage stage) {
        BorderPane root = new BorderPane();
        root.setTop(createTopSection());
        root.setCenter(createCenterSection());
        root.setBottom(createBottomSection());

        registerEvents(stage);
        registerDragAndDrop();
        loadSavedTemplates();

        ScrollPane scrollPane = new ScrollPane(root);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(false);
        scrollPane.setPannable(true);

        Scene scene = new Scene(scrollPane, appConfigService.getAppWidth(), appConfigService.getAppHeight());

        stage.setTitle(appConfigService.getAppTitle() + "- Professional PDF Branding Tool");
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/brandpdfpro.png"))));
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Creates and configures the top header section of the application view.
     *
     * @return a Label containing the stylized application title
     */
    private Label createTopSection() {
        Label title = new Label("BrandPDF Pro");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold;");
        BorderPane.setMargin(title, new Insets(15));
        return title;
    }

    /**
     * Constructs the primary form layout grid containing all configurations, file selectors,
     * image previews, checkboxes, and processing action controls.
     *
     * @return a fully populated and formatted GridPane object representing the center pane
     */
    private GridPane createCenterSection() {
        GridPane form = new GridPane();
        form.setPadding(new Insets(20));
        form.setHgap(10);
        form.setVgap(15);

        // Instantiate Text Fields
        headerField = new TextField();
        footerField = new TextField();
        pdfField = new TextField();
        pdfField.setPromptText("Drag & Drop PDF Here");
        inputFolderField = new TextField();
        inputFolderField.setPromptText("Drag Folder Here");
        outputField = new TextField();

        File defaultOutputFolder = fileService.getDefaultOutputFolder();
        outputField.setText(defaultOutputFolder.getAbsolutePath());
        headerField.setPrefWidth(500);

        // Instantiate Action Buttons
        headerBtn = new Button("Browse");
        footerBtn = new Button("Browse");
        pdfBtn = new Button("Browse");
        inputFolderBtn = new Button("Browse");
        outputBtn = new Button("Browse");
        processBtn = new Button("🚀 Generate Branded PDF");

        // Configuration Checkboxes
        pageNumberCheckBox = new CheckBox("Add Page Numbers");
        pageNumberCheckBox.setSelected(true);

        documentTagCheckBox = new CheckBox("Add Document Tag");
        documentTagCheckBox.setSelected(true);

        // Document Tag ComboBox
        documentTagComboBox = new ComboBox<>();
        documentTagComboBox.getItems().addAll("CONFIDENTIAL", "OUTSOURCED", "INTERNAL", "DRAFT", "RESTRICTED");
        documentTagComboBox.setValue("OUTSOURCED");
        documentTagComboBox.disableProperty().bind(documentTagCheckBox.selectedProperty().not());

        preventOverlapCheckBox = new CheckBox("Prevent Overlap");
        preventOverlapCheckBox.setSelected(false);

        // Overlap Management Options
        scaleContentRadio = new RadioButton("Scale Content");
        compressContentRadio = new RadioButton("Compress Content");

        ToggleGroup overlapModeGroup = new ToggleGroup();
        scaleContentRadio.setToggleGroup(overlapModeGroup);
        compressContentRadio.setToggleGroup(overlapModeGroup);
        compressContentRadio.setSelected(true);
        scaleContentRadio.setDisable(true);
        compressContentRadio.setDisable(true);

        batchProcessingCheckBox = new CheckBox("Batch Processing");
        batchProcessingCheckBox.setSelected(false);

        // Dimension Settings & Brand Config
        headerHeightField = new TextField();
        footerHeightField = new TextField();
        companyNameField = new TextField(); // company name text box
        saveSettingsBtn = new Button("💾 Save Settings");

        headerHeightField.setText(String.valueOf(settingsService.getHeaderHeight()));
        footerHeightField.setText(String.valueOf(settingsService.getFooterHeight()));
        companyNameField.setText(String.valueOf(settingsService.getCompanyName()));

        // Image Previews Initialization
        headerPreview = new ImageView();
        footerPreview = new ImageView();

        headerPreview.setFitWidth(appConfigService.getPreviewWidth());
        headerPreview.setFitHeight(appConfigService.getPreviewHeight());
        headerPreview.setPreserveRatio(false);

        footerPreview.setFitWidth(appConfigService.getPreviewWidth());
        footerPreview.setFitHeight(appConfigService.getPreviewHeight());
        footerPreview.setPreserveRatio(false);

        // Build grid dynamically row-by-row
        int row = 0;

        // Header Section
        form.add(new Label("🖼 Header Template"), 0, row);
        form.add(headerField, 1, row);
        form.add(headerBtn, 2, row);
        row++;

        form.add(headerPreview, 1, row);
        row++;

        // Footer Section
        form.add(new Label("🖼 Footer Template"), 0, row);
        form.add(footerField, 1, row);
        form.add(footerBtn, 2, row);
        row++;

        form.add(footerPreview, 1, row);
        row++;

        // Source PDF Target
        form.add(new Label("📄 Source PDF"), 0, row);
        form.add(pdfField, 1, row);
        form.add(pdfBtn, 2, row);
        row++;

        // Mode Toggles
        form.add(batchProcessingCheckBox, 1, row);
        row++;

        // Folder Directives
        form.add(new Label("📂 Input Folder"), 0, row);
        form.add(inputFolderField, 1, row);
        form.add(inputFolderBtn, 2, row);
        row++;

        form.add(new Label("📁 Output Folder"), 0, row);
        form.add(outputField, 1, row);
        form.add(outputBtn, 2, row);
        row++;

        // Option Bundles Laying
        HBox optionsBox = new HBox(15);
        VBox overlapOptionsBox = new VBox(3, scaleContentRadio, compressContentRadio);
        overlapOptionsBox.setPadding(new Insets(0, 0, 0, 25));
        optionsBox.getChildren().addAll(pageNumberCheckBox, documentTagCheckBox, documentTagComboBox,preventOverlapCheckBox, overlapOptionsBox);

        form.add(optionsBox, 1, row);
        row++;

        // Settings Block Layout
        form.add(new Label("⚙ Settings"), 0, row);
        row++;

        form.add(new Label("Header Height"), 0, row);
        form.add(headerHeightField, 1, row);
        row++;

        form.add(new Label("Footer Height"), 0, row);
        form.add(footerHeightField, 1, row);
        row++;

        // company name-start
        form.add(new Label("Company Name"), 0, row);
        form.add(companyNameField, 1, row);
        row++;
        // company name-end

        form.add(saveSettingsBtn, 1, row);
        row++;

        form.add(processBtn, 1, row);
        updateProcessingMode();

        return form;
    }

    /**
     * Generates the status presentation layout bar located at the bottom of the window application.
     *
     * @return a Label that handles processing operational messaging logs.
     */
    private Label createBottomSection() {
        statusLabel = new Label("🟢 Ready");
        BorderPane.setMargin(statusLabel, new Insets(10));
        return statusLabel;
    }

    /**
     * Binds internal action logic triggers and events to UI user interaction components.
     *
     * @param stage window lifecycle controller used directly inside browsing windows.
     */
    private void registerEvents(Stage stage) {
        headerBtn.setOnAction(e -> browseHeader(stage));
        footerBtn.setOnAction(e -> browseFooter(stage));
        pdfBtn.setOnAction(e -> browsePdf(stage));
        outputBtn.setOnAction(e -> browseOutputFolder(stage));
        processBtn.setOnAction(e -> processPdf());
        saveSettingsBtn.setOnAction(e -> saveSettings());
        inputFolderBtn.setOnAction(e -> browseInputFolder(stage));
        batchProcessingCheckBox.setOnAction(e -> updateProcessingMode());
        documentTagCheckBox.setOnAction(e -> updateDocumentTagControls());
        preventOverlapCheckBox.setOnAction(e -> updateOverlapControls());
    }

    /**
     * Checks data store configurations for existing Header and Footer templates,
     * loading images into active fields and rendering pre-cached assets instantly.
     */
    private void loadSavedTemplates() {
        File header = templateService.getHeaderTemplate();
        if (header != null) {
            headerField.setText(header.getAbsolutePath());
            loadImagePreview(header, headerPreview);
        }

        File footer = templateService.getFooterTemplate();
        if (footer != null) {
            footerField.setText(footer.getAbsolutePath());
            loadImagePreview(footer, footerPreview);
        }

        if (header != null || footer != null) {
            statusLabel.setText("🟢 Saved Templates Loaded");
        }
    }

    /**
     * Displays a native OS explorer file selector dialog tracking the selection
     * of header background graphical template options.
     *
     * @param stage the parent UI viewport reference
     */
    private void browseHeader(Stage stage) {
        File file = fileService.chooseImage(stage, "Select Header Template");
        if (file == null) {
            return;
        }
        try {
            templateService.saveHeaderTemplate(file);
            headerField.setText(file.getAbsolutePath());
            loadImagePreview(file, headerPreview);
            statusLabel.setText("🟢 Header Saved");
        } catch (IOException ex) {
            statusLabel.setText("🔴 Header Save Failed");
            ex.printStackTrace();
        }
    }

    /**
     * Displays a native OS explorer file selector dialog tracking the selection
     * of footer background graphical template options.
     *
     * @param stage the parent UI viewport reference
     */
    private void browseFooter(Stage stage) {
        File file = fileService.chooseImage(stage, "Select Footer Template");
        if (file == null) {
            return;
        }
        try {
            templateService.saveFooterTemplate(file);
            footerField.setText(file.getAbsolutePath());
            loadImagePreview(file, footerPreview);
            statusLabel.setText("🟢 Footer Saved");
        } catch (IOException ex) {
            statusLabel.setText("🔴 Footer Save Failed");
            ex.printStackTrace();
        }
    }

    /**
     * Displays a file dialog tracking the target input standalone PDF document.
     *
     * @param stage the parent UI viewport reference
     */
    private void browsePdf(Stage stage) {
        File file = fileService.choosePdf(stage);
        if (file != null) {
            pdfField.setText(file.getAbsolutePath());
            statusLabel.setText("🟢 PDF Selected");
        }
    }

    /**
     * Displays a folder selection dialog assigning targeted bulk-processing input zones.
     *
     * @param stage the parent UI viewport reference
     */
    private void browseInputFolder(Stage stage) {
        File folder = fileService.chooseDirectory(stage);
        if (folder != null) {
            inputFolderField.setText(folder.getAbsolutePath());
            statusLabel.setText("🟢 Input Folder Selected");
        }
    }

    /**
     * Displays a folder selection dialog assigning the destination zone for built PDFs.
     *
     * @param stage the parent UI viewport reference
     */
    private void browseOutputFolder(Stage stage) {
        File folder = fileService.chooseDirectory(stage);
        if (folder != null) {
            outputField.setText(folder.getAbsolutePath());
            statusLabel.setText("🟢 Output Folder Selected");
        }
    }

    /**
     * Extracts state values from form controllers to pass asset directives
     * onwards into PDF construction core rendering services.
     */
    private void processPdf() {
        try {
            File headerFile = templateService.getHeaderTemplate();
            File footerFile = templateService.getFooterTemplate();
            File pdfFile = new File(pdfField.getText());
            File outputFolder = new File(outputField.getText());
            boolean addPageNumbers = pageNumberCheckBox.isSelected();
            boolean batchMode = batchProcessingCheckBox.isSelected();
            boolean addDocumentTag = documentTagCheckBox.isSelected();
            String documentTag = documentTagComboBox.getValue();
            boolean preventOverlap = preventOverlapCheckBox.isSelected();

            boolean scaleTheContent = false;
            boolean compressTheContent = false;

            if (preventOverlap) {
                scaleTheContent = scaleContentRadio.isSelected();
                compressTheContent = compressContentRadio.isSelected();
            }

            System.out.println("=================================");
            System.out.println("PROCESS OPTIONS");
            System.out.println("=================================");
            System.out.println("Prevent Overlap : " + preventOverlap);
            System.out.println("Scale Content   : " + scaleTheContent);
            System.out.println("Compress Content: " + compressTheContent);
            System.out.println("=================================");

            if (batchMode) {
                File inputFolder = new File(inputFolderField.getText());
                int processedCount = batchProcessorService.processFolder(headerFile, footerFile, inputFolder, outputFolder, addPageNumbers, addDocumentTag, documentTag, preventOverlap, compressTheContent, compressTheContent);
                statusLabel.setText("🟢 " + processedCount + " PDF(s) Processed");
            } else {
                pdfProcessorService.processPdf(headerFile, footerFile, pdfFile, outputFolder, addPageNumbers, addDocumentTag, documentTag, preventOverlap, scaleTheContent, compressTheContent);
                statusLabel.setText("🟢 PDF Generated Successfully");
            }
        } catch (Exception ex) {
            statusLabel.setText("🔴 " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * Parses dimensional inputs and metadata adjustments from forms and saves
     * configurations directly to the system settings database registry.
     */
    private void saveSettings() {
        try {
            float headerHeight = Float.parseFloat(headerHeightField.getText());
            float footerHeight = Float.parseFloat(footerHeightField.getText());
            String companyName = companyNameField.getText();

            settingsService.setHeaderHeight(headerHeight);
            settingsService.setFooterHeight(footerHeight);
            settingsService.setCompanyName(companyName);

            settingsService.saveSettings();
            settingsService.loadSettings();

            statusLabel.setText("🟢 Settings Saved");
        } catch (Exception ex) {
            statusLabel.setText("🔴 Invalid Settings");
            ex.printStackTrace();
        }
    }

    /**
     * Helper mapping engine reading physical image directories and rendering
     * instances natively inside JavaFX views safely.
     *
     * @param imageFile physical path to target image file
     * @param imageView target view element where the preview image will be bound
     */
    private void loadImagePreview(File imageFile, ImageView imageView) {
        if (imageFile == null) {
            return;
        }
        Image image = new Image(imageFile.toURI().toString());
        imageView.setImage(image);
    }

    /**
     * Reconfigures UI control access dynamically based on the state of the
     * batch processing configuration flag.
     */
    private void updateProcessingMode() {
        boolean batchMode = batchProcessingCheckBox.isSelected();
        System.out.println("Batch Mode = " + batchMode);

        pdfField.setDisable(batchMode);
        pdfBtn.setDisable(batchMode);
        inputFolderField.setDisable(!batchMode);
        inputFolderBtn.setDisable(!batchMode);
    }

    /**
     * Manages document metadata classification options configuration states
     * in alignment with UI selections.
     */
    private void updateDocumentTagControls() {
//        boolean enabled = documentTagCheckBox.isSelected();
//        documentTagComboBox.setDisable(!enabled);
    }

    /**
     * Manages dimensional and programmatic scaling toggle states dynamically,
     * protecting spatial alignments against layer overflows.
     */
    private void updateOverlapControls() {
        boolean enabled = preventOverlapCheckBox.isSelected();
        scaleContentRadio.setDisable(!enabled);
        compressContentRadio.setDisable(!enabled);
        if (!enabled) {
            scaleContentRadio.setSelected(true);
        }

        System.out.println("=================================");
        System.out.println("Prevent Overlap = " + enabled);
        System.out.println("Scale Content   = " + scaleContentRadio.isSelected());
        System.out.println("Compress Content= " + compressContentRadio.isSelected());
        System.out.println("=================================");
    }

    /**
     * Registers drag-and-drop gesture listeners on the PDF source text field.
     * Intercepts OS file system drop events, filters incoming assets to ensure
     * they contain valid files, validates that the primary payload ends with a
     * ".pdf" extension, and updates the text field layout path and status label.
     */
    private void registerDragAndDrop() {

        pdfField.setOnDragOver(event -> {
            Dragboard dragboard = event.getDragboard();
            if (dragboard.hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY);
            }
            event.consume();
        });

        pdfField.setOnDragDropped(event -> {
            Dragboard dragboard = event.getDragboard();
            boolean success = false;

            if (dragboard.hasFiles()) {
                File file = dragboard.getFiles().get(0);

                if (file.isDirectory()) {
                    batchProcessingCheckBox.setSelected(true);
                    updateProcessingMode();
                    inputFolderField.setText(file.getAbsolutePath());
                    statusLabel.setText("🟢 Input Folder Selected");
                    success = true;

                } else if (file.getName().toLowerCase().endsWith(".pdf")) {
                    batchProcessingCheckBox.setSelected(false);
                    updateProcessingMode();
                    pdfField.setText(file.getAbsolutePath());
                    statusLabel.setText("🟢 PDF Selected");
                    success = true;
                }
            }

            event.setDropCompleted(success);
            event.consume();
        });
    }
}