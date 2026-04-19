package com.liseth.miprimeraapp;

public class Pet {

    // Aquí guardo toda la información principal de la mascota
    public String nombre;
    public String fechaNacimiento;
    public String edadTexto;
    public String raza;
    public String caracteristicas;
    public String vacunas;
    public String historial;

    // Nuevos campos del perfil completo
    public String sexo;
    public String peso;
    public String color;
    public String alergias;
    public String observaciones;

    // Aquí guardo la URI de la imagen de la mascota
    public String imagenUri;

    // Constructor completo de la mascota
    public Pet(String nombre,
               String fechaNacimiento,
               String edadTexto,
               String raza,
               String caracteristicas,
               String vacunas,
               String historial,
               String sexo,
               String peso,
               String color,
               String alergias,
               String observaciones,
               String imagenUri) {

        this.nombre = nombre;
        this.fechaNacimiento = fechaNacimiento;
        this.edadTexto = edadTexto;
        this.raza = raza;
        this.caracteristicas = caracteristicas;
        this.vacunas = vacunas;
        this.historial = historial;
        this.sexo = sexo;
        this.peso = peso;
        this.color = color;
        this.alergias = alergias;
        this.observaciones = observaciones;
        this.imagenUri = imagenUri;
    }
}