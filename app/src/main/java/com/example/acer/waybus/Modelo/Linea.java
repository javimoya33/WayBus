package com.example.acer.waybus.Modelo;

import com.android.volley.Request;

/**
 * Módelo de la tabla 'Linea'
 */
public class Linea {

    /**
     * Atributos
     */
    private String idLinea;
    private String numBus;
    private String Capacidad;
    private String Modelo;
    private String Lineas_idEstaciones;

    public Linea(String idLinea, String numbus, String capacidad, String modelo, String lineas_idEstaciones)
    {
        this.idLinea = idLinea;
        this.numBus = numbus;
        this.Capacidad = capacidad;
        this.Modelo = modelo;
        this.Lineas_idEstaciones = lineas_idEstaciones;
    }

    public String getIdLinea() { return idLinea; }

    public String getNumBus() { return numBus; }

    public String getCapacidad() { return Capacidad; }

    public String getModelo() { return Modelo; }

    public String getLineas_idEstaciones() { return Lineas_idEstaciones; }
}
