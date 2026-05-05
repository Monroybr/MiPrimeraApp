package com.liseth.miprimeraapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Aquí defino el nombre de la base de datos local de mi aplicación
    private static final String DATABASE_NAME = "apppet.db";

    // Aquí aumento la versión porque agregué nuevas tablas al proyecto
    private static final int DATABASE_VERSION = 3;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Tabla para guardar los datos del dueño o usuario
        db.execSQL("CREATE TABLE IF NOT EXISTS usuarios (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nombres TEXT NOT NULL, " +
                "apellidos TEXT NOT NULL, " +
                "fecha_nacimiento TEXT NOT NULL, " +
                "correo TEXT UNIQUE NOT NULL, " +
                "contrasena TEXT NOT NULL, " +
                "telefono TEXT NOT NULL, " +
                "direccion TEXT NOT NULL)");

        // Tabla para guardar el perfil completo de cada mascota
        db.execSQL("CREATE TABLE IF NOT EXISTS mascotas (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nombre TEXT NOT NULL, " +
                "fecha_nacimiento TEXT NOT NULL, " +
                "edad_texto TEXT, " +
                "raza TEXT NOT NULL, " +
                "caracteristicas TEXT, " +
                "vacunas TEXT, " +
                "historial TEXT, " +
                "sexo TEXT, " +
                "peso TEXT, " +
                "color TEXT, " +
                "alergias TEXT, " +
                "observaciones TEXT, " +
                "imagen_uri TEXT)");

        // Tabla para guardar el carnet de vacunación de cada mascota
        db.execSQL("CREATE TABLE IF NOT EXISTS vacunas (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "mascota_id INTEGER NOT NULL, " +
                "vacuna TEXT NOT NULL, " +
                "fecha_aplicacion TEXT, " +
                "lugar TEXT, " +
                "proxima_dosis TEXT, " +
                "FOREIGN KEY(mascota_id) REFERENCES mascotas(id))");

        // Tabla para guardar el historial clínico de cada mascota
        db.execSQL("CREATE TABLE IF NOT EXISTS historial (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "mascota_id INTEGER NOT NULL, " +
                "fecha_registro TEXT, " +
                "enfermedades TEXT, " +
                "procedimientos TEXT, " +
                "medicacion TEXT, " +
                "observaciones TEXT, " +
                "FOREIGN KEY(mascota_id) REFERENCES mascotas(id))");

        // Tabla para guardar las citas veterinarias
        db.execSQL("CREATE TABLE IF NOT EXISTS citas (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "mascota_id INTEGER NOT NULL, " +
                "nombre_mascota TEXT, " +
                "fecha TEXT NOT NULL, " +
                "hora TEXT NOT NULL, " +
                "veterinaria TEXT, " +
                "motivo TEXT, " +
                "recordatorio_activo INTEGER DEFAULT 0, " +
                "FOREIGN KEY(mascota_id) REFERENCES mascotas(id))");

        // Tabla para guardar productos de la tienda
        db.execSQL("CREATE TABLE IF NOT EXISTS productos (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nombre TEXT NOT NULL, " +
                "categoria TEXT, " +
                "precio REAL NOT NULL, " +
                "descripcion TEXT, " +
                "imagen_uri TEXT)");

        // Tabla para guardar productos agregados al carrito
        db.execSQL("CREATE TABLE IF NOT EXISTS carrito (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "producto_id INTEGER, " +
                "nombre TEXT NOT NULL, " +
                "categoria TEXT, " +
                "precio REAL NOT NULL, " +
                "cantidad INTEGER NOT NULL, " +
                "FOREIGN KEY(producto_id) REFERENCES productos(id))");

        // Tabla para guardar pedidos realizados
        db.execSQL("CREATE TABLE IF NOT EXISTS pedidos (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "usuario_id INTEGER, " +
                "fecha TEXT NOT NULL, " +
                "total REAL NOT NULL, " +
                "estado TEXT, " +
                "nombre_cliente TEXT, " +
                "telefono_cliente TEXT, " +
                "direccion_entrega TEXT, " +
                "FOREIGN KEY(usuario_id) REFERENCES usuarios(id))");

        // Tabla para guardar el detalle de cada pedido
        db.execSQL("CREATE TABLE IF NOT EXISTS detalle_pedido (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "pedido_id INTEGER NOT NULL, " +
                "producto_id INTEGER, " +
                "nombre_producto TEXT NOT NULL, " +
                "cantidad INTEGER NOT NULL, " +
                "precio_unitario REAL NOT NULL, " +
                "subtotal REAL NOT NULL, " +
                "FOREIGN KEY(pedido_id) REFERENCES pedidos(id), " +
                "FOREIGN KEY(producto_id) REFERENCES productos(id))");

        // Tabla para guardar notificaciones internas de la app
        db.execSQL("CREATE TABLE IF NOT EXISTS notificaciones (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "titulo TEXT NOT NULL, " +
                "mensaje TEXT NOT NULL, " +
                "tipo TEXT, " +
                "fecha TEXT, " +
                "leida INTEGER DEFAULT 0)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // Si la versión anterior era menor a 2, agrego la tabla mascotas
        if (oldVersion < 2) {
            db.execSQL("CREATE TABLE IF NOT EXISTS mascotas (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "nombre TEXT NOT NULL, " +
                    "fecha_nacimiento TEXT NOT NULL, " +
                    "edad_texto TEXT, " +
                    "raza TEXT NOT NULL, " +
                    "caracteristicas TEXT, " +
                    "vacunas TEXT, " +
                    "historial TEXT, " +
                    "sexo TEXT, " +
                    "peso TEXT, " +
                    "color TEXT, " +
                    "alergias TEXT, " +
                    "observaciones TEXT, " +
                    "imagen_uri TEXT)");
        }

        // Si la versión anterior era menor a 3, agrego las nuevas tablas del proyecto
        if (oldVersion < 3) {

            db.execSQL("CREATE TABLE IF NOT EXISTS vacunas (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "mascota_id INTEGER NOT NULL, " +
                    "vacuna TEXT NOT NULL, " +
                    "fecha_aplicacion TEXT, " +
                    "lugar TEXT, " +
                    "proxima_dosis TEXT, " +
                    "FOREIGN KEY(mascota_id) REFERENCES mascotas(id))");

            db.execSQL("CREATE TABLE IF NOT EXISTS historial (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "mascota_id INTEGER NOT NULL, " +
                    "fecha_registro TEXT, " +
                    "enfermedades TEXT, " +
                    "procedimientos TEXT, " +
                    "medicacion TEXT, " +
                    "observaciones TEXT, " +
                    "FOREIGN KEY(mascota_id) REFERENCES mascotas(id))");

            db.execSQL("CREATE TABLE IF NOT EXISTS citas (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "mascota_id INTEGER NOT NULL, " +
                    "nombre_mascota TEXT, " +
                    "fecha TEXT NOT NULL, " +
                    "hora TEXT NOT NULL, " +
                    "veterinaria TEXT, " +
                    "motivo TEXT, " +
                    "recordatorio_activo INTEGER DEFAULT 0, " +
                    "FOREIGN KEY(mascota_id) REFERENCES mascotas(id))");

            db.execSQL("CREATE TABLE IF NOT EXISTS productos (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "nombre TEXT NOT NULL, " +
                    "categoria TEXT, " +
                    "precio REAL NOT NULL, " +
                    "descripcion TEXT, " +
                    "imagen_uri TEXT)");

            db.execSQL("CREATE TABLE IF NOT EXISTS carrito (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "producto_id INTEGER, " +
                    "nombre TEXT NOT NULL, " +
                    "categoria TEXT, " +
                    "precio REAL NOT NULL, " +
                    "cantidad INTEGER NOT NULL, " +
                    "FOREIGN KEY(producto_id) REFERENCES productos(id))");

            db.execSQL("CREATE TABLE IF NOT EXISTS pedidos (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "usuario_id INTEGER, " +
                    "fecha TEXT NOT NULL, " +
                    "total REAL NOT NULL, " +
                    "estado TEXT, " +
                    "nombre_cliente TEXT, " +
                    "telefono_cliente TEXT, " +
                    "direccion_entrega TEXT, " +
                    "FOREIGN KEY(usuario_id) REFERENCES usuarios(id))");

            db.execSQL("CREATE TABLE IF NOT EXISTS detalle_pedido (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "pedido_id INTEGER NOT NULL, " +
                    "producto_id INTEGER, " +
                    "nombre_producto TEXT NOT NULL, " +
                    "cantidad INTEGER NOT NULL, " +
                    "precio_unitario REAL NOT NULL, " +
                    "subtotal REAL NOT NULL, " +
                    "FOREIGN KEY(pedido_id) REFERENCES pedidos(id), " +
                    "FOREIGN KEY(producto_id) REFERENCES productos(id))");

            db.execSQL("CREATE TABLE IF NOT EXISTS notificaciones (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "titulo TEXT NOT NULL, " +
                    "mensaje TEXT NOT NULL, " +
                    "tipo TEXT, " +
                    "fecha TEXT, " +
                    "leida INTEGER DEFAULT 0)");
        }
    }
}