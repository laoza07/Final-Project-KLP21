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
        sidebar.getStyleClass().add("sidebar-bg");

        VBox logoBox = new VBox(5);
        logoBox.setAlignment(Pos.CENTER);

        StackPane logoImgContainer = new StackPane();
        try {
            Image image = new Image(getClass().getResourceAsStream("/logo.png"));
            ImageView logoView = new ImageView(image);
            logoView.setFitHeight(50);
            logoView.setPreserveRatio(true);
            logoImgContainer.getChildren().add(logoView);
        } catch (Exception e) {
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

        btnHome = createNavButton("⌂", "Beranda", true);
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
                view = new JobListView();
            }
            case "Lamaran" -> {
                targetBtn = btnApps;
                view = new MyApplicationsView();
            }
            case "Markah" -> {
                targetBtn = btnSaved;
                view = new SavedJobsView(this);
            }
            case "Profil" -> {
                targetBtn = btnProfile;
                view = new ProfileView();
            }
            default -> {
                targetBtn = btnHome;
                view = new UserHomeView(currentUsername);
            }
        }

        updateActiveButton(targetBtn, navMenu);
        setContentView(view);
    }

    private void updateActiveButton(Button activeBtn, VBox container) {
        for (Node node : container.getChildren()) {
            if (node instanceof Button) {
                Button btn = (Button) node;
                btn.getStyleClass().removeAll("nav-btn-active", "nav-btn");
                if (btn == activeBtn) {
                    btn.getStyleClass().add("nav-btn-active");
                } else {
                    btn.getStyleClass().add("nav-btn");
                }
            }
        }
    }

    private Button createNavButton(String icon, String text, boolean isActive) {
        Button btn = new Button(icon + "     " + text);
        btn.setMaxWidth(Double.MAX_VALUE);
        if (isActive)
            btn.getStyleClass().add("nav-btn-active");
        else
            btn.getStyleClass().add("nav-btn");
        return btn;
    }

    public void setContentView(Node view) {
        contentArea.getChildren().clear();
        StackPane wrapper = new StackPane(view);
        wrapper.setStyle("-fx-background-color: white;");
        VBox.setVgrow(wrapper, Priority.ALWAYS);
        contentArea.getChildren().add(wrapper);
    }
}