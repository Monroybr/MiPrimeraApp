package com.liseth.miprimeraapp;

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

public class VacunasListActivity extends AppCompatActivity {

    private RecyclerView rvVacunas;
    private TextView tvVacioVacunas;

    // Aquí recibo el id real de la mascota desde SQLite
    private int petId = -1;

    private VacunaDAO vacunaDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vacunas_list);

        rvVacunas = findViewById(R.id.rvVacunas);
        tvVacioVacunas = findViewById(R.id.tvVacioVacunas);

        vacunaDAO = new VacunaDAO(this);

        rvVacunas.setLayoutManager(new LinearLayoutManager(this));

        petId = getIntent().getIntExtra("pet_id", -1);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (petId == -1) {
            tvVacioVacunas.setVisibility(View.VISIBLE);
            tvVacioVacunas.setText("No se pudo identificar la mascota.");
            rvVacunas.setAdapter(new VacunaAdapter(new ArrayList<>()));

            Toast.makeText(this, "Error: no llegó pet_id desde PetDetailActivity", Toast.LENGTH_LONG).show();
            return;
        }

        ArrayList<Vacuna> lista = cargarVacunasDeMascota(petId);

        ordenarPorFechaAplicacionDesc(lista);

        tvVacioVacunas.setVisibility(lista.isEmpty() ? View.VISIBLE : View.GONE);

        rvVacunas.setAdapter(new VacunaAdapter(lista));
    }

    // Este metodo carga desde SQLite las vacunas de la mascota seleccionada
    private ArrayList<Vacuna> cargarVacunasDeMascota(int mascotaId) {
        ArrayList<Vacuna> lista = new ArrayList<>();

        Cursor cursor = vacunaDAO.obtenerVacunasPorMascota(mascotaId);

        try {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                    int mascota = cursor.getInt(cursor.getColumnIndexOrThrow("mascota_id"));
                    String vacuna = cursor.getString(cursor.getColumnIndexOrThrow("vacuna"));
                    String fechaAplicacion = cursor.getString(cursor.getColumnIndexOrThrow("fecha_aplicacion"));
                    String lugar = cursor.getString(cursor.getColumnIndexOrThrow("lugar"));
                    String proxima = cursor.getString(cursor.getColumnIndexOrThrow("proxima_dosis"));

                    lista.add(new Vacuna(
                            id,
                            mascota,
                            vacuna,
                            fechaAplicacion,
                            lugar,
                            proxima
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

    // Este metodo ordena las vacunas por fecha de aplicación
    private void ordenarPorFechaAplicacionDesc(ArrayList<Vacuna> lista) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        Collections.sort(lista, (a, b) -> {
            try {
                Date da = sdf.parse(a.fechaAplicacion);
                Date db = sdf.parse(b.fechaAplicacion);

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