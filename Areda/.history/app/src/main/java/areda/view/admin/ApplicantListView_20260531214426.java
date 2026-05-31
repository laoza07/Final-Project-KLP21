package areda.view.admin;

import areda.model.DatabaseManager;
import areda.model.DatabaseManager.Lamaran;
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

public class ApplicantListView extends StackPane {

    private VBox mainContent;
    private StackPane modalOverlay;
    private VBox tableContainer;
    private ComboBox<String> lowonganFilter;
    private TextField searchField;

    public ApplicantListView() {
        this.setStyle("-fx-background-color: white;");

        mainContent = new VBox(20);
        mainContent.setPadding(new Insets(30));
        mainContent.setAlignment(Pos.TOP_LEFT);

        HBox header = createHeader();
        HBox filterBar = createFilterBar();

        tableContainer = new VBox();
        tableContainer.setStyle("-fx-background-color: white;");
        refreshTable();

        mainContent.getChildren().addAll(header, filterBar, tableContainer);

        modalOverlay = new StackPane();
        modalOverlay.setStyle("-fx-background-color: rgba(0,0,0,0.4);");
        modalOverlay.setVisible(false);

        this.getChildren().addAll(mainContent, modalOverlay);
    }

    private HBox createHeader() {
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(5);
        Label title = new Label("Pelamar");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        Label subtitle = new Label("Daftar pelamar pekerjaan");
        subtitle.setFont(Font.font("Arial", 12));
        subtitle.setTextFill(Color.web("#718096"));
        titleBox.getChildren().addAll(title, subtitle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox iconBox = new HBox(15);
        iconBox.setAlignment(Pos.CENTER);
        Label bell = new Label("🔔");
        Circle avatar = new Circle(15, Color.LIGHTGRAY);
        iconBox.getChildren().addAll(bell, avatar);

        header.getChildren().addAll(titleBox, spacer, iconBox);
        return header;
    }

    private HBox createFilterBar() {
        HBox bar = new HBox(15);
        bar.setAlignment(Pos.CENTER_LEFT);

        lowonganFilter = new ComboBox<>();
        lowonganFilter.getItems().addAll(DatabaseManager.getAllDivisi());
        lowonganFilter.setValue("Semua Lowongan");
        lowonganFilter.setStyle(
                "-fx-background-color: #F7FAFC; -fx-background-radius: 8; -fx-border-color: #E2E8F0; -fx-border-radius: 8; -fx-padding: 5 10;");
        lowonganFilter.setPrefWidth(200);
        lowonganFilter.setOnAction(e -> refreshTable());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox rightBox = new HBox(10);
        rightBox.setAlignment(Pos.CENTER_RIGHT);

        searchField = new TextField();
        searchField.setPromptText("🔍 Cari Nama...");
        searchField.setStyle(
                "-fx-background-color: #F7FAFC; -fx-background-radius: 8; -fx-border-color: #E2E8F0; -fx-border-radius: 8; -fx-padding: 8;");
        searchField.setPrefWidth(200);
        searchField.textProperty().addListener((obs, oldVal, newVal) -> refreshTable());

        rightBox.getChildren().add(searchField);
        bar.getChildren().addAll(lowonganFilter, spacer, rightBox);
        return bar;
    }

    private void refreshTable() {
        tableContainer.getChildren().clear();

        HBox headerRow = createTableRow("No.", "Nama Pelamar", "Divisi", "Tanggal Melamar", "Status", "Aksi", true,
                null);
        tableContainer.getChildren().add(headerRow);

        String selectedDivisi = lowonganFilter.getValue();
        String searchText = searchField.getText().toLowerCase();

        int no = 1;
        for (Lamaran lamaran : DatabaseManager.getAllLamaran()) {
            Lowongan l = DatabaseManager.getLowonganById(lamaran.idLowongan);
            String divisi = (l != null) ? l.divisi : "Unknown";
            String jobTitle = (l != null) ? l.judul : "Unknown";

            boolean matchDivisi = selectedDivisi.equals("Semua Lowongan") || divisi.equals(selectedDivisi);
            boolean matchSearch = searchText.isEmpty() || lamaran.namaPelamar.toLowerCase().contains(searchText);

            if (matchDivisi && matchSearch) {
                tableContainer.getChildren().add(createTableRow(
                        String.valueOf(no++),
                        lamaran.namaPelamar,
                        divisi,
                        lamaran.tanggal,
                        lamaran.statusTahapan,
                        "↗",
                        false,
                        lamaran));
            }
        }

        if (no == 1) {
            Label empty = new Label("Tidak ada data pelamar.");
            empty.setPadding(new Insets(20));
            empty.setTextFill(Color.web("#718096"));
            tableContainer.getChildren().add(empty);
        }
    }

    private HBox createTableRow(String no, String name, String divisi, String date, String status, String actionText,
            boolean isHeader, Lamaran lamaran) {
        HBox row = new HBox();
        row.setPadding(new Insets(12, 15, 12, 15));
        row.setAlignment(Pos.CENTER_LEFT);

        if (isHeader) {
            row.setStyle("-fx-background-color: #F7FAFC; -fx-background-radius: 8;");
        } else {
            row.setStyle("-fx-border-color: #EDF2F7; -fx-border-width: 0 0 1 0;");
        }

        Label lblNo = createCellLabel(no, 50, isHeader);

        HBox nameBox = new HBox(10);
        nameBox.setAlignment(Pos.CENTER_LEFT);
        nameBox.setPrefWidth(200);
        if (!isHeader) {
            String initials = name.substring(0, 1).toUpperCase();
            StackPane avatarPane = new StackPane();
            avatarPane.setPrefSize(30, 30);
            avatarPane.setStyle("-fx-background-color: #C6F6D5; -fx-background-radius: 15;");

            Label initialLabel = new Label(initials);
            initialLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
            initialLabel.setTextFill(Color.web("#22543D"));
            avatarPane.getChildren().add(initialLabel);

            Label lName = new Label(name);
            lName.setFont(Font.font("Arial", 14));
            lName.setTextFill(Color.web("#2D3748"));
            nameBox.getChildren().addAll(avatarPane, lName);
        } else {
            Label lName = new Label(name);
            lName.setFont(Font.font("Arial", FontWeight.BOLD, 12));
            lName.setTextFill(Color.web("#718096"));
            nameBox.getChildren().add(lName);
        }

        Label lblDivisi = createCellLabel(divisi, 150, isHeader);
        Label lblDate = createCellLabel(date, 120, isHeader);

        Node statusNode;
        if (isHeader) {
            statusNode = createCellLabel(status, 100, true);
        } else {
            Label pill = new Label(status);

            pill.setStyle(
                    "-fx-background-color: #EDF2F7; -fx-text-fill: #4A5568; -fx-background-radius: 20; -fx-padding: 5 15; -fx-font-size: 11px; -fx-font-weight: bold;");
            StackPane cell = new StackPane(pill);
            cell.setPrefWidth(100);
            cell.setAlignment(Pos.CENTER_LEFT);
            statusNode = cell;
        }

        Node actionNode;
        if (isHeader) {
            actionNode = createCellLabel(actionText, 50, true);
        } else {
            Button actionBtn = new Button(actionText);
            actionBtn.setStyle(
                    "-fx-background-color: transparent; -fx-text-fill: #2B6CB0; -fx-font-size: 16px; -fx-cursor: hand;");
            actionBtn.setOnAction(e -> showDetailModal(lamaran));
            HBox cell = new HBox(actionBtn);
            cell.setPrefWidth(50);
            cell.setAlignment(Pos.CENTER_LEFT);
            actionNode = cell;
        }

        row.getChildren().addAll(lblNo, nameBox, lblDivisi, lblDate, statusNode, actionNode);
        return row;
    }

    private Label createCellLabel(String text, double width, boolean isHeader) {
        Label lbl = new Label(text);
        lbl.setPrefWidth(width);
        if (isHeader) {
            lbl.setFont(Font.font("Arial", FontWeight.BOLD, 12));
            lbl.setTextFill(Color.web("#718096"));
        } else {
            lbl.setFont(Font.font("Arial", 13));
            lbl.setTextFill(Color.web("#4A5568"));
        }
        return lbl;
    }

    private void showDetailModal(Lamaran app) {
        VBox modal = new VBox(20);
        modal.setMaxSize(600, 550);
        modal.setPadding(new Insets(30));
        modal.setStyle("-fx-background-color: white; -fx-background-radius: 20;");

        HBox top = new HBox();
        top.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("Detail Lamaran");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Button close = new Button("X");
        close.setStyle(
                "-fx-background-color: transparent; -fx-font-weight: bold; -fx-cursor: hand; -fx-font-size: 16px;");
        close.setOnAction(e -> hideModal());
        top.getChildren().addAll(title, spacer, close);

        HBox contentBox = new HBox(30);

        VBox profileInfo = new VBox(15);
        profileInfo.setPrefWidth(300);

        HBox avatarBox = new HBox(15);
        avatarBox.setAlignment(Pos.CENTER_LEFT);
        Circle bigAvatar = new Circle(30, Color.web("#48BB78"));
        VBox nameBox = new VBox(5);
        Label nameLbl = new Label(app.namaPelamar);
        nameLbl.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        Label statusLbl = new Label(app.statusTahapan);
        String color = switch (app.statusTahapan) {
            case "Diterima" -> "#C6F6D5";
            case "Ditolak" -> "#FED7D7";
            case "Interview" -> "#BEE3F8";
            default -> "#EDF2F7";
        };
        statusLbl.setStyle("-fx-background-color: " + color
                + "; -fx-text-fill: black; -fx-background-radius: 20; -fx-padding: 5 15; -fx-font-size: 11px;");
        nameBox.getChildren().addAll(nameLbl, statusLbl);
        avatarBox.getChildren().addAll(bigAvatar, nameBox);

        GridPane details = new GridPane();
        details.setVgap(10);
        details.setHgap(10);
        details.add(createLabel("Email", true), 0, 0);
        details.add(createLabel(app.emailPelamar, false), 1, 0);
        details.add(createLabel("No. HP", true), 0, 1);
        details.add(createLabel(app.teleponPelamar, false), 1, 1);

        VBox aboutBox = new VBox(5);
        aboutBox.getChildren().addAll(createLabel("Tentang Saya", true), createLabel(app.tentangPelamar, false));

        VBox statusBox = new VBox(5);
        statusBox.setPadding(new Insets(10, 0, 0, 0));
        statusBox.getChildren().add(createLabel("Ubah Status Pelamar", true));
        ComboBox<String> statusCombo = new ComboBox<>();
        statusCombo.getItems().addAll("Seleksi Berkas", "Interview", "Diterima", "Ditolak");
        statusCombo.setValue(app.statusTahapan);
        statusCombo.setStyle("-fx-background-color: #F7FAFC; -fx-background-radius: 8;");

        Button saveStatusBtn = new Button("Update Status");
        saveStatusBtn.setStyle(
                "-fx-background-color: #0066CC; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand;");
        saveStatusBtn.setOnAction(e -> {
            DatabaseManager.updateLamaranStatus(app.idLamaran, statusCombo.getValue());
            statusLbl.setText(statusCombo.getValue());
            statusLbl.setStyle("-fx-background-color: " + (switch (statusCombo.getValue()) {
                case "Diterima" -> "#C6F6D5";
                case "Ditolak" -> "#FED7D7";
                case "Interview" -> "#BEE3F8";
                default -> "#EDF2F7";
            }) + "; -fx-text-fill: black; -fx-background-radius: 20; -fx-padding: 5 15; -fx-font-size: 11px;");
            refreshTable();
        });
        HBox statusControl = new HBox(10, statusCombo, saveStatusBtn);
        statusBox.getChildren().add(statusControl);

        profileInfo.getChildren().addAll(avatarBox, details, aboutBox, statusBox);

        VBox docs = new VBox(15);
        docs.setPadding(new Insets(20));
        docs.setStyle("-fx-background-color: #EBF8FF; -fx-background-radius: 15;");
        docs.setPrefWidth(200);

        Label docsTitle = new Label("Dokumen Terunggah");
        docsTitle.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        docsTitle.setTextFill(Color.web("#2B6CB0"));

        docs.getChildren().addAll(
                docsTitle,
                createDocCard(app.cvPath.isEmpty() ? "Belum ada CV" : app.cvPath),
                createDocCard(app.motletPath.isEmpty() ? "Belum ada ML" : app.motletPath));

        contentBox.getChildren().addAll(profileInfo, docs);
        modal.getChildren().addAll(top, contentBox);

        modalOverlay.getChildren().clear();
        modalOverlay.getChildren().add(modal);
        modalOverlay.setVisible(true);
        mainContent.setDisable(true);
    }

    private void hideModal() {
        modalOverlay.setVisible(false);
        mainContent.setDisable(false);
    }

    private Label createLabel(String text, boolean isBold) {
        Label lbl = new Label(text);
        lbl.setWrapText(true);
        if (isBold) {
            lbl.setFont(Font.font("Arial", FontWeight.BOLD, 12));
            lbl.setTextFill(Color.web("#718096"));
        } else {
            lbl.setFont(Font.font("Arial", 14));
        }
        return lbl;
    }

    private HBox createDocCard(String filename) {
        HBox card = new HBox(10);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(10));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 8;");

        Label icon = new Label("");
        Label name = new Label(filename);
        name.setFont(Font.font("Arial", 12));
        name.setWrapText(true);
        name.setMaxWidth(130);

        card.getChildren().addAll(icon, name);
        return card;
    }
}
