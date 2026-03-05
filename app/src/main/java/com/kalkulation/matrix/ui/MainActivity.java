package com.kalkulation.matrix.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kalkulation.matrix.R;
import com.kalkulation.matrix.adapter.PositionAdapter;
import com.kalkulation.matrix.data.KalkulationsDatenbank;
import com.kalkulation.matrix.model.KalkulationsPosition;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private EditText editSuche;
    private RecyclerView recyclerPositionen;
    private LinearLayout layoutEmpty;
    private TextView txtErgebnisCount;
    private TextView txtEmptyTitle;
    private TextView txtEmptyMessage;
    private Chip chipKategorie;
    private Chip chipGewerk;

    private PositionAdapter adapter;
    private KalkulationsDatenbank db;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private String aktuelleKategorie = null;
    private String aktuellesGewerk = null;

    // Debounce für Suche
    private final Handler suchHandler = new Handler(Looper.getMainLooper());
    private Runnable suchRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = KalkulationsDatenbank.getInstance(this);

        initViews();
        setupToolbar();
        setupSuche();
        setupFilter();
        setupFab();
    }

    @Override
    protected void onResume() {
        super.onResume();
        sucheDurchfuehren();
    }

    private void initViews() {
        editSuche = findViewById(R.id.editSuche);
        recyclerPositionen = findViewById(R.id.recyclerPositionen);
        layoutEmpty = findViewById(R.id.layoutEmpty);
        txtErgebnisCount = findViewById(R.id.txtErgebnisCount);
        txtEmptyTitle = findViewById(R.id.txtEmptyTitle);
        txtEmptyMessage = findViewById(R.id.txtEmptyMessage);
        chipKategorie = findViewById(R.id.chipKategorie);
        chipGewerk = findViewById(R.id.chipGewerk);

        adapter = new PositionAdapter();
        recyclerPositionen.setLayoutManager(new LinearLayoutManager(this));
        recyclerPositionen.setAdapter(adapter);

        adapter.setOnPositionClickListener(position -> {
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra("position_id", position.getId());
            startActivity(intent);
        });

        findViewById(R.id.btnImport).setOnClickListener(v -> {
            startActivity(new Intent(this, ImportActivity.class));
        });
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void setupSuche() {
        editSuche.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                // Debounce: 300ms warten nach letzter Eingabe
                if (suchRunnable != null) {
                    suchHandler.removeCallbacks(suchRunnable);
                }
                suchRunnable = () -> sucheDurchfuehren();
                suchHandler.postDelayed(suchRunnable, 300);
            }
        });
    }

    private void setupFilter() {
        chipKategorie.setOnClickListener(v -> {
            executor.execute(() -> {
                List<String> kategorien = db.getAlleKategorien();
                mainHandler.post(() -> zeigeFilterDialog("Kategorie", kategorien, gewaehlte -> {
                    aktuelleKategorie = gewaehlte;
                    chipKategorie.setText(gewaehlte != null ? gewaehlte : getString(R.string.alle_kategorien));
                    sucheDurchfuehren();
                }));
            });
        });

        chipGewerk.setOnClickListener(v -> {
            executor.execute(() -> {
                List<String> gewerke = db.getAlleGewerke();
                mainHandler.post(() -> zeigeFilterDialog("Gewerk", gewerke, gewaehlte -> {
                    aktuellesGewerk = gewaehlte;
                    chipGewerk.setText(gewaehlte != null ? gewaehlte : getString(R.string.alle_gewerke));
                    sucheDurchfuehren();
                }));
            });
        });
    }

    private void setupFab() {
        FloatingActionButton fab = findViewById(R.id.fabAction);
        fab.setOnClickListener(v -> {
            startActivity(new Intent(this, ImportActivity.class));
        });
    }

    private void sucheDurchfuehren() {
        String suchbegriff = editSuche.getText().toString().trim();

        executor.execute(() -> {
            int total = db.getAnzahlPositionen();

            if (total == 0) {
                mainHandler.post(() -> {
                    layoutEmpty.setVisibility(View.VISIBLE);
                    recyclerPositionen.setVisibility(View.GONE);
                    txtErgebnisCount.setText(String.format(getString(R.string.positionen_gesamt), 0));
                });
                return;
            }

            List<KalkulationsPosition> ergebnisse;

            if ((aktuelleKategorie != null || aktuellesGewerk != null) || !suchbegriff.isEmpty()) {
                ergebnisse = db.sucheGefiltert(suchbegriff, aktuelleKategorie, aktuellesGewerk);
            } else {
                ergebnisse = db.suche(null);
            }

            mainHandler.post(() -> {
                adapter.setPositionen(ergebnisse);

                if (ergebnisse.isEmpty()) {
                    layoutEmpty.setVisibility(View.VISIBLE);
                    recyclerPositionen.setVisibility(View.GONE);
                    txtEmptyTitle.setText(R.string.keine_ergebnisse);
                    txtEmptyMessage.setText("");
                    findViewById(R.id.btnImport).setVisibility(View.GONE);
                } else {
                    layoutEmpty.setVisibility(View.GONE);
                    recyclerPositionen.setVisibility(View.VISIBLE);
                }

                txtErgebnisCount.setText(String.format(getString(R.string.ergebnis_count), ergebnisse.size()));
            });
        });
    }

    private interface FilterCallback {
        void onSelected(String value);
    }

    private void zeigeFilterDialog(String titel, List<String> optionen, FilterCallback callback) {
        String[] items = new String[optionen.size() + 1];
        items[0] = "Alle";
        for (int i = 0; i < optionen.size(); i++) {
            items[i + 1] = optionen.get(i);
        }

        new AlertDialog.Builder(this)
                .setTitle(titel + " wählen")
                .setItems(items, (dialog, which) -> {
                    if (which == 0) {
                        callback.onSelected(null);
                    } else {
                        callback.onSelected(items[which]);
                    }
                })
                .show();
    }
}
