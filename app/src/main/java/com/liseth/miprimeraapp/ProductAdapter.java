package com.liseth.miprimeraapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    // Aquí creo una interfaz para manejar la acción de agregar productos al carrito
    public interface OnAddToCartListener {
        void onAddToCart(Product product);
    }

    private final List<Product> products;
    private final OnAddToCartListener listener;

    // Constructor del adaptador
    public ProductAdapter(List<Product> products, OnAddToCartListener listener) {
        this.products = products;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Aquí inflo el diseño de cada tarjeta de producto
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        // Obtengo el producto actual según la posición
        Product product = products.get(position);

        // Muestro los datos del producto
        holder.tvNombreProducto.setText(product.nombre);
        holder.tvCategoriaProducto.setText("Categoría: " + product.categoria);
        holder.tvPrecioProducto.setText(String.format(Locale.getDefault(), "$ %.2f", product.precio));

        // Botón para agregar al carrito
        holder.btnAgregarCarrito.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAddToCart(product);
                Toast.makeText(v.getContext(), product.nombre + " agregado al carrito", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    // ViewHolder para cada producto
    static class ProductViewHolder extends RecyclerView.ViewHolder {

        TextView tvNombreProducto, tvCategoriaProducto, tvPrecioProducto;
        Button btnAgregarCarrito;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);

            tvNombreProducto = itemView.findViewById(R.id.tvNombreProducto);
            tvCategoriaProducto = itemView.findViewById(R.id.tvCategoriaProducto);
            tvPrecioProducto = itemView.findViewById(R.id.tvPrecioProducto);
            btnAgregarCarrito = itemView.findViewById(R.id.btnAgregarCarrito);
        }
    }
}