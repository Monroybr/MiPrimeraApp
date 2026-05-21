package com.liseth.miprimeraapp;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.InputStream;

public class PetDetailActivity extends AppCompatActivity {

    private TextView tvNombreDetalle, tvInfoBasicaDetalle, tvCaracteristicasDetalle,
            tvVacunasDetalle, tvHistorialDetalle,
            tvSexoDetalle, tvPesoDetalle, tvColorDetalle, tvAlergiasDetalle, tvObservacionesDetalle;

    private ImageView imgMascotaDetalle;

    private Button btnEditarMascota, btnAgregarVacuna, btnCarnetVacunas,
            btnAgregarHistorial, btnVerHistorial, btnAgendarCita,
            btnVerCitas, btnCompartirInfo;

    private int petId = -1;
    private int petIndex = -1;

    private MascotaDAO mascotaDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_detail);

        mascotaDAO = new MascotaDAO(this);

        imgMascotaDetalle = findViewById(R.id.imgMascotaDetalle);

        tvNombreDetalle = findViewById(R.id.tvNombreDetalle);
        tvInfoBasicaDetalle = findViewById(R.id.tvInfoBasicaDetalle);
        tvCaracteristicasDetalle = findViewById(R.id.tvCaracteristicasDetalle);
        tvVacunasDetalle = findViewById(R.id.tvVacunasDetalle);
        tvHistorialDetalle = findViewById(R.id.tvHistorialDetalle);

        tvSexoDetalle = findViewById(R.id.tvSexoDetalle);
        tvPesoDetalle = findViewById(R.id.tvPesoDetalle);
        tvColorDetalle = findViewById(R.id.tvColorDetalle);
        tvAlergiasDetalle = findViewById(R.id.tvAlergiasDetalle);
        tvObservacionesDetalle = findViewById(R.id.tvObservacionesDetalle);

        btnEditarMascota = findViewById(R.id.btnEditarMascota);
        btnAgregarVacuna = findViewById(R.id.btnAgregarVacuna);
        btnCarnetVacunas = findViewById(R.id.btnCarnetVacunas);
        btnAgregarHistorial = findViewById(R.id.btnAgregarHistorial);
        btnVerHistorial = findViewById(R.id.btnVerHistorial);
        btnAgendarCita = findViewById(R.id.btnAgendarCita);
        btnVerCitas = findViewById(R.id.btnVerCitas);
        btnCompartirInfo = findViewById(R.id.btnCompartirInfo);

        petId = getIntent().getIntExtra("pet_id", -1);
        petIndex = getIntent().getIntExtra("pet_index", -1);

        if (petId == -1) {
            mostrarMascotaNoEncontrada();
            return;
        }

        btnEditarMascota.setOnClickListener(v -> {
            Intent intent = new Intent(PetDetailActivity.this, AddPetActivity.class);
            intent.putExtra("modo_edicion", true);
            intent.putExtra("pet_id", petId);
            startActivity(intent);
        });

        btnAgregarVacuna.setOnClickListener(v -> {
            Intent intent = new Intent(PetDetailActivity.this, AddVacunacionActivity.class);
            intent.putExtra("pet_id", petId);
            intent.putExtra("pet_index", petIndex);
            startActivity(intent);
        });

        btnCarnetVacunas.setOnClickListener(v -> {
            Intent intent = new Intent(PetDetailActivity.this, VacunasListActivity.class);
            intent.putExtra("pet_id", petId);
            intent.putExtra("pet_index", petIndex);
            startActivity(intent);
        });

        btnAgregarHistorial.setOnClickListener(v -> {
            Intent intent = new Intent(PetDetailActivity.this, AddHistorialActivity.class);
            intent.putExtra("pet_id", petId);
            intent.putExtra("pet_index", petIndex);
            startActivity(intent);
        });

        btnVerHistorial.setOnClickListener(v -> {
            Intent intent = new Intent(PetDetailActivity.this, HistorialListActivity.class);
            intent.putExtra("pet_id", petId);
            intent.putExtra("pet_index", petIndex);
            startActivity(intent);
        });

        btnAgendarCita.setOnClickListener(v -> {
            Intent intent = new Intent(PetDetailActivity.this, AddCitaActivity.class);
            intent.putExtra("pet_id", petId);
            intent.putExtra("pet_index", petIndex);
            startActivity(intent);
        });

        btnVerCitas.setOnClickListener(v -> {
            Intent intent = new Intent(PetDetailActivity.this, CitasListActivity.class);
            intent.putExtra("pet_id", petId);
            intent.putExtra("pet_index", petIndex);
            startActivity(intent);
        });

        btnCompartirInfo.setOnClickListener(v -> {
            Intent intent = new Intent(PetDetailActivity.this, SharePetInfoActivity.class);
            intent.putExtra("pet_index", petIndex);
            intent.putExtra("pet_id", petId);
            startActivity(intent);
        });

        cargarMascotaPorId(petId);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (petId != -1) {
            cargarMascotaPorId(petId);
        }
    }

    private void cargarMascotaPorId(int id) {
        Cursor cursor = mascotaDAO.obtenerMascotaPorId(id);

        try {
            if (cursor != null && cursor.moveToFirst()) {
                String nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"));
                String fecha = cursor.getString(cursor.getColumnIndexOrThrow("fecha_nacimiento"));
                String edad = cursor.getString(cursor.getColumnIndexOrThrow("edad_texto"));
                String raza = cursor.getString(cursor.getColumnIndexOrThrow("raza"));
                String carac = cursor.getString(cursor.getColumnIndexOrThrow("caracteristicas"));
                String sexo = cursor.getString(cursor.getColumnIndexOrThrow("sexo"));
                String peso = cursor.getString(cursor.getColumnIndexOrThrow("peso"));
                String color = cursor.getString(cursor.getColumnIndexOrThrow("color"));
                String alergias = cursor.getString(cursor.getColumnIndexOrThrow("alergias"));
                String observaciones = cursor.getString(cursor.getColumnIndexOrThrow("observaciones"));
                String imagenUri = cursor.getString(cursor.getColumnIndexOrThrow("imagen_uri"));

                tvNombreDetalle.setText(nombre == null || nombre.isEmpty() ? "Sin nombre" : nombre);
                tvInfoBasicaDetalle.setText(raza + " • " + fecha + " • " + edad);
                tvCaracteristicasDetalle.setText("Características: " + (carac == null || carac.isEmpty() ? "-" : carac));
                tvSexoDetalle.setText("Sexo: " + (sexo == null || sexo.isEmpty() ? "-" : sexo));
                tvPesoDetalle.setText("Peso: " + (peso == null || peso.isEmpty() ? "-" : peso + " kg"));
                tvColorDetalle.setText("Color: " + (color == null || color.isEmpty() ? "-" : color));
                tvAlergiasDetalle.setText("Alergias: " + (alergias == null || alergias.isEmpty() ? "-" : alergias));
                tvObservacionesDetalle.setText("Observaciones: " + (observaciones == null || observaciones.isEmpty() ? "-" : observaciones));

                tvVacunasDetalle.setText("Vacunas registradas: revisa el carnet de vacunación");
                tvHistorialDetalle.setText("Historial médico: revisa los registros clínicos");

                if (imagenUri != null && !imagenUri.isEmpty()) {
                    try {
                        Uri uri = Uri.parse(imagenUri);
                        InputStream inputStream = getContentResolver().openInputStream(uri);
                        imgMascotaDetalle.setImageBitmap(BitmapFactory.decodeStream(inputStream));
                    } catch (Exception ignored) {
                        imgMascotaDetalle.setImageResource(R.mipmap.ic_launcher);
                    }
                } else {
                    imgMascotaDetalle.setImageResource(R.mipmap.ic_launcher);
                }
            } else {
                mostrarMascotaNoEncontrada();
            }
        } catch (Exception e) {
            mostrarMascotaNoEncontrada();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void mostrarMascotaNoEncontrada() {
        tvNombreDetalle.setText("Mascota no encontrada");
        tvInfoBasicaDetalle.setText("-");
        tvCaracteristicasDetalle.setText("Características: -");
        tvSexoDetalle.setText("Sexo: -");
        tvPesoDetalle.setText("Peso: -");
        tvColorDetalle.setText("Color: -");
        tvAlergiasDetalle.setText("Alergias: -");
        tvObservacionesDetalle.setText("Observaciones: -");
        tvVacunasDetalle.setText("Vacunas registradas: 0");
        tvHistorialDetalle.setText("Registros clínicos: 0");
        imgMascotaDetalle.setImageResource(R.mipmap.ic_launcher);
    }
}