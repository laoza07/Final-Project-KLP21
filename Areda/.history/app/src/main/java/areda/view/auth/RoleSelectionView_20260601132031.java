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
        this.setStyle("-fx-background-color: #C9E9FF;");

        VBox mainContainer = new VBox();
        mainContainer.setAlignment(Pos.CENTER);
        mainContainer.setPadding(new Insets(40));

        VBox card = new VBox(35);
        card.setAlignment(Pos.CENTER);
        card.setMaxWidth(800);
        card.setPadding(new Insets(50, 60, 50, 60));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 25; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.08), 20, 0, 0, 5);");

        Label welcome = new Label("Selamat Datang!");
        welcome.setFont(Font.font("Amaranth", FontWeight.NORMAL, 40));
        welcome.setTextFill(Color.web("#1A202C"));

        StackPane logoIcon = new StackPane();
        logoIcon.setPrefSize(215, 223);
        logoIcon.setAlignment(Pos.CENTER);
        try {
            Image image = new Image(getClass().getResourceAsStream("/logo.png"));
            ImageView logoView = new ImageView(image);
            logoView.setFitWidth(180);
            logoView.setPreserveRatio(true);
            logoIcon.getChildren().add(logoView);
        } catch (Exception e) {
            Label fallbackLogo = new Label("A");
            fallbackLogo.setStyle(
                    "-fx-font-size: 160px; -fx-font-weight: 900; -fx-text-fill: #0048FF; -fx-font-family: 'Arial';");
            logoIcon.getChildren().add(fallbackLogo);
        }

        Label titleLabel = new Label("Areda Careers");
        titleLabel.setFont(Font.font("Konkhmer Sleokchher", FontWeight.BOLD, 64.4));
        titleLabel.setAlignment(Pos.CENTER);
        titleLabel.setTextFill(Color.web("#1A202C"));

        Label tagline = new Label("Temukan Peluang Terbaik dan Bangun Karier Impianmu Bersama Areda Careers");
        tagline.setFont(Font.font("Andika New Basic", FontWeight.NORMAL, 25));
        tagline.setWrapText(true);
        tagline.setTextAlignment(TextAlignment.CENTER);
        tagline.setMaxWidth(800);
        tagline.setTextFill(Color.web("#4A5568"));

        VBox buttonBox = new VBox(20);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(20, 0, 0, 0));

        Button userLoginBtn = createStyledButton("LOGIN USER");
        Button adminLoginBtn = createStyledButton("LOGIN ADMIN");

        userLoginBtn.setOnAction(e -> onUserLogin.run());
        adminLoginBtn.setOnAction(e -> onAdminLogin.run());

        buttonBox.getChildren().addAll(userLoginBtn, adminLoginBtn);
        card.getChildren().addAll(welcome, logoIcon, titleLabel, tagline, buttonBox);
        mainContainer.getChildren().add(card);
        this.getChildren().add(mainContainer);
    }

    private Button createStyledButton(String text) {
        Button btn = new Button(text);
        btn.getStyleClass().add("btn-auth-primary");
        return btn;
    }
}