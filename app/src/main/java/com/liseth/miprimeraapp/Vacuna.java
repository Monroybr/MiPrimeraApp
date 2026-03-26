package com.liseth.miprimeraapp;

public class Vacuna {
    public int petIndex;
    public String vacuna;
    public String fechaAplicacion;
    public String lugar;
    public String proximaDosis;

    public Vacuna(int petIndex, String vacuna, String fechaAplicacion, String lugar, String proximaDosis) {
        this.petIndex = petIndex;
        this.vacuna = vacuna;
        this.fechaAplicacion = fechaAplicacion;
        this.lugar = lugar;
        this.proximaDosis = proximaDosis;
    }
}