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

import java.util.ArrayList;

public class PetsListActivity extends AppCompatActivity {

    private RecyclerView rvMascotas;
    private TextView tvVacio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pets_list);

        rvMascotas = findViewById(R.id.rvMascotas);
        tvVacio = findViewById(R.id.tvVacio);

        rvMascotas.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onResume() {
        super.onResume();

        ArrayList<Pet> pets = cargarMascotas();

        tvVacio.setVisibility(pets.isEmpty() ? View.VISIBLE : View.GONE);

        rvMascotas.setAdapter(new PetAdapter(pets, position -> {
            Intent intent = new Intent(PetsListActivity.this, PetDetailActivity.class);
            intent.putExtra("pet_index", position);
            startActivity(intent);
        }));
    }

    private ArrayList<Pet> cargarMascotas() {
        ArrayList<Pet> lista = new ArrayList<>();

        SharedPreferences prefs = getSharedPreferences("mascotas", MODE_PRIVATE);
        String json = prefs.getString("mascotas_json", "[]");

        try {
            JSONArray arr = new JSONArray(json);

            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);

                String nombre = obj.optString("nombre", "");
                String fecha = obj.optString("fechaNacimiento", "");
                String edad = obj.optString("edadTexto", "-");
                String raza = obj.optString("raza", "");
                String carac = obj.optString("caracteristicas", "");
                String vacunas = obj.optString("vacunas", "");
                String historial = obj.optString("historial", "");

                lista.add(new Pet(nombre, fecha, edad, raza, carac, vacunas, historial));
            }
        } catch (Exception ignored) { }

        return lista;
    }
}