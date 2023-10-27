package com.mk.wayra.model;

public class Provincia {
    private String nombre;
    private int id;

    public Provincia(String nombre, int id) {
        this.nombre = nombre;
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
