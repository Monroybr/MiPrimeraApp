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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SharePetInfoActivity extends AppCompatActivity {

    // Aquí declaro los componentes de la interfaz que voy a usar
    private TextView tvNombreMascotaShare, tvMensajeShare;
    private CheckBox cbVacunas, cbHistorial;
    private Button btnGenerarCompartirPdf;

    // Variables donde voy a guardar la información de la mascota
    private int petIndex = -1;
    private String nombreMascota = "Mascota";
    private String edadMascota = "-";
    private String caracteristicasMascota = "-";
    private String nombreDueno = "-";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_pet_info);

        // Aquí enlazo los elementos del XML con el código
        tvNombreMascotaShare = findViewById(R.id.tvNombreMascotaShare);
        tvMensajeShare = findViewById(R.id.tvMensajeShare);
        cbVacunas = findViewById(R.id.cbVacunas);
        cbHistorial = findViewById(R.id.cbHistorial);
        btnGenerarCompartirPdf = findViewById(R.id.btnGenerarCompartirPdf);

        // Obtengo el índice de la mascota que viene desde otra pantalla
        petIndex = getIntent().getIntExtra("pet_index", -1);

        // Valido que el índice exista
        if (petIndex == -1) {
            tvMensajeShare.setText("No se pudo identificar la mascota.");
            btnGenerarCompartirPdf.setEnabled(false);
            return;
        }

        // Cargo los datos de la mascota y del dueño
        cargarDatosMascota(petIndex);
        cargarDatosDueno();

        // Muestro el nombre de la mascota en pantalla
        tvNombreMascotaShare.setText("Mascota: " + nombreMascota);

        // Evento del botón para generar el PDF
        btnGenerarCompartirPdf.setOnClickListener(v -> generarYCompartirPdf());
    }

    // Este metodo lo uso para obtener los datos de la mascota desde SharedPreferences
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

                // Valido si las características están vacías
                if (caracteristicasMascota == null || caracteristicasMascota.trim().isEmpty()) {
                    caracteristicasMascota = "-";
                }
            }
        } catch (Exception ignored) {
        }
    }

    // Aquí obtengo el nombre del dueño desde el registro del usuario
    private void cargarDatosDueno() {
        SharedPreferences prefs = getSharedPreferences("usuarios", MODE_PRIVATE);

        String nombres = prefs.getString("nombres", "");
        String apellidos = prefs.getString("apellidos", "");

        nombreDueno = (nombres + " " + apellidos).trim();

        if (nombreDueno.isEmpty()) {
            nombreDueno = "No registrado";
        }
    }

    // Este es el metodo principal donde genero el PDF
    private void generarYCompartirPdf() {

        // Verifico que el usuario haya seleccionado al menos una opción
        if (!cbVacunas.isChecked() && !cbHistorial.isChecked()) {
            tvMensajeShare.setText("Selecciona al menos una sección.");
            return;
        }

        PdfDocument document = new PdfDocument();

        // Defino estilos de texto
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

        // Creo una página tamaño A4
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);

        int x = 40;
        int y = 50;
        int espacio = 20;

        // Obtengo la fecha actual
        String fecha = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date());

        // Encabezado del documento
        page.getCanvas().drawText("REPORTE DE LA MASCOTA", x, y, titulo);
        y += 25;
        page.getCanvas().drawText("Fecha: " + fecha, x, y, texto);
        y += 20;

        // Línea divisora
        page.getCanvas().drawLine(x, y, 550, y, linea);
        y += 25;

        // Sección de datos generales
        page.getCanvas().drawText("DATOS GENERALES", x, y, subtitulo);
        y += espacio;

        page.getCanvas().drawText("Nombre: " + nombreMascota, x, y, texto);
        y += espacio;

        page.getCanvas().drawText("Edad: " + edadMascota, x, y, texto);
        y += espacio;

        page.getCanvas().drawText("Características: " + caracteristicasMascota, x, y, texto);
        y += espacio;

        page.getCanvas().drawText("Dueño: " + nombreDueno, x, y, texto);
        y += 25;

        // Sección de vacunas
        if (cbVacunas.isChecked()) {
            page.getCanvas().drawText("CARNET DE VACUNACIÓN", x, y, subtitulo);
            y += espacio;

            y = escribirVacunas(page, x, y, espacio, texto);
            y += 15;
        }

        // Sección de historial
        if (cbHistorial.isChecked()) {
            page.getCanvas().drawText("HISTORIAL MÉDICO", x, y, subtitulo);
            y += espacio;

            y = escribirHistorial(page, x, y, espacio, texto);
        }

        document.finishPage(page);

        try {
            // Creo el archivo PDF
            File file = new File(getExternalFilesDir(null), "reporte_" + nombreMascota + ".pdf");

            FileOutputStream fos = new FileOutputStream(file);
            document.writeTo(fos);
            document.close();
            fos.close();

            // Uso FileProvider para compartir el archivo
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

    // Metodo para escribir las vacunas en el PDF
    private int escribirVacunas(PdfDocument.Page page, int x, int y, int espacio, Paint paint) {

        SharedPreferences prefs = getSharedPreferences("vacunas", MODE_PRIVATE);
        String json = prefs.getString("vacunas_json", "[]");

        try {
            JSONArray arr = new JSONArray(json);

            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);

                if (obj.optInt("pet_index", -1) != petIndex) continue;

                page.getCanvas().drawText("• " + obj.optString("vacuna", "-"), x, y, paint);
                y += espacio;
            }

        } catch (Exception ignored) {}

        return y;
    }

    // Metodo para escribir el historial en el PDF
    private int escribirHistorial(PdfDocument.Page page, int x, int y, int espacio, Paint paint) {

        SharedPreferences prefs = getSharedPreferences("historial", MODE_PRIVATE);
        String json = prefs.getString("historial_json", "[]");

        try {
            JSONArray arr = new JSONArray(json);

            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);

                if (obj.optInt("pet_index", -1) != petIndex) continue;

                page.getCanvas().drawText("• " + obj.optString("fechaRegistro", "-"), x, y, paint);
                y += espacio;
            }

        } catch (Exception ignored) {}

        return y;
    }
}