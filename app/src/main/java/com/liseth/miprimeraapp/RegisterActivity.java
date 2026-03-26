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

    private EditText etNombres, etApellidos, etFechaNacimiento, etCorreo, etContrasena;
    private Button btnCrearCuenta;
    private TextView tvMensajeRegistro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etNombres = findViewById(R.id.etNombres);
        etApellidos = findViewById(R.id.etApellidos);
        etFechaNacimiento = findViewById(R.id.etFechaNacimiento);
        etCorreo = findViewById(R.id.etCorreo);
        etContrasena = findViewById(R.id.etContrasena);
        btnCrearCuenta = findViewById(R.id.btnCrearCuenta);
        tvMensajeRegistro = findViewById(R.id.tvMensajeRegistro);

        // Abrir selector de fecha
        etFechaNacimiento.setOnClickListener(v -> mostrarDatePicker());

        btnCrearCuenta.setOnClickListener(v -> {

            String nombres = etNombres.getText().toString().trim();
            String apellidos = etApellidos.getText().toString().trim();
            String fecha = etFechaNacimiento.getText().toString().trim();
            String correo = etCorreo.getText().toString().trim();
            String contrasena = etContrasena.getText().toString().trim();

            if (nombres.isEmpty() || apellidos.isEmpty() || fecha.isEmpty() || correo.isEmpty() || contrasena.isEmpty()) {
                tvMensajeRegistro.setText("Por favor completa todos los campos.");
                return;
            }

            // Guardar datos (modo básico)
            SharedPreferences prefs = getSharedPreferences("usuarios", MODE_PRIVATE);
            prefs.edit()
                    .putString("correo", correo)
                    .putString("contrasena", contrasena)
                    .putString("nombres", nombres)
                    .putString("apellidos", apellidos)
                    .putString("fechaNacimiento", fecha)
                    .apply();

            tvMensajeRegistro.setText("Cuenta creada correctamente ✅. Ahora puedes iniciar sesión.");

            // Redirigir al Login
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

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
}