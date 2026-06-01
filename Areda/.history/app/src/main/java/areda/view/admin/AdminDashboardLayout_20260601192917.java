package areda.view.admin;

import areda.view.layout.BaseDashboardLayout;
import javafx.scene.control.Button;

public class AdminDashboardLayout extends BaseDashboardLayout {
    private Button btnHome, btnJobs, btnApps;

    public AdminDashboardLayout(Runnable onLogout) {
        super(onLogout, "Admin");
    }

    @Override
    protected void setupNavigationButtons() {
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
    }

    @Override
    protected void initializeDefaultView() {
        setContentView(new AdminHomeView());
    }
}