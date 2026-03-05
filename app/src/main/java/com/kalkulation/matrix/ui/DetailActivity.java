package com.kalkulation.matrix.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.kalkulation.matrix.R;
import com.kalkulation.matrix.data.KalkulationsDatenbank;
import com.kalkulation.matrix.model.KalkulationsPosition;

import java.text.NumberFormat;
import java.util.Locale;

public class DetailActivity extends AppCompatActivity {

    private KalkulationsPosition position;
    private EditText editMenge;
    private TextView txtBerechneteKosten;
    private TextView txtBerechneteStunden;
    private final NumberFormat waehrungsFormat = NumberFormat.getCurrencyInstance(Locale.GERMANY);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        long id = getIntent().getLongExtra("position_id", -1);
        if (id == -1) {
            finish();
            return;
        }

        KalkulationsDatenbank db = KalkulationsDatenbank.getInstance(this);
        position = db.getPositionById(id);
        if (position == null) {
            finish();
            return;
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(position.getBezeichnung());
        }

        bindDaten();
        setupMengenBerechnung();
    }

    private void bindDaten() {
        ((TextView) findViewById(R.id.txtDetailPosNr)).setText(position.getPositionsNummer());
        ((TextView) findViewById(R.id.txtDetailBezeichnung)).setText(position.getBezeichnung());
        ((TextView) findViewById(R.id.txtDetailBeschreibung)).setText(position.getBeschreibung());

        ((TextView) findViewById(R.id.txtDetailStunden))
                .setText(String.format(Locale.GERMANY, "%.2f h", position.getStundenSatz()));
        ((TextView) findViewById(R.id.txtDetailMaterial))
                .setText(waehrungsFormat.format(position.getMaterialKosten()));
        ((TextView) findViewById(R.id.txtDetailGesamt))
                .setText(waehrungsFormat.format(position.getGesamtKosten()));
        ((TextView) findViewById(R.id.txtDetailEinheit)).setText(position.getEinheit());

        ((TextView) findViewById(R.id.txtDetailKategorie)).setText(position.getKategorie());
        ((TextView) findViewById(R.id.txtDetailGewerk)).setText(position.getGewerk());
        ((TextView) findViewById(R.id.txtDetailSchwierigkeit)).setText(position.getSchwierigkeitsgrad());

        ((TextView) findViewById(R.id.txtMengenEinheit)).setText(position.getEinheit());
    }

    private void setupMengenBerechnung() {
        editMenge = findViewById(R.id.editMenge);
        txtBerechneteKosten = findViewById(R.id.txtBerechneteKosten);
        txtBerechneteStunden = findViewById(R.id.txtBerechneteStunden);

        berechne();

        editMenge.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                berechne();
            }
        });
    }

    private void berechne() {
        double menge = 1;
        try {
            String text = editMenge.getText().toString().replace(",", ".");
            if (!text.isEmpty()) {
                menge = Double.parseDouble(text);
            }
        } catch (NumberFormatException ignored) {}

        double kosten = position.getGesamtKosten() * menge;
        double stunden = position.getStundenSatz() * menge;

        txtBerechneteKosten.setText(waehrungsFormat.format(kosten));
        txtBerechneteStunden.setText(String.format(Locale.GERMANY, "%.2f Stunden gesamt", stunden));
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
