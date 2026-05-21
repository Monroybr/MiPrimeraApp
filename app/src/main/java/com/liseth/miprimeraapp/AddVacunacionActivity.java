package com.liseth.miprimeraapp;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class AddVacunacionActivity extends AppCompatActivity {

    private Spinner spMascotas, spVacunas;
    private EditText etFechaAplicacion, etLugar;
    private TextView tvProximaDosis, tvMensajeVacuna;
    private Button btnGuardarVacuna;

    // Estas listas me ayudan a cargar las mascotas desde SQLite en el Spinner
    private final ArrayList<String> mascotasNombres = new ArrayList<>();
    private final ArrayList<Integer> mascotasIds = new ArrayList<>();

    // Esta lista contiene las vacunas disponibles
    private final ArrayList<String> vacunas = new ArrayList<>();

    private Calendar fechaAplicacionCal = null;

    // Aquí declaro los DAO para consultar mascotas y guardar vacunas
    private MascotaDAO mascotaDAO;
    private VacunaDAO vacunaDAO;

    // Aquí recibo el id de la mascota cuando entro desde el detalle
    private int petIdRecibido = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vacunacion);

        mascotaDAO = new MascotaDAO(this);
        vacunaDAO = new VacunaDAO(this);

        spMascotas = findViewById(R.id.spMascotas);
        spVacunas = findViewById(R.id.spVacunas);
        etFechaAplicacion = findViewById(R.id.etFechaAplicacion);
        etLugar = findViewById(R.id.etLugar);
        tvProximaDosis = findViewById(R.id.tvProximaDosis);
        tvMensajeVacuna = findViewById(R.id.tvMensajeVacuna);
        btnGuardarVacuna = findViewById(R.id.btnGuardarVacuna);

        // Aquí recibo el id real de SQLite si la pantalla se abrió desde el detalle de una mascota
        petIdRecibido = getIntent().getIntExtra("pet_id", -1);

        cargarMascotasEnSpinner();
        cargarVacunasEnSpinner();

        // Si recibí una mascota específica, intento seleccionarla automáticamente
        seleccionarMascotaRecibida();

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

    // Este metodo carga las mascotas desde SQLite en el Spinner
    private void cargarMascotasEnSpinner() {
        mascotasNombres.clear();
        mascotasIds.clear();

        Cursor cursor = mascotaDAO.obtenerMascotas();

        try {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                    String nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"));

                    mascotasIds.add(id);
                    mascotasNombres.add(nombre);

                } while (cursor.moveToNext());
            }
        } catch (Exception ignored) {
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        if (mascotasNombres.isEmpty()) {
            mascotasNombres.add("No hay mascotas registradas");
            mascotasIds.add(-1);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                mascotasNombres
        );

        spMascotas.setAdapter(adapter);
    }

    // Este metodo selecciona automáticamente la mascota si vengo desde PetDetailActivity
    private void seleccionarMascotaRecibida() {
        if (petIdRecibido == -1) return;

        for (int i = 0; i < mascotasIds.size(); i++) {
            if (mascotasIds.get(i) == petIdRecibido) {
                spMascotas.setSelection(i);
                break;
            }
        }
    }

    // Este metodo carga la lista base de vacunas
    private void cargarVacunasEnSpinner() {
        vacunas.clear();
        vacunas.add("Rabia (anual)");
        vacunas.add("Moquillo/Parvovirus (anual)");
        vacunas.add("Leptospirosis (anual)");
        vacunas.add("Triple Felina (anual)");
        vacunas.add("Bordetella (cada 6 meses)");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                vacunas
        );

        spVacunas.setAdapter(adapter);
    }

    // Aquí muestro un calendario para seleccionar la fecha de aplicación
    private void mostrarDatePicker() {
        Calendar c = Calendar.getInstance();

        DatePickerDialog dialog = new DatePickerDialog(this, (view, y, m, d) -> {
            fechaAplicacionCal = Calendar.getInstance();
            fechaAplicacionCal.set(y, m, d);

            String fecha = String.format(Locale.getDefault(), "%02d/%02d/%04d", d, (m + 1), y);
            etFechaAplicacion.setText(fecha);

            actualizarProximaDosis();

        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));

        dialog.show();
    }

    // Este metodo calcula y muestra la próxima dosis
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

    // Según la vacuna, determino si la próxima dosis es a 6 o 12 meses
    private int mesesSegunVacuna(String vacuna) {
        if (vacuna != null && vacuna.contains("6 meses")) return 6;
        return 12;
    }

    // Aquí guardo la vacunación en SQLite y programo el recordatorio
    private void guardarVacunacion() {
        int posicionMascota = spMascotas.getSelectedItemPosition();

        if (posicionMascota < 0 || mascotasIds.isEmpty()) {
            tvMensajeVacuna.setText("Primero registra una mascota.");
            return;
        }

        int mascotaId = mascotasIds.get(posicionMascota);

        if (mascotaId == -1) {
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

        long resultado = vacunaDAO.insertarVacuna(
                mascotaId,
                vacuna,
                fechaAplicacion,
                lugar,
                proximaDosis
        );

        if (resultado != -1) {

            NotificationHelper.mostrarNotificacion(
                    this,
                    "Vacuna registrada",
                    "Se registró la vacuna \"" + vacuna + "\" para " + nombreMascota + ". Próxima dosis: " + proximaDosis
            );

            programarRecordatorioExacto(nombreMascota, vacuna, proximaDosis, proxima);

            tvMensajeVacuna.setText("Vacunación guardada ✅");
            finish();

        } else {
            tvMensajeVacuna.setText("Error guardando vacunación.");
        }
    }

    // Este metodo programa una alarma exacta para la próxima dosis
    private void programarRecordatorioExacto(String nombreMascota, String vacuna, String proximaDosis, Calendar fechaRecordatorio) {

        fechaRecordatorio.set(Calendar.HOUR_OF_DAY, 8);
        fechaRecordatorio.set(Calendar.MINUTE, 0);
        fechaRecordatorio.set(Calendar.SECOND, 0);
        fechaRecordatorio.set(Calendar.MILLISECOND, 0);

        Intent intent = new Intent(this, VaccineReminderReceiver.class);
        intent.putExtra("nombreMascota", nombreMascota);
        intent.putExtra("vacuna", vacuna);
        intent.putExtra("proximaDosis", proximaDosis);

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