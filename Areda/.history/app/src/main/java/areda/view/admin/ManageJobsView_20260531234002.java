package areda.view.admin;

import areda.model.DatabaseManager;
import areda.model.DatabaseManager.Lowongan;
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

import java.util.ArrayList;
import java.util.List;

public class ManageJobsView extends StackPane {

    private VBox mainContent;
    private StackPane modalOverlay;
    private VBox jobListContainer;
    private String currentFilter = "Semua";

    public ManageJobsView() {
        this.setStyle("-fx-background-color: white;");
        mainContent = new VBox(25);
        mainContent.setPadding(new Insets(30));
        mainContent.setAlignment(Pos.TOP_LEFT);
        HBox header = createHeader();
        HBox controlBar = createControlBar();
        jobListContainer = new VBox(15);
        refreshJobList();
        ScrollPane scroll = new ScrollPane(jobListContainer);
        scroll.setFitToWidth(true);
        scroll.setStyle(
                "-fx-background-color: transparent; -fx-background: transparent; -fx-border-color: transparent;");
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        mainContent.getChildren().addAll(header, controlBar, scroll);
        modalOverlay = new StackPane();
        modalOverlay.setStyle("-fx-background-color: rgba(0,0,0,0.4);");
        modalOverlay.setVisible(false);
        this.getChildren().addAll(mainContent, modalOverlay);
    }

    private void refreshJobList() {
        jobListContainer.getChildren().clear();
        List<Lowongan> jobs = DatabaseManager.getLowonganByDivisi(currentFilter);
        if (jobs.isEmpty()) {
            Label emptyLabel = new Label("Tidak ada lowongan ditemukan.");
            emptyLabel.setTextFill(Color.web("#1A202C"));
            emptyLabel.setPadding(new Insets(20));
            jobListContainer.getChildren().add(emptyLabel);
        } else {
            for (Lowongan job : jobs) {
                jobListContainer.getChildren().add(createJobCard(job));
            }
        }
    }

    private HBox createHeader() {
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("Kelola Lowongan");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        title.setTextFill(Color.web("#1A202C"));
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox iconBox = new HBox(15);
        iconBox.setAlignment(Pos.CENTER);
        iconBox.getChildren().addAll(new Label("🔔"), new Circle(15, Color.LIGHTGRAY));
        header.getChildren().addAll(title, spacer, iconBox);
        return header;
    }

    private HBox createControlBar() {
        HBox bar = new HBox(15);
        bar.setAlignment(Pos.CENTER_LEFT);
        Button addBtn = new Button("+ Tambah Lowongan");
        addBtn.setStyle(
                "-fx-background-color: #0066CC; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 10 20; -fx-cursor: hand;");
        addBtn.setOnAction(e -> showJobFormModal(null));
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox searchBox = new HBox(10);
        searchBox.setAlignment(Pos.CENTER_RIGHT);
        ComboBox<String> filter = new ComboBox<>();
        filter.getItems().addAll(DatabaseManager.getAllDivisi());
        filter.setValue("Semua");
        filter.setStyle("-fx-background-color: #F7FAFC; -fx-background-radius: 8;");
        filter.setOnAction(e -> {
            currentFilter = filter.getValue();
            refreshJobList();
        });
        searchBox.getChildren().add(filter);
        bar.getChildren().addAll(addBtn, spacer, searchBox);
        return bar;
    }

    private HBox createJobCard(Lowongan job) {
        HBox card = new HBox(15);
        card.setPadding(new Insets(20));
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle("-fx-background-color: #BEE3F8; -fx-background-radius: 15;");

        VBox left = new VBox(5);

        Label title = new Label(job.getJudul());
        title.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        title.setTextFill(Color.web("#1A202C"));

        Label cat = new Label("💼 " + job.getDivisi());
        cat.setFont(Font.font(12));
        cat.setTextFill(Color.web("#4A5568"));

        Label slotLabel = new Label();
        if (job.getSisaSlot() == -1) {
            slotLabel.setText("Full");
            slotLabel.setTextFill(Color.web("#2B6CB0")); // Biru Jelas
            slotLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        } else if (job.getSisaSlot() == 0) {
            slotLabel.setText("Penuh");
            slotLabel.setTextFill(Color.web("#E53E3E")); // Merah
            slotLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        } else {
            slotLabel.setText("Tersisa " + job.getSisaSlot() + " Slot");
            slotLabel.setTextFill(Color.web("#2B6CB0")); // Biru Jelas
            slotLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        }

        left.getChildren().addAll(title, cat, slotLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox actions = new HBox(10);
        actions.setAlignment(Pos.CENTER);

        Button editBtn = new Button("✏ Edit");
        editBtn.setStyle(
                "-fx-background-color: white; -fx-text-fill: #2B6CB0; -fx-background-radius: 8; -fx-cursor: hand; -fx-font-size: 12px; -fx-font-weight: bold;");
        editBtn.setOnAction(e -> showJobFormModal(job));

        Button delBtn = new Button("🗑 Hapus");
        delBtn.setStyle(
                "-fx-background-color: white; -fx-text-fill: #E53E3E; -fx-background-radius: 8; -fx-cursor: hand; -fx-font-size: 12px; -fx-font-weight: bold;");
        delBtn.setOnAction(e -> showDeleteConfirmModal(job));

        actions.getChildren().addAll(editBtn, delBtn);

        card.getChildren().addAll(left, spacer, actions);
        return card;
    }

    private void showModal(Node content) {
        modalOverlay.getChildren().clear();
        modalOverlay.getChildren().add(content);
        modalOverlay.setVisible(true);
        mainContent.setDisable(true);
    }

    private void hideModal() {
        modalOverlay.setVisible(false);
        mainContent.setDisable(false);
    }

    private void showJobFormModal(Lowongan jobToEdit) {
        boolean isEdit = jobToEdit != null;
        VBox modal = new VBox(20);
        modal.setMaxSize(500, 650);
        modal.setPadding(new Insets(30));
        modal.setStyle("-fx-background-color: white; -fx-background-radius: 20;");

        HBox top = new HBox();
        top.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label(isEdit ? "Edit Lowongan" : "Tambah Lowongan Baru");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        title.setTextFill(Color.web("#1A202C"));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button close = new Button("X");
        close.setStyle("-fx-background-color: transparent; -fx-font-weight: bold; -fx-cursor: hand;");
        close.setOnAction(e -> hideModal());

        top.getChildren().addAll(title, spacer, close);

        VBox form = new VBox(15);
        TextField titleField = createInput("Masukkan nama lowongan");

        ComboBox<String> divField = new ComboBox<>();
        divField.getItems().addAll("Marketing", "Teknologi Informasi", "Human Resource", "Keuangan", "Operasional");
        divField.setStyle("-fx-background-color: #F7FAFC; -fx-background-radius: 8; -fx-padding: 5;");
        divField.setMaxWidth(Double.MAX_VALUE);

        TextField slotField = createInput("Contoh: 2 (-1 untuk Full)");

        slotField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("-?\\d*")) {
                slotField.setText(newVal.replaceAll("[^-\\d]", ""));
            }
        });

        VBox descBox = new VBox(5);
        Label descLbl = new Label("Deskripsi Pekerjaan");
        descLbl.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        descLbl.setTextFill(Color.web("#1A202C")); // Label Hitam
        TextArea descArea = new TextArea();
        descArea.setPromptText("Tuliskan deskripsi pekerjaan...");
        descArea.setWrapText(true);
        descArea.setPrefRowCount(3);
        descArea.setStyle("-fx-background-color: #F7FAFC; -fx-background-radius: 8; -fx-padding: 5;");
        descBox.getChildren().addAll(descLbl, descArea);

        VBox skillBox = new VBox(5);
        Label skillLbl = new Label("Keahlian yang Dibutuhkan");
        skillLbl.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        skillLbl.setTextFill(Color.web("#1A202C")); // Label Hitam
        TextArea skillArea = new TextArea();
        skillArea.setPromptText("Pisahkan dengan koma...");
        skillArea.setWrapText(true);
        skillArea.setPrefRowCount(3);
        skillArea.setStyle("-fx-background-color: #F7FAFC; -fx-background-radius: 8; -fx-padding: 5;");
        skillBox.getChildren().addAll(skillLbl, skillArea);

        if (isEdit) {
            titleField.setText(jobToEdit.getJudul());
            divField.setValue(jobToEdit.getDivisi());
            slotField.setText(String.valueOf(jobToEdit.getSisaSlot()));
            descArea.setText(jobToEdit.getDeskripsi());
            skillArea.setText(String.join(", ", jobToEdit.getSkillDibutuhkan()));
        }

        form.getChildren().addAll(
                createLabeledNode("Nama Lowongan", titleField),
                createLabeledNode("Divisi / Departemen", divField),
                createLabeledNode("Kuota / Jumlah Slot", slotField),
                descBox,
                skillBox);

        HBox btnBox = new HBox(15);
        btnBox.setAlignment(Pos.CENTER_RIGHT);

        Button cancelBtn = new Button("Batal");
        cancelBtn.setStyle(
                "-fx-background-color: white; -fx-border-color: #CBD5E0; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 10 20; -fx-cursor: hand;");
        cancelBtn.setOnAction(e -> hideModal());

        Button saveBtn = new Button("Simpan");
        saveBtn.setStyle(
                "-fx-background-color: #0066CC; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 10 20; -fx-cursor: hand;");
        saveBtn.setOnAction(e -> {

            if (titleField.getText().trim().isEmpty()) {
                showAlert("Error", "Nama lowongan tidak boleh kosong!");
                return;
            }

            if (divField.getValue() == null) {
                showAlert("Error", "Silakan pilih Divisi / Departemen!");
                return;
            }

            String slotText = slotField.getText().trim();
            if (slotText.isEmpty()) {
                showAlert("Error", "Kuota tidak boleh kosong!");
                return;
            }
            int slots;
            try {
                slots = Integer.parseInt(slotText);
                if (slots == 0) {
                    showAlert("Error", "Kuota tidak boleh 0!\nGunakan -1 untuk Full.");
                    return;
                }
                if (slots < -1) {
                    showAlert("Error", "Kuota tidak valid!\nHanya boleh -1 (Full) atau angka positif.");
                    return;
                }
            } catch (NumberFormatException ex) {
                showAlert("Error", "Kuota harus berupa angka!");
                return;
            }

            if (descArea.getText().trim().isEmpty()) {
                showAlert("Error", "Deskripsi pekerjaan tidak boleh kosong!");
                return;
            }

            if (skillArea.getText().trim().isEmpty()) {
                showAlert("Error", "Keahlian yang dibutuhkan tidak boleh kosong!");
                return;
            }

            try {
                List<String> skills = new ArrayList<>();
                String[] rawSkills = skillArea.getText().split(",");
                for (String s : rawSkills) {
                    if (!s.trim().isEmpty())
                        skills.add(s.trim());
                }

                if (isEdit) {
                    jobToEdit.setJudul(titleField.getText().trim());
                    jobToEdit.setDivisi(divField.getValue());
                    jobToEdit.setSisaSlot(slots);
                    jobToEdit.setDeskripsi(descArea.getText().trim());
                    jobToEdit.setSkillDibutuhkan(skills);
                    DatabaseManager.updateLowongan(jobToEdit);
                } else {
                    String newId = "NEW-" + System.currentTimeMillis();
                    DatabaseManager.tambahLowongan(new Lowongan(
                            newId, divField.getValue(), titleField.getText().trim(),
                            descArea.getText().trim(), skills, slots,
                            java.time.LocalDate.now().toString()));
                }
                refreshJobList();
                hideModal();
                showAlert("Sukses", "Data berhasil disimpan!");
            } catch (Exception ex) {
                showAlert("Error", "Terjadi kesalahan: " + ex.getMessage());
            }
        });

        btnBox.getChildren().addAll(cancelBtn, saveBtn);
        modal.getChildren().addAll(top, form, btnBox);
        showModal(modal);
    }

    private void showDeleteConfirmModal(Lowongan job) {
        VBox modal = new VBox(20);
        modal.setMaxSize(350, 250);
        modal.setAlignment(Pos.CENTER);
        modal.setPadding(new Insets(30));
        modal.setStyle(
                "-fx-background-color: white; -fx-background-radius: 20; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.15), 15, 0, 0, 5);");
        StackPane icon = new StackPane();
        Circle circle = new Circle(25, Color.web("#FFF5F5"));
        circle.setStroke(Color.web("#FC8181"));
        Label exclamation = new Label("!");
        exclamation.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        exclamation.setTextFill(Color.web("#FC8181"));
        icon.getChildren().addAll(circle, exclamation);
        Label title = new Label("Hapus Lowongan?");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        title.setTextFill(Color.web("#1A202C"));
        Text sub = new Text("Apakah Anda yakin ingin menghapus lowongan '" + job.getJudul()
                + "'? Tindakan ini tidak dapat dibatalkan.");
        sub.setWrappingWidth(280);
        sub.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        sub.setFill(Color.web("#718096"));
        HBox buttons = new HBox(15);
        buttons.setAlignment(Pos.CENTER);
        Button cancel = new Button("Batal");
        cancel.setStyle(
                "-fx-background-color: #EDF2F7; -fx-padding: 10 25; -fx-background-radius: 8; -fx-cursor: hand;");
        cancel.setOnAction(e -> hideModal());
        Button delete = new Button("Hapus");
        delete.setStyle(
                "-fx-background-color: #E53E3E; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 25; -fx-background-radius: 8; -fx-cursor: hand;");
        delete.setOnAction(e -> {
            DatabaseManager.hapusLowongan(job.getId());
            refreshJobList();
            hideModal();
        });
        buttons.getChildren().addAll(cancel, delete);
        modal.getChildren().addAll(icon, title, sub, buttons);
        showModal(modal);
    }

    private VBox createLabeledNode(String labelText, Node inputNode) {
        VBox box = new VBox(5);
        Label lbl = new Label(labelText);
        lbl.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        lbl.setTextFill(Color.web("#1A202C"));
        box.getChildren().addAll(lbl, inputNode);
        return box;
    }

    private TextField createInput(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setStyle("-fx-background-color: #F7FAFC; -fx-background-radius: 8; -fx-padding: 10;");
        return tf;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}