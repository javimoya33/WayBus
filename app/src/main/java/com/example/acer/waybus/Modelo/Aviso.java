package com.example.acer.waybus.Modelo;

/**
 * Módelo de la tabla 'Avisos'
 */
public class Aviso {

    /**
     * Atributos
     */
    private int idAvisos;
    private String Repeticion;
    private String Notificacion;
    private int Avisos_idRutas;
    private int Avisos_idUsuarios;

    public Aviso(int idaviso, String repeticion, String notificacion, int avisos_idRutas, int avisos_idUsuarios)
    {
        this.idAvisos = idaviso;
        this.Repeticion = repeticion;
        this.Notificacion = notificacion;
        this.Avisos_idRutas = avisos_idRutas;
        this.Avisos_idUsuarios = avisos_idUsuarios;
    }

    public int getIdAviso()
    {
        return idAvisos;
    }

    public String getRepeticion()
    {
        return Repeticion;
    }

    public String getNotificacion()
    {
        return Notificacion;
    }

    public int getAvisos_idRutas()
    {
        return Avisos_idRutas;
    }

    public int getAvisos_idUsuarios()
    {
        return Avisos_idUsuarios;
    }
}
