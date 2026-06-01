package areda.view.auth;

import java.util.function.Consumer;

import areda.model.DatabaseManager;
import areda.model.Profil;
import areda.model.UserAccount;
import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
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
        this.setStyle("-fx-background-color: white;");

        VBox leftCol = createLeftColumn();
        HBox.setHgrow(leftCol, Priority.ALWAYS);

        rightPane = new StackPane();
        HBox.setHgrow(rightPane, Priority.ALWAYS);
        rightPane.setStyle("-fx-background-color: white;");

        loadingOverlay = new StackPane();
        loadingOverlay.setStyle("-fx-background-color: rgba(255,255,255,0.2); -fx-background-radius: 30;");
        loadingOverlay.setVisible(false);
        loadingOverlay.getChildren().add(new ProgressIndicator());

        switchState(AuthState.LOGIN_MAIN);
        this.getChildren().addAll(leftCol, rightPane);
    }

    private VBox createLeftColumn() {
        VBox left = new VBox(30);
        left.setMinWidth(400);
        left.setMaxWidth(500);
        left.setPadding(new Insets(60, 40, 40, 60));
        left.setAlignment(Pos.TOP_LEFT);

        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);
        StackPane logo = createLogo(60);
        Label brand = new Label("AREDA CAREERS");
        brand.setFont(Font.font("System", FontWeight.BOLD, 28));
        brand.setTextFill(Color.web("#1A202C"));
        header.getChildren().addAll(logo, brand);

        Label desc = new Label("Areda Careers membantu anda menemukan lowongan kerja terbaik dengan proses lamaran yang mudah dan cepat.");
        desc.setFont(Font.font("Andika New Basic", FontWeight.NORMAL, 16));
        desc.setWrapText(true);
        desc.setTextFill(Color.web("#4A5568"));
        desc.setMaxWidth(350);
        desc.setLineSpacing(5);

        Button backBtn = new Button("← Kembali ke Beranda");
        backBtn.setStyle(
                "-fx-background-color: transparent; -fx-text-fill: #0048FF; -fx-font-weight: bold; -fx-font-size: 14px; -fx-cursor: hand; -fx-padding: 20 0 0 0;");
        backBtn.setOnAction(e -> {
            if (onBack != null)
                onBack.run();
        });

        left.getChildren().addAll(header, desc, backBtn);
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
        VBox card = new VBox(15);
        card.setAlignment(Pos.CENTER);
        card.setMaxWidth(420);
        card.setPadding(new Insets(40));
        card.getStyleClass().add("auth-card");

        StackPane smallLogo = createLogo(45);
        Label cardTitle = new Label("Areda Careers");
        cardTitle.setFont(Font.font("System", FontWeight.BOLD, 18));

        Line line = new Line(0, 0, 280, 0);
        line.setStroke(Color.web("#4A5568"));
        line.setOpacity(0.2);

        String mainTitle = state == AuthState.REGISTER_FORM ? "USER REGISTRATION"
                : (isAdminLogin ? "ADMIN LOGIN" : "USER LOGIN");
        Label titleLabel = new Label(mainTitle);
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.web("#1A202C"));

        Label subTitle = new Label("Masuk untuk Melanjutkan pencarian pekerjaan terbaikmu!");
        subTitle.setFont(Font.font("System", 13));
        subTitle.setTextFill(Color.web("#4A5568"));
        subTitle.setWrapText(true);
        subTitle.setTextAlignment(TextAlignment.CENTER);

        card.getChildren().addAll(smallLogo, cardTitle, line, titleLabel, subTitle);

        switch (state) {
            case LOGIN_MAIN, LOGIN_FAILED -> setupLoginForm(card, state);
            case REGISTER_FORM -> setupRegisterForm(card);
            case REGISTER_SUCCESS -> setupSuccessView(card);
        }
        rightPane.getChildren().addAll(card, loadingOverlay);
    }

    private void setupLoginForm(VBox card, AuthState state) {
        VBox form = new VBox(10);
        form.setAlignment(Pos.CENTER_LEFT);

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
                if (DatabaseManager.validateAdminLogin(emailText, passText)) {
                    if (onLoginSuccess != null)
                        onLoginSuccess.accept("Admin");
                } else {
                    showAlert("Error", "Email atau password Admin salah!");
                }
            } else {
                UserAccount account = DatabaseManager.loginUser(emailText, passText);
                if (account != null) {
                    Profil profil = DatabaseManager.getCurrentUserProfil();
                    String displayName = (profil.getNama() != null && !profil.getNama().isEmpty()) ? profil.getNama()
                            : emailText;
                    if (onLoginSuccess != null) {
                        onLoginSuccess.accept(displayName);
                    }
                } else {
                    showAlert("Error", "Email atau password salah!");
                }
            }
        });

        VBox footer = new VBox(12);
        footer.setAlignment(Pos.CENTER);
        footer.setPadding(new Insets(10, 0, 0, 0));

        if (!isAdminLogin) {
            Label txt = new Label("belum punya akun? Daftar Sekarang!");
            txt.setFont(Font.font("System", 13));
            txt.setTextFill(Color.web("#4A5568"));

            Button regBtn = new Button("Daftar di sini");
            regBtn.getStyleClass().add("btn-register-link");
            regBtn.setOnAction(e -> switchState(AuthState.REGISTER_FORM));

            footer.getChildren().addAll(txt, regBtn);
        }

        card.getChildren().addAll(
                createLabel("Masukkan Email Anda"), email,
                createLabel("Masukkan Password"), pass,
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