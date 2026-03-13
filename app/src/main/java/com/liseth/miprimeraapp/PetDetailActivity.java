package com.liseth.miprimeraapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class PetDetailActivity extends AppCompatActivity {

    private Button btnAgregarVacuna, btnCarnetVacunas, btnAgregarHistorial, btnVerHistorial, btnCompartirInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_detail);


        btnAgregarVacuna = findViewById(R.id.btnAgregarVacuna);
        btnCarnetVacunas = findViewById(R.id.btnCarnetVacunas);
        btnAgregarHistorial = findViewById(R.id.btnAgregarHistorial);
        btnVerHistorial = findViewById(R.id.btnVerHistorial);
        btnCompartirInfo = findViewById(R.id.btnCompartirInfo);


        //Agrega vacunas de las mascotas
        btnAgregarVacuna.setOnClickListener(v -> {
            Intent intent = new Intent(PetDetailActivity.this, AddVacunacionActivity.class);
            startActivity(intent);
        });

        //Visualiza el carnet de vacunas
        btnCarnetVacunas.setOnClickListener(v -> {
            Intent intent = new Intent(PetDetailActivity.this, VacunasListActivity.class);
            startActivity(intent);
        });

        //Agrega historial medico de la mascota
        btnAgregarHistorial.setOnClickListener(v -> {
            Intent intent = new Intent(PetDetailActivity.this, AddHistorialActivity.class);
            startActivity(intent);
        });

        //Visualiza historial de la mascota
        btnVerHistorial.setOnClickListener(v -> {
            Intent intent = new Intent(PetDetailActivity.this, HistorialListActivity.class);
            startActivity(intent);
        });

        //Comparte la info de la mascota en pdf
        btnCompartirInfo.setOnClickListener(v -> {
            Intent intent = new Intent(PetDetailActivity.this, SharePetInfoActivity.class);
            startActivity(intent);
        });

    }

}