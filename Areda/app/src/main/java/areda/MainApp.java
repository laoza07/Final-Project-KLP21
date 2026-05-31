package areda;

import areda.view.auth.LoginView;
import areda.view.auth.RoleSelectionView;
import areda.view.user.UserDashboardLayout;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    private Stage primaryStage;
    private Scene roleSelectionScene;
    private Scene loginScene;
    private Scene adminLoginScene;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Areda Careers");
        primaryStage.setMinWidth(950);
        primaryStage.setMinHeight(650);

        roleSelectionScene = new Scene(createRoleSelectionView(), 950, 650);
        loginScene = new Scene(createLoginView(), 950, 650);
        adminLoginScene = new Scene(createAdminLoginView(), 950, 650);

        primaryStage.setScene(roleSelectionScene);
        primaryStage.show();
    }

    private RoleSelectionView createRoleSelectionView() {
        return new RoleSelectionView(
                this::showLogin,
                this::showAdminLogin);
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
        UserDashboardLayout dashboard = new UserDashboardLayout(() -> {
            showRoleSelection();
        }, username);

        Scene userScene = new Scene(dashboard, 1000, 700);
        primaryStage.setScene(userScene);
    }

    private void showAdminDashboard(String username) {
        areda.view.admin.AdminDashboardLayout adminDashboard = new areda.view.admin.AdminDashboardLayout(
                () -> {
                    showRoleSelection();
                });

        Scene adminScene = new Scene(adminDashboard, 1000, 700);
        primaryStage.setScene(adminScene);
    }

    public static void main(String[] args) {
        launch(args);
    }
}