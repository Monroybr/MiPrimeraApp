package com.liseth.miprimeraapp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Calendar;

public class AddPetActivity extends AppCompatActivity {

    // Aquí declaro los campos del formulario de registro de mascota
    private EditText etNombreMascota, etFechaNacimientoMascota, etRaza, etCaracteristicas,
            etVacunas, etHistorialMedico, etSexo, etPeso, etColor, etAlergias, etObservaciones;

    private TextView tvEdad, tvMensajeAddPet;
    private Button btnGuardarMascota, btnSeleccionarImagen, btnTomarFoto;
    private ImageView imgMascota;

    // Aquí guardo la URI de la imagen seleccionada
    private Uri imagenMascotaUri = null;

    // Códigos para identificar selección de imagen y cámara
    private static final int REQUEST_IMAGE_PICK = 1001;
    private static final int REQUEST_IMAGE_CAMERA = 1002;

    // Variables para guardar la fecha seleccionada y calcular la edad
    private int birthYear = -1;
    private int birthMonth = -1;
    private int birthDay = -1;

    // Variables para saber si estoy editando
    private boolean modoEdicion = false;
    private int petIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pet);

        // Aquí relaciono las variables con los elementos del XML
        etNombreMascota = findViewById(R.id.etNombreMascota);
        etFechaNacimientoMascota = findViewById(R.id.etFechaNacimientoMascota);
        tvEdad = findViewById(R.id.tvEdad);
        etRaza = findViewById(R.id.etRaza);
        etCaracteristicas = findViewById(R.id.etCaracteristicas);
        etVacunas = findViewById(R.id.etVacunas);
        etHistorialMedico = findViewById(R.id.etHistorialMedico);

        etSexo = findViewById(R.id.etSexo);
        etPeso = findViewById(R.id.etPeso);
        etColor = findViewById(R.id.etColor);
        etAlergias = findViewById(R.id.etAlergias);
        etObservaciones = findViewById(R.id.etObservaciones);

        imgMascota = findViewById(R.id.imgMascota);
        btnSeleccionarImagen = findViewById(R.id.btnSeleccionarImagen);
        btnTomarFoto = findViewById(R.id.btnTomarFoto);

        btnGuardarMascota = findViewById(R.id.btnGuardarMascota);
        tvMensajeAddPet = findViewById(R.id.tvMensajeAddPet);

        // Aquí identifico si estoy en modo edición
        modoEdicion = getIntent().getBooleanExtra("modo_edicion", false);
        petIndex = getIntent().getIntExtra("pet_index", -1);

        if (modoEdicion) {
            btnGuardarMascota.setText("Guardar cambios");
            cargarDatosMascotaParaEditar();
        }

        // Aquí abro el calendario al tocar el campo de fecha
        etFechaNacimientoMascota.setOnClickListener(v -> mostrarDatePicker());

        // Aquí abro el selector de imágenes
        btnSeleccionarImagen.setOnClickListener(v -> abrirGaleria());

        // Aquí abro la cámara
        btnTomarFoto.setOnClickListener(v -> abrirCamara());

        // Aquí guardo la mascota cuando se presiona el botón
        btnGuardarMascota.setOnClickListener(v -> guardarMascota());
    }

    // Este método carga los datos actuales cuando voy a editar
    private void cargarDatosMascotaParaEditar() {
        SharedPreferences prefs = getSharedPreferences("mascotas", MODE_PRIVATE);
        String json = prefs.getString("mascotas_json", "[]");

        try {
            JSONArray arr = new JSONArray(json);

            if (petIndex >= 0 && petIndex < arr.length()) {
                JSONObject obj = arr.getJSONObject(petIndex);

                etNombreMascota.setText(obj.optString("nombre", ""));
                etFechaNacimientoMascota.setText(obj.optString("fechaNacimiento", ""));
                etRaza.setText(obj.optString("raza", ""));
                etCaracteristicas.setText(obj.optString("caracteristicas", ""));
                etVacunas.setText(obj.optString("vacunas", ""));
                etHistorialMedico.setText(obj.optString("historial", ""));
                etSexo.setText(obj.optString("sexo", ""));
                etPeso.setText(obj.optString("peso", ""));
                etColor.setText(obj.optString("color", ""));
                etAlergias.setText(obj.optString("alergias", ""));
                etObservaciones.setText(obj.optString("observaciones", ""));

                String edad = obj.optString("edadTexto", "-");
                tvEdad.setText("Edad: " + edad);

                String imagenUriTexto = obj.optString("imagenUri", "");
                if (!imagenUriTexto.isEmpty()) {
                    imagenMascotaUri = Uri.parse(imagenUriTexto);
                    imgMascota.setImageURI(imagenMascotaUri);
                }
            }
        } catch (Exception e) {
            tvMensajeAddPet.setText("Error cargando datos para editar.");
        }
    }

    // Este método abre la galería para seleccionar una foto
    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }

    // Este método abre la cámara
    private void abrirCamara() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_IMAGE_CAMERA);
    }

    // Aquí recibo la imagen seleccionada o la foto tomada
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK && data != null && data.getData() != null) {
                imagenMascotaUri = data.getData();

                final int takeFlags = data.getFlags()
                        & (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                try {
                    getContentResolver().takePersistableUriPermission(imagenMascotaUri, takeFlags);
                } catch (Exception ignored) {
                }

                imgMascota.setImageURI(imagenMascotaUri);
            }

            if (requestCode == REQUEST_IMAGE_CAMERA && resultCode == RESULT_OK && data != null) {
                Bitmap foto = (Bitmap) data.getExtras().get("data");

                if (foto != null) {
                    imgMascota.setImageBitmap(foto);

                    String path = MediaStore.Images.Media.insertImage(
                            getContentResolver(),
                            foto,
                            "Mascota_" + System.currentTimeMillis(),
                            null
                    );

                    if (path != null) {
                        imagenMascotaUri = Uri.parse(path);
                    }
                }
            }
        } catch (Exception e) {
            tvMensajeAddPet.setText("Error cargando la imagen.");
        }
    }

    // Este método muestra el calendario para seleccionar la fecha de nacimiento
    private void mostrarDatePicker() {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(this, (view, y, m, d) -> {
            birthYear = y;
            birthMonth = m;
            birthDay = d;

            String fecha = String.format("%02d/%02d/%04d", d, (m + 1), y);
            etFechaNacimientoMascota.setText(fecha);

            tvEdad.setText("Edad: " + calcularEdadTexto(y, m, d));
        }, year, month, day);

        dialog.show();
    }

    // Este método calcula la edad de la mascota con base en la fecha seleccionada
    private String calcularEdadTexto(int y, int m, int d) {
        Calendar hoy = Calendar.getInstance();
        Calendar nacimiento = Calendar.getInstance();
        nacimiento.set(y, m, d);

        if (nacimiento.after(hoy)) return "-";

        int years = hoy.get(Calendar.YEAR) - nacimiento.get(Calendar.YEAR);
        int months = hoy.get(Calendar.MONTH) - nacimiento.get(Calendar.MONTH);
        int days = hoy.get(Calendar.DAY_OF_MONTH) - nacimiento.get(Calendar.DAY_OF_MONTH);

        if (days < 0) months -= 1;

        if (months < 0) {
            years -= 1;
            months += 12;
        }

        if (years < 0) return "-";
        if (years == 0) return months + " meses";

        return years + " años, " + months + " meses";
    }

    // Este método guarda o actualiza toda la información de la mascota
    private void guardarMascota() {
        String nombre = etNombreMascota.getText().toString().trim();
        String fecha = etFechaNacimientoMascota.getText().toString().trim();
        String raza = etRaza.getText().toString().trim();
        String caracteristicas = etCaracteristicas.getText().toString().trim();
        String vacunas = etVacunas.getText().toString().trim();
        String historial = etHistorialMedico.getText().toString().trim();

        String sexo = etSexo.getText().toString().trim();
        String peso = etPeso.getText().toString().trim();
        String color = etColor.getText().toString().trim();
        String alergias = etAlergias.getText().toString().trim();
        String observaciones = etObservaciones.getText().toString().trim();

        // Aquí valido los campos mínimos obligatorios
        if (nombre.isEmpty() || fecha.isEmpty() || raza.isEmpty()) {
            tvMensajeAddPet.setText("Completa al menos: Nombre, Fecha de nacimiento y Raza.");
            return;
        }

        String edad;
        if (birthYear == -1) {
            String textoEdad = tvEdad.getText().toString().replace("Edad: ", "").trim();
            edad = textoEdad.isEmpty() ? "-" : textoEdad;
        } else {
            edad = calcularEdadTexto(birthYear, birthMonth, birthDay);
        }

        SharedPreferences prefs = getSharedPreferences("mascotas", MODE_PRIVATE);
        String json = prefs.getString("mascotas_json", "[]");

        try {
            JSONArray arr = new JSONArray(json);

            JSONObject obj = new JSONObject();
            obj.put("nombre", nombre);
            obj.put("fechaNacimiento", fecha);
            obj.put("edadTexto", edad);
            obj.put("raza", raza);
            obj.put("caracteristicas", caracteristicas);
            obj.put("vacunas", vacunas);
            obj.put("historial", historial);
            obj.put("sexo", sexo);
            obj.put("peso", peso);
            obj.put("color", color);
            obj.put("alergias", alergias);
            obj.put("observaciones", observaciones);
            obj.put("imagenUri", imagenMascotaUri != null ? imagenMascotaUri.toString() : "");

            // Aquí decido si agrego una mascota nueva o actualizo una existente
            if (modoEdicion && petIndex >= 0 && petIndex < arr.length()) {
                arr.put(petIndex, obj);
            } else {
                arr.put(obj);
            }

            prefs.edit().putString("mascotas_json", arr.toString()).apply();

            tvMensajeAddPet.setText(modoEdicion ? "Mascota actualizada ✅" : "Mascota registrada ✅");
            finish();

        } catch (Exception e) {
            tvMensajeAddPet.setText("Error guardando la mascota.");
        }
    }
}