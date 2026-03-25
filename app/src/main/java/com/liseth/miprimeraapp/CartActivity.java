package com.liseth.miprimeraapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

public class CartActivity extends AppCompatActivity {

    private RecyclerView rvCart;
    private TextView tvVacioCart, tvTotalCart;
    private Button btnVaciarCart, btnFinalizarCompra;

    private final ArrayList<CartItem> cartItems = new ArrayList<>();
    private CartAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        rvCart = findViewById(R.id.rvCart);
        tvVacioCart = findViewById(R.id.tvVacioCart);
        tvTotalCart = findViewById(R.id.tvTotalCart);
        btnVaciarCart = findViewById(R.id.btnVaciarCart);
        btnFinalizarCompra = findViewById(R.id.btnFinalizarCompra);

        rvCart.setLayoutManager(new LinearLayoutManager(this));

        adapter = new CartAdapter(cartItems, new CartAdapter.OnCartActionListener() {
            @Override
            public void onAumentar(CartItem item) {
                item.cantidad++;
                guardarCarrito();
                cargarCarrito();
            }

            @Override
            public void onDisminuir(CartItem item) {
                if (item.cantidad > 1) {
                    item.cantidad--;
                } else {
                    cartItems.remove(item);
                }
                guardarCarrito();
                cargarCarrito();
            }

            @Override
            public void onEliminar(CartItem item) {
                cartItems.remove(item);
                guardarCarrito();
                cargarCarrito();
            }
        });

        rvCart.setAdapter(adapter);

        btnVaciarCart.setOnClickListener(v -> vaciarCarrito());

        btnFinalizarCompra.setOnClickListener(v -> {
            if (cartItems.isEmpty()) {
                Toast.makeText(this, "El carrito está vacío", Toast.LENGTH_SHORT).show();
            } else {
                startActivity(new android.content.Intent(CartActivity.this, CheckoutActivity.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarCarrito();
    }

    private void cargarCarrito() {
        cartItems.clear();

        SharedPreferences prefs = getSharedPreferences("carrito", MODE_PRIVATE);
        String json = prefs.getString("carrito_json", "[]");

        try {
            JSONArray arr = new JSONArray(json);

            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);

                String nombre = obj.optString("nombre", "");
                String categoria = obj.optString("categoria", "");
                double precio = obj.optDouble("precio", 0);
                int cantidad = obj.optInt("cantidad", 1);

                cartItems.add(new CartItem(nombre, categoria, precio, cantidad));
            }
        } catch (Exception ignored) { }

        adapter.notifyDataSetChanged();
        actualizarVista();
    }

    private void guardarCarrito() {
        SharedPreferences prefs = getSharedPreferences("carrito", MODE_PRIVATE);
        JSONArray arr = new JSONArray();

        try {
            for (CartItem item : cartItems) {
                JSONObject obj = new JSONObject();
                obj.put("nombre", item.nombre);
                obj.put("categoria", item.categoria);
                obj.put("precio", item.precio);
                obj.put("cantidad", item.cantidad);
                arr.put(obj);
            }

            prefs.edit().putString("carrito_json", arr.toString()).apply();

        } catch (Exception ignored) { }
    }

    private void actualizarVista() {
        tvVacioCart.setVisibility(cartItems.isEmpty() ? View.VISIBLE : View.GONE);

        double total = 0;
        for (CartItem item : cartItems) {
            total += item.getSubtotal();
        }

        tvTotalCart.setText(String.format(Locale.getDefault(), "Total: $ %.2f", total));
    }

    private void vaciarCarrito() {
        SharedPreferences prefs = getSharedPreferences("carrito", MODE_PRIVATE);
        prefs.edit().putString("carrito_json", "[]").apply();
        cargarCarrito();
    }
}
