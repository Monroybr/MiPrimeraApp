package com.liseth.miprimeraapp;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationHelper {

    // Aquí defino el id del canal de notificaciones
    public static final String CHANNEL_ID = "canal_notificaciones_mascotas";

    // Este método crea el canal de notificaciones para Android 8 o superior
    public static void crearCanal(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            // Aquí uso el sonido por defecto del sistema
            Uri soundUri = android.provider.Settings.System.DEFAULT_NOTIFICATION_URI;

            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();

            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Notificaciones Mascotas",
                    NotificationManager.IMPORTANCE_HIGH
            );

            // Configuro descripción, vibración y sonido
            channel.setDescription("Canal para vacunas, citas y alertas de mascotas");
            channel.enableVibration(true);
            channel.setSound(soundUri, audioAttributes);

            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    // Este método muestra una notificación emergente con sonido
    public static void mostrarNotificacion(Context context, String titulo, String mensaje) {

        // Aquí creo el intent para abrir la pantalla de notificaciones al tocar la alerta
        Intent intent = new Intent(context, NotificationsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(titulo)
                .setContentText(mensaje)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(mensaje))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setDefaults(NotificationCompat.DEFAULT_ALL);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        // Antes de mostrar la notificación, valido el permiso en Android 13+
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // Aquí muestro la notificación usando un id único basado en el tiempo
        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }
}