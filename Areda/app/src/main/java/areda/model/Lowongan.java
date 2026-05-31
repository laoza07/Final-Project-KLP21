package areda.model;

import java.io.Serializable;
import java.util.List;

public class Lowongan implements Serializable {
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