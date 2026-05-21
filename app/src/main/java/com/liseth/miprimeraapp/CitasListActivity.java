package com.liseth.miprimeraapp;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CitasListActivity extends AppCompatActivity {

    private RecyclerView rvCitas;
    private TextView tvVacioCitas;

    private int petId = -1;

    private CitaDAO citaDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_citas_list);

        rvCitas = findViewById(R.id.rvCitas);
        tvVacioCitas = findViewById(R.id.tvVacioCitas);

        citaDAO = new CitaDAO(this);

        rvCitas.setLayoutManager(new LinearLayoutManager(this));

        // Aquí recibo el id real de la mascota desde PetDetailActivity
        petId = getIntent().getIntExtra("pet_id", -1);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (petId == -1) {
            tvVacioCitas.setVisibility(View.VISIBLE);
            tvVacioCitas.setText("No se pudo identificar la mascota.");
            rvCitas.setAdapter(new CitaAdapter(new ArrayList<>()));

            Toast.makeText(this, "Error: no llegó pet_id desde PetDetailActivity", Toast.LENGTH_LONG).show();
            return;
        }

        ArrayList<Cita> lista = cargarCitasDeMascota(petId);

        tvVacioCitas.setVisibility(lista.isEmpty() ? View.VISIBLE : View.GONE);

        rvCitas.setAdapter(new CitaAdapter(lista));
    }

    // Este metodo carga desde SQLite las citas de la mascota seleccionada
    private ArrayList<Cita> cargarCitasDeMascota(int mascotaId) {
        ArrayList<Cita> lista = new ArrayList<>();

        Cursor cursor = citaDAO.obtenerCitasPorMascota(mascotaId);

        try {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                    int mascota = cursor.getInt(cursor.getColumnIndexOrThrow("mascota_id"));
                    String nombreMascota = cursor.getString(cursor.getColumnIndexOrThrow("nombre_mascota"));
                    String fecha = cursor.getString(cursor.getColumnIndexOrThrow("fecha"));
                    String hora = cursor.getString(cursor.getColumnIndexOrThrow("hora"));
                    String veterinaria = cursor.getString(cursor.getColumnIndexOrThrow("veterinaria"));
                    String motivo = cursor.getString(cursor.getColumnIndexOrThrow("motivo"));

                    lista.add(new Cita(
                            id,
                            mascota,
                            nombreMascota,
                            fecha,
                            hora,
                            veterinaria,
                            motivo
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
}