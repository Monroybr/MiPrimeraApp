package com.liseth.miprimeraapp;

import android.content.Intent;
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

    // Componentes de la interfaz
    private RecyclerView rvCart;
    private TextView tvVacioCart, tvTotalCart;
    private Button btnVaciarCart, btnFinalizarCompra;

    // Lista donde voy cargando los productos del carrito
    private final ArrayList<CartItem> cartItems = new ArrayList<>();
    private CartAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // Relaciono variables con los elementos del XML
        rvCart = findViewById(R.id.rvCart);
        tvVacioCart = findViewById(R.id.tvVacioCart);
        tvTotalCart = findViewById(R.id.tvTotalCart);
        btnVaciarCart = findViewById(R.id.btnVaciarCart);
        btnFinalizarCompra = findViewById(R.id.btnFinalizarCompra);

        // Configuro el RecyclerView
        rvCart.setLayoutManager(new LinearLayoutManager(this));

        // Configuro el adaptador y las acciones del carrito
        adapter = new CartAdapter(cartItems, new CartAdapter.OnCartActionListener() {
            @Override
            public void onAumentar(CartItem item) {
                item.cantidad++;
                guardarCarrito();
                cargarCarrito();
                Toast.makeText(CartActivity.this, "Cantidad aumentada", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDisminuir(CartItem item) {
                if (item.cantidad > 1) {
                    item.cantidad--;
                    Toast.makeText(CartActivity.this, "Cantidad disminuida", Toast.LENGTH_SHORT).show();
                } else {
                    cartItems.remove(item);
                    Toast.makeText(CartActivity.this, "Producto eliminado del carrito", Toast.LENGTH_SHORT).show();
                }

                guardarCarrito();
                cargarCarrito();
            }

            @Override
            public void onEliminar(CartItem item) {
                cartItems.remove(item);
                guardarCarrito();
                cargarCarrito();
                Toast.makeText(CartActivity.this, "Producto eliminado", Toast.LENGTH_SHORT).show();
            }
        });

        rvCart.setAdapter(adapter);

        // Botón para vaciar el carrito completo
        btnVaciarCart.setOnClickListener(v -> {
            if (cartItems.isEmpty()) {
                Toast.makeText(this, "El carrito ya está vacío", Toast.LENGTH_SHORT).show();
            } else {
                vaciarCarrito();
                Toast.makeText(this, "Carrito vaciado correctamente", Toast.LENGTH_SHORT).show();
            }
        });

        // Botón para ir a la pantalla de pago
        btnFinalizarCompra.setOnClickListener(v -> {
            if (cartItems.isEmpty()) {
                Toast.makeText(this, "El carrito está vacío", Toast.LENGTH_SHORT).show();
            } else {
                startActivity(new Intent(CartActivity.this, CheckoutActivity.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Cada vez que vuelvo a esta pantalla, recargo la información del carrito
        cargarCarrito();
    }

    // Este método carga los productos guardados en SharedPreferences
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
        } catch (Exception ignored) {
        }

        adapter.notifyDataSetChanged();
        actualizarVista();
    }

    // Este método guarda el carrito actualizado en SharedPreferences
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

        } catch (Exception ignored) {
        }
    }

    // Este metodo actualiza el mensaje de vacío y el total
    private void actualizarVista() {
        boolean estaVacio = cartItems.isEmpty();

        tvVacioCart.setVisibility(estaVacio ? View.VISIBLE : View.GONE);
        rvCart.setVisibility(estaVacio ? View.GONE : View.VISIBLE);

        double total = 0;
        for (CartItem item : cartItems) {
            total += item.getSubtotal();
        }

        tvTotalCart.setText(String.format(Locale.getDefault(), "Total: $ %.2f", total));
    }

    //eliminatodo el contenido del carrito
    private void vaciarCarrito() {
        SharedPreferences prefs = getSharedPreferences("carrito", MODE_PRIVATE);
        prefs.edit().putString("carrito_json", "[]").apply();
        cargarCarrito();
    }
}