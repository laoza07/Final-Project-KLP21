package areda.model;

public class Lamaran {
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
    public String tanggalInterview;

    public Lamaran() {
    }

    public Lamaran(String idLamaran, String idLowongan, String namaPelamar, String emailPelamar,
            String teleponPelamar, String tentangPelamar, String tanggal, String statusTahapan,
            String cvPath, String motletPath) {
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
        this.tanggalInterview = "";
    }
}