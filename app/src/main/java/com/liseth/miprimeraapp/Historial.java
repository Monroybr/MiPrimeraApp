package com.liseth.miprimeraapp;

public class Historial {

    // Aquí guardo el id real del historial en SQLite
    public int id;

    // Aquí guardo el id real de la mascota en SQLite
    public int mascotaId;

    public String fechaRegistro;
    public String enfermedades;
    public String procedimientos;
    public String medicacion;
    public String observaciones;

    public Historial(int id, int mascotaId, String fechaRegistro,
                     String enfermedades, String procedimientos,
                     String medicacion, String observaciones) {

        this.id = id;
        this.mascotaId = mascotaId;
        this.fechaRegistro = fechaRegistro;
        this.enfermedades = enfermedades;
        this.procedimientos = procedimientos;
        this.medicacion = medicacion;
        this.observaciones = observaciones;
    }
}