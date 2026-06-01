package areda.view.auth;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

public class RoleSelectionView extends StackPane {

    public RoleSelectionView(Runnable onUserLogin, Runnable onAdminLogin) {

        // 1 & 2. Pengaturan Background Full Biru (Warna: #C9E9FF)
        this.setStyle("-fx-background-color: #C9E9FF;");
        this.setPrefSize(1440, 1024);

        VBox mainContainer = new VBox();
        mainContainer.setAlignment(Pos.CENTER);
        mainContainer.setPadding(new Insets(40));

        // Card background disamakan dengan background utama (full biru)
        VBox card = new VBox(35);
        card.setAlignment(Pos.CENTER);
        card.setPrefSize(1440, 1024);
        card.setMaxSize(1440, 1024);
        card.setStyle("-fx-background-color: #C9E9FF; -fx-background-radius: 25;");

        // 3. Tulisan Selamat Datang
        Label welcome = new Label("Selamat Datang!");
        welcome.setFont(Font.font("Amaranth", FontWeight.NORMAL, 40));
        welcome.setTextFill(Color.web("#000000"));

        // 4. Logo Layout menggunakan ImageView
        StackPane logoIcon = new StackPane();
        logoIcon.setPrefSize(215, 223);
        logoIcon.setMaxSize(215, 223);
        logoIcon.setAlignment(Pos.CENTER);

        try {
            Image image = new Image(getClass().getResourceAsStream("/logo.png"));
            ImageView logoView = new ImageView(image);
            logoView.setFitWidth(180);
            logoView.setPreserveRatio(true);
            logoIcon.getChildren().add(logoView);
        } catch (Exception e) {
            // CADANGAN: Jika gambar gagal dimuat, tampilkan "A" warna biru (#0048FF)
            Label fallbackLogo = new Label("A");
            fallbackLogo.setStyle(
                    "-fx-font-size: 160px; -fx-font-weight: 900; -fx-text-fill: #0048FF; -fx-font-family: 'Arial';");
            logoIcon.getChildren().add(fallbackLogo);
        }

        // 5. Tulisan "Areda Careers"
        Label titleLabel = new Label("Areda Careers");
        titleLabel.setFont(Font.font("Konkhmer Sleokchher", FontWeight.BOLD, 64.4));
        titleLabel.setTextFill(Color.web("#000000"));
        titleLabel.setAlignment(Pos.CENTER);

        // 6. Tagline
        Label tagline = new Label("Temukan Peluang Terbaik dan Bangun Karier Impianmu Bersama Areda Careers");
        tagline.setFont(Font.font("Andika New Basic", FontWeight.NORMAL, 25));
        tagline.setTextFill(Color.web("#000000"));
        tagline.setWrapText(true);
        tagline.setTextAlignment(TextAlignment.CENTER);
        tagline.setMaxWidth(950);

        // 7. Container Tombol
        VBox buttonBox = new VBox(20);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(20, 0, 0, 0));

        Button userLoginBtn = createStyledButton("LOGIN USER");
        Button adminLoginBtn = createStyledButton("LOGIN ADMIN");

        userLoginBtn.setOnAction(e -> onUserLogin.run());
        adminLoginBtn.setOnAction(e -> onAdminLogin.run());

        buttonBox.getChildren().addAll(userLoginBtn, adminLoginBtn);

        // Memasukkan ke dalam kartu background
        card.getChildren().addAll(welcome, logoIcon, titleLabel, tagline, buttonBox);

        mainContainer.getChildren().add(card);
        this.getChildren().add(mainContainer);
    }

    private Button createStyledButton(String text) {
        Button btn = new Button(text);

        String normalStyle = "-fx-background-color: #0048FF; " +
                "-fx-text-fill: #FFFFFF; " +
                "-fx-font-weight: bold; " +
                "-fx-font-size: 16px; " +
                "-fx-background-radius: 25; " +
                "-fx-min-width: 320; " +
                "-fx-padding: 14 0; " +
                "-fx-cursor: hand;";

        String hoverStyle = "-fx-background-color: #0035D0; " +
                "-fx-text-fill: #FFFFFF; " +
                "-fx-font-weight: bold; " +
                "-fx-font-size: 16px; " +
                "-fx-background-radius: 25; " +
                "-fx-min-width: 320; " +
                "-fx-padding: 14 0; " +
                "-fx-cursor: hand;";

        btn.setStyle(normalStyle);
        btn.setOnMouseEntered(e -> btn.setStyle(hoverStyle));
        btn.setOnMouseExited(e -> btn.setStyle(normalStyle));
        return btn;
    }
}