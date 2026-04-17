package com.liseth.miprimeraapp;

public class Product {
    // Aquí defino los datos de cada producto de la tienda
    public String nombre;
    public String categoria;
    public double precio;

    // Constructor del producto
    public Product(String nombre, String categoria, double precio) {
        this.nombre = nombre;
        this.categoria = categoria;
        this.precio = precio;
    }
}