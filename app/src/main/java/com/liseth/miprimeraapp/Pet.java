package com.liseth.miprimeraapp;

public class Pet {
    public String nombre;
    public String fechaNacimiento; // dd/mm/aaaa
    public String edadTexto;       // "2 años, 3 meses"
    public String raza;
    public String caracteristicas;
    public String vacunas;
    public String historial;

    public Pet(String nombre, String fechaNacimiento, String edadTexto, String raza,
               String caracteristicas, String vacunas, String historial) {
        this.nombre = nombre;
        this.fechaNacimiento = fechaNacimiento;
        this.edadTexto = edadTexto;
        this.raza = raza;
        this.caracteristicas = caracteristicas;
        this.vacunas = vacunas;
        this.historial = historial;
    }
}