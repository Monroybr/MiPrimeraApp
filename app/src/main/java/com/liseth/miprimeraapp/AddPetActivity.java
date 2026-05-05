package com.liseth.miprimeraapp;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.Calendar;

public class AddPetActivity extends AppCompatActivity {

    // Aquí declaro los campos del formulario de mascota
    private EditText etNombreMascota, etFechaNacimientoMascota, etRaza, etCaracteristicas,
            etVacunas, etHistorialMedico, etSexo, etPeso, etColor, etAlergias, etObservaciones;

    private TextView tvEdad, tvMensajeAddPet;
    private Button btnGuardarMascota, btnSeleccionarImagen, btnTomarFoto;
    private ImageView imgMascota;

    // Aquí guardo la URI de la foto seleccionada o tomada
    private Uri imagenMascotaUri = null;

    private static final int REQUEST_IMAGE_PICK = 1001;
    private static final int REQUEST_IMAGE_CAMERA = 1002;

    // Variables para calcular la edad
    private int birthYear = -1;
    private int birthMonth = -1;
    private int birthDay = -1;

    // Variables para saber si estoy registrando o editando
    private boolean modoEdicion = false;
    private int petId = -1;

    // Aquí declaro el DAO para guardar o actualizar mascotas en SQLite
    private MascotaDAO mascotaDAO;

    // Este launcher me permite solicitar permiso de cámara en Android
    private final ActivityResultLauncher<String> cameraPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    abrirCamaraDirectamente();
                } else {
                    tvMensajeAddPet.setText("Debes conceder permiso de cámara para tomar la foto.");
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pet);

        mascotaDAO = new MascotaDAO(this);

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

        // Aquí identifico si esta pantalla se abrió para editar una mascota
        modoEdicion = getIntent().getBooleanExtra("modo_edicion", false);
        petId = getIntent().getIntExtra("pet_id", -1);

        if (modoEdicion && petId != -1) {
            btnGuardarMascota.setText("Guardar cambios");
            cargarDatosMascotaParaEditar(petId);
        }

        etFechaNacimientoMascota.setOnClickListener(v -> mostrarDatePicker());

        btnSeleccionarImagen.setOnClickListener(v -> abrirGaleria());

        btnTomarFoto.setOnClickListener(v -> validarPermisoCamaraYAbrir());

        btnGuardarMascota.setOnClickListener(v -> guardarMascota());
    }

    // Este método carga los datos actuales de la mascota cuando estoy editando
    private void cargarDatosMascotaParaEditar(int id) {
        Cursor cursor = mascotaDAO.obtenerMascotaPorId(id);

        try {
            if (cursor != null && cursor.moveToFirst()) {
                etNombreMascota.setText(cursor.getString(cursor.getColumnIndexOrThrow("nombre")));
                etFechaNacimientoMascota.setText(cursor.getString(cursor.getColumnIndexOrThrow("fecha_nacimiento")));
                tvEdad.setText("Edad: " + cursor.getString(cursor.getColumnIndexOrThrow("edad_texto")));
                etRaza.setText(cursor.getString(cursor.getColumnIndexOrThrow("raza")));
                etCaracteristicas.setText(cursor.getString(cursor.getColumnIndexOrThrow("caracteristicas")));
                etVacunas.setText(cursor.getString(cursor.getColumnIndexOrThrow("vacunas")));
                etHistorialMedico.setText(cursor.getString(cursor.getColumnIndexOrThrow("historial")));
                etSexo.setText(cursor.getString(cursor.getColumnIndexOrThrow("sexo")));
                etPeso.setText(cursor.getString(cursor.getColumnIndexOrThrow("peso")));
                etColor.setText(cursor.getString(cursor.getColumnIndexOrThrow("color")));
                etAlergias.setText(cursor.getString(cursor.getColumnIndexOrThrow("alergias")));
                etObservaciones.setText(cursor.getString(cursor.getColumnIndexOrThrow("observaciones")));

                String imagen = cursor.getString(cursor.getColumnIndexOrThrow("imagen_uri"));

                if (imagen != null && !imagen.isEmpty()) {
                    imagenMascotaUri = Uri.parse(imagen);
                    imgMascota.setImageURI(imagenMascotaUri);
                }
            }
        } catch (Exception e) {
            tvMensajeAddPet.setText("Error cargando datos para editar.");
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    // Este método abre la galería del dispositivo
    private void abrirGaleria() {
        try {
            Intent intent;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
            } else {
                intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            }

            intent.setType("image/*");
            startActivityForResult(intent, REQUEST_IMAGE_PICK);

        } catch (Exception e) {
            tvMensajeAddPet.setText("No se pudo abrir la galería.");
        }
    }

    // Este método revisa si tengo permiso de cámara antes de abrirla
    private void validarPermisoCamaraYAbrir() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            abrirCamaraDirectamente();
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    // Este método abre directamente la cámara
    private void abrirCamaraDirectamente() {
        try {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(intent, REQUEST_IMAGE_CAMERA);
            } else {
                tvMensajeAddPet.setText("No se encontró aplicación de cámara.");
            }

        } catch (Exception e) {
            tvMensajeAddPet.setText("No se pudo abrir la cámara.");
        }
    }

    // Aquí recibo la foto de galería o cámara
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK) {
                if (data == null || data.getData() == null) {
                    tvMensajeAddPet.setText("No se pudo seleccionar la imagen.");
                    return;
                }

                imagenMascotaUri = data.getData();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    final int takeFlags = data.getFlags() & Intent.FLAG_GRANT_READ_URI_PERMISSION;

                    try {
                        getContentResolver().takePersistableUriPermission(imagenMascotaUri, takeFlags);
                    } catch (Exception ignored) {
                    }
                }

                imgMascota.setImageURI(imagenMascotaUri);
            }

            if (requestCode == REQUEST_IMAGE_CAMERA && resultCode == RESULT_OK) {
                if (data == null || data.getExtras() == null) {
                    tvMensajeAddPet.setText("No se pudo capturar la imagen.");
                    return;
                }

                Bitmap foto = (Bitmap) data.getExtras().get("data");

                if (foto != null) {
                    imgMascota.setImageBitmap(foto);

                    String path = MediaStore.Images.Media.insertImage(
                            getContentResolver(),
                            foto,
                            "Mascota_" + System.currentTimeMillis(),
                            null
                    );

                    if (path != null && !path.isEmpty()) {
                        imagenMascotaUri = Uri.parse(path);
                    }
                }
            }

        } catch (Exception e) {
            tvMensajeAddPet.setText("Error al cargar la imagen.");
        }
    }

    // Este método muestra el calendario para seleccionar fecha de nacimiento
    private void mostrarDatePicker() {
        Calendar c = Calendar.getInstance();

        DatePickerDialog dialog = new DatePickerDialog(this, (view, y, m, d) -> {
            birthYear = y;
            birthMonth = m;
            birthDay = d;

            String fecha = String.format("%02d/%02d/%04d", d, (m + 1), y);
            etFechaNacimientoMascota.setText(fecha);

            tvEdad.setText("Edad: " + calcularEdadTexto(y, m, d));

        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));

        dialog.show();
    }

    // Este método calcula la edad de la mascota
    private String calcularEdadTexto(int y, int m, int d) {
        Calendar hoy = Calendar.getInstance();
        Calendar nacimiento = Calendar.getInstance();
        nacimiento.set(y, m, d);

        if (nacimiento.after(hoy)) return "-";

        int years = hoy.get(Calendar.YEAR) - nacimiento.get(Calendar.YEAR);
        int months = hoy.get(Calendar.MONTH) - nacimiento.get(Calendar.MONTH);
        int days = hoy.get(Calendar.DAY_OF_MONTH) - nacimiento.get(Calendar.DAY_OF_MONTH);

        if (days < 0) months--;

        if (months < 0) {
            years--;
            months += 12;
        }

        if (years == 0) return months + " meses";

        return years + " años, " + months + " meses";
    }

    // Este método guarda una mascota nueva o actualiza una existente
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

        if (nombre.isEmpty() || fecha.isEmpty() || raza.isEmpty()) {
            tvMensajeAddPet.setText("Completa al menos: Nombre, Fecha de nacimiento y Raza.");
            return;
        }

        String edad;

        if (birthYear == -1) {
            edad = tvEdad.getText().toString().replace("Edad: ", "").trim();
        } else {
            edad = calcularEdadTexto(birthYear, birthMonth, birthDay);
        }

        String imagen = imagenMascotaUri != null ? imagenMascotaUri.toString() : "";

        if (modoEdicion && petId != -1) {
            int resultado = mascotaDAO.actualizarMascota(
                    petId,
                    nombre,
                    fecha,
                    edad,
                    raza,
                    caracteristicas,
                    vacunas,
                    historial,
                    sexo,
                    peso,
                    color,
                    alergias,
                    observaciones,
                    imagen
            );

            if (resultado > 0) {
                tvMensajeAddPet.setText("Mascota actualizada ✅");
                finish();
            } else {
                tvMensajeAddPet.setText("Error actualizando la mascota.");
            }

        } else {
            long resultado = mascotaDAO.insertarMascota(
                    nombre,
                    fecha,
                    edad,
                    raza,
                    caracteristicas,
                    vacunas,
                    historial,
                    sexo,
                    peso,
                    color,
                    alergias,
                    observaciones,
                    imagen
            );

            if (resultado != -1) {
                tvMensajeAddPet.setText("Mascota registrada ✅");
                finish();
            } else {
                tvMensajeAddPet.setText("Error guardando la mascota.");
            }
        }
    }
}