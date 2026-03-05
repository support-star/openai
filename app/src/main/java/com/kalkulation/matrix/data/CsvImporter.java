package com.kalkulation.matrix.data;

import android.content.Context;
import android.net.Uri;

import com.kalkulation.matrix.model.KalkulationsPosition;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Importiert Kalkulationsdaten aus CSV-Dateien.
 * Unterstützt verschiedene Trennzeichen und Spalten-Mappings.
 *
 * Erwartetes CSV-Format (Semikolon-getrennt):
 * PositionsNr;Kategorie;Bezeichnung;Beschreibung;Einheit;StundenSatz;MaterialKosten;GesamtKosten;Gewerk;Schwierigkeitsgrad
 */
public class CsvImporter {

    public interface ImportCallback {
        void onProgress(int current, int total);
        void onComplete(int imported, int errors);
        void onError(String message);
    }

    private final Context context;
    private char trennzeichen = ';';
    private boolean hatHeaderZeile = true;

    // Spalten-Mapping (Index in CSV -> Feld)
    private int colPosNr = 0;
    private int colKategorie = 1;
    private int colBezeichnung = 2;
    private int colBeschreibung = 3;
    private int colEinheit = 4;
    private int colStundenSatz = 5;
    private int colMaterialKosten = 6;
    private int colGesamtKosten = 7;
    private int colGewerk = 8;
    private int colSchwierigkeit = 9;

    public CsvImporter(Context context) {
        this.context = context;
    }

    public void setTrennzeichen(char trennzeichen) {
        this.trennzeichen = trennzeichen;
    }

    public void setHatHeaderZeile(boolean hatHeaderZeile) {
        this.hatHeaderZeile = hatHeaderZeile;
    }

    /**
     * Setzt das Spalten-Mapping.
     * Übergebe -1 für nicht vorhandene Spalten.
     */
    public void setSpaltenMapping(int posNr, int kategorie, int bezeichnung,
                                   int beschreibung, int einheit, int stundenSatz,
                                   int materialKosten, int gesamtKosten,
                                   int gewerk, int schwierigkeit) {
        this.colPosNr = posNr;
        this.colKategorie = kategorie;
        this.colBezeichnung = bezeichnung;
        this.colBeschreibung = beschreibung;
        this.colEinheit = einheit;
        this.colStundenSatz = stundenSatz;
        this.colMaterialKosten = materialKosten;
        this.colGesamtKosten = gesamtKosten;
        this.colGewerk = gewerk;
        this.colSchwierigkeit = schwierigkeit;
    }

    /**
     * Importiert eine CSV-Datei aus einem URI (z.B. vom Dateibrowser).
     */
    public void importFromUri(Uri uri, ImportCallback callback) {
        try {
            InputStream is = context.getContentResolver().openInputStream(uri);
            if (is == null) {
                callback.onError("Datei konnte nicht geöffnet werden.");
                return;
            }
            importFromStream(is, callback);
        } catch (Exception e) {
            callback.onError("Fehler beim Öffnen: " + e.getMessage());
        }
    }

    /**
     * Importiert aus dem Assets-Ordner (für eingebettete Kalkulationshilfe).
     */
    public void importFromAssets(String dateiname, ImportCallback callback) {
        try {
            InputStream is = context.getAssets().open(dateiname);
            importFromStream(is, callback);
        } catch (Exception e) {
            callback.onError("Datei nicht gefunden: " + dateiname);
        }
    }

    private void importFromStream(InputStream is, ImportCallback callback) {
        List<KalkulationsPosition> positionen = new ArrayList<>();
        int errors = 0;
        int lineCount = 0;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"))) {
            String zeile;

            // Header überspringen
            if (hatHeaderZeile && reader.readLine() != null) {
                lineCount++;
            }

            while ((zeile = reader.readLine()) != null) {
                lineCount++;
                try {
                    KalkulationsPosition pos = parseZeile(zeile);
                    if (pos != null) {
                        positionen.add(pos);
                    }

                    // Fortschritt alle 500 Zeilen melden
                    if (positionen.size() % 500 == 0) {
                        callback.onProgress(positionen.size(), -1);
                    }
                } catch (Exception e) {
                    errors++;
                }
            }

            // In Datenbank schreiben
            callback.onProgress(positionen.size(), positionen.size());
            KalkulationsDatenbank db = KalkulationsDatenbank.getInstance(context);
            int imported = db.importPositionen(positionen);

            callback.onComplete(imported, errors);

        } catch (Exception e) {
            callback.onError("Importfehler in Zeile " + lineCount + ": " + e.getMessage());
        }
    }

    private KalkulationsPosition parseZeile(String zeile) {
        if (zeile == null || zeile.trim().isEmpty()) return null;

        String[] spalten = zeile.split(String.valueOf(trennzeichen), -1);

        KalkulationsPosition pos = new KalkulationsPosition();

        pos.setPositionsNummer(getWert(spalten, colPosNr, ""));
        pos.setKategorie(getWert(spalten, colKategorie, ""));
        pos.setBezeichnung(getWert(spalten, colBezeichnung, ""));
        pos.setBeschreibung(getWert(spalten, colBeschreibung, ""));
        pos.setEinheit(getWert(spalten, colEinheit, ""));
        pos.setStundenSatz(getZahl(spalten, colStundenSatz));
        pos.setMaterialKosten(getZahl(spalten, colMaterialKosten));
        pos.setGesamtKosten(getZahl(spalten, colGesamtKosten));
        pos.setGewerk(getWert(spalten, colGewerk, ""));
        pos.setSchwierigkeitsgrad(getWert(spalten, colSchwierigkeit, ""));

        // Mindestens Bezeichnung muss vorhanden sein
        if (pos.getBezeichnung().isEmpty() && pos.getPositionsNummer().isEmpty()) {
            return null;
        }

        return pos;
    }

    private String getWert(String[] spalten, int index, String defaultWert) {
        if (index < 0 || index >= spalten.length) return defaultWert;
        String wert = spalten[index].trim();
        // Anführungszeichen entfernen
        if (wert.startsWith("\"") && wert.endsWith("\"")) {
            wert = wert.substring(1, wert.length() - 1);
        }
        return wert;
    }

    private double getZahl(String[] spalten, int index) {
        if (index < 0 || index >= spalten.length) return 0;
        String wert = spalten[index].trim().replace(",", ".").replace("\"", "");
        if (wert.isEmpty()) return 0;
        try {
            return Double.parseDouble(wert);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
