package com.liseth.miprimeraapp;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;


public class AddPetActivity extends AppCompatActivity {

    private Button btnGuardarMascota;

    @Override


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pet);

        btnGuardarMascota = findViewById(R.id.btnGuardarMascota);

        // Ingresar
        btnGuardarMascota.setOnClickListener(v -> {
            Intent intent = new Intent(AddPetActivity.this, HomeActivity.class);
            startActivity(intent);
        });
    }






}