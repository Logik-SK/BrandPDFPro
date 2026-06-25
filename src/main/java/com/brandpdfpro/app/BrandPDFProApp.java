package com.brandpdfpro.app;

import com.brandpdfpro.controller.MainController;
import com.brandpdfpro.exception.ProcessingException;
import com.brandpdfpro.exception.ValidationException;
import com.brandpdfpro.model.ProcessingProfile;
import com.brandpdfpro.model.ProcessingRequest;
import com.brandpdfpro.model.license.LicenseInfo;
import com.brandpdfpro.service.AppConfigService;
import com.brandpdfpro.service.FileService;
import com.brandpdfpro.service.ProgressCallback;
import com.brandpdfpro.service.license.ActivationManager;
import com.brandpdfpro.util.AppConstants;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

/**
 * Main application layout entry point for BrandPDF Pro.
 * Provides a responsive desktop environment for applying corporate templates
 * and processing batches with an adaptive dark/light design system.
 */
public class BrandPDFProApp extends Application {

    private static final Logger logger = LoggerFactory.getLogger(BrandPDFProApp.class);

    // =========================================================================
    // Core Business Logic Services
    // =========================================================================
    private final FileService fileService = new FileService();
    private final AppConfigService appConfigService = new AppConfigService();
    private final MainController mainController = new MainController();
    private final ActivationManager activationManager = new ActivationManager();

    // =========================================================================
    // View Containers & UI Components
    // =========================================================================
    private ImageView headerPreview;
    private ImageView footerPreview;

    private StackPane contentArea;
    private Button processPageBtn;
    private Button profilesPageBtn;
    private Button settingsPageBtn;
    private Button licensePageBtn;
    private Button aboutPageBtn;
    private ToggleButton themeToggleBtn;

    private Node processPage;
    private Node profilesPage;
    private Node settingsPage;
    private Node licensePage;
    private Node aboutPage;
    private AppPage currentPage = AppPage.PROCESS;
    private TextField headerField;
    private TextField footerField;
    private TextField pdfField;
    private TextField outputFolderField;
    private TextField headerHeightField;
    private TextField companyNameField;
    private TextField footerHeightField;
    private TextField inputFolderField;
    // License Page Controls
    private TextArea activationKeyArea;
    private Button activateLicenseBtn;
    private Button copyMachineIdBtn;
    // Control Buttons
    private Button headerBtn;
    private Button footerBtn;
    private Button pdfBtn;
    private Button outputFolderBtn;
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
    // Status Indicators & Screen Nodes
    private Label statusLabel;
    private ProgressBar progressBar;
    private Label progressLabel;
    private HBox bottomSection;
    private Scene mainScene;

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Initializes core windows layouts, application pipelines, and displays primary user workspace view frames.
     */
    @Override
    public void start(Stage stage) {
        logger.info("Initializing system layout templates.");
        BorderPane root = new BorderPane();

        VBox sidebar = createSidebar();
        sidebar.setMinWidth(240);
        sidebar.setPrefWidth(240);
        root.setLeft(sidebar);

        // Build core workspace view layouts
        processPage = createProcessPage();
        profilesPage = createProfilesPage();
        settingsPage = createSettingsPage();
        licensePage = createLicensePage();
        aboutPage = createAboutPage();

        contentArea = new StackPane();
        contentArea.getStyleClass().add("content-area");
        contentArea.getChildren().add(processPage);
        root.setCenter(contentArea);

        bottomSection = createBottomSection();
        root.setBottom(bottomSection);

        registerEvents(stage);
        registerDragAndDrop();
        loadSavedTemplates();

        ScrollPane scrollPane = new ScrollPane(root);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setPannable(true);

        mainScene = new Scene(scrollPane, appConfigService.getAppWidth(), appConfigService.getAppHeight());
        applyTheme(false);

        stage.setTitle(appConfigService.getAppTitle() + " - Professional PDF Branding Tool");
        try {
            stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/brandpdfpro.png"))));
        } catch (Exception ignored) {
            logger.warn("Application tray branding graphic resource missing.");
        }

        stage.setScene(mainScene);
        stage.show();
        logger.info("Application window lifecycle activated successfully.");
    }

    private HBox createBottomSection() {
        logger.debug("Compiling UI component framework for Application Status Bar layout context.");

        HBox container = new HBox(15);
        container.getStyleClass().add("status-bar-container");
        container.setPadding(new Insets(12, 24, 12, 24));
        container.setAlignment(Pos.CENTER_LEFT);

        statusLabel = new Label("🟢 Ready");
        statusLabel.getStyleClass().add("status-bar-label");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label versionLabel = new Label(AppConstants.APP_NAME + " v" + AppConstants.APP_VERSION);
        versionLabel.getStyleClass().add("status-bar-version");

        container.getChildren().addAll(statusLabel, spacer, versionLabel);

        return container;
    }

    private void registerEvents(Stage stage) {
        headerBtn.setOnAction(e -> browseHeader(stage));
        footerBtn.setOnAction(e -> browseFooter(stage));
        pdfBtn.setOnAction(e -> browsePdf(stage));
        outputFolderBtn.setOnAction(e -> browseOutputFolder(stage));
        processBtn.setOnAction(e -> processPdf());
        saveSettingsBtn.setOnAction(e -> saveSettings());
        inputFolderBtn.setOnAction(e -> browseInputFolder(stage));
        batchProcessingCheckBox.setOnAction(e -> updateProcessingMode());
        documentTagCheckBox.setOnAction(e -> updateDocumentTagControls());
        preventOverlapCheckBox.setOnAction(e -> updateOverlapControls());
        saveProfileBtn.setOnAction(e -> saveProfile());
        loadProfileBtn.setOnAction(e -> loadProfile());
        deleteProfileBtn.setOnAction(e -> deleteProfile());

        processPageBtn.setOnAction(e -> setActivePage(processPage, processPageBtn));
        profilesPageBtn.setOnAction(e -> setActivePage(profilesPage, profilesPageBtn));
        settingsPageBtn.setOnAction(e -> setActivePage(settingsPage, settingsPageBtn));
        licensePageBtn.setOnAction(e -> setActivePage(licensePage, licensePageBtn));
        aboutPageBtn.setOnAction(e -> setActivePage(aboutPage, aboutPageBtn));

        themeToggleBtn.setOnAction(e -> applyTheme(!themeToggleBtn.isSelected()));

        if (copyMachineIdBtn != null) {
            copyMachineIdBtn.setOnAction(e -> copyMachineId());
        }

        if (activateLicenseBtn != null) {
            activateLicenseBtn.setOnAction(e -> activateLicense());
        }
    }

    private void setActivePage(Node page, Button activeBtn) {

        contentArea.getChildren().setAll(page);

        processPageBtn.getStyleClass().remove("active");
        profilesPageBtn.getStyleClass().remove("active");
        settingsPageBtn.getStyleClass().remove("active");
        licensePageBtn.getStyleClass().remove("active");
        aboutPageBtn.getStyleClass().remove("active");

        activeBtn.getStyleClass().add("active");

        if (page == processPage) {
            currentPage = AppPage.PROCESS;
        } else if (page == profilesPage) {
            currentPage = AppPage.PROFILES;
        } else if (page == settingsPage) {
            currentPage = AppPage.SETTINGS;
        } else if (page == licensePage) {
            currentPage = AppPage.LICENSE;
        } else if (page == aboutPage) {
            currentPage = AppPage.ABOUT;
        }
    }

    /**
     * Swaps interface styling skin sheets matching selected display preferences.
     */
    private void applyTheme(boolean isDarkTheme) {
        mainScene.getStylesheets().clear();
        try {
            if (isDarkTheme) {
                mainScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/brandpdfpro-dark.css")).toExternalForm());
                themeToggleBtn.setText("☀️ Light Theme Mode");
                themeToggleBtn.setSelected(false);
                logger.info("Visual framework configured into Dark Mode styling sheets.");
            } else {
                mainScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/brandpdfpro-light.css")).toExternalForm());
                themeToggleBtn.setText("🌙 Dark Theme Mode");
                themeToggleBtn.setSelected(true);
                logger.info("Visual framework configured into Light Mode styling sheets.");
            }
        } catch (NullPointerException e) {
            logger.error("Core component stylesheet targets missing during lookup initialization setup loops.");
            System.err.println("Styling stylesheet path resource missing. Verify compilation target directories.");
        }
    }

    private void loadSavedTemplates() {
        File header = mainController.getHeaderTemplate();
        if (header != null) {
            headerField.setText(header.getAbsolutePath());
            loadImagePreview(header, headerPreview);
        }
        File footer = mainController.getFooterTemplate();
        if (footer != null) {
            footerField.setText(footer.getAbsolutePath());
            loadImagePreview(footer, footerPreview);
        }
        if (header != null || footer != null) {
            statusLabel.setText("🟢 SYSTEM STATE: SAVED TEMPLATES LOADED");
        }
    }

    private void browseHeader(Stage stage) {
        File file = fileService.chooseImage(stage, "Select Header Template");
        if (file == null) return;
        try {
            mainController.saveHeaderTemplate(file);
            headerField.setText(file.getAbsolutePath());
            loadImagePreview(file, headerPreview);
            statusLabel.setText("🟢 SYSTEM STATE: HEADER IMAGE SAVED");
        } catch (IOException ex) {
            logger.error("Failed to commit header image asset mapping: {}", ex.getMessage());
            statusLabel.setText("🔴 CRITICAL: HEADER ASSET PERSISTENCE ERROR");
        }
    }

    private void browseFooter(Stage stage) {
        File file = fileService.chooseImage(stage, "Select Footer Template");
        if (file == null) return;
        try {
            mainController.saveFooterTemplate(file);
            footerField.setText(file.getAbsolutePath());
            loadImagePreview(file, footerPreview);
            statusLabel.setText("🟢 SYSTEM STATE: FOOTER IMAGE SAVED");
        } catch (IOException ex) {
            logger.error("Failed to commit footer image asset mapping: {}", ex.getMessage());
            statusLabel.setText("🔴 CRITICAL: FOOTER ASSET PERSISTENCE ERROR");
        }
    }

    private void browsePdf(Stage stage) {
        File file = fileService.choosePdf(stage);
        if (file != null) {
            pdfField.setText(file.getAbsolutePath());
            statusLabel.setText("🟢 SYSTEM STATE: SOURCE PDF MATCHED");
        }
    }

    private void browseInputFolder(Stage stage) {
        File folder = fileService.chooseDirectory(stage);
        if (folder != null) {
            inputFolderField.setText(folder.getAbsolutePath());
            statusLabel.setText("🟢 SYSTEM STATE: PROCESSING TARGET AREA INITIALIZED");
        }
    }

    private void browseOutputFolder(Stage stage) {
        File folder = fileService.chooseDirectory(stage);
        if (folder != null) {
            outputFolderField.setText(folder.getAbsolutePath());
            statusLabel.setText("🟢 Output Folder Selected");
        }
    }

    /**
     * Spawns multi-threaded worker routines parsing documents outside foreground viewport logic threads.
     */
    private void processPdf() {
        try {
            Task<Void> task = new Task<>() {
                @Override
                protected Void call() throws Exception {
                    ProcessingRequest request = new ProcessingRequest();

                    request.setHeaderFile(mainController.getHeaderTemplate());
                    request.setFooterFile(mainController.getFooterTemplate());
                    request.setPdfFile(new File(pdfField.getText()));
                    request.setOutputFolder(new File(outputFolderField.getText()));
                    request.setBatchMode(batchProcessingCheckBox.isSelected());

                    if (request.isBatchMode()) {
                        request.setInputFolder(new File(inputFolderField.getText()));
                    }

                    request.setAddPageNumbers(pageNumberCheckBox.isSelected());
                    request.setAddDocumentTag(documentTagCheckBox.isSelected());
                    request.setDocumentTag(documentTagComboBox.getValue());
                    request.setPreventOverlap(preventOverlapCheckBox.isSelected());
                    request.setScaleTheContent(scaleContentRadio.isSelected());
                    request.setCompressTheContent(compressContentRadio.isSelected());
                    request.setIncreasePageSize(increasePageSizeRadio.isSelected());

                    // Configure thread-safe update pipelines to safely sync progress components
                    ProgressCallback callback = (current, total, message) -> Platform.runLater(() -> {
                        progressBar.setProgress((double) current / total);
                        progressLabel.setText(current + " / " + total + " - " + message);
                    });

                    // Delegate complete processing lifecycle handling to the main controller layer
                    int processedCount = mainController.process(request, callback);

                    // Repaint operational summary completions safely onto the foreground thread
                    Platform.runLater(() -> {
                        if (request.isBatchMode()) {
                            statusLabel.setText("🟢 " + processedCount + " PDF(s) Processed");
                        } else {
                            statusLabel.setText("✅ PDF Generated Successfully");
                        }
                    });

                    return null;
                }
            };

            // Bind worker task lifecycle mutations directly to tracking UI elements
            task.setOnRunning(event -> {
                processBtn.setDisable(true);
                showProgress();
                progressBar.setProgress(0);
                progressLabel.setText("Processing...");
                statusLabel.setText("⏳ Processing...");
            });

            task.setOnSucceeded(event -> {
                progressBar.setProgress(1.0);
                progressLabel.setText("Processing finished successfully.");
                statusLabel.setText("✅ Processing Completed Successfully");
                processBtn.setDisable(false);
                hideProgress();
            });

            task.setOnFailed(event -> {
                processBtn.setDisable(false);
                hideProgress();

                Throwable ex = task.getException();

                if (ex instanceof ValidationException) {
                    logger.warn("Request lifecycle validation violation caught: {}", ex.getMessage());
                    statusLabel.setText("⚠ Validation Error: " + ex.getMessage());

                } else if (ex instanceof ProcessingException) {
                    logger.error("Core processing engine execution failure: {}", ex.getMessage(), ex);
                    statusLabel.setText("❌ Processing Error: " + ex.getMessage());

                } else if (ex != null) {
                    logger.error("Unhandled structural runtime anomaly intercepted outside normal boundary rules: {}", ex.getMessage(), ex);
                    statusLabel.setText("❌ Unexpected Error");
                } else {
                    statusLabel.setText("❌ Processing Failed");
                }
            });

            // Initialize background daemon handling thread framework
            Thread thread = new Thread(task);
            thread.setDaemon(true);
            thread.start();

        } catch (Exception ex) {
            logger.error("Failed to construct or spawn background process task runtime workers: {}", ex.getMessage());
            statusLabel.setText("❌ Initialization failed: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * Saves application configuration modifications and sizes back down to disk caches.
     */
    private void saveSettings() {
        try {
            float headerHeight = Float.parseFloat(headerHeightField.getText());
            float footerHeight = Float.parseFloat(footerHeightField.getText());
            String companyName = companyNameField.getText();

            mainController.setHeaderHeight(headerHeight);
            mainController.setFooterHeight(footerHeight);
            mainController.setCompanyName(companyName);

            mainController.saveSettings();
            mainController.loadSettings();

            logger.info("Global application margins and structural company tags updated successfully.");
            statusLabel.setText("🟢 Settings Saved");
        } catch (Exception ex) {
            logger.error("Configuration tracking payload rejection mapping settings updates: {}", ex.getMessage());
            statusLabel.setText("🔴 Invalid Settings");
            ex.printStackTrace();
        }
    }

    private void loadImagePreview(File imageFile, ImageView imageView) {
        if (imageFile == null) return;
        Image image = new Image(imageFile.toURI().toString());
        imageView.setImage(image);
    }

    private void updateProcessingMode() {
        boolean batchMode = batchProcessingCheckBox.isSelected();
        logger.info("Workspace mode shifted. Batch processing active: {}", batchMode);

        pdfField.setDisable(batchMode);
        pdfBtn.setDisable(batchMode);
        inputFolderField.setDisable(!batchMode);
        inputFolderBtn.setDisable(!batchMode);
    }

    private void updateDocumentTagControls() {
        documentTagComboBox.setDisable(!documentTagCheckBox.isSelected());
    }

    private void updateOverlapControls() {
        boolean enabled = preventOverlapCheckBox.isSelected();
        scaleContentRadio.setDisable(!enabled);
        compressContentRadio.setDisable(!enabled);
        increasePageSizeRadio.setDisable(!enabled);
        if (!enabled) {
            compressContentRadio.setSelected(true);
        }
    }

    /**
     * Intercepts operating system drag-and-drop actions to automatically map targeted asset directories.
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
                logger.info("System intercepted workspace drop event for pathway: {}", file.getAbsolutePath());

                if (file.isDirectory()) {
                    batchProcessingCheckBox.setSelected(true);
                    updateProcessingMode();
                    inputFolderField.setText(file.getAbsolutePath());
                    statusLabel.setText("🟢 DRAG-DROP INJECTED DIRECTORY PIPELINE");
                    success = true;
                } else if (file.getName().toLowerCase().endsWith(".pdf")) {
                    batchProcessingCheckBox.setSelected(false);
                    updateProcessingMode();
                    pdfField.setText(file.getAbsolutePath());
                    statusLabel.setText("🟢 DRAG-DROP DETECTED STANDALONE INPUT");
                    success = true;
                }
            }
            event.setDropCompleted(success);
            event.consume();
        });
    }

    private void showProgress() {
        if (!bottomSection.getChildren().contains(progressBar)) {
            progressBar = new ProgressBar(0);
            progressBar.setPrefWidth(200);
            progressLabel = new Label("");
            progressLabel.getStyleClass().add("progress-label");
            bottomSection.getChildren().addAll(progressBar, progressLabel);
        }
        progressBar.setVisible(true);
        progressLabel.setVisible(true);
    }

    private void hideProgress() {
        if (progressBar != null && progressLabel != null) {
            progressBar.setVisible(false);
            progressLabel.setVisible(false);
            bottomSection.getChildren().removeAll(progressBar, progressLabel);
        }
    }

    private void refreshProfiles() {
        profileComboBox.getItems().clear();
        profileComboBox.getItems().addAll(mainController.getAllProfiles());
        if (!profileComboBox.getItems().isEmpty()) {
            profileComboBox.setValue(profileComboBox.getItems().get(0));
        }
    }

    /**
     * Assembles all configurations together to generate a persistent layout parameter profile record.
     */
    private void saveProfile() {
        try {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Save Profile");
            dialog.setHeaderText(null);
            dialog.setContentText("Enter Profile Name:");

            Optional<String> result = dialog.showAndWait();
            if (result.isEmpty()) return;

            String profileName = result.get().trim();
            if (profileName.isEmpty()) {
                statusLabel.setText("❌ CONFIG ALARM: ATTRIBUTE KEY NAME CANNOT RUN EMPTY");
                return;
            }

            ProcessingProfile profile = new ProcessingProfile();
            profile.setProfileName(profileName);
            profile.setHeaderTemplatePath(mainController.getHeaderTemplate() != null ? mainController.getHeaderTemplate().getAbsolutePath() : "");
            profile.setFooterTemplatePath(mainController.getFooterTemplate() != null ? mainController.getFooterTemplate().getAbsolutePath() : "");
            profile.setAddPageNumbers(pageNumberCheckBox.isSelected());
            profile.setAddDocumentTag(documentTagCheckBox.isSelected());
            profile.setDocumentTag(documentTagComboBox.getValue());
            profile.setPreventOverlap(preventOverlapCheckBox.isSelected());
            profile.setScaleContent(scaleContentRadio.isSelected());
            profile.setCompressContent(compressContentRadio.isSelected());
            profile.setIncreasePageSize(increasePageSizeRadio.isSelected());

            logger.info("Serializing new workspace profile configuration under identity key name: '{}'", profileName);
            mainController.saveProfile(profile);
            refreshProfiles();
            profileComboBox.setValue(profileName);
            statusLabel.setText("🟢 PROFILES: COMPILED PERSISTENT SAVED TEMPLATE STATE");
        } catch (Exception ex) {
            logger.error("Aborting template serialization due to exception: {}", ex.getMessage());
            statusLabel.setText("❌ CONFIG UNEXPECTED ERROR CAUGHT PREVENTING DEPLOYMENT SAVE");
        }
    }

    /**
     * Restores saved operational presets into checkboxes and dropdown options.
     */
    private void loadProfile() {
        try {
            String profileName = profileComboBox.getValue();
            if (profileName == null || profileName.trim().isEmpty()) {
                statusLabel.setText("❌ PREPARATION REJECTED: UNASSIGNED TARGET RETRIEVAL MATRIX KEY");
                return;
            }

            logger.info("Reconciling operational presets from target layout index key name: '{}'", profileName);
            ProcessingProfile profile = mainController.loadProfile(profileName);
            if (profile.getHeaderTemplatePath() != null && !profile.getHeaderTemplatePath().isEmpty()) {
                File headerFile = new File(profile.getHeaderTemplatePath());
                mainController.saveHeaderTemplate(headerFile);
                headerField.setText(headerFile.getAbsolutePath());
                loadImagePreview(headerFile, headerPreview);
            }

            if (profile.getFooterTemplatePath() != null && !profile.getFooterTemplatePath().isEmpty()) {
                File footerFile = new File(profile.getFooterTemplatePath());
                mainController.saveFooterTemplate(footerFile);
                footerField.setText(footerFile.getAbsolutePath());
                loadImagePreview(footerFile, footerPreview);
            }

            pageNumberCheckBox.setSelected(profile.isAddPageNumbers());
            documentTagCheckBox.setSelected(profile.isAddDocumentTag());
            documentTagComboBox.setValue(profile.getDocumentTag());
            preventOverlapCheckBox.setSelected(profile.isPreventOverlap());
            scaleContentRadio.setSelected(profile.isScaleContent());
            compressContentRadio.setSelected(profile.isCompressContent());
            increasePageSizeRadio.setSelected(profile.isIncreasePageSize());

            updateOverlapControls();
            updateDocumentTagControls();
            statusLabel.setText("🟢 PROFILES: COMPLETED RECONCILIATION SYNCHRONIZATION RUN");
        } catch (Exception ex) {
            logger.error("Failed to cleanly sync and align workspace profile variables: {}", ex.getMessage());
            statusLabel.setText("❌ SYNC ERROR: CRITICAL CONFLICT PARSING CACHED SYSTEM FILE ENTRIES");
        }
    }

    /**
     * Erases workspace configuration profiles permanently from storage targets.
     */
    private void deleteProfile() {
        try {
            String profileName = profileComboBox.getValue();
            if (profileName == null || profileName.trim().isEmpty()) {
                statusLabel.setText("❌ DELETION COMMAND REFUSED: SELECTION PROFILE POINTER NULL");
                return;
            }

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Profile");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure you want to delete profile '" + profileName + "'?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isEmpty() || result.get() != ButtonType.OK) return;

            logger.warn("Requesting tracking file truncation for profile identifier token: '{}'", profileName);
            if (mainController.deleteProfile(profileName)) {
                refreshProfiles();
                profileComboBox.setValue(null);
                statusLabel.setText("🟢 PROFILES: ERASED CACHED PERSISTENT CONFIG DATA");
            } else {
                statusLabel.setText("❌ ATTEMPT REJECTED: SPECIFIED REGISTRY PATH TARGET ABSENT");
            }
        } catch (Exception ex) {
            logger.error("Exception encountered purging profile metadata indices: {}", ex.getMessage());
            statusLabel.setText("❌ SYSTEM FAILURE ENCOUNTERED TRUNCATING STORAGE ENTRIES");
        }
    }

    private VBox createSidebar() {
        VBox sidebar = new VBox(12);
        sidebar.setPadding(new Insets(24, 16, 24, 16));
        sidebar.getStyleClass().add("sidebar");

        Label brandingHeader = new Label("BRAND PDF PRO");
        brandingHeader.getStyleClass().add("sidebar-title");

        themeToggleBtn = new ToggleButton("☀️ Light Theme Mode");
        themeToggleBtn.setMaxWidth(Double.MAX_VALUE);
        themeToggleBtn.getStyleClass().add("theme-toggle-btn");

        processPageBtn = new Button("📄 Process Files");
        profilesPageBtn = new Button("📁 Template Profiles");
        settingsPageBtn = new Button("⚙️ Settings");
        licensePageBtn = new Button("🔑 Application License");
        aboutPageBtn = new Button("ℹ️ About");

        processPageBtn.setMaxWidth(Double.MAX_VALUE);
        profilesPageBtn.setMaxWidth(Double.MAX_VALUE);
        settingsPageBtn.setMaxWidth(Double.MAX_VALUE);
        licensePageBtn.setMaxWidth(Double.MAX_VALUE);
        aboutPageBtn.setMaxWidth(Double.MAX_VALUE);

        processPageBtn.getStyleClass().addAll("sidebar-btn", "active");
        profilesPageBtn.getStyleClass().add("sidebar-btn");
        settingsPageBtn.getStyleClass().add("sidebar-btn");
        licensePageBtn.getStyleClass().add("sidebar-btn");
        aboutPageBtn.getStyleClass().add("sidebar-btn");

        sidebar.getChildren().addAll(brandingHeader, themeToggleBtn, new Separator(), processPageBtn, profilesPageBtn, settingsPageBtn, licensePageBtn, aboutPageBtn);
        return sidebar;
    }

    // =========================================================================
    // Layout Factory Generation Pipelines
    // =========================================================================

    private Node createProcessPage() {
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(25));
        grid.getStyleClass().add("main-pane");

        int row = 0;

        // --- FILE SELECTION SECTION ---

        // Input PDF Row
        pdfField = new TextField();
        pdfField.setPromptText("Drag & drop a PDF or browse files...");
        pdfField.setPrefWidth(450);
        pdfBtn = new Button("Browse...");
        grid.add(new Label("Source PDF:"), 0, row);
        grid.add(pdfField, 1, row);
        grid.add(pdfBtn, 2, row);
        row++;

        // Batch Directory Row
        inputFolderField = new TextField();
        inputFolderField.setPromptText("Select folder for batch processing...");
        inputFolderField.setDisable(true);
        inputFolderBtn = new Button("Browse...");
        inputFolderBtn.setDisable(true);
        grid.add(new Label("Batch Input Folder:"), 0, row);
        grid.add(inputFolderField, 1, row);
        grid.add(inputFolderBtn, 2, row);
        row++;

        // Output Directory Row
        outputFolderField = new TextField();
        File defaultOutputFolder = fileService.getDefaultOutputFolder();
        outputFolderField.setText(defaultOutputFolder.getAbsolutePath());
        outputFolderField.setPromptText("Select output folder destination...");
        outputFolderField.setDisable(true);
        outputFolderBtn = new Button("Browse...");
        outputFolderBtn.setDisable(false);
        grid.add(new Label("Output Folder:"), 0, row);
        grid.add(outputFolderField, 1, row);
        grid.add(outputFolderBtn, 2, row);
        row++;

        // --- BRANDING ASSETS SECTION ---

        // Header Asset Rows
        headerField = new TextField();
        headerField.setEditable(false);
        headerField.setPromptText("No file selected");
        headerBtn = new Button("Choose Header...");

        // PROFESSIONAL UI TWEAK: Use clean thumbnail scales instead of giant boxes
        headerPreview = new ImageView();
        headerPreview.setFitHeight(60);
        headerPreview.setFitWidth(120);
        headerPreview.setPreserveRatio(true);

        grid.add(new Label("Header Image:"), 0, row);
        grid.add(headerField, 1, row);
        grid.add(headerBtn, 2, row);
        row++;
        grid.add(headerPreview, 1, row);
        row++;

        // Footer Asset Rows
        footerField = new TextField();
        footerField.setEditable(false);
        footerField.setPromptText("No file selected");
        footerBtn = new Button("Choose Footer...");

        // PROFESSIONAL UI TWEAK: Standardized thumbnail aspect scaling
        footerPreview = new ImageView();
        footerPreview.setFitHeight(60);
        footerPreview.setFitWidth(120);
        footerPreview.setPreserveRatio(true);

        grid.add(new Label("Footer Image:"), 0, row);
        grid.add(footerField, 1, row);
        grid.add(footerBtn, 2, row);
        row++;
        grid.add(footerPreview, 1, row);
        row++;

        // --- LAYOUT & CONFIGURATION OPTIONS ---

        VBox optionBox = new VBox(14);
        optionBox.setPadding(new Insets(10, 0, 15, 0));

        batchProcessingCheckBox = new CheckBox("Enable Batch Processing");
        pageNumberCheckBox = new CheckBox("Include Page Numbers");

        // Clean inline styling for classification tags
        documentTagCheckBox = new CheckBox("Add Security Classification Tag");
        documentTagComboBox = new ComboBox<>();
        documentTagComboBox.getItems().addAll(AppConstants.DOCUMENT_TAGS);
        documentTagComboBox.setValue("CONFIDENTIAL");
        documentTagComboBox.setDisable(true);

        // Nest the dropdown nicely next to its activating checkbox
        HBox classificationBox = new HBox(12, documentTagCheckBox, documentTagComboBox);
        classificationBox.setAlignment(Pos.CENTER_LEFT);

        preventOverlapCheckBox = new CheckBox("Prevent Layout Overlap");
        ToggleGroup radioGroup = new ToggleGroup();
        scaleContentRadio = new RadioButton("Scale Content");
        compressContentRadio = new RadioButton("Compress Content");
        increasePageSizeRadio = new RadioButton("Expand Page Size");

        scaleContentRadio.setToggleGroup(radioGroup);
        compressContentRadio.setToggleGroup(radioGroup);
        increasePageSizeRadio.setToggleGroup(radioGroup);
        compressContentRadio.setSelected(true);

        scaleContentRadio.setDisable(true);
        compressContentRadio.setDisable(true);
        increasePageSizeRadio.setDisable(true);

        // Indent dependent layout choices slightly for clean visual hierarchy
        HBox radioBox = new HBox(15, scaleContentRadio, compressContentRadio, increasePageSizeRadio);
        radioBox.setPadding(new Insets(0, 0, 0, 22));

        optionBox.getChildren().addAll(batchProcessingCheckBox, pageNumberCheckBox, classificationBox, preventOverlapCheckBox, radioBox);
        grid.add(optionBox, 1, row, 2, 1);
        row++;

        // --- ACTION BUTTON ---

        processBtn = new Button("Apply Branding");
        processBtn.getStyleClass().add("btn-primary");
        processBtn.setPrefWidth(200);
        grid.add(processBtn, 1, row);

        return grid;
    }

    private Node createProfilesPage() {
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(30));
        layout.getStyleClass().add("main-pane");

        Label title = new Label("Profile Manager");
        title.getStyleClass().add("section-header");

        profileComboBox = new ComboBox<>();
        profileComboBox.setMinWidth(320);
        refreshProfiles();

        loadProfileBtn = new Button("📂 Load Profile");
        loadProfileBtn.getStyleClass().add("btn-action-load");
        deleteProfileBtn = new Button("🗑️ Delete Profile");
        deleteProfileBtn.getStyleClass().add("btn-danger");
        saveProfileBtn = new Button("💾 Save Profile");
        saveProfileBtn.getStyleClass().add("btn-secondary");

        HBox controlBox = new HBox(15, loadProfileBtn, deleteProfileBtn);
        layout.getChildren().addAll(title, new Label("Select Profile:"), profileComboBox, controlBox, new Separator(), saveProfileBtn);
        return layout;
    }

    private Node createSettingsPage() {
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(30));
        grid.getStyleClass().add("main-pane");

        int row = 0;

        Label title = new Label("Layout Settings");
        title.getStyleClass().add("section-header");
        grid.add(title, 0, row, 2, 1);
        row++;

        headerHeightField = new TextField(String.valueOf(mainController.getHeaderHeight()));
        footerHeightField = new TextField(String.valueOf(mainController.getFooterHeight()));
        companyNameField = new TextField(mainController.getCompanyName());

        saveSettingsBtn = new Button("💾 Apply Changes");
        saveSettingsBtn.getStyleClass().add("btn-primary");

        grid.add(new Label("Header Height:"), 0, row);
        grid.add(headerHeightField, 1, row);
        row++;

        grid.add(new Label("Footer Height:"), 0, row);
        grid.add(footerHeightField, 1, row);
        row++;

        grid.add(new Label("Company Name:"), 0, row);
        grid.add(companyNameField, 1, row);
        row++;

        grid.add(saveSettingsBtn, 1, row);

        return grid;
    }

    /**
     * Constructs the license management interface panel describing license parameters and scopes.
     *
     * @return a structured JavaFX Node containing licensing configurations and text blocks
     */
    private Node createLicensePage() {

        logger.debug("Compiling UI component framework for License Management workspace.");

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(30));
        layout.getStyleClass().add("main-pane");

        Label titleLabel = new Label("🔑 License Management");
        titleLabel.getStyleClass().add("section-header");

        LicenseInfo licenseInfo = activationManager.getActiveLicense();

        boolean activated = licenseInfo != null && activationManager.isActivated();

        String status = activated ? "🟢 LICENSE ACTIVE" : "🟡 TRIAL EDITION";
        String licenseType = activated ? licenseInfo.getLicenseType().getDisplayName() : "Community";
        String customerName = activated ? licenseInfo.getCustomerName() : "-";
        String customerEmail = activated ? licenseInfo.getCustomerEmail() : "-";
        String expiryDate = activated ? licenseInfo.getExpiryDate().toString() : "-";

        String machineId = activationManager.getMachineId();

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(12);

        int row = 0;

        grid.add(new Label("Status:"), 0, row);
        grid.add(new Label(status), 1, row++);

        grid.add(new Label("License Type:"), 0, row);
        grid.add(new Label(licenseType), 1, row++);

        grid.add(new Label("Customer Name:"), 0, row);
        grid.add(new Label(customerName), 1, row++);

        grid.add(new Label("Customer Email:"), 0, row);
        grid.add(new Label(customerEmail), 1, row++);

        grid.add(new Label("Expiry Date:"), 0, row);
        grid.add(new Label(expiryDate), 1, row++);

        grid.add(new Label("Machine ID:"), 0, row);

        TextField machineIdField = new TextField(machineId);
        machineIdField.setEditable(false);
        machineIdField.setPrefColumnCount(16);
        machineIdField.setMaxWidth(180);

        copyMachineIdBtn = new Button("Copy");
        copyMachineIdBtn.setOnAction(e -> copyMachineId());

        HBox machineBox = new HBox(10, machineIdField, copyMachineIdBtn);

        grid.add(machineBox, 1, row++);

        grid.add(new Label("Application Version:"), 0, row);
        grid.add(new Label(AppConstants.APP_VERSION), 1, row++);

        layout.getChildren().addAll(
                titleLabel,
                new Separator(),
                grid
        );

        if (!activated) {

            Separator separator = new Separator();

            Label activationLabel = new Label("Offline License Activation");
            activationLabel.getStyleClass().add("sub-header");

            activationKeyArea = new TextArea();
            activationKeyArea.setPromptText("Paste your license key here...");
            activationKeyArea.setWrapText(true);
            activationKeyArea.setPrefRowCount(5);

            activateLicenseBtn = new Button("Activate License");
            activateLicenseBtn.getStyleClass().add("btn-primary");
            activateLicenseBtn.setOnAction(e -> activateLicense());

            Label noteLabel = new Label(
                    "To activate BrandPDF Pro:\n\n" +
                            "1. Copy the Machine ID.\n" +
                            "2. Send it to the BrandPDF Pro administrator.\n" +
                            "3. Paste the received License Key.\n" +
                            "4. Click 'Activate License'."
            );

            noteLabel.setWrapText(true);

            layout.getChildren().addAll(
                    separator,
                    activationLabel,
                    activationKeyArea,
                    activateLicenseBtn,
                    noteLabel
            );
        }

        return layout;
    }

    /**
     * Standard layout window detailing runtime metrics and information regarding software builds.
     *
     * @return Context details dashboard pane element container.
     */
    private Node createAboutPage() {
        logger.debug("Compiling UI component framework for About Dashboard workspace view.");

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(30));
        layout.getStyleClass().add("main-pane");

        Label title = new Label("About BrandPDF Pro");
        title.getStyleClass().add("section-header");

        Label appNameLabel = new Label(AppConstants.APP_NAME);
        appNameLabel.getStyleClass().add("sub-header");

        Label versionLabel = new Label("Version: " + AppConstants.APP_VERSION);
        Label companyLabel = new Label("Developed by " + AppConstants.COMPANY_NAME);

        Label descriptionLabel = new Label("Professional PDF Branding & Batch Processing Solution");
        descriptionLabel.setWrapText(true);

        Label featuresLabel = new Label("""
                Features:
                
                • Header & Footer Branding
                • Batch PDF Processing
                • Processing Profiles
                • Drag & Drop Support
                • Validation Framework
                • Logging & Diagnostics
                • Theme Support
                """);
        featuresLabel.setWrapText(true);

        Label copyrightLabel = new Label(AppConstants.COPYRIGHT);

        layout.getChildren().addAll(title, new Separator(), appNameLabel, versionLabel, companyLabel, descriptionLabel, new Separator(), featuresLabel, new Separator(), copyrightLabel);

        return layout;
    }

    private void copyMachineId() {

        try {

            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            content.putString(activationManager.getMachineId());
            clipboard.setContent(content);
            statusLabel.setText("🟢 Machine ID copied");
        } catch (Exception ex) {
            logger.error("Failed to copy machine id", ex);
            statusLabel.setText("❌ Copy failed");
        }
    }

    private void activateLicense() {

        try {
            String licenseKey = activationKeyArea.getText().trim();
            if (licenseKey.isEmpty()) {
                statusLabel.setText("❌ Enter license key");
                return;
            }
            LicenseInfo licenseInfo = activationManager.activateLicense(licenseKey);
            refreshLicensePage();
            statusLabel.setText("🟢 License Activated");
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Activation Successful");
           // alert.setHeaderText(null);
           // alert.setContentText("License activated successfully.\n\n" + "Please restart BrandPDF Pro.");
           // alert.showAndWait();

        } catch (Exception ex) {
            logger.error("License activation failed", ex);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Activation Failed");
            alert.setHeaderText(null);
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
            statusLabel.setText("❌ Activation Failed");
        }
    }

    /**
     * Refresh License Management page after activation/deactivation.
     */
    private void refreshLicensePage() {

        // Recreate the page with latest license information
        licensePage = createLicensePage();

        // If the user is currently viewing the License page,
        // refresh it immediately.
        if (currentPage == AppPage.LICENSE) {

            contentArea.getChildren().setAll(licensePage);

            licensePageBtn.getStyleClass().remove("active");
            licensePageBtn.getStyleClass().add("active");
        }
    }

    private enum AppPage {
        PROCESS, PROFILES, SETTINGS, LICENSE, ABOUT
    }
}