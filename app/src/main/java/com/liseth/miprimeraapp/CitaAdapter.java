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
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cita, parent, false);

        return new CitaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CitaViewHolder holder, int position) {
        Cita cita = lista.get(position);

        holder.tvFechaHoraCita.setText("Fecha: " + cita.fecha + " - Hora: " + cita.hora);
        holder.tvVeterinariaCita.setText("Veterinaria: " + (cita.veterinaria == null || cita.veterinaria.isEmpty() ? "-" : cita.veterinaria));
        holder.tvMotivoCita.setText("Motivo: " + (cita.motivo == null || cita.motivo.isEmpty() ? "-" : cita.motivo));
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    static class CitaViewHolder extends RecyclerView.ViewHolder {

        TextView tvFechaHoraCita, tvVeterinariaCita, tvMotivoCita;

        public CitaViewHolder(@NonNull View itemView) {
            super(itemView);

            tvFechaHoraCita = itemView.findViewById(R.id.tvFechaHoraCita);
            tvVeterinariaCita = itemView.findViewById(R.id.tvVeterinariaCita);
            tvMotivoCita = itemView.findViewById(R.id.tvMotivoCita);
        }
    }
}