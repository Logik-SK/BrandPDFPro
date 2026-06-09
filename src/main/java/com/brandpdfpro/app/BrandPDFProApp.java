package com.brandpdfpro.app;

import com.brandpdfpro.model.ProcessingProfile;
import com.brandpdfpro.service.*;
import javafx.application.Application;
import javafx.application.Platform;
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
import javafx.concurrent.Task;

import javax.swing.text.html.parser.DTDConstants;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;


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
    private final ProfileService profileService = new ProfileService();

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
    private Button saveProfileBtn;
    private Button loadProfileBtn;
    private Button deleteProfileBtn;

    // Checkboxes and ComboBoxes
    private CheckBox pageNumberCheckBox;
    private CheckBox preventOverlapCheckBox;
    private CheckBox batchProcessingCheckBox;
    private CheckBox documentTagCheckBox;
    private ComboBox<String> documentTagComboBox;
    private ComboBox<String> profileComboBox;

    // Radio Buttons (Mutually Exclusive)
    private RadioButton scaleContentRadio;
    private RadioButton compressContentRadio;
    private RadioButton increasePageSizeRadio;

    // Status Indicator
    private Label statusLabel;

    //Progress Bar
    private ProgressBar progressBar;
    private Label progressLabel;

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
        increasePageSizeRadio = new RadioButton("Increase Page Size");

        ToggleGroup overlapModeGroup = new ToggleGroup();
        scaleContentRadio.setToggleGroup(overlapModeGroup);
        compressContentRadio.setToggleGroup(overlapModeGroup);
        increasePageSizeRadio.setToggleGroup(overlapModeGroup);
        compressContentRadio.setSelected(true);
        scaleContentRadio.setDisable(true);
        compressContentRadio.setDisable(true);
        increasePageSizeRadio.setDisable(true);

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

        //Progress Bar
        progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(350);

        progressLabel = new Label("Ready");

        progressBar.setVisible(false);
        progressLabel.setVisible(false);

        profileComboBox = new ComboBox<>();
        refreshProfiles();

        saveProfileBtn = new Button("💾 Save Profile");
        loadProfileBtn = new Button("📂 Load Profile");
        deleteProfileBtn = new Button("🗑 Delete");

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
        VBox overlapOptionsBox = new VBox(3, scaleContentRadio, compressContentRadio, increasePageSizeRadio);
        overlapOptionsBox.setPadding(new Insets(0, 0, 0, 25));
        optionsBox.getChildren().addAll(pageNumberCheckBox, documentTagCheckBox, documentTagComboBox, preventOverlapCheckBox, overlapOptionsBox);

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

        //Profile Box
        form.add(new Label("👤 Profile"), 0, row);
        HBox profileBox = new HBox(10, profileComboBox, loadProfileBtn, saveProfileBtn, deleteProfileBtn);
        form.add(profileBox,1,row);
        row++;

        form.add(processBtn, 1, row);
        row++;



        //Progress bar
        VBox processingBox = new VBox(10, processBtn, progressLabel, progressBar);
        form.add(processingBox, 1, row);

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
        saveProfileBtn.setOnAction(e -> saveProfile());
        loadProfileBtn.setOnAction(e -> loadProfile());
        deleteProfileBtn.setOnAction(e -> deleteProfile());
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
     * Orchestrates the primary PDF modification workflow on a background daemon thread.
     * <p>
     * Compiles UI settings (such as text values, checkbox inputs, and structural overlap variables),
     * abstracts execution inside a JavaFX {@link javafx.concurrent.Task}, and handles concurrent state switching.
     * It uses structural callbacks wrapped in {@code Platform.runLater()} to safely update thread-restricted
     * UI components like progress bars, status fields, and buttons throughout its batch or single execution lifecycles.
     * </p>
     */
    private void processPdf() {
        try {
            Task<Void> task = new Task<>() {
                @Override
                protected Void call() throws Exception {
                    // Extract template image assets from the disk template service
                    File headerFile = templateService.getHeaderTemplate();
                    File footerFile = templateService.getFooterTemplate();
                    File pdfFile = new File(pdfField.getText());
                    File outputFolder = new File(outputField.getText());

                    // Read explicit control selections from GUI components
                    boolean addPageNumbers = pageNumberCheckBox.isSelected();
                    boolean batchMode = batchProcessingCheckBox.isSelected();
                    boolean addDocumentTag = documentTagCheckBox.isSelected();
                    String documentTag = documentTagComboBox.getValue();
                    boolean preventOverlap = preventOverlapCheckBox.isSelected();

                    boolean scaleTheContent = false;
                    boolean compressTheContent = false;
                    boolean increasePageSize = false;

                    if (preventOverlap) {
                        scaleTheContent = scaleContentRadio.isSelected();
                        compressTheContent = compressContentRadio.isSelected();
                        increasePageSize = increasePageSizeRadio.isSelected();
                    }

                    System.out.println("=================================");
                    System.out.println("PROCESS OPTIONS");
                    System.out.println("=================================");
                    System.out.println("Prevent Overlap : " + preventOverlap);
                    System.out.println("Scale Content   : " + scaleTheContent);
                    System.out.println("Compress Content: " + compressTheContent);
                    System.out.println("Increase Page Size: " + increasePageSize);
                    System.out.println("=================================");

                    if (batchMode) {
                        // Instantiate thread-safe updater listener callback for UI rendering
                        ProgressCallback callback = (current, total, message) -> Platform.runLater(() -> {
                            progressBar.setProgress((double) current / total);
                            progressLabel.setText(current + " / " + total + " - " + message);
                        });

                        File inputFolder = new File(inputFolderField.getText());
                        int processedCount = batchProcessorService.processFolder(
                                headerFile, footerFile, inputFolder, outputFolder,
                                addPageNumbers, addDocumentTag, documentTag, preventOverlap,
                                scaleTheContent, compressTheContent, increasePageSize, callback
                        );

                        Platform.runLater(() ->
                                statusLabel.setText("🟢 " + processedCount + " PDF(s) Processed Successfully")
                        );
                    } else {
                        pdfProcessorService.processPdf(
                                headerFile, footerFile, pdfFile, outputFolder,
                                addPageNumbers, addDocumentTag, documentTag, preventOverlap,
                                scaleTheContent, compressTheContent, increasePageSize
                        );

                        Platform.runLater(() ->
                                statusLabel.setText("🟢 PDF Generated Successfully")
                        );
                    }

                    return null;
                }
            };

            // Hook task lifecycle behavior states to UI tracking metrics
            task.setOnRunning(event -> {
                processBtn.setDisable(true);
                showProgress();
                progressBar.setProgress(0);
                progressLabel.setText("Processing...");
                statusLabel.setText("⏳ Processing...");
            });

            task.setOnSucceeded(event -> {
                progressBar.setProgress(1.0);
                progressLabel.setText("Completed");
                statusLabel.setText("🟢 Processing Completed Successfully");
                processBtn.setDisable(false);
            });

            task.setOnFailed(event -> {
                processBtn.setDisable(false);
                Throwable ex = task.getException();
                statusLabel.setText("❌ Processing Failed");
                if (ex != null) {
                    progressLabel.setText(ex.getMessage());
                    ex.printStackTrace();
                }
            });

            // Fire off execution in a standard standalone background Daemon context
            Thread thread = new Thread(task);
            thread.setDaemon(true);
            thread.start();

        } catch (Exception ex) {
            statusLabel.setText("❌ " + ex.getMessage());
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
        increasePageSizeRadio.setDisable(!enabled);
        if (!enabled) {
            compressContentRadio.setSelected(true);
        }

        System.out.println("=================================");
        System.out.println("Prevent Overlap = " + enabled);
        System.out.println("Scale Content   = " + scaleContentRadio.isSelected());
        System.out.println("Compress Content= " + compressContentRadio.isSelected());
        System.out.println("=================================");
        System.out.println("Increase Page Size= " + increasePageSizeRadio.isSelected());
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

    /**
     * Activates progress tracking nodes in the user interface.
     * Unhides the progress bar and layout status labels, resets the progress indices
     * back to zero, and primes the tracking string text to notify the user of execution initiation.
     */
    private void showProgress() {
        progressBar.setVisible(true);
        progressLabel.setVisible(true);
        progressBar.setProgress(0);
        progressLabel.setText("Starting...");
    }

    /**
     * Deactivates and hides progress tracking nodes from the user interface viewport.
     * Call this cleanup wrapper when active processing loops exit or terminate.
     */
    private void hideProgress() {
        progressBar.setVisible(false);
        progressLabel.setVisible(false);
    }

    private void refreshProfiles() {

        profileComboBox.getItems().clear();
        profileComboBox.getItems().addAll(profileService.getAllProfiles());
        if (!profileComboBox.getItems().isEmpty()) {
            profileComboBox.setValue(profileComboBox.getItems().get(0));
        }
    }
    /**
     * Spawns a modal input prompt allowing the user to commit current UI configuration states
     * into a persistent named {@link ProcessingProfile}.
     * <p>
     * Displays a JavaFX {@link javafx.scene.control.TextInputDialog} to capture a profile name.
     * Upon validation, it maps all layout variables, selected paths, and formatting checkboxes
     * directly into a fresh model instance, delegates persistence tasks to the underlying
     * profile service, triggers a cache refresh, and updates the view combobox state.
     * </p>
     */
    private void saveProfile() {
        try {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Save Profile");
            dialog.setHeaderText(null);
            dialog.setContentText("Enter Profile Name:");

            Optional<String> result = dialog.showAndWait();
            if (result.isEmpty()) {
                return;
            }

            String profileName = result.get().trim();
            if (profileName.isEmpty()) {
                statusLabel.setText("❌ Profile name cannot be empty.");
                return;
            }

            // Map current GUI element states into a new persistent profile model
            ProcessingProfile profile = new ProcessingProfile();
            profile.setProfileName(profileName);

            profile.setHeaderTemplatePath(
                    templateService.getHeaderTemplate() != null
                            ? templateService.getHeaderTemplate().getAbsolutePath()
                            : ""
            );

            profile.setFooterTemplatePath(
                    templateService.getFooterTemplate() != null
                            ? templateService.getFooterTemplate().getAbsolutePath()
                            : ""
            );

            profile.setAddPageNumbers(pageNumberCheckBox.isSelected());
            profile.setAddDocumentTag(documentTagCheckBox.isSelected());
            profile.setDocumentTag(documentTagComboBox.getValue());
            profile.setPreventOverlap(preventOverlapCheckBox.isSelected());
            profile.setScaleContent(scaleContentRadio.isSelected());
            profile.setCompressContent(compressContentRadio.isSelected());
            profile.setIncreasePageSize(increasePageSizeRadio.isSelected());

            // Delegate persistence and synchronize view controls
            profileService.saveProfile(profile);
            refreshProfiles();
            profileComboBox.setValue(profileName);

            statusLabel.setText("🟢 Profile Saved Successfully");

        } catch (Exception ex) {
            statusLabel.setText("❌ " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * Extracts a chosen profile selection out of the UI ComboBox control and maps its preserved
     * configurations directly back onto the active user interface elements.
     * <p>
     * Performs target presence checks on selection indices, pulls the complete data configuration
     * model via the profile service layer, updates template workspace file blocks for headers
     * and footers, maps checkbox states, and synchronizes contextual toggle controls via
     * {@link #updatePreventOverlapControls()}.
     * </p>
     */
    private void loadProfile() {
        try {
            String profileName = profileComboBox.getValue();

            if (profileName == null || profileName.trim().isEmpty()) {
                statusLabel.setText("❌ Please select a profile.");
                return;
            }

            ProcessingProfile profile = profileService.loadProfile(profileName);

            // Handle Header Template Synchronization
            if (profile.getHeaderTemplatePath() != null && !profile.getHeaderTemplatePath().isEmpty()) {
                File headerFile = new File(profile.getHeaderTemplatePath());
                templateService.saveHeaderTemplate(headerFile);
                headerField.setText(headerFile.getName());
            }

            // Handle Footer Template Synchronization
            if (profile.getFooterTemplatePath() != null && !profile.getFooterTemplatePath().isEmpty()) {
                File footerFile = new File(profile.getFooterTemplatePath());
                templateService.saveFooterTemplate(footerFile);
                footerField.setText(footerFile.getName());
            }

            // Bind persistent model attributes back down onto specific JavaFX component selectors
            pageNumberCheckBox.setSelected(profile.isAddPageNumbers());
            documentTagCheckBox.setSelected(profile.isAddDocumentTag());
            documentTagComboBox.setValue(profile.getDocumentTag());
            preventOverlapCheckBox.setSelected(profile.isPreventOverlap());
            scaleContentRadio.setSelected(profile.isScaleContent());
            compressContentRadio.setSelected(profile.isCompressContent());
            increasePageSizeRadio.setSelected(profile.isIncreasePageSize());

            // Force visual dependent layout buttons to repaint matching active states
            updatePreventOverlapControls();

            statusLabel.setText("🟢 Profile Loaded Successfully");

        } catch (Exception ex) {
            statusLabel.setText("❌ " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * Deletes a targeted processing configuration profile from persistence storage.
     * <p>
     * Extracts the active selection from the profile ComboBox control, validates its
     * presence, and prompts the user with a modal JavaFX confirmation {@link javafx.scene.control.Alert}.
     * If the user affirms the warning dialog, it invokes the profile service layer to purge the data records,
     * refreshes the available profiles index, and flushes dependent view states.
     * </p>
     */
    private void deleteProfile() {
        try {
            String profileName = profileComboBox.getValue();

            if (profileName == null || profileName.trim().isEmpty()) {
                statusLabel.setText("❌ Please select a profile.");
                return;
            }

            // Configure and display confirmation dialog framework
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Profile");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure you want to delete profile '" + profileName + "'?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isEmpty() || result.get() != ButtonType.OK) {
                return;
            }

            // Execute removal through the service layer and update components
            boolean deleted = profileService.deleteProfile(profileName);

            if (deleted) {
                refreshProfiles();
                profileComboBox.setValue(null);
                statusLabel.setText("🟢 Profile Deleted Successfully");
            } else {
                statusLabel.setText("❌ Profile not found.");
            }

        } catch (Exception ex) {
            statusLabel.setText("❌ " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void updatePreventOverlapControls() {
        boolean enabled = preventOverlapCheckBox.isSelected();
        scaleContentRadio.setDisable(!enabled);
        compressContentRadio.setDisable(!enabled);
        increasePageSizeRadio.setDisable(!enabled);
    }
}