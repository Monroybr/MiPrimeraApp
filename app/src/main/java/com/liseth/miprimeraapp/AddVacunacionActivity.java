package com.liseth.miprimeraapp;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
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

    // Estas listas me ayudan a cargar mascotas y vacunas en los Spinner
    private final ArrayList<String> mascotasNombres = new ArrayList<>();
    private final ArrayList<Integer> mascotasIndex = new ArrayList<>();
    private final ArrayList<String> vacunas = new ArrayList<>();

    private Calendar fechaAplicacionCal = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vacunacion);

        // Relaciono las variables con los elementos del XML
        spMascotas = findViewById(R.id.spMascotas);
        spVacunas = findViewById(R.id.spVacunas);
        etFechaAplicacion = findViewById(R.id.etFechaAplicacion);
        etLugar = findViewById(R.id.etLugar);
        tvProximaDosis = findViewById(R.id.tvProximaDosis);
        tvMensajeVacuna = findViewById(R.id.tvMensajeVacuna);
        btnGuardarVacuna = findViewById(R.id.btnGuardarVacuna);

        cargarMascotasEnSpinner();
        cargarVacunasEnSpinner();

        // Abro el calendario cuando el usuario toca la fecha
        etFechaAplicacion.setOnClickListener(v -> mostrarDatePicker());

        // Cada vez que cambia la vacuna, recalculo la próxima dosis
        spVacunas.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                actualizarProximaDosis();
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
            }
        });

        // Evento para guardar la vacunación
        btnGuardarVacuna.setOnClickListener(v -> guardarVacunacion());
    }

    // Este método carga las mascotas registradas en el Spinner
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

    // Este método carga la lista base de vacunas
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

    // Aquí muestro un DatePicker para seleccionar la fecha de aplicación
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

    // Este método calcula y muestra la próxima dosis
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

    // Según la vacuna, determino si la siguiente dosis es a 6 o 12 meses
    private int mesesSegunVacuna(String vacuna) {
        if (vacuna.contains("6 meses")) return 6;
        return 12;
    }

    // Aquí guardo la vacuna y además programo el recordatorio exacto
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

        if (fechaAplicacionCal == null) {
            tvMensajeVacuna.setText("Selecciona una fecha válida.");
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

            // Aquí muestro la notificación inmediata de confirmación
            NotificationHelper.mostrarNotificacion(
                    this,
                    "Vacuna registrada",
                    "Se registró la vacuna \"" + vacuna + "\" para " + nombreMascota + ". Próxima dosis: " + proximaDosis
            );

            // Aquí programo la alarma exacta para la próxima dosis
            programarRecordatorioExacto(nombreMascota, vacuna, proximaDosis, proxima);

            tvMensajeVacuna.setText("Vacunación guardada ✅");
            finish();

        } catch (Exception e) {
            tvMensajeVacuna.setText("Error guardando vacunación.");
        }
    }

    // Este método programa una alarma exacta para la próxima dosis
    private void programarRecordatorioExacto(String nombreMascota, String vacuna, String proximaDosis, Calendar fechaRecordatorio) {

        // Aquí ajusto la hora para que la notificación salga a las 8:00 a. m.
        fechaRecordatorio.set(Calendar.HOUR_OF_DAY, 8);
        fechaRecordatorio.set(Calendar.MINUTE, 0);
        fechaRecordatorio.set(Calendar.SECOND, 0);
        fechaRecordatorio.set(Calendar.MILLISECOND, 0);

        Intent intent = new Intent(this, VaccineReminderReceiver.class);
        intent.putExtra("nombreMascota", nombreMascota);
        intent.putExtra("vacuna", vacuna);
        intent.putExtra("proximaDosis", proximaDosis);

        // Uso un requestCode único para evitar conflictos entre alarmas
        int requestCode = (nombreMascota + vacuna + proximaDosis).hashCode();

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        if (alarmManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        fechaRecordatorio.getTimeInMillis(),
                        pendingIntent
                );
            } else {
                alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        fechaRecordatorio.getTimeInMillis(),
                        pendingIntent
                );
            }
        }
    }
}