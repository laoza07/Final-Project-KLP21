package areda.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class DatabaseManager {
    private static final String FILE_PATH = "areda_database.ser";
    private static DatabaseState state;
    private static UserAccount currentUserAccount;
    private static ObservableList<Lowongan> observableLowongan = FXCollections.observableArrayList();
    private static ObservableList<Lamaran> observableLamaran = FXCollections.observableArrayList();

    private static class DatabaseState implements Serializable {
        private static final long serialVersionUID = 1L;
        public List<Lowongan> listLowongan = new ArrayList<>();
        public List<UserAccount> listUserAccounts = new ArrayList<>();
        public Map<String, Profil> mapProfilByUser = new HashMap<>();
        public List<Lamaran> listLamaran = new ArrayList<>();
        public Set<String> bookmarkedLowonganIds = new HashSet<>();
        public Profil profilUser = new Profil();
    }

    static {
        loadData();
    }

    private static void loadData() {
        File file = new File(FILE_PATH);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                state = (DatabaseState) ois.readObject();
                observableLowongan.setAll(state.listLowongan);
                observableLamaran.setAll(state.listLamaran);
            } catch (Exception e) {
                System.err.println("Database corrupted, resetting...");
                seedInitialData();
            }
        } else {
            seedInitialData();
        }
    }

    public static void saveData() {
        state.listLowongan = new ArrayList<>(observableLowongan);
        state.listLamaran = new ArrayList<>(observableLamaran);
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(state);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void seedInitialData() {
        state = new DatabaseState();
        state.listUserAccounts.add(new UserAccount("admin@areda.com", "admin123", UserRole.ADMIN));

        observableLowongan.add(new Lowongan("MKT-01", "Marketing", "Social Media Specialist",
                "Mengelola akun medsos, meningkatkan engagement.", Arrays.asList("Social Media Management",
                        "Content Planning", "Copywriting", "Meta Business Suite", "Canva / Adobe Photoshop"),
                2, "10 Mei 2026"));
        observableLowongan
                .add(new Lowongan("MKT-02", "Marketing", "Content Creator", "Membuat konten visual & tulisan kreatif.",
                        Arrays.asList("Canva / Adobe Illustrator", "Adobe Premiere", "Photography & Videography"), 5,
                        "12 Mei 2026"));
        observableLowongan
                .add(new Lowongan("MKT-03", "Marketing", "Digital Marketing Staff", "Merancang strategi iklan digital.",
                        Arrays.asList("Google Ads", "Meta Ads", "SEO & SEM"), -1, "15 Mei 2026"));
        observableLowongan.add(new Lowongan("IT-01", "Teknologi Informasi", "Frontend Developer",
                "Membangun tampilan website responsif.", Arrays.asList("HTML", "CSS", "JavaScript", "React.js"), 3,
                "01 Mei 2026"));
        observableLowongan.add(new Lowongan("IT-02", "Teknologi Informasi", "Backend Developer",
                "Mengembangkan sistem backend, server, database.", Arrays.asList("PHP", "Node.js", "MySQL"), 2,
                "05 Mei 2026"));
        observableLowongan
                .add(new Lowongan("IT-03", "Teknologi Informasi", "UI/UX Designer", "Merancang wireframe, prototipe.",
                        Arrays.asList("Figma", "Wireframing", "Prototyping"), 1, "08 Mei 2026"));
        observableLowongan
                .add(new Lowongan("IT-04", "Teknologi Informasi", "QA Tester", "Membuat test case, pengujian manual.",
                        Arrays.asList("Manual Testing", "Test Case Writing", "Bug Tracking"), -1, "18 Mei 2026"));
        observableLowongan.add(new Lowongan("HR-01", "Human Resource", "HR Staff", "Administrasi SDM, data karyawan.",
                Arrays.asList("Microsoft Excel", "HRIS", "Payroll System"), 4, "20 Mei 2026"));
        observableLowongan.add(
                new Lowongan("HR-02", "Human Resource", "Recruitment Staff", "Screening kandidat dan jadwal interview.",
                        Arrays.asList("CV Screening", "ATS", "Interview Scheduling"), 1, "21 Mei 2026"));
        observableLowongan
                .add(new Lowongan("FIN-01", "Keuangan", "Finance Staff", "Monitoring arus kas, laporan keuangan.",
                        Arrays.asList("Microsoft Excel", "Financial Reporting"), 2, "22 Mei 2026"));
        observableLowongan
                .add(new Lowongan("FIN-02", "Keuangan", "Accounting Staff", "Pencatatan jurnal dan pembukuan akurat.",
                        Arrays.asList("Journal Entry", "Accurate / SAP"), -1, "25 Mei 2026"));
        observableLowongan
                .add(new Lowongan("OPS-01", "Operasional", "Administrasi", "Pengelolaan dokumen dan data entry.",
                        Arrays.asList("Microsoft Office", "Data Entry"), 3, "26 Mei 2026"));
        observableLowongan
                .add(new Lowongan("OPS-02", "Operasional", "Customer Service", "Melayani kebutuhan, keluhan pelanggan.",
                        Arrays.asList("CRM Software", "Ticketing System"), 2, "28 Mei 2026"));

        saveData();
    }

    // === USER ACCOUNT MANAGEMENT ===

    public static boolean registerUser(String email, String password, String nama) {
        for (UserAccount account : state.listUserAccounts) {
            if (account.getEmail().equalsIgnoreCase(email))
                return false;
        }

        UserAccount newAccount = new UserAccount(email, password, UserRole.USER);
        state.listUserAccounts.add(newAccount);

        Profil newProfil = new Profil();
        newProfil.email = email;
        newProfil.nama = nama;
        state.mapProfilByUser.put(email, newProfil);

        saveData();
        return true;
    }

    public static UserAccount loginUser(String email, String password) {
        for (UserAccount account : state.listUserAccounts) {
            if (account.getEmail().equalsIgnoreCase(email) && account.getPassword().equals(password)) {
                currentUserAccount = account;
                return account;
            }
        }
        return null;
    }

    public static void logout() {
        currentUserAccount = null;
    }

    public static UserAccount getCurrentUserAccount() {
        return currentUserAccount;
    }

    public static boolean isCurrentUserAccepted() {
        if (currentUserAccount == null)
            return false;
        return currentUserAccount.isAccepted();
    }

    // === PROFIL MANAGEMENT ===

    public static Profil getProfil() {
        if (currentUserAccount != null) {
            Profil profil = state.mapProfilByUser.get(currentUserAccount.getEmail());
            if (profil != null)
                return profil;
        }
        return state.profilUser;
    }

    public static Profil getCurrentUserProfil() {
        if (currentUserAccount == null)
            return new Profil();

        Profil profil = state.mapProfilByUser.get(currentUserAccount.getEmail());
        if (profil == null) {
            profil = new Profil();
            profil.email = currentUserAccount.getEmail();
            state.mapProfilByUser.put(currentUserAccount.getEmail(), profil);
        }
        return profil;
    }

    public static void updateProfil(Profil profil) {
        if (currentUserAccount != null) {
            state.mapProfilByUser.put(currentUserAccount.getEmail(), profil);
        } else {
            state.profilUser = profil;
        }
        saveData();
    }

    public static void updateCurrentUserProfil(Profil profil) {
        if (currentUserAccount != null) {
            state.mapProfilByUser.put(currentUserAccount.getEmail(), profil);
            saveData();
        }
    }

    // === LOWONGAN MANAGEMENT ===

    public static ObservableList<Lowongan> getAllLowongan() {
        return observableLowongan;
    }

    public static List<Lowongan> getLowonganByDivisi(String divisi) {
        if (divisi == null || divisi.equalsIgnoreCase("Semua") || divisi.isEmpty()) {
            return new ArrayList<>(observableLowongan);
        }
        List<Lowongan> filtered = new ArrayList<>();
        for (Lowongan l : observableLowongan) {
            if (l.divisi.equalsIgnoreCase(divisi))
                filtered.add(l);
        }
        return filtered;
    }

    public static int getTotalLowonganCount() {
        return observableLowongan.size();
    }

    public static int getLowonganTersediaCount() {
        return (int) observableLowongan.stream().filter(l -> l.sisaSlot > 0).count();
    }

    public static Lowongan getLowonganById(String id) {
        return observableLowongan.stream().filter(l -> l.id.equals(id)).findFirst().orElse(null);
    }

    public static boolean isBookmarked(String lowonganId) {
        return state.bookmarkedLowonganIds.contains(lowonganId);
    }

    public static void toggleBookmark(String lowonganId) {
        if (state.bookmarkedLowonganIds.contains(lowonganId))
            state.bookmarkedLowonganIds.remove(lowonganId);
        else
            state.bookmarkedLowonganIds.add(lowonganId);
        saveData();
    }

    public static List<Lowongan> getBookmarkedLowongan() {
        return observableLowongan.stream()
                .filter(l -> state.bookmarkedLowonganIds.contains(l.id))
                .collect(Collectors.toList());
    }

    public static void addLowongan(Lowongan l) {
        observableLowongan.add(l);
        saveData();
    }

    public static void tambahLowongan(Lowongan l) {
        addLowongan(l);
    }

    public static void hapusLowongan(String id) {
        observableLowongan.removeIf(l -> l.id.equals(id));
        observableLamaran.removeIf(lamaran -> lamaran.idLowongan.equals(id));
        state.bookmarkedLowonganIds.remove(id);
        saveData();
    }

    public static void updateLowongan(Lowongan updated) {
        for (int i = 0; i < observableLowongan.size(); i++) {
            if (observableLowongan.get(i).id.equals(updated.id)) {
                observableLowongan.set(i, updated);
                break;
            }
        }
        saveData();
    }

    public static void addLamaran(Lamaran lamaran) {
        observableLamaran.add(lamaran);
        saveData();
    }

    public static ObservableList<Lamaran> getMyLamaran() {
        if (currentUserAccount == null)
            return observableLamaran;

        List<Lamaran> userLamaran = observableLamaran.stream()
                .filter(l -> l.emailPelamar.equals(currentUserAccount.getEmail()))
                .collect(Collectors.toList());
        return FXCollections.observableArrayList(userLamaran);
    }

    public static ObservableList<Lamaran> getAllLamaran() {
        return observableLamaran;
    }

    public static boolean hasUserApplied(String lowonganId) {
        if (currentUserAccount == null)
            return false;
        for (Lamaran lamaran : observableLamaran) {
            if (lamaran.idLowongan.equals(lowonganId) &&
                    lamaran.emailPelamar.equals(currentUserAccount.getEmail())) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasUserApplied(String lowonganId, String userEmail) {
        for (Lamaran lamaran : observableLamaran) {
            if (lamaran.idLowongan.equals(lowonganId) && lamaran.emailPelamar.equals(userEmail)) {
                return true;
            }
        }
        return false;
    }

    public static void updateLamaranStatus(String lamaranId, String newStatus) {
        for (int i = 0; i < observableLamaran.size(); i++) {
            if (observableLamaran.get(i).idLamaran.equals(lamaranId)) {
                Lamaran lamaran = observableLamaran.get(i);
                String oldStatus = lamaran.statusTahapan;
                lamaran.statusTahapan = newStatus;

                if (newStatus.equals("Diterima") && !oldStatus.equals("Diterima")) {
                    for (UserAccount account : state.listUserAccounts) {
                        if (account.getEmail().equals(lamaran.emailPelamar)) {
                            account.setAccepted(true);
                            break;
                        }
                    }
                    decreaseSlot(lamaran.idLowongan);
                }

                if (oldStatus.equals("Diterima") && !newStatus.equals("Diterima")) {
                    for (UserAccount account : state.listUserAccounts) {
                        if (account.getEmail().equals(lamaran.emailPelamar)) {
                            account.setAccepted(false);
                            break;
                        }
                    }
                    Lowongan job = getLowonganById(lamaran.idLowongan);
                    if (job != null && job.sisaSlot != -1) {
                        job.sisaSlot++;
                        saveData();
                    }
                }
                break;
            }
        }
        saveData();
    }

    public static void updateLamaranInterviewDate(String lamaranId, String tanggalInterview) {
        for (int i = 0; i < observableLamaran.size(); i++) {
            if (observableLamaran.get(i).idLamaran.equals(lamaranId)) {
                observableLamaran.get(i).tanggalInterview = tanggalInterview;
                break;
            }
        }
        saveData();
    }

    public static List<String> getAllDivisi() {
        List<String> divisiList = new ArrayList<>();
        divisiList.add("Semua");
        for (Lowongan l : observableLowongan) {
            if (!divisiList.contains(l.divisi))
                divisiList.add(l.divisi);
        }
        return divisiList;
    }

    public static boolean validateAdminLogin(String email, String password) {
        return email.equals("admin@areda.com") && password.equals("admin123");
    }

    public static boolean decreaseSlot(String lowonganId) {
        for (Lowongan l : observableLowongan) {
            if (l.id.equals(lowonganId)) {
                if (l.sisaSlot == -1)
                    return true;
                if (l.sisaSlot > 0) {
                    l.sisaSlot--;
                    saveData();
                    return true;
                }
                return false;
            }
        }
        return false;
    }

    public static void increaseSlot(String lowonganId) {
        for (Lowongan l : observableLowongan) {
            if (l.id.equals(lowonganId)) {
                if (l.sisaSlot == -1)
                    return;
                l.sisaSlot++;
                saveData();
                return;
            }
        }
    }

    public static boolean isLowonganAvailable(String lowonganId) {
        Lowongan l = getLowonganById(lowonganId);
        return l != null && (l.sisaSlot > 0 || l.sisaSlot == -1);
    }

    public static boolean updateLamaranStatusWithSlot(String lamaranId, String newStatus) {
        Lamaran targetLamaran = null;
        String lowonganId = null;
        String oldStatus = null;

        for (int i = 0; i < observableLamaran.size(); i++) {
            if (observableLamaran.get(i).idLamaran.equals(lamaranId)) {
                targetLamaran = observableLamaran.get(i);
                lowonganId = targetLamaran.idLowongan;
                oldStatus = targetLamaran.statusTahapan;
                break;
            }
        }

        if (targetLamaran == null || lowonganId == null)
            return false;

        if (oldStatus.equals("Diterima") && !newStatus.equals("Diterima")) {
            increaseSlot(lowonganId);
            for (UserAccount account : state.listUserAccounts) {
                if (account.getEmail().equals(targetLamaran.emailPelamar)) {
                    account.setAccepted(false);
                    break;
                }
            }
        }

        if (!oldStatus.equals("Diterima") && newStatus.equals("Diterima")) {
            if (!decreaseSlot(lowonganId))
                return false;
            for (UserAccount account : state.listUserAccounts) {
                if (account.getEmail().equals(targetLamaran.emailPelamar)) {
                    account.setAccepted(true);
                    break;
                }
            }
        }

        targetLamaran.statusTahapan = newStatus;
        saveData();
        return true;
    }
}