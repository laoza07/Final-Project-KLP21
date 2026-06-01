package areda.view.admin;

import areda.model.DatabaseManager;
import areda.model.Lamaran;
import areda.model.Lowongan;
import areda.view.components.AvatarComponent;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class ApplicantListView extends StackPane {
    private VBox mainContent;
    private StackPane modalOverlay;
    private TableView<Lamaran> tableView;
    private ComboBox<String> lowonganFilter;
    private TextField searchField;

    public ApplicantListView() {
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

        tableView.getColumns().addAll(colNo, colName, colDiv, colDate, colStatus, colAction);
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

        // 🔥 FIX: DatePicker dengan StringConverter untuk hindari
        // DateTimeParseException
        DatePicker interviewDatePicker = new DatePicker();
        interviewDatePicker.getStyleClass().add("form-input");
        interviewDatePicker.setMaxWidth(Double.MAX_VALUE);

        // Set value dengan safe parsing
        if (app.getTanggalInterview() != null && !app.getTanggalInterview().isEmpty()) {
            try {
                interviewDatePicker.setValue(LocalDate.parse(app.getTanggalInterview()));
            } catch (Exception ignored) {
                interviewDatePicker.setValue(null);
            }
        }

        // 🔥 FIX: StringConverter untuk handle input invalid dari user
        interviewDatePicker.setConverter(new StringConverter<LocalDate>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            @Override
            public String toString(LocalDate date) {
                return (date != null) ? formatter.format(date) : "";
            }

            @Override
            public LocalDate fromString(String string) {
                if (string == null || string.isEmpty())
                    return null;
                try {
                    return LocalDate.parse(string, formatter);
                } catch (Exception e) {
                    // ✅ Return null jika format invalid, jangan throw exception
                    return null;
                }
            }
        });

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

        Label cvLabel = new Label(
                (app.getCvPath() == null || app.getCvPath().isEmpty()) ? "❌ Belum ada CV" : "📄    " + app.getCvPath());
        cvLabel.setWrapText(true);
        cvLabel.setMaxWidth(200);
        cvLabel.setTextFill(Color.web("#2D3748"));

        Label mlLabel = new Label(
                (app.getMotletPath() == null || app.getMotletPath().isEmpty()) ? "❌ Belum ada ML"
                        : "📄    " + app.getMotletPath());
        mlLabel.setWrapText(true);
        mlLabel.setMaxWidth(200);
        mlLabel.setTextFill(Color.web("#2D3748"));

        docs.getChildren().addAll(docsTitle, cvLabel, mlLabel);

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
}