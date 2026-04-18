package com.liseth.miprimeraapp;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class OwnerProfileActivity extends AppCompatActivity {

    // Aquí declaro los campos del perfil del dueño
    private EditText etNombresPerfil, etApellidosPerfil, etFechaNacimientoPerfil,
            etCorreoPerfil, etTelefonoPerfil, etDireccionPerfil;

    private Button btnGuardarPerfil;
    private TextView tvMensajePerfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_profile);

        // Aquí relaciono las variables con los elementos del XML
        etNombresPerfil = findViewById(R.id.etNombresPerfil);
        etApellidosPerfil = findViewById(R.id.etApellidosPerfil);
        etFechaNacimientoPerfil = findViewById(R.id.etFechaNacimientoPerfil);
        etCorreoPerfil = findViewById(R.id.etCorreoPerfil);
        etTelefonoPerfil = findViewById(R.id.etTelefonoPerfil);
        etDireccionPerfil = findViewById(R.id.etDireccionPerfil);

        btnGuardarPerfil = findViewById(R.id.btnGuardarPerfil);
        tvMensajePerfil = findViewById(R.id.tvMensajePerfil);

        // Aquí cargo la información ya guardada del dueño
        cargarDatosPerfil();

        // Aquí abro el calendario para editar la fecha de nacimiento
        etFechaNacimientoPerfil.setOnClickListener(v -> mostrarDatePicker());

        // Aquí guardo los cambios del perfil
        btnGuardarPerfil.setOnClickListener(v -> guardarPerfil());
    }

    // Este método carga los datos guardados del dueño desde SharedPreferences
    private void cargarDatosPerfil() {
        SharedPreferences prefs = getSharedPreferences("usuarios", MODE_PRIVATE);

        etNombresPerfil.setText(prefs.getString("nombres", ""));
        etApellidosPerfil.setText(prefs.getString("apellidos", ""));
        etFechaNacimientoPerfil.setText(prefs.getString("fechaNacimiento", ""));
        etCorreoPerfil.setText(prefs.getString("correo", ""));
        etTelefonoPerfil.setText(prefs.getString("telefono", ""));
        etDireccionPerfil.setText(prefs.getString("direccion", ""));
    }

    // Este método muestra el calendario para seleccionar la fecha
    private void mostrarDatePicker() {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(this, (view, y, m, d) -> {
            String fecha = String.format("%02d/%02d/%04d", d, (m + 1), y);
            etFechaNacimientoPerfil.setText(fecha);
        }, year, month, day);

        dialog.show();
    }

    // Este método guarda los cambios del perfil del dueño
    private void guardarPerfil() {
        String nombres = etNombresPerfil.getText().toString().trim();
        String apellidos = etApellidosPerfil.getText().toString().trim();
        String fechaNacimiento = etFechaNacimientoPerfil.getText().toString().trim();
        String correo = etCorreoPerfil.getText().toString().trim();
        String telefono = etTelefonoPerfil.getText().toString().trim();
        String direccion = etDireccionPerfil.getText().toString().trim();

        // Aquí valido que los campos importantes no estén vacíos
        if (nombres.isEmpty() || apellidos.isEmpty() || correo.isEmpty()) {
            tvMensajePerfil.setText("Completa al menos nombres, apellidos y correo.");
            return;
        }

        SharedPreferences prefs = getSharedPreferences("usuarios", MODE_PRIVATE);
        prefs.edit()
                .putString("nombres", nombres)
                .putString("apellidos", apellidos)
                .putString("fechaNacimiento", fechaNacimiento)
                .putString("correo", correo)
                .putString("telefono", telefono)
                .putString("direccion", direccion)
                .apply();

        tvMensajePerfil.setText("Perfil actualizado correctamente ✅");
    }
}