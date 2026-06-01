package areda.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;
import java.util.*;

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

        try (Connection conn = DatabaseConnection.getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute(sqlUsers);
            stmt.execute(sqlLowongan);
            stmt.execute(sqlLamaran);
            stmt.execute(sqlProfil);
            stmt.execute(sqlBookmarks);

            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users");
            if (rs.next() && rs.getInt(1) == 0) {
                seedInitialData(conn);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void seedInitialData(Connection conn) {
        try {
            PreparedStatement pstmt = conn
                    .prepareStatement("INSERT INTO users (email, password, role, is_accepted) VALUES (?, ?, ?, ?)");
            pstmt.setString(1, "admin@areda.com");
            pstmt.setString(2, "admin123");
            pstmt.setString(3, "ADMIN");
            pstmt.setInt(4, 0);
            pstmt.executeUpdate();

            String sqlL = "INSERT INTO lowongan (id, divisi, judul, deskripsi, skill_dibutuhkan, sisa_slot, tanggal_dibuat) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pL = conn.prepareStatement(sqlL);

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
        } catch (SQLException e) {
            e.printStackTrace();
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
        l.tanggalInterview = rs.getString("tanggal_interview");
        return l;
    }

    public static boolean isEmailRegistered(String email) {
        String sql = "SELECT 1 FROM users WHERE email = ?";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
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
        Connection conn = DatabaseConnection.getConnection();
        try {
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
                conn.rollback();
                return false;
            }
        } catch (SQLException e) {
            return false;
        }
    }

    public static UserAccount loginUser(String email, String password) {
        String sql = "SELECT * FROM users WHERE email = ? AND password = ?";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
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
            e.printStackTrace();
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
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, currentUserAccount.getEmail());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Profil p = new Profil();
                p.email = safeGet(rs, "email");
                p.nama = safeGet(rs, "nama");
                p.telepon = safeGet(rs, "telepon");
                p.tentangSaya = safeGet(rs, "tentang_saya");
                p.domisili = safeGet(rs, "domisili");
                p.tempatLahir = safeGet(rs, "tempat_lahir");
                p.tanggalLahir = safeGet(rs, "tanggal_lahir");
                p.jenisKelamin = safeGet(rs, "jenis_kelamin");
                p.statusPernikahan = safeGet(rs, "status_pernikahan");
                p.alamat = safeGet(rs, "alamat");
                p.pendidikan = safeGet(rs, "pendidikan");
                p.cvPath = safeGet(rs, "cv_path");
                p.motletPath = safeGet(rs, "motlet_path");
                p.photoPath = safeGet(rs, "photo_path");
                return p;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Profil p = new Profil();
        p.email = currentUserAccount.getEmail();
        return p;
    }

    private static String safeGet(ResultSet rs, String column) throws SQLException {
        String val = rs.getString(column);
        return val == null ? "" : val;
    }

    public static void updateProfil(Profil profil) {
        updateCurrentUserProfil(profil);
    }

    public static void updateCurrentUserProfil(Profil p) {
        if (currentUserAccount == null)
            return;
        String sql = "INSERT OR REPLACE INTO profil (email, nama, telepon, tentang_saya, domisili, tempat_lahir, tanggal_lahir, jenis_kelamin, status_pernikahan, alamat, pendidikan, cv_path, motlet_path, photo_path) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, p.email);
            pstmt.setString(2, safe(p.nama));
            pstmt.setString(3, safe(p.telepon));
            pstmt.setString(4, safe(p.tentangSaya));
            pstmt.setString(5, safe(p.domisili));
            pstmt.setString(6, safe(p.tempatLahir));
            pstmt.setString(7, safe(p.tanggalLahir));
            pstmt.setString(8, safe(p.jenisKelamin));
            pstmt.setString(9, safe(p.statusPernikahan));
            pstmt.setString(10, safe(p.alamat));
            pstmt.setString(11, safe(p.pendidikan));
            pstmt.setString(12, safe(p.cvPath));
            pstmt.setString(13, safe(p.motletPath));
            pstmt.setString(14, safe(p.photoPath));
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static String safe(String s) {
        return s == null ? "" : s;
    }

    public static ObservableList<Lowongan> getAllLowongan() {
        ObservableList<Lowongan> list = FXCollections.observableArrayList();
        Connection conn = DatabaseConnection.getConnection();
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT * FROM lowongan")) {
            while (rs.next())
                list.add(mapLowongan(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static List<Lowongan> getLowonganByDivisi(String divisi) {
        List<Lowongan> list = new ArrayList<>();
        String sql = (divisi == null || divisi.equalsIgnoreCase("Semua")) ? "SELECT * FROM lowongan"
                : "SELECT * FROM lowongan WHERE divisi = ?";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            if (!divisi.equalsIgnoreCase("Semua"))
                pstmt.setString(1, divisi);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next())
                list.add(mapLowongan(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static int getTotalLowonganCount() {
        return getAllLowongan().size();
    }

    public static int getLowonganTersediaCount() {
        return (int) getAllLowongan().stream().filter(l -> l.sisaSlot > 0 || l.sisaSlot == -1).count();
    }

    public static Lowongan getLowonganById(String id) {
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM lowongan WHERE id = ?")) {
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next())
                return mapLowongan(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void addLowongan(Lowongan l) {
        String sql = "INSERT INTO lowongan (id, divisi, judul, deskripsi, skill_dibutuhkan, sisa_slot, tanggal_dibuat) VALUES (?, ?, ?, ?, ?, ?, ?)";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, l.id);
            pstmt.setString(2, l.divisi);
            pstmt.setString(3, l.judul);
            pstmt.setString(4, l.deskripsi);
            pstmt.setString(5, String.join(",", l.skillDibutuhkan));
            pstmt.setInt(6, l.sisaSlot);
            pstmt.setString(7, l.tanggalDibuat);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void tambahLowongan(Lowongan l) {
        addLowongan(l);
    }

    public static void updateLowongan(Lowongan l) {
        String sql = "UPDATE lowongan SET divisi=?, judul=?, deskripsi=?, skill_dibutuhkan=?, sisa_slot=? WHERE id=?";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, l.divisi);
            pstmt.setString(2, l.judul);
            pstmt.setString(3, l.deskripsi);
            pstmt.setString(4, String.join(",", l.skillDibutuhkan));
            pstmt.setInt(5, l.sisaSlot);
            pstmt.setString(6, l.id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void hapusLowongan(String id) {
        Connection conn = DatabaseConnection.getConnection();
        try {
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
                conn.rollback();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<String> getAllDivisi() {
        List<String> divisiList = new ArrayList<>();
        divisiList.add("Semua");
        Connection conn = DatabaseConnection.getConnection();
        try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT DISTINCT divisi FROM lowongan")) {
            while (rs.next())
                divisiList.add(rs.getString("divisi"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return divisiList;
    }

    public static boolean isBookmarked(String lowonganId) {
        if (currentUserAccount == null)
            return false;
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement pstmt = conn
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
        Connection conn = DatabaseConnection.getConnection();
        if (isBookmarked(lowonganId)) {
            try (PreparedStatement pstmt = conn
                    .prepareStatement("DELETE FROM bookmarks WHERE email = ? AND id_lowongan = ?")) {
                pstmt.setString(1, email);
                pstmt.setString(2, lowonganId);
                pstmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            try (PreparedStatement pstmt = conn
                    .prepareStatement("INSERT INTO bookmarks (email, id_lowongan) VALUES (?, ?)")) {
                pstmt.setString(1, email);
                pstmt.setString(2, lowonganId);
                pstmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static List<Lowongan> getBookmarkedLowongan() {
        List<Lowongan> list = new ArrayList<>();
        if (currentUserAccount == null)
            return list;
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(
                "SELECT l.* FROM lowongan l INNER JOIN bookmarks b ON l.id = b.id_lowongan WHERE b.email = ?")) {
            pstmt.setString(1, currentUserAccount.getEmail());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next())
                list.add(mapLowongan(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static void addLamaran(Lamaran l) {
        String sql = "INSERT INTO lamaran VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, l.idLamaran);
            pstmt.setString(2, l.idLowongan);
            pstmt.setString(3, l.namaPelamar);
            pstmt.setString(4, l.emailPelamar);
            pstmt.setString(5, l.teleponPelamar);
            pstmt.setString(6, l.tentangPelamar);
            pstmt.setString(7, l.tanggal);
            pstmt.setString(8, l.statusTahapan);
            pstmt.setString(9, l.cvPath);
            pstmt.setString(10, l.motletPath);
            pstmt.setString(11, l.tanggalInterview);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static ObservableList<Lamaran> getAllLamaran() {
        ObservableList<Lamaran> list = FXCollections.observableArrayList();
        Connection conn = DatabaseConnection.getConnection();
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT * FROM lamaran")) {
            while (rs.next())
                list.add(mapLamaran(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static ObservableList<Lamaran> getMyLamaran() {
        ObservableList<Lamaran> list = FXCollections.observableArrayList();
        if (currentUserAccount == null)
            return list;
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM lamaran WHERE email_pelamar = ?")) {
            pstmt.setString(1, currentUserAccount.getEmail());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next())
                list.add(mapLamaran(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static boolean hasUserApplied(String lowonganId) {
        return currentUserAccount != null && hasUserApplied(lowonganId, currentUserAccount.getEmail());
    }

    public static boolean hasUserApplied(String lowonganId, String userEmail) {
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement pstmt = conn
                .prepareStatement("SELECT 1 FROM lamaran WHERE id_lowongan = ? AND email_pelamar = ?")) {
            pstmt.setString(1, lowonganId);
            pstmt.setString(2, userEmail);
            return pstmt.executeQuery().next();
        } catch (SQLException e) {
            return false;
        }
    }

    public static void updateLamaranInterviewDate(String lamaranId, String tgl) {
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement pstmt = conn
                .prepareStatement("UPDATE lamaran SET tanggal_interview = ? WHERE id_lamaran = ?")) {
            pstmt.setString(1, tgl);
            pstmt.setString(2, lamaranId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateLamaranStatus(String lamaranId, String newStatus) {
        Connection conn = DatabaseConnection.getConnection();
        try {
            conn.setAutoCommit(false);
            Lamaran lamaran = null;
            try (PreparedStatement p = conn.prepareStatement("SELECT * FROM lamaran WHERE id_lamaran = ?")) {
                p.setString(1, lamaranId);
                ResultSet rs = p.executeQuery();
                if (rs.next())
                    lamaran = mapLamaran(rs);
            }
            if (lamaran != null) {
                String oldStatus = lamaran.statusTahapan;
                try (PreparedStatement p = conn
                        .prepareStatement("UPDATE lamaran SET status_tahapan = ? WHERE id_lamaran = ?")) {
                    p.setString(1, newStatus);
                    p.setString(2, lamaranId);
                    p.executeUpdate();
                }
                if (newStatus.equals("Diterima") && !oldStatus.equals("Diterima")) {
                    try (PreparedStatement p = conn
                            .prepareStatement("UPDATE users SET is_accepted = 1 WHERE email = ?")) {
                        p.setString(1, lamaran.emailPelamar);
                        p.executeUpdate();
                    }
                    decreaseSlotTx(conn, lamaran.idLowongan);
                }
                if (oldStatus.equals("Diterima") && !newStatus.equals("Diterima")) {
                    try (PreparedStatement p = conn
                            .prepareStatement("UPDATE users SET is_accepted = 0 WHERE email = ?")) {
                        p.setString(1, lamaran.emailPelamar);
                        p.executeUpdate();
                    }
                    increaseSlotTx(conn, lamaran.idLowongan);
                }
            }
            conn.commit();
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        }
    }

    public static boolean updateLamaranStatusWithSlot(String lamaranId, String newStatus) {
        updateLamaranStatus(lamaranId, newStatus);
        return true;
    }

    private static void decreaseSlotTx(Connection conn, String lowonganId) throws SQLException {
        Lowongan l = getLowonganById(lowonganId);
        if (l != null && l.sisaSlot > 0) {
            try (PreparedStatement p = conn
                    .prepareStatement("UPDATE lowongan SET sisa_slot = sisa_slot - 1 WHERE id = ?")) {
                p.setString(1, lowonganId);
                p.executeUpdate();
            }
        }
    }

    private static void increaseSlotTx(Connection conn, String lowonganId) throws SQLException {
        Lowongan l = getLowonganById(lowonganId);
        if (l != null && l.sisaSlot != -1) {
            try (PreparedStatement p = conn
                    .prepareStatement("UPDATE lowongan SET sisa_slot = sisa_slot + 1 WHERE id = ?")) {
                p.setString(1, lowonganId);
                p.executeUpdate();
            }
        }
    }

    public static boolean decreaseSlot(String lowonganId) {
        Connection conn = DatabaseConnection.getConnection();
        try {
            decreaseSlotTx(conn, lowonganId);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public static void increaseSlot(String lowonganId) {
        Connection conn = DatabaseConnection.getConnection();
        try {
            increaseSlotTx(conn, lowonganId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean isLowonganAvailable(String lowonganId) {
        Lowongan l = getLowonganById(lowonganId);
        return l != null && (l.sisaSlot > 0 || l.sisaSlot == -1);
    }
}