package com.liseth.miprimeraapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    private final List<NotificationItem> notifications;

    public NotificationAdapter(List<NotificationItem> notifications) {
        this.notifications = notifications;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        NotificationItem item = notifications.get(position);

        holder.tvTituloNotificacion.setText(item.titulo);
        holder.tvTipoNotificacion.setText("Tipo: " + item.tipo);
        holder.tvMensajeNotificacion.setText(item.mensaje);
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    static class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView tvTituloNotificacion, tvTipoNotificacion, tvMensajeNotificacion;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTituloNotificacion = itemView.findViewById(R.id.tvTituloNotificacion);
            tvTipoNotificacion = itemView.findViewById(R.id.tvTipoNotificacion);
            tvMensajeNotificacion = itemView.findViewById(R.id.tvMensajeNotificacion);
        }
    }
}