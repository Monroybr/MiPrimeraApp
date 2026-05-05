package com.liseth.miprimeraapp;

public class Pet {

    // Aquí guardo el id real de la mascota en SQLite
    public int id;

    // Aquí guardo la información principal de la mascota
    public String nombre;
    public String fechaNacimiento;
    public String edadTexto;
    public String raza;
    public String caracteristicas;
    public String vacunas;
    public String historial;

    // Aquí guardo los datos adicionales del perfil completo
    public String sexo;
    public String peso;
    public String color;
    public String alergias;
    public String observaciones;

    // Aquí guardo la ruta o URI de la imagen de la mascota
    public String imagenUri;

    // Constructor completo para crear objetos de tipo Pet desde SQLite
    public Pet(int id,
               String nombre,
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

        this.id = id;
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