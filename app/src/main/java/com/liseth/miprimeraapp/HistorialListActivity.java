package com.liseth.miprimeraapp;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

public class HistorialListActivity extends AppCompatActivity {

    private RecyclerView rvHistorial;
    private TextView tvVacioHistorial;

    // Aquí recibo el id real de la mascota desde SQLite
    private int petId = -1;

    private HistorialDAO historialDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial_list);

        rvHistorial = findViewById(R.id.rvHistorial);
        tvVacioHistorial = findViewById(R.id.tvVacioHistorial);

        historialDAO = new HistorialDAO(this);

        rvHistorial.setLayoutManager(new LinearLayoutManager(this));

        petId = getIntent().getIntExtra("pet_id", -1);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (petId == -1) {
            tvVacioHistorial.setVisibility(View.VISIBLE);
            tvVacioHistorial.setText("No se pudo identificar la mascota.");
            rvHistorial.setAdapter(new HistorialAdapter(new ArrayList<>(), null));

            Toast.makeText(this, "Error: no llegó pet_id desde PetDetailActivity", Toast.LENGTH_LONG).show();
            return;
        }

        ArrayList<Historial> lista = cargarHistorialDeMascota(petId);

        ordenarPorFechaDesc(lista);

        tvVacioHistorial.setVisibility(lista.isEmpty() ? View.VISIBLE : View.GONE);

        rvHistorial.setAdapter(new HistorialAdapter(lista, (pos, item) -> {
            Intent intent = new Intent(HistorialListActivity.this, HistorialDetailActivity.class);

            // Aquí envío el id real del historial guardado en SQLite
            intent.putExtra("historial_id", item.id);

            startActivity(intent);
        }));
    }

    // Este metodo carga desde SQLite el historial clínico de la mascota seleccionada
    private ArrayList<Historial> cargarHistorialDeMascota(int mascotaId) {
        ArrayList<Historial> lista = new ArrayList<>();

        Cursor cursor = historialDAO.obtenerHistorialPorMascota(mascotaId);

        try {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                    int mascota = cursor.getInt(cursor.getColumnIndexOrThrow("mascota_id"));
                    String fecha = cursor.getString(cursor.getColumnIndexOrThrow("fecha_registro"));
                    String enfermedades = cursor.getString(cursor.getColumnIndexOrThrow("enfermedades"));
                    String procedimientos = cursor.getString(cursor.getColumnIndexOrThrow("procedimientos"));
                    String medicacion = cursor.getString(cursor.getColumnIndexOrThrow("medicacion"));
                    String observaciones = cursor.getString(cursor.getColumnIndexOrThrow("observaciones"));

                    lista.add(new Historial(
                            id,
                            mascota,
                            fecha,
                            enfermedades,
                            procedimientos,
                            medicacion,
                            observaciones
                    ));

                } while (cursor.moveToNext());
            }

        } catch (Exception ignored) {
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return lista;
    }

    // Este metodo ordena los registros por fecha descendente
    private void ordenarPorFechaDesc(ArrayList<Historial> lista) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        Collections.sort(lista, (a, b) -> {
            try {
                Date da = sdf.parse(a.fechaRegistro);
                Date db = sdf.parse(b.fechaRegistro);

                if (da == null && db == null) return 0;
                if (da == null) return 1;
                if (db == null) return -1;

                return db.compareTo(da);

            } catch (Exception e) {
                return 0;
            }
        });
    }
}