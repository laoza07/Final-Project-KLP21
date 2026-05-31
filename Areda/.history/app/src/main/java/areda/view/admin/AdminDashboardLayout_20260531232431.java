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
        modalOverlay.setStyle("-fx-background-color: rgba(0,0,0,0.5);");
        modalOverlay.setVisible(false);

        this.getChildren().addAll(mainContainer, modalOverlay);
    }

    private void createSidebar() {
        sidebar = new VBox();
        sidebar.setPrefWidth(240);
        sidebar.setPadding(new Insets(30, 15, 30, 15));
        sidebar.setStyle("-fx-background-color: #BEE3F8;");

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

        VBox navMenu = new VBox(10);
        navMenu.setAlignment(Pos.TOP_CENTER);

        Button btnHome = createNavButton("⌂", "Beranda", true);
        Button btnJobs = createNavButton("", "Kelola Lowongan", false);
        Button btnApps = createNavButton("👥", "Pelamar", false);

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
        btnLogout.setStyle(
                "-fx-background-color: transparent; -fx-text-fill: #2D3748; -fx-font-size: 14px; -fx-alignment: BASELINE_LEFT; -fx-padding: 10 20; -fx-cursor: hand;");
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