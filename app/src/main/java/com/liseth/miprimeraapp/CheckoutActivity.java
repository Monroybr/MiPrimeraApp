package com.liseth.miprimeraapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class CheckoutActivity extends AppCompatActivity {
    private Button btnConfirmarPedido;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        btnConfirmarPedido = findViewById(R.id.btnConfirmarPedido);

        btnConfirmarPedido.setOnClickListener(v -> {
            Intent intent = new Intent(CheckoutActivity.this, OrderActivity.class);
            startActivity(intent);
        });
    }
}
