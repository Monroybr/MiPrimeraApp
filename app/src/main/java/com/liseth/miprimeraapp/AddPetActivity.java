package com.liseth.miprimeraapp;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Calendar;

public class AddPetActivity extends AppCompatActivity {

    private EditText etNombreMascota, etFechaNacimientoMascota, etRaza, etCaracteristicas, etVacunas, etHistorialMedico;
    private TextView tvEdad, tvMensajeAddPet;
    private Button btnGuardarMascota;

    private int birthYear = -1;
    private int birthMonth = -1; // 0-11
    private int birthDay = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pet);

        etNombreMascota = findViewById(R.id.etNombreMascota);
        etFechaNacimientoMascota = findViewById(R.id.etFechaNacimientoMascota);
        tvEdad = findViewById(R.id.tvEdad);
        etRaza = findViewById(R.id.etRaza);
        etCaracteristicas = findViewById(R.id.etCaracteristicas);
        etVacunas = findViewById(R.id.etVacunas);
        etHistorialMedico = findViewById(R.id.etHistorialMedico);
        btnGuardarMascota = findViewById(R.id.btnGuardarMascota);
        tvMensajeAddPet = findViewById(R.id.tvMensajeAddPet);

        etFechaNacimientoMascota.setOnClickListener(v -> mostrarDatePicker());
        btnGuardarMascota.setOnClickListener(v -> guardarMascota());
    }

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

    private void guardarMascota() {
        String nombre = etNombreMascota.getText().toString().trim();
        String fecha = etFechaNacimientoMascota.getText().toString().trim();
        String raza = etRaza.getText().toString().trim();
        String caracteristicas = etCaracteristicas.getText().toString().trim();
        String vacunas = etVacunas.getText().toString().trim();
        String historial = etHistorialMedico.getText().toString().trim();

        if (nombre.isEmpty() || fecha.isEmpty() || raza.isEmpty()) {
            tvMensajeAddPet.setText("Completa al menos: Nombre, Fecha de nacimiento y Raza.");
            return;
        }

        String edad = (birthYear == -1) ? "-" : calcularEdadTexto(birthYear, birthMonth, birthDay);

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

            arr.put(obj);

            prefs.edit().putString("mascotas_json", arr.toString()).apply();

            tvMensajeAddPet.setText("Mascota registrada ✅");
            finish();

        } catch (Exception e) {
            tvMensajeAddPet.setText("Error guardando la mascota.");
        }
    }
}