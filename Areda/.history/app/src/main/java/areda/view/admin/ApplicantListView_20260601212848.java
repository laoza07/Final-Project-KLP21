package areda.view.admin;

import areda.model.DatabaseManager;
import areda.model.Lamaran;
import areda.model.Lowongan;
import areda.view.components.AvatarComponent;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;

public class ApplicantListView extends StackPane {
    private VBox mainContent;
    private StackPane modalOverlay;
    private TableView<Lamaran> tableView;
    private ComboBox<String> lowonganFilter;
    private TextField searchField;

    private static final String UPLOAD_FOLDER = "uploads";
    private static final String CV_FOLDER = UPLOAD_FOLDER + File.separator + "cv";
    private static final String ML_FOLDER = UPLOAD_FOLDER + File.separator + "motlet";

    public ApplicantListView() {

        createUploadDirectories();

        this.setStyle("-fx-background-color: white;");
        mainContent = new VBox(20);
        mainContent.setPadding(new Insets(30));
        mainContent.setAlignment(Pos.TOP_LEFT);

        HBox header = createHeader();
        HBox filterBar = createFilterBar();

        tableView = new TableView<>();
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        setupTableColumns();
        refreshTable();

        VBox.setVgrow(tableView, Priority.ALWAYS);
        mainContent.getChildren().addAll(header, filterBar, tableView);

        modalOverlay = new StackPane();
        modalOverlay.getStyleClass().add("modal-overlay");
        modalOverlay.setVisible(false);
        this.getChildren().addAll(mainContent, modalOverlay);
    }

    private void createUploadDirectories() {
        try {
            Files.createDirectories(Paths.get(CV_FOLDER));
            Files.createDirectories(Paths.get(ML_FOLDER));
        } catch (IOException e) {
            System.err.println("Gagal membuat folder upload: " + e.getMessage());
        }
    }

    private void setupTableColumns() {

        TableColumn<Lamaran, String> colNo = new TableColumn<>("No.");
        colNo.setPrefWidth(50);
        colNo.setCellFactory(col -> new TableCell<Lamaran, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : String.valueOf(getIndex() + 1));
                setAlignment(Pos.CENTER);
            }
        });

        TableColumn<Lamaran, String> colName = new TableColumn<>("Nama Pelamar");
        colName.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNamaPelamar()));
        colName.setCellFactory(col -> new TableCell<Lamaran, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    HBox nameBox = new HBox(10);
                    nameBox.setAlignment(Pos.CENTER_LEFT);
                    AvatarComponent avatar = new AvatarComponent(item, 32);
                    Label lName = new Label(item);
                    lName.setFont(Font.font("Arial", FontWeight.BOLD, 13));
                    lName.setTextFill(Color.web("#2D3748"));
                    nameBox.getChildren().addAll(avatar, lName);
                    setGraphic(nameBox);
                }
            }
        });

        TableColumn<Lamaran, String> colDiv = new TableColumn<>("Divisi");
        colDiv.setCellValueFactory(data -> {
            Lowongan l = DatabaseManager.getLowonganById(data.getValue().getIdLowongan());
            return new SimpleStringProperty(l != null ? l.getDivisi() : "Unknown");
        });

        TableColumn<Lamaran, String> colDate = new TableColumn<>("Tanggal Melamar");
        colDate.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTanggal()));

        TableColumn<Lamaran, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStatusTahapan()));
        colStatus.setCellFactory(col -> new TableCell<Lamaran, String>() {
            private final Label pill = new Label();
            {
                setAlignment(Pos.CENTER);
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    pill.setText(item);
                    pill.getStyleClass().clear();
                    String pillClass = switch (item) {
                        case "Diterima" -> "pill-diterima";
                        case "Ditolak" -> "pill-ditolak";
                        case "Interview" -> "pill-interview";
                        default -> "pill-default";
                    };
                    pill.getStyleClass().add(pillClass);
                    setGraphic(pill);
                }
            }
        });

        TableColumn<Lamaran, Void> colCV = new TableColumn<>("CV");
        colCV.setPrefWidth(80);
        colCV.setCellFactory(col -> new TableCell<Lamaran, Void>() {
            private final Button btnCV = new Button("📄 CV");
            {
                btnCV.getStyleClass().add("btn-outline");
                btnCV.setStyle(
                        "-fx-text-fill: #2B6CB0; -fx-border-color: #2B6CB0; -fx-font-size: 11px; -fx-padding: 5 10;");
                btnCV.setOnAction(e -> {
                    Lamaran lamaran = getTableView().getItems().get(getIndex());
                    openPdfFile(lamaran.getCvPath(), "CV", CV_FOLDER);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Lamaran lamaran = getTableView().getItems().get(getIndex());
                    boolean hasFile = lamaran.getCvPath() != null && !lamaran.getCvPath().isEmpty()
                            && !lamaran.getCvPath().startsWith("📄");
                    btnCV.setDisable(!hasFile);
                    setGraphic(btnCV);
                }
                setAlignment(Pos.CENTER);
            }
        });

        TableColumn<Lamaran, Void> colML = new TableColumn<>("Motivation Letter");
        colML.setPrefWidth(120);
        colML.setCellFactory(col -> new TableCell<Lamaran, Void>() {
            private final Button btnML = new Button("📝 ML");
            {
                btnML.getStyleClass().add("btn-outline");
                btnML.setStyle(
                        "-fx-text-fill: #2B6CB0; -fx-border-color: #2B6CB0; -fx-font-size: 11px; -fx-padding: 5 10;");
                btnML.setOnAction(e -> {
                    Lamaran lamaran = getTableView().getItems().get(getIndex());
                    openPdfFile(lamaran.getMotletPath(), "Motivation Letter", ML_FOLDER);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Lamaran lamaran = getTableView().getItems().get(getIndex());
                    boolean hasFile = lamaran.getMotletPath() != null && !lamaran.getMotletPath().isEmpty()
                            && !lamaran.getMotletPath().startsWith("📄");
                    btnML.setDisable(!hasFile);
                    setGraphic(btnML);
                }
                setAlignment(Pos.CENTER);
            }
        });

        TableColumn<Lamaran, Void> colAction = new TableColumn<>("Aksi");
        colAction.setCellFactory(col -> new TableCell<Lamaran, Void>() {
            private final Button btn = new Button("Detail");
            {
                btn.getStyleClass().add("btn-edit");
                btn.setOnAction(e -> {
                    Lamaran lamaran = getTableView().getItems().get(getIndex());
                    showDetailModal(lamaran);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
                setAlignment(Pos.CENTER);
            }
        });

        tableView.getColumns().addAll(colNo, colName, colDiv, colDate, colStatus, colCV, colML, colAction);
    }

    private void openPdfFile(String filePath, String fileType, String expectedFolder) {
        if (filePath == null || filePath.isEmpty() || filePath.startsWith("📄")) {
            showAlert("Informasi", fileType + " belum diunggah oleh pelamar.");
            return;
        }

        String cleanPath = filePath.replace("📄 ", "").trim();
        File pdfFile = new File(expectedFolder, cleanPath);

        if (!pdfFile.exists()) {
            pdfFile = new File(cleanPath);
        }
        if (!pdfFile.exists()) {
            pdfFile = new File(System.getProperty("user.dir"), cleanPath);
        }

        if (!pdfFile.exists()) {
            String message = fileType + " tidak ditemukan.\n\nPath tersimpan: " + cleanPath +
                    "\nWorking directory: " + System.getProperty("user.dir") +
                    "\n\nSilakan buka manual melalui File Explorer.";
            showAlert("File Tidak Ditemukan", message);
            return;
        }

        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
                Desktop.getDesktop().open(pdfFile);
            } else {
                showManualOpenDialog(fileType, pdfFile.getAbsolutePath());
            }
        } catch (IOException | UnsupportedOperationException | SecurityException e) {
            System.err.println("Error membuka file: " + e.getMessage());
            showManualOpenDialog(fileType, pdfFile.getAbsolutePath());
        }
    }

    private void showManualOpenDialog(String fileType, String absolutePath) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Buka File Manual");
        alert.setHeaderText(fileType + " tidak dapat dibuka otomatis");
        alert.setContentText("Silakan buka file berikut secara manual:\n\n" + absolutePath);

        ButtonType copyBtn = new ButtonType("Salin Path", ButtonBar.ButtonData.OK_DONE);
        ButtonType closeBtn = new ButtonType("Tutup", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(copyBtn, closeBtn);

        alert.showAndWait().ifPresent(response -> {
            if (response == copyBtn) {
                Clipboard clipboard = Clipboard.getSystemClipboard();
                ClipboardContent content = new ClipboardContent();
                content.putString(absolutePath);
                clipboard.setContent(content);
                showAlert("Tersalin", "Path file telah disalin ke clipboard.");
            }
        });
    }

    private void showDetailModal(Lamaran app) {

        VBox modal = new VBox(25);
        modal.setMaxSize(650, 550);
        modal.setPadding(new Insets(30));
        modal.getStyleClass().add("modal-card");

        ScrollPane modalScroll = new ScrollPane(modal);
        modalScroll.setFitToWidth(true);
        modalScroll.setStyle(
                "-fx-background-color: transparent; -fx-background: transparent; -fx-border-color: transparent;");
        modalScroll.setMaxSize(670, 600);

        HBox top = new HBox();
        top.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("Detail Lamaran");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        title.setTextFill(Color.web("#1A202C"));
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Button close = new Button("X");
        close.getStyleClass().add("btn-outline");
        close.setOnAction(e -> hideModal());
        top.getChildren().addAll(title, spacer, close);

        HBox contentBox = new HBox(30);

        VBox profileInfo = new VBox(20);
        profileInfo.setPrefWidth(320);

        HBox nameBox = new HBox(15);
        nameBox.setAlignment(Pos.CENTER_LEFT);
        AvatarComponent avatar = new AvatarComponent(app.getNamaPelamar(), 45);
        VBox nameText = new VBox(2);
        Label nameLbl = new Label(app.getNamaPelamar());
        nameLbl.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        nameLbl.setTextFill(Color.web("#1A202C"));
        Label emailLbl = new Label(app.getEmailPelamar());
        emailLbl.setFont(Font.font("Arial", 12));
        emailLbl.setTextFill(Color.web("#718096"));
        nameText.getChildren().addAll(nameLbl, emailLbl);
        nameBox.getChildren().addAll(avatar, nameText);

        VBox statusBox = new VBox(5);
        Label statusLbl = new Label("Ubah Status Tahapan");
        statusLbl.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        statusLbl.setTextFill(Color.web("#4A5568"));
        ComboBox<String> statusCombo = new ComboBox<>();
        statusCombo.getItems().addAll("Seleksi Berkas", "Interview", "Diterima", "Ditolak");
        statusCombo.setValue(app.getStatusTahapan());
        statusCombo.getStyleClass().add("form-input");
        statusCombo.setMaxWidth(Double.MAX_VALUE);
        statusBox.getChildren().addAll(statusLbl, statusCombo);

        VBox dateBox = new VBox(5);
        Label dateLbl = new Label("Jadwal Interview (Opsional)");
        dateLbl.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        dateLbl.setTextFill(Color.web("#4A5568"));
        DatePicker interviewDatePicker = new DatePicker();
        interviewDatePicker.getStyleClass().add("form-input");
        interviewDatePicker.setMaxWidth(Double.MAX_VALUE);
        try {
            if (app.getTanggalInterview() != null && !app.getTanggalInterview().isEmpty())
                interviewDatePicker.setValue(java.time.LocalDate.parse(app.getTanggalInterview()));
        } catch (Exception ignored) {
        }
        dateBox.getChildren().addAll(dateLbl, interviewDatePicker);

        Button saveBtn = new Button("Simpan Perubahan");
        saveBtn.getStyleClass().add("btn-primary");
        saveBtn.setMaxWidth(Double.MAX_VALUE);
        saveBtn.setOnAction(e -> {
            DatabaseManager.updateLamaranStatus(app.getIdLamaran(), statusCombo.getValue());
            if (interviewDatePicker.getValue() != null)
                DatabaseManager.updateLamaranInterviewDate(app.getIdLamaran(),
                        interviewDatePicker.getValue().toString());
            refreshTable();
            hideModal();
        });

        profileInfo.getChildren().addAll(nameBox, statusBox, dateBox, saveBtn);

        VBox docs = new VBox(15);
        docs.setPadding(new Insets(20));
        docs.setStyle("-fx-background-color: #EBF8FF; -fx-background-radius: 15;");
        docs.setPrefWidth(220);

        Label docsTitle = new Label("Dokumen Terunggah");
        docsTitle.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        docsTitle.setTextFill(Color.web("#2B6CB0"));

        VBox cvBox = new VBox(5);
        Label cvLabel = new Label("Curriculum Vitae:");
        cvLabel.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        cvLabel.setTextFill(Color.web("#1A202C"));

        HBox cvRow = new HBox(10);
        cvRow.setAlignment(Pos.CENTER_LEFT);
        Label cvFileName = new Label(
                (app.getCvPath() == null || app.getCvPath().isEmpty() || app.getCvPath().startsWith("📄"))
                        ? "❌ Belum ada CV"
                        : "📄 " + app.getCvPath().replace("📄 ", ""));
        cvFileName.setWrapText(true);
        cvFileName.setMaxWidth(150);
        cvFileName.setTextFill(Color.web("#2D3748"));

        Button btnOpenCV = new Button("🔓");
        btnOpenCV.setPrefSize(25, 25);
        btnOpenCV.setStyle(
                "-fx-background-color: #2B6CB0; -fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand; -fx-font-weight: bold;");
        btnOpenCV.setOnAction(e -> openPdfFile(app.getCvPath(), "CV", CV_FOLDER));
        btnOpenCV.setDisable(app.getCvPath() == null || app.getCvPath().isEmpty() || app.getCvPath().startsWith("📄"));

        cvRow.getChildren().addAll(cvFileName, btnOpenCV);
        cvBox.getChildren().addAll(cvLabel, cvRow);

        VBox mlBox = new VBox(5);
        Label mlLabel = new Label("Motivation Letter:");
        mlLabel.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        mlLabel.setTextFill(Color.web("#1A202C"));

        HBox mlRow = new HBox(10);
        mlRow.setAlignment(Pos.CENTER_LEFT);
        Label mlFileName = new Label(
                (app.getMotletPath() == null || app.getMotletPath().isEmpty() || app.getMotletPath().startsWith("📄"))
                        ? "❌ Belum ada ML"
                        : "📄 " + app.getMotletPath().replace("📄 ", ""));
        mlFileName.setWrapText(true);
        mlFileName.setMaxWidth(150);
        mlFileName.setTextFill(Color.web("#2D3748"));

        Button btnOpenML = new Button("🔓");
        btnOpenML.setPrefSize(25, 25);
        btnOpenML.setStyle(
                "-fx-background-color: #2B6CB0; -fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand; -fx-font-weight: bold;");
        btnOpenML.setOnAction(e -> openPdfFile(app.getMotletPath(), "Motivation Letter", ML_FOLDER));
        btnOpenML.setDisable(
                app.getMotletPath() == null || app.getMotletPath().isEmpty() || app.getMotletPath().startsWith("📄"));

        mlRow.getChildren().addAll(mlFileName, btnOpenML);
        mlBox.getChildren().addAll(mlLabel, mlRow);

        docs.getChildren().addAll(docsTitle, cvBox, mlBox);
        contentBox.getChildren().addAll(profileInfo, docs);

        modal.getChildren().addAll(top, contentBox);

        modalOverlay.getChildren().clear();
        modalOverlay.getChildren().add(modalScroll);
        modalOverlay.setVisible(true);
        mainContent.setDisable(true);
    }

    private void hideModal() {
        modalOverlay.setVisible(false);
        mainContent.setDisable(false);
    }

    private void refreshTable() {
        String selectedDivisi = lowonganFilter.getValue();
        String searchText = searchField.getText().toLowerCase();

        List<Lamaran> filtered = DatabaseManager.getAllLamaran().stream().filter(lamaran -> {
            Lowongan l = DatabaseManager.getLowonganById(lamaran.getIdLowongan());
            String divisi = (l != null) ? l.getDivisi() : "Unknown";
            boolean matchDivisi = selectedDivisi.equals("Semua") || divisi.equals(selectedDivisi);
            boolean matchSearch = searchText.isEmpty() || lamaran.getNamaPelamar().toLowerCase().contains(searchText);
            return matchDivisi && matchSearch;
        }).collect(Collectors.toList());

        tableView.getItems().setAll(filtered);
        if (filtered.isEmpty())
            tableView.setPlaceholder(new Label("Tidak ada data pelamar."));
    }

    private HBox createHeader() {
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        VBox titleBox = new VBox(5);
        Label title = new Label("Pelamar");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        title.setTextFill(Color.web("#1A202C"));
        Label subtitle = new Label("Daftar pelamar pekerjaan");
        subtitle.setFont(Font.font("Arial", 12));
        subtitle.setTextFill(Color.web("#718096"));
        titleBox.getChildren().addAll(title, subtitle);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        header.getChildren().addAll(titleBox, spacer);
        return header;
    }

    private HBox createFilterBar() {
        HBox bar = new HBox(15);
        bar.setAlignment(Pos.CENTER_LEFT);

        lowonganFilter = new ComboBox<>();
        lowonganFilter.getItems().addAll(DatabaseManager.getAllDivisi());
        lowonganFilter.setValue("Semua");
        lowonganFilter.getStyleClass().add("form-input");
        lowonganFilter.setPrefWidth(200);
        lowonganFilter.setOnAction(e -> refreshTable());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        searchField = new TextField();
        searchField.setPromptText("🔍 Cari Nama...");
        searchField.getStyleClass().add("form-input");
        searchField.setPrefWidth(200);
        searchField.textProperty().addListener((obs, oldVal, newVal) -> refreshTable());

        bar.getChildren().addAll(lowonganFilter, spacer, searchField);
        return bar;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(title.equals("Error") ? Alert.AlertType.ERROR : Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}