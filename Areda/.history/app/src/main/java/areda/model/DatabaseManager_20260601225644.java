package areda.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DatabaseManager {
    private static UserAccount currentUserAccount;

    static {
        initializeDatabase();
    }

    private static void initializeDatabase() {
        String sqlUsers = "CREATE TABLE IF NOT EXISTS users (email TEXT PRIMARY KEY, password TEXT, role TEXT, is_accepted INTEGER DEFAULT 0)";
        String sqlLowongan = "CREATE TABLE IF NOT EXISTS lowongan (id TEXT PRIMARY KEY, divisi TEXT, judul TEXT, deskripsi TEXT, skill_dibutuhkan TEXT, sisa_slot INTEGER, tanggal_dibuat TEXT)";
        String sqlLamaran = "CREATE TABLE IF NOT EXISTS lamaran (id_lamaran TEXT PRIMARY KEY, id_lowongan TEXT, nama_pelamar TEXT, email_pelamar TEXT, telepon_pelamar TEXT, tentang_pelamar TEXT, tanggal TEXT, status_tahapan TEXT, cv_path TEXT, motlet_path TEXT, tanggal_interview TEXT)";
        String sqlProfil = "CREATE TABLE IF NOT EXISTS profil (email TEXT PRIMARY KEY, nama TEXT, telepon TEXT, tentang_saya TEXT, domisili TEXT, tempat_lahir TEXT, tanggal_lahir TEXT, jenis_kelamin TEXT, status_pernikahan TEXT, alamat TEXT, pendidikan TEXT, cv_path TEXT, motlet_path TEXT, photo_path TEXT)";
        String sqlBookmarks = "CREATE TABLE IF NOT EXISTS bookmarks (email TEXT, id_lowongan TEXT, PRIMARY KEY (email, id_lowongan))";

        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement()) {

            stmt.execute(sqlUsers);
            stmt.execute(sqlLowongan);
            stmt.execute(sqlLamaran);
            stmt.execute(sqlProfil);
            stmt.execute(sqlBookmarks);

            ResultSet rsUser = stmt.executeQuery("SELECT COUNT(*) FROM users");
            if (rsUser.next() && rsUser.getInt(1) == 0) {
                seedAdminData(conn);
            }
            ResultSet rsLowongan = stmt.executeQuery("SELECT COUNT(*) FROM lowongan");
            if (rsLowongan.next() && rsLowongan.getInt(1) == 0) {
                seedLowonganData(conn);
            }
        } catch (SQLException e) {
            System.err.println("Gagal inisialisasi database: " + e.getMessage());
        }
    }

    private static void seedAdminData(Connection conn) throws SQLException {
        try (PreparedStatement pstmt = conn.prepareStatement(
                "INSERT INTO users (email, password, role, is_accepted) VALUES (?, ?, ?, ?)")) {
            pstmt.setString(1, "admin@areda.com");
            pstmt.setString(2, "admin123");
            pstmt.setString(3, "ADMIN");
            pstmt.setInt(4, 0);
            pstmt.executeUpdate();
        }
    }

    private static void seedLowonganData(Connection conn) throws SQLException {
        String sqlL = "INSERT INTO lowongan (id, divisi, judul, deskripsi, skill_dibutuhkan, sisa_slot, tanggal_dibuat) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pL = conn.prepareStatement(sqlL)) {
            addSeedLowongan(pL, "MKT-01", "Marketing", "Social Media Specialist",
                    "Bertanggung jawab mengelola seluruh akun media sosial perusahaan, membuat konten kreatif, meningkatkan engagement audiens, dan menganalisis performa kampanye digital.",
                    "Social Media Management,Content Planning,Copywriting,Meta Business Suite,Canva / Adobe Photoshop",
                    2, "10 Mei 2026");

            addSeedLowongan(pL, "MKT-02", "Marketing", "Content Creator",
                    "Membuat konten visual dan tulisan yang kreatif untuk berbagai platform digital, berkolaborasi dengan tim marketing, dan memastikan konten sesuai brand guideline.",
                    "Canva / Adobe Illustrator,Adobe Premiere,Photography & Videography,Creative Writing", 5,
                    "12 Mei 2026");

            addSeedLowongan(pL, "MKT-03", "Marketing", "Digital Marketing Staff",
                    "Merancang dan menjalankan strategi iklan digital di Google Ads dan Meta Ads, melakukan optimasi SEO/SEM, serta melaporkan ROI kampanye.",
                    "Google Ads,Meta Ads,SEO & SEM,Google Analytics", 0, "15 Mei 2026");

            addSeedLowongan(pL, "IT-01", "Teknologi Informasi", "Frontend Developer",
                    "Mengembangkan tampilan website yang responsif dan user-friendly menggunakan React.js, berkolaborasi dengan UI/UX designer, dan memastikan performa optimal.",
                    "HTML,CSS,JavaScript,React.js,Git", 3, "01 Mei 2026");

            addSeedLowongan(pL, "IT-02", "Teknologi Informasi", "Backend Developer",
                    "Membangun sistem backend yang scalable, mengelola database MySQL, mengembangkan API RESTful, dan memastikan keamanan aplikasi.",
                    "PHP,Node.js,MySQL,REST API,Git", 2, "05 Mei 2026");

            addSeedLowongan(pL, "IT-03", "Teknologi Informasi", "UI/UX Designer",
                    "Merancang wireframe dan prototipe aplikasi, melakukan user research, dan memastikan pengalaman pengguna yang intuitif dan menyenangkan.",
                    "Figma,Wireframing,Prototyping,User Research,Adobe XD", 1, "08 Mei 2026");

            addSeedLowongan(pL, "IT-04", "Teknologi Informasi", "QA Tester",
                    "Membuat test case, melakukan pengujian manual dan otomatis, melaporkan bug, dan memastikan kualitas produk sebelum rilis.",
                    "Manual Testing,Test Case Writing,Bug Tracking,Selenium", 1, "18 Mei 2026");

            addSeedLowongan(pL, "HR-01", "Human Resource", "HR Staff",
                    "Mengelola administrasi SDM, data karyawan, proses payroll, dan mendukung program pengembangan karyawan.",
                    "Microsoft Excel,HRIS,Payroll System,Communication Skills", 4, "20 Mei 2026");

            addSeedLowongan(pL, "HR-02", "Human Resource", "Recruitment Staff",
                    "Melakukan screening CV, menjadwalkan interview, berkoordinasi dengan user, dan memastikan proses rekrutmen berjalan efektif.",
                    "CV Screening,ATS,Interview Scheduling,LinkedIn Recruiter", 1, "21 Mei 2026");

            addSeedLowongan(pL, "FIN-01", "Keuangan", "Finance Staff",
                    "Monitoring arus kas, menyusun laporan keuangan bulanan, dan mendukung proses audit internal.",
                    "Microsoft Excel,Financial Reporting,Accurate Software", 2, "22 Mei 2026");

            addSeedLowongan(pL, "FIN-02", "Keuangan", "Accounting Staff",
                    "Pencatatan jurnal harian, rekonsiliasi bank, pembukuan akurat, dan penyusunan laporan keuangan sesuai standar akuntansi.",
                    "Journal Entry,Accurate / SAP,Financial Statement", 0, "25 Mei 2026");

            addSeedLowongan(pL, "OPS-01", "Operasional", "Administrasi",
                    "Pengelolaan dokumen perusahaan, data entry, pengarsipan, dan mendukung kelancaran operasional harian.",
                    "Microsoft Office,Data Entry,Document Management", 3, "26 Mei 2026");

            addSeedLowongan(pL, "OPS-02", "Operasional", "Customer Service",
                    "Melayani kebutuhan dan keluhan pelanggan melalui berbagai channel, mencatat feedback, dan memastikan kepuasan pelanggan.",
                    "CRM Software,Ticketing System,Communication Skills,Problem Solving", 2, "28 Mei 2026");
        }
    }

    private static void addSeedLowongan(PreparedStatement p, String id, String div, String jud, String desk,
            String skill, int slot, String tgl) throws SQLException {
        p.setString(1, id);
        p.setString(2, div);
        p.setString(3, jud);
        p.setString(4, desk);
        p.setString(5, skill);
        p.setInt(6, slot);
        p.setString(7, tgl);
        p.executeUpdate();
    }

    private static Lowongan mapLowongan(ResultSet rs) throws SQLException {
        String skillsStr = rs.getString("skill_dibutuhkan");
        List<String> skills = (skillsStr == null || skillsStr.isEmpty()) ? new ArrayList<>()
                : Arrays.asList(skillsStr.split("\\s*,\\s*"));
        return new Lowongan(rs.getString("id"), rs.getString("divisi"), rs.getString("judul"),
                rs.getString("deskripsi"), skills, rs.getInt("sisa_slot"), rs.getString("tanggal_dibuat"));
    }

    private static Lamaran mapLamaran(ResultSet rs) throws SQLException {
        Lamaran l = new Lamaran(rs.getString("id_lamaran"), rs.getString("id_lowongan"), rs.getString("nama_pelamar"),
                rs.getString("email_pelamar"), rs.getString("telepon_pelamar"), rs.getString("tentang_pelamar"),
                rs.getString("tanggal"), rs.getString("status_tahapan"), rs.getString("cv_path"),
                rs.getString("motlet_path"));
        l.setTanggalInterview(rs.getString("tanggal_interview"));
        return l;
    }

    public static boolean isEmailRegistered(String email) {
        String sql = "SELECT 1 FROM users WHERE email = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            return pstmt.executeQuery().next();
        } catch (SQLException e) {
            return false;
        }
    }

    public static boolean registerUser(String email, String password, String nama) {
        if (isEmailRegistered(email))
            return false;
        String sqlUser = "INSERT INTO users (email, password, role, is_accepted) VALUES (?, ?, 'USER', 0)";
        String sqlProfil = "INSERT INTO profil (email, nama) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement p1 = conn.prepareStatement(sqlUser);
                    PreparedStatement p2 = conn.prepareStatement(sqlProfil)) {
                p1.setString(1, email);
                p1.setString(2, password);
                p1.executeUpdate();
                p2.setString(1, email);
                p2.setString(2, nama);
                p2.executeUpdate();
                conn.commit();
                return true;
            } catch (SQLException e) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                }
                return false;
            }
        } catch (SQLException e) {
            return false;
        }
    }

    public static UserAccount loginUser(String email, String password) {
        String sql = "SELECT * FROM users WHERE email = ? AND password = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                currentUserAccount = new UserAccount(rs.getString("email"), rs.getString("password"),
                        rs.getString("role"));
                currentUserAccount.setAccepted(rs.getInt("is_accepted") == 1);
                return currentUserAccount;
            }
        } catch (SQLException e) {
            System.err.println("Error login: " + e.getMessage());
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
        return currentUserAccount != null && currentUserAccount.isAccepted();
    }

    public static boolean validateAdminLogin(String email, String password) {
        return email.equals("admin@areda.com") && password.equals("admin123");
    }

    public static Profil getProfil() {
        return getCurrentUserProfil();
    }

    public static Profil getCurrentUserProfil() {
        if (currentUserAccount == null)
            return new Profil();
        String sql = "SELECT * FROM profil WHERE email = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, currentUserAccount.getEmail());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next())
                return mapProfil(rs);
        } catch (SQLException e) {
            System.err.println("Error get profil: " + e.getMessage());
        }
        Profil p = new Profil();
        p.setEmail(currentUserAccount.getEmail());
        return p;
    }

    private static Profil mapProfil(ResultSet rs) throws SQLException {
        Profil p = new Profil();
        p.setEmail(safeGet(rs, "email"));
        p.setNama(safeGet(rs, "nama"));
        p.setTelepon(safeGet(rs, "telepon"));
        p.setTentangSaya(safeGet(rs, "tentang_saya"));
        p.setDomisili(safeGet(rs, "domisili"));
        p.setTempatLahir(safeGet(rs, "tempat_lahir"));
        p.setTanggalLahir(safeGet(rs, "tanggal_lahir"));
        p.setJenisKelamin(safeGet(rs, "jenis_kelamin"));
        p.setStatusPernikahan(safeGet(rs, "status_pernikahan"));
        p.setAlamat(safeGet(rs, "alamat"));
        p.setPendidikan(safeGet(rs, "pendidikan"));
        p.setCvPath(safeGet(rs, "cv_path"));
        p.setMotletPath(safeGet(rs, "motlet_path"));
        p.setPhotoPath(safeGet(rs, "photo_path"));
        return p;
    }

    private static String safeGet(ResultSet rs, String column) throws SQLException {
        String val = rs.getString(column);
        return val == null ? "" : val;
    }

    public static void updateCurrentUserProfil(Profil p) {
        if (currentUserAccount == null)
            return;
        String sql = "INSERT OR REPLACE INTO profil (email, nama, telepon, tentang_saya, domisili, tempat_lahir, tanggal_lahir, jenis_kelamin, status_pernikahan, alamat, pendidikan, cv_path, motlet_path, photo_path) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, p.getEmail());
            pstmt.setString(2, safe(p.getNama()));
            pstmt.setString(3, safe(p.getTelepon()));
            pstmt.setString(4, safe(p.getTentangSaya()));
            pstmt.setString(5, safe(p.getDomisili()));
            pstmt.setString(6, safe(p.getTempatLahir()));
            pstmt.setString(7, safe(p.getTanggalLahir()));
            pstmt.setString(8, safe(p.getJenisKelamin()));
            pstmt.setString(9, safe(p.getStatusPernikahan()));
            pstmt.setString(10, safe(p.getAlamat()));
            pstmt.setString(11, safe(p.getPendidikan()));
            pstmt.setString(12, safe(p.getCvPath()));
            pstmt.setString(13, safe(p.getMotletPath()));
            pstmt.setString(14, safe(p.getPhotoPath()));
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error update profil: " + e.getMessage());
        }
    }

    private static String safe(String s) {
        return s == null ? "" : s;
    }

    public static ObservableList<Lowongan> getAllLowongan() {
        ObservableList<Lowongan> list = FXCollections.observableArrayList();
        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM lowongan")) {
            while (rs.next())
                list.add(mapLowongan(rs));
        } catch (SQLException e) {
            System.err.println("Error get all lowongan: " + e.getMessage());
        }
        return list;
    }

    public static List<Lowongan> getLowonganByDivisi(String divisi) {
        List<Lowongan> list = new ArrayList<>();
        String sql = (divisi == null || divisi.equalsIgnoreCase("Semua")) ? "SELECT * FROM lowongan"
                : "SELECT * FROM lowongan WHERE divisi = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            if (!divisi.equalsIgnoreCase("Semua"))
                pstmt.setString(1, divisi);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next())
                list.add(mapLowongan(rs));
        } catch (SQLException e) {
            System.err.println("Error get by divisi: " + e.getMessage());
        }
        return list;
    }

    public static int getTotalLowonganCount() {
        return getAllLowongan().size();
    }

    public static int getLowonganTersediaCount() {
        return (int) getAllLowongan().stream().filter(l -> l.getSisaSlot() > 0).count();
    }

    public static Lowongan getLowonganById(String id) {
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM lowongan WHERE id = ?")) {
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next())
                return mapLowongan(rs);
        } catch (SQLException e) {
            System.err.println("Error get by id: " + e.getMessage());
        }
        return null;
    }

    public static void addLowongan(Lowongan l) {
        String sql = "INSERT INTO lowongan (id, divisi, judul, deskripsi, skill_dibutuhkan, sisa_slot, tanggal_dibuat) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, l.getId());
            pstmt.setString(2, l.getDivisi());
            pstmt.setString(3, l.getJudul());
            pstmt.setString(4, l.getDeskripsi());
            pstmt.setString(5, String.join(", ", l.getSkillDibutuhkan()));
            pstmt.setInt(6, l.getSisaSlot());
            pstmt.setString(7, l.getTanggalDibuat());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error add lowongan: " + e.getMessage());
        }
    }

    public static void updateLowongan(Lowongan l) {
        String sql = "UPDATE lowongan SET divisi=?, judul=?, deskripsi=?, skill_dibutuhkan=?, sisa_slot=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, l.getDivisi());
            pstmt.setString(2, l.getJudul());
            pstmt.setString(3, l.getDeskripsi());
            pstmt.setString(4, String.join(", ", l.getSkillDibutuhkan()));
            pstmt.setInt(5, l.getSisaSlot());
            pstmt.setString(6, l.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error update lowongan: " + e.getMessage());
        }
    }

    public static void hapusLowongan(String id) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement p1 = conn.prepareStatement("DELETE FROM lamaran WHERE id_lowongan = ?");
                    PreparedStatement p2 = conn.prepareStatement("DELETE FROM bookmarks WHERE id_lowongan = ?");
                    PreparedStatement p3 = conn.prepareStatement("DELETE FROM lowongan WHERE id = ?")) {
                p1.setString(1, id);
                p1.executeUpdate();
                p2.setString(1, id);
                p2.executeUpdate();
                p3.setString(1, id);
                p3.executeUpdate();
                conn.commit();
            } catch (SQLException e) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                }
                System.err.println("Error delete lowongan: " + e.getMessage());
            }
        } catch (SQLException e) {
            System.err.println("Error connection delete: " + e.getMessage());
        }
    }

    public static List<String> getAllDivisi() {
        List<String> divisiList = new ArrayList<>();
        divisiList.add("Semua");
        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT DISTINCT divisi FROM lowongan")) {
            while (rs.next())
                divisiList.add(rs.getString("divisi"));
        } catch (SQLException e) {
            System.err.println("Error get divisi: " + e.getMessage());
        }
        return divisiList;
    }

    public static boolean isBookmarked(String lowonganId) {
        if (currentUserAccount == null)
            return false;
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn
                        .prepareStatement("SELECT 1 FROM bookmarks WHERE email = ? AND id_lowongan = ?")) {
            pstmt.setString(1, currentUserAccount.getEmail());
            pstmt.setString(2, lowonganId);
            return pstmt.executeQuery().next();
        } catch (SQLException e) {
            return false;
        }
    }

    public static void toggleBookmark(String lowonganId) {
        if (currentUserAccount == null)
            return;
        String email = currentUserAccount.getEmail();
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (isBookmarked(lowonganId)) {
                try (PreparedStatement pstmt = conn
                        .prepareStatement("DELETE FROM bookmarks WHERE email = ? AND id_lowongan = ?")) {
                    pstmt.setString(1, email);
                    pstmt.setString(2, lowonganId);
                    pstmt.executeUpdate();
                }
            } else {
                try (PreparedStatement pstmt = conn
                        .prepareStatement("INSERT INTO bookmarks (email, id_lowongan) VALUES (?, ?)")) {
                    pstmt.setString(1, email);
                    pstmt.setString(2, lowonganId);
                    pstmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            System.err.println("Error toggle bookmark: " + e.getMessage());
        }
    }

    public static List<Lowongan> getBookmarkedLowongan() {
        List<Lowongan> list = new ArrayList<>();
        if (currentUserAccount == null)
            return list;
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(
                        "SELECT l.* FROM lowongan l INNER JOIN bookmarks b ON l.id = b.id_lowongan WHERE b.email = ?")) {
            pstmt.setString(1, currentUserAccount.getEmail());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next())
                list.add(mapLowongan(rs));
        } catch (SQLException e) {
            System.err.println("Error get bookmarks: " + e.getMessage());
        }
        return list;
    }

    public static void addLamaran(Lamaran l) {
        String sql = "INSERT INTO lamaran VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, l.getIdLamaran());
            pstmt.setString(2, l.getIdLowongan());
            pstmt.setString(3, l.getNamaPelamar());
            pstmt.setString(4, l.getEmailPelamar());
            pstmt.setString(5, l.getTeleponPelamar());
            pstmt.setString(6, l.getTentangPelamar());
            pstmt.setString(7, l.getTanggal());
            pstmt.setString(8, l.getStatusTahapan());
            pstmt.setString(9, l.getCvPath());
            pstmt.setString(10, l.getMotletPath());
            pstmt.setString(11, l.getTanggalInterview());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error add lamaran: " + e.getMessage());
        }
    }

    public static ObservableList<Lamaran> getAllLamaran() {
        ObservableList<Lamaran> list = FXCollections.observableArrayList();
        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM lamaran")) {
            while (rs.next())
                list.add(mapLamaran(rs));
        } catch (SQLException e) {
            System.err.println("Error get all lamaran: " + e.getMessage());
        }
        return list;
    }

    public static ObservableList<Lamaran> getMyLamaran() {
        ObservableList<Lamaran> list = FXCollections.observableArrayList();
        if (currentUserAccount == null)
            return list;
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM lamaran WHERE email_pelamar = ?")) {
            pstmt.setString(1, currentUserAccount.getEmail());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next())
                list.add(mapLamaran(rs));
        } catch (SQLException e) {
            System.err.println("Error get my lamaran: " + e.getMessage());
        }
        return list;
    }

    public static boolean hasUserApplied(String lowonganId) {
        return currentUserAccount != null && hasUserApplied(lowonganId, currentUserAccount.getEmail());
    }

    public static boolean hasUserApplied(String lowonganId, String userEmail) {
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn
                        .prepareStatement("SELECT 1 FROM lamaran WHERE id_lowongan = ? AND email_pelamar = ?")) {
            pstmt.setString(1, lowonganId);
            pstmt.setString(2, userEmail);
            return pstmt.executeQuery().next();
        } catch (SQLException e) {
            return false;
        }
    }

    public static void updateLamaranInterviewDate(String lamaranId, String tgl) {
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn
                        .prepareStatement("UPDATE lamaran SET tanggal_interview = ? WHERE id_lamaran = ?")) {
            pstmt.setString(1, tgl);
            pstmt.setString(2, lamaranId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error update interview date: " + e.getMessage());
        }
    }

    public static void updateLamaranStatus(String lamaranId, String newStatus) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            Lamaran lamaran = null;
            try (PreparedStatement p = conn.prepareStatement("SELECT * FROM lamaran WHERE id_lamaran = ?")) {
                p.setString(1, lamaranId);
                try (ResultSet rs = p.executeQuery()) {
                    if (rs.next())
                        lamaran = mapLamaran(rs);
                }
            }

            if (lamaran == null) {
                conn.rollback();
                System.err.println("Lamaran tidak ditemukan: " + lamaranId);
                return;
            }

            String oldStatus = lamaran.getStatusTahapan();
            String lowonganId = lamaran.getIdLowongan();
            String emailPelamar = lamaran.getEmailPelamar();

            try (PreparedStatement p = conn
                    .prepareStatement("UPDATE lamaran SET status_tahapan = ? WHERE id_lamaran = ?")) {
                p.setString(1, newStatus);
                p.setString(2, lamaranId);
                p.executeUpdate();
            }

            if (!oldStatus.equals(newStatus)) {
                if ("Diterima".equals(newStatus)) {
                    Lowongan l = getLowonganById(lowonganId);

                    if (l != null && l.getSisaSlot() > 0) {
                        try (PreparedStatement p = conn
                                .prepareStatement("UPDATE lowongan SET sisa_slot = sisa_slot - 1 WHERE id = ?")) {
                            p.setString(1, lowonganId);
                            p.executeUpdate();
                        }
                    }
                    try (PreparedStatement p = conn
                            .prepareStatement("UPDATE users SET is_accepted = 1 WHERE email = ?")) {
                        p.setString(1, emailPelamar);
                        p.executeUpdate();
                    }
                } else if ("Diterima".equals(oldStatus)) {

                    try (PreparedStatement p = conn
                            .prepareStatement("UPDATE lowongan SET sisa_slot = sisa_slot + 1 WHERE id = ?")) {
                        p.setString(1, lowonganId);
                        p.executeUpdate();
                    }
                    try (PreparedStatement p = conn
                            .prepareStatement("UPDATE users SET is_accepted = 0 WHERE email = ?")) {
                        p.setString(1, emailPelamar);
                        p.executeUpdate();
                    }
                }
            }

            conn.commit();

        } catch (SQLException e) {
            System.err.println("Error update status: " + e.getMessage());

        }
    }

    public static boolean isLowonganAvailable(String lowonganId) {
        Lowongan l = getLowonganById(lowonganId);
        return l != null && l.getSisaSlot() > 0;
    }
}