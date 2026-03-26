package com.liseth.miprimeraapp;

public class Order {
    public String numeroPedido;
    public String cliente;
    public String direccion;
    public String metodoPago;
    public String detalleProductos;
    public String fecha;
    public double total;

    public Order(String numeroPedido, String cliente, String direccion, String metodoPago,
                 String detalleProductos, String fecha, double total) {
        this.numeroPedido = numeroPedido;
        this.cliente = cliente;
        this.direccion = direccion;
        this.metodoPago = metodoPago;
        this.detalleProductos = detalleProductos;
        this.fecha = fecha;
        this.total = total;
    }
}