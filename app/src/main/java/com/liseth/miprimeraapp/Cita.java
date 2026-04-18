package com.liseth.miprimeraapp;

public class Cita {

    // Aquí guardo la información básica de cada cita veterinaria
    public int petIndex;
    public String nombreMascota;
    public String fecha;
    public String hora;
    public String veterinaria;
    public String motivo;

    public Cita(int petIndex, String nombreMascota, String fecha, String hora, String veterinaria, String motivo) {
        this.petIndex = petIndex;
        this.nombreMascota = nombreMascota;
        this.fecha = fecha;
        this.hora = hora;
        this.veterinaria = veterinaria;
        this.motivo = motivo;
    }
}