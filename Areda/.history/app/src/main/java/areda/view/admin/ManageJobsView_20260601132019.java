package areda.view.admin;

import areda.model.DatabaseManager;
import areda.model.Lowongan;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
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
        VBox.setVgrow(scroll, Priority.ALWAYS);
        mainContent.getChildren().addAll(header, controlBar, scroll);

        modalOverlay = new StackPane();
        modalOverlay.getStyleClass().add("modal-overlay");
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
        header.getChildren().addAll(title, spacer);
        return header;
    }

    private HBox createControlBar() {
        HBox bar = new HBox(15);
        bar.setAlignment(Pos.CENTER_LEFT);

        Button addBtn = new Button("+ Tambah Lowongan");
        addBtn.getStyleClass().add("btn-primary");
        addBtn.setOnAction(e -> showJobFormModal(null));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox searchBox = new HBox(10);
        searchBox.setAlignment(Pos.CENTER_RIGHT);
        ComboBox<String> filter = new ComboBox<>();
        filter.getItems().addAll(DatabaseManager.getAllDivisi());
        filter.setValue("Semua");
        filter.getStyleClass().add("form-input");
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
        card.getStyleClass().add("job-card");
        card.setAlignment(Pos.CENTER_LEFT);

        VBox left = new VBox(5);
        Label title = new Label(job.getJudul());
        title.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        title.setTextFill(Color.web("#1A202C"));

        Label cat = new Label("💼  " + job.getDivisi());
        cat.setFont(Font.font(12));
        cat.setTextFill(Color.web("#1A202C"));

        Label slotLabel = new Label();
        if (job.getSisaSlot() == -1) {
            slotLabel.setText("Unlimited");
            slotLabel.setTextFill(Color.web("#2B6CB0"));
        } else if (job.getSisaSlot() == 0) {
            slotLabel.setText("Penuh");
            slotLabel.setTextFill(Color.web("#E53E3E"));
        } else {
            slotLabel.setText("Tersisa " + job.getSisaSlot() + " Slot");
            slotLabel.setTextFill(Color.web("#2B6CB0"));
        }
        slotLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        left.getChildren().addAll(title, cat, slotLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox actions = new HBox(10);
        actions.setAlignment(Pos.CENTER);

        Button editBtn = new Button("✏ Edit");
        editBtn.getStyleClass().add("btn-edit");
        editBtn.setOnAction(e -> showJobFormModal(job));

        Button delBtn = new Button("🗑 Hapus");
        delBtn.getStyleClass().add("btn-delete");
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
        modal.getStyleClass().add("modal-card");

        ScrollPane modalScroll = new ScrollPane(modal);
        modalScroll.setFitToWidth(true);
        modalScroll.setStyle(
                "-fx-background-color: transparent; -fx-background: transparent; -fx-border-color: transparent;");
        modalScroll.setMaxSize(520, 700);

        HBox top = new HBox();
        top.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label(isEdit ? "Edit Lowongan" : "Tambah Lowongan Baru");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        title.setTextFill(Color.web("#1A202C"));
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Button close = new Button("X");
        close.setStyle("-fx-background-color: transparent; -fx-font-weight: bold; -fx-cursor: hand;");
        close.setTextFill(Color.web("#1A202C"));
        close.setOnAction(e -> hideModal());
        top.getChildren().addAll(title, spacer, close);

        VBox form = new VBox(15);
        TextField titleField = createInput("Masukkan nama lowongan");

        ComboBox<String> divField = new ComboBox<>();
        divField.getItems().addAll("Marketing", "Teknologi Informasi", "Human Resource", "Keuangan", "Operasional");
        divField.getStyleClass().add("form-input");
        divField.setMaxWidth(Double.MAX_VALUE);

        TextField slotField = createInput("Contoh: 1)");
        slotField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("-?\\d*"))
                slotField.setText(newVal.replaceAll("[^-\\d]", ""));
        });

        VBox descBox = new VBox(5);
        Label descLbl = new Label("Deskripsi Pekerjaan");
        descLbl.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        descLbl.setTextFill(Color.web("#1A202C"));
        TextArea descArea = new TextArea();
        descArea.setPromptText("Tuliskan deskripsi pekerjaan...");
        descArea.setWrapText(true);
        descArea.setPrefRowCount(3);
        descArea.getStyleClass().add("form-input");
        descBox.getChildren().addAll(descLbl, descArea);

        VBox skillBox = new VBox(5);
        Label skillLbl = new Label("Keahlian yang Dibutuhkan");
        skillLbl.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        skillLbl.setTextFill(Color.web("#1A202C"));
        TextArea skillArea = new TextArea();
        skillArea.setPromptText("Pisahkan dengan koma...");
        skillArea.setWrapText(true);
        skillArea.setPrefRowCount(3);
        skillArea.getStyleClass().add("form-input");
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
                descBox, skillBox);

        HBox btnBox = new HBox(15);
        btnBox.setAlignment(Pos.CENTER_RIGHT);

        Button cancelBtn = new Button("Batal");
        cancelBtn.getStyleClass().add("btn-outline");
        cancelBtn.setOnAction(e -> hideModal());

        Button saveBtn = new Button("Simpan");
        saveBtn.getStyleClass().add("btn-primary");
        saveBtn.setOnAction(e -> {
            if (titleField.getText().trim().isEmpty()) {
                showAlert("Error", "Nama lowongan tidak boleh kosong!");
                return;
            }
            if (divField.getValue() == null) {
                showAlert("Error", "Silakan pilih Divisi!");
                return;
            }

            int slots;
            try {
                slots = Integer.parseInt(slotField.getText().trim());
                if (slots < 0) {
                    showAlert("Error",
                            "Kuota tidak valid! Minimal angka 1.");
                    return;
                }
            } catch (NumberFormatException ex) {
                showAlert("Error", "Kuota harus berupa angka!");
                return;
            }

            if (descArea.getText().trim().isEmpty()) {
                showAlert("Error", "Deskripsi tidak boleh kosong!");
                return;
            }
            if (skillArea.getText().trim().isEmpty()) {
                showAlert("Error", "Keahlian tidak boleh kosong!");
                return;
            }

            try {
                List<String> skills = new ArrayList<>();
                for (String s : skillArea.getText().split(","))
                    if (!s.trim().isEmpty())
                        skills.add(s.trim());

                if (isEdit) {
                    jobToEdit.setJudul(titleField.getText().trim());
                    jobToEdit.setDivisi(divField.getValue());
                    jobToEdit.setSisaSlot(slots);
                    jobToEdit.setDeskripsi(descArea.getText().trim());
                    jobToEdit.setSkillDibutuhkan(skills);
                    DatabaseManager.updateLowongan(jobToEdit);
                } else {
                    String newId = "NEW-" + System.currentTimeMillis();
                    DatabaseManager.tambahLowongan(new Lowongan(newId, divField.getValue(), titleField.getText().trim(),
                            descArea.getText().trim(), skills, slots, java.time.LocalDate.now().toString()));
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
        showModal(modalScroll);
    }

    private void showDeleteConfirmModal(Lowongan job) {
        VBox modal = new VBox(20);
        modal.setMaxSize(350, 250);
        modal.setAlignment(Pos.CENTER);
        modal.setPadding(new Insets(30));
        modal.getStyleClass().add("modal-card");

        Label title = new Label("Hapus Lowongan?");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        title.setTextFill(Color.web("#1A202C"));
        Text sub = new Text("Apakah Anda yakin ingin menghapus lowongan '" + job.getJudul() + "'?");
        sub.setWrappingWidth(280);
        sub.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        sub.setFill(Color.web("#1A202C"));

        HBox buttons = new HBox(15);
        buttons.setAlignment(Pos.CENTER);
        Button cancel = new Button("Batal");
        cancel.getStyleClass().add("btn-outline");
        cancel.setOnAction(e -> hideModal());
        Button delete = new Button("Hapus");
        delete.getStyleClass().add("btn-danger");
        delete.setOnAction(e -> {
            DatabaseManager.hapusLowongan(job.getId());
            refreshJobList();
            hideModal();
        });

        buttons.getChildren().addAll(cancel, delete);
        modal.getChildren().addAll(title, sub, buttons);
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
        tf.getStyleClass().add("form-input");
        tf.setStyle("-fx-text-fill: #1A202C; -fx-prompt-text-fill: #A0AEC0;");
        return tf;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(title.equals("Error") ? Alert.AlertType.ERROR : Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}