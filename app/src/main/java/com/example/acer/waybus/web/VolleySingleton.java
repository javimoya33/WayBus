package com.example.acer.waybus.web;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Clase que representa un cliente HTTP Volley
 */
public class VolleySingleton {

    // Atributos
    private static VolleySingleton singleton;
    private RequestQueue requestQueue;
    private static Context context;

    private VolleySingleton(Context context)
    {
        VolleySingleton.context = context;
    }

    /**
     * Retorna la instancia única del Singleton
     * @param context contexto donde se ejecutarán las peticiones
     * @return Instancia
     */
    public static synchronized VolleySingleton getInstance(Context context)
    {
        if (singleton == null)
        {
            singleton = new VolleySingleton(context.getApplicationContext());
        }
        return singleton;
    }

    /**
     * Obtiene la instancia de la cola de peticiones
     * @return cola de peticiones
     */
    public RequestQueue getRequestQueue()
    {
        if (this.requestQueue == null)
        {
            this.requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return requestQueue;
    }

    /**
     * Añade la petición a la cola
     * @param request petición
     * @param <T> Resultado final de tipo T
     */
    public <T> void addToRequestQueue(Request<T> request)
    {
        getRequestQueue().add(request);
    }
}
