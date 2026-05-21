package com.liseth.miprimeraapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.Locale;

public class AddCitaActivity extends AppCompatActivity {

    private TextView tvNombreMascotaCita, tvMensajeCita;
    private EditText etFechaCita, etHoraCita, etVeterinaria, etMotivoCita;
    private Button btnGuardarCita;

    private int petId = -1;
    private String nombreMascota = "Mascota";

    private MascotaDAO mascotaDAO;
    private CitaDAO citaDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_cita);

        // Aquí inicializo los DAO para consultar mascotas y guardar citas
        mascotaDAO = new MascotaDAO(this);
        citaDAO = new CitaDAO(this);

        tvNombreMascotaCita = findViewById(R.id.tvNombreMascotaCita);
        tvMensajeCita = findViewById(R.id.tvMensajeCita);
        etFechaCita = findViewById(R.id.etFechaCita);
        etHoraCita = findViewById(R.id.etHoraCita);
        etVeterinaria = findViewById(R.id.etVeterinaria);
        etMotivoCita = findViewById(R.id.etMotivoCita);
        btnGuardarCita = findViewById(R.id.btnGuardarCita);

        // Aquí recibo el id real de la mascota desde PetDetailActivity
        petId = getIntent().getIntExtra("pet_id", -1);

        if (petId == -1) {
            tvMensajeCita.setText("No se pudo identificar la mascota.");
            btnGuardarCita.setEnabled(false);
            return;
        }

        cargarNombreMascota();

        etFechaCita.setOnClickListener(v -> mostrarDatePicker());
        etHoraCita.setOnClickListener(v -> mostrarTimePicker());

        btnGuardarCita.setOnClickListener(v -> guardarCita());
    }

    // Este metodo consulta el nombre de la mascota desde SQLite
    private void cargarNombreMascota() {
        Cursor cursor = mascotaDAO.obtenerMascotaPorId(petId);

        try {
            if (cursor != null && cursor.moveToFirst()) {
                nombreMascota = cursor.getString(cursor.getColumnIndexOrThrow("nombre"));
                tvNombreMascotaCita.setText("Mascota: " + nombreMascota);
            } else {
                tvNombreMascotaCita.setText("Mascota: -");
            }
        } catch (Exception e) {
            tvNombreMascotaCita.setText("Mascota: -");
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    // Este metodo abre el calendario para seleccionar la fecha de la cita
    private void mostrarDatePicker() {
        Calendar c = Calendar.getInstance();

        DatePickerDialog dialog = new DatePickerDialog(this, (view, y, m, d) -> {
            String fecha = String.format(Locale.getDefault(), "%02d/%02d/%04d", d, (m + 1), y);
            etFechaCita.setText(fecha);
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));

        dialog.show();
    }

    // Este metodo abre el selector de hora para la cita
    private void mostrarTimePicker() {
        Calendar c = Calendar.getInstance();

        TimePickerDialog dialog = new TimePickerDialog(this, (view, hourOfDay, minute) -> {
            String hora = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
            etHoraCita.setText(hora);
        }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true);

        dialog.show();
    }

    // Este metodo guarda la cita veterinaria en SQLite
    private void guardarCita() {
        String fecha = etFechaCita.getText().toString().trim();
        String hora = etHoraCita.getText().toString().trim();
        String veterinaria = etVeterinaria.getText().toString().trim();
        String motivo = etMotivoCita.getText().toString().trim();

        if (fecha.isEmpty() || hora.isEmpty() || veterinaria.isEmpty() || motivo.isEmpty()) {
            tvMensajeCita.setText("Completa todos los campos.");
            return;
        }

        long resultado = citaDAO.insertarCita(
                petId,
                nombreMascota,
                fecha,
                hora,
                veterinaria,
                motivo
        );

        if (resultado != -1) {
            NotificationHelper.mostrarNotificacion(
                    this,
                    "Cita veterinaria registrada",
                    "Se agendó una cita para " + nombreMascota + " el " + fecha + " a las " + hora + "."
            );

            tvMensajeCita.setText("Cita registrada ✅");
            finish();
        } else {
            tvMensajeCita.setText("Error guardando la cita.");
        }
    }
}