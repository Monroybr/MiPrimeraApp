package com.liseth.miprimeraapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class VacunaDAO {

    // Aquí declaro el helper que me permite acceder a la base de datos
    private final DatabaseHelper dbHelper;

    public VacunaDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    // Este metodo me permite guardar una vacuna en SQLite
    public long insertarVacuna(int mascotaId, String vacuna, String fechaAplicacion,
                               String lugar, String proximaDosis) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("mascota_id", mascotaId);
        values.put("vacuna", vacuna);
        values.put("fecha_aplicacion", fechaAplicacion);
        values.put("lugar", lugar);
        values.put("proxima_dosis", proximaDosis);

        long resultado = db.insert("vacunas", null, values);
        db.close();

        return resultado;
    }

    // Este metodo me permite consultar las vacunas de una mascota específica
    public Cursor obtenerVacunasPorMascota(int mascotaId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM vacunas WHERE mascota_id = ? ORDER BY id DESC",
                new String[]{String.valueOf(mascotaId)}
        );
    }

    // Este metodo me permite contar cuántas vacunas tiene una mascota
    public int contarVacunasPorMascota(int mascotaId) {
        int total = 0;

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM vacunas WHERE mascota_id = ?",
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