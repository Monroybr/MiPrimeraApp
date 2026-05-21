package com.liseth.miprimeraapp;

public class Vacuna {

    // Aquí guardo el id real de la vacuna en SQLite
    public int id;

    // Aquí guardo el id real de la mascota en SQLite
    public int mascotaId;

    public String vacuna;
    public String fechaAplicacion;
    public String lugar;
    public String proximaDosis;

    public Vacuna(int id, int mascotaId, String vacuna, String fechaAplicacion, String lugar, String proximaDosis) {
        this.id = id;
        this.mascotaId = mascotaId;
        this.vacuna = vacuna;
        this.fechaAplicacion = fechaAplicacion;
        this.lugar = lugar;
        this.proximaDosis = proximaDosis;
    }
}