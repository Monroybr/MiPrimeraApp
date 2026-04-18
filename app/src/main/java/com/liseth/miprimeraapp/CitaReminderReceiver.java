package com.liseth.miprimeraapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class CitaReminderReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        // Aquí recupero los datos enviados cuando programé la alarma de la cita
        String nombreMascota = intent.getStringExtra("nombreMascota");
        String fecha = intent.getStringExtra("fecha");
        String hora = intent.getStringExtra("hora");
        String veterinaria = intent.getStringExtra("veterinaria");

        if (nombreMascota == null) nombreMascota = "tu mascota";
        if (fecha == null) fecha = "-";
        if (hora == null) hora = "-";
        if (veterinaria == null) veterinaria = "-";

        // Aquí me aseguro de que el canal exista
        NotificationHelper.crearCanal(context);

        // Aquí muestro la notificación exacta de la cita
        NotificationHelper.mostrarNotificacion(
                context,
                "Cita veterinaria programada",
                "Hoy tienes cita para " + nombreMascota + " a las " + hora + " en " + veterinaria + ". Fecha: " + fecha
        );
    }
}