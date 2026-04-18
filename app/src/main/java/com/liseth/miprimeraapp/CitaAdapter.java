package com.liseth.miprimeraapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CitaAdapter extends RecyclerView.Adapter<CitaAdapter.CitaViewHolder> {

    private final List<Cita> lista;

    public CitaAdapter(List<Cita> lista) {
        this.lista = lista;
    }

    @NonNull
    @Override
    public CitaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Aquí inflo el diseño de cada tarjeta de cita
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cita, parent, false);
        return new CitaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CitaViewHolder holder, int position) {
        // Aquí obtengo la cita correspondiente a la posición actual
        Cita cita = lista.get(position);

        // Aquí muestro la información de la cita en pantalla
        holder.tvMascotaCita.setText("Mascota: " + cita.nombreMascota);
        holder.tvFechaHoraCita.setText("Fecha: " + cita.fecha + " - Hora: " + cita.hora);
        holder.tvVeterinariaCita.setText("Veterinaria: " + cita.veterinaria);
        holder.tvMotivoCita.setText("Motivo: " + cita.motivo);
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    static class CitaViewHolder extends RecyclerView.ViewHolder {

        TextView tvMascotaCita, tvFechaHoraCita, tvVeterinariaCita, tvMotivoCita;

        public CitaViewHolder(@NonNull View itemView) {
            super(itemView);

            tvMascotaCita = itemView.findViewById(R.id.tvMascotaCita);
            tvFechaHoraCita = itemView.findViewById(R.id.tvFechaHoraCita);
            tvVeterinariaCita = itemView.findViewById(R.id.tvVeterinariaCita);
            tvMotivoCita = itemView.findViewById(R.id.tvMotivoCita);
        }
    }
}