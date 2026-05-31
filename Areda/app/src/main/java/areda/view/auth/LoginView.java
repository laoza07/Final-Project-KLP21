package areda.view.auth;

import areda.model.DatabaseManager;
import areda.model.DatabaseManager.Profil;
import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import java.util.function.Consumer;

public class LoginView extends HBox {

    private StackPane rightPane;
    private StackPane loadingOverlay;
    private Consumer<String> onLoginSuccess;
    private Runnable onBack;
    private boolean isAdminLogin;

    public enum AuthState {
        LOGIN_MAIN, LOGIN_FAILED, REGISTER_FORM, REGISTER_SUCCESS
    }

    public LoginView(Consumer<String> onLoginSuccess, boolean isAdminLogin, Runnable onBack) {
        this.onLoginSuccess = onLoginSuccess;
        this.isAdminLogin = isAdminLogin;
        this.onBack = onBack;
        this.setPrefSize(950, 650);
        this.setStyle("-fx-background-color: white;");

        VBox leftCol = createLeftColumn();
        rightPane = new StackPane();
        rightPane.setPrefWidth(550);
        rightPane.setStyle("-fx-background-color: #F7FAFC;");

        loadingOverlay = new StackPane();
        loadingOverlay.setStyle("-fx-background-color: rgba(0,0,0,0.1);");
        loadingOverlay.setVisible(false);
        loadingOverlay.getChildren().add(new ProgressIndicator());

        switchState(AuthState.LOGIN_MAIN);
        this.getChildren().addAll(leftCol, rightPane);
    }

    private VBox createLeftColumn() {
        VBox left = new VBox(30);
        left.setPrefWidth(400);
        left.setPadding(new Insets(60));
        left.setAlignment(Pos.CENTER_LEFT);

        Button backBtn = new Button("← Kembali");
        backBtn.setStyle(
                "-fx-background-color: transparent; -fx-text-fill: #718096; -fx-font-size: 14px; -fx-cursor: hand; -fx-padding: 5 10;");
        backBtn.setOnAction(e -> {
            if (onBack != null)
                onBack.run();
        });

        StackPane logo = createLogo(60);
        Label brand = new Label("AREDA CAREERS");
        brand.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        brand.setTextFill(Color.web("#2D3748"));

        Label desc = new Label(
                "Areda Careers membantu anda menemukan lowongan kerja terbaik dengan proses lamaran yang mudah dan cepat.");
        desc.setFont(Font.font("Arial", 14));
        desc.setWrapText(true);
        desc.setTextFill(Color.web("#4A5568"));

        left.getChildren().addAll(backBtn, logo, brand, desc);
        return left;
    }

    private void switchState(AuthState state) {
        loadingOverlay.setVisible(true);
        rightPane.setDisable(true);
        PauseTransition pause = new PauseTransition(Duration.seconds(0.4));
        pause.setOnFinished(e -> {
            renderState(state);
            loadingOverlay.setVisible(false);
            rightPane.setDisable(false);
        });
        pause.play();
    }

    private void renderState(AuthState state) {
        rightPane.getChildren().clear();
        VBox card = new VBox(20);
        card.setAlignment(Pos.CENTER);
        card.setMaxWidth(400);
        card.setPadding(new Insets(40));
        card.setStyle(
                "-fx-background-color: #BEE3F8; -fx-background-radius: 20; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5);");

        card.getChildren().addAll(createLogo(30),
                new Label(state == AuthState.REGISTER_FORM ? "REGISTER" : "Areda Careers") {
                    {
                        setFont(Font.font("Arial", FontWeight.BOLD, 18));
                        setTextFill(Color.web("#2D3748"));
                    }
                },
                new Line(0, 0, 250, 0) {
                    {
                        setStroke(Color.web("#A0AEC0"));
                        setOpacity(0.5);
                    }
                });

        switch (state) {
            case LOGIN_MAIN, LOGIN_FAILED -> setupLoginForm(card, state);
            case REGISTER_FORM -> setupRegisterForm(card);
            case REGISTER_SUCCESS -> setupSuccessView(card);
        }
        rightPane.getChildren().addAll(card, loadingOverlay);
    }

    private void setupLoginForm(VBox card, AuthState state) {
        TextField email = createInput("email@domain.com");
        PasswordField pass = createPass("password...");
        Button login = createBtn(isAdminLogin ? "LOGIN ADMIN" : "LOGIN");

        login.setOnAction(e -> {
            String emailText = email.getText().trim();
            String passText = pass.getText();

            if (emailText.isEmpty() || passText.isEmpty()) {
                showAlert("Error", "Email dan password harus diisi!");
                return;
            }

            if (isAdminLogin) {
                // ADMIN LOGIC
                if (DatabaseManager.validateAdminLogin(emailText, passText)) {
                    if (onLoginSuccess != null)
                        onLoginSuccess.accept("Admin");
                } else {
                    showAlert("Error", "Email atau password Admin salah!");
                }
            } else {
                // USER LOGIC
                String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
                if (!emailText.matches(emailRegex)) {
                    showAlert("Error", "Format email tidak valid!");
                    return;
                }
                if (passText.length() < 6) {
                    showAlert("Error", "Password minimal 6 karakter!");
                    return;
                }

                Profil profil = DatabaseManager.getProfil();

                if (!profil.nama.isEmpty() && profil.email.equalsIgnoreCase(emailText)) {
                    if (onLoginSuccess != null) {
                        onLoginSuccess.accept(profil.nama);
                    }
                } else if (profil.nama.isEmpty()) {
                    showAlert("Info", "Akun belum terdaftar! Silakan daftar terlebih dahulu.");
                    switchState(AuthState.REGISTER_FORM);
                } else {
                    showAlert("Error", "Email belum terdaftar!");
                }
            }
        });

        VBox footer = new VBox(5);
        footer.setAlignment(Pos.CENTER);
        if (!isAdminLogin) {
            Label txt = new Label("belum punya akun? Daftar Sekarang!");
            txt.setTextFill(Color.web("#4A5568"));
            Hyperlink link = new Hyperlink("Daftar di sini");
            link.setStyle("-fx-text-fill: #2B6CB0;");
            link.setOnAction(e -> switchState(AuthState.REGISTER_FORM));
            footer.getChildren().addAll(txt, link);
        }

        card.getChildren().addAll(
                createLabel("Masukkan Email Anda"),
                email,
                createLabel("Masukkan Password"),
                pass,
                login,
                footer);
    }

    private void setupRegisterForm(VBox card) {
        TextField name = createInput("Masukkan Nama Lengkap");
        TextField email = createInput("Masukkan Email Aktif");
        PasswordField pass = createPass("Buat Password");
        PasswordField conf = createPass("Konfirmasi Password");
        Button reg = createBtn("DAFTAR SEKARANG!");

        reg.setOnAction(e -> {
            if (name.getText().isEmpty() || email.getText().isEmpty() ||
                    pass.getText().isEmpty() || conf.getText().isEmpty()) {
                showAlert("Error", "Semua field harus diisi!");
                return;
            }

            String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
            if (!email.getText().matches(emailRegex)) {
                showAlert("Error", "Format email tidak valid!");
                return;
            }

            if (!pass.getText().equals(conf.getText())) {
                showAlert("Error", "Password konfirmasi tidak cocok!");
                return;
            }

            if (pass.getText().length() < 6) {
                showAlert("Error", "Password minimal 6 karakter!");
                return;
            }

            Profil profilSudahAda = DatabaseManager.getProfil();
            if (!profilSudahAda.nama.isEmpty() && profilSudahAda.email.equalsIgnoreCase(email.getText().trim())) {
                showAlert("Error", "Email sudah terdaftar! Silakan Login.");
                switchState(AuthState.LOGIN_MAIN);
                return;
            }

            Profil profil = DatabaseManager.getProfil();
            profil.nama = name.getText();
            profil.email = email.getText().trim();
            DatabaseManager.updateProfil(profil);

            switchState(AuthState.REGISTER_SUCCESS);
        });

        HBox foot = new HBox(5);
        foot.setAlignment(Pos.CENTER);
        Hyperlink log = new Hyperlink("Masuk");
        log.setStyle("-fx-text-fill: #2B6CB0;");
        log.setOnAction(e -> switchState(AuthState.LOGIN_MAIN));
        foot.getChildren().addAll(new Label("Sudah punya akun?"), log);
        card.getChildren().addAll(name, email, pass, conf, reg, foot);
    }

    private void setupSuccessView(VBox card) {
        Label msg = new Label("Akun Anda Berhasil dibuat, Silahkan Login dengan Akun Anda");
        msg.setWrapText(true);
        msg.setTextAlignment(TextAlignment.CENTER);
        msg.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        msg.setTextFill(Color.web("#2D3748"));

        Button btn = createBtn("Masuk");
        btn.setOnAction(e -> switchState(AuthState.LOGIN_MAIN));
        card.getChildren().addAll(msg, btn);
    }

    private StackPane createLogo(double size) {
        StackPane sp = new StackPane();
        Label a = new Label("A");
        a.setFont(Font.font("Arial", FontWeight.BOLD, size));
        a.setTextFill(Color.web("#2D3748"));
        Circle dot = new Circle(size / 8, Color.web("#A0522D"));
        StackPane.setAlignment(dot, Pos.BOTTOM_RIGHT);
        sp.getChildren().addAll(a, dot);
        sp.setMaxSize(size, size);
        return sp;
    }

    private Label createLabel(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("Arial", 12));
        label.setTextFill(Color.web("#4A5568"));
        label.setPadding(new Insets(5, 0, 5, 0));
        return label;
    }

    private TextField createInput(String prompt) {
        TextField t = new TextField();
        t.setPromptText(prompt);
        t.setStyle(
                "-fx-background-color: white; -fx-background-radius: 8; -fx-padding: 12; -fx-border-color: #E2E8F0; -fx-border-radius: 8;");
        return t;
    }

    private PasswordField createPass(String prompt) {
        PasswordField t = new PasswordField();
        t.setPromptText(prompt);
        t.setStyle(
                "-fx-background-color: white; -fx-background-radius: 8; -fx-padding: 12; -fx-border-color: #E2E8F0; -fx-border-radius: 8;");
        return t;
    }

    private Button createBtn(String text) {
        Button b = new Button(text);
        b.setMaxWidth(Double.MAX_VALUE);
        String normal = "-fx-background-color: #0066CC; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 12; -fx-cursor: hand;";
        String hover = "-fx-background-color: #004C99; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 12; -fx-cursor: hand;";
        b.setStyle(normal);
        b.setOnMouseEntered(e -> b.setStyle(hover));
        b.setOnMouseExited(e -> b.setStyle(normal));
        return b;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        if (title.equals("Info")) {
            alert = new Alert(Alert.AlertType.INFORMATION);
        }
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}