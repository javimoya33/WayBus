package com.example.acer.waybus.Modelo;

/**
 * Módelo de la tabla 'Usuarios'
 */
public class Usuario {

    /**
     * Atributos
     */
    private int idUsuario;
    private String idMovil;

    public Usuario(int idusuario, String idmovil)
    {
        this.idUsuario = idusuario;
        this.idMovil = idmovil;
    }

    public int getIdUsuario(){ return  idUsuario; }

    public String getIdMovil() { return idMovil; }
}
