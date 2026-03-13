package com.liseth.miprimeraapp;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    private Button btnNuevaMascota, btnProductos, btnVerMascotas, btnNotificaciones, btnBusqueda;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        btnNuevaMascota = findViewById(R.id.btnNuevaMascota);
        btnProductos = findViewById(R.id.btnProductos);
        btnVerMascotas = findViewById(R.id.btnVerMascotas);
        btnNotificaciones = findViewById(R.id.btnNotificaciones);
        btnBusqueda = findViewById(R.id.btnBusqueda);

        //boton crear mascota
        btnNuevaMascota.setOnClickListener(v ->{
            Intent intent = new Intent(HomeActivity.this, AddPetActivity.class);
            startActivity(intent);
        });

        //Muestra el detalle de las mascotas
        btnVerMascotas.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, PetDetailActivity.class);
            startActivity(intent);
        });

        //Muestra los productos disponibles
        btnProductos.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, StoreActivity.class);
            startActivity(intent);
        });

        //Muestra las notificaciones de promociones y vacunas a vencer
        btnNotificaciones.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, NotificationsActivity.class);
            startActivity(intent);
        });

        //Muestra veterinarias cercanas
        btnBusqueda.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, BusquedaActivity.class);
            startActivity(intent);
        });

    }





}