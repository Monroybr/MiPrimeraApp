package com.liseth.miprimeraapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class OrdersHistoryActivity extends AppCompatActivity {

    private RecyclerView rvOrders;
    private TextView tvVacioOrders;

    private final ArrayList<Order> orderList = new ArrayList<>();
    private OrderAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders_history);

        rvOrders = findViewById(R.id.rvOrders);
        tvVacioOrders = findViewById(R.id.tvVacioOrders);

        rvOrders.setLayoutManager(new LinearLayoutManager(this));
        adapter = new OrderAdapter(orderList);
        rvOrders.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarPedidos();
    }

    private void cargarPedidos() {
        orderList.clear();

        SharedPreferences prefs = getSharedPreferences("pedidos", MODE_PRIVATE);
        String json = prefs.getString("pedidos_json", "[]");

        try {
            JSONArray arr = new JSONArray(json);

            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);

                String numero = obj.optString("numeroPedido", "");
                String cliente = obj.optString("cliente", "");
                String direccion = obj.optString("direccion", "");
                String metodoPago = obj.optString("metodoPago", "");
                String detalle = obj.optString("detalleProductos", "");
                String fecha = obj.optString("fecha", "");
                double total = obj.optDouble("total", 0);

                orderList.add(new Order(numero, cliente, direccion, metodoPago, detalle, fecha, total));
            }
        } catch (Exception ignored) { }

        adapter.notifyDataSetChanged();
        tvVacioOrders.setVisibility(orderList.isEmpty() ? View.VISIBLE : View.GONE);
    }
}