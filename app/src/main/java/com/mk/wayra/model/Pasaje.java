package com.mk.wayra.model;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

public class Pasaje {
    private String primerNom;
    private String segundoNom;
    private String apePaterno;
    private String apeMaterno;
    private String numIdentidad;
    private String telefono;
    private String origen;
    private String destino;
    private Double precio;
    private String fecha;
    private String hora;

    public Pasaje(String primerNom, String apePaterno, String origen, String destino, String fecha, String hora) {
        this.primerNom = primerNom;
        this.apePaterno = apePaterno;
        this.origen = origen;
        this.destino = destino;
        this.fecha = fecha;
        this.hora = hora;
    }

    public String getPrimerNom() {
        return primerNom;
    }

    public void setPrimerNom(String primerNom) {
        this.primerNom = primerNom;
    }

    public String getSegundoNom() {
        return segundoNom;
    }

    public void setSegundoNom(String segundoNom) {
        this.segundoNom = segundoNom;
    }

    public String getApePaterno() {
        return apePaterno;
    }

    public void setApePaterno(String apePaterno) {
        this.apePaterno = apePaterno;
    }

    public String getApeMaterno() {
        return apeMaterno;
    }

    public void setApeMaterno(String apeMaterno) {
        this.apeMaterno = apeMaterno;
    }

    public String getNumIdentidad() {
        return numIdentidad;
    }

    public void setNumIdentidad(String numIdentidad) {
        this.numIdentidad = numIdentidad;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }
}
