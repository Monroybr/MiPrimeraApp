package com.liseth.miprimeraapp;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class HistorialDetailActivity extends AppCompatActivity {

    private TextView tvFechaDetalle, tvEnfermedadesDetalle, tvProcedimientosDetalle,
            tvMedicacionDetalle, tvMensajeDetalle;

    private Button btnEliminarHistorial;

    // Aquí guardo el id real del historial en SQLite
    private int historialId = -1;

    private HistorialDAO historialDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial_detail);

        historialDAO = new HistorialDAO(this);

        tvFechaDetalle = findViewById(R.id.tvFechaDetalle);
        tvEnfermedadesDetalle = findViewById(R.id.tvEnfermedadesDetalle);
        tvProcedimientosDetalle = findViewById(R.id.tvProcedimientosDetalle);
        tvMedicacionDetalle = findViewById(R.id.tvMedicacionDetalle);
        tvMensajeDetalle = findViewById(R.id.tvMensajeDetalle);
        btnEliminarHistorial = findViewById(R.id.btnEliminarHistorial);

        historialId = getIntent().getIntExtra("historial_id", -1);

        if (historialId == -1) {
            tvMensajeDetalle.setText("Registro no encontrado.");
            return;
        }

        cargarRegistro(historialId);

        btnEliminarHistorial.setOnClickListener(v -> eliminarRegistro(historialId));
    }

    // Este metodo carga el registro clínico desde SQLite
    private void cargarRegistro(int id) {
        Cursor cursor = historialDAO.obtenerHistorialPorId(id);

        try {
            if (cursor != null && cursor.moveToFirst()) {
                String fecha = cursor.getString(cursor.getColumnIndexOrThrow("fecha_registro"));
                String enfermedades = cursor.getString(cursor.getColumnIndexOrThrow("enfermedades"));
                String procedimientos = cursor.getString(cursor.getColumnIndexOrThrow("procedimientos"));
                String medicacion = cursor.getString(cursor.getColumnIndexOrThrow("medicacion"));

                tvFechaDetalle.setText("Fecha: " + (fecha == null || fecha.isEmpty() ? "-" : fecha));
                tvEnfermedadesDetalle.setText("Enfermedades: " + (enfermedades == null || enfermedades.isEmpty() ? "-" : enfermedades));
                tvProcedimientosDetalle.setText("Procedimientos: " + (procedimientos == null || procedimientos.isEmpty() ? "-" : procedimientos));
                tvMedicacionDetalle.setText("Medicación: " + (medicacion == null || medicacion.isEmpty() ? "-" : medicacion));

            } else {
                tvMensajeDetalle.setText("Registro no encontrado.");
            }

        } catch (Exception e) {
            tvMensajeDetalle.setText("Error cargando el registro.");
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    // Este metodo elimina el registro clínico desde SQLite
    private void eliminarRegistro(int id) {
        int resultado = historialDAO.eliminarHistorial(id);

        if (resultado > 0) {
            tvMensajeDetalle.setText("Registro eliminado ✅");
            finish();
        } else {
            tvMensajeDetalle.setText("No se pudo eliminar el registro.");
        }
    }
}