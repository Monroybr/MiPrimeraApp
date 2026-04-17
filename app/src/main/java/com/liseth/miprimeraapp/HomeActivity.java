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

    // Aquí declaro los elementos de la interfaz de la pantalla principal
    private TextView tvListaMascotas, tvBadge;
    private ImageView imgCampana;
    private Button btnNuevaMascota, btnProductos, btnVerMascotas, btnBusqueda;

    // Este lanzador me sirve para pedir el permiso de notificaciones en Android 13 o superior
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                // Por ahora no necesito hacer una acción adicional cuando el usuario responda
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Aquí creo el canal de notificaciones para asegurar que las alertas funcionen correctamente
        NotificationHelper.crearCanal(this);

        // Aquí solicito el permiso de notificaciones si el dispositivo lo requiere
        solicitarPermisoNotificaciones();

        // Aquí programo la revisión automática de vacunas en segundo plano con WorkManager
        programarRecordatoriosAutomaticos();

        // Relaciono las variables con los elementos del XML
        tvListaMascotas = findViewById(R.id.tvListaMascotas);
        tvBadge = findViewById(R.id.tvBadge);
        imgCampana = findViewById(R.id.imgCampana);

        btnNuevaMascota = findViewById(R.id.btnNuevaMascota);
        btnProductos = findViewById(R.id.btnProductos);
        btnVerMascotas = findViewById(R.id.btnVerMascotas);
        btnBusqueda = findViewById(R.id.btnBusqueda);

        // Botón para registrar una nueva mascota
        btnNuevaMascota.setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this, AddPetActivity.class))
        );

        // Botón para ver la lista completa de mascotas registradas
        btnVerMascotas.setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this, PetsListActivity.class))
        );

        // Botón para abrir la tienda
        btnProductos.setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this, StoreActivity.class))
        );

        // Botón para abrir el mapa de veterinarias cercanas
        btnBusqueda.setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this, BusquedaActivity.class))
        );

        // Evento de la campana para abrir la pantalla de notificaciones
        imgCampana.setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this, NotificationsActivity.class))
        );

        // Muestro el resumen de mascotas en la pantalla principal
        mostrarMascotasEnHome();

        // Actualizo el número de notificaciones visibles en el badge
        actualizarBadgeNotificaciones();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Cada vez que regreso a esta pantalla, actualizo la información visible
        mostrarMascotasEnHome();
        actualizarBadgeNotificaciones();
    }

    // Este método me sirve para pedir el permiso de notificaciones en Android 13 o superior
    private void solicitarPermisoNotificaciones() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }

    // Aquí programo una tarea periódica para revisar vacunas automáticamente en segundo plano
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

    // Este método me permite mostrar un resumen de las mascotas registradas en la pantalla principal
    private void mostrarMascotasEnHome() {
        SharedPreferences prefs = getSharedPreferences("mascotas", MODE_PRIVATE);
        String json = prefs.getString("mascotas_json", "[]");

        try {
            JSONArray arr = new JSONArray(json);

            // Si no hay mascotas registradas, muestro un mensaje por defecto
            if (arr.length() == 0) {
                tvListaMascotas.setText("Aún no tienes mascotas registradas.");
                return;
            }

            StringBuilder sb = new StringBuilder();

            // Recorro cada mascota guardada y construyo el texto de resumen
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

    // Este método calcula cuántas notificaciones activas tengo y actualiza el badge rojo
    private void actualizarBadgeNotificaciones() {
        int totalNotificaciones = 0;

        // Cuento las notificaciones relacionadas con vacunas
        totalNotificaciones += contarNotificacionesVacunas();

        // Agrego promociones de ejemplo
        totalNotificaciones += contarPromociones();

        // Agrego una cita de ejemplo
        totalNotificaciones += contarCitasEjemplo();

        // Si hay notificaciones, muestro el badge y animo la campana
        if (totalNotificaciones > 0) {
            tvBadge.setText(String.valueOf(totalNotificaciones));
            tvBadge.setVisibility(View.VISIBLE);
            animarCampana();
        } else {
            tvBadge.setVisibility(View.GONE);
            imgCampana.clearAnimation();
        }
    }

    // Este método aplica la animación de campana cuando existen notificaciones
    private void animarCampana() {
        Animation animacion = AnimationUtils.loadAnimation(this, R.anim.campana_anim);
        imgCampana.startAnimation(animacion);
    }

    // Aquí reviso las vacunas guardadas para saber si generan notificación en el badge
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

                // Si la vacuna está vencida o vence dentro de 7 días, la cuento como notificación
                if (diffDays < 0 || diffDays <= 7) {
                    total++;
                }
            }

        } catch (Exception ignored) {
        }

        return total;
    }

    // Aquí defino promociones de ejemplo para mostrar en el contador
    private int contarPromociones() {
        return 2;
    }

    // Aquí defino una cita de ejemplo para mostrar en el contador
    private int contarCitasEjemplo() {
        return 1;
    }
}