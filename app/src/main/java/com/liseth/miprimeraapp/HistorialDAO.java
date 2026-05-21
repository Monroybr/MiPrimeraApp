package com.liseth.miprimeraapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class HistorialDAO {

    // Aquí declaro el helper que me permite acceder a la base de datos SQLite
    private final DatabaseHelper dbHelper;

    public HistorialDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    // Este metodo me permite guardar un registro de historial clínico en SQLite
    public long insertarHistorial(int mascotaId, String fechaRegistro, String enfermedades,
                                  String procedimientos, String medicacion, String observaciones) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("mascota_id", mascotaId);
        values.put("fecha_registro", fechaRegistro);
        values.put("enfermedades", enfermedades);
        values.put("procedimientos", procedimientos);
        values.put("medicacion", medicacion);
        values.put("observaciones", observaciones);

        long resultado = db.insert("historial", null, values);
        db.close();

        return resultado;
    }

    // Este metodo me permite consultar todos los registros clínicos de una mascota
    public Cursor obtenerHistorialPorMascota(int mascotaId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM historial WHERE mascota_id = ? ORDER BY id DESC",
                new String[]{String.valueOf(mascotaId)}
        );
    }

    // Este metodo me permite consultar un registro específico por su id
    public Cursor obtenerHistorialPorId(int historialId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM historial WHERE id = ?",
                new String[]{String.valueOf(historialId)}
        );
    }

    // Este metodo me permite eliminar un registro clínico por su id
    public int eliminarHistorial(int historialId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int resultado = db.delete(
                "historial",
                "id = ?",
                new String[]{String.valueOf(historialId)}
        );

        db.close();
        return resultado;
    }

    // Este metodo me permite contar cuántos registros clínicos tiene una mascota
    public int contarHistorialPorMascota(int mascotaId) {
        int total = 0;

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM historial WHERE mascota_id = ?",
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