package areda.view.user;

import areda.model.DatabaseManager;
import areda.model.Lowongan;
import areda.model.Profil;
import areda.model.Lamaran;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;

public class JobListView extends StackPane {
    private VBox contentBox;
    private StackPane overlay;
    private String currentFilter = "Semua";
    private VBox jobListContainer;

    private static final String UPLOAD_FOLDER = "uploads";
    private static final String CV_FOLDER = UPLOAD_FOLDER + File.separator + "cv";
    private static final String ML_FOLDER = UPLOAD_FOLDER + File.separator + "motlet";

    public JobListView() {

        createUploadDirectories();

        this.setStyle("-fx-background-color: white;");
        contentBox = new VBox(25);
        contentBox.setPadding(new Insets(30));
        contentBox.setAlignment(Pos.TOP_LEFT);
        overlay = new StackPane();
        overlay.getStyleClass().add("modal-overlay");
        overlay.setVisible(false);
        this.getChildren().addAll(contentBox, overlay);
        showJobListPage();
    }

    private void createUploadDirectories() {
        try {
            Files.createDirectories(Paths.get(CV_FOLDER));
            Files.createDirectories(Paths.get(ML_FOLDER));
        } catch (IOException e) {
            System.err.println("Gagal membuat folder upload: " + e.getMessage());
            showAlert("Error", "Gagal menyiapkan folder upload dokumen.");
        }
    }

    private String saveUploadedFile(File sourceFile, String targetFolder) {
        if (sourceFile == null || !sourceFile.exists())
            return null;

        try {

            String originalName = sourceFile.getName();
            String extension = "";
            int lastDot = originalName.lastIndexOf(".");
            if (lastDot > 0) {
                extension = originalName.substring(lastDot);
            }
            String uniqueName = System.currentTimeMillis() + "_" +
                    originalName.replaceAll("[^a-zA-Z0-9\\.\\-]", "_") + extension;

            Path targetPath = Paths.get(targetFolder, uniqueName);
            Files.copy(sourceFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            return uniqueName;
        } catch (IOException e) {
            System.err.println("Gagal menyimpan file: " + e.getMessage());
            return null;
        }
    }

    private void showJobListPage() {
        contentBox.getChildren().clear();
        HBox header = createHeader("Daftar Lowongan");
        HBox controlBar = createControlBar();
        jobListContainer = new VBox(15);
        refreshJobList("", currentFilter);
        ScrollPane scroll = new ScrollPane(jobListContainer);
        scroll.setFitToWidth(true);
        scroll.setStyle(
                "-fx-background-color: transparent; -fx-background: transparent; -fx-border-color: transparent;");
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        VBox.setVgrow(scroll, Priority.ALWAYS);
        contentBox.getChildren().addAll(header, controlBar, scroll);
    }

    private HBox createControlBar() {
        HBox bar = new HBox(15);
        bar.setAlignment(Pos.CENTER_RIGHT);
        TextField search = new TextField();
        search.setPromptText("🔍 Cari lowongan...");
        search.getStyleClass().add("form-input");
        search.setPrefWidth(220);
        search.textProperty().addListener((obs, old, newVal) -> refreshJobList(newVal, currentFilter));

        ComboBox<String> filter = new ComboBox<>();
        filter.getItems().addAll(DatabaseManager.getAllDivisi());
        filter.setValue("Semua");
        filter.getStyleClass().add("form-input");
        filter.setPrefWidth(150);
        filter.setOnAction(e -> {
            currentFilter = filter.getValue();
            refreshJobList(search.getText(), currentFilter);
        });

        bar.getChildren().addAll(search, filter);
        return bar;
    }

    private void refreshJobList(String searchText, String selectedDivisi) {
        jobListContainer.getChildren().clear();
        List<Lowongan> filteredJobs = DatabaseManager.getAllLowongan().stream().filter(job -> {
            boolean matchText = searchText.isEmpty() || job.getJudul().toLowerCase().contains(searchText.toLowerCase())
                    || job.getDeskripsi().toLowerCase().contains(searchText.toLowerCase());
            boolean matchDivisi = selectedDivisi.equals("Semua") || job.getDivisi().equals(selectedDivisi);
            return matchText && matchDivisi;
        }).collect(Collectors.toList());

        if (filteredJobs.isEmpty()) {
            Label empty = new Label("Tidak ada lowongan ditemukan.");
            empty.setPadding(new Insets(20));
            empty.setTextFill(Color.web("#4A5568"));
            jobListContainer.getChildren().add(empty);
        } else {
            for (Lowongan job : filteredJobs)
                jobListContainer.getChildren().add(createJobCard(job));
        }
    }

    private void showJobDetailPage(Lowongan job) {
        contentBox.getChildren().clear();
        Button backBtn = new Button("←");
        backBtn.setStyle(
                "-fx-background-color: transparent; -fx-font-size: 18px; -fx-cursor: hand; -fx-text-fill: #1A202C;");
        backBtn.setOnAction(e -> showJobListPage());

        HBox header = createHeader(job.getJudul());
        header.getChildren().add(0, backBtn);

        ScrollPane detailScroll = new ScrollPane();
        detailScroll.setFitToWidth(true);
        detailScroll.setStyle(
                "-fx-background-color: transparent; -fx-background: transparent; -fx-border-color: transparent;");
        detailScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        VBox.setVgrow(detailScroll, Priority.ALWAYS);

        VBox detailCard = new VBox(20);
        detailCard.setPadding(new Insets(30));
        detailCard.setStyle(
                "-fx-background-color: white; -fx-background-radius: 20; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 10, 0, 0, 5); -fx-border-color: #E2E8F0; -fx-border-radius: 20;");

        Label catLabel = new Label(job.getDivisi());
        catLabel.setTextFill(Color.web("#718096"));

        VBox descBox = new VBox(10);
        Label descLbl = new Label("Deskripsi");
        descLbl.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        descLbl.setTextFill(Color.web("#1A202C"));
        descBox.getChildren().add(descLbl);

        Label descText = new Label(job.getDeskripsi());
        descText.setWrapText(true);
        descText.setMaxWidth(Double.MAX_VALUE);
        descText.setTextFill(Color.web("#2D3748"));
        descText.setFont(Font.font("Arial", 14));
        descBox.getChildren().add(descText);

        VBox skillBox = new VBox(10);
        Label skillLbl = new Label("Skill yang dibutuhkan");
        skillLbl.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        skillLbl.setTextFill(Color.web("#1A202C"));
        skillBox.getChildren().add(skillLbl);

        StringBuilder skillsText = new StringBuilder();
        for (String skill : job.getSkillDibutuhkan())
            skillsText.append("• ").append(skill).append("\n");
        Label skillsLabel = new Label(skillsText.toString());
        skillsLabel.setWrapText(true);
        skillsLabel.setMaxWidth(Double.MAX_VALUE);
        skillsLabel.setTextFill(Color.web("#2D3748"));
        skillsLabel.setFont(Font.font("Arial", 14));
        skillBox.getChildren().add(skillsLabel);

        Button applyBtn = new Button("Daftar Sekarang");
        applyBtn.setMaxWidth(Double.MAX_VALUE);
        boolean isAccepted = DatabaseManager.isCurrentUserAccepted();

        if (job.getSisaSlot() == 0 || isAccepted) {
            applyBtn.setText(isAccepted ? "Anda Sudah Diterima di Tempat Lain" : "Lowongan Penuh");
            applyBtn.setDisable(true);
            applyBtn.setStyle(
                    "-fx-background-color: #EDF2F7; -fx-text-fill: #A0AEC0; -fx-font-weight: bold; -fx-padding: 12; -fx-background-radius: 10;");
        } else {
            applyBtn.getStyleClass().add("btn-primary");
            applyBtn.setMaxWidth(Double.MAX_VALUE);
            applyBtn.setOnAction(e -> {
                Lowongan l = DatabaseManager.getLowonganById(job.getId());
                if (l == null || l.getSisaSlot() == 0) {
                    showAlert("Error", "Lowongan penuh atau tidak ditemukan!");
                    return;
                }
                Profil p = DatabaseManager.getCurrentUserProfil();
                if (DatabaseManager.hasUserApplied(job.getId())) {
                    showAlert("Peringatan", "Anda sudah melamar untuk posisi ini!");
                    return;
                }

                boolean namaLengkap = p.getNama() != null && !p.getNama().trim().isEmpty();
                boolean emailLengkap = p.getEmail() != null && !p.getEmail().trim().isEmpty();
                boolean teleponLengkap = p.getTelepon() != null && !p.getTelepon().trim().isEmpty();

                if (namaLengkap && emailLengkap && teleponLengkap)
                    showRegistrationModal(job);
                else
                    showIncompleteProfileModal();
            });
        }
        detailCard.getChildren().addAll(catLabel, descBox, skillBox, applyBtn);
        detailScroll.setContent(detailCard);
        contentBox.getChildren().addAll(header, detailScroll);
    }

    private void showModal(Node modal) {
        overlay.getChildren().clear();
        overlay.getChildren().add(modal);
        overlay.setVisible(true);
        contentBox.setDisable(true);
    }

    private void hideModal() {
        overlay.setVisible(false);
        contentBox.setDisable(false);
    }

    private void showRegistrationModal(Lowongan job) {

        createUploadDirectories();

        VBox modal = new VBox(20);
        modal.setMaxSize(500, 600);
        modal.setPadding(new Insets(30));
        modal.getStyleClass().add("modal-card");

        ScrollPane modalScroll = new ScrollPane(modal);
        modalScroll.setFitToWidth(true);
        modalScroll.setStyle(
                "-fx-background-color: transparent; -fx-background: transparent; -fx-border-color: transparent;");
        modalScroll.setMaxSize(520, 650);

        HBox top = new HBox();
        top.setAlignment(Pos.CENTER_RIGHT);
        Button close = new Button("X");
        close.setStyle("-fx-background-color: transparent; -fx-font-weight: bold; -fx-cursor: hand;");
        close.setTextFill(Color.web("#1A202C"));
        close.setOnAction(e -> hideModal());
        top.getChildren().add(close);

        Label title = new Label("Daftar Kerja: " + job.getJudul());
        title.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        title.setTextFill(Color.web("#1A202C"));

        Profil p = DatabaseManager.getCurrentUserProfil();

        TextField name = new TextField(p.getNama() == null ? "" : p.getNama());
        name.getStyleClass().add("form-input");
        name.setDisable(true);

        TextField email = new TextField(p.getEmail() == null ? "" : p.getEmail());
        email.getStyleClass().add("form-input");
        email.setDisable(true);

        TextField phone = new TextField(p.getTelepon() == null ? "" : p.getTelepon());
        phone.getStyleClass().add("form-input");
        phone.setDisable(true);

        TextArea about = new TextArea(p.getTentangSaya() == null ? "" : p.getTentangSaya());
        about.setPromptText("Ceritakan tentang diri Anda...");
        about.setPrefRowCount(4);
        about.getStyleClass().add("form-input");

        Label cvLabelText = new Label(
                (p.getCvPath() == null || p.getCvPath().isEmpty()) ? "📄 Unggah CV (PDF)" : p.getCvPath());
        cvLabelText.setTextFill(Color.web("#1A202C"));
        VBox uploadCV = createUploadBox("Curriculum Vitae (CV)*", cvLabelText, "CV");

        Label mlLabelText = new Label(
                (p.getMotletPath() == null || p.getMotletPath().isEmpty()) ? "📄 Unggah Motivation Letter (PDF)"
                        : p.getMotletPath());
        mlLabelText.setTextFill(Color.web("#1A202C"));
        VBox uploadLetter = createUploadBox("Motivation Letter*", mlLabelText, "ML");

        Button submit = new Button("KIRIM LAMARAN");
        submit.setMaxWidth(Double.MAX_VALUE);
        submit.getStyleClass().add("btn-primary");
        submit.setOnAction(e -> {

            String cvFileName = cvLabelText.getUserData() != null ? cvLabelText.getUserData().toString()
                    : (cvLabelText.getText().startsWith("📄") ? null : cvLabelText.getText().replace("📄 ", "").trim());
            String mlFileName = mlLabelText.getUserData() != null ? mlLabelText.getUserData().toString()
                    : (mlLabelText.getText().startsWith("📄") ? null : mlLabelText.getText().replace("📄 ", "").trim());

            if (cvFileName == null || mlFileName == null) {
                showAlert("Error", "CV dan Motivation Letter harus diunggah!");
                return;
            }

            String newId = "LAM-" + System.currentTimeMillis();
            Lamaran newLamaran = new Lamaran(newId, job.getId(),
                    p.getNama() == null ? "" : p.getNama(),
                    p.getEmail() == null ? "" : p.getEmail(),
                    p.getTelepon() == null ? "" : p.getTelepon(),
                    about.getText(),
                    java.time.LocalDate.now().toString(),
                    "Seleksi Berkas",
                    cvFileName,
                    mlFileName);

            DatabaseManager.addLamaran(newLamaran);
            showAlert("Sukses", "Berhasil mendaftar untuk posisi " + job.getJudul());
            hideModal();
            showJobListPage();
        });

        modal.getChildren().addAll(top, title,
                createLabeledNode("Nama Lengkap*", name),
                createLabeledNode("Email*", email),
                createLabeledNode("No. Handphone*", phone),
                createLabeledNode("Tentang Saya", about),
                uploadCV, uploadLetter, submit);
        showModal(modalScroll);
    }

    private void showIncompleteProfileModal() {
        VBox modal = new VBox(20);
        modal.setMaxSize(380, 260);
        modal.setAlignment(Pos.CENTER);
        modal.setPadding(new Insets(30));
        modal.getStyleClass().add("modal-card");

        Label title = new Label("Profil Belum Lengkap");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        title.setTextFill(Color.web("#1A202C"));
        Label sub = new Label("Lengkapi profil Anda sebelum melanjutkan.");
        sub.setTextFill(Color.web("#4A5568"));
        sub.setFont(Font.font("Arial", 13));

        HBox buttons = new HBox(15);
        buttons.setAlignment(Pos.CENTER);
        buttons.setPadding(new Insets(10, 0, 0, 0));
        Button nanti = new Button("Nanti");
        nanti.getStyleClass().add("btn-outline");
        nanti.setOnAction(e -> hideModal());
        Button isiProfil = new Button("Isi Profil");
        isiProfil.getStyleClass().add("btn-primary");
        isiProfil.setOnAction(e -> {
            hideModal();
            Node node = this;
            while (node.getParent() != null) {
                node = node.getParent();
                if (node instanceof UserDashboardLayout) {
                    ((UserDashboardLayout) node).navigateTo("Profil");
                    break;
                }
            }
        });

        buttons.getChildren().addAll(nanti, isiProfil);
        modal.getChildren().addAll(title, sub, buttons);
        showModal(modal);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(title.equals("Error") ? AlertType.ERROR : AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private HBox createHeader(String titleText) {
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label(titleText);
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        title.setTextFill(Color.web("#1A202C"));
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        header.getChildren().addAll(title, spacer);
        return header;
    }

    private HBox createJobCard(Lowongan job) {
        HBox card = new HBox(15);
        card.getStyleClass().add("job-card");
        card.setAlignment(Pos.CENTER_LEFT);
        card.setOnMouseClicked(e -> showJobDetailPage(job));

        VBox left = new VBox(5);
        Label title = new Label(job.getJudul());
        title.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        title.setTextFill(Color.web("#1A202C"));
        Label cat = new Label("💼 " + job.getDivisi());
        cat.setFont(Font.font("Arial", 12));
        cat.setTextFill(Color.web("#2D3748"));
        left.getChildren().addAll(title, cat);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Button bookmarkBtn = new Button("🔖");
        updateBookmarkStyle(bookmarkBtn, DatabaseManager.isBookmarked(job.getId()));
        bookmarkBtn.setOnAction(e -> {
            e.consume();
            DatabaseManager.toggleBookmark(job.getId());
            updateBookmarkStyle(bookmarkBtn, DatabaseManager.isBookmarked(job.getId()));
        });

        card.getChildren().addAll(left, spacer, bookmarkBtn);
        return card;
    }

    private VBox createLabeledNode(String label, Node node) {
        VBox box = new VBox(5);
        Label lbl = new Label(label);
        lbl.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        lbl.setTextFill(Color.web("#1A202C"));
        box.getChildren().addAll(lbl, node);
        return box;
    }

    private VBox createUploadBox(String title, Label fileLabel, String fileType) {
        VBox box = new VBox(5);
        Label lbl = new Label(title);
        lbl.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        lbl.setTextFill(Color.web("#1A202C"));

        StackPane dropArea = new StackPane();
        dropArea.setPadding(new Insets(10));
        dropArea.setStyle(
                "-fx-border-color: #CBD5E0; -fx-border-style: dashed; -fx-border-width: 1; -fx-border-radius: 8; -fx-background-color: #F7FAFC; -fx-cursor: hand;");
        fileLabel.setFont(Font.font(11));
        fileLabel.setTextFill(Color.web("#2D3748"));
        dropArea.getChildren().add(fileLabel);

        dropArea.setOnMouseClicked(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Pilih File PDF");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
            File selectedFile = fileChooser.showOpenDialog(this.getScene().getWindow());

            if (selectedFile != null) {

                String targetFolder = fileType.equals("CV") ? CV_FOLDER : ML_FOLDER;
                String savedFileName = saveUploadedFile(selectedFile, targetFolder);

                if (savedFileName != null) {
                    fileLabel.setText("📄 " + savedFileName);
                    fileLabel.setTextFill(Color.web("#2B6CB0"));

                    fileLabel.setUserData(savedFileName);
                } else {
                    showAlert("Error", "Gagal menyimpan file. Silakan coba lagi.");
                }
            }
        });
        box.getChildren().addAll(lbl, dropArea);
        return box;
    }

    private void updateBookmarkStyle(Button btn, boolean isBookmarked) {
        btn.getStyleClass().clear();
        btn.getStyleClass().add(isBookmarked ? "bookmark-active" : "bookmark-inactive");
    }
}