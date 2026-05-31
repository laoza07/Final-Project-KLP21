package areda.view.user;

import areda.model.DatabaseManager;
import areda.model.DatabaseManager.Profil;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import java.io.File;

public class ProfileView extends StackPane {

    private VBox mainContent;
    private StackPane modalOverlay;
    private VBox dataTableContainer;
    private Label hlName, hlEmail, hlLocation, hlInitials;
    private Circle hlAvatarCircle;

    public ProfileView() {
        this.setStyle("-fx-background-color: white;");
        mainContent = new VBox(30);
        mainContent.setPadding(new Insets(30));
        mainContent.setAlignment(Pos.TOP_LEFT);
        HBox header = createHeader();
        HBox highlightCard = createHighlightCard();
        VBox tableSection = createDataTableSection();
        mainContent.getChildren().addAll(header, highlightCard, tableSection);
        modalOverlay = new StackPane();
        modalOverlay.setStyle("-fx-background-color: rgba(0,0,0,0.5);");
        modalOverlay.setVisible(false);
        this.getChildren().addAll(mainContent, modalOverlay);
        refreshUI();
    }

    private HBox createHeader() {
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("Profil Saya");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox icons = new HBox(15, new Label(""), new Circle(15, Color.LIGHTGRAY));
        icons.setAlignment(Pos.CENTER);
        header.getChildren().addAll(title, spacer, icons);
        return header;
    }

    private HBox createHighlightCard() {
        HBox card = new HBox(25);
        card.setPadding(new Insets(30));
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle(
                "-fx-background-color: #BEE3F8; -fx-background-radius: 20; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5);");
        StackPane avatarStack = new StackPane();
        hlAvatarCircle = new Circle(50, Color.WHITE);
        hlInitials = new Label();
        hlInitials.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        hlInitials.setTextFill(Color.web("#2B6CB0"));
        avatarStack.getChildren().addAll(hlAvatarCircle, hlInitials);
        VBox info = new VBox(5);
        info.setAlignment(Pos.CENTER_LEFT);
        hlName = new Label();
        hlName.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        hlEmail = new Label();
        hlEmail.setTextFill(Color.web("#4A5568"));
        hlLocation = new Label();
        hlLocation.setFont(Font.font("Arial", 14));
        hlLocation.setTextFill(Color.web("#2D3748"));
        HBox locBox = new HBox(5, new Label("📍"), hlLocation);
        locBox.setAlignment(Pos.CENTER_LEFT);
        info.getChildren().addAll(hlName, hlEmail, locBox);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Button editBtn = new Button("Edit Profil");
        editBtn.setStyle(
                "-fx-background-color: white; -fx-text-fill: #2B6CB0; -fx-font-weight: bold; -fx-background-radius: 25; -fx-padding: 10 20; -fx-cursor: hand;");
        editBtn.setGraphic(new Label(""));
        editBtn.setOnAction(e -> showEditModal());
        card.getChildren().addAll(avatarStack, info, spacer, editBtn);
        return card;
    }

    private VBox createDataTableSection() {
        VBox section = new VBox(20);
        HBox titleBox = new HBox(10, new Label("🪪"), new Label("Data Lengkap"));
        titleBox.setAlignment(Pos.CENTER_LEFT);
        ((Label) titleBox.getChildren().get(1)).setFont(Font.font("Arial", FontWeight.BOLD, 18));
        dataTableContainer = new VBox();
        dataTableContainer.setStyle("-fx-background-color: white;");
        section.getChildren().addAll(titleBox, dataTableContainer);
        return section;
    }

    private void refreshUI() {
        Profil p = DatabaseManager.getProfil();
        hlName.setText(p.nama);
        hlEmail.setText(p.email);
        hlLocation.setText(p.domisili);
        if (p.photoPath != null && !p.photoPath.isEmpty()) {
            try {
                hlAvatarCircle.setFill(new ImagePattern(new javafx.scene.image.Image(p.photoPath)));
                hlInitials.setVisible(false);
            } catch (Exception e) {
                hlAvatarCircle.setFill(Color.WHITE);
                hlInitials.setVisible(true);
                hlInitials.setText(getInitials(p.nama));
            }
        } else {
            hlAvatarCircle.setFill(Color.WHITE);
            hlInitials.setVisible(true);
            hlInitials.setText(getInitials(p.nama));
        }
        dataTableContainer.getChildren().clear();
        dataTableContainer.getChildren().addAll(
                createDataRow("👤", "Nama Lengkap", p.nama),
                createDataRow("🎂", "Tempat Tanggal Lahir", p.tempatLahir + ", " + p.tanggalLahir),
                createDataRow("🚻", "Jenis Kelamin", p.jenisKelamin),
                createDataRow("", "Status", p.statusPernikahan),
                createDataRow("📞", "No.HP", p.telepon),
                createDataRow("", "Email", p.email),
                createDataRow("", "Alamat", p.alamat),
                createDataRow("🎓", "Pendidikan Terakhir", p.pendidikan));
    }

    private VBox createDataRow(String icon, String label, String value) {
        VBox rowBox = new VBox();
        HBox row = new HBox(20);
        row.setPadding(new Insets(15, 5, 15, 5));
        row.setAlignment(Pos.CENTER_LEFT);
        Label lblIcon = new Label(icon);
        Label lblName = new Label(label);
        lblName.setPrefWidth(200);
        lblName.setTextFill(Color.web("#718096"));
        lblName.setFont(Font.font("Arial", 14));
        Label lblVal = new Label(value);
        lblVal.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        row.getChildren().addAll(lblIcon, lblName, lblVal);
        rowBox.getChildren().addAll(row, new Line(0, 0, 800, 0) {
            {
                setStroke(Color.web("#EDF2F7"));
            }
        });
        return rowBox;
    }

    private String getInitials(String name) {
        if (name == null || name.isEmpty())
            return "??";
        String[] parts = name.split(" ");
        if (parts.length >= 2)
            return (parts[0].substring(0, 1) + parts[1].substring(0, 1)).toUpperCase();
        return name.substring(0, Math.min(2, name.length())).toUpperCase();
    }

    private void showEditModal() {
        Profil p = DatabaseManager.getProfil();
        VBox modal = new VBox(25);
        modal.setMaxSize(800, 650);
        modal.setPadding(new Insets(40));
        modal.setStyle("-fx-background-color: white; -fx-background-radius: 30;");
        HBox top = new HBox();
        top.setAlignment(Pos.CENTER);
        Label title = new Label("Isi Data Profil");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        Region s1 = new Region();
        HBox.setHgrow(s1, Priority.ALWAYS);
        Button close = new Button("X");
        close.setStyle("-fx-background-color: transparent; -fx-font-weight: bold; -fx-font-size: 18px;");
        close.setOnAction(e -> hideModal());
        top.getChildren().addAll(new Region() {
            {
                setPrefWidth(30);
            }
        }, title, s1, close);
        VBox photoBox = new VBox(10);
        photoBox.setAlignment(Pos.CENTER);
        Label photoLbl = new Label("Tambahkan Foto Profil:");
        photoLbl.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        StackPane cameraBtn = new StackPane();
        Circle camCircle = new Circle(40, Color.web("#F7FAFC"));
        camCircle.setStroke(Color.web("#E2E8F0"));
        Label camIcon = new Label("");
        cameraBtn.getChildren().addAll(camCircle, camIcon);
        cameraBtn.setStyle("-fx-cursor: hand;");
        final String[] tempPhotoPath = { p.photoPath };
        cameraBtn.setOnMouseClicked(e -> {
            FileChooser fc = new FileChooser();
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"));
            File file = fc.showOpenDialog(this.getScene().getWindow());
            if (file != null) {
                tempPhotoPath[0] = file.toURI().toString();
                camCircle.setFill(new ImagePattern(new javafx.scene.image.Image(tempPhotoPath[0])));
                camIcon.setVisible(false);
            }
        });
        if (p.photoPath != null && !p.photoPath.isEmpty()) {
            camCircle.setFill(new ImagePattern(new javafx.scene.image.Image(p.photoPath)));
            camIcon.setVisible(false);
        }
        photoBox.getChildren().addAll(photoLbl, cameraBtn);
        GridPane grid = new GridPane();
        grid.setHgap(30);
        grid.setVgap(15);
        TextField fName = createField(p.nama);
        TextField fTempat = new TextField(p.tempatLahir != null ? p.tempatLahir : "");
        DatePicker fTanggal = new DatePicker();
        try {
            fTanggal.setValue(java.time.LocalDate.parse(p.tanggalLahir));
        } catch (Exception ex) {
            fTanggal.setValue(java.time.LocalDate.now());
        }
        ComboBox<String> fGender = new ComboBox<>();
        fGender.getItems().addAll("Pria", "Wanita");
        fGender.setValue(p.jenisKelamin.isEmpty() ? "Pria" : p.jenisKelamin);
        fGender.setStyle("-fx-background-color: #F7FAFC; -fx-background-radius: 8; -fx-padding: 5;");
        ComboBox<String> fStatus = new ComboBox<>();
        fStatus.getItems().addAll("Belum Menikah", "Menikah");
        fStatus.setValue(p.statusPernikahan.isEmpty() ? "Belum Menikah" : p.statusPernikahan);
        fStatus.setStyle("-fx-background-color: #F7FAFC; -fx-background-radius: 8; -fx-padding: 5;");
        TextField fPhone = createField(p.telepon);

        fPhone.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*"))
                fPhone.setText(newVal.replaceAll("[^\\d]", ""));
        });
        TextField fEmail = createField(p.email);
        TextField fAlamat = createField(p.alamat);
        ComboBox<String> fEdu = new ComboBox<>();
        fEdu.getItems().addAll("SMA/SMK", "D3 (Diploma)", "S1 (Sarjana)", "S2 (Magister)", "S3 (Doktor)");
        fEdu.setValue(p.pendidikan.isEmpty() ? "S1 (Sarjana)" : p.pendidikan);
        fEdu.setStyle("-fx-background-color: #F7FAFC; -fx-background-radius: 8; -fx-padding: 5;");
        grid.add(new Label("Nama Lengkap"), 0, 0);
        grid.add(fName, 0, 1);
        grid.add(new Label("Tempat, Tanggal Lahir"), 0, 2);
        grid.add(new HBox(10, fTempat, fTanggal), 0, 3);
        grid.add(new Label("Jenis Kelamin"), 0, 4);
        grid.add(fGender, 0, 5);
        grid.add(new Label("Status Pernikahan"), 0, 6);
        grid.add(fStatus, 0, 7);
        grid.add(new Label("No.HP"), 1, 0);
        grid.add(fPhone, 1, 1);
        grid.add(new Label("Email"), 1, 2);
        grid.add(fEmail, 1, 3);
        grid.add(new Label("Alamat Lengkap"), 1, 4);
        grid.add(fAlamat, 1, 5);
        grid.add(new Label("Pendidikan Terakhir"), 1, 6);
        grid.add(fEdu, 1, 7);
        Button save = new Button("Simpan Perubahan");
        save.setMaxWidth(Double.MAX_VALUE);
        save.setStyle(
                "-fx-background-color: #3182CE; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 15; -fx-background-radius: 10; -fx-cursor: hand;");
        save.setOnAction(e -> {
            if (fName.getText().isEmpty())
                return;
            p.nama = fName.getText();
            p.tempatLahir = fTempat.getText();
            p.tanggalLahir = fTanggal.getValue() != null ? fTanggal.getValue().toString() : "";
            p.jenisKelamin = fGender.getValue();
            p.statusPernikahan = fStatus.getValue();
            p.telepon = fPhone.getText();
            p.email = fEmail.getText();
            p.alamat = fAlamat.getText();
            p.pendidikan = fEdu.getValue();
            p.photoPath = tempPhotoPath[0];
            DatabaseManager.updateProfil(p);
            refreshUI();
            hideModal();
            showAlert("Sukses", "Profil berhasil diperbarui!");
        });
        modal.getChildren().addAll(top, photoBox, grid, save);
        showModal(modal);
    }

    private TextField createField(String text) {
        TextField tf = new TextField(text);
        tf.setStyle("-fx-background-color: #F7FAFC; -fx-background-radius: 8; -fx-padding: 10;");
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
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}