package com.liseth.miprimeraapp;

public class CartItem {
    // Aquí guardo la información básica de cada producto agregado al carrito
    public String nombre;
    public String categoria;
    public double precio;
    public int cantidad;

    // Constructor para crear un producto dentro del carrito
    public CartItem(String nombre, String categoria, double precio, int cantidad) {
        this.nombre = nombre;
        this.categoria = categoria;
        this.precio = precio;
        this.cantidad = cantidad;
    }
    // Este metodo me permite calcular el subtotal del producto
    public double getSubtotal() {
        return precio * cantidad;
    }
}