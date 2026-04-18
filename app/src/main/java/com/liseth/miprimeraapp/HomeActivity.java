package com.liseth.miprimeraapp;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class HomeActivity extends AppCompatActivity {

    private TextView tvListaMascotas, tvBadge;
    private ImageView imgCampana;
    private Button btnNuevaMascota, btnProductos, btnVerMascotas, btnBusqueda, btnMiPerfil;

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        NotificationHelper.crearCanal(this);
        solicitarPermisoNotificaciones();
        programarRecordatoriosAutomaticos();

        tvListaMascotas = findViewById(R.id.tvListaMascotas);
        tvBadge = findViewById(R.id.tvBadge);
        imgCampana = findViewById(R.id.imgCampana);

        btnNuevaMascota = findViewById(R.id.btnNuevaMascota);
        btnProductos = findViewById(R.id.btnProductos);
        btnVerMascotas = findViewById(R.id.btnVerMascotas);
        btnBusqueda = findViewById(R.id.btnBusqueda);
        btnMiPerfil = findViewById(R.id.btnMiPerfil);

        btnNuevaMascota.setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this, AddPetActivity.class))
        );

        btnVerMascotas.setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this, PetsListActivity.class))
        );

        btnProductos.setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this, StoreActivity.class))
        );

        btnBusqueda.setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this, BusquedaActivity.class))
        );

        // Aquí abro la pantalla del perfil del dueño
        btnMiPerfil.setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this, OwnerProfileActivity.class))
        );

        imgCampana.setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this, NotificationsActivity.class))
        );

        mostrarMascotasEnHome();
        actualizarBadgeNotificaciones();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mostrarMascotasEnHome();
        actualizarBadgeNotificaciones();
    }

    private void solicitarPermisoNotificaciones() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }

    private void programarRecordatoriosAutomaticos() {
        PeriodicWorkRequest reminderWork =
                new PeriodicWorkRequest.Builder(ReminderWorker.class, 15, TimeUnit.MINUTES)
                        .build();

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "recordatorio_vacunas_worker",
                ExistingPeriodicWorkPolicy.KEEP,
                reminderWork
        );
    }

    private void mostrarMascotasEnHome() {
        SharedPreferences prefs = getSharedPreferences("mascotas", MODE_PRIVATE);
        String json = prefs.getString("mascotas_json", "[]");

        try {
            JSONArray arr = new JSONArray(json);

            if (arr.length() == 0) {
                tvListaMascotas.setText("Aún no tienes mascotas registradas.");
                return;
            }

            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);

                String nombre = obj.optString("nombre", "-");
                String raza = obj.optString("raza", "-");
                String edad = obj.optString("edadTexto", "-");

                sb.append("• ").append(nombre)
                        .append(" (").append(raza).append(") - ")
                        .append(edad)
                        .append("\n");
            }

            tvListaMascotas.setText(sb.toString());

        } catch (Exception e) {
            tvListaMascotas.setText("Error cargando mascotas.");
        }
    }

    private void actualizarBadgeNotificaciones() {
        int totalNotificaciones = 0;

        totalNotificaciones += contarNotificacionesVacunas();
        totalNotificaciones += contarPromociones();
        totalNotificaciones += contarCitasEjemplo();

        if (totalNotificaciones > 0) {
            tvBadge.setText(String.valueOf(totalNotificaciones));
            tvBadge.setVisibility(View.VISIBLE);
            animarCampana();
        } else {
            tvBadge.setVisibility(View.GONE);
            imgCampana.clearAnimation();
        }
    }

    private void animarCampana() {
        Animation animacion = AnimationUtils.loadAnimation(this, R.anim.campana_anim);
        imgCampana.startAnimation(animacion);
    }

    private int contarNotificacionesVacunas() {
        int total = 0;

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

                String proximaDosis = obj.optString("proximaDosis", "");
                if (proximaDosis.isEmpty()) continue;

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

                if (diffDays < 0 || diffDays <= 7) {
                    total++;
                }
            }

        } catch (Exception ignored) {
        }

        return total;
    }

    private int contarPromociones() {
        return 2;
    }

    private int contarCitasEjemplo() {
        return 1;
    }
}