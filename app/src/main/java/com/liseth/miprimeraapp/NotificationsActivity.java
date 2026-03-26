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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class NotificationsActivity extends AppCompatActivity {

    private RecyclerView rvNotifications;
    private TextView tvVacioNotifications;

    private final ArrayList<NotificationItem> notificationList = new ArrayList<>();
    private NotificationAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        rvNotifications = findViewById(R.id.rvNotifications);
        tvVacioNotifications = findViewById(R.id.tvVacioNotifications);

        rvNotifications.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NotificationAdapter(notificationList);
        rvNotifications.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarNotificaciones();
    }

    private void cargarNotificaciones() {
        notificationList.clear();

        cargarNotificacionesVacunas();
        cargarPromociones();
        cargarCitasEjemplo();

        adapter.notifyDataSetChanged();
        tvVacioNotifications.setVisibility(notificationList.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void cargarNotificacionesVacunas() {
        SharedPreferences prefs = getSharedPreferences("vacunas", MODE_PRIVATE);
        String json = prefs.getString("vacunas_json", "[]");

        try {
            JSONArray arr = new JSONArray(json);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

            Calendar hoy = Calendar.getInstance();
            hoy.set(Calendar.HOUR_OF_DAY, 0);
            hoy.set(Calendar.MINUTE, 0);
            hoy.set(Calendar.SECOND, 0);
            hoy.set(Calendar.MILLISECOND, 0);

            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);

                String vacuna = obj.optString("vacuna", "");
                String proximaDosis = obj.optString("proximaDosis", "");

                Date fechaProx = sdf.parse(proximaDosis);
                if (fechaProx == null) continue;

                Calendar prox = Calendar.getInstance();
                prox.setTime(fechaProx);
                prox.set(Calendar.HOUR_OF_DAY, 0);
                prox.set(Calendar.MINUTE, 0);
                prox.set(Calendar.SECOND, 0);
                prox.set(Calendar.MILLISECOND, 0);

                long diffMs = prox.getTimeInMillis() - hoy.getTimeInMillis();
                long diffDays = diffMs / (1000L * 60 * 60 * 24);

                if (diffDays < 0) {
                    notificationList.add(new NotificationItem(
                            "Vacuna vencida",
                            "La vacuna \"" + vacuna + "\" ya está vencida desde " + proximaDosis + ".",
                            "Vacunación"
                    ));
                } else if (diffDays <= 7) {
                    notificationList.add(new NotificationItem(
                            "Próxima vacuna",
                            "La vacuna \"" + vacuna + "\" vence en " + diffDays + " días (" + proximaDosis + ").",
                            "Vacunación"
                    ));
                }
            }

        } catch (Exception ignored) { }
    }

    private void cargarPromociones() {
        notificationList.add(new NotificationItem(
                "Promoción",
                "20% de descuento en concentrado para perro esta semana.",
                "Tienda"
        ));

        notificationList.add(new NotificationItem(
                "Promoción",
                "Lleva 2 juguetes y paga 1 en accesorios seleccionados.",
                "Tienda"
        ));
    }

    private void cargarCitasEjemplo() {
        // Por ahora son ejemplos, luego lo conectamos con citas reales
        notificationList.add(new NotificationItem(
                "Cita próxima",
                "Recuerda la cita veterinaria de tu mascota este viernes a las 3:00 p. m.",
                "Citas"
        ));
    }
}