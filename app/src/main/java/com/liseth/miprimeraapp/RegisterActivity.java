package com.liseth.miprimeraapp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class RegisterActivity extends AppCompatActivity {

    // Aquí declaro los campos del formulario de registro
    private EditText etNombres, etApellidos, etFechaNacimiento, etCorreo, etContrasena,
            etTelefono, etDireccion;

    private Button btnCrearCuenta;
    private TextView tvMensajeRegistro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Aquí relaciono las variables con los elementos del XML
        etNombres = findViewById(R.id.etNombres);
        etApellidos = findViewById(R.id.etApellidos);
        etFechaNacimiento = findViewById(R.id.etFechaNacimiento);
        etCorreo = findViewById(R.id.etCorreo);
        etContrasena = findViewById(R.id.etContrasena);
        etTelefono = findViewById(R.id.etTelefono);
        etDireccion = findViewById(R.id.etDireccion);

        btnCrearCuenta = findViewById(R.id.btnCrearCuenta);
        tvMensajeRegistro = findViewById(R.id.tvMensajeRegistro);

        // Aquí abro el selector de fecha al tocar el campo
        etFechaNacimiento.setOnClickListener(v -> mostrarDatePicker());

        // Aquí guardo los datos del usuario cuando presiona crear cuenta
        btnCrearCuenta.setOnClickListener(v -> guardarUsuario());
    }

    // Este método muestra el calendario para seleccionar la fecha de nacimiento
    private void mostrarDatePicker() {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(this, (view, y, m, d) -> {
            String fecha = String.format("%02d/%02d/%04d", d, (m + 1), y);
            etFechaNacimiento.setText(fecha);
        }, year, month, day);

        dialog.show();
    }

    // Este método valida y guarda la información del dueño
    private void guardarUsuario() {
        String nombres = etNombres.getText().toString().trim();
        String apellidos = etApellidos.getText().toString().trim();
        String fecha = etFechaNacimiento.getText().toString().trim();
        String correo = etCorreo.getText().toString().trim();
        String contrasena = etContrasena.getText().toString().trim();
        String telefono = etTelefono.getText().toString().trim();
        String direccion = etDireccion.getText().toString().trim();

        // Aquí valido que todos los campos estén completos
        if (nombres.isEmpty() || apellidos.isEmpty() || fecha.isEmpty()
                || correo.isEmpty() || contrasena.isEmpty()
                || telefono.isEmpty() || direccion.isEmpty()) {

            tvMensajeRegistro.setText("Por favor completa todos los campos.");
            return;
        }

        // Aquí guardo los datos del usuario en SharedPreferences
        SharedPreferences prefs = getSharedPreferences("usuarios", MODE_PRIVATE);
        prefs.edit()
                .putString("correo", correo)
                .putString("contrasena", contrasena)
                .putString("nombres", nombres)
                .putString("apellidos", apellidos)
                .putString("fechaNacimiento", fecha)
                .putString("telefono", telefono)
                .putString("direccion", direccion)
                .apply();

        tvMensajeRegistro.setText("Cuenta creada correctamente ✅. Ahora puedes iniciar sesión.");

        // Aquí redirijo al usuario a la pantalla de login
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}