package areda.view.user;

import areda.model.DatabaseManager;
import areda.model.Lamaran;
import areda.model.Lowongan;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class MyApplicationsView extends StackPane {
    private VBox mainContent;
    private StackPane modalOverlay;
    private VBox tableContainer;
    private ComboBox<String> divisiFilter;
    private ComboBox<String> statusFilter;

    public MyApplicationsView() {
        this.setStyle("-fx-background-color: white;");
        mainContent = new VBox(25);
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

    private void refreshTable() {
        tableContainer.getChildren().clear();

        String selectedDivisi = divisiFilter.getValue();
        String selectedStatus = statusFilter.getValue();

        HBox headerRow = createTableRow("No.", "Nama Lowongan", "Divisi", "Tanggal Melamar",
                "Tanggal Interview", "Status", "Progress", true, null);
        tableContainer.getChildren().add(headerRow);

        int no = 1;
        for (Lamaran lamaran : DatabaseManager.getMyLamaran()) {
            Lowongan l = DatabaseManager.getLowonganById(lamaran.idLowongan);
            String judul = l != null ? l.judul : "Unknown";
            String divisi = l != null ? l.divisi : "Unknown";

            boolean matchDivisi = (selectedDivisi == null || selectedDivisi.equals("Semua Lowongan")
                    || divisi.equals(selectedDivisi));
            boolean matchStatus = (selectedStatus == null || selectedStatus.equals("Semua")
                    || lamaran.statusTahapan.equals(selectedStatus));

            if (matchDivisi && matchStatus) {

                String tanggalInterview = (lamaran.tanggalInterview != null && !lamaran.tanggalInterview.isEmpty())
                        ? lamaran.tanggalInterview
                        : "-";

                tableContainer.getChildren().add(createTableRow(
                        String.valueOf(no++), judul, divisi, lamaran.tanggal,
                        tanggalInterview, lamaran.statusTahapan, "↗", false, lamaran));
            }
        }

        if (no == 1) {
            Label empty = new Label("Anda belum melamar pekerjaan apapun.");
            empty.setPadding(new Insets(20));
            empty.setTextFill(Color.web("#718096"));
            tableContainer.getChildren().add(empty);
        }
    }

    private HBox createHeader() {
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("Daftar Lowongan");
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

    private HBox createFilterBar() {
        HBox bar = new HBox(15);
        bar.setAlignment(Pos.CENTER_LEFT);

        divisiFilter = new ComboBox<>();
        divisiFilter.getItems().addAll("Semua Lowongan", "Marketing", "Teknologi Informasi", "Human Resource",
                "Keuangan", "Operasional");
        divisiFilter.setValue("Semua Lowongan");
        divisiFilter.setStyle("-fx-background-color: #F7FAFC; -fx-background-radius: 10; -fx-padding: 8;");
        divisiFilter.setPrefWidth(200);
        divisiFilter.setOnAction(e -> refreshTable());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox rightBox = new HBox(10);
        rightBox.setAlignment(Pos.CENTER_RIGHT);

        TextField search = new TextField();
        search.setPromptText("🔍 Cari lowongan");
        search.setStyle("-fx-background-color: #F7FAFC; -fx-background-radius: 10; -fx-padding: 8;");
        search.setPrefWidth(180);
        search.textProperty().addListener((obs, oldVal, newVal) -> refreshTable());

        statusFilter = new ComboBox<>();
        statusFilter.getItems().addAll("Semua", "Seleksi Berkas", "Interview", "Diterima", "Ditolak");
        statusFilter.setValue("Semua");
        statusFilter.setStyle("-fx-background-color: #F7FAFC; -fx-background-radius: 10; -fx-padding: 5;");
        statusFilter.setPrefWidth(150);
        statusFilter.setOnAction(e -> refreshTable());

        rightBox.getChildren().addAll(search, statusFilter);
        bar.getChildren().addAll(divisiFilter, spacer, rightBox);
        return bar;
    }

    private HBox createTableRow(String no, String name, String div, String date,
            String tanggalInterview, String status, String prog, boolean isHeader, Lamaran lamaran) {
        HBox row = new HBox();
        row.setPadding(new Insets(15, 10, 15, 10));
        row.setAlignment(Pos.CENTER_LEFT);

        if (isHeader) {
            row.setStyle("-fx-background-color: #F7FAFC; -fx-border-color: #E2E8F0; -fx-border-width: 0 0 1 0;");
        } else {
            row.setStyle("-fx-border-color: #E2E8F0; -fx-border-width: 0 0 1 0;");
        }

        Label lblNo = createCellLabel(no, 40, isHeader);
        Label lblName = createCellLabel(name, 200, isHeader);
        Label lblDiv = createCellLabel(div, 150, isHeader);
        Label lblDate = createCellLabel(date, 150, isHeader);

        Label lblInterviewDate = createCellLabel(tanggalInterview, 150, isHeader);
        if (!isHeader && !tanggalInterview.equals("-")) {
            lblInterviewDate.setTextFill(Color.web("#2B6CB0")); // Biru untuk tanggal yang sudah dijadwalkan
            lblInterviewDate.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        }

        Node statusNode;
        if (isHeader) {
            statusNode = createCellLabel(status, 120, true);
        } else {
            Label pill = new Label(status);
            String bgColor = switch (status) {
                case "Diterima" -> "#C6F6D5";
                case "Ditolak" -> "#FED7D7";
                case "Interview" -> "#BEE3F8";
                default -> "#EDF2F7";
            };
            String textColor = switch (status) {
                case "Diterima" -> "#22543D";
                case "Ditolak" -> "#9B2C2C";
                case "Interview" -> "#2B6CB0";
                default -> "#4A5568";
            };
            pill.setStyle("-fx-background-color: " + bgColor + "; -fx-text-fill: " + textColor
                    + "; -fx-background-radius: 20; -fx-padding: 5 15; -fx-font-size: 11px; -fx-font-weight: bold;");
            StackPane cell = new StackPane(pill);
            cell.setPrefWidth(120);
            cell.setAlignment(Pos.CENTER_LEFT);
            statusNode = cell;
        }

        Node progNode;
        if (isHeader) {
            progNode = createCellLabel(prog, 80, true);
        } else {
            Button progBtn = new Button(prog);
            progBtn.setStyle(
                    "-fx-background-color: #BEE3F8; -fx-text-fill: #2B6CB0; -fx-background-radius: 8; -fx-font-weight: bold; -fx-cursor: hand;");
            progBtn.setPrefSize(35, 30);
            progBtn.setOnAction(e -> {
                Lowongan l = DatabaseManager.getLowonganById(lamaran.idLowongan);
                showProgressModal(l != null ? l.judul : "Unknown", lamaran.statusTahapan, lamaran.tanggalInterview);
            });

            HBox cell = new HBox(10, progBtn);
            cell.setPrefWidth(80);
            cell.setAlignment(Pos.CENTER_LEFT);
            progNode = cell;
        }

        row.getChildren().addAll(lblNo, lblName, lblDiv, lblDate, lblInterviewDate, statusNode, progNode);
        return row;
    }

    private Label createCellLabel(String text, double width, boolean isHeader) {
        Label lbl = new Label(text);
        lbl.setPrefWidth(width);
        if (isHeader) {
            lbl.setFont(Font.font("Arial", FontWeight.BOLD, 13));
            lbl.setTextFill(Color.web("#4A5568"));
        } else {
            lbl.setFont(Font.font("Arial", 13));
            lbl.setTextFill(Color.web("#2D3748"));
        }
        return lbl;
    }

    private void showProgressModal(String jobTitle, String statusTahapan, String tanggalInterview) {
        VBox modal = new VBox(25);
        modal.setMaxSize(450, 500);
        modal.setPadding(new Insets(30));
        modal.setStyle("-fx-background-color: white; -fx-background-radius: 20;");

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label(jobTitle);
        title.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        title.setTextFill(Color.web("#1A202C"));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button closeBtn = new Button("X");
        closeBtn.setStyle("-fx-background-color: transparent; -fx-font-weight: bold; -fx-cursor: hand;");
        closeBtn.setOnAction(e -> hideModal());

        header.getChildren().addAll(title, spacer, closeBtn);

        VBox infoBox = new VBox(15);
        if (tanggalInterview != null && !tanggalInterview.isEmpty()) {
            HBox interviewInfo = new HBox(10);
            interviewInfo.setAlignment(Pos.CENTER_LEFT);
            interviewInfo.setPadding(new Insets(15));
            interviewInfo.setStyle("-fx-background-color: #EBF8FF; -fx-background-radius: 10;");

            Label icon = new Label("📅");
            icon.setFont(Font.font(20));

            VBox textBox = new VBox(5);
            Label labelTitle = new Label("Jadwal Interview");
            labelTitle.setFont(Font.font("Arial", FontWeight.BOLD, 13));
            labelTitle.setTextFill(Color.web("#2B6CB0"));

            Label labelDate = new Label(tanggalInterview);
            labelDate.setFont(Font.font("Arial", FontWeight.BOLD, 16));
            labelDate.setTextFill(Color.web("#1A202C"));

            textBox.getChildren().addAll(labelTitle, labelDate);
            interviewInfo.getChildren().addAll(icon, textBox);
            infoBox.getChildren().add(interviewInfo);
        }

        VBox timeline = new VBox(15);
        boolean isBerkas = statusTahapan.equals("Seleksi Berkas");
        boolean isInterview = statusTahapan.equals("Interview");
        boolean isFinal = statusTahapan.equals("Diterima") || statusTahapan.equals("Ditolak");

        timeline.getChildren().addAll(
                createTimelineStep("1", "Pengumpulan Berkas", true),
                createTimelineStep("2", "Interview", isInterview || isFinal),
                createTimelineStep("3", "Hasil Akhir: " + (isFinal ? statusTahapan : "Pending"), isFinal));

        modal.getChildren().addAll(header, infoBox, timeline);

        modalOverlay.getChildren().clear();
        modalOverlay.getChildren().add(modal);
        modalOverlay.setVisible(true);
        mainContent.setDisable(true);
    }

    private void hideModal() {
        modalOverlay.setVisible(false);
        mainContent.setDisable(false);
    }

    private HBox createTimelineStep(String num, String text, boolean isActive) {
        HBox box = new HBox(15);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPadding(new Insets(12, 20, 12, 20));

        if (isActive) {
            box.setStyle("-fx-background-color: #C6F6D5; -fx-background-radius: 10;");
        } else {
            box.setStyle("-fx-background-color: #EDF2F7; -fx-background-radius: 10;");
        }

        Label lNum = new Label(num);
        lNum.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        lNum.setTextFill(isActive ? Color.web("#22543D") : Color.web("#4A5568"));

        Label lText = new Label(text);
        lText.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        lText.setTextFill(isActive ? Color.web("#22543D") : Color.web("#4A5568"));

        box.getChildren().addAll(lNum, lText);
        return box;
    }
}