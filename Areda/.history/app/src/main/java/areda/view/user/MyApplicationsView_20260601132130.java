package areda.view.user;

import areda.model.DatabaseManager;
import areda.model.Lamaran;
import areda.model.Lowongan;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.util.List;
import java.util.stream.Collectors;

public class MyApplicationsView extends StackPane {
    private VBox mainContent;
    private StackPane modalOverlay;
    private TableView<Lamaran> tableView;
    private ComboBox<String> divisiFilter;
    private ComboBox<String> statusFilter;
    private TextField searchField;

    public MyApplicationsView() {
        this.setStyle("-fx-background-color: white;");
        mainContent = new VBox(25);
        mainContent.setPadding(new Insets(30));
        mainContent.setAlignment(Pos.TOP_LEFT);

        HBox header = createHeader();
        HBox filterBar = createFilterBar();

        tableView = new TableView<>();
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView.setStyle(
                "-fx-background-color: transparent; -fx-border-color: #E2E8F0; -fx-border-radius: 10; -fx-background-radius: 10;");

        setupTableColumns();
        refreshTable();

        VBox.setVgrow(tableView, Priority.ALWAYS);
        mainContent.getChildren().addAll(header, filterBar, tableView);

        modalOverlay = new StackPane();
        modalOverlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.4);");
        modalOverlay.setVisible(false);
        this.getChildren().addAll(mainContent, modalOverlay);
    }

    private void setupTableColumns() {
        TableColumn<Lamaran, String> colNo = new TableColumn<>("No.");
        colNo.setPrefWidth(50);
        colNo.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : String.valueOf(getIndex() + 1));
                setAlignment(Pos.CENTER);
            }
        });

        TableColumn<Lamaran, String> colJob = new TableColumn<>("Nama Lowongan");
        colJob.setCellValueFactory(data -> {
            Lowongan l = DatabaseManager.getLowonganById(data.getValue().idLowongan);
            return new SimpleStringProperty(l != null ? l.judul : "Unknown");
        });

        TableColumn<Lamaran, String> colDiv = new TableColumn<>("Divisi");
        colDiv.setCellValueFactory(data -> {
            Lowongan l = DatabaseManager.getLowonganById(data.getValue().idLowongan);
            return new SimpleStringProperty(l != null ? l.divisi : "Unknown");
        });

        TableColumn<Lamaran, String> colDate = new TableColumn<>("Tanggal Melamar");
        colDate.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().tanggal));

        TableColumn<Lamaran, String> colIntDate = new TableColumn<>("Tanggal Interview");
        colIntDate.setCellValueFactory(data -> {
            String t = data.getValue().tanggalInterview;
            return new SimpleStringProperty((t != null && !t.isEmpty()) ? t : "-");
        });
        colIntDate.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || item.equals("-")) {
                    setText("-");
                    setTextFill(Color.web("#4A5568"));
                    setFont(Font.font("Arial", 13));
                } else {
                    setText(item);
                    setTextFill(Color.web("#2B6CB0"));
                    setFont(Font.font("Arial", FontWeight.BOLD, 13));
                }
                setAlignment(Pos.CENTER_LEFT);
            }
        });

        TableColumn<Lamaran, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().statusTahapan));
        colStatus.setCellFactory(col -> new TableCell<>() {
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
                    String bgColor = switch (item) {
                        case "Diterima" -> "#0fb744";
                        case "Ditolak" -> "#890909";
                        case "Interview" -> "#24149b";
                        default -> "#000000";
                    };
                    String textColor = switch (item) {
                        case "Diterima" -> "#22543D";
                        case "Ditolak" -> "#9B2C2C";
                        case "Interview" -> "#2B6CB0";
                        default -> "#000000";
                    };
                    pill.setStyle("-fx-background-color: " + bgColor + "; -fx-text-fill: " + textColor
                            + "; -fx-background-radius: 20; -fx-padding: 5 15; -fx-font-size: 11px; -fx-font-weight: bold;");
                    setGraphic(pill);
                }
            }
        });

        TableColumn<Lamaran, Void> colProgress = new TableColumn<>("Progress");
        colProgress.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("↗");
            {
                btn.setStyle(
                        "-fx-background-color: #BEE3F8; -fx-text-fill: #2B6CB0; -fx-background-radius: 8; -fx-font-weight: bold; -fx-cursor: hand;");
                btn.setPrefSize(35, 30);
                btn.setOnAction(e -> {
                    Lamaran lamaran = getTableView().getItems().get(getIndex());
                    Lowongan l = DatabaseManager.getLowonganById(lamaran.idLowongan);
                    showProgressModal(l != null ? l.judul : "Unknown", lamaran.statusTahapan, lamaran.tanggalInterview);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
                setAlignment(Pos.CENTER);
            }
        });

        tableView.getColumns().addAll(colNo, colJob, colDiv, colDate, colIntDate, colStatus, colProgress);
    }

    private void refreshTable() {
        String selectedDivisi = divisiFilter.getValue();
        String selectedStatus = statusFilter.getValue();
        String searchText = searchField.getText().toLowerCase();

        List<Lamaran> filtered = DatabaseManager.getMyLamaran().stream().filter(lamaran -> {
            Lowongan l = DatabaseManager.getLowonganById(lamaran.idLowongan);
            String divisi = l != null ? l.divisi : "Unknown";
            String judul = l != null ? l.judul : "Unknown";

            boolean matchDivisi = (selectedDivisi == null || selectedDivisi.equals("Semua Lowongan")
                    || divisi.equals(selectedDivisi));
            boolean matchStatus = (selectedStatus == null || selectedStatus.equals("Semua")
                    || lamaran.statusTahapan.equals(selectedStatus));
            boolean matchSearch = searchText.isEmpty() || judul.toLowerCase().contains(searchText);

            return matchDivisi && matchStatus && matchSearch;
        }).collect(Collectors.toList());

        tableView.getItems().setAll(filtered);
        if (filtered.isEmpty())
            tableView.setPlaceholder(new Label("Anda belum melamar pekerjaan apapun atau filter tidak ditemukan."));
    }

    private HBox createHeader() {
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("Daftar Lamaran Saya");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        title.setTextFill(Color.web("#1A202C"));
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        header.getChildren().addAll(title, spacer);
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

        searchField = new TextField();
        searchField.setPromptText("🔍 Cari lowongan");
        searchField.setStyle("-fx-background-color: #F7FAFC; -fx-background-radius: 10; -fx-padding: 8;");
        searchField.setPrefWidth(180);
        searchField.textProperty().addListener((obs, oldVal, newVal) -> refreshTable());

        statusFilter = new ComboBox<>();
        statusFilter.getItems().addAll("Semua", "Seleksi Berkas", "Interview", "Diterima", "Ditolak");
        statusFilter.setValue("Semua");
        statusFilter.setStyle("-fx-background-color: #F7FAFC; -fx-background-radius: 10; -fx-padding: 5;");
        statusFilter.setPrefWidth(150);
        statusFilter.setOnAction(e -> refreshTable());

        rightBox.getChildren().addAll(searchField, statusFilter);
        bar.getChildren().addAll(divisiFilter, spacer, rightBox);
        return bar;
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
        title.setTextFill(Color.web("#000000"));
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
            labelDate.setTextFill(Color.web("#000000"));
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
        box.setStyle(isActive ? "-fx-background-color: #C6F6D5; -fx-background-radius: 10;"
                : "-fx-background-color: #EDF2F7; -fx-background-radius: 10;");
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