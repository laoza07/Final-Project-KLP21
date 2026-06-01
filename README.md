# Final-Project-KLP21
# 🚀 Areda Careers
> **"Temukan Peluang, Bangun Masa Depan"**

[![Java](https://img.shields.io/badge/Language-Java-orange.svg)](https://www.oracle.com/java/)
[![JavaFX](https://img.shields.io/badge/UI-JavaFX-blue.svg)](https://openjfx.io/)
[![Gradle](https://img.shields.io/badge/Build-Gradle-green.svg)](https://gradle.org/)
[![SQLite](https://img.shields.io/badge/Database-SQLite-003B57.svg)](https://www.sqlite.org/)

---

## 📝 Deskripsi Proyek
**Areda Careers** adalah aplikasi rekrutmen kerja berbasis desktop yang dibangun menggunakan **JavaFX**. Platform ini dirancang untuk menyederhanakan interaksi antara perusahaan (Admin) dan pencari kerja (Pelamar) dalam satu ekosistem yang efisien. Mulai dari manajemen lowongan hingga pemantauan status lamaran secara *real-time*, Areda Careers adalah solusi modern untuk kebutuhan karier Anda.

> ### ✨ Filosofi Nama
> **AREDA** merupakan akronim dari nama para pendiri proyek ini: **A**yla, **RE**za, dan Ad**DAH**. Nama ini melambangkan semangat kolaborasi tim dalam menciptakan jembatan bagi para profesional untuk menemukan peluang karier yang gemilang.

---

## 🛠️ Teknologi yang Digunakan
Aplikasi ini memanfaatkan teknologi modern Java untuk memastikan performa yang stabil dan antarmuka yang menarik:
*   **Java**: Bahasa pemrograman utama.
*   **JavaFX**: Framework untuk antarmuka pengguna (GUI) yang kaya.
*   **Gradle**: Tool manajemen proyek dan otomatisasi *build*.
*   **SQLite**: Database ringan untuk penyimpanan data lokal yang efisien.
*   **CSS**: Digunakan untuk kustomisasi *styling* antarmuka agar terlihat modern.

---

## 🌟 Fitur Utama

### 🔑 Autentikasi
*   **Login Multi-Role**: Sistem pintar yang membedakan hak akses dan antarmuka antara **Admin** dan **Pelamar**.

### 💼 Panel Admin (Recruiter Mode)
*   **Dashboard Statistik**: Pantau total lowongan aktif dan jumlah lamaran masuk secara cepat.
*   **Manajemen Lowongan (CRUD)**: Publikasikan, edit, atau hapus lowongan pekerjaan dengan mudah.
*   **Manajemen Pelamar**: Tinjau detail profil kandidat dan ubah status lamaran (*Interview*, Diterima, atau Ditolak).

### 👤 Panel Pelamar (Job Seeker Mode)
*   **Eksplorasi Lowongan**: Cari pekerjaan yang sesuai, baca detail deskripsi, dan simpan ke **Markah** (Bookmark).
*   **Pengajuan Lamaran**: Lamar pekerjaan langsung melalui aplikasi dengan unggahan CV dan *motivation letter*.
*   **Real-time Tracking**: Pantau perkembangan status lamaran Anda dari tahap awal hingga hasil akhir.
*   **Manajemen Profil**: Kelola data pribadi dan dokumen pendukung rekrutmen.

---

## 📁 Struktur Kode
Aplikasi ini mengikuti pola struktur yang terorganisir untuk memudahkan pengembangan:

```text
src
└── main
    └── java
        └── areda
            ├── model        # Logika data dan pengelolaan database (SQLite)
            ├── view         # Komponen antarmuka pengguna
            │   ├── admin    # Fitur khusus Role Admin
            │   ├── auth     # Fitur login dan registrasi
            │   ├── base     # Interface dan class dasar
            │   ├── components # UI Components (reusable)
            │   ├── layout   # Template dashboard utama
            │   └── user     # Fitur khusus Role Pelamar
            └── MainApp.java # Entry point aplikasi
```

---

## 🧩 Penerapan Konsep OOP
Proyek ini mengimplementasikan prinsip Pemrograman Berorientasi Objek (OOP) untuk menjaga kode tetap bersih dan mudah dipelihara:

### 1. Encapsulation (Enkapsulasi)
Melindungi integritas data dengan menggunakan access modifier `private` dan menyediakan akses melalui getter/setter.
```java
private String email, password, role;
private boolean isAccepted;
```

### 2. Inheritance (Pewarisan)
Memanfaatkan penggunaan kembali kode dengan mewarisi sifat dari class induk ke class turunan.
```java
public class AdminDashboardLayout extends BaseDashboardLayout {
    // Mewarisi fungsionalitas dari BaseDashboardLayout
}
```

### 3. Polymorphism (Polimorfisme)
Menerapkan *method overriding* untuk memberikan perilaku spesifik pada class turunan.
```java
@Override
protected void initializeDefaultView() {
    setContentView(new AdminHomeView());
}
```

### 4. Abstraction (Abstraksi)
Menyembunyikan detail implementasi yang kompleks di balik antarmuka yang sederhana bagi pengguna.

---

## 🚀 Cara Menjalankan Aplikasi

### Persiapan
1. Pastikan Anda memiliki **JDK 17** atau versi terbaru.
2. Clone repositori ini ke direktori lokal Anda.

### Eksekusi
Buka terminal atau command prompt pada direktori root proyek, lalu jalankan perintah berikut:

**Windows:**
```bash
.\gradlew run
```

**Linux / macOS:**
```bash
./gradlew run
```

---

## 👥 Tim Pengembang
Proyek ini dikembangkan dengan penuh dedikasi oleh Kelompok 21:

| Nama | NIM | 
| :--- | :---: | 
| **Aditiya Izza Fahreza** | H071251069 | 
| **Zahrana Kumayla Irfan** | H071251097 |
| **Mawaddah Fajri Lahamuddin** | H071251041 |

---

<p align="center">
  Made with ❤️ by <b>KLP 21</b>
</p>