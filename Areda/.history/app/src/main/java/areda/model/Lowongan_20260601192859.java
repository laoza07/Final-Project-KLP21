package areda.model;

import java.util.List;

public class Lowongan extends BaseModel {
    private String id;
    private String divisi;
    private String judul;
    private String deskripsi;
    private List<String> skillDibutuhkan;
    private int sisaSlot;
    private String tanggalDibuat;

    public Lowongan() {
    }

    public Lowongan(String id, String divisi, String judul, String deskripsi,
            List<String> skillDibutuhkan, int sisaSlot, String tanggalDibuat) {
        this.id = id;
        this.divisi = divisi;
        this.judul = judul;
        this.deskripsi = deskripsi;
        this.skillDibutuhkan = skillDibutuhkan;
        this.sisaSlot = sisaSlot;
        this.tanggalDibuat = tanggalDibuat;
    }

    @Override
    public String getIdentifier() {
        return id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDivisi() {
        return divisi;
    }

    public void setDivisi(String divisi) {
        this.divisi = divisi;
    }

    public String getJudul() {
        return judul;
    }

    public void setJudul(String judul) {
        this.judul = judul;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public List<String> getSkillDibutuhkan() {
        return skillDibutuhkan;
    }

    public void setSkillDibutuhkan(List<String> skillDibutuhkan) {
        this.skillDibutuhkan = skillDibutuhkan;
    }

    public int getSisaSlot() {
        return sisaSlot;
    }

    public void setSisaSlot(int sisaSlot) {
        this.sisaSlot = sisaSlot;
    }

    public String getTanggalDibuat() {
        return tanggalDibuat;
    }

    public void setTanggalDibuat(String tanggalDibuat) {
        this.tanggalDibuat = tanggalDibuat;
    }
}