package com.liseth.miprimeraapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

public class HistorialDetailActivity extends AppCompatActivity {

    private TextView tvFechaDetalle, tvEnfermedadesDetalle, tvProcedimientosDetalle, tvMedicacionDetalle, tvMensajeDetalle;
    private Button btnEliminarHistorial;

    private int globalIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial_detail);

        tvFechaDetalle = findViewById(R.id.tvFechaDetalle);
        tvEnfermedadesDetalle = findViewById(R.id.tvEnfermedadesDetalle);
        tvProcedimientosDetalle = findViewById(R.id.tvProcedimientosDetalle);
        tvMedicacionDetalle = findViewById(R.id.tvMedicacionDetalle);
        tvMensajeDetalle = findViewById(R.id.tvMensajeDetalle);
        btnEliminarHistorial = findViewById(R.id.btnEliminarHistorial);

        globalIndex = getIntent().getIntExtra("hist_global_index", -1);

        if (globalIndex == -1) {
            tvMensajeDetalle.setText("Registro no encontrado.");
            return;
        }

        cargarRegistro(globalIndex);

        btnEliminarHistorial.setOnClickListener(v -> eliminarRegistro(globalIndex));
    }

    private void cargarRegistro(int index) {
        SharedPreferences prefs = getSharedPreferences("historial", MODE_PRIVATE);
        String json = prefs.getString("historial_json", "[]");

        try {
            JSONArray arr = new JSONArray(json);

            if (index < 0 || index >= arr.length()) {
                tvMensajeDetalle.setText("Registro no encontrado.");
                return;
            }

            JSONObject obj = arr.getJSONObject(index);

            String fecha = obj.optString("fechaRegistro", "-");
            String enf = obj.optString("enfermedades", "");
            String proc = obj.optString("procedimientos", "");
            String med = obj.optString("medicacion", "");

            tvFechaDetalle.setText("Fecha: " + fecha);
            tvEnfermedadesDetalle.setText("Enfermedades: " + (enf.isEmpty() ? "-" : enf));
            tvProcedimientosDetalle.setText("Procedimientos: " + (proc.isEmpty() ? "-" : proc));
            tvMedicacionDetalle.setText("Medicación: " + (med.isEmpty() ? "-" : med));

        } catch (Exception e) {
            tvMensajeDetalle.setText("Error cargando el registro.");
        }
    }

    private void eliminarRegistro(int index) {
        SharedPreferences prefs = getSharedPreferences("historial", MODE_PRIVATE);
        String json = prefs.getString("historial_json", "[]");

        try {
            JSONArray arr = new JSONArray(json);

            if (index < 0 || index >= arr.length()) {
                tvMensajeDetalle.setText("No se pudo eliminar (índice inválido).");
                return;
            }

            JSONArray nuevo = new JSONArray();
            for (int i = 0; i < arr.length(); i++) {
                if (i == index) continue;
                nuevo.put(arr.getJSONObject(i));
            }

            prefs.edit().putString("historial_json", nuevo.toString()).apply();

            tvMensajeDetalle.setText("Registro eliminado ✅");
            finish();

        } catch (Exception e) {
            tvMensajeDetalle.setText("Error eliminando el registro.");
        }
    }
}