package com.liseth.miprimeraapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    public interface OnCartActionListener {
        void onAumentar(CartItem item);
        void onDisminuir(CartItem item);
        void onEliminar(CartItem item);
    }

    private final List<CartItem> items;
    private final OnCartActionListener listener;

    public CartAdapter(List<CartItem> items, OnCartActionListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = items.get(position);

        holder.tvNombreCart.setText(item.nombre);
        holder.tvCategoriaCart.setText("Categoría: " + item.categoria);
        holder.tvPrecioCart.setText(String.format(Locale.getDefault(), "Precio unitario: $ %.2f", item.precio));
        holder.tvCantidadCart.setText("Cantidad: " + item.cantidad);
        holder.tvSubtotalCart.setText(String.format(Locale.getDefault(), "Subtotal: $ %.2f", item.getSubtotal()));

        holder.btnAumentar.setOnClickListener(v -> {
            if (listener != null) listener.onAumentar(item);
        });

        holder.btnDisminuir.setOnClickListener(v -> {
            if (listener != null) listener.onDisminuir(item);
        });

        holder.btnEliminar.setOnClickListener(v -> {
            if (listener != null) listener.onEliminar(item);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {

        TextView tvNombreCart, tvCategoriaCart, tvPrecioCart, tvCantidadCart, tvSubtotalCart;
        Button btnAumentar, btnDisminuir, btnEliminar;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombreCart = itemView.findViewById(R.id.tvNombreCart);
            tvCategoriaCart = itemView.findViewById(R.id.tvCategoriaCart);
            tvPrecioCart = itemView.findViewById(R.id.tvPrecioCart);
            tvCantidadCart = itemView.findViewById(R.id.tvCantidadCart);
            tvSubtotalCart = itemView.findViewById(R.id.tvSubtotalCart);

            btnAumentar = itemView.findViewById(R.id.btnAumentar);
            btnDisminuir = itemView.findViewById(R.id.btnDisminuir);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);
        }
    }
}