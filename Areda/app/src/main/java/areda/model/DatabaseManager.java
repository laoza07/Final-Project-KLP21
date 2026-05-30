package areda.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.io.*;
import java.util.*;

public class DatabaseManager {

    private static final String FILE_PATH = "areda_database.ser";
    private static DatabaseState state;

    private static ObservableList<Lowongan> observableLowongan = FXCollections.observableArrayList();
    private static ObservableList<Lamaran> observableLamaran = FXCollections.observableArrayList();

    public static class Lowongan implements Serializable {
        private static final long serialVersionUID = 1L;
        public String id;
        public String divisi;
        public String judul;
        public String deskripsi;
        public List<String> skillDibutuhkan;
        public int sisaSlot;
        public String tanggalDibuat;

        public Lowongan(String id, String divisi, String judul, String deskripsi, List<String> skillDibutuhkan,
                int sisaSlot, String tanggalDibuat) {
            this.id = id;
            this.divisi = divisi;
            this.judul = judul;
            this.deskripsi = deskripsi;
            this.skillDibutuhkan = skillDibutuhkan;
            this.sisaSlot = sisaSlot;
            this.tanggalDibuat = tanggalDibuat;
        }

        public String getId() {
            return id;
        }

        public String getDivisi() {
            return divisi;
        }

        public String getJudul() {
            return judul;
        }

        public String getDeskripsi() {
            return deskripsi;
        }

        public List<String> getSkillDibutuhkan() {
            return skillDibutuhkan;
        }

        public int getSisaSlot() {
            return sisaSlot;
        }

        public void setJudul(String judul) {
            this.judul = judul;
        }

        public void setDivisi(String divisi) {
            this.divisi = divisi;
        }

        public void setSisaSlot(int sisaSlot) {
            this.sisaSlot = sisaSlot;
        }

        public void setDeskripsi(String deskripsi) {
            this.deskripsi = deskripsi;
        }

        public void setSkillDibutuhkan(List<String> skill) {
            this.skillDibutuhkan = skill;
        }
    }

    public static class Profil implements Serializable {
        private static final long serialVersionUID = 1L;
        public String nama = "";
        public String email = "";
        public String telepon = "";
        public String tentangSaya = "";
        public String domisili = "";
        public String tempatLahir = "";
        public String tanggalLahir = "";
        public String jenisKelamin = "";
        public String statusPernikahan = "";
        public String alamat = "";
        public String pendidikan = "";
        public String cvPath = "";
        public String motletPath = "";
        public String photoPath = "";
    }

    public static class Lamaran implements Serializable {
        private static final long serialVersionUID = 1L;
        public String idLamaran;
        public String idLowongan;
        public String namaPelamar;
        public String emailPelamar;
        public String teleponPelamar;
        public String tentangPelamar;
        public String tanggal;
        public String statusTahapan;
        public String cvPath;
        public String motletPath;

        public Lamaran(String idLamaran, String idLowongan, String namaPelamar, String emailPelamar,
                String teleponPelamar, String tentangPelamar, String tanggal, String statusTahapan, String cvPath,
                String motletPath) {
            this.idLamaran = idLamaran;
            this.idLowongan = idLowongan;
            this.namaPelamar = namaPelamar;
            this.emailPelamar = emailPelamar;
            this.teleponPelamar = teleponPelamar;
            this.tentangPelamar = tentangPelamar;
            this.tanggal = tanggal;
            this.statusTahapan = statusTahapan;
            this.cvPath = cvPath;
            this.motletPath = motletPath;
        }
    }

    public static class DatabaseState implements Serializable {
        private static final long serialVersionUID = 1L;
        public List<Lowongan> listLowongan = new ArrayList<>();
        public Profil profilUser = new Profil();
        public List<Lamaran> listLamaran = new ArrayList<>();
        public Set<String> bookmarkedLowonganIds = new HashSet<>();
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

    public static ObservableList<Lowongan> getAllLowongan() {
        return observableLowongan;
    }

    public static List<Lowongan> getLowonganByDivisi(String divisi) {
        if (divisi == null || divisi.equalsIgnoreCase("Semua") || divisi.isEmpty()) {
            return new ArrayList<>(observableLowongan);
        }
        List<Lowongan> filtered = new ArrayList<>();
        for (Lowongan l : observableLowongan) {
            if (l.divisi.equalsIgnoreCase(divisi)) {
                filtered.add(l);
            }
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

    public static Profil getProfil() {
        return state.profilUser;
    }

    public static void updateProfil(Profil p) {
        state.profilUser = p;
        saveData();
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
                .collect(java.util.stream.Collectors.toList());
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
        return observableLamaran;
    }

    public static ObservableList<Lamaran> getAllLamaran() {
        return observableLamaran;
    }

    public static void updateLamaranStatus(String lamaranId, String newStatus) {
        for (int i = 0; i < observableLamaran.size(); i++) {
            if (observableLamaran.get(i).idLamaran.equals(lamaranId)) {
                observableLamaran.get(i).statusTahapan = newStatus;
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

                if (l.sisaSlot == -1) {
                    return true;
                }

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

                if (l.sisaSlot == -1) {
                    return;
                }
                l.sisaSlot++;
                saveData();
                return;
            }
        }
    }

    public static boolean hasUserApplied(String lowonganId, String userEmail) {
        for (Lamaran lamaran : observableLamaran) {
            if (lamaran.idLowongan.equals(lowonganId) && lamaran.emailPelamar.equals(userEmail)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isLowonganAvailable(String lowonganId) {
        Lowongan l = getLowonganById(lowonganId);
        if (l == null) {
            return false;
        }
        return l.sisaSlot > 0 || l.sisaSlot == -1;
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

        if (targetLamaran == null || lowonganId == null) {
            return false;
        }

        if (oldStatus.equals("Diterima") && !newStatus.equals("Diterima")) {
            increaseSlot(lowonganId);
        }

        if (!oldStatus.equals("Diterima") && newStatus.equals("Diterima")) {
            if (!decreaseSlot(lowonganId)) {
                return false;
            }
        }

        targetLamaran.statusTahapan = newStatus;
        saveData();
        return true;
    }
}