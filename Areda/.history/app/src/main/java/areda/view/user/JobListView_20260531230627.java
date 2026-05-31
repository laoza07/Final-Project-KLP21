package areda.view.user;

import areda.model.DatabaseManager;
import areda.model.DatabaseManager.Lowongan;
import areda.model.DatabaseManager.Profil;
import areda.model.DatabaseManager.Lamaran;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class JobListView extends StackPane {
    private VBox contentBox;
    private StackPane overlay;
    private String currentFilter = "Semua";
    private VBox jobListContainer;

    public JobListView() {
        this.setStyle("-fx-background-color: white;");
        contentBox = new VBox(25);
        contentBox.setPadding(new Insets(30));
        contentBox.setAlignment(Pos.TOP_LEFT);
        overlay = new StackPane();
        overlay.setStyle("-fx-background-color: rgba(0,0,0,0.4);");
        overlay.setVisible(false);
        this.getChildren().addAll(contentBox, overlay);
        showJobListPage();
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
        contentBox.getChildren().addAll(header, controlBar, scroll);
    }

    private HBox createControlBar() {
        HBox bar = new HBox(15);
        bar.setAlignment(Pos.CENTER_RIGHT);
        TextField search = new TextField();
        search.setPromptText(" Cari lowongan...");
        search.setStyle("-fx-background-color: #F7FAFC; -fx-background-radius: 10; -fx-padding: 8;");
        search.setPrefWidth(220);
        search.textProperty().addListener((obs, old, newVal) -> refreshJobList(newVal, currentFilter));
        ComboBox<String> filter = new ComboBox<>();
        filter.getItems().addAll(DatabaseManager.getAllDivisi());
        filter.setValue("Semua");
        filter.setStyle("-fx-background-color: #F7FAFC; -fx-background-radius: 10; -fx-padding: 5;");
        filter.setOnAction(e -> {
            currentFilter = filter.getValue();
            refreshJobList(search.getText(), currentFilter);
        });
        bar.getChildren().addAll(search, filter);
        return bar;
    }

    private void refreshJobList(String searchText, String selectedDivisi) {
        jobListContainer.getChildren().clear();
        List<Lowongan> filteredJobs = DatabaseManager.getAllLowongan().stream()
                .filter(job -> {
                    boolean matchText = searchText.isEmpty()
                            || job.judul.toLowerCase().contains(searchText.toLowerCase())
                            || job.deskripsi.toLowerCase().contains(searchText.toLowerCase());
                    boolean matchDivisi = selectedDivisi.equals("Semua") || job.divisi.equals(selectedDivisi);
                    return matchText && matchDivisi;
                }).collect(Collectors.toList());

        if (filteredJobs.isEmpty()) {
            Label empty = new Label("Tidak ada lowongan ditemukan.");
            empty.setPadding(new Insets(20));
            jobListContainer.getChildren().add(empty);
        } else {
            for (Lowongan job : filteredJobs)
                jobListContainer.getChildren().add(createJobCard(job));
        }
    }

    private void showJobDetailPage(Lowongan job) {
        contentBox.getChildren().clear();
        Button backBtn = new Button("←");
        backBtn.setStyle("-fx-background-color: transparent; -fx-font-size: 18px; -fx-cursor: hand;");
        backBtn.setOnAction(e -> showJobListPage());
        HBox header = createHeader(job.judul);
        header.getChildren().add(0, backBtn);
        VBox detailCard = new VBox(20);
        detailCard.setPadding(new Insets(30));
        detailCard.setStyle(
                "-fx-background-color: white; -fx-background-radius: 20; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 10, 0, 0, 5); -fx-border-color: #E2E8F0; -fx-border-radius: 20;");
        Label catLabel = new Label(job.divisi);
        catLabel.setTextFill(Color.web("#718096"));
        VBox descBox = new VBox(10);
        descBox.getChildren().add(new Label("Deskripsi") {
            {
                setFont(Font.font("Arial", FontWeight.BOLD, 16));
            }
        });
        Text descText = new Text(job.deskripsi);
        descText.setWrappingWidth(600);
        descBox.getChildren().add(descText);
        VBox skillBox = new VBox(10);
        skillBox.getChildren().add(new Label("Skill yang dibutuhkan") {
            {
                setFont(Font.font("Arial", FontWeight.BOLD, 16));
            }
        });
        StringBuilder skillsText = new StringBuilder();
        for (String skill : job.skillDibutuhkan)
            skillsText.append("•  ").append(skill).append("\n");
        skillBox.getChildren().add(new Label(skillsText.toString()));

        Button applyBtn = new Button("Daftar Sekarang");
        applyBtn.setMaxWidth(Double.MAX_VALUE);

        // FITUR BARU: CEK JIKA USER SUDAH DITERIMA
        boolean isAccepted = DatabaseManager.isCurrentUserAccepted();

        if (job.sisaSlot == 0 || isAccepted) {
            if (isAccepted) {
                applyBtn.setText("Anda Sudah Diterima di Tempat Lain");
            } else {
                applyBtn.setText("Lowongan Penuh");
            }
            applyBtn.setDisable(true);
            applyBtn.setStyle(
                    "-fx-background-color: #EDF2F7; -fx-text-fill: #A0AEC0; -fx-font-weight: bold; -fx-padding: 12; -fx-background-radius: 10;");
        } else {
            applyBtn.setStyle(
                    "-fx-background-color: rgba(190, 227, 248, 0.5); -fx-text-fill: #2B6CB0; -fx-font-weight: bold; -fx-padding: 12; -fx-background-radius: 10; -fx-cursor: hand;");
            applyBtn.setOnAction(e -> {
                Lowongan l = DatabaseManager.getLowonganById(job.id);
                if (l == null || l.sisaSlot == 0) {
                    showAlert("Error", "Lowongan penuh atau tidak ditemukan!");
                    return;
                }
                Profil p = DatabaseManager.getProfil();
                if (DatabaseManager.hasUserApplied(job.id)) {
                    showAlert("Peringatan", "Anda sudah melamar untuk posisi ini!");
                    return;
                }
                boolean isProfileComplete = !p.nama.isEmpty() && !p.email.isEmpty() && !p.telepon.isEmpty();
                if (isProfileComplete)
                    showRegistrationModal(job);
                else
                    showIncompleteProfileModal();
            });
        }
        detailCard.getChildren().addAll(catLabel, descBox, skillBox, applyBtn);
        contentBox.getChildren().addAll(header, detailCard);
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
        VBox modal = new VBox(20);
        modal.setMaxSize(500, 600);
        modal.setPadding(new Insets(30));
        modal.setStyle("-fx-background-color: white; -fx-background-radius: 20;");
        HBox top = new HBox();
        top.setAlignment(Pos.CENTER_RIGHT);
        Button close = new Button("X");
        close.setStyle("-fx-background-color: transparent; -fx-font-weight: bold; -fx-cursor: hand;");
        close.setOnAction(e -> hideModal());
        top.getChildren().add(close);
        Label title = new Label("Daftar Kerja: " + job.judul);
        title.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        Profil p = DatabaseManager.getProfil();
        TextField name = createInput("Nama Lengkap*", p.nama);
        name.setDisable(true);
        TextField email = createInput("Email*", p.email);
        email.setDisable(true);
        TextField phone = createInput("No. Handphone*", p.telepon);
        phone.setDisable(true);
        TextArea about = new TextArea(p.tentangSaya);
        about.setPromptText("Ceritakan tentang diri Anda...");
        about.setPrefRowCount(4);
        about.setStyle("-fx-background-color: #F7FAFC; -fx-background-radius: 8; -fx-padding: 10;");
        Label cvLabelText = new Label(p.cvPath.isEmpty() ? "📄 Unggah CV (PDF)" : p.cvPath);
        VBox uploadCV = createUploadBox("Curriculum Vitae (CV)*", cvLabelText);
        Label mlLabelText = new Label(p.motletPath.isEmpty() ? " Unggah Motivation Letter (PDF)" : p.motletPath);
        VBox uploadLetter = createUploadBox("Motivation Letter*", mlLabelText);
        Button submit = new Button("KIRIM LAMARAN");
        submit.setMaxWidth(Double.MAX_VALUE);
        submit.setStyle(
                "-fx-background-color: #0066CC; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 15; -fx-background-radius: 10; -fx-cursor: hand;");
        submit.setOnAction(e -> {
            if (cvLabelText.getText().equals("📄 Unggah CV (PDF)")
                    || mlLabelText.getText().equals("📄 Unggah Motivation Letter (PDF)")) {
                showAlert("Error", "CV dan Motivation Letter harus diunggah!");
                return;
            }
            String newId = "LAM-" + System.currentTimeMillis();
            Lamaran newLamaran = new Lamaran(newId, job.id, p.nama, p.email, p.telepon, about.getText(),
                    java.time.LocalDate.now().toString(), "Seleksi Berkas", cvLabelText.getText(),
                    mlLabelText.getText());
            DatabaseManager.addLamaran(newLamaran);
            showAlert("Sukses", "Berhasil mendaftar untuk posisi " + job.judul);
            hideModal();
            showJobListPage();
        });
        modal.getChildren().addAll(top, title, name, email, phone, createLabeledNode("Tentang Saya", about), uploadCV,
                uploadLetter, submit);
        showModal(modal);
    }

    private void showIncompleteProfileModal() {
        VBox modal = new VBox(20);
        modal.setMaxSize(380, 260);
        modal.setAlignment(Pos.CENTER);
        modal.setPadding(new Insets(30));
        modal.setStyle(
                "-fx-background-color: white; -fx-background-radius: 20; -fx-border-color: #E2E8F0; -fx-border-width: 1; -fx-border-radius: 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 15, 0.3, 0, 5);");
        StackPane iconCircle = new StackPane();
        iconCircle.setPrefSize(50, 50);
        Circle bgCircle = new Circle(25);
        bgCircle.setFill(Color.web("#FFFFF0"));
        bgCircle.setStroke(Color.web("#ECC94B"));
        bgCircle.setStrokeWidth(2);
        Label exclamation = new Label("!");
        exclamation.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        exclamation.setTextFill(Color.web("#ECC94B"));
        iconCircle.getChildren().addAll(bgCircle, exclamation);
        Label title = new Label("Profil Belum Lengkap");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        title.setTextFill(Color.web("#1A202C"));
        Label sub = new Label("Lengkapi profil Anda sebelum melanjutkan.");
        sub.setTextFill(Color.web("#718096"));
        sub.setFont(Font.font("Arial", 13));
        HBox buttons = new HBox(15);
        buttons.setAlignment(Pos.CENTER);
        buttons.setPadding(new Insets(10, 0, 0, 0));
        Button nanti = new Button("Nanti");
        nanti.setStyle(
                "-fx-background-color: #EDF2F7; -fx-text-fill: #4A5568; -fx-font-weight: bold; -fx-padding: 10 25; -fx-background-radius: 10; -fx-cursor: hand;");
        nanti.setOnAction(e -> hideModal());
        Button isiProfil = new Button("Isi Profil");
        isiProfil.setStyle(
                "-fx-background-color: #BEE3F8; -fx-text-fill: #2B6CB0; -fx-font-weight: bold; -fx-padding: 10 25; -fx-background-radius: 10; -fx-cursor: hand;");
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
        modal.getChildren().addAll(iconCircle, title, sub, buttons);
        showModal(modal);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
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
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox icons = new HBox(15, new Label("🔔"), new Circle(15, Color.LIGHTGRAY));
        icons.setAlignment(Pos.CENTER);
        header.getChildren().addAll(title, spacer, icons);
        return header;
    }

    private HBox createJobCard(Lowongan job) {
        HBox card = new HBox(15);
        card.setPadding(new Insets(20));
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle("-fx-background-color: #BEE3F8; -fx-background-radius: 15; -fx-cursor: hand;");
        card.setOnMouseClicked(e -> showJobDetailPage(job));
        VBox left = new VBox(5);
        Label title = new Label(job.judul);
        title.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        title.setTextFill(Color.web("#1A202C"));
        Label cat = new Label("💼 " + job.divisi);
        cat.setFont(Font.font("Arial", 12));
        cat.setTextFill(Color.web("#2D3748"));
        left.getChildren().addAll(title, cat);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Button bookmarkBtn = new Button("🔖");
        updateBookmarkStyle(bookmarkBtn, DatabaseManager.isBookmarked(job.id));
        bookmarkBtn.setOnAction(e -> {
            e.consume();
            DatabaseManager.toggleBookmark(job.id);
            updateBookmarkStyle(bookmarkBtn, DatabaseManager.isBookmarked(job.id));
        });
        card.getChildren().addAll(left, spacer, bookmarkBtn);
        return card;
    }

    private TextField createInput(String label, String value) {
        VBox vbox = new VBox(5);
        Label lbl = new Label(label);
        lbl.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        TextField tf = new TextField(value);
        tf.setStyle("-fx-background-color: #F7FAFC; -fx-background-radius: 8; -fx-padding: 10;");
        vbox.getChildren().addAll(lbl, tf);
        return tf;
    }

    private VBox createLabeledNode(String label, Node node) {
        VBox box = new VBox(5);
        Label lbl = new Label(label);
        lbl.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        box.getChildren().addAll(lbl, node);
        return box;
    }

    private VBox createUploadBox(String title, Label fileLabel) {
        VBox box = new VBox(5);
        Label lbl = new Label(title);
        lbl.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        StackPane dropArea = new StackPane();
        dropArea.setPadding(new Insets(10));
        dropArea.setStyle(
                "-fx-border-color: #CBD5E0; -fx-border-style: dashed; -fx-border-width: 1; -fx-border-radius: 8; -fx-background-color: #F7FAFC; -fx-cursor: hand;");
        fileLabel.setFont(Font.font(11));
        fileLabel.setTextFill(Color.web("#A0AEC0"));
        dropArea.getChildren().add(fileLabel);
        dropArea.setOnMouseClicked(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Pilih File PDF");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
            File selectedFile = fileChooser.showOpenDialog(this.getScene().getWindow());
            if (selectedFile != null) {
                fileLabel.setText(selectedFile.getName());
                fileLabel.setTextFill(Color.web("#2B6CB0"));
            }
        });
        box.getChildren().addAll(lbl, dropArea);
        return box;
    }

    private void updateBookmarkStyle(Button btn, boolean isBookmarked) {
        if (isBookmarked)
            btn.setStyle(
                    "-fx-background-color: transparent; -fx-text-fill: #2B6CB0; -fx-font-size: 18px; -fx-cursor: hand;");
        else
            btn.setStyle(
                    "-fx-background-color: transparent; -fx-text-fill: #A0AEC0; -fx-font-size: 18px; -fx-cursor: hand;");
    }
}