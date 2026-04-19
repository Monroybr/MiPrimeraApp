package com.liseth.miprimeraapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SharePetInfoActivity extends AppCompatActivity {

    private TextView tvNombreMascotaShare, tvMensajeShare;
    private CheckBox cbVacunas, cbHistorial;
    private Button btnGenerarCompartirPdf;

    private int petIndex = -1;
    private String nombreMascota = "Mascota";
    private String edadMascota = "-";
    private String razaMascota = "-";
    private String sexoMascota = "-";
    private String pesoMascota = "-";
    private String colorMascota = "-";
    private String alergiasMascota = "-";
    private String caracteristicasMascota = "-";
    private String observacionesMascota = "-";
    private String imagenMascotaUri = "";

    private String nombreDueno = "-";
    private String telefonoDueno = "-";
    private String correoDueno = "-";
    private String direccionDueno = "-";

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

                nombreMascota = obtenerValorSeguro(obj.optString("nombre", "Mascota"));
                edadMascota = obtenerValorSeguro(obj.optString("edadTexto", "-"));
                razaMascota = obtenerValorSeguro(obj.optString("raza", "-"));
                sexoMascota = obtenerValorSeguro(obj.optString("sexo", "-"));
                pesoMascota = obtenerValorSeguro(obj.optString("peso", "-"));
                colorMascota = obtenerValorSeguro(obj.optString("color", "-"));
                alergiasMascota = obtenerValorSeguro(obj.optString("alergias", "-"));
                caracteristicasMascota = obtenerValorSeguro(obj.optString("caracteristicas", "-"));
                observacionesMascota = obtenerValorSeguro(obj.optString("observaciones", "-"));
                imagenMascotaUri = obj.optString("imagenUri", "");
            }
        } catch (Exception ignored) {
        }
    }

    private void cargarDatosDueno() {
        SharedPreferences prefs = getSharedPreferences("usuarios", MODE_PRIVATE);

        String nombres = prefs.getString("nombres", "");
        String apellidos = prefs.getString("apellidos", "");

        nombreDueno = (nombres + " " + apellidos).trim();
        telefonoDueno = prefs.getString("telefono", "-");
        correoDueno = prefs.getString("correo", "-");
        direccionDueno = prefs.getString("direccion", "-");

        nombreDueno = nombreDueno.isEmpty() ? "No registrado" : nombreDueno;
        telefonoDueno = obtenerValorSeguro(telefonoDueno);
        correoDueno = obtenerValorSeguro(correoDueno);
        direccionDueno = obtenerValorSeguro(direccionDueno);
    }

    private String obtenerValorSeguro(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            return "-";
        }
        return valor.trim();
    }

    private void generarYCompartirPdf() {
        if (!cbVacunas.isChecked() && !cbHistorial.isChecked()) {
            tvMensajeShare.setText("Selecciona al menos una sección.");
            return;
        }

        PdfDocument document = new PdfDocument();

        Paint titulo = new Paint();
        titulo.setTextSize(20f);
        titulo.setFakeBoldText(true);

        Paint subtitulo = new Paint();
        subtitulo.setTextSize(15f);
        subtitulo.setFakeBoldText(true);

        Paint texto = new Paint();
        texto.setTextSize(12f);

        Paint linea = new Paint();
        linea.setStrokeWidth(2f);

        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);

        int x = 40;
        int y = 50;
        int espacio = 20;

        String fecha = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date());

        page.getCanvas().drawText("REPORTE DE LA MASCOTA", x, y, titulo);
        y += 25;
        page.getCanvas().drawText("Fecha de generación: " + fecha, x, y, texto);
        y += 20;

        // Aquí intento dibujar la foto de la mascota en el PDF
        if (imagenMascotaUri != null && !imagenMascotaUri.isEmpty()) {
            try {
                InputStream inputStream = getContentResolver().openInputStream(Uri.parse(imagenMascotaUri));
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                if (bitmap != null) {
                    Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, 120, 120, false);
                    page.getCanvas().drawBitmap(scaledBitmap, x, y, null);
                    y += 140;
                }
            } catch (Exception ignored) {
            }
        }

        page.getCanvas().drawLine(x, y, 550, y, linea);
        y += 25;

        page.getCanvas().drawText("DATOS DE LA MASCOTA", x, y, subtitulo);
        y += espacio;

        page.getCanvas().drawText("Nombre: " + nombreMascota, x, y, texto);
        y += espacio;
        page.getCanvas().drawText("Edad: " + edadMascota, x, y, texto);
        y += espacio;
        page.getCanvas().drawText("Raza: " + razaMascota, x, y, texto);
        y += espacio;
        page.getCanvas().drawText("Sexo: " + sexoMascota, x, y, texto);
        y += espacio;
        page.getCanvas().drawText("Peso: " + (pesoMascota.equals("-") ? "-" : pesoMascota + " kg"), x, y, texto);
        y += espacio;
        page.getCanvas().drawText("Color: " + colorMascota, x, y, texto);
        y += espacio;
        page.getCanvas().drawText("Alergias: " + alergiasMascota, x, y, texto);
        y += espacio;
        page.getCanvas().drawText("Características: " + caracteristicasMascota, x, y, texto);
        y += espacio;
        page.getCanvas().drawText("Observaciones: " + observacionesMascota, x, y, texto);
        y += 25;

        page.getCanvas().drawLine(x, y, 550, y, linea);
        y += 25;

        page.getCanvas().drawText("DATOS DEL DUEÑO", x, y, subtitulo);
        y += espacio;

        page.getCanvas().drawText("Nombre: " + nombreDueno, x, y, texto);
        y += espacio;
        page.getCanvas().drawText("Teléfono: " + telefonoDueno, x, y, texto);
        y += espacio;
        page.getCanvas().drawText("Correo: " + correoDueno, x, y, texto);
        y += espacio;
        page.getCanvas().drawText("Dirección: " + direccionDueno, x, y, texto);
        y += 25;

        page.getCanvas().drawLine(x, y, 550, y, linea);
        y += 25;

        if (cbVacunas.isChecked()) {
            page.getCanvas().drawText("CARNET DE VACUNACIÓN", x, y, subtitulo);
            y += espacio;
            y = escribirVacunas(page, x, y, espacio, texto);
            y += 15;
        }

        if (cbHistorial.isChecked()) {
            page.getCanvas().drawText("HISTORIAL MÉDICO", x, y, subtitulo);
            y += espacio;
            y = escribirHistorial(page, x, y, espacio, texto);
        }

        document.finishPage(page);

        try {
            File file = new File(getExternalFilesDir(null), "reporte_" + nombreMascota + ".pdf");

            FileOutputStream fos = new FileOutputStream(file);
            document.writeTo(fos);
            document.close();
            fos.close();

            Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".provider", file);

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("application/pdf");
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            startActivity(Intent.createChooser(intent, "Compartir PDF"));

        } catch (Exception e) {
            tvMensajeShare.setText("Error generando el PDF.");
        }
    }

    private int escribirVacunas(PdfDocument.Page page, int x, int y, int espacio, Paint paint) {
        SharedPreferences prefs = getSharedPreferences("vacunas", MODE_PRIVATE);
        String json = prefs.getString("vacunas_json", "[]");
        boolean encontroVacunas = false;

        try {
            JSONArray arr = new JSONArray(json);

            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);

                if (obj.optInt("pet_index", -1) != petIndex) continue;

                encontroVacunas = true;

                String vacuna = obj.optString("vacuna", "-");
                String fechaAplicacion = obj.optString("fechaAplicacion", "-");
                String proximaDosis = obj.optString("proximaDosis", "-");

                page.getCanvas().drawText("• Vacuna: " + vacuna, x, y, paint);
                y += espacio;
                page.getCanvas().drawText("  Fecha aplicación: " + fechaAplicacion, x + 10, y, paint);
                y += espacio;
                page.getCanvas().drawText("  Próxima dosis: " + proximaDosis, x + 10, y, paint);
                y += espacio;
            }

            if (!encontroVacunas) {
                page.getCanvas().drawText("No hay vacunas registradas.", x, y, paint);
                y += espacio;
            }

        } catch (Exception ignored) {
        }

        return y;
    }

    private int escribirHistorial(PdfDocument.Page page, int x, int y, int espacio, Paint paint) {
        SharedPreferences prefs = getSharedPreferences("historial", MODE_PRIVATE);
        String json = prefs.getString("historial_json", "[]");
        boolean encontroHistorial = false;

        try {
            JSONArray arr = new JSONArray(json);

            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);

                if (obj.optInt("pet_index", -1) != petIndex) continue;

                encontroHistorial = true;

                String fechaRegistro = obj.optString("fechaRegistro", "-");
                String enfermedades = obj.optString("enfermedades", "-");
                String procedimientos = obj.optString("procedimientos", "-");
                String medicacion = obj.optString("medicacion", "-");

                page.getCanvas().drawText("• Fecha: " + fechaRegistro, x, y, paint);
                y += espacio;
                page.getCanvas().drawText("  Enfermedades: " + enfermedades, x + 10, y, paint);
                y += espacio;
                page.getCanvas().drawText("  Procedimientos: " + procedimientos, x + 10, y, paint);
                y += espacio;
                page.getCanvas().drawText("  Medicación: " + medicacion, x + 10, y, paint);
                y += espacio;
            }

            if (!encontroHistorial) {
                page.getCanvas().drawText("No hay historial médico registrado.", x, y, paint);
                y += espacio;
            }

        } catch (Exception ignored) {
        }

        return y;
    }
}