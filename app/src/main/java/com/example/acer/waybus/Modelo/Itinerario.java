package com.example.acer.waybus.Modelo;

/**
 * Módelo de la tabla 'Itinerario'
 */
public class Itinerario {

    /**
     * Atributos
     */
    private int idItinerario;
    private Double Latitud;
    private Double Longitud;

    public Itinerario(int idItinerario, Double latitud, Double longitud)
    {
        this.idItinerario = idItinerario;
        this.Latitud = latitud;
        this.Longitud = longitud;
    }

    public int getIdItinerario()
    {
        return idItinerario;
    }

    public Double getLatitud()
    {
        return Latitud;
    }

    public Double getLongitud()
    {
        return Longitud;
    }

}
