package com.liseth.miprimeraapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class MascotaDAO {

    // Aquí declaro el helper que me permite acceder a la base de datos
    private final DatabaseHelper dbHelper;

    public MascotaDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    // Este metodo me permite guardar una mascota nueva en SQLite
    public long insertarMascota(String nombre, String fechaNacimiento, String edadTexto,
                                String raza, String caracteristicas, String vacunas,
                                String historial, String sexo, String peso, String color,
                                String alergias, String observaciones, String imagenUri) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Aquí organizo los datos que voy a insertar en la tabla mascotas
        ContentValues values = new ContentValues();
        values.put("nombre", nombre);
        values.put("fecha_nacimiento", fechaNacimiento);
        values.put("edad_texto", edadTexto);
        values.put("raza", raza);
        values.put("caracteristicas", caracteristicas);
        values.put("vacunas", vacunas);
        values.put("historial", historial);
        values.put("sexo", sexo);
        values.put("peso", peso);
        values.put("color", color);
        values.put("alergias", alergias);
        values.put("observaciones", observaciones);
        values.put("imagen_uri", imagenUri);

        long resultado = db.insert("mascotas", null, values);
        db.close();

        return resultado;
    }

    // Este metodo me permite actualizar la información de una mascota ya registrada
    public int actualizarMascota(int id, String nombre, String fechaNacimiento, String edadTexto,
                                 String raza, String caracteristicas, String vacunas,
                                 String historial, String sexo, String peso, String color,
                                 String alergias, String observaciones, String imagenUri) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("nombre", nombre);
        values.put("fecha_nacimiento", fechaNacimiento);
        values.put("edad_texto", edadTexto);
        values.put("raza", raza);
        values.put("caracteristicas", caracteristicas);
        values.put("vacunas", vacunas);
        values.put("historial", historial);
        values.put("sexo", sexo);
        values.put("peso", peso);
        values.put("color", color);
        values.put("alergias", alergias);
        values.put("observaciones", observaciones);
        values.put("imagen_uri", imagenUri);

        int resultado = db.update(
                "mascotas",
                values,
                "id = ?",
                new String[]{String.valueOf(id)}
        );

        db.close();
        return resultado;
    }

    // Este metodo me permite consultar todas las mascotas registradas
    public Cursor obtenerMascotas() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM mascotas ORDER BY id DESC",
                null
        );
    }

    // Este metodo me permite consultar una mascota específica por su id
    public Cursor obtenerMascotaPorId(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM mascotas WHERE id = ?",
                new String[]{String.valueOf(id)}
        );
    }
}