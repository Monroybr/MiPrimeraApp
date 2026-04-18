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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class AddCitaActivity extends AppCompatActivity {

    // Aquí declaro los elementos de la interfaz
    private Spinner spMascotasCita;
    private EditText etFechaCita, etHoraCita, etVeterinariaCita, etMotivoCita;
    private TextView tvMensajeCita;
    private Button btnGuardarCita;

    // Estas listas me sirven para cargar mascotas en el spinner
    private final ArrayList<String> mascotasNombres = new ArrayList<>();
    private final ArrayList<Integer> mascotasIndex = new ArrayList<>();

    private Calendar fechaCitaCal = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_cita);

        // Aquí relaciono las variables con los elementos del XML
        spMascotasCita = findViewById(R.id.spMascotasCita);
        etFechaCita = findViewById(R.id.etFechaCita);
        etHoraCita = findViewById(R.id.etHoraCita);
        etVeterinariaCita = findViewById(R.id.etVeterinariaCita);
        etMotivoCita = findViewById(R.id.etMotivoCita);
        tvMensajeCita = findViewById(R.id.tvMensajeCita);
        btnGuardarCita = findViewById(R.id.btnGuardarCita);

        cargarMascotasEnSpinner();

        // Aquí abro el DatePicker al tocar la fecha
        etFechaCita.setOnClickListener(v -> mostrarDatePicker());

        // Aquí guardo la cita
        btnGuardarCita.setOnClickListener(v -> guardarCita());
    }

    // Este método carga las mascotas registradas en el spinner
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
        spMascotasCita.setAdapter(adapter);
    }

    // Aquí muestro el calendario para seleccionar la fecha de la cita
    private void mostrarDatePicker() {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(this, (view, y, m, d) -> {
            fechaCitaCal = Calendar.getInstance();
            fechaCitaCal.set(y, m, d);

            String fecha = String.format(Locale.getDefault(), "%02d/%02d/%04d", d, (m + 1), y);
            etFechaCita.setText(fecha);
        }, year, month, day);

        dialog.show();
    }

    // Aquí guardo la cita y programo la notificación exacta
    private void guardarCita() {
        int idxMascota = mascotasIndex.get(spMascotasCita.getSelectedItemPosition());

        if (idxMascota == -1) {
            tvMensajeCita.setText("Primero registra una mascota.");
            return;
        }

        String nombreMascota = spMascotasCita.getSelectedItem().toString();
        String fecha = etFechaCita.getText().toString().trim();
        String hora = etHoraCita.getText().toString().trim();
        String veterinaria = etVeterinariaCita.getText().toString().trim();
        String motivo = etMotivoCita.getText().toString().trim();

        if (fecha.isEmpty() || hora.isEmpty() || veterinaria.isEmpty() || motivo.isEmpty()) {
            tvMensajeCita.setText("Completa todos los campos de la cita.");
            return;
        }

        if (fechaCitaCal == null) {
            tvMensajeCita.setText("Selecciona una fecha válida.");
            return;
        }

        SharedPreferences prefs = getSharedPreferences("citas", MODE_PRIVATE);
        String json = prefs.getString("citas_json", "[]");

        try {
            JSONArray arr = new JSONArray(json);

            JSONObject obj = new JSONObject();
            obj.put("pet_index", idxMascota);
            obj.put("nombreMascota", nombreMascota);
            obj.put("fecha", fecha);
            obj.put("hora", hora);
            obj.put("veterinaria", veterinaria);
            obj.put("motivo", motivo);

            arr.put(obj);

            prefs.edit().putString("citas_json", arr.toString()).apply();

            // Aquí muestro la notificación inmediata de confirmación
            NotificationHelper.mostrarNotificacion(
                    this,
                    "Cita registrada",
                    "Se registró una cita para " + nombreMascota + " el " + fecha + " a las " + hora + "."
            );

            // Aquí programo la notificación exacta para el día de la cita
            programarRecordatorioCita(nombreMascota, fecha, hora, veterinaria);

            tvMensajeCita.setText("Cita registrada ✅");
            finish();

        } catch (Exception e) {
            tvMensajeCita.setText("Error guardando la cita.");
        }
    }

    // Aquí programo la alarma exacta de la cita
    private void programarRecordatorioCita(String nombreMascota, String fecha, String hora, String veterinaria) {

        // Aquí convierto la hora escrita por el usuario a horas y minutos
        String[] partesHora = hora.split(":");
        int horaEntera = 8;
        int minutoEntero = 0;

        try {
            if (partesHora.length == 2) {
                horaEntera = Integer.parseInt(partesHora[0]);
                minutoEntero = Integer.parseInt(partesHora[1]);
            }
        } catch (Exception ignored) {
        }

        // Aquí defino la hora exacta de la alarma
        fechaCitaCal.set(Calendar.HOUR_OF_DAY, horaEntera);
        fechaCitaCal.set(Calendar.MINUTE, minutoEntero);
        fechaCitaCal.set(Calendar.SECOND, 0);
        fechaCitaCal.set(Calendar.MILLISECOND, 0);

        Intent intent = new Intent(this, CitaReminderReceiver.class);
        intent.putExtra("nombreMascota", nombreMascota);
        intent.putExtra("fecha", fecha);
        intent.putExtra("hora", hora);
        intent.putExtra("veterinaria", veterinaria);

        int requestCode = (nombreMascota + fecha + hora).hashCode();

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
                        fechaCitaCal.getTimeInMillis(),
                        pendingIntent
                );
            } else {
                alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        fechaCitaCal.getTimeInMillis(),
                        pendingIntent
                );
            }
        }
    }
}