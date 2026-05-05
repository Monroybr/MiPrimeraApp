package com.liseth.miprimeraapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class UsuarioDAO {

    private final DatabaseHelper dbHelper;

    public UsuarioDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    // Este metodo guarda un usuario nuevo en SQLite
    public long insertarUsuario(String nombres, String apellidos, String fechaNacimiento,
                                String correo, String contrasena, String telefono, String direccion) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("nombres", nombres);
        values.put("apellidos", apellidos);
        values.put("fecha_nacimiento", fechaNacimiento);
        values.put("correo", correo);
        values.put("contrasena", contrasena);
        values.put("telefono", telefono);
        values.put("direccion", direccion);

        long resultado = db.insert("usuarios", null, values);
        db.close();

        return resultado;
    }

    // Este metodo valida si el correo y la contraseña existen en la base de datos
    public boolean validarLogin(String correo, String contrasena) {

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT id FROM usuarios WHERE correo = ? AND contrasena = ?",
                new String[]{correo, contrasena}
        );

        boolean existe = cursor.moveToFirst();

        cursor.close();
        db.close();

        return existe;
    }

    // Este metodo valida si el correo ya está registrado
    public boolean existeCorreo(String correo) {

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT id FROM usuarios WHERE correo = ?",
                new String[]{correo}
        );

        boolean existe = cursor.moveToFirst();

        cursor.close();
        db.close();

        return existe;
    }
}