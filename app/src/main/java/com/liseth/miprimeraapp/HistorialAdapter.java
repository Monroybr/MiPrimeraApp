package com.liseth.miprimeraapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HistorialAdapter extends RecyclerView.Adapter<HistorialAdapter.HistorialViewHolder> {

    public interface OnHistorialClickListener {
        void onHistorialClick(int position, Historial item);
    }

    private final List<Historial> lista;
    private final OnHistorialClickListener listener;

    public HistorialAdapter(List<Historial> lista, OnHistorialClickListener listener) {
        this.lista = lista;
        this.listener = listener;
    }

    @NonNull
    @Override
    public HistorialViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_historial, parent, false);
        return new HistorialViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull HistorialViewHolder holder, int position) {
        Historial h = lista.get(position);

        holder.tvFechaHist.setText("Fecha: " + h.fechaRegistro);
        holder.tvEnfermedadesHist.setText("Enfermedades: " + (h.enfermedades == null || h.enfermedades.isEmpty() ? "-" : h.enfermedades));
        holder.tvMedicacionHist.setText("Medicación: " + (h.medicacion == null || h.medicacion.isEmpty() ? "-" : h.medicacion));
        holder.tvProcedimientosHist.setText("Procedimientos: " + (h.procedimientos == null || h.procedimientos.isEmpty() ? "-" : h.procedimientos));

        holder.itemView.setOnClickListener(v -> {
            if (listener == null) return;

            int pos = holder.getAdapterPosition();
            if (pos == RecyclerView.NO_POSITION) return;

            Historial item = lista.get(pos);
            listener.onHistorialClick(pos, item);
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    static class HistorialViewHolder extends RecyclerView.ViewHolder {

        TextView tvFechaHist, tvEnfermedadesHist, tvMedicacionHist, tvProcedimientosHist;

        public HistorialViewHolder(@NonNull View itemView) {
            super(itemView);

            tvFechaHist = itemView.findViewById(R.id.tvFechaHist);
            tvEnfermedadesHist = itemView.findViewById(R.id.tvEnfermedadesHist);
            tvMedicacionHist = itemView.findViewById(R.id.tvMedicacionHist);
            tvProcedimientosHist = itemView.findViewById(R.id.tvProcedimientosHist);
        }
    }
}