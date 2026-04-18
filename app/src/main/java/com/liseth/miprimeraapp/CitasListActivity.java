package com.liseth.miprimeraapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class CitasListActivity extends AppCompatActivity {

    private RecyclerView rvCitas;
    private TextView tvVacioCitas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_citas_list);

        rvCitas = findViewById(R.id.rvCitas);
        tvVacioCitas = findViewById(R.id.tvVacioCitas);

        rvCitas.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onResume() {
        super.onResume();

        ArrayList<Cita> lista = cargarCitas();

        tvVacioCitas.setVisibility(lista.isEmpty() ? View.VISIBLE : View.GONE);
        rvCitas.setAdapter(new CitaAdapter(lista));
    }

    // Aquí cargo todas las citas guardadas
    private ArrayList<Cita> cargarCitas() {
        ArrayList<Cita> lista = new ArrayList<>();

        SharedPreferences prefs = getSharedPreferences("citas", MODE_PRIVATE);
        String json = prefs.getString("citas_json", "[]");

        try {
            JSONArray arr = new JSONArray(json);

            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);

                int petIndex = obj.optInt("pet_index", -1);
                String nombreMascota = obj.optString("nombreMascota", "");
                String fecha = obj.optString("fecha", "");
                String hora = obj.optString("hora", "");
                String veterinaria = obj.optString("veterinaria", "");
                String motivo = obj.optString("motivo", "");

                lista.add(new Cita(petIndex, nombreMascota, fecha, hora, veterinaria, motivo));
            }

        } catch (Exception ignored) {
        }

        return lista;
    }
}