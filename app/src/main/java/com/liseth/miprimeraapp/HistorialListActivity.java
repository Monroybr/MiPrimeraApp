package com.liseth.miprimeraapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

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

public class HistorialListActivity extends AppCompatActivity {

    private RecyclerView rvHistorial;
    private TextView tvVacioHistorial;

    private int petIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial_list);

        rvHistorial = findViewById(R.id.rvHistorial);
        tvVacioHistorial = findViewById(R.id.tvVacioHistorial);

        rvHistorial.setLayoutManager(new LinearLayoutManager(this));

        petIndex = getIntent().getIntExtra("pet_index", -1);
    }

    @Override
    protected void onResume() {
        super.onResume();

        ArrayList<Historial> lista = cargarHistorialDeMascota(petIndex);

        ordenarPorFechaDesc(lista);

        tvVacioHistorial.setVisibility(lista.isEmpty() ? View.VISIBLE : View.GONE);

        rvHistorial.setAdapter(new HistorialAdapter(lista, (pos, item) -> {
            // Abre el  detalle del registro usando índice REAL del JSON
            Intent intent = new Intent(HistorialListActivity.this, HistorialDetailActivity.class);
            intent.putExtra("hist_global_index", item.globalIndex);
            startActivity(intent);
        }));
    }

    private ArrayList<Historial> cargarHistorialDeMascota(int petIndex) {
        ArrayList<Historial> lista = new ArrayList<>();
        if (petIndex == -1) return lista;

        SharedPreferences prefs = getSharedPreferences("historial", MODE_PRIVATE);
        String json = prefs.getString("historial_json", "[]");

        try {
            JSONArray arr = new JSONArray(json);

            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);

                int idx = obj.optInt("pet_index", -1);
                if (idx != petIndex) continue;

                String fecha = obj.optString("fechaRegistro", "");
                String enf = obj.optString("enfermedades", "");
                String proc = obj.optString("procedimientos", "");
                String med = obj.optString("medicacion", "");

                // ✅ i = índice real dentro del historial_json
                lista.add(new Historial(idx, i, fecha, enf, proc, med));
            }
        } catch (Exception ignored) { }

        return lista;
    }

    private void ordenarPorFechaDesc(ArrayList<Historial> lista) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        Collections.sort(lista, (a, b) -> {
            try {
                Date da = sdf.parse(a.fechaRegistro);
                Date db = sdf.parse(b.fechaRegistro);

                if (da == null && db == null) return 0;
                if (da == null) return 1;
                if (db == null) return -1;

                return db.compareTo(da); // DESC
            } catch (Exception e) {
                return 0;
            }
        });
    }
}