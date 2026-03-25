package com.liseth.miprimeraapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CheckoutActivity extends AppCompatActivity {

    private EditText etNombreCliente, etDireccion;
    private RadioGroup rgMetodoPago;
    private TextView tvDetallePedido, tvTotalCheckout;
    private Button btnConfirmarPedido;

    private String detallePedidoTexto = "";
    private double totalPedido = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        etNombreCliente = findViewById(R.id.etNombreCliente);
        etDireccion = findViewById(R.id.etDireccion);
        rgMetodoPago = findViewById(R.id.rgMetodoPago);
        tvDetallePedido = findViewById(R.id.tvDetallePedido);
        tvTotalCheckout = findViewById(R.id.tvTotalCheckout);
        btnConfirmarPedido = findViewById(R.id.btnConfirmarPedido);

        cargarResumenPedido();

        btnConfirmarPedido.setOnClickListener(v -> confirmarPedido());
    }

    private void cargarResumenPedido() {
        SharedPreferences prefs = getSharedPreferences("carrito", MODE_PRIVATE);
        String json = prefs.getString("carrito_json", "[]");

        StringBuilder detalle = new StringBuilder();
        totalPedido = 0;

        try {
            JSONArray arr = new JSONArray(json);

            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);

                String nombre = obj.optString("nombre", "");
                int cantidad = obj.optInt("cantidad", 1);
                double precio = obj.optDouble("precio", 0);

                double subtotal = cantidad * precio;
                totalPedido += subtotal;

                detalle.append("• ")
                        .append(nombre)
                        .append(" x")
                        .append(cantidad)
                        .append(" - $ ")
                        .append(String.format(Locale.getDefault(), "%.2f", subtotal))
                        .append("\n");
            }

        } catch (Exception ignored) { }

        detallePedidoTexto = detalle.toString();

        tvDetallePedido.setText(detallePedidoTexto.isEmpty() ? "No hay productos en el carrito." : detallePedidoTexto);
        tvTotalCheckout.setText(String.format(Locale.getDefault(), "Total: $ %.2f", totalPedido));
    }

    private void confirmarPedido() {
        String nombre = etNombreCliente.getText().toString().trim();
        String direccion = etDireccion.getText().toString().trim();

        int metodoSeleccionadoId = rgMetodoPago.getCheckedRadioButtonId();

        if (nombre.isEmpty() || direccion.isEmpty()) {
            Toast.makeText(this, "Completa nombre y dirección", Toast.LENGTH_SHORT).show();
            return;
        }

        if (metodoSeleccionadoId == -1) {
            Toast.makeText(this, "Selecciona un método de pago", Toast.LENGTH_SHORT).show();
            return;
        }

        RadioButton rbMetodo = findViewById(metodoSeleccionadoId);
        String metodoPago = rbMetodo.getText().toString();

        guardarPedido(nombre, direccion, metodoPago);

        SharedPreferences prefs = getSharedPreferences("carrito", MODE_PRIVATE);
        prefs.edit().putString("carrito_json", "[]").apply();

        Toast.makeText(this, "Pedido confirmado ✅", Toast.LENGTH_LONG).show();

        startActivity(new Intent(CheckoutActivity.this, OrdersHistoryActivity.class));
        finish();
    }

    private void guardarPedido(String cliente, String direccion, String metodoPago) {
        SharedPreferences prefs = getSharedPreferences("pedidos", MODE_PRIVATE);
        String json = prefs.getString("pedidos_json", "[]");

        try {
            JSONArray arr = new JSONArray(json);

            String numeroPedido = "#PED-" + (arr.length() + 1);
            String fecha = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date());

            JSONObject obj = new JSONObject();
            obj.put("numeroPedido", numeroPedido);
            obj.put("cliente", cliente);
            obj.put("direccion", direccion);
            obj.put("metodoPago", metodoPago);
            obj.put("detalleProductos", detallePedidoTexto);
            obj.put("fecha", fecha);
            obj.put("total", totalPedido);

            arr.put(obj);

            prefs.edit().putString("pedidos_json", arr.toString()).apply();

        } catch (Exception ignored) { }
    }
}