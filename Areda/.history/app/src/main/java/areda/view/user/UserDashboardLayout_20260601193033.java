package areda.view.user;

import areda.view.layout.BaseDashboardLayout;
import javafx.scene.control.Button;
import javafx.scene.Node;

public class UserDashboardLayout extends BaseDashboardLayout {
    private Button btnHome, btnJobs, btnApps, btnSaved, btnProfile;

    public UserDashboardLayout(Runnable onLogout, String username) {
        super(onLogout, username);
    }

    @Override
    protected void setupNavigationButtons() {
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
    }

    @Override
    protected void initializeDefaultView() {
        setContentView(new UserHomeView(currentUsername));
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
}