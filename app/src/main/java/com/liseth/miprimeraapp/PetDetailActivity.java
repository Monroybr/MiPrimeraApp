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

    // Aquí declaro los campos que mostrarán la información de la mascota
    private TextView tvNombreDetalle, tvInfoBasicaDetalle, tvCaracteristicasDetalle,
            tvVacunasDetalle, tvHistorialDetalle,
            tvSexoDetalle, tvPesoDetalle, tvColorDetalle, tvAlergiasDetalle, tvObservacionesDetalle;

    private Button btnAgregarVacuna, btnCarnetVacunas, btnAgregarHistorial, btnVerHistorial, btnCompartirInfo;

    private int index = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_detail);

        // Relaciono variables con elementos del XML
        tvNombreDetalle = findViewById(R.id.tvNombreDetalle);
        tvInfoBasicaDetalle = findViewById(R.id.tvInfoBasicaDetalle);
        tvCaracteristicasDetalle = findViewById(R.id.tvCaracteristicasDetalle);
        tvVacunasDetalle = findViewById(R.id.tvVacunasDetalle);
        tvHistorialDetalle = findViewById(R.id.tvHistorialDetalle);

        tvSexoDetalle = findViewById(R.id.tvSexoDetalle);
        tvPesoDetalle = findViewById(R.id.tvPesoDetalle);
        tvColorDetalle = findViewById(R.id.tvColorDetalle);
        tvAlergiasDetalle = findViewById(R.id.tvAlergiasDetalle);
        tvObservacionesDetalle = findViewById(R.id.tvObservacionesDetalle);

        btnAgregarVacuna = findViewById(R.id.btnAgregarVacuna);
        btnCarnetVacunas = findViewById(R.id.btnCarnetVacunas);
        btnAgregarHistorial = findViewById(R.id.btnAgregarHistorial);
        btnVerHistorial = findViewById(R.id.btnVerHistorial);
        btnCompartirInfo = findViewById(R.id.btnCompartirInfo);

        // Aquí recibo el índice de la mascota seleccionada
        index = getIntent().getIntExtra("pet_index", -1);

        if (index == -1) {
            mostrarMascotaNoEncontrada();
            return;
        }

        // Botón para agregar vacuna
        btnAgregarVacuna.setOnClickListener(v -> {
            Intent intent = new Intent(PetDetailActivity.this, AddVacunacionActivity.class);
            intent.putExtra("pet_index", index);
            startActivity(intent);
        });

        // Botón para ver carnet de vacunas
        btnCarnetVacunas.setOnClickListener(v -> {
            Intent intent = new Intent(PetDetailActivity.this, VacunasListActivity.class);
            intent.putExtra("pet_index", index);
            startActivity(intent);
        });

        // Botón para agregar historial clínico
        btnAgregarHistorial.setOnClickListener(v -> {
            Intent intent = new Intent(PetDetailActivity.this, AddHistorialActivity.class);
            intent.putExtra("pet_index", index);
            startActivity(intent);
        });

        // Botón para ver historial clínico
        btnVerHistorial.setOnClickListener(v -> {
            Intent intent = new Intent(PetDetailActivity.this, HistorialListActivity.class);
            intent.putExtra("pet_index", index);
            startActivity(intent);
        });

        // Botón para compartir información
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

        // Cada vez que regreso a esta pantalla, actualizo los datos
        if (index != -1) {
            cargarMascotaPorIndice(index);
        }
    }

    // Este método carga toda la información de la mascota seleccionada
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

            String sexo = obj.optString("sexo", "");
            String peso = obj.optString("peso", "");
            String color = obj.optString("color", "");
            String alergias = obj.optString("alergias", "");
            String observaciones = obj.optString("observaciones", "");

            tvNombreDetalle.setText(nombre.isEmpty() ? "Sin nombre" : nombre);
            tvInfoBasicaDetalle.setText(raza + " • " + fecha + " • " + edad);
            tvCaracteristicasDetalle.setText("Características: " + (carac.isEmpty() ? "-" : carac));

            // Aquí muestro los nuevos campos del perfil completo
            tvSexoDetalle.setText("Sexo: " + (sexo.isEmpty() ? "-" : sexo));
            tvPesoDetalle.setText("Peso: " + (peso.isEmpty() ? "-" : peso + " kg"));
            tvColorDetalle.setText("Color: " + (color.isEmpty() ? "-" : color));
            tvAlergiasDetalle.setText("Alergias: " + (alergias.isEmpty() ? "-" : alergias));
            tvObservacionesDetalle.setText("Observaciones: " + (observaciones.isEmpty() ? "-" : observaciones));

            int totalVacunas = contarVacunasDeMascota(index);
            int totalHistorial = contarHistorialDeMascota(index);

            tvVacunasDetalle.setText("Vacunas registradas: " + totalVacunas);
            tvHistorialDetalle.setText("Registros clínicos: " + totalHistorial);

        } catch (Exception e) {
            tvNombreDetalle.setText("Error cargando mascota");
            tvInfoBasicaDetalle.setText("-");
            tvCaracteristicasDetalle.setText("Características: -");
            tvSexoDetalle.setText("Sexo: -");
            tvPesoDetalle.setText("Peso: -");
            tvColorDetalle.setText("Color: -");
            tvAlergiasDetalle.setText("Alergias: -");
            tvObservacionesDetalle.setText("Observaciones: -");
            tvVacunasDetalle.setText("Vacunas registradas: 0");
            tvHistorialDetalle.setText("Registros clínicos: 0");
        }
    }

    // Aquí cuento cuántas vacunas tiene registradas la mascota
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

    // Aquí cuento cuántos registros clínicos tiene la mascota
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

    // Este metodo muestra un estado por defecto si la mascota no existe
    private void mostrarMascotaNoEncontrada() {
        tvNombreDetalle.setText("Mascota no encontrada");
        tvInfoBasicaDetalle.setText("-");
        tvCaracteristicasDetalle.setText("Características: -");
        tvSexoDetalle.setText("Sexo: -");
        tvPesoDetalle.setText("Peso: -");
        tvColorDetalle.setText("Color: -");
        tvAlergiasDetalle.setText("Alergias: -");
        tvObservacionesDetalle.setText("Observaciones: -");
        tvVacunasDetalle.setText("Vacunas registradas: 0");
        tvHistorialDetalle.setText("Registros clínicos: 0");
    }
}