package com.kalkulation.matrix.model;

/**
 * Datenmodell für eine Kalkulationsposition.
 * Repräsentiert eine Zeile aus der Kalkulationshilfe.
 */
public class KalkulationsPosition {

    private long id;
    private String positionsNummer;
    private String kategorie;
    private String bezeichnung;
    private String beschreibung;
    private String einheit;
    private double stundenSatz;
    private double materialKosten;
    private double gesamtKosten;
    private String gewerk;
    private String schwierigkeitsgrad;

    public KalkulationsPosition() {}

    public KalkulationsPosition(String positionsNummer, String kategorie, String bezeichnung,
                                 String beschreibung, String einheit, double stundenSatz,
                                 double materialKosten, double gesamtKosten, String gewerk,
                                 String schwierigkeitsgrad) {
        this.positionsNummer = positionsNummer;
        this.kategorie = kategorie;
        this.bezeichnung = bezeichnung;
        this.beschreibung = beschreibung;
        this.einheit = einheit;
        this.stundenSatz = stundenSatz;
        this.materialKosten = materialKosten;
        this.gesamtKosten = gesamtKosten;
        this.gewerk = gewerk;
        this.schwierigkeitsgrad = schwierigkeitsgrad;
    }

    // Getters and Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getPositionsNummer() { return positionsNummer; }
    public void setPositionsNummer(String positionsNummer) { this.positionsNummer = positionsNummer; }

    public String getKategorie() { return kategorie; }
    public void setKategorie(String kategorie) { this.kategorie = kategorie; }

    public String getBezeichnung() { return bezeichnung; }
    public void setBezeichnung(String bezeichnung) { this.bezeichnung = bezeichnung; }

    public String getBeschreibung() { return beschreibung; }
    public void setBeschreibung(String beschreibung) { this.beschreibung = beschreibung; }

    public String getEinheit() { return einheit; }
    public void setEinheit(String einheit) { this.einheit = einheit; }

    public double getStundenSatz() { return stundenSatz; }
    public void setStundenSatz(double stundenSatz) { this.stundenSatz = stundenSatz; }

    public double getMaterialKosten() { return materialKosten; }
    public void setMaterialKosten(double materialKosten) { this.materialKosten = materialKosten; }

    public double getGesamtKosten() { return gesamtKosten; }
    public void setGesamtKosten(double gesamtKosten) { this.gesamtKosten = gesamtKosten; }

    public String getGewerk() { return gewerk; }
    public void setGewerk(String gewerk) { this.gewerk = gewerk; }

    public String getSchwierigkeitsgrad() { return schwierigkeitsgrad; }
    public void setSchwierigkeitsgrad(String schwierigkeitsgrad) { this.schwierigkeitsgrad = schwierigkeitsgrad; }

    @Override
    public String toString() {
        return positionsNummer + " - " + bezeichnung;
    }
}
