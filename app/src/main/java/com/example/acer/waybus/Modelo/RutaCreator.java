package com.example.acer.waybus.Modelo;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Acer on 01/05/2017.
 */
public class RutaCreator {

    static RutaCreator rutaCreator;
    List<Ruta> parentViewHolders;

    public RutaCreator(Context context)
    {
        parentViewHolders = new ArrayList<>();
    }

    public static RutaCreator get(Context context)
    {
        if (rutaCreator == null)
        {
            rutaCreator = new RutaCreator(context);
        }
        return rutaCreator;
    }

    public List<Ruta> getAll()
    {
        return parentViewHolders;
    }
}
