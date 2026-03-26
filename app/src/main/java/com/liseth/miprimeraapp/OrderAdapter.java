package com.liseth.miprimeraapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private final List<Order> orders;

    public OrderAdapter(List<Order> orders) {
        this.orders = orders;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orders.get(position);

        holder.tvNumeroPedido.setText("Pedido " + order.numeroPedido);
        holder.tvFechaPedido.setText("Fecha: " + order.fecha);
        holder.tvClientePedido.setText("Cliente: " + order.cliente);
        holder.tvMetodoPagoPedido.setText("Pago: " + order.metodoPago);
        holder.tvTotalPedido.setText(String.format(Locale.getDefault(), "Total: $ %.2f", order.total));
        holder.tvDetallePedidoItem.setText(order.detalleProductos);
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvNumeroPedido, tvFechaPedido, tvClientePedido, tvMetodoPagoPedido, tvTotalPedido, tvDetallePedidoItem;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNumeroPedido = itemView.findViewById(R.id.tvNumeroPedido);
            tvFechaPedido = itemView.findViewById(R.id.tvFechaPedido);
            tvClientePedido = itemView.findViewById(R.id.tvClientePedido);
            tvMetodoPagoPedido = itemView.findViewById(R.id.tvMetodoPagoPedido);
            tvTotalPedido = itemView.findViewById(R.id.tvTotalPedido);
            tvDetallePedidoItem = itemView.findViewById(R.id.tvDetallePedidoItem);
        }
    }
}