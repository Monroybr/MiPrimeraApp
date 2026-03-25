package com.liseth.miprimeraapp;

public class Historial {
    public int petIndex;
    public int globalIndex; // índice dentro del historial_json
    public String fechaRegistro;
    public String enfermedades;
    public String procedimientos;
    public String medicacion;

    public Historial(int petIndex, int globalIndex, String fechaRegistro, String enfermedades, String procedimientos, String medicacion) {
        this.petIndex = petIndex;
        this.globalIndex = globalIndex;
        this.fechaRegistro = fechaRegistro;
        this.enfermedades = enfermedades;
        this.procedimientos = procedimientos;
        this.medicacion = medicacion;
    }
}