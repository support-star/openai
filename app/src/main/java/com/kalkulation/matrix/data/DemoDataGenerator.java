package com.kalkulation.matrix.data;

import android.content.Context;

import com.kalkulation.matrix.model.KalkulationsPosition;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Generiert realistische Demo-Kalkulationsdaten für Bauprojekte.
 * Erstellt ca. 500 Positionen aus verschiedenen Gewerken.
 */
public class DemoDataGenerator {

    private static final Random random = new Random(42);

    // Gewerke und ihre typischen Positionen
    private static final String[][] GEWERKE_POSITIONEN = {
        // Gewerk, Bezeichnung-Prefix, Einheit, StundenMin, StundenMax, MaterialMin, MaterialMax
        {"Elektro", "Elektroinstallation", "Stk", "0.5", "8", "15", "500"},
        {"Elektro", "Kabelverlegung", "m", "0.1", "0.5", "2", "25"},
        {"Elektro", "Schaltschrank", "Stk", "4", "40", "200", "5000"},
        {"Elektro", "Beleuchtung", "Stk", "0.5", "3", "20", "300"},
        {"Elektro", "Steckdose", "Stk", "0.3", "1", "5", "30"},

        {"Sanitär", "Wasserleitung", "m", "0.2", "1", "5", "50"},
        {"Sanitär", "Sanitärinstallation", "Stk", "1", "8", "50", "2000"},
        {"Sanitär", "Heizungsanschluss", "Stk", "2", "6", "30", "500"},
        {"Sanitär", "Abwasserleitung", "m", "0.3", "1.5", "8", "40"},
        {"Sanitär", "Armatur", "Stk", "0.5", "2", "20", "800"},

        {"Rohbau", "Mauerwerk", "m²", "0.5", "2", "20", "80"},
        {"Rohbau", "Betonarbeiten", "m³", "2", "8", "80", "200"},
        {"Rohbau", "Bewehrung", "kg", "0.01", "0.05", "1", "3"},
        {"Rohbau", "Schalung", "m²", "0.5", "2", "10", "40"},

        {"Trockenbau", "Gipskartonwand", "m²", "0.3", "1", "10", "40"},
        {"Trockenbau", "Abhangdecke", "m²", "0.5", "1.5", "15", "60"},
        {"Trockenbau", "Dämmung", "m²", "0.1", "0.5", "5", "30"},

        {"Maler", "Anstrich", "m²", "0.1", "0.3", "2", "15"},
        {"Maler", "Tapezierung", "m²", "0.15", "0.4", "3", "20"},
        {"Maler", "Lackierung", "m²", "0.2", "0.5", "5", "25"},
        {"Maler", "Spachtelarbeiten", "m²", "0.2", "0.8", "3", "10"},

        {"Bodenbelag", "Fliesenverlegung", "m²", "0.3", "1", "15", "80"},
        {"Bodenbelag", "Parkett", "m²", "0.2", "0.8", "20", "100"},
        {"Bodenbelag", "Teppich", "m²", "0.1", "0.3", "10", "50"},
        {"Bodenbelag", "Estrich", "m²", "0.2", "0.5", "8", "25"},

        {"Dach", "Dacheindeckung", "m²", "0.3", "1", "20", "80"},
        {"Dach", "Dachdämmung", "m²", "0.2", "0.5", "10", "40"},
        {"Dach", "Dachrinne", "m", "0.2", "0.5", "8", "30"},

        {"Fenster/Türen", "Fenster", "Stk", "1", "4", "100", "1500"},
        {"Fenster/Türen", "Innentür", "Stk", "1", "3", "80", "600"},
        {"Fenster/Türen", "Außentür", "Stk", "2", "6", "200", "3000"},

        {"Außenanlagen", "Pflasterung", "m²", "0.3", "1", "15", "60"},
        {"Außenanlagen", "Erdarbeiten", "m³", "0.2", "0.5", "5", "20"},
        {"Außenanlagen", "Zaunanlage", "m", "0.3", "1", "20", "80"},

        {"IT/Netzwerk", "Netzwerkdose", "Stk", "0.3", "1", "10", "40"},
        {"IT/Netzwerk", "Serverrack", "Stk", "4", "16", "500", "5000"},
        {"IT/Netzwerk", "WLAN Access Point", "Stk", "0.5", "2", "50", "300"},
        {"IT/Netzwerk", "Glasfaserleitung", "m", "0.2", "0.8", "5", "30"},

        {"Lüftung/Klima", "Lüftungskanal", "m", "0.3", "1", "15", "60"},
        {"Lüftung/Klima", "Klimagerät", "Stk", "4", "16", "500", "5000"},
        {"Lüftung/Klima", "Lüftungsgitter", "Stk", "0.2", "0.5", "10", "50"},
    };

    private static final String[] KATEGORIEN = {
        "Neubau", "Sanierung", "Umbau", "Erweiterung", "Instandhaltung",
        "Modernisierung", "Rückbau", "Sonderleistung"
    };

    private static final String[] SCHWIERIGKEITEN = {
        "Einfach", "Mittel", "Schwer", "Sehr schwer"
    };

    private static final String[] ZUSATZ_BEZEICHNUNGEN = {
        "Standard", "Aufputz", "Unterputz", "mit Zubehör", "inkl. Montage",
        "DN50", "DN100", "DN150", "1-fach", "2-fach", "3-fach",
        "bis 2,5m", "bis 3,5m", "über 3,5m",
        "Innenbereich", "Außenbereich", "Nassbereich",
        "brandgeschützt", "schallgeschützt", "wärmegedämmt",
        "nach DIN", "RAL-Qualität", "Premium"
    };

    public static int generateAndInsert(Context context) {
        List<KalkulationsPosition> positionen = generate();
        KalkulationsDatenbank db = KalkulationsDatenbank.getInstance(context);
        return db.importPositionen(positionen);
    }

    public static List<KalkulationsPosition> generate() {
        List<KalkulationsPosition> positionen = new ArrayList<>();
        int posNr = 1;

        for (String[] gewerk : GEWERKE_POSITIONEN) {
            String gewerkName = gewerk[0];
            String bezeichnungPrefix = gewerk[1];
            String einheit = gewerk[2];
            double stundenMin = Double.parseDouble(gewerk[3]);
            double stundenMax = Double.parseDouble(gewerk[4]);
            double materialMin = Double.parseDouble(gewerk[5]);
            double materialMax = Double.parseDouble(gewerk[6]);

            // Mehrere Varianten pro Position
            int varianten = 8 + random.nextInt(8);
            for (int i = 0; i < varianten; i++) {
                String zusatz = ZUSATZ_BEZEICHNUNGEN[random.nextInt(ZUSATZ_BEZEICHNUNGEN.length)];
                String kategorie = KATEGORIEN[random.nextInt(KATEGORIEN.length)];
                String schwierigkeit = SCHWIERIGKEITEN[random.nextInt(SCHWIERIGKEITEN.length)];

                double stunden = stundenMin + random.nextDouble() * (stundenMax - stundenMin);
                double material = materialMin + random.nextDouble() * (materialMax - materialMin);
                double stundenKosten = stunden * 65.0; // 65€/h Kalkulationssatz
                double gesamt = stundenKosten + material;

                KalkulationsPosition pos = new KalkulationsPosition(
                    String.format("%03d.%03d", (posNr / 1000) + 1, posNr % 1000),
                    kategorie,
                    bezeichnungPrefix + " " + zusatz,
                    "Lieferung und Montage " + bezeichnungPrefix.toLowerCase() + " " + zusatz.toLowerCase()
                        + ", " + gewerkName + ", " + kategorie,
                    einheit,
                    Math.round(stunden * 100.0) / 100.0,
                    Math.round(material * 100.0) / 100.0,
                    Math.round(gesamt * 100.0) / 100.0,
                    gewerkName,
                    schwierigkeit
                );

                positionen.add(pos);
                posNr++;
            }
        }

        return positionen;
    }
}
