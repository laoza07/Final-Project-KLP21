package areda;

import areda.view.auth.LoginView;
import areda.view.auth.RoleSelectionView;
import areda.view.user.UserDashboardLayout;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {
    private Stage primaryStage;
    private Scene roleSelectionScene, loginScene, adminLoginScene;
    private static final double APP_WIDTH = 1100;
    private static final double APP_HEIGHT = 700;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Areda Careers");
        primaryStage.setMinWidth(950);
        primaryStage.setMinHeight(650);
        primaryStage.setResizable(true);

        roleSelectionScene = new Scene(createRoleSelectionView(), APP_WIDTH, APP_HEIGHT);
        loginScene = new Scene(createLoginView(), APP_WIDTH, APP_HEIGHT);
        adminLoginScene = new Scene(createAdminLoginView(), APP_WIDTH, APP_HEIGHT);

        applyCss(roleSelectionScene);
        applyCss(loginScene);
        applyCss(adminLoginScene);

        primaryStage.setScene(roleSelectionScene);
        primaryStage.show();
    }

    private void applyCss(Scene scene) {
        if (getClass().getResource("/areda.css") != null)
            scene.getStylesheets().add(getClass().getResource("/areda.css").toExternalForm());
    }

    private RoleSelectionView createRoleSelectionView() {
        return new RoleSelectionView(this::showLogin, this::showAdminLogin);
    }

    private LoginView createLoginView() {
        return new LoginView(this::showUserDashboard, false, this::showRoleSelection);
    }

    private LoginView createAdminLoginView() {
        return new LoginView(this::showAdminDashboard, true, this::showRoleSelection);
    }

    private void showLogin() {
        loginScene.setRoot(createLoginView());
        primaryStage.setScene(loginScene);
    }

    private void showAdminLogin() {
        adminLoginScene.setRoot(createAdminLoginView());
        primaryStage.setScene(adminLoginScene);
    }

    private void showRoleSelection() {
        roleSelectionScene.setRoot(createRoleSelectionView());
        primaryStage.setScene(roleSelectionScene);
    }

    private void showUserDashboard(String username) {
        UserDashboardLayout dashboard = new UserDashboardLayout(this::showRoleSelection, username);
        Scene userScene = new Scene(dashboard, APP_WIDTH, APP_HEIGHT);
        applyCss(userScene);
        primaryStage.setScene(userScene);
    }

    private void showAdminDashboard(String username) {
        areda.view.admin.AdminDashboardLayout adminDashboard = new areda.view.admin.AdminDashboardLayout(
                this::showRoleSelection);
        Scene adminScene = new Scene(adminDashboard, APP_WIDTH, APP_HEIGHT);
        applyCss(adminScene);
        primaryStage.setScene(adminScene);
    }

    public static void main(String[] args) {
        launch(args);
    }
}