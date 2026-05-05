package com.liseth.miprimeraapp;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PetsListActivity extends AppCompatActivity {

    private RecyclerView rvMascotas;
    private TextView tvVacio;

    // Aquí declaro el DAO para consultar las mascotas guardadas en SQLite
    private MascotaDAO mascotaDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pets_list);

        rvMascotas = findViewById(R.id.rvMascotas);
        tvVacio = findViewById(R.id.tvVacio);

        // Aquí inicializo el DAO de mascotas
        mascotaDAO = new MascotaDAO(this);

        rvMascotas.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Cada vez que regreso a esta pantalla, cargo las mascotas desde SQLite
        ArrayList<Pet> pets = cargarMascotasDesdeSQLite();

        tvVacio.setVisibility(pets.isEmpty() ? View.VISIBLE : View.GONE);

        rvMascotas.setAdapter(new PetAdapter(pets, position -> {
            Pet pet = pets.get(position);

            Intent intent = new Intent(PetsListActivity.this, PetDetailActivity.class);

            // Aquí envío el id real de SQLite para abrir el detalle correcto
            intent.putExtra("pet_id", pet.id);

            // Mantengo pet_index mientras vacunas e historial siguen en la otra estructura
            intent.putExtra("pet_index", position);

            startActivity(intent);
        }));
    }

    // Este método consulta las mascotas desde SQLite y las convierte en objetos Pet
    private ArrayList<Pet> cargarMascotasDesdeSQLite() {
        ArrayList<Pet> lista = new ArrayList<>();

        Cursor cursor = mascotaDAO.obtenerMascotas();

        try {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                    String nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"));
                    String fecha = cursor.getString(cursor.getColumnIndexOrThrow("fecha_nacimiento"));
                    String edad = cursor.getString(cursor.getColumnIndexOrThrow("edad_texto"));
                    String raza = cursor.getString(cursor.getColumnIndexOrThrow("raza"));
                    String caracteristicas = cursor.getString(cursor.getColumnIndexOrThrow("caracteristicas"));
                    String vacunas = cursor.getString(cursor.getColumnIndexOrThrow("vacunas"));
                    String historial = cursor.getString(cursor.getColumnIndexOrThrow("historial"));
                    String sexo = cursor.getString(cursor.getColumnIndexOrThrow("sexo"));
                    String peso = cursor.getString(cursor.getColumnIndexOrThrow("peso"));
                    String color = cursor.getString(cursor.getColumnIndexOrThrow("color"));
                    String alergias = cursor.getString(cursor.getColumnIndexOrThrow("alergias"));
                    String observaciones = cursor.getString(cursor.getColumnIndexOrThrow("observaciones"));
                    String imagenUri = cursor.getString(cursor.getColumnIndexOrThrow("imagen_uri"));

                    lista.add(new Pet(
                            id,
                            nombre,
                            fecha,
                            edad,
                            raza,
                            caracteristicas,
                            vacunas,
                            historial,
                            sexo,
                            peso,
                            color,
                            alergias,
                            observaciones,
                            imagenUri
                    ));

                } while (cursor.moveToNext());
            }
        } catch (Exception ignored) {
        } finally {
            // Aquí cierro el cursor para evitar errores de memoria
            if (cursor != null) {
                cursor.close();
            }
        }

        return lista;
    }
}