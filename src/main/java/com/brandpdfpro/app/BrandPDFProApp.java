package com.brandpdfpro.app;

import com.brandpdfpro.model.ProcessingProfile;
import com.brandpdfpro.service.*;
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
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.stage.Stage;

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

    // =========================================================================
    // Core Business Logic Services
    // =========================================================================
    private final FileService fileService = new FileService();
    private final TemplateService templateService = new TemplateService();
    private final PdfProcessorService pdfProcessorService = new PdfProcessorService();
    private final SettingsService settingsService = new SettingsService();
    private final BatchProcessorService batchProcessorService = new BatchProcessorService();
    private final AppConfigService appConfigService = new AppConfigService();
    private final ProfileService profileService = new ProfileService();

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

    private TextField headerField;
    private TextField footerField;
    private TextField pdfField;
    private TextField outputFolderField;
    private TextField headerHeightField;
    private TextField companyNameField;
    private TextField footerHeightField;
    private TextField inputFolderField;

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

    // Status Indicator
    private Label statusLabel;

    //Progress Bar
    private ProgressBar progressBar;
    private Label progressLabel;
    private HBox bottomSection;
    private Scene mainScene;

    /**
     * The main entry point for the application.
     *
     * @param args Command-line arguments.
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Entry hook initializing the layout view pipeline, resolving NullPointer risks
     * by building pages prior to assigning core interactive event handlers.
     *
     * @param stage The primary stage for this application.
     */
    @Override
    public void start(Stage stage) {
        BorderPane root = new BorderPane();

        // 1. Core Structural Initializations
        VBox sidebar = createSidebar();
        sidebar.setMinWidth(240);
        sidebar.setPrefWidth(240);
        root.setLeft(sidebar);

        // 2. Build View Pages (Resolves Bug #1 NPE Vulnerabilities during runtime assignment)
        processPage = createProcessPage();
        profilesPage = createProfilesPage();
        settingsPage = createSettingsPage();
        licensePage = createLicensePage();
        aboutPage = createAboutPage();

        contentArea = new StackPane();
        contentArea.getStyleClass().add("content-area");

        // Establish default starting workspace page
        contentArea.getChildren().add(processPage);
        root.setCenter(contentArea);

        bottomSection = createBottomSection();
        root.setBottom(bottomSection);

        // 3. Operational Setup and Workspace Parameter Sync
        registerEvents(stage);
        registerDragAndDrop();
        loadSavedTemplates();

        // Fluid Scroll Pane container keeping dynamic responsiveness regardless of screen dimensions
        ScrollPane scrollPane = new ScrollPane(root);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setPannable(true);

        mainScene = new Scene(scrollPane, appConfigService.getAppWidth(), appConfigService.getAppHeight());

        applyTheme(false);

        stage.setTitle(appConfigService.getAppTitle() + " - Professional PDF Branding Tool");
        try {
            stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/brandpdfpro.png"))));
        } catch (Exception ignored) {}

        stage.setScene(mainScene);
        stage.show();
    }

    /**
     * Builds an upgraded dashboard statistics notification floor segment using a
     * clean layout matrix design.
     *
     * @return An HBox container configured as the application status bar floor.
     */
    private HBox createBottomSection() {
        HBox container = new HBox(15);
        container.getStyleClass().add("status-bar-container");
        container.setPadding(new Insets(12, 24, 12, 24));
        container.setAlignment(Pos.CENTER_LEFT);

        statusLabel = new Label("🟢 SYSTEM STATE: READY");
        statusLabel.getStyleClass().add("status-bar-label");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        container.getChildren().addAll(statusLabel, spacer);
        return container;
    }

    /**
     * Binds core click functional actions, execution commands, toggles,
     * and view management listeners to page items.
     *
     * @param stage The primary stage window required for dialog mappings.
     */
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
    }

    /**
     * Cycles workspace focus targets, clearing nodes while managing menu selection classes
     * to resolve missing tracking markers.
     *
     * @param page      The target Node view layout to swap in.
     * @param activeBtn The sidebar navigation button to visually toggle as active.
     */
    private void setActivePage(Node page, Button activeBtn) {
        contentArea.getChildren().clear();
        contentArea.getChildren().add(page);

        processPageBtn.getStyleClass().remove("active");
        profilesPageBtn.getStyleClass().remove("active");
        settingsPageBtn.getStyleClass().remove("active");
        licensePageBtn.getStyleClass().remove("active");
        aboutPageBtn.getStyleClass().remove("active");
        activeBtn.getStyleClass().add("active");
    }

    /**
     * Switches dynamic configuration presentation stylesheet arrays to match user theme choices.
     *
     * @param isDarkTheme Set to true to inject Dark Mode styling rules; false for Light Mode rules.
     */
    private void applyTheme(boolean isDarkTheme) {
        mainScene.getStylesheets().clear();
        try {
            if (isDarkTheme) {
                mainScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/brandpdfpro-dark.css")).toExternalForm());
                themeToggleBtn.setText("☀️ Light Theme Mode");
                themeToggleBtn.setSelected(false);
            } else {
                mainScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/brandpdfpro-light.css")).toExternalForm());
                themeToggleBtn.setText("🌙 Dark Theme Mode");
                themeToggleBtn.setSelected(true);
            }
        } catch (NullPointerException e) {
            System.err.println("Styling stylesheet path resource missing. Verify compilation target directories.");
        }
    }

    /**
     * Automatically attempts to restore cached template file references from persistent service levels.
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
            statusLabel.setText("🟢 SYSTEM STATE: SAVED TEMPLATES LOADED");
        }
    }

    /**
     * Triggers filesystem file picker dialog to resolve header corporate asset pathways.
     *
     * @param stage Root frame element required to bind window dialog ownership.
     */
    private void browseHeader(Stage stage) {
        File file = fileService.chooseImage(stage, "Select Header Template");
        if (file == null) return;
        try {
            templateService.saveHeaderTemplate(file);
            headerField.setText(file.getAbsolutePath());
            loadImagePreview(file, headerPreview);
            statusLabel.setText("🟢 SYSTEM STATE: HEADER IMAGE SAVED");
        } catch (IOException ex) {
            statusLabel.setText("🔴 CRITICAL: HEADER ASSET PERSISTENCE ERROR");
        }
    }

    /**
     * Triggers filesystem file picker dialog to resolve footer corporate asset pathways.
     *
     * @param stage Root frame element required to bind window dialog ownership.
     */
    private void browseFooter(Stage stage) {
        File file = fileService.chooseImage(stage, "Select Footer Template");
        if (file == null) return;
        try {
            templateService.saveFooterTemplate(file);
            footerField.setText(file.getAbsolutePath());
            loadImagePreview(file, footerPreview);
            statusLabel.setText("🟢 SYSTEM STATE: FOOTER IMAGE SAVED");
        } catch (IOException ex) {
            statusLabel.setText("🔴 CRITICAL: FOOTER ASSET PERSISTENCE ERROR");
        }
    }

    /**
     * Displays a native system file selector configured explicitly for isolated PDF document targets.
     *
     * @param stage Parent window context mapping framework.
     */
    private void browsePdf(Stage stage) {
        File file = fileService.choosePdf(stage);
        if (file != null) {
            pdfField.setText(file.getAbsolutePath());
            statusLabel.setText("🟢 SYSTEM STATE: SOURCE PDF MATCHED");
        }
    }

    /**
     * Spawns directory chooser mappings to feed batch directory ingestion routines.
     *
     * @param stage Primary structural system view target stage.
     */
    private void browseInputFolder(Stage stage) {
        File folder = fileService.chooseDirectory(stage);
        if (folder != null) {
            inputFolderField.setText(folder.getAbsolutePath());
            statusLabel.setText("🟢 SYSTEM STATE: PROCESSING TARGET AREA INITIALIZED");
        }
    }

    /**
     * Configures the target output file system destination directory location context pointer.
     *
     * @param stage Operational layout window layer.
     */
    private void browseOutputFolder(Stage stage) {
        File folder = fileService.chooseDirectory(stage);
        if (folder != null) {
            outputFolderField.setText(folder.getAbsolutePath());
            statusLabel.setText("🟢 Output Folder Selected");
        }
    }

    /**
     * Executes asynchronous file transformation workflows over background processing threads.
     * Integrates direct progress listener handlers that cleanly drop tracking bars when completed.
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
                    File outputFolder = new File(outputFolderField.getText());

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
                                statusLabel.setText("🟢 BATCH METRICS: " + processedCount + " WORKSPACE FILES PROCESSED")
                        );
                    } else {
                        pdfProcessorService.processPdf(
                                headerFile, footerFile, pdfFile, outputFolder,
                                addPageNumbers, addDocumentTag, documentTag, preventOverlap,
                                scaleTheContent, compressTheContent, increasePageSize
                        );
                        Platform.runLater(() ->
                                statusLabel.setText("✅ File processed successfully")
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
                progressLabel.setText("Processing finished successfully.");
                statusLabel.setText("Success: PDF generation complete");
                processBtn.setDisable(false);
                hideProgress(); // Resolves Bug #7 (Guarantees cleanup tracking elements erase)
            });

            task.setOnFailed(event -> {
                processBtn.setDisable(false);
                hideProgress();
                Throwable ex = task.getException();
                statusLabel.setText("Error: Processing failed");
                if (ex != null) {
                    progressLabel.setText(ex.getMessage());
                }
            });

            // Fire off execution in a standard standalone background Daemon context
            Thread thread = new Thread(task);
            thread.setDaemon(true);
            thread.start();

        } catch (Exception ex) {
            statusLabel.setText("❌ Initialization failed: " + ex.getMessage());
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
     * @param imageFile The platform file reference directing to target visual templates.
     * @param imageView The destination display component container node.
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
        documentTagComboBox.setDisable(!documentTagCheckBox.isSelected());
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

    /**
     * Activates progress tracking nodes in the user interface.
     * Unhides the progress bar and layout status labels, resets the progress indices
     * back to zero, and primes the tracking string text to notify the user of execution initiation.
     */
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

    /**
     * Deactivates and hides progress tracking nodes from the user interface viewport.
     * Call this cleanup wrapper when active processing loops exit or terminate.
     */
    private void hideProgress() {
        if (progressBar != null && progressLabel != null) {
            progressBar.setVisible(false);
            progressLabel.setVisible(false);
            bottomSection.getChildren().removeAll(progressBar, progressLabel);
        }
    }

    /**
     * Queries background profile layers, resetting structural ComboBox dropdown indices.
     */
    private void refreshProfiles() {
        profileComboBox.getItems().clear();
        profileComboBox.getItems().addAll(profileService.getAllProfiles());
        if (!profileComboBox.getItems().isEmpty()) {
            profileComboBox.setValue(profileComboBox.getItems().get(0));
        }
    }

    /**
     * Compiles interactive UI field positions, generating structural saved profile records.
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
                statusLabel.setText("❌ CONFIG ALARM: ATTRIBUTE KEY NAME CANNOT RUN EMPTY");
                return;
            }

            ProcessingProfile profile = new ProcessingProfile();
            profile.setProfileName(profileName);
            profile.setHeaderTemplatePath(templateService.getHeaderTemplate() != null ? templateService.getHeaderTemplate().getAbsolutePath() : "");
            profile.setFooterTemplatePath(templateService.getFooterTemplate() != null ? templateService.getFooterTemplate().getAbsolutePath() : "");
            profile.setAddPageNumbers(pageNumberCheckBox.isSelected());
            profile.setAddDocumentTag(documentTagCheckBox.isSelected());
            profile.setDocumentTag(documentTagComboBox.getValue());
            profile.setPreventOverlap(preventOverlapCheckBox.isSelected());
            profile.setScaleContent(scaleContentRadio.isSelected());
            profile.setCompressContent(compressContentRadio.isSelected());
            profile.setIncreasePageSize(increasePageSizeRadio.isSelected());

            profileService.saveProfile(profile);
            refreshProfiles();
            profileComboBox.setValue(profileName);
            statusLabel.setText("🟢 PROFILES: COMPILED PERSISTENT SAVED TEMPLATE STATE");
        } catch (Exception ex) {
            statusLabel.setText("❌ CONFIG UNEXPECTED ERROR CAUGHT PREVENTING DEPLOYMENT SAVE");
        }
    }

    /**
     * Resolves Bug #3 Data Leak anomalies. Synchronizes application values, checkbox
     * states, layout boundaries, and mutual exclusion matrices cleanly upon choice activation.
     */
    private void loadProfile() {
        try {
            String profileName = profileComboBox.getValue();
            if (profileName == null || profileName.trim().isEmpty()) {
                statusLabel.setText("❌ PREPARATION REJECTED: UNASSIGNED TARGET RETRIEVAL MATRIX KEY");
                return;
            }

            ProcessingProfile profile = profileService.loadProfile(profileName);
            if (profile.getHeaderTemplatePath() != null && !profile.getHeaderTemplatePath().isEmpty()) {
                File headerFile = new File(profile.getHeaderTemplatePath());
                templateService.saveHeaderTemplate(headerFile);
                headerField.setText(headerFile.getAbsolutePath());
                loadImagePreview(headerFile, headerPreview);
            }

            // Handle Footer Template Synchronization
            if (profile.getFooterTemplatePath() != null && !profile.getFooterTemplatePath().isEmpty()) {
                File footerFile = new File(profile.getFooterTemplatePath());
                templateService.saveFooterTemplate(footerFile);
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
            statusLabel.setText("❌ SYNC ERROR: CRITICAL CONFLICT PARSING CACHED SYSTEM FILE ENTRIES");
        }
    }

    /**
     * Erases historical targeted runtime profile system parameters from the database or filesystem.
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

            if (profileService.deleteProfile(profileName)) {
                refreshProfiles();
                profileComboBox.setValue(null);
                statusLabel.setText("🟢 PROFILES: ERASED CACHED PERSISTENT CONFIG DATA");
            } else {
                statusLabel.setText("❌ ATTEMPT REJECTED: SPECIFIED REGISTRY PATH TARGET ABSENT");
            }
        } catch (Exception ex) {
            statusLabel.setText("❌ SYSTEM FAILURE ENCOUNTERED TRUNCATING STORAGE ENTRIES");
        }
    }

    // =========================================================================
    // Layout Factory Generation Pipelines
    // =========================================================================

    /**
     * Assembles the navigation menu container, packing core routing items.
     *
     * @return Fully structured navigation VBox node wrapper.
     */
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

    /**
     * Generates primary file interaction control dashboards utilizing dynamic row variables.
     *
     * @return Generated Grid layout page addressing workflow operations.
     */
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
        grid.add(headerPreview, 1, row); // Renders neatly beneath the text field
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
        documentTagComboBox.getItems().addAll("CONFIDENTIAL", "PUBLIC RELEASE", "PROPRIETARY", "RESTRICTED");
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

        optionBox.getChildren().addAll(
                batchProcessingCheckBox,
                pageNumberCheckBox,
                classificationBox,
                preventOverlapCheckBox,
                radioBox
        );
        grid.add(optionBox, 1, row, 2, 1);
        row++;

        // --- ACTION BUTTON ---

        processBtn = new Button("Apply Branding");
        processBtn.getStyleClass().add("btn-primary");
        processBtn.setPrefWidth(200); // More standard size for clear form finalization
        grid.add(processBtn, 1, row);

        return grid;
    }

    /**
     * Resolves Bug #5 Data Isolations. Configuration profile view layouts are isolated here.
     *
     * @return Generated container holding functional components routing template setups.
     */
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

    /**
     * Resolves Bug #5 Data Isolations. Base height variables and margins are isolated here using user row logic.
     *
     * @return Structural settings pane component.
     */
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

        headerHeightField = new TextField(String.valueOf(settingsService.getHeaderHeight()));
        footerHeightField = new TextField(String.valueOf(settingsService.getFooterHeight()));
        companyNameField = new TextField(settingsService.getCompanyName());

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
     * Constructs static metadata segments describing production environments.
     *
     * @return Generated interface containing software licensing scopes.
     */
    private Node createLicensePage() {
        VBox layout = new VBox(12);
        layout.setPadding(new Insets(30));
        layout.getStyleClass().add("main-pane");

        Label l1 = new Label("License Information");
        l1.getStyleClass().add("section-header");
        layout.getChildren().addAll(
                l1,
                new Separator(),
                new Label("License Status: Verified Enterprise SaaS Run-Thread"),
                new Label("Registered User Scope ID: Tier-1 Production Environment Instance")
        );
        return layout;
    }

    /**
     * Standard layout window detailing information regarding software builds.
     *
     * @return Context details dashboard pane element.
     */
    private Node createAboutPage() {
        VBox layout = new VBox(12);
        layout.setPadding(new Insets(30));
        layout.getStyleClass().add("main-pane");

        Label title = new Label("About BrandPDF Pro");
        title.getStyleClass().add("section-header");
        layout.getChildren().addAll(
                title,
                new Separator(),
                new Label("Version: 2.6.4 Standard Edition"),
                new Label("Designed for high-volume corporate document processing.")
        );
        return layout;
    }
}