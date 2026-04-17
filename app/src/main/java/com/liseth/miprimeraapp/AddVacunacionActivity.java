package com.liseth.miprimeraapp;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class AddVacunacionActivity extends AppCompatActivity {

    private Spinner spMascotas, spVacunas;
    private EditText etFechaAplicacion, etLugar;
    private TextView tvProximaDosis, tvMensajeVacuna;
    private Button btnGuardarVacuna;

    private final ArrayList<String> mascotasNombres = new ArrayList<>();
    private final ArrayList<Integer> mascotasIndex = new ArrayList<>();
    private final ArrayList<String> vacunas = new ArrayList<>();

    private Calendar fechaAplicacionCal = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vacunacion);

        spMascotas = findViewById(R.id.spMascotas);
        spVacunas = findViewById(R.id.spVacunas);
        etFechaAplicacion = findViewById(R.id.etFechaAplicacion);
        etLugar = findViewById(R.id.etLugar);
        tvProximaDosis = findViewById(R.id.tvProximaDosis);
        tvMensajeVacuna = findViewById(R.id.tvMensajeVacuna);
        btnGuardarVacuna = findViewById(R.id.btnGuardarVacuna);

        cargarMascotasEnSpinner();
        cargarVacunasEnSpinner();

        etFechaAplicacion.setOnClickListener(v -> mostrarDatePicker());

        spVacunas.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                actualizarProximaDosis();
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
            }
        });

        btnGuardarVacuna.setOnClickListener(v -> guardarVacunacion());
    }

    private void cargarMascotasEnSpinner() {
        mascotasNombres.clear();
        mascotasIndex.clear();

        SharedPreferences prefs = getSharedPreferences("mascotas", MODE_PRIVATE);
        String json = prefs.getString("mascotas_json", "[]");

        try {
            JSONArray arr = new JSONArray(json);

            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                String nombre = obj.optString("nombre", "Mascota " + (i + 1));
                mascotasNombres.add(nombre);
                mascotasIndex.add(i);
            }
        } catch (Exception ignored) {
        }

        if (mascotasNombres.isEmpty()) {
            mascotasNombres.add("No hay mascotas registradas");
            mascotasIndex.add(-1);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, mascotasNombres);
        spMascotas.setAdapter(adapter);
    }

    private void cargarVacunasEnSpinner() {
        vacunas.clear();
        vacunas.add("Rabia (anual)");
        vacunas.add("Moquillo/Parvovirus (anual)");
        vacunas.add("Leptospirosis (anual)");
        vacunas.add("Triple Felina (anual)");
        vacunas.add("Bordetella (cada 6 meses)");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, vacunas);
        spVacunas.setAdapter(adapter);
    }

    private void mostrarDatePicker() {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(this, (view, y, m, d) -> {
            fechaAplicacionCal = Calendar.getInstance();
            fechaAplicacionCal.set(y, m, d);

            String fecha = String.format(Locale.getDefault(), "%02d/%02d/%04d", d, (m + 1), y);
            etFechaAplicacion.setText(fecha);

            actualizarProximaDosis();
        }, year, month, day);

        dialog.show();
    }

    private void actualizarProximaDosis() {
        if (fechaAplicacionCal == null) {
            tvProximaDosis.setText("Próxima dosis: -");
            return;
        }

        String vacuna = (String) spVacunas.getSelectedItem();
        Calendar proxima = (Calendar) fechaAplicacionCal.clone();

        int meses = mesesSegunVacuna(vacuna);
        proxima.add(Calendar.MONTH, meses);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        tvProximaDosis.setText("Próxima dosis: " + sdf.format(proxima.getTime()));
    }

    private int mesesSegunVacuna(String vacuna) {
        if (vacuna.contains("6 meses")) return 6;
        return 12;
    }

    private void guardarVacunacion() {
        int idxMascota = mascotasIndex.get(spMascotas.getSelectedItemPosition());
        if (idxMascota == -1) {
            tvMensajeVacuna.setText("Primero registra una mascota.");
            return;
        }

        String nombreMascota = spMascotas.getSelectedItem().toString();
        String vacuna = (String) spVacunas.getSelectedItem();
        String fechaAplicacion = etFechaAplicacion.getText().toString().trim();
        String lugar = etLugar.getText().toString().trim();

        if (fechaAplicacion.isEmpty() || lugar.isEmpty()) {
            tvMensajeVacuna.setText("Completa fecha de aplicación y lugar.");
            return;
        }

        Calendar proxima = (Calendar) fechaAplicacionCal.clone();
        proxima.add(Calendar.MONTH, mesesSegunVacuna(vacuna));
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String proximaDosis = sdf.format(proxima.getTime());

        SharedPreferences prefs = getSharedPreferences("vacunas", MODE_PRIVATE);
        String json = prefs.getString("vacunas_json", "[]");

        try {
            JSONArray arr = new JSONArray(json);

            JSONObject obj = new JSONObject();
            obj.put("pet_index", idxMascota);
            obj.put("vacuna", vacuna);
            obj.put("fechaAplicacion", fechaAplicacion);
            obj.put("lugar", lugar);
            obj.put("proximaDosis", proximaDosis);

            arr.put(obj);

            prefs.edit().putString("vacunas_json", arr.toString()).apply();

            // Aquí muestro la notificación real en el celular cuando se registra la vacuna
            NotificationHelper.mostrarNotificacion(
                    this,
                    "Vacuna registrada",
                    "Se registró la vacuna \"" + vacuna + "\" para " + nombreMascota + ". Próxima dosis: " + proximaDosis
            );

            tvMensajeVacuna.setText("Vacunación guardada ✅");
            finish();

        } catch (Exception e) {
            tvMensajeVacuna.setText("Error guardando vacunación.");
        }
    }
}