package com.liseth.miprimeraapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class StoreActivity extends AppCompatActivity {

    private RecyclerView rvProductos;
    private TextView tvCarrito;
    private Button btnVerCarrito;

    private final ArrayList<Product> productList = new ArrayList<>();
    private int cantidadCarrito = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        rvProductos = findViewById(R.id.rvProductos);
        tvCarrito = findViewById(R.id.tvCarrito);
        btnVerCarrito = findViewById(R.id.btnVerCarrito);

        cargarProductos();
        actualizarCantidadCarrito();

        rvProductos.setLayoutManager(new LinearLayoutManager(this));
        rvProductos.setAdapter(new ProductAdapter(productList, product -> {
            agregarAlCarrito(product);
            actualizarCantidadCarrito();
        }));

        btnVerCarrito.setOnClickListener(v -> {
            startActivity(new Intent(StoreActivity.this, CartActivity.class));
        });
    }

    private void cargarProductos() {
        productList.add(new Product("Concentrado para perro 10kg", "Comida", 85000));
        productList.add(new Product("Concentrado para gato 3kg", "Comida", 42000));
        productList.add(new Product("Antipulgas", "Medicamentos", 30000));
        productList.add(new Product("Vitaminas para mascota", "Medicamentos", 25000));
        productList.add(new Product("Collar ajustable", "Accesorios", 18000));
        productList.add(new Product("Juguete mordedor", "Accesorios", 15000));
        productList.add(new Product("Cama pequeña", "Accesorios", 55000));
    }

    private void agregarAlCarrito(Product product) {
        SharedPreferences prefs = getSharedPreferences("carrito", MODE_PRIVATE);
        String json = prefs.getString("carrito_json", "[]");

        try {
            JSONArray arr = new JSONArray(json);
            boolean encontrado = false;

            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);

                if (obj.optString("nombre").equals(product.nombre)) {
                    int cantidadActual = obj.optInt("cantidad", 1);
                    obj.put("cantidad", cantidadActual + 1);
                    encontrado = true;
                    break;
                }
            }

            if (!encontrado) {
                JSONObject obj = new JSONObject();
                obj.put("nombre", product.nombre);
                obj.put("categoria", product.categoria);
                obj.put("precio", product.precio);
                obj.put("cantidad", 1);
                arr.put(obj);
            }

            prefs.edit().putString("carrito_json", arr.toString()).apply();

        } catch (Exception ignored) { }
    }

    private void actualizarCantidadCarrito() {
        SharedPreferences prefs = getSharedPreferences("carrito", MODE_PRIVATE);
        String json = prefs.getString("carrito_json", "[]");

        int totalProductos = 0;

        try {
            JSONArray arr = new JSONArray(json);

            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                totalProductos += obj.optInt("cantidad", 1);
            }
        } catch (Exception ignored) { }

        cantidadCarrito = totalProductos;
        tvCarrito.setText("Carrito: " + cantidadCarrito + " productos");
    }
}