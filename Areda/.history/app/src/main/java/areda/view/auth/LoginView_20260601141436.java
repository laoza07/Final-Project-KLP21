package areda.view.auth;

import java.util.function.Consumer;
import areda.model.DatabaseManager;
import areda.model.Profil;
import areda.model.UserAccount;
import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

public class LoginView extends HBox {
    private StackPane rightPane, loadingOverlay;
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
        this.setStyle("-fx-background-color: #C9E9FF;");

        VBox leftCol = createLeftColumn();
        rightPane = new StackPane();
        rightPane.setPrefWidth(550);
        rightPane.setStyle("-fx-background-color: #C9E9FF;");

        loadingOverlay = new StackPane();
        loadingOverlay.setStyle("-fx-background-color: rgba(0,0,0,0.1);");
        loadingOverlay.setVisible(false);
        loadingOverlay.getChildren().add(new ProgressIndicator());

        switchState(AuthState.LOGIN_MAIN);
        this.getChildren().addAll(leftCol, rightPane);
    }

    private VBox createLeftColumn() {
        VBox left = new VBox(25);
        left.setPrefWidth(400);
        left.setPadding(new Insets(50, 40, 50, 40));
        left.setAlignment(Pos.CENTER_LEFT);
        left.setStyle("-fx-background-color: white;");

        Button backBtn = new Button("← Kembali");
        backBtn.setStyle(
                "-fx-background-color: transparent; -fx-text-fill: #718096; -fx-font-size: 14px; -fx-cursor: hand;");
        backBtn.setOnAction(e -> {
            if (onBack != null)
                onBack.run();
        });

        StackPane logo = createLogo(100);
        Label brand = new Label("Areda Careers");
        brand.setFont(Font.font("Konkhmer Sleokchher", FontWeight.BOLD, 32));
        brand.setTextFill(Color.web("#1A202C"));

        Label desc = new Label("Temukan Peluang Terbaik dan Bangun Karier Impianmu Bersama Areda Careers.");
        desc.setFont(Font.font("Andika New Basic", FontWeight.NORMAL, 15));
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
        VBox card = new VBox(18);
        card.setAlignment(Pos.CENTER);
        card.setMaxWidth(380);
        card.setPadding(new Insets(35));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 25; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.08), 15, 0, 0, 5);");

        String titleText = state == AuthState.REGISTER_FORM ? "REGISTER"
                : (isAdminLogin ? "LOGIN ADMIN" : "LOGIN USER");
        Label titleLabel = new Label(titleText);
        titleLabel.setFont(Font.font("Amaranth", FontWeight.BOLD, 22));
        titleLabel.setTextFill(Color.web("#1A202C"));

        Line line = new Line(0, 0, 220, 0);
        line.setStroke(Color.web("#0048FF"));
        line.setOpacity(0.3);

        card.getChildren().addAll(createLogo(65), titleLabel, line);

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
        Button login = createBtn(isAdminLogin ? "LOGIN ADMIN" : "LOGIN USER");

        login.setOnAction(e -> {
            String emailText = email.getText().trim();
            String passText = pass.getText();

            if (emailText.isEmpty() || passText.isEmpty()) {
                showAlert("Error", "Email dan password harus diisi!");
                return;
            }

            if (isAdminLogin) {
                if (DatabaseManager.validateAdminLogin(emailText, passText)) {
                    if (onLoginSuccess != null)
                        onLoginSuccess.accept("Admin");
                } else
                    showAlert("Error", "Email atau password Admin salah!");
            } else {
                UserAccount account = DatabaseManager.loginUser(emailText, passText);
                if (account != null) {
                    Profil profil = DatabaseManager.getCurrentUserProfil();
                    if (onLoginSuccess != null)
                        onLoginSuccess.accept(profil.nama.isEmpty() ? emailText : profil.nama);
                } else {
                    if (DatabaseManager.isEmailRegistered(emailText)) {
                        showAlert("Error", "Password salah! Silakan coba lagi.");
                    } else {
                        showAlert("Error", "Akun belum terdaftar!\nSilakan daftar terlebih dahulu.");
                    }
                }
            }
        });

        VBox footer = new VBox(5);
        footer.setAlignment(Pos.CENTER);
        if (!isAdminLogin) {
            Label txt = new Label("Belum punya akun?");
            txt.setFont(Font.font("Andika New Basic", 13));
            txt.setTextFill(Color.web("#4A5568"));
            Hyperlink link = new Hyperlink("Daftar Sekarang!");
            link.setStyle("-fx-text-fill: #0048FF; -fx-font-weight: bold;");
            link.setOnAction(e -> switchState(AuthState.REGISTER_FORM));
            footer.getChildren().addAll(txt, link);
        }
        card.getChildren().addAll(createLabel("Masukkan Email Anda"), email, createLabel("Masukkan Password"), pass,
                login, footer);
    }

    private void setupRegisterForm(VBox card) {
        TextField name = createInput("Masukkan Nama Lengkap");
        TextField email = createInput("Masukkan Email Aktif");
        PasswordField pass = createPass("Buat Password");
        PasswordField conf = createPass("Konfirmasi Password");
        Button reg = createBtn("DAFTAR SEKARANG!");

        reg.setOnAction(e -> {
            if (name.getText().isEmpty() || email.getText().isEmpty() || pass.getText().isEmpty()
                    || conf.getText().isEmpty()) {
                showAlert("Error", "Semua field harus diisi!");
                return;
            }
            if (!email.getText().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
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

            if (DatabaseManager.isEmailRegistered(email.getText().trim())) {
                showAlert("Error", "Email sudah terdaftar! Silakan Login.");
                switchState(AuthState.LOGIN_MAIN);
            } else if (DatabaseManager.registerUser(email.getText().trim(), pass.getText(), name.getText())) {
                switchState(AuthState.REGISTER_SUCCESS);
            } else {
                showAlert("Error", "Gagal mendaftar. Terjadi kesalahan database.");
            }
        });

        HBox foot = new HBox(5);
        foot.setAlignment(Pos.CENTER);
        Label suddenLabel = new Label("Sudah punya akun?");
        suddenLabel.setFont(Font.font("Andika New Basic", 13));
        suddenLabel.setTextFill(Color.web("#4A5568"));
        Hyperlink log = new Hyperlink("Masuk");
        log.setStyle("-fx-text-fill: #0048FF; -fx-font-weight: bold;");
        log.setOnAction(e -> switchState(AuthState.LOGIN_MAIN));
        foot.getChildren().addAll(suddenLabel, log);
        card.getChildren().addAll(name, email, pass, conf, reg, foot);
    }

    private void setupSuccessView(VBox card) {
        StackPane successIcon = new StackPane();
        successIcon.setPrefSize(60, 60);
        Circle greenCircle = new Circle(30, Color.web("#C6F6D5"));
        greenCircle.setStroke(Color.web("#38A169"));
        greenCircle.setStrokeWidth(3);
        Label checkMark = new Label("✓");
        checkMark.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        checkMark.setTextFill(Color.web("#22543D"));
        successIcon.getChildren().addAll(greenCircle, checkMark);

        Label msg = new Label("Akun Anda Berhasil dibuat,\nSilahkan Login dengan Akun Anda");
        msg.setWrapText(true);
        msg.setTextAlignment(TextAlignment.CENTER);
        msg.setFont(Font.font("Andika New Basic", FontWeight.BOLD, 14));
        msg.setTextFill(Color.web("#1A202C"));

        Button btn = createBtn("Masuk");
        btn.setOnAction(e -> switchState(AuthState.LOGIN_MAIN));
        card.getChildren().addAll(successIcon, msg, btn);
    }

    private StackPane createLogo(double size) {
        StackPane sp = new StackPane();
        sp.setPrefSize(size, size);
        sp.setMaxSize(size, size);
        sp.setAlignment(Pos.CENTER);
        try {
            ImageView logoView = new ImageView(new Image(getClass().getResourceAsStream("/logo.png")));
            logoView.setFitWidth(size);
            logoView.setPreserveRatio(true);
            sp.getChildren().add(logoView);
        } catch (Exception e) {
            Label fallback = new Label("A");
            fallback.setFont(Font.font("Arial", FontWeight.BOLD, size * 0.8));
            fallback.setTextFill(Color.web("#0048FF"));
            sp.getChildren().add(fallback);
        }
        return sp;
    }

    private Label createLabel(String text) {
        Label l = new Label(text);
        l.setFont(Font.font("Andika New Basic", 12));
        l.setPadding(new Insets(3, 0, 3, 0));
        l.setTextFill(Color.web("#4A5568"));
        return l;
    }

    private TextField createInput(String p) {
        TextField t = new TextField();
        t.setPromptText(p);
        t.getStyleClass().add("form-input");
        return t;
    }

    private PasswordField createPass(String p) {
        PasswordField t = new PasswordField();
        t.setPromptText(p);
        t.getStyleClass().add("form-input");
        return t;
    }

    private Button createBtn(String t) {
        Button b = new Button(t);
        b.setMaxWidth(Double.MAX_VALUE);
        b.getStyleClass().add("btn-auth-primary");
        return b;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(title.equals("Error") ? Alert.AlertType.ERROR : Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}