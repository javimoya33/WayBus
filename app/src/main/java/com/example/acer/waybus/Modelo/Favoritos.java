package com.example.acer.waybus.Modelo;

/**
 * Módelo de la tabla 'Favoritos'
 */
public class Favoritos {

    /**
     * Atributos
     */
    private int idFavorito;
    private String NomFav;
    private int Favoritos_idRutas;
    private int Favoritos_idUsuarios;

    public Favoritos(int idfavorito, String nombre, int favoritos_idRutas, int favoritos_idUsuarios)
    {
        this.idFavorito = idfavorito;
        this.NomFav = nombre;
        this.Favoritos_idRutas = favoritos_idRutas;
        this.Favoritos_idUsuarios = favoritos_idUsuarios;
    }

    public int getIdFavorito() { return idFavorito; }

    public String getNomFav() { return NomFav; }

    public int getFavoritos_idRutas() { return Favoritos_idRutas; }

    public int getFavoritos_idUsuarios() { return Favoritos_idUsuarios; }
}
