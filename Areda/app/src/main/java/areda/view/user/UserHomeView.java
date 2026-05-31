package areda.view.user;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class UserHomeView extends VBox {

    public UserHomeView(String username) {
        this.setSpacing(40);
        this.setPadding(new Insets(40));
        this.setStyle("-fx-background-color: white;");

        HBox header = createHeader(username);
        VBox timeline = createTimeline();
        this.getChildren().addAll(header, timeline);
    }

    private HBox createHeader(String username) {
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(5);
        Label welcomeLabel = new Label("Halo, " + username + "!");
        welcomeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        welcomeLabel.setTextFill(Color.BLACK);

        Label subLabel = new Label("Berikut adalah tahapan pendaftaran kerja di Areda Careers!!");
        subLabel.setFont(Font.font("Arial", 14));
        subLabel.setTextFill(Color.web("#718096"));
        titleBox.getChildren().addAll(welcomeLabel, subLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox iconBox = new HBox(20);
        iconBox.setAlignment(Pos.CENTER);
        Label bellIcon = new Label("🔔");
        bellIcon.setFont(Font.font(20));
        Circle avatar = new Circle(20, Color.web("#CBD5E0"));
        avatar.setStroke(Color.web("#A0AEC0"));
        iconBox.getChildren().addAll(bellIcon, avatar);

        header.getChildren().addAll(titleBox, spacer, iconBox);
        return header;
    }

    private VBox createTimeline() {
        VBox timeline = new VBox(30);
        timeline.setAlignment(Pos.TOP_LEFT);
        timeline.getChildren().addAll(
                createTimelineRow("Tahap 1", "Pendaftaran",
                        "Pilih lowongan yang sesuai, lalu lakukan pendaftaran melalui sistem untuk memulai proses lamaran",
                        "📝"),
                createTimelineRow("Tahap 2", "Seleksi Berkas",
                        "Tim HR akan meninjau dokumen lamaran Anda untuk memastikan kesesuaian dengan kualifikasi yang dibutuhkan.",
                        "🔍"),
                createTimelineRow("Tahap 3", "Interview",
                        "Jika berkas Anda lolos seleksi, tim kami akan menghubungi Anda untuk mengikuti proses wawancara sesuai jadwal yang ditentukan.",
                        "🎤"),
                createTimelineRow("Tahap 4", "Pengumuman",
                        "Setelah seluruh proses seleksi selesai, hasil akhir lamaran akan diumumkan melalui sistem sebagai diterima atau ditolak.",
                        "📢"));
        return timeline;
    }

    private HBox createTimelineRow(String tag, String title, String desc, String iconChar) {
        HBox row = new HBox(25);
        row.setAlignment(Pos.CENTER_LEFT);
        Label tagLabel = new Label(tag);
        tagLabel.setMinWidth(70);
        tagLabel.setAlignment(Pos.CENTER);
        tagLabel.setPadding(new Insets(8, 12, 8, 12));
        tagLabel.setStyle(
                "-fx-background-color: #BEE3F8; -fx-background-radius: 10; -fx-text-fill: #2B6CB0; -fx-font-weight: bold; -fx-font-size: 12px;");
        StackPane iconCircle = new StackPane();
        Circle circle = new Circle(22, Color.web("#BEE3F8"));
        Label icon = new Label(iconChar);
        icon.setFont(Font.font(18));
        icon.setTextFill(Color.web("#2B6CB0"));
        iconCircle.getChildren().addAll(circle, icon);
        VBox textBox = new VBox(5);
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        Label descLabel = new Label(desc);
        descLabel.setFont(Font.font("Arial", 13));
        descLabel.setTextFill(Color.web("#4A5568"));
        descLabel.setWrapText(true);
        descLabel.setMaxWidth(600);
        textBox.getChildren().addAll(titleLabel, descLabel);
        row.getChildren().addAll(tagLabel, iconCircle, textBox);
        return row;
    }
}