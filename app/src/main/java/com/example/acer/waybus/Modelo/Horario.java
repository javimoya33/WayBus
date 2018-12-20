package com.example.acer.waybus.Modelo;

import java.util.ArrayList;

/**
 * Módelo de la tabla 'Horario'
 */
public class Horario{

    /**
     * Atributos
     */
    private int idHorario;
    private String HoraSalida;
    private String HoraLlegada;
    private String Duracion;
    private String Precio;
    private String DiasSemana;
    private int Horarios_idRutas;

    public Horario(int idHorario, String horaSalida, String horaLlegada,
                   String duracion, String precio, String diasSemana, int horarios_idRutas)
    {
        this.idHorario = idHorario;
        this.HoraSalida = horaSalida;
        this.HoraLlegada = horaLlegada;
        this.Duracion = duracion;
        this.Precio = precio;
        this.DiasSemana = diasSemana;
        this.Horarios_idRutas = horarios_idRutas;
    }

    public int getIdHorario()
    {
        return idHorario;
    }

    public String getHoraSalida()
    {
        return HoraSalida;
    }

    public String getHoraLlegada()
    {
        return HoraLlegada;
    }

    public String getDuracion(){
        return Duracion;
    }

    public String getPrecio()
    {
        return Precio;
    }

    public String getDiasSemana()
    {
        return DiasSemana;
    }

    public int getHorarios_idRutas() { return Horarios_idRutas; }
}
