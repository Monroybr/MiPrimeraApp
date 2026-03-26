package com.liseth.miprimeraapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;

public class SharePetInfoActivity extends AppCompatActivity {

    private TextView tvNombreMascotaShare, tvMensajeShare;
    private CheckBox cbVacunas, cbHistorial;
    private Button btnGenerarCompartirPdf;

    private int petIndex = -1;
    private String nombreMascota = "Mascota";
    private String edadMascota = "-";
    private String caracteristicasMascota = "-";
    private String nombreDueno = "-";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_pet_info);

        tvNombreMascotaShare = findViewById(R.id.tvNombreMascotaShare);
        tvMensajeShare = findViewById(R.id.tvMensajeShare);
        cbVacunas = findViewById(R.id.cbVacunas);
        cbHistorial = findViewById(R.id.cbHistorial);
        btnGenerarCompartirPdf = findViewById(R.id.btnGenerarCompartirPdf);

        petIndex = getIntent().getIntExtra("pet_index", -1);

        if (petIndex == -1) {
            tvMensajeShare.setText("No se pudo identificar la mascota.");
            btnGenerarCompartirPdf.setEnabled(false);
            return;
        }

        cargarDatosMascota(petIndex);
        cargarDatosDueno();

        tvNombreMascotaShare.setText("Mascota: " + nombreMascota);

        btnGenerarCompartirPdf.setOnClickListener(v -> generarYCompartirPdf());
    }

    private void cargarDatosMascota(int index) {
        SharedPreferences prefs = getSharedPreferences("mascotas", MODE_PRIVATE);
        String json = prefs.getString("mascotas_json", "[]");

        try {
            JSONArray arr = new JSONArray(json);

            if (index >= 0 && index < arr.length()) {
                JSONObject obj = arr.getJSONObject(index);

                nombreMascota = obj.optString("nombre", "Mascota");
                edadMascota = obj.optString("edadTexto", "-");
                caracteristicasMascota = obj.optString("caracteristicas", "-");

                if (caracteristicasMascota == null || caracteristicasMascota.isEmpty()) {
                    caracteristicasMascota = "-";
                }
            }
        } catch (Exception ignored) {
        }
    }

    private void cargarDatosDueno() {
        SharedPreferences prefs = getSharedPreferences("usuarios", MODE_PRIVATE);

        String nombres = prefs.getString("nombres", "");
        String apellidos = prefs.getString("apellidos", "");

        nombreDueno = (nombres + " " + apellidos).trim();

        if (nombreDueno.isEmpty()) {
            nombreDueno = "No registrado";
        }
    }

    private void generarYCompartirPdf() {
        if (!cbVacunas.isChecked() && !cbHistorial.isChecked()) {
            tvMensajeShare.setText("Selecciona al menos una sección.");
            return;
        }

        PdfDocument document = new PdfDocument();
        Paint titlePaint = new Paint();
        Paint bodyPaint = new Paint();

        titlePaint.setTextSize(18f);
        titlePaint.setFakeBoldText(true);

        bodyPaint.setTextSize(12f);

        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);

        int x = 40;
        int y = 50;
        int lineHeight = 20;

        page.getCanvas().drawText("Información de la mascota", x, y, titlePaint);
        y += 30;

        page.getCanvas().drawText("Nombre de la mascota: " + nombreMascota, x, y, bodyPaint);
        y += 20;

        page.getCanvas().drawText("Edad: " + edadMascota, x, y, bodyPaint);
        y += 20;

        page.getCanvas().drawText("Características: " + caracteristicasMascota, x, y, bodyPaint);
        y += 20;

        page.getCanvas().drawText("Dueño: " + nombreDueno, x, y, bodyPaint);
        y += 30;

        if (cbVacunas.isChecked()) {
            page.getCanvas().drawText("Carnet de vacunación", x, y, titlePaint);
            y += 25;

            y = escribirVacunasEnPdf(page, x, y, lineHeight, bodyPaint);
            y += 20;
        }

        if (cbHistorial.isChecked()) {
            page.getCanvas().drawText("Historial médico", x, y, titlePaint);
            y += 25;

            y = escribirHistorialEnPdf(page, x, y, lineHeight, bodyPaint);
        }

        document.finishPage(page);

        try {
            File file = new File(getExternalFilesDir(null), "info_" + nombreMascota + ".pdf");
            FileOutputStream fos = new FileOutputStream(file);
            document.writeTo(fos);
            document.close();
            fos.close();

            Uri uri = FileProvider.getUriForFile(
                    this,
                    getPackageName() + ".provider",
                    file
            );

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("application/pdf");
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Información de " + nombreMascota);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            startActivity(Intent.createChooser(shareIntent, "Compartir PDF"));

        } catch (Exception e) {
            tvMensajeShare.setText("Error generando el PDF.");
        }
    }

    private int escribirVacunasEnPdf(PdfDocument.Page page, int x, int y, int lineHeight, Paint paint) {
        SharedPreferences prefs = getSharedPreferences("vacunas", MODE_PRIVATE);
        String json = prefs.getString("vacunas_json", "[]");

        boolean encontro = false;

        try {
            JSONArray arr = new JSONArray(json);

            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                int idx = obj.optInt("pet_index", -1);

                if (idx != petIndex) continue;

                encontro = true;

                String vacuna = obj.optString("vacuna", "-");
                String fechaAplicacion = obj.optString("fechaAplicacion", "-");
                String lugar = obj.optString("lugar", "-");
                String proximaDosis = obj.optString("proximaDosis", "-");

                page.getCanvas().drawText("- " + vacuna, x, y, paint);
                y += lineHeight;
                page.getCanvas().drawText("  Aplicación: " + fechaAplicacion, x, y, paint);
                y += lineHeight;
                page.getCanvas().drawText("  Lugar: " + lugar, x, y, paint);
                y += lineHeight;
                page.getCanvas().drawText("  Próxima dosis: " + proximaDosis, x, y, paint);
                y += lineHeight + 8;
            }

        } catch (Exception ignored) {
        }

        if (!encontro) {
            page.getCanvas().drawText("No hay vacunas registradas.", x, y, paint);
            y += lineHeight;
        }

        return y;
    }

    private int escribirHistorialEnPdf(PdfDocument.Page page, int x, int y, int lineHeight, Paint paint) {
        SharedPreferences prefs = getSharedPreferences("historial", MODE_PRIVATE);
        String json = prefs.getString("historial_json", "[]");

        boolean encontro = false;

        try {
            JSONArray arr = new JSONArray(json);

            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                int idx = obj.optInt("pet_index", -1);

                if (idx != petIndex) continue;

                encontro = true;

                String fecha = obj.optString("fechaRegistro", "-");
                String enfermedades = obj.optString("enfermedades", "-");
                String procedimientos = obj.optString("procedimientos", "-");
                String medicacion = obj.optString("medicacion", "-");

                page.getCanvas().drawText("- Fecha: " + fecha, x, y, paint);
                y += lineHeight;
                page.getCanvas().drawText("  Enfermedades: " + enfermedades, x, y, paint);
                y += lineHeight;
                page.getCanvas().drawText("  Procedimientos: " + procedimientos, x, y, paint);
                y += lineHeight;
                page.getCanvas().drawText("  Medicación: " + medicacion, x, y, paint);
                y += lineHeight + 8;
            }

        } catch (Exception ignored) {
        }

        if (!encontro) {
            page.getCanvas().drawText("No hay historial médico registrado.", x, y, paint);
            y += lineHeight;
        }

        return y;
    }
}