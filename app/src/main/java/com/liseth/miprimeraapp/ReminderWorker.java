package com.liseth.miprimeraapp;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ReminderWorker extends Worker {

    // Constructor del Worker
    public ReminderWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        // Aquí me aseguro de que exista el canal de notificaciones
        NotificationHelper.crearCanal(getApplicationContext());

        // Aquí ejecuto la revisión automática de vacunas
        revisarVacunas();

        // Si todo sale bien, retorno éxito
        return Result.success();
    }

    // Este método revisa las vacunas guardadas y genera notificaciones automáticas
    private void revisarVacunas() {
        SharedPreferences prefs = getApplicationContext()
                .getSharedPreferences("vacunas", Context.MODE_PRIVATE);

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

                if (proximaDosis.isEmpty()) {
                    continue;
                }

                Date fechaProx = sdf.parse(proximaDosis);
                if (fechaProx == null) {
                    continue;
                }

                Calendar prox = Calendar.getInstance();
                prox.setTime(fechaProx);
                prox.set(Calendar.HOUR_OF_DAY, 0);
                prox.set(Calendar.MINUTE, 0);
                prox.set(Calendar.SECOND, 0);
                prox.set(Calendar.MILLISECOND, 0);

                long diffMs = prox.getTimeInMillis() - hoy.getTimeInMillis();
                long diffDays = diffMs / (1000L * 60 * 60 * 24);

                // Aquí notifico si la vacuna ya venció
                if (diffDays < 0) {
                    NotificationHelper.mostrarNotificacion(
                            getApplicationContext(),
                            "Vacuna vencida",
                            "La vacuna \"" + vacuna + "\" ya está vencida desde " + proximaDosis + "."
                    );
                }

                // Aquí notifico si la vacuna vence en 3 días o menos
                else if (diffDays <= 3) {
                    NotificationHelper.mostrarNotificacion(
                            getApplicationContext(),
                            "Vacuna próxima",
                            "La vacuna \"" + vacuna + "\" vence en " + diffDays + " días (" + proximaDosis + ")."
                    );
                }
            }

        } catch (Exception ignored) {
        }
    }
}