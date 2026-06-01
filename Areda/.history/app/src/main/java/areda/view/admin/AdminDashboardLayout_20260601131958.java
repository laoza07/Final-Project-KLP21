package areda.view.admin;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class AdminDashboardLayout extends StackPane {
    private BorderPane mainContainer;
    private VBox sidebar;
    private StackPane contentArea;
    private StackPane modalOverlay;
    private Runnable onLogout;
    private VBox navMenu;
    private Button btnHome, btnJobs, btnApps;

    public AdminDashboardLayout(Runnable onLogout) {
        this.onLogout = onLogout;
        this.setPrefSize(1000, 700);

        mainContainer = new BorderPane();
        mainContainer.setStyle("-fx-background-color: white;");

        createSidebar();
        mainContainer.setLeft(sidebar);

        contentArea = new StackPane();
        contentArea.setStyle("-fx-background-color: white;");
        mainContainer.setCenter(contentArea);

        setContentView(new AdminHomeView());

        modalOverlay = new StackPane();
        modalOverlay.getStyleClass().add("modal-overlay");
        modalOverlay.setVisible(false);

        this.getChildren().addAll(mainContainer, modalOverlay);
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
        btnJobs = createNavButton("💼", "Kelola Lowongan", false);
        btnApps = createNavButton("👥", "Pelamar", false);

        btnHome.setOnAction(e -> {
            updateActiveButton(btnHome, navMenu);
            setContentView(new AdminHomeView());
        });
        btnJobs.setOnAction(e -> {
            updateActiveButton(btnJobs, navMenu);
            setContentView(new ManageJobsView());
        });
        btnApps.setOnAction(e -> {
            updateActiveButton(btnApps, navMenu);
            setContentView(new ApplicantListView());
        });

        navMenu.getChildren().addAll(btnHome, btnJobs, btnApps);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button btnLogout = createNavButton("⭾", "Keluar", false);
        btnLogout.setOnAction(e -> showLogoutModal());

        sidebar.getChildren().addAll(logoBox, navMenu, spacer, btnLogout);
    }

    private void showLogoutModal() {
        VBox modal = new VBox(20);
        modal.setMaxSize(350, 250);
        modal.setAlignment(Pos.CENTER);
        modal.setPadding(new Insets(30));
        modal.setStyle(
                "-fx-background-color: white; -fx-background-radius: 20; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.15), 15, 0, 0, 5);");

        StackPane icon = new StackPane();
        Circle circle = new Circle(25, Color.web("#FFFAF0"));
        circle.setStroke(Color.web("#ECC94B"));
        Label exclamation = new Label("!");
        exclamation.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        exclamation.setTextFill(Color.web("#ECC94B"));
        icon.getChildren().addAll(circle, exclamation);

        Label title = new Label("Keluar dari Akun?");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        title.setTextFill(Color.web("#1A202C"));

        Label sub = new Label("Apakah anda yakin ingin keluar?");
        sub.setTextFill(Color.web("#718096"));

        HBox buttons = new HBox(15);
        buttons.setAlignment(Pos.CENTER);

        Button cancel = new Button("Batal");
        cancel.setStyle(
                "-fx-background-color: #EDF2F7; -fx-padding: 10 25; -fx-background-radius: 8; -fx-cursor: hand;");
        cancel.setOnAction(e -> hideModal());

        Button delete = new Button("Keluar");
        delete.setStyle(
                "-fx-background-color: #E53E3E; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 25; -fx-background-radius: 8; -fx-cursor: hand;");
        delete.setOnAction(e -> {
            hideModal();
            if (onLogout != null)
                onLogout.run();
        });

        buttons.getChildren().addAll(cancel, delete);
        modal.getChildren().addAll(icon, title, sub, buttons);

        modalOverlay.getChildren().clear();
        modalOverlay.getChildren().add(modal);
        modalOverlay.setVisible(true);
        mainContainer.setDisable(true);
    }

    private void hideModal() {
        modalOverlay.setVisible(false);
        mainContainer.setDisable(false);
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