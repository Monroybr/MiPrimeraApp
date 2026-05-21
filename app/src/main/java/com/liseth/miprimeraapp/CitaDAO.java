package com.liseth.miprimeraapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CitaDAO {

    private final DatabaseHelper dbHelper;

    public CitaDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    // Este metodo me permite guardar una cita veterinaria
    public long insertarCita(int mascotaId,
                             String nombreMascota,
                             String fecha,
                             String hora,
                             String veterinaria,
                             String motivo) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("mascota_id", mascotaId);
        values.put("nombre_mascota", nombreMascota);
        values.put("fecha", fecha);
        values.put("hora", hora);
        values.put("veterinaria", veterinaria);
        values.put("motivo", motivo);

        long resultado = db.insert("citas", null, values);

        db.close();

        return resultado;
    }

    // Este metodo me permite consultar las citas de una mascota
    public Cursor obtenerCitasPorMascota(int mascotaId) {

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM citas WHERE mascota_id = ? ORDER BY id DESC",
                new String[]{String.valueOf(mascotaId)}
        );
    }

    // Este metodo me permite contar las citas de una mascota
    public int contarCitasPorMascota(int mascotaId) {

        int total = 0;

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM citas WHERE mascota_id = ?",
                new String[]{String.valueOf(mascotaId)}
        );

        if (cursor.moveToFirst()) {
            total = cursor.getInt(0);
        }

        cursor.close();
        db.close();

        return total;
    }
}