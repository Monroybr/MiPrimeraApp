package com.liseth.miprimeraapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

public class PetDetailActivity extends AppCompatActivity {

    private TextView tvNombreDetalle, tvInfoBasicaDetalle, tvCaracteristicasDetalle, tvVacunasDetalle, tvHistorialDetalle;
    private Button btnAgregarVacuna, btnCarnetVacunas, btnAgregarHistorial, btnVerHistorial, btnCompartirInfo;

    private int index = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_detail);

        tvNombreDetalle = findViewById(R.id.tvNombreDetalle);
        tvInfoBasicaDetalle = findViewById(R.id.tvInfoBasicaDetalle);
        tvCaracteristicasDetalle = findViewById(R.id.tvCaracteristicasDetalle);
        tvVacunasDetalle = findViewById(R.id.tvVacunasDetalle);
        tvHistorialDetalle = findViewById(R.id.tvHistorialDetalle);

        btnAgregarVacuna = findViewById(R.id.btnAgregarVacuna);
        btnCarnetVacunas = findViewById(R.id.btnCarnetVacunas);
        btnAgregarHistorial = findViewById(R.id.btnAgregarHistorial);
        btnVerHistorial = findViewById(R.id.btnVerHistorial);
        btnCompartirInfo = findViewById(R.id.btnCompartirInfo);

        index = getIntent().getIntExtra("pet_index", -1);

        if (index == -1) {
            mostrarMascotaNoEncontrada();
            return;
        }

        btnAgregarVacuna.setOnClickListener(v -> {
            Intent intent = new Intent(PetDetailActivity.this, AddVacunacionActivity.class);
            intent.putExtra("pet_index", index);
            startActivity(intent);
        });

        btnCarnetVacunas.setOnClickListener(v -> {
            Intent intent = new Intent(PetDetailActivity.this, VacunasListActivity.class);
            intent.putExtra("pet_index", index);
            startActivity(intent);
        });

        btnAgregarHistorial.setOnClickListener(v -> {
            Intent intent = new Intent(PetDetailActivity.this, AddHistorialActivity.class);
            intent.putExtra("pet_index", index);
            startActivity(intent);
        });

        btnVerHistorial.setOnClickListener(v -> {
            Intent intent = new Intent(PetDetailActivity.this, HistorialListActivity.class);
            intent.putExtra("pet_index", index);
            startActivity(intent);
        });

        btnCompartirInfo.setOnClickListener(v -> {
            Intent intent = new Intent(PetDetailActivity.this, SharePetInfoActivity.class);
            intent.putExtra("pet_index", index);
            startActivity(intent);
        });

        cargarMascotaPorIndice(index);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (index != -1) {
            cargarMascotaPorIndice(index);
        }
    }

    private void cargarMascotaPorIndice(int index) {
        SharedPreferences prefs = getSharedPreferences("mascotas", MODE_PRIVATE);
        String json = prefs.getString("mascotas_json", "[]");

        try {
            JSONArray arr = new JSONArray(json);

            if (index < 0 || index >= arr.length()) {
                mostrarMascotaNoEncontrada();
                return;
            }

            JSONObject obj = arr.getJSONObject(index);

            String nombre = obj.optString("nombre", "");
            String fecha = obj.optString("fechaNacimiento", "-");
            String edad = obj.optString("edadTexto", "-");
            String raza = obj.optString("raza", "-");
            String carac = obj.optString("caracteristicas", "");

            tvNombreDetalle.setText(nombre.isEmpty() ? "Sin nombre" : nombre);
            tvInfoBasicaDetalle.setText(raza + " • " + fecha + " • " + edad);
            tvCaracteristicasDetalle.setText("Características: " + (carac.isEmpty() ? "-" : carac));

            int totalVacunas = contarVacunasDeMascota(index);
            int totalHistorial = contarHistorialDeMascota(index);

            tvVacunasDetalle.setText("Vacunas registradas: " + totalVacunas);
            tvHistorialDetalle.setText("Registros clínicos: " + totalHistorial);

        } catch (Exception e) {
            tvNombreDetalle.setText("Error cargando mascota");
            tvInfoBasicaDetalle.setText("-");
            tvCaracteristicasDetalle.setText("Características: -");
            tvVacunasDetalle.setText("Vacunas registradas: 0");
            tvHistorialDetalle.setText("Registros clínicos: 0");
        }
    }

    private int contarVacunasDeMascota(int petIndex) {
        int total = 0;

        SharedPreferences prefs = getSharedPreferences("vacunas", MODE_PRIVATE);
        String json = prefs.getString("vacunas_json", "[]");

        try {
            JSONArray arr = new JSONArray(json);

            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                int idx = obj.optInt("pet_index", -1);

                if (idx == petIndex) {
                    total++;
                }
            }
        } catch (Exception ignored) {
        }

        return total;
    }

    private int contarHistorialDeMascota(int petIndex) {
        int total = 0;

        SharedPreferences prefs = getSharedPreferences("historial", MODE_PRIVATE);
        String json = prefs.getString("historial_json", "[]");

        try {
            JSONArray arr = new JSONArray(json);

            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                int idx = obj.optInt("pet_index", -1);

                if (idx == petIndex) {
                    total++;
                }
            }
        } catch (Exception ignored) {
        }

        return total;
    }

    private void mostrarMascotaNoEncontrada() {
        tvNombreDetalle.setText("Mascota no encontrada");
        tvInfoBasicaDetalle.setText("-");
        tvCaracteristicasDetalle.setText("Características: -");
        tvVacunasDetalle.setText("Vacunas registradas: 0");
        tvHistorialDetalle.setText("Registros clínicos: 0");
    }
}