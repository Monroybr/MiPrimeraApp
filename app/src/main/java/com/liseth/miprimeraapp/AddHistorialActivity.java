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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class AddHistorialActivity extends AppCompatActivity {

    private Spinner spMascotasHist;
    private EditText etFechaRegistroHist, etEnfermedades, etProcedimientos, etMedicacion;
    private Button btnGuardarHistorial;
    private TextView tvMensajeHistorial;

    private final ArrayList<String> mascotasNombres = new ArrayList<>();
    private final ArrayList<Integer> mascotasIndex = new ArrayList<>();

    private Calendar fechaRegistroCal = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_historial);

        spMascotasHist = findViewById(R.id.spMascotasHist);
        etFechaRegistroHist = findViewById(R.id.etFechaRegistroHist);
        etEnfermedades = findViewById(R.id.etEnfermedades);
        etProcedimientos = findViewById(R.id.etProcedimientos);
        etMedicacion = findViewById(R.id.etMedicacion);
        btnGuardarHistorial = findViewById(R.id.btnGuardarHistorial);
        tvMensajeHistorial = findViewById(R.id.tvMensajeHistorial);

        cargarMascotasEnSpinner();

        // Si venimos desde PetDetailActivity
        int petIndexFromDetail = getIntent().getIntExtra("pet_index", -1);
        if (petIndexFromDetail != -1) {
            int pos = mascotasIndex.indexOf(petIndexFromDetail);
            if (pos != -1) spMascotasHist.setSelection(pos);
        }

        // Fecha por defecto: hoy (opcional)
        setFechaHoy();

        etFechaRegistroHist.setOnClickListener(v -> mostrarDatePicker());

        btnGuardarHistorial.setOnClickListener(v -> guardarHistorial());
    }

    private void setFechaHoy() {
        Calendar c = Calendar.getInstance();
        fechaRegistroCal = (Calendar) c.clone();
        String fecha = String.format(Locale.getDefault(), "%02d/%02d/%04d",
                c.get(Calendar.DAY_OF_MONTH), (c.get(Calendar.MONTH) + 1), c.get(Calendar.YEAR));
        etFechaRegistroHist.setText(fecha);
    }

    private void mostrarDatePicker() {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(this, (view, y, m, d) -> {
            fechaRegistroCal = Calendar.getInstance();
            fechaRegistroCal.set(y, m, d);

            String fecha = String.format(Locale.getDefault(), "%02d/%02d/%04d", d, (m + 1), y);
            etFechaRegistroHist.setText(fecha);
        }, year, month, day);

        dialog.show();
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
        } catch (Exception ignored) { }

        if (mascotasNombres.isEmpty()) {
            mascotasNombres.add("No hay mascotas registradas");
            mascotasIndex.add(-1);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, mascotasNombres);
        spMascotasHist.setAdapter(adapter);
    }

    private void guardarHistorial() {
        int idxMascota = mascotasIndex.get(spMascotasHist.getSelectedItemPosition());
        if (idxMascota == -1) {
            tvMensajeHistorial.setText("Primero registra una mascota.");
            return;
        }

        String fechaRegistro = etFechaRegistroHist.getText().toString().trim();
        String enfermedades = etEnfermedades.getText().toString().trim();
        String procedimientos = etProcedimientos.getText().toString().trim();
        String medicacion = etMedicacion.getText().toString().trim();

        if (fechaRegistro.isEmpty()) {
            tvMensajeHistorial.setText("Selecciona la fecha del registro.");
            return;
        }

        if (enfermedades.isEmpty() && procedimientos.isEmpty() && medicacion.isEmpty()) {
            tvMensajeHistorial.setText("Escribe al menos un dato (enfermedades, procedimientos o medicación).");
            return;
        }

        SharedPreferences prefs = getSharedPreferences("historial", MODE_PRIVATE);
        String json = prefs.getString("historial_json", "[]");

        try {
            JSONArray arr = new JSONArray(json);

            JSONObject obj = new JSONObject();
            obj.put("pet_index", idxMascota);
            obj.put("fechaRegistro", fechaRegistro);
            obj.put("enfermedades", enfermedades);
            obj.put("procedimientos", procedimientos);
            obj.put("medicacion", medicacion);

            arr.put(obj);

            prefs.edit().putString("historial_json", arr.toString()).apply();

            tvMensajeHistorial.setText("Historial guardado ✅");
            finish();

        } catch (Exception e) {
            tvMensajeHistorial.setText("Error guardando historial.");
        }
    }
}