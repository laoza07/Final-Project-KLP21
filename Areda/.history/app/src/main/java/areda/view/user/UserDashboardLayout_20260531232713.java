package areda.view.user;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class UserDashboardLayout extends BorderPane {

    private VBox sidebar;
    private StackPane contentArea;
    private Runnable onLogout;
    private String currentUsername;

    private Button btnHome, btnJobs, btnApps, btnSaved, btnProfile;
    private VBox navMenu;

    public UserDashboardLayout(Runnable onLogout, String username) {
        this.onLogout = onLogout;
        this.currentUsername = username;
        this.setPrefSize(1000, 700);
        this.setStyle("-fx-background-color: white;");

        createSidebar();
        this.setLeft(sidebar);

        contentArea = new StackPane();
        contentArea.setStyle("-fx-background-color: white;");
        this.setCenter(contentArea);

        setContentView(new UserHomeView(currentUsername));
    }

    private void createSidebar() {
        sidebar = new VBox();
        sidebar.setPrefWidth(240);
        sidebar.setPadding(new Insets(30, 15, 30, 15));
        sidebar.setStyle("-fx-background-color: #BEE3F8;");

        VBox logoBox = new VBox(5);
        logoBox.setAlignment(Pos.CENTER);

        // PENYESUAIAN LOGO: Mengganti teks 'A' dengan ImageView
        StackPane logoImgContainer = new StackPane();
        try {
            // Mencoba memuat file logo.png dari resource
            // Pastikan huruf kapitalnya cocok dengan nama file di folder (logo.png /
            // logo.PNG)
            Image image = new Image(getClass().getResourceAsStream("/logo.png"));
            ImageView logoView = new ImageView(image);
            logoView.setFitHeight(50); // Menyesuaikan tinggi logo agar pas di sidebar
            logoView.setPreserveRatio(true);
            logoImgContainer.getChildren().add(logoView);
        } catch (Exception e) {
            // FALLBACK: Menampilkan huruf 'A' jika gambar gagal dimuat
            Label letterA = new Label("A");
            letterA.setFont(Font.font("Arial", FontWeight.BOLD, 40));
            letterA.setTextFill(Color.web("#2D3748"));
            Circle dot = new Circle(4, Color.web("#A0522D"));
            StackPane.setAlignment(dot, Pos.BOTTOM_RIGHT);
            StackPane.setMargin(dot, new Insets(0, 5, 8, 0));
            logoImgContainer.getChildren().addAll(letterA, dot);
        }
        logoImgContainer.setMaxSize(60, 60);

        Label brandName = new Label("Areda Careers");
        brandName.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        brandName.setTextFill(Color.web("#2D3748"));

        Line separator = new Line(0, 0, 180, 0);
        separator.setStroke(Color.web("#A0AEC0"));
        separator.setOpacity(0.5);

        logoBox.getChildren().addAll(logoImgContainer, brandName, separator);
        VBox.setMargin(separator, new Insets(15, 0, 20, 0));

        navMenu = new VBox(10);
        navMenu.setAlignment(Pos.TOP_CENTER);

        btnHome = createNavButton("", "Beranda", true);
        btnJobs = createNavButton("💼", "Lowongan", false);
        btnApps = createNavButton("👥", "Lamaran", false);
        btnSaved = createNavButton("🔖", "Markah", false);
        btnProfile = createNavButton("👤", "Profil", false);

        btnHome.setOnAction(e -> navigateTo("Beranda"));
        btnJobs.setOnAction(e -> navigateTo("Lowongan"));
        btnApps.setOnAction(e -> navigateTo("Lamaran"));
        btnSaved.setOnAction(e -> navigateTo("Markah"));
        btnProfile.setOnAction(e -> navigateTo("Profil"));

        navMenu.getChildren().addAll(btnHome, btnJobs, btnApps, btnSaved, btnProfile);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button btnLogout = createNavButton("⭾", "Keluar", false);
        btnLogout.setStyle(
                "-fx-background-color: transparent; -fx-text-fill: #2D3748; -fx-font-size: 14px; -fx-alignment: BASELINE_LEFT; -fx-padding: 10 20; -fx-cursor: hand;");
        btnLogout.setOnAction(e -> {
            if (onLogout != null)
                onLogout.run();
        });

        sidebar.getChildren().addAll(logoBox, navMenu, spacer, btnLogout);
    }

    public void navigateTo(String menuName) {
        Button targetBtn;
        Node view;

        switch (menuName) {
            case "Lowongan" -> {
                targetBtn = btnJobs;
                view = new JobListView(); // Asumsi kelas ini sudah ada
            }
            case "Lamaran" -> {
                targetBtn = btnApps;
                view = new MyApplicationsView(); // Asumsi kelas ini sudah ada
            }
            case "Markah" -> {
                targetBtn = btnSaved;
                view = new SavedJobsView(this); // Asumsi kelas ini sudah ada
            }
            case "Profil" -> {
                targetBtn = btnProfile;
                view = new ProfileView(); // Asumsi kelas ini sudah ada
            }
            default -> {
                targetBtn = btnHome;
                view = new UserHomeView(currentUsername); // Asumsi kelas ini sudah ada
            }
        }

        updateActiveButton(targetBtn, navMenu);
        setContentView(view);
    }

    private void updateActiveButton(Button activeBtn, VBox container) {
        for (Node node : container.getChildren()) {
            if (node instanceof Button) {
                Button btn = (Button) node;
                boolean isActive = btn == activeBtn;
                btn.setStyle(getButtonStyle(isActive));
            }
        }
    }

    private String getButtonStyle(boolean isActive) {
        if (isActive) {
            return "-fx-background-color: white; -fx-text-fill: #2B6CB0; -fx-font-weight: bold; -fx-font-size: 14px; -fx-background-radius: 10; -fx-alignment: BASELINE_LEFT; -fx-padding: 12 20; -fx-cursor: hand;";
        } else {
            return "-fx-background-color: transparent; -fx-text-fill: #2D3748; -fx-font-size: 14px; -fx-alignment: BASELINE_LEFT; -fx-padding: 12 20; -fx-cursor: hand;";
        }
    }

    private Button createNavButton(String icon, String text, boolean isActive) {
        Button btn = new Button(icon + "   " + text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setStyle(getButtonStyle(isActive));

        if (!isActive) {
            btn.setOnMouseEntered(e -> {
                if (!btn.getStyle().contains("white")) {
                    btn.setStyle(getButtonStyle(false) + "-fx-background-color: rgba(255,255,255,0.3);");
                }
            });
            btn.setOnMouseExited(e -> {
                if (!btn.getStyle().contains("white")) {
                    btn.setStyle(getButtonStyle(false));
                }
            });
        }
        return btn;
    }

    public void setContentView(Node view) {
        contentArea.getChildren().clear();
        contentArea.getChildren().add(view);
    }
}