package com.liseth.miprimeraapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pet, parent, false);
        return new PetViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PetViewHolder holder, int position) {
        Pet p = pets.get(position);

        holder.tvNombre.setText(p.nombre);
        holder.tvDatosBasicos.setText(p.raza + " • " + p.fechaNacimiento + " • " + p.edadTexto);

        holder.tvCaracteristicas.setText("Características: " + (p.caracteristicas.isEmpty() ? "-" : p.caracteristicas));
        holder.tvVacunas.setText("Vacunas: " + (p.vacunas.isEmpty() ? "-" : p.vacunas));
        holder.tvHistorial.setText("Historial médico: " + (p.historial.isEmpty() ? "-" : p.historial));

        //  CLICK en toda la tarjeta
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onPetClick(position);
        });
    }

    @Override
    public int getItemCount() {
        return pets.size();
    }

    static class PetViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvDatosBasicos, tvCaracteristicas, tvVacunas, tvHistorial;

        public PetViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvDatosBasicos = itemView.findViewById(R.id.tvDatosBasicos);
            tvCaracteristicas = itemView.findViewById(R.id.tvCaracteristicas);
            tvVacunas = itemView.findViewById(R.id.tvVacunas);
            tvHistorial = itemView.findViewById(R.id.tvHistorial);
        }
    }
}