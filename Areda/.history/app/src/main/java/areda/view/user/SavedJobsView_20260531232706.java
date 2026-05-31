package areda.view.user;

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
import java.util.List;

public class SavedJobsView extends VBox {

    private UserDashboardLayout layout;
    private TextField searchField;
    private ComboBox<String> filterCombo;
    private VBox jobListContainer;

    public SavedJobsView(UserDashboardLayout layout) {
        this.layout = layout;
        this.setSpacing(25);
        this.setPadding(new Insets(30));
        this.setStyle("-fx-background-color: white;");
        HBox header = createHeader();
        HBox controlBar = createControlBar();
        jobListContainer = new VBox(15);
        refreshJobList("", "Semua");
        ScrollPane scroll = new ScrollPane(jobListContainer);
        scroll.setFitToWidth(true);
        scroll.setStyle(
                "-fx-background-color: transparent; -fx-background: transparent; -fx-border-color: transparent;");
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        this.getChildren().addAll(header, controlBar, scroll);
    }

    private HBox createControlBar() {
        HBox bar = new HBox(15);
        bar.setAlignment(Pos.CENTER_RIGHT);
        searchField = new TextField();
        searchField.setPromptText(" Cari lowongan...");
        searchField.setStyle("-fx-background-color: #F7FAFC; -fx-background-radius: 10; -fx-padding: 8;");
        searchField.setPrefWidth(220);
        searchField.textProperty().addListener((obs, oldVal, newVal) -> refreshJobList(newVal, filterCombo.getValue()));
        filterCombo = new ComboBox<>();
        filterCombo.getItems().addAll(DatabaseManager.getAllDivisi());
        filterCombo.setValue("Semua");
        filterCombo.setStyle("-fx-background-color: #F7FAFC; -fx-background-radius: 10; -fx-padding: 5;");
        filterCombo.setOnAction(e -> refreshJobList(searchField.getText(), filterCombo.getValue()));
        bar.getChildren().addAll(searchField, filterCombo);
        return bar;
    }

    private void refreshJobList(String searchText, String selectedDivisi) {
        jobListContainer.getChildren().clear();
        List<Lowongan> allBookmarked = DatabaseManager.getBookmarkedLowongan();
        List<Lowongan> filteredJobs = allBookmarked.stream()
                .filter(job -> {
                    boolean matchText = searchText.isEmpty()
                            || job.judul.toLowerCase().contains(searchText.toLowerCase())
                            || job.deskripsi.toLowerCase().contains(searchText.toLowerCase());
                    boolean matchDivisi = selectedDivisi.equals("Semua") || job.divisi.equals(selectedDivisi);
                    return matchText && matchDivisi;
                }).toList();
        if (filteredJobs.isEmpty()) {
            boolean isSearching = !searchText.isEmpty() || !selectedDivisi.equals("Semua");
            jobListContainer.getChildren().add(createEmptyState(isSearching));
        } else {
            for (Lowongan job : filteredJobs)
                jobListContainer.getChildren().add(createJobCard(job));
        }
    }

    private HBox createHeader() {
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("Markah");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox iconBox = new HBox(15);
        iconBox.setAlignment(Pos.CENTER);
        iconBox.getChildren().addAll(new Label("🔔"), new Circle(15, Color.LIGHTGRAY));
        header.getChildren().addAll(title, spacer, iconBox);
        return header;
    }

    private HBox createJobCard(Lowongan job) {
        HBox card = new HBox(15);
        card.setPadding(new Insets(20));
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle("-fx-background-color: #BEE3F8; -fx-background-radius: 15; -fx-cursor: hand;");
        VBox left = new VBox(5);
        Label title = new Label(job.judul);
        title.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        Label cat = new Label("💼 " + job.divisi);
        cat.setFont(Font.font(12));
        cat.setTextFill(Color.web("#4A5568"));
        left.getChildren().addAll(title, cat);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Button bookmarkIcon = new Button("🔖");
        bookmarkIcon.setStyle(
                "-fx-background-color: transparent; -fx-text-fill: #2B6CB0; -fx-font-size: 18px; -fx-cursor: hand;");
        bookmarkIcon.setOnAction(e -> {
            DatabaseManager.toggleBookmark(job.id);
            refreshJobList(searchField.getText(), filterCombo.getValue());
        });
        card.getChildren().addAll(left, spacer, bookmarkIcon);
        return card;
    }

    private VBox createEmptyState(boolean isFiltered) {
        VBox emptyBox = new VBox(20);
        emptyBox.setAlignment(Pos.CENTER);
        emptyBox.setPadding(new Insets(40, 20, 40, 20));
        Label icon = new Label(isFiltered ? "🔍" : "🔖");
        icon.setFont(Font.font(48));
        Label title = new Label(isFiltered ? "Tidak Ditemukan" : "Belum Ada Lowongan Tersimpan");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        title.setTextFill(isFiltered ? Color.web("#E53E3E") : Color.web("#3182CE"));
        Text desc = new Text(isFiltered ? "Coba ubah kata kunci pencarian atau filter divisi Anda."
                : "Simpan lowongan yang menarik dengan klik ikon markah (🔖) pada daftar lowongan.");
        desc.setWrappingWidth(400);
        desc.setFont(Font.font("Arial", 14));
        desc.setFill(Color.web("#718096"));
        desc.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        if (!isFiltered) {
            Button backBtn = new Button("Cari Lowongan");
            backBtn.setStyle(
                    "-fx-background-color: #EBF8FF; -fx-text-fill: #3182CE; -fx-font-weight: bold; -fx-padding: 12 25; -fx-background-radius: 30; -fx-cursor: hand;");
            backBtn.setOnAction(e -> layout.navigateTo("Lowongan"));
            emptyBox.getChildren().addAll(icon, title, desc, backBtn);
        } else {
            emptyBox.getChildren().addAll(icon, title, desc);
        }
        return emptyBox;
    }
}