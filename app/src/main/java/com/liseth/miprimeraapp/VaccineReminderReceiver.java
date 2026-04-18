package com.liseth.miprimeraapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class VaccineReminderReceiver extends BroadcastReceiver {

    // Este receiver se ejecuta cuando llega la fecha exacta programada con AlarmManager
    @Override
    public void onReceive(Context context, Intent intent) {

        // Obtengo los datos que envié al programar la alarma
        String nombreMascota = intent.getStringExtra("nombreMascota");
        String vacuna = intent.getStringExtra("vacuna");
        String proximaDosis = intent.getStringExtra("proximaDosis");

        if (nombreMascota == null) nombreMascota = "tu mascota";
        if (vacuna == null) vacuna = "vacuna";
        if (proximaDosis == null) proximaDosis = "hoy";

        // Me aseguro de que el canal de notificaciones exista
        NotificationHelper.crearCanal(context);

        // Aquí muestro la notificación exacta el día programado
        NotificationHelper.mostrarNotificacion(
                context,
                "Recordatorio de vacuna",
                "Hoy corresponde la vacuna \"" + vacuna + "\" para " + nombreMascota + ". Fecha programada: " + proximaDosis
        );
    }
}