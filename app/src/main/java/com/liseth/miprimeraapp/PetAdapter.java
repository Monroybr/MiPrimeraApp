package com.liseth.miprimeraapp;

import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.InputStream;
import java.util.List;

public class PetAdapter extends RecyclerView.Adapter<PetAdapter.PetViewHolder> {

    public interface OnPetClickListener {
        void onPetClick(int position);
    }

    private final List<Pet> pets;
    private final OnPetClickListener listener;

    public PetAdapter(List<Pet> pets, OnPetClickListener listener) {
        this.pets = pets;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pet, parent, false);
        return new PetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PetViewHolder holder, int position) {
        Pet pet = pets.get(position);

        holder.tvNombre.setText(pet.nombre);
        holder.tvDatosBasicos.setText(pet.raza + " • " + pet.fechaNacimiento + " • " + pet.edadTexto);
        holder.tvCaracteristicas.setText("Características: " + (pet.caracteristicas == null || pet.caracteristicas.isEmpty() ? "-" : pet.caracteristicas));
        holder.tvVacunas.setText("Vacunas: " + (pet.vacunas == null || pet.vacunas.isEmpty() ? "-" : pet.vacunas));
        holder.tvHistorial.setText("Historial médico: " + (pet.historial == null || pet.historial.isEmpty() ? "-" : pet.historial));

        // Aquí intento mostrar la foto de la mascota si existe
        if (pet.imagenUri != null && !pet.imagenUri.isEmpty()) {
            try {
                Uri uri = Uri.parse(pet.imagenUri);
                InputStream inputStream = holder.itemView.getContext().getContentResolver().openInputStream(uri);
                holder.imgPetItem.setImageBitmap(BitmapFactory.decodeStream(inputStream));
            } catch (Exception e) {
                holder.imgPetItem.setImageResource(R.mipmap.ic_launcher);
            }
        } else {
            holder.imgPetItem.setImageResource(R.mipmap.ic_launcher);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onPetClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return pets.size();
    }

    static class PetViewHolder extends RecyclerView.ViewHolder {

        ImageView imgPetItem;
        TextView tvNombre, tvDatosBasicos, tvCaracteristicas, tvVacunas, tvHistorial;

        public PetViewHolder(@NonNull View itemView) {
            super(itemView);

            imgPetItem = itemView.findViewById(R.id.imgPetItem);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvDatosBasicos = itemView.findViewById(R.id.tvDatosBasicos);
            tvCaracteristicas = itemView.findViewById(R.id.tvCaracteristicas);
            tvVacunas = itemView.findViewById(R.id.tvVacunas);
            tvHistorial = itemView.findViewById(R.id.tvHistorial);
        }
    }
}