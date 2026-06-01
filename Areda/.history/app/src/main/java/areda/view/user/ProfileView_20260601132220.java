package areda.view.user;

import areda.model.DatabaseManager;
import areda.model.Profil;
import areda.view.components.AvatarComponent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class ProfileView extends StackPane {
    private VBox mainContent;
    private StackPane modalOverlay;
    private VBox dataTableContainer;
    private Label hlName, hlEmail, hlLocation;
    private AvatarComponent hlAvatar;
    private HBox highlightCard;

    public ProfileView() {
        this.setStyle("-fx-background-color: white;");
        mainContent = new VBox(30);
        mainContent.setPadding(new Insets(30));
        mainContent.setAlignment(Pos.TOP_LEFT);

        ScrollPane scroll = new ScrollPane(mainContent);
        scroll.setFitToWidth(true);
        scroll.setStyle(
                "-fx-background-color: transparent; -fx-background: transparent; -fx-border-color: transparent;");
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        VBox.setVgrow(scroll, Priority.ALWAYS);

        HBox header = createHeader();
        highlightCard = createHighlightCard();
        VBox tableSection = createDataTableSection();
        mainContent.getChildren().addAll(header, highlightCard, tableSection);

        modalOverlay = new StackPane();
        modalOverlay.getStyleClass().add("modal-overlay");
        modalOverlay.setVisible(false);
        this.getChildren().addAll(scroll, modalOverlay);
        refreshUI();
    }

    private HBox createHeader() {
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("Profil Saya");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        title.setTextFill(Color.web("#1A202C"));
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        header.getChildren().addAll(title, spacer);
        return header;
    }

    private HBox createHighlightCard() {
        HBox card = new HBox(25);
        card.setPadding(new Insets(30));
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle(
                "-fx-background-color: #BEE3F8; -fx-background-radius: 20; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.08), 10, 0, 0, 5);");

        hlAvatar = new AvatarComponent("?", 80);

        VBox info = new VBox(5);
        info.setAlignment(Pos.CENTER_LEFT);

        hlName = new Label();
        hlName.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        hlName.setTextFill(Color.web("#1A202C"));

        hlEmail = new Label();
        hlEmail.setTextFill(Color.web("#2D3748"));
        hlEmail.setFont(Font.font("Arial", 14));

        hlLocation = new Label();
        hlLocation.setFont(Font.font("Arial", 14));
        hlLocation.setTextFill(Color.web("#2D3748"));

        Label pinIcon = new Label("📍");
        pinIcon.setTextFill(Color.web("#2D3748"));
        HBox locBox = new HBox(5, pinIcon, hlLocation);
        locBox.setAlignment(Pos.CENTER_LEFT);

        info.getChildren().addAll(hlName, hlEmail, locBox);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button editBtn = new Button("Edit Profil");
        editBtn.getStyleClass().add("btn-outline");
        editBtn.setStyle("-fx-text-fill: #2B6CB0; -fx-border-color: #2B6CB0;");
        editBtn.setOnAction(e -> showEditModal());

        card.getChildren().addAll(hlAvatar, info, spacer, editBtn);
        return card;
    }

    private VBox createDataTableSection() {
        VBox section = new VBox(20);

        Label idIcon = new Label("🪪");
        idIcon.setTextFill(Color.web("#1A202C"));
        Label titleLbl = new Label("Data Lengkap");
        titleLbl.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        titleLbl.setTextFill(Color.web("#1A202C"));

        HBox titleBox = new HBox(10, idIcon, titleLbl);
        titleBox.setAlignment(Pos.CENTER_LEFT);

        dataTableContainer = new VBox();
        dataTableContainer.setStyle("-fx-background-color: white;");
        section.getChildren().addAll(titleBox, dataTableContainer);
        return section;
    }

    private void refreshUI() {
        Profil p = DatabaseManager.getCurrentUserProfil();
        String displayName = (p.nama == null || p.nama.isEmpty()) ? "Belum diisi" : p.nama;

        hlName.setText(displayName);
        hlEmail.setText(p.email == null ? "" : p.email);

        if (highlightCard.getChildren().get(0) instanceof AvatarComponent) {
            highlightCard.getChildren().set(0, new AvatarComponent(displayName, 80));
        }

        dataTableContainer.getChildren().clear();
        dataTableContainer.getChildren().addAll(
                createDataRow("👤", "Nama Lengkap", p.nama),
                createDataRow("🎂", "Tempat, Tanggal Lahir",
                        (p.tempatLahir == null || p.tempatLahir.isEmpty() ? "-" : p.tempatLahir) + ", " +
                                (p.tanggalLahir == null || p.tanggalLahir.isEmpty() ? "-" : p.tanggalLahir)),
                createDataRow("🚻", "Jenis Kelamin", p.jenisKelamin),
                createDataRow("💍", "Status", p.statusPernikahan),
                createDataRow("📞", "No.HP", p.telepon),
                createDataRow("📧", "Email", p.email),
                createDataRow("🏠", "Alamat", p.alamat),
                createDataRow("🎓", "Pendidikan Terakhir", p.pendidikan));
    }

    private VBox createDataRow(String icon, String label, String value) {
        VBox rowBox = new VBox();
        HBox row = new HBox(20);
        row.setPadding(new Insets(15, 5, 15, 5));
        row.setAlignment(Pos.CENTER_LEFT);

        Label lblIcon = new Label(icon);
        lblIcon.setFont(Font.font(16));
        lblIcon.setTextFill(Color.web("#1A202C"));

        Label lblName = new Label(label);
        lblName.setPrefWidth(200);
        lblName.setTextFill(Color.web("#4A5568"));
        lblName.setFont(Font.font("Arial", 14));

        String displayValue = (value == null || value.isEmpty()) ? "Belum diisi" : value;
        Label lblVal = new Label(displayValue);
        lblVal.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        lblVal.setTextFill(Color.web("#1A202C"));
        lblVal.setWrapText(true);
        HBox.setHgrow(lblVal, Priority.ALWAYS);

        row.getChildren().addAll(lblIcon, lblName, lblVal);

        Separator separator = new Separator();
        separator.setStyle("-fx-background-color: #EDF2F7;");

        rowBox.getChildren().addAll(row, separator);
        return rowBox;
    }

    private void showEditModal() {
        Profil p = DatabaseManager.getCurrentUserProfil();

        VBox modal = new VBox(25);
        modal.setMaxSize(800, 650);
        modal.setPadding(new Insets(40));
        modal.getStyleClass().add("modal-card");

        ScrollPane modalScroll = new ScrollPane(modal);
        modalScroll.setFitToWidth(true);
        modalScroll.setStyle(
                "-fx-background-color: transparent; -fx-background: transparent; -fx-border-color: transparent;");
        modalScroll.setMaxSize(820, 700);

        HBox top = new HBox();
        top.setAlignment(Pos.CENTER);

        Label title = new Label("Isi Data Profil");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        title.setTextFill(Color.web("#1A202C"));

        Region s1 = new Region();
        HBox.setHgrow(s1, Priority.ALWAYS);

        Button close = new Button("X");
        close.getStyleClass().add("btn-outline");
        close.setTextFill(Color.web("#1A202C"));
        close.setOnAction(e -> hideModal());

        top.getChildren().addAll(new Region() {
            {
                setPrefWidth(30);
            }
        }, title, s1, close);

        GridPane grid = new GridPane();
        grid.setHgap(30);
        grid.setVgap(15);

        TextField fName = createField(p.nama);
        TextField fTempat = new TextField(p.tempatLahir != null ? p.tempatLahir : "");
        fTempat.getStyleClass().add("form-input");

        DatePicker fTanggal = new DatePicker();
        fTanggal.getStyleClass().add("form-input");
        try {
            if (p.tanggalLahir != null && !p.tanggalLahir.isEmpty())
                fTanggal.setValue(java.time.LocalDate.parse(p.tanggalLahir));
        } catch (Exception ex) {
            fTanggal.setValue(java.time.LocalDate.now());
        }

        ComboBox<String> fGender = new ComboBox<>();
        fGender.getItems().addAll("Pria", "Wanita");
        fGender.setValue((p.jenisKelamin == null || p.jenisKelamin.isEmpty()) ? "Pria" : p.jenisKelamin);
        fGender.getStyleClass().add("form-input");

        ComboBox<String> fStatus = new ComboBox<>();
        fStatus.getItems().addAll("Belum Menikah", "Menikah");
        fStatus.setValue(
                (p.statusPernikahan == null || p.statusPernikahan.isEmpty()) ? "Belum Menikah" : p.statusPernikahan);
        fStatus.getStyleClass().add("form-input");

        TextField fPhone = createField(p.telepon);
        fPhone.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*"))
                fPhone.setText(newVal.replaceAll("[^\\d]", ""));
        });

        TextField fEmail = createField(p.email);
        fEmail.setDisable(true);

        TextField fAlamat = createField(p.alamat);

        ComboBox<String> fEdu = new ComboBox<>();
        fEdu.getItems().addAll("SMA/SMK", "D3 (Diploma)", "S1 (Sarjana)", "S2 (Magister)", "S3 (Doktor)");
        fEdu.setValue((p.pendidikan == null || p.pendidikan.isEmpty()) ? "S1 (Sarjana)" : p.pendidikan);
        fEdu.getStyleClass().add("form-input");

        grid.add(createLabel("Nama Lengkap"), 0, 0);
        grid.add(fName, 0, 1);
        grid.add(createLabel("Tempat, Tanggal Lahir"), 0, 2);
        grid.add(new HBox(10, fTempat, fTanggal), 0, 3);
        grid.add(createLabel("Jenis Kelamin"), 0, 4);
        grid.add(fGender, 0, 5);
        grid.add(createLabel("Status Pernikahan"), 0, 6);
        grid.add(fStatus, 0, 7);
        grid.add(createLabel("No.HP"), 1, 0);
        grid.add(fPhone, 1, 1);
        grid.add(createLabel("Email"), 1, 2);
        grid.add(fEmail, 1, 3);
        grid.add(createLabel("Alamat Lengkap"), 1, 4);
        grid.add(fAlamat, 1, 5);
        grid.add(createLabel("Pendidikan Terakhir"), 1, 6);
        grid.add(fEdu, 1, 7);

        Button save = new Button("Simpan Perubahan");
        save.setMaxWidth(Double.MAX_VALUE);
        save.getStyleClass().add("btn-primary");

        save.setOnAction(e -> {
            if (fName.getText().isEmpty()) {
                showAlert("Error", "Nama lengkap harus diisi!");
                return;
            }

            p.nama = fName.getText();
            p.tempatLahir = fTempat.getText();
            p.tanggalLahir = fTanggal.getValue() != null ? fTanggal.getValue().toString() : "";
            p.jenisKelamin = fGender.getValue();
            p.statusPernikahan = fStatus.getValue();
            p.telepon = fPhone.getText();
            p.alamat = fAlamat.getText();
            p.pendidikan = fEdu.getValue();

            DatabaseManager.updateCurrentUserProfil(p);
            refreshUI();
            hideModal();
            showAlert("Sukses", "Profil berhasil diperbarui!");
        });

        modal.getChildren().addAll(top, grid, save);
        showModal(modalScroll);
    }

    private Label createLabel(String text) {
        Label lbl = new Label(text);
        lbl.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        lbl.setTextFill(Color.web("#1A202C"));
        return lbl;
    }

    private TextField createField(String text) {
        TextField tf = new TextField(text != null ? text : "");
        tf.getStyleClass().add("form-input");
        return tf;
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

    private void showAlert(String title, String message) {
        Alert alert = new Alert(title.equals("Error") ? Alert.AlertType.ERROR : Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}