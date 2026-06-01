package areda.model;

public class Lamaran extends BaseModel {
    private String idLamaran;
    private String idLowongan;
    private String namaPelamar;
    private String emailPelamar;
    private String teleponPelamar;
    private String tentangPelamar;
    private String tanggal;
    private String statusTahapan;
    private String cvPath;
    private String motletPath;
    private String tanggalInterview;

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

    @Override
    public String getIdentifier() {
        return idLamaran;
    }

    public String getIdLamaran() {
        return idLamaran;
    }

    public void setIdLamaran(String idLamaran) {
        this.idLamaran = idLamaran;
    }

    public String getIdLowongan() {
        return idLowongan;
    }

    public void setIdLowongan(String idLowongan) {
        this.idLowongan = idLowongan;
    }

    public String getNamaPelamar() {
        return namaPelamar;
    }

    public void setNamaPelamar(String namaPelamar) {
        this.namaPelamar = namaPelamar;
    }

    public String getEmailPelamar() {
        return emailPelamar;
    }

    public void setEmailPelamar(String emailPelamar) {
        this.emailPelamar = emailPelamar;
    }

    public String getTeleponPelamar() {
        return teleponPelamar;
    }

    public void setTeleponPelamar(String teleponPelamar) {
        this.teleponPelamar = teleponPelamar;
    }

    public String getTentangPelamar() {
        return tentangPelamar;
    }

    public void setTentangPelamar(String tentangPelamar) {
        this.tentangPelamar = tentangPelamar;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getStatusTahapan() {
        return statusTahapan;
    }

    public void setStatusTahapan(String statusTahapan) {
        this.statusTahapan = statusTahapan;
    }

    public String getCvPath() {
        return cvPath;
    }

    public void setCvPath(String cvPath) {
        this.cvPath = cvPath;
    }

    public String getMotletPath() {
        return motletPath;
    }

    public void setMotletPath(String motletPath) {
        this.motletPath = motletPath;
    }

    public String getTanggalInterview() {
        return tanggalInterview;
    }

    public void setTanggalInterview(String tanggalInterview) {
        this.tanggalInterview = tanggalInterview;
    }
}