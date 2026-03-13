package com.liseth.miprimeraapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;


public class CartActivity extends AppCompatActivity {
    private Button btnFinalizarCompra;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        btnFinalizarCompra = findViewById(R.id.btnFinalizarCompra);

        btnFinalizarCompra.setOnClickListener(v -> {
            Intent intent = new Intent(CartActivity.this, CheckoutActivity.class);
            startActivity(intent);
        });

    }
}
