package com.kalkulation.matrix.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.kalkulation.matrix.model.KalkulationsPosition;

import java.util.ArrayList;
import java.util.List;

/**
 * SQLite-Datenbank für die Kalkulationspositionen.
 * Optimiert für schnelle Volltextsuche über 20.000+ Positionen.
 */
public class KalkulationsDatenbank extends SQLiteOpenHelper {

    private static final String DB_NAME = "kalkulation.db";
    private static final int DB_VERSION = 1;

    private static final String TABLE_POSITIONEN = "positionen";
    private static final String TABLE_FTS = "positionen_fts";

    // Spalten
    public static final String COL_ID = "_id";
    public static final String COL_POS_NR = "positions_nummer";
    public static final String COL_KATEGORIE = "kategorie";
    public static final String COL_BEZEICHNUNG = "bezeichnung";
    public static final String COL_BESCHREIBUNG = "beschreibung";
    public static final String COL_EINHEIT = "einheit";
    public static final String COL_STUNDEN_SATZ = "stunden_satz";
    public static final String COL_MATERIAL_KOSTEN = "material_kosten";
    public static final String COL_GESAMT_KOSTEN = "gesamt_kosten";
    public static final String COL_GEWERK = "gewerk";
    public static final String COL_SCHWIERIGKEIT = "schwierigkeitsgrad";

    private static KalkulationsDatenbank instance;

    public static synchronized KalkulationsDatenbank getInstance(Context context) {
        if (instance == null) {
            instance = new KalkulationsDatenbank(context.getApplicationContext());
        }
        return instance;
    }

    private KalkulationsDatenbank(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Haupttabelle
        db.execSQL("CREATE TABLE " + TABLE_POSITIONEN + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_POS_NR + " TEXT, " +
                COL_KATEGORIE + " TEXT, " +
                COL_BEZEICHNUNG + " TEXT, " +
                COL_BESCHREIBUNG + " TEXT, " +
                COL_EINHEIT + " TEXT, " +
                COL_STUNDEN_SATZ + " REAL DEFAULT 0, " +
                COL_MATERIAL_KOSTEN + " REAL DEFAULT 0, " +
                COL_GESAMT_KOSTEN + " REAL DEFAULT 0, " +
                COL_GEWERK + " TEXT, " +
                COL_SCHWIERIGKEIT + " TEXT" +
                ")");

        // FTS-Tabelle für schnelle Volltextsuche
        db.execSQL("CREATE VIRTUAL TABLE " + TABLE_FTS +
                " USING fts4(content=" + TABLE_POSITIONEN + ", " +
                COL_POS_NR + ", " +
                COL_KATEGORIE + ", " +
                COL_BEZEICHNUNG + ", " +
                COL_BESCHREIBUNG + ", " +
                COL_GEWERK + ")");

        // Indizes für häufige Filter
        db.execSQL("CREATE INDEX idx_kategorie ON " + TABLE_POSITIONEN + "(" + COL_KATEGORIE + ")");
        db.execSQL("CREATE INDEX idx_gewerk ON " + TABLE_POSITIONEN + "(" + COL_GEWERK + ")");
        db.execSQL("CREATE INDEX idx_pos_nr ON " + TABLE_POSITIONEN + "(" + COL_POS_NR + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_POSITIONEN);
        onCreate(db);
    }

    /**
     * Fügt eine einzelne Position ein.
     */
    public long insertPosition(KalkulationsPosition pos) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = positionToValues(pos);
        long id = db.insert(TABLE_POSITIONEN, null, values);

        // FTS aktualisieren
        if (id != -1) {
            db.execSQL("INSERT INTO " + TABLE_FTS + "(" + TABLE_FTS + ") VALUES('rebuild')");
        }
        return id;
    }

    /**
     * Batch-Import: Fügt viele Positionen effizient ein.
     * Optimiert für 20.000+ Datensätze.
     */
    public int importPositionen(List<KalkulationsPosition> positionen) {
        SQLiteDatabase db = getWritableDatabase();
        int count = 0;

        db.beginTransaction();
        try {
            // Alte Daten löschen
            db.delete(TABLE_POSITIONEN, null, null);

            for (KalkulationsPosition pos : positionen) {
                ContentValues values = positionToValues(pos);
                if (db.insert(TABLE_POSITIONEN, null, values) != -1) {
                    count++;
                }
            }

            // FTS-Index neu aufbauen
            db.execSQL("INSERT INTO " + TABLE_FTS + "(" + TABLE_FTS + ") VALUES('rebuild')");

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        return count;
    }

    /**
     * Volltextsuche über alle relevanten Spalten.
     * Nutzt FTS4 für blitzschnelle Suche auch bei 20.000+ Einträgen.
     */
    public List<KalkulationsPosition> suche(String suchbegriff) {
        List<KalkulationsPosition> ergebnisse = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        String query;
        String[] args;

        if (suchbegriff == null || suchbegriff.trim().isEmpty()) {
            // Alle Positionen (limitiert)
            query = "SELECT * FROM " + TABLE_POSITIONEN + " ORDER BY " + COL_POS_NR + " LIMIT 100";
            args = null;
        } else {
            // FTS-Suche mit Wildcard
            String suchTerm = suchbegriff.trim().replace("'", "''") + "*";
            query = "SELECT p.* FROM " + TABLE_POSITIONEN + " p " +
                    "INNER JOIN " + TABLE_FTS + " f ON p." + COL_ID + " = f.rowid " +
                    "WHERE " + TABLE_FTS + " MATCH ? " +
                    "ORDER BY p." + COL_POS_NR + " LIMIT 200";
            args = new String[]{suchTerm};
        }

        Cursor cursor = db.rawQuery(query, args);
        while (cursor.moveToNext()) {
            ergebnisse.add(cursorToPosition(cursor));
        }
        cursor.close();

        return ergebnisse;
    }

    /**
     * Suche nach Kategorie.
     */
    public List<KalkulationsPosition> sucheNachKategorie(String kategorie) {
        List<KalkulationsPosition> ergebnisse = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(TABLE_POSITIONEN, null,
                COL_KATEGORIE + " = ?", new String[]{kategorie},
                null, null, COL_POS_NR);

        while (cursor.moveToNext()) {
            ergebnisse.add(cursorToPosition(cursor));
        }
        cursor.close();

        return ergebnisse;
    }

    /**
     * Suche nach Gewerk.
     */
    public List<KalkulationsPosition> sucheNachGewerk(String gewerk) {
        List<KalkulationsPosition> ergebnisse = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(TABLE_POSITIONEN, null,
                COL_GEWERK + " = ?", new String[]{gewerk},
                null, null, COL_POS_NR);

        while (cursor.moveToNext()) {
            ergebnisse.add(cursorToPosition(cursor));
        }
        cursor.close();

        return ergebnisse;
    }

    /**
     * Kombinierte Suche: Suchbegriff + Kategorie-Filter.
     */
    public List<KalkulationsPosition> sucheGefiltert(String suchbegriff, String kategorie, String gewerk) {
        List<KalkulationsPosition> ergebnisse = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        StringBuilder where = new StringBuilder();
        List<String> argList = new ArrayList<>();

        if (suchbegriff != null && !suchbegriff.trim().isEmpty()) {
            where.append(COL_BEZEICHNUNG + " LIKE ? OR " + COL_BESCHREIBUNG + " LIKE ? OR " + COL_POS_NR + " LIKE ?");
            String like = "%" + suchbegriff.trim() + "%";
            argList.add(like);
            argList.add(like);
            argList.add(like);
        }

        if (kategorie != null && !kategorie.isEmpty()) {
            if (where.length() > 0) where.insert(0, "(").append(") AND ");
            where.append(COL_KATEGORIE + " = ?");
            argList.add(kategorie);
        }

        if (gewerk != null && !gewerk.isEmpty()) {
            if (where.length() > 0) where.append(" AND ");
            where.append(COL_GEWERK + " = ?");
            argList.add(gewerk);
        }

        String selection = where.length() > 0 ? where.toString() : null;
        String[] args = argList.isEmpty() ? null : argList.toArray(new String[0]);

        Cursor cursor = db.query(TABLE_POSITIONEN, null,
                selection, args,
                null, null, COL_POS_NR, "200");

        while (cursor.moveToNext()) {
            ergebnisse.add(cursorToPosition(cursor));
        }
        cursor.close();

        return ergebnisse;
    }

    /**
     * Alle verfügbaren Kategorien.
     */
    public List<String> getAlleKategorien() {
        List<String> kategorien = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT DISTINCT " + COL_KATEGORIE + " FROM " + TABLE_POSITIONEN +
                        " WHERE " + COL_KATEGORIE + " IS NOT NULL ORDER BY " + COL_KATEGORIE, null);

        while (cursor.moveToNext()) {
            kategorien.add(cursor.getString(0));
        }
        cursor.close();

        return kategorien;
    }

    /**
     * Alle verfügbaren Gewerke.
     */
    public List<String> getAlleGewerke() {
        List<String> gewerke = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT DISTINCT " + COL_GEWERK + " FROM " + TABLE_POSITIONEN +
                        " WHERE " + COL_GEWERK + " IS NOT NULL ORDER BY " + COL_GEWERK, null);

        while (cursor.moveToNext()) {
            gewerke.add(cursor.getString(0));
        }
        cursor.close();

        return gewerke;
    }

    /**
     * Anzahl der Positionen in der Datenbank.
     */
    public int getAnzahlPositionen() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_POSITIONEN, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        return count;
    }

    /**
     * Einzelne Position nach ID laden.
     */
    public KalkulationsPosition getPositionById(long id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_POSITIONEN, null,
                COL_ID + " = ?", new String[]{String.valueOf(id)},
                null, null, null);

        KalkulationsPosition pos = null;
        if (cursor.moveToFirst()) {
            pos = cursorToPosition(cursor);
        }
        cursor.close();
        return pos;
    }

    private ContentValues positionToValues(KalkulationsPosition pos) {
        ContentValues values = new ContentValues();
        values.put(COL_POS_NR, pos.getPositionsNummer());
        values.put(COL_KATEGORIE, pos.getKategorie());
        values.put(COL_BEZEICHNUNG, pos.getBezeichnung());
        values.put(COL_BESCHREIBUNG, pos.getBeschreibung());
        values.put(COL_EINHEIT, pos.getEinheit());
        values.put(COL_STUNDEN_SATZ, pos.getStundenSatz());
        values.put(COL_MATERIAL_KOSTEN, pos.getMaterialKosten());
        values.put(COL_GESAMT_KOSTEN, pos.getGesamtKosten());
        values.put(COL_GEWERK, pos.getGewerk());
        values.put(COL_SCHWIERIGKEIT, pos.getSchwierigkeitsgrad());
        return values;
    }

    private KalkulationsPosition cursorToPosition(Cursor cursor) {
        KalkulationsPosition pos = new KalkulationsPosition();
        pos.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COL_ID)));
        pos.setPositionsNummer(cursor.getString(cursor.getColumnIndexOrThrow(COL_POS_NR)));
        pos.setKategorie(cursor.getString(cursor.getColumnIndexOrThrow(COL_KATEGORIE)));
        pos.setBezeichnung(cursor.getString(cursor.getColumnIndexOrThrow(COL_BEZEICHNUNG)));
        pos.setBeschreibung(cursor.getString(cursor.getColumnIndexOrThrow(COL_BESCHREIBUNG)));
        pos.setEinheit(cursor.getString(cursor.getColumnIndexOrThrow(COL_EINHEIT)));
        pos.setStundenSatz(cursor.getDouble(cursor.getColumnIndexOrThrow(COL_STUNDEN_SATZ)));
        pos.setMaterialKosten(cursor.getDouble(cursor.getColumnIndexOrThrow(COL_MATERIAL_KOSTEN)));
        pos.setGesamtKosten(cursor.getDouble(cursor.getColumnIndexOrThrow(COL_GESAMT_KOSTEN)));
        pos.setGewerk(cursor.getString(cursor.getColumnIndexOrThrow(COL_GEWERK)));
        pos.setSchwierigkeitsgrad(cursor.getString(cursor.getColumnIndexOrThrow(COL_SCHWIERIGKEIT)));
        return pos;
    }
}
