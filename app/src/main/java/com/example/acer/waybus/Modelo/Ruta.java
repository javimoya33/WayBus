package com.example.acer.waybus.Modelo;

import com.bignerdranch.expandablerecyclerview.Model.ParentObject;

import java.util.List;

/**
 * Módelo de la tabla 'Ruta'
 */
public class Ruta implements ParentObject{

    /**
     * Atributos
     */
    private int idRuta;
    private String Origen;
    private String Destino;
    private int Favorito;
    private Double LatitudOrigen;
    private Double LongitudOrigen;

    private List<Object> childHorarios;

    public Ruta(int idRuta, String origen, String destino, int favorito, double latitudOrigen, double longitudOrigen)
    {
        this.idRuta = idRuta;
        this.Origen = origen;
        this.Destino = destino;
        this.Favorito = favorito;
        this.LatitudOrigen = latitudOrigen;
        this.LongitudOrigen = longitudOrigen;
    }

    public int getIdRuta()
    {

        return idRuta;
    }

    public String getOrigen()
    {
        return Origen;
    }

    public String getDestino()
    {
        return Destino;
    }

    public int getFavorito()
    {
        return Favorito;
    }

    public double getLatitudOrigen() { return LatitudOrigen; }

    public double getLongitudOrigen() { return  LongitudOrigen; }

    @Override
    public List<Object> getChildObjectList()
    {
        return childHorarios;
    }

    @Override
    public void setChildObjectList(List<Object> list)
    {
        childHorarios = list;
    }
}
