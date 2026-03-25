package com.liseth.miprimeraapp;

public class CartItem {
    public String nombre;
    public String categoria;
    public double precio;
    public int cantidad;

    public CartItem(String nombre, String categoria, double precio, int cantidad) {
        this.nombre = nombre;
        this.categoria = categoria;
        this.precio = precio;
        this.cantidad = cantidad;
    }

    public double getSubtotal() {
        return precio * cantidad;
    }
}