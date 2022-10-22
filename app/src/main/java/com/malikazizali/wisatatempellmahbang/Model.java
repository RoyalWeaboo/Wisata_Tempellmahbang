package com.malikazizali.wisatatempellmahbang;

public class Model {
    String kode_paket,nama_paket,harga_paket,gambar_paket,deskripsi,deskripsi_lengkap;

    public Model(String kode_paket, String nama_paket, String harga_paket, String gambar_paket, String deskripsi, String deskripsi_lengkap){
        this.kode_paket = kode_paket;
        this.nama_paket = nama_paket;
        this.harga_paket = harga_paket;
        this.gambar_paket = gambar_paket;
        this.deskripsi = deskripsi;
        this.deskripsi_lengkap = deskripsi_lengkap;
    }

    public String getKode_paket() {
        return kode_paket;
    }

    public String getNama_paket() {
        return nama_paket;
    }

    public String getHarga_paket() {
        return harga_paket;
    }

    public String getGambar_paket() {
        return gambar_paket;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public String getDeskripsi_lengkap() {
        return deskripsi_lengkap;
    }
}
