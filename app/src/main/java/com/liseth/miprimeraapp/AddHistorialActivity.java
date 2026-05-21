package com.liseth.miprimeraapp;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class AddHistorialActivity extends AppCompatActivity {

    private Spinner spMascotasHist;
    private EditText etFechaRegistroHist, etEnfermedades, etProcedimientos, etMedicacion;
    private Button btnGuardarHistorial;
    private TextView tvMensajeHistorial;

    // Estas listas me permiten cargar las mascotas desde SQLite en el Spinner
    private final ArrayList<String> mascotasNombres = new ArrayList<>();
    private final ArrayList<Integer> mascotasIds = new ArrayList<>();

    private Calendar fechaRegistroCal = null;

    // Aquí declaro los DAO que voy a usar
    private MascotaDAO mascotaDAO;
    private HistorialDAO historialDAO;

    // Aquí recibo el id real de la mascota cuando vengo desde el detalle
    private int petIdRecibido = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_historial);

        mascotaDAO = new MascotaDAO(this);
        historialDAO = new HistorialDAO(this);

        spMascotasHist = findViewById(R.id.spMascotasHist);
        etFechaRegistroHist = findViewById(R.id.etFechaRegistroHist);
        etEnfermedades = findViewById(R.id.etEnfermedades);
        etProcedimientos = findViewById(R.id.etProcedimientos);
        etMedicacion = findViewById(R.id.etMedicacion);
        btnGuardarHistorial = findViewById(R.id.btnGuardarHistorial);
        tvMensajeHistorial = findViewById(R.id.tvMensajeHistorial);

        // Recibo el id real de SQLite si la pantalla se abrió desde PetDetailActivity
        petIdRecibido = getIntent().getIntExtra("pet_id", -1);

        cargarMascotasEnSpinner();
        seleccionarMascotaRecibida();

        setFechaHoy();

        etFechaRegistroHist.setOnClickListener(v -> mostrarDatePicker());

        btnGuardarHistorial.setOnClickListener(v -> guardarHistorial());
    }

    // Este metodo coloca la fecha actual por defecto
    private void setFechaHoy() {
        Calendar c = Calendar.getInstance();
        fechaRegistroCal = (Calendar) c.clone();

        String fecha = String.format(Locale.getDefault(), "%02d/%02d/%04d",
                c.get(Calendar.DAY_OF_MONTH),
                (c.get(Calendar.MONTH) + 1),
                c.get(Calendar.YEAR));

        etFechaRegistroHist.setText(fecha);
    }

    // Este metodo muestra el calendario para seleccionar la fecha del registro
    private void mostrarDatePicker() {
        Calendar c = Calendar.getInstance();

        DatePickerDialog dialog = new DatePickerDialog(this, (view, y, m, d) -> {
            fechaRegistroCal = Calendar.getInstance();
            fechaRegistroCal.set(y, m, d);

            String fecha = String.format(Locale.getDefault(), "%02d/%02d/%04d", d, (m + 1), y);
            etFechaRegistroHist.setText(fecha);

        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));

        dialog.show();
    }

    // Este metodo carga las mascotas desde SQLite
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

        spMascotasHist.setAdapter(adapter);
    }

    // Este metodo selecciona automáticamente la mascota si vengo desde el detalle
    private void seleccionarMascotaRecibida() {
        if (petIdRecibido == -1) return;

        for (int i = 0; i < mascotasIds.size(); i++) {
            if (mascotasIds.get(i) == petIdRecibido) {
                spMascotasHist.setSelection(i);
                break;
            }
        }
    }

    // Este metodo guarda el historial clínico en SQLite
    private void guardarHistorial() {
        int posicionMascota = spMascotasHist.getSelectedItemPosition();

        if (posicionMascota < 0 || mascotasIds.isEmpty()) {
            tvMensajeHistorial.setText("Primero registra una mascota.");
            return;
        }

        int mascotaId = mascotasIds.get(posicionMascota);

        if (mascotaId == -1) {
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
            tvMensajeHistorial.setText("Escribe al menos un dato: enfermedades, procedimientos o medicación.");
            return;
        }

        long resultado = historialDAO.insertarHistorial(
                mascotaId,
                fechaRegistro,
                enfermedades,
                procedimientos,
                medicacion,
                ""
        );

        if (resultado != -1) {
            tvMensajeHistorial.setText("Historial guardado ✅");
            finish();
        } else {
            tvMensajeHistorial.setText("Error guardando historial.");
        }
    }
}