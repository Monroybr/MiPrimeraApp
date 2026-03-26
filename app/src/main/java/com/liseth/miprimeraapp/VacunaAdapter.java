package com.liseth.miprimeraapp;

import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class VacunaAdapter extends RecyclerView.Adapter<VacunaAdapter.VacunaViewHolder> {

    private final List<Vacuna> vacunas;

    public VacunaAdapter(List<Vacuna> vacunas) {
        this.vacunas = vacunas;
    }

    @NonNull
    @Override
    public VacunaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_vacuna, parent, false);
        return new VacunaViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VacunaViewHolder holder, int position) {

        Vacuna v = vacunas.get(position);

        holder.tvVacunaNombre.setText(v.vacuna);
        holder.tvVacunaAplicacion.setText("Aplicación: " + v.fechaAplicacion);

        holder.tvVacunaLugar.setText(
                "Lugar: " + (v.lugar == null || v.lugar.isEmpty() ? "-" : v.lugar)
        );

        // 🔹 Estilo base (evita que RecyclerView recicle estilos)
        holder.tvVacunaProxima.setText(
                "Próxima dosis: " + (v.proximaDosis == null ? "-" : v.proximaDosis)
        );
        holder.tvVacunaProxima.setTextColor(Color.DKGRAY);
        holder.tvVacunaProxima.setTypeface(null, Typeface.NORMAL);

        if (v.proximaDosis == null || v.proximaDosis.trim().isEmpty()) {
            return;
        }

        try {

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date fechaProx = sdf.parse(v.proximaDosis.trim());

            if (fechaProx == null) return;

            Calendar hoy = Calendar.getInstance();
            hoy.set(Calendar.HOUR_OF_DAY, 0);
            hoy.set(Calendar.MINUTE, 0);
            hoy.set(Calendar.SECOND, 0);
            hoy.set(Calendar.MILLISECOND, 0);

            Calendar prox = Calendar.getInstance();
            prox.setTime(fechaProx);
            prox.set(Calendar.HOUR_OF_DAY, 0);
            prox.set(Calendar.MINUTE, 0);
            prox.set(Calendar.SECOND, 0);
            prox.set(Calendar.MILLISECOND, 0);

            long diffMs = prox.getTimeInMillis() - hoy.getTimeInMillis();
            long diffDays = diffMs / (1000L * 60 * 60 * 24);

            if (diffDays < 0) {

                // 🔴 Vacuna vencida
                holder.tvVacunaProxima.setText("🔴 Vencida: " + v.proximaDosis);
                holder.tvVacunaProxima.setTextColor(Color.RED);
                holder.tvVacunaProxima.setTypeface(null, Typeface.BOLD);

            } else if (diffDays <= 7) {

                // 🟠 Próxima pronto
                holder.tvVacunaProxima.setText("🟠 Próxima en " + diffDays + " días: " + v.proximaDosis);
                holder.tvVacunaProxima.setTextColor(0xFFFF8800);
                holder.tvVacunaProxima.setTypeface(null, Typeface.BOLD);

            } else {

                // ✅ Normal
                holder.tvVacunaProxima.setText("✅ Próxima dosis: " + v.proximaDosis);
                holder.tvVacunaProxima.setTextColor(Color.DKGRAY);
                holder.tvVacunaProxima.setTypeface(null, Typeface.NORMAL);
            }

        } catch (Exception ignored) {
        }
    }

    @Override
    public int getItemCount() {
        return vacunas.size();
    }

    static class VacunaViewHolder extends RecyclerView.ViewHolder {

        TextView tvVacunaNombre;
        TextView tvVacunaAplicacion;
        TextView tvVacunaLugar;
        TextView tvVacunaProxima;

        public VacunaViewHolder(@NonNull View itemView) {
            super(itemView);

            tvVacunaNombre = itemView.findViewById(R.id.tvVacunaNombre);
            tvVacunaAplicacion = itemView.findViewById(R.id.tvVacunaAplicacion);
            tvVacunaLugar = itemView.findViewById(R.id.tvVacunaLugar);
            tvVacunaProxima = itemView.findViewById(R.id.tvVacunaProxima);
        }
    }
}