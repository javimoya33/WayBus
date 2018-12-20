package com.example.acer.waybus.Modelo;

/**
 * Módelo de la tabla 'Estacion'
 */
public class Estacion {

    /**
     * Atributos
     */
    private String idEstacion;
    private String Nombre;
    private String CP;
    private String Direccion;
    private String Longitud;
    private String Latitud;
    private String Telefono;
    private String Fax;
    private String Email;
    private String Web;

    public Estacion(String idEstacion, String nombre, String cp, String direccion, String longitud,
                    String latitud, String telefono, String fax, String email, String web)
    {
        this.idEstacion = idEstacion;
        this.Nombre = nombre;
        this.CP = cp;
        this.Direccion = direccion;
        this.Longitud = longitud;
        this.Latitud = latitud;
        this.Telefono = telefono;
        this.Fax = fax;
        this.Email = email;
        this.Web = web;
    }

    public String getIdEstacion(){ return idEstacion; }

    public String getNombre(){ return Nombre; }

    public String getCP(){ return CP; }

    public String getDireccion(){ return Direccion; }

    public String getLongitud(){ return Longitud; }

    public String getLatitud(){ return Latitud; }

    public String getTelefono(){ return Telefono; }

    public String getFax(){ return Fax; }

    public String getEmail(){ return Email; }

    public String getWeb(){ return Web; }
}
