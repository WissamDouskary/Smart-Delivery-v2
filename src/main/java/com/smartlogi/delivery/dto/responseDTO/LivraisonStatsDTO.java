package com.smartlogi.delivery.dto.responseDTO;

public class LivraisonStatsDTO {
    private String livreurNom;
    private String zoneNom;
    private Long nombreColis;
    private Double poidsTotal;

    public LivraisonStatsDTO(String livreurNom, String zoneNom, Long nombreColis, Double poidsTotal) {
        this.livreurNom = livreurNom;
        this.zoneNom = zoneNom;
        this.nombreColis = nombreColis;
        this.poidsTotal = poidsTotal;
    }

    public LivraisonStatsDTO(){}

    public String getLivreurNom() {
        return livreurNom;
    }

    public void setLivreurNom(String livreurNom) {
        this.livreurNom = livreurNom;
    }

    public String getZoneNom() {
        return zoneNom;
    }

    public void setZoneNom(String zoneNom) {
        this.zoneNom = zoneNom;
    }

    public Long getNombreColis() {
        return nombreColis;
    }

    public void setNombreColis(Long nombreColis) {
        this.nombreColis = nombreColis;
    }

    public Double getPoidsTotal() {
        return poidsTotal;
    }

    public void setPoidsTotal(Double poidsTotal) {
        this.poidsTotal = poidsTotal;
    }
}
