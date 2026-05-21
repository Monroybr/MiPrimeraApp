package com.liseth.miprimeraapp;

public class Cita {

    // Aquí guardo el id real de la cita en SQLite
    public int id;

    // Aquí guardo el id real de la mascota
    public int mascotaId;

    public String nombreMascota;
    public String fecha;
    public String hora;
    public String veterinaria;
    public String motivo;

    public Cita(int id, int mascotaId, String nombreMascota,
                String fecha, String hora,
                String veterinaria, String motivo) {

        this.id = id;
        this.mascotaId = mascotaId;
        this.nombreMascota = nombreMascota;
        this.fecha = fecha;
        this.hora = hora;
        this.veterinaria = veterinaria;
        this.motivo = motivo;
    }
}