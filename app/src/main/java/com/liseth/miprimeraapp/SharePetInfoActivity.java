package com.liseth.miprimeraapp;

import android.content.Intent;
import android.database.Cursor;
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

    // Aquí recibo el id real de la mascota en SQLite
    private int petId = -1;

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

    // Por ahora dejo estos datos del dueño como valores por defecto
    private String nombreDueno = "No registrado";
    private String telefonoDueno = "-";
    private String correoDueno = "-";
    private String direccionDueno = "-";

    private MascotaDAO mascotaDAO;
    private VacunaDAO vacunaDAO;
    private HistorialDAO historialDAO;
    private CitaDAO citaDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_pet_info);

        mascotaDAO = new MascotaDAO(this);
        vacunaDAO = new VacunaDAO(this);
        historialDAO = new HistorialDAO(this);
        citaDAO = new CitaDAO(this);

        tvNombreMascotaShare = findViewById(R.id.tvNombreMascotaShare);
        tvMensajeShare = findViewById(R.id.tvMensajeShare);
        cbVacunas = findViewById(R.id.cbVacunas);
        cbHistorial = findViewById(R.id.cbHistorial);
        btnGenerarCompartirPdf = findViewById(R.id.btnGenerarCompartirPdf);

        petId = getIntent().getIntExtra("pet_id", -1);

        if (petId == -1) {
            tvMensajeShare.setText("No se pudo identificar la mascota.");
            btnGenerarCompartirPdf.setEnabled(false);
            return;
        }

        cargarDatosMascotaDesdeSQLite(petId);

        tvNombreMascotaShare.setText("Mascota: " + nombreMascota);

        btnGenerarCompartirPdf.setOnClickListener(v -> generarYCompartirPdf());
    }

    // Este metodo carga los datos de la mascota desde SQLite
    private void cargarDatosMascotaDesdeSQLite(int id) {
        Cursor cursor = mascotaDAO.obtenerMascotaPorId(id);

        try {
            if (cursor != null && cursor.moveToFirst()) {
                nombreMascota = obtenerValorSeguro(cursor.getString(cursor.getColumnIndexOrThrow("nombre")));
                edadMascota = obtenerValorSeguro(cursor.getString(cursor.getColumnIndexOrThrow("edad_texto")));
                razaMascota = obtenerValorSeguro(cursor.getString(cursor.getColumnIndexOrThrow("raza")));
                sexoMascota = obtenerValorSeguro(cursor.getString(cursor.getColumnIndexOrThrow("sexo")));
                pesoMascota = obtenerValorSeguro(cursor.getString(cursor.getColumnIndexOrThrow("peso")));
                colorMascota = obtenerValorSeguro(cursor.getString(cursor.getColumnIndexOrThrow("color")));
                alergiasMascota = obtenerValorSeguro(cursor.getString(cursor.getColumnIndexOrThrow("alergias")));
                caracteristicasMascota = obtenerValorSeguro(cursor.getString(cursor.getColumnIndexOrThrow("caracteristicas")));
                observacionesMascota = obtenerValorSeguro(cursor.getString(cursor.getColumnIndexOrThrow("observaciones")));
                imagenMascotaUri = obtenerValorSeguro(cursor.getString(cursor.getColumnIndexOrThrow("imagen_uri")));

                if (imagenMascotaUri.equals("-")) {
                    imagenMascotaUri = "";
                }
            }
        } catch (Exception ignored) {
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
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

        // Aquí agrego la foto de la mascota si existe
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
            y = escribirVacunasDesdeSQLite(page, x, y, espacio, texto);
            y += 15;
        }

        if (cbHistorial.isChecked()) {
            page.getCanvas().drawText("HISTORIAL MÉDICO", x, y, subtitulo);
            y += espacio;
            y = escribirHistorialDesdeSQLite(page, x, y, espacio, texto);
            y += 15;
        }

        page.getCanvas().drawText("CITAS VETERINARIAS", x, y, subtitulo);
        y += espacio;
        y = escribirCitasDesdeSQLite(page, x, y, espacio, texto);

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

    // Este metodo escribe las vacunas registradas desde SQLite
    private int escribirVacunasDesdeSQLite(PdfDocument.Page page, int x, int y, int espacio, Paint paint) {
        boolean encontroVacunas = false;

        Cursor cursor = vacunaDAO.obtenerVacunasPorMascota(petId);

        try {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    encontroVacunas = true;

                    String vacuna = obtenerValorSeguro(cursor.getString(cursor.getColumnIndexOrThrow("vacuna")));
                    String fechaAplicacion = obtenerValorSeguro(cursor.getString(cursor.getColumnIndexOrThrow("fecha_aplicacion")));
                    String lugar = obtenerValorSeguro(cursor.getString(cursor.getColumnIndexOrThrow("lugar")));
                    String proximaDosis = obtenerValorSeguro(cursor.getString(cursor.getColumnIndexOrThrow("proxima_dosis")));

                    page.getCanvas().drawText("• Vacuna: " + vacuna, x, y, paint);
                    y += espacio;
                    page.getCanvas().drawText("  Fecha aplicación: " + fechaAplicacion, x + 10, y, paint);
                    y += espacio;
                    page.getCanvas().drawText("  Lugar: " + lugar, x + 10, y, paint);
                    y += espacio;
                    page.getCanvas().drawText("  Próxima dosis: " + proximaDosis, x + 10, y, paint);
                    y += espacio;

                } while (cursor.moveToNext());
            }

            if (!encontroVacunas) {
                page.getCanvas().drawText("No hay vacunas registradas.", x, y, paint);
                y += espacio;
            }

        } catch (Exception ignored) {
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return y;
    }

    // Este metodo escribe el historial médico desde SQLite
    private int escribirHistorialDesdeSQLite(PdfDocument.Page page, int x, int y, int espacio, Paint paint) {
        boolean encontroHistorial = false;

        Cursor cursor = historialDAO.obtenerHistorialPorMascota(petId);

        try {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    encontroHistorial = true;

                    String fechaRegistro = obtenerValorSeguro(cursor.getString(cursor.getColumnIndexOrThrow("fecha_registro")));
                    String enfermedades = obtenerValorSeguro(cursor.getString(cursor.getColumnIndexOrThrow("enfermedades")));
                    String procedimientos = obtenerValorSeguro(cursor.getString(cursor.getColumnIndexOrThrow("procedimientos")));
                    String medicacion = obtenerValorSeguro(cursor.getString(cursor.getColumnIndexOrThrow("medicacion")));
                    String observaciones = obtenerValorSeguro(cursor.getString(cursor.getColumnIndexOrThrow("observaciones")));

                    page.getCanvas().drawText("• Fecha: " + fechaRegistro, x, y, paint);
                    y += espacio;
                    page.getCanvas().drawText("  Enfermedades: " + enfermedades, x + 10, y, paint);
                    y += espacio;
                    page.getCanvas().drawText("  Procedimientos: " + procedimientos, x + 10, y, paint);
                    y += espacio;
                    page.getCanvas().drawText("  Medicación: " + medicacion, x + 10, y, paint);
                    y += espacio;
                    page.getCanvas().drawText("  Observaciones: " + observaciones, x + 10, y, paint);
                    y += espacio;

                } while (cursor.moveToNext());
            }

            if (!encontroHistorial) {
                page.getCanvas().drawText("No hay historial médico registrado.", x, y, paint);
                y += espacio;
            }

        } catch (Exception ignored) {
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return y;
    }

    // Este metodo escribe las citas veterinarias desde SQLite
    private int escribirCitasDesdeSQLite(PdfDocument.Page page, int x, int y, int espacio, Paint paint) {
        boolean encontroCitas = false;

        Cursor cursor = citaDAO.obtenerCitasPorMascota(petId);

        try {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    encontroCitas = true;

                    String fecha = obtenerValorSeguro(cursor.getString(cursor.getColumnIndexOrThrow("fecha")));
                    String hora = obtenerValorSeguro(cursor.getString(cursor.getColumnIndexOrThrow("hora")));
                    String veterinaria = obtenerValorSeguro(cursor.getString(cursor.getColumnIndexOrThrow("veterinaria")));
                    String motivo = obtenerValorSeguro(cursor.getString(cursor.getColumnIndexOrThrow("motivo")));

                    page.getCanvas().drawText("• Fecha: " + fecha + " - Hora: " + hora, x, y, paint);
                    y += espacio;
                    page.getCanvas().drawText("  Veterinaria: " + veterinaria, x + 10, y, paint);
                    y += espacio;
                    page.getCanvas().drawText("  Motivo: " + motivo, x + 10, y, paint);
                    y += espacio;

                } while (cursor.moveToNext());
            }

            if (!encontroCitas) {
                page.getCanvas().drawText("No hay citas veterinarias registradas.", x, y, paint);
                y += espacio;
            }

        } catch (Exception ignored) {
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return y;
    }
}