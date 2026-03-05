package com.kalkulation.matrix.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.kalkulation.matrix.R;
import com.kalkulation.matrix.data.CsvImporter;
import com.kalkulation.matrix.data.DemoDataGenerator;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImportActivity extends AppCompatActivity {

    private RadioGroup radioTrennzeichen;
    private CheckBox checkHeader;
    private ProgressBar progressImport;
    private TextView txtImportStatus;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private final ActivityResultLauncher<Intent> dateiLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri uri = result.getData().getData();
                    if (uri != null) {
                        importiereCsv(uri);
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        radioTrennzeichen = findViewById(R.id.radioTrennzeichen);
        checkHeader = findViewById(R.id.checkHeader);
        progressImport = findViewById(R.id.progressImport);
        txtImportStatus = findViewById(R.id.txtImportStatus);

        findViewById(R.id.btnDateiWaehlen).setOnClickListener(v -> dateiAuswaehlen());
        findViewById(R.id.btnDemoLaden).setOnClickListener(v -> demoLaden());
    }

    private void dateiAuswaehlen() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        String[] mimeTypes = {"text/csv", "text/comma-separated-values", "application/csv", "text/plain"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        dateiLauncher.launch(intent);
    }

    private char getTrennzeichen() {
        int id = radioTrennzeichen.getCheckedRadioButtonId();
        if (id == R.id.radioKomma) return ',';
        if (id == R.id.radioTab) return '\t';
        return ';';
    }

    private void importiereCsv(Uri uri) {
        progressImport.setVisibility(View.VISIBLE);
        progressImport.setIndeterminate(true);
        txtImportStatus.setText("");

        executor.execute(() -> {
            CsvImporter importer = new CsvImporter(this);
            importer.setTrennzeichen(getTrennzeichen());
            importer.setHatHeaderZeile(checkHeader.isChecked());

            importer.importFromUri(uri, new CsvImporter.ImportCallback() {
                @Override
                public void onProgress(int current, int total) {
                    mainHandler.post(() -> {
                        txtImportStatus.setText(String.format(getString(R.string.import_laeuft), current));
                    });
                }

                @Override
                public void onComplete(int imported, int errors) {
                    mainHandler.post(() -> {
                        progressImport.setVisibility(View.GONE);
                        txtImportStatus.setText(String.format(getString(R.string.import_fertig), imported, errors));
                        Toast.makeText(ImportActivity.this,
                                imported + " Positionen importiert", Toast.LENGTH_LONG).show();
                    });
                }

                @Override
                public void onError(String message) {
                    mainHandler.post(() -> {
                        progressImport.setVisibility(View.GONE);
                        txtImportStatus.setText(String.format(getString(R.string.import_fehler), message));
                    });
                }
            });
        });
    }

    private void demoLaden() {
        progressImport.setVisibility(View.VISIBLE);
        progressImport.setIndeterminate(true);
        txtImportStatus.setText("Generiere Demo-Daten...");

        executor.execute(() -> {
            int count = DemoDataGenerator.generateAndInsert(this);
            mainHandler.post(() -> {
                progressImport.setVisibility(View.GONE);
                txtImportStatus.setText(count + " Demo-Positionen geladen");
                Toast.makeText(this, count + " Demo-Positionen geladen", Toast.LENGTH_LONG).show();
            });
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
