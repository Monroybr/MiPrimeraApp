package com.liseth.miprimeraapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class StoreActivity extends AppCompatActivity {

    // Aquí declaro los componentes de la interfaz
    private RecyclerView rvProductos;
    private TextView tvCarrito;
    private Button btnVerCarrito;
    private Button btnFiltroTodos, btnFiltroComida, btnFiltroMedicamentos, btnFiltroAccesorios;

    // Esta lista guarda todos los productos
    private final ArrayList<Product> productList = new ArrayList<>();

    // Esta lista me sirve para mostrar los productos filtrados
    private final ArrayList<Product> filteredList = new ArrayList<>();

    private int cantidadCarrito = 0;
    private ProductAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        // Relaciono variables con los elementos del XML
        rvProductos = findViewById(R.id.rvProductos);
        tvCarrito = findViewById(R.id.tvCarrito);
        btnVerCarrito = findViewById(R.id.btnVerCarrito);

        btnFiltroTodos = findViewById(R.id.btnFiltroTodos);
        btnFiltroComida = findViewById(R.id.btnFiltroComida);
        btnFiltroMedicamentos = findViewById(R.id.btnFiltroMedicamentos);
        btnFiltroAccesorios = findViewById(R.id.btnFiltroAccesorios);

        // Cargo los productos base de la tienda
        cargarProductos();

        // Inicialmente muestro todos
        filteredList.addAll(productList);

        // Actualizo el contador del carrito
        actualizarCantidadCarrito();

        // Configuro el RecyclerView
        rvProductos.setLayoutManager(new LinearLayoutManager(this));

        // Aquí configuro el adaptador con la lista filtrada
        adapter = new ProductAdapter(filteredList, product -> {
            agregarAlCarrito(product);
            actualizarCantidadCarrito();
            Toast.makeText(this, product.nombre + " agregado al carrito", Toast.LENGTH_SHORT).show();
        });

        rvProductos.setAdapter(adapter);

        // Botón para abrir el carrito
        btnVerCarrito.setOnClickListener(v ->
                startActivity(new Intent(StoreActivity.this, CartActivity.class))
        );

        // Filtro: mostrar todos los productos
        btnFiltroTodos.setOnClickListener(v -> filtrarProductos("Todos"));

        // Filtro: mostrar solo comida
        btnFiltroComida.setOnClickListener(v -> filtrarProductos("Comida"));

        // Filtro: mostrar solo medicamentos
        btnFiltroMedicamentos.setOnClickListener(v -> filtrarProductos("Medicamentos"));

        // Filtro: mostrar solo accesorios
        btnFiltroAccesorios.setOnClickListener(v -> filtrarProductos("Accesorios"));
    }

    // Aquí creo la lista base de productos
    private void cargarProductos() {
        productList.add(new Product("Concentrado para perro 10kg", "Comida", 85000));
        productList.add(new Product("Concentrado para gato 3kg", "Comida", 42000));
        productList.add(new Product("Antipulgas", "Medicamentos", 30000));
        productList.add(new Product("Vitaminas para mascota", "Medicamentos", 25000));
        productList.add(new Product("Collar ajustable", "Accesorios", 18000));
        productList.add(new Product("Juguete mordedor", "Accesorios", 15000));
        productList.add(new Product("Cama pequeña", "Accesorios", 55000));
    }

    // Este método me permite filtrar productos por categoría
    private void filtrarProductos(String categoria) {
        filteredList.clear();

        if (categoria.equals("Todos")) {
            filteredList.addAll(productList);
        } else {
            for (Product product : productList) {
                if (product.categoria.equalsIgnoreCase(categoria)) {
                    filteredList.add(product);
                }
            }
        }

        adapter.notifyDataSetChanged();
    }

    // Método para agregar productos al carrito
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

        } catch (Exception ignored) {
        }
    }

    // Aquí actualizo el número de productos del carrito
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
        } catch (Exception ignored) {
        }

        cantidadCarrito = totalProductos;
        tvCarrito.setText("Carrito: " + cantidadCarrito + " productos");
    }
}