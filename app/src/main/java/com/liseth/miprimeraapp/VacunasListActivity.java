package com.liseth.miprimeraapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

public class VacunasListActivity extends AppCompatActivity {

    private RecyclerView rvVacunas;
    private TextView tvVacioVacunas;

    private int petIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vacunas_list);

        rvVacunas = findViewById(R.id.rvVacunas);
        tvVacioVacunas = findViewById(R.id.tvVacioVacunas);

        rvVacunas.setLayoutManager(new LinearLayoutManager(this));

        petIndex = getIntent().getIntExtra("pet_index", -1);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // ✅ Si no llega el índice, mostramos mensaje y no intentamos cargar
        if (petIndex == -1) {
            tvVacioVacunas.setVisibility(View.VISIBLE);
            tvVacioVacunas.setText("No se pudo identificar la mascota (pet_index = -1).");
            rvVacunas.setAdapter(new VacunaAdapter(new ArrayList<>()));

            Toast.makeText(this, "Error: petIndex = -1 (no llegó desde PetDetailActivity)", Toast.LENGTH_LONG).show();
            return;
        }

        ArrayList<Vacuna> lista = cargarVacunasDeMascota(petIndex);

        // ✅ ordenar por fecha de aplicación (descendente)
        ordenarPorFechaAplicacionDesc(lista);

        // ✅ diagnóstico rápido
        Toast.makeText(this,
                "petIndex=" + petIndex + " | vacunas encontradas=" + lista.size(),
                Toast.LENGTH_LONG).show();

        tvVacioVacunas.setVisibility(lista.isEmpty() ? View.VISIBLE : View.GONE);

        rvVacunas.setAdapter(new VacunaAdapter(lista));
    }

    private ArrayList<Vacuna> cargarVacunasDeMascota(int petIndex) {
        ArrayList<Vacuna> lista = new ArrayList<>();

        SharedPreferences prefs = getSharedPreferences("vacunas", MODE_PRIVATE);
        String json = prefs.getString("vacunas_json", "[]");

        try {
            JSONArray arr = new JSONArray(json);

            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);

                int idx = obj.optInt("pet_index", -1);
                if (idx != petIndex) continue;

                String vacuna = obj.optString("vacuna", "");
                String fechaAplicacion = obj.optString("fechaAplicacion", "");
                String lugar = obj.optString("lugar", "");
                String proxima = obj.optString("proximaDosis", "");

                lista.add(new Vacuna(idx, vacuna, fechaAplicacion, lugar, proxima));
            }

        } catch (Exception ignored) { }

        return lista;
    }

    private void ordenarPorFechaAplicacionDesc(ArrayList<Vacuna> lista) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        Collections.sort(lista, (a, b) -> {
            try {
                Date da = sdf.parse(a.fechaAplicacion);
                Date db = sdf.parse(b.fechaAplicacion);

                if (da == null && db == null) return 0;
                if (da == null) return 1;   // null al final
                if (db == null) return -1;

                // DESC: más reciente primero
                return db.compareTo(da);

            } catch (Exception e) {
                return 0;
            }
        });
    }
}