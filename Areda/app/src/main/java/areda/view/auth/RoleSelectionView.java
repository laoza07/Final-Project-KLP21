package areda.view.auth;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

public class RoleSelectionView extends StackPane {

    public RoleSelectionView(Runnable onUserLogin, Runnable onAdminLogin) {

        this.setStyle("-fx-background-color: white;");

        VBox mainContainer = new VBox();
        mainContainer.setAlignment(Pos.CENTER);
        mainContainer.setPadding(new Insets(40));

        VBox card = new VBox(30);
        card.setAlignment(Pos.CENTER);
        card.setMaxSize(500, 600);
        card.setPadding(new Insets(40));
        card.setStyle(
                "-fx-background-color: #BEE3F8; -fx-background-radius: 30; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5);");

        Label welcome = new Label("Selamat Datang!");
        welcome.setFont(Font.font("Arial", FontWeight.NORMAL, 18));
        welcome.setTextFill(Color.web("#2D3748"));

        StackPane logo = new StackPane();
        Label letterA = new Label("A");
        letterA.setFont(Font.font("Arial", FontWeight.BOLD, 80));
        letterA.setTextFill(Color.web("#2D3748"));

        Circle dot = new Circle(8, Color.web("#A0522D"));
        StackPane.setAlignment(dot, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(dot, new Insets(0, 5, 15, 0));
        logo.getChildren().addAll(letterA, dot);
        logo.setMaxWidth(100);

        Label title = new Label("AREDA CAREERS");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        title.setTextFill(Color.web("#2D3748"));
        title.setWrapText(true);
        title.setMaxWidth(400);

        title.setAlignment(Pos.CENTER);

        Label tagline = new Label("Temukan peluang terbaik dan bangun karier impianmu bersama Areda Careers");
        tagline.setFont(Font.font("Arial", FontWeight.NORMAL, 13));
        tagline.setTextFill(Color.web("#4A5568"));
        tagline.setWrapText(true);
        tagline.setTextAlignment(TextAlignment.CENTER);
        tagline.setMaxWidth(350);

        VBox buttonBox = new VBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));

        Button userLoginBtn = createStyledButton("LOGIN USER");
        Button adminLoginBtn = createStyledButton("LOGIN ADMIN");

        userLoginBtn.setOnAction(e -> onUserLogin.run());
        adminLoginBtn.setOnAction(e -> onAdminLogin.run());

        buttonBox.getChildren().addAll(userLoginBtn, adminLoginBtn);

        card.getChildren().addAll(welcome, logo, title, tagline, buttonBox);

        mainContainer.getChildren().add(card);

        this.getChildren().add(mainContainer);
    }

    private Button createStyledButton(String text) {
        Button btn = new Button(text);
        String normalStyle = "-fx-background-color: #0066CC; " +
                "-fx-text-fill: white; " +
                "-fx-font-weight: bold; " +
                "-fx-font-size: 14px; " +
                "-fx-background-radius: 20; " +
                "-fx-min-width: 280; " +
                "-fx-padding: 12 0; " +
                "-fx-cursor: hand;";

        String hoverStyle = "-fx-background-color: #004C99; " +
                "-fx-text-fill: white; " +
                "-fx-font-weight: bold; " +
                "-fx-font-size: 14px; " +
                "-fx-background-radius: 20; " +
                "-fx-min-width: 280; " +
                "-fx-padding: 12 0; " +
                "-fx-cursor: hand;";

        btn.setStyle(normalStyle);
        btn.setOnMouseEntered(e -> btn.setStyle(hoverStyle));
        btn.setOnMouseExited(e -> btn.setStyle(normalStyle));
        return btn;
    }
}