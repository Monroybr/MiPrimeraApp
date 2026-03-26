package com.liseth.miprimeraapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

public class HomeActivity extends AppCompatActivity {

    private TextView tvListaMascotas;
    private Button btnNuevaMascota, btnProductos, btnVerMascotas, btnNotificaciones, btnBusqueda;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        tvListaMascotas = findViewById(R.id.tvListaMascotas);
        btnNuevaMascota = findViewById(R.id.btnNuevaMascota);
        btnProductos = findViewById(R.id.btnProductos);
        btnVerMascotas = findViewById(R.id.btnVerMascotas);
        btnNotificaciones = findViewById(R.id.btnNotificaciones);
        btnBusqueda = findViewById(R.id.btnBusqueda);

        btnNuevaMascota.setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this, AddPetActivity.class))
        );

        btnVerMascotas.setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this, PetsListActivity.class))
        );

        btnProductos.setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this, StoreActivity.class))
        );

        btnNotificaciones.setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this, NotificationsActivity.class))
        );

        btnBusqueda.setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this, BusquedaActivity.class))
        );

        mostrarMascotasEnHome();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mostrarMascotasEnHome();
    }

    private void mostrarMascotasEnHome() {
        SharedPreferences prefs = getSharedPreferences("mascotas", MODE_PRIVATE);
        String json = prefs.getString("mascotas_json", "[]");

        try {
            JSONArray arr = new JSONArray(json);

            if (arr.length() == 0) {
                tvListaMascotas.setText("Aún no tienes mascotas registradas.");
                return;
            }

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);

                String nombre = obj.optString("nombre", "-");
                String raza = obj.optString("raza", "-");
                String edad = obj.optString("edadTexto", "-");

                sb.append("• ").append(nombre)
                        .append(" (").append(raza).append(") - ")
                        .append(edad)
                        .append("\n");
            }

            tvListaMascotas.setText(sb.toString());

        } catch (Exception e) {
            tvListaMascotas.setText("Error cargando mascotas.");
        }
    }
}