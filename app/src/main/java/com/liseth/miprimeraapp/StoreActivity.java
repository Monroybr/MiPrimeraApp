package com.liseth.miprimeraapp;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;


import androidx.appcompat.app.AppCompatActivity;


public class StoreActivity extends AppCompatActivity {
    private Button btnVerCarrito;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        btnVerCarrito = findViewById(R.id.btnVerCarrito);

        btnVerCarrito.setOnClickListener(v -> {
            Intent intent = new Intent(StoreActivity.this, CartActivity.class);
            startActivity(intent);
        });
    }
}
