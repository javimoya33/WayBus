package com.example.acer.waybus.Adaptadores;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.acer.waybus.Rutas.Adaptador_Rutas;
import com.example.acer.waybus.Modelo.Estacion;
import com.example.acer.waybus.Modelo.Horario;
import com.example.acer.waybus.Modelo.Linea;
import com.example.acer.waybus.Modelo.Ruta;
import com.example.acer.waybus.Modelo.Usuario;
import com.example.acer.waybus.tools.Constantes;
import com.example.acer.waybus.web.VolleySingleton;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

/**
 * Clase contenedora de los meétodos para la carga de rutas
 */
public class cargarRutas {

    /* Etiqueta de depuración */
    private static final String TAG = cargarUsuarioFavoritos.class.getSimpleName();

    /*A daptador del recycler view */
    private Adaptador_Rutas adapter;

    private Gson gson = new Gson();

    /**
     * Método que devolverá la lista de rutas que se inician en la población más cercana a la localización del usuario.
     *
     * @param context -> Contexto de la aplicación
     * @param lista -> lista de rutas correspondiente al recyclerview
     * @param rutas_favs -> Array que alberga la posición de las rutas favoritas
     * @param idMovil -> id del dispositivo móvil del usuario
     * @param origen -> Variable correspondiente al origen de la ruta
     * @param activity -> Actividad de la aplicación
     */
    public void cargarAdaptadorMiZona(final Context context, final RecyclerView lista, final int[] rutas_favs,
                                      String idMovil, String origen, final Activity activity)
    {
        // URL con la consulta a la base de datos
        String newUrl = Constantes.GET_RUTAS_MIZONA + "?origen=" + origen + "&idMovil=" + idMovil;

        // Petición GET que devolverán la lista de rutas más cercanas
        VolleySingleton.
                getInstance(context).
                addToRequestQueue(
                        new JsonObjectRequest(
                                Request.Method.GET,
                                newUrl,
                                (String) null,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {

                                        procesarRespuesta(response, context, lista, rutas_favs, activity);
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.d(TAG, "Error Volley: " + error.toString());
                                    }
                                }
                        )
                );
    }

    /**
     * Método que devolverá la lista total de rutas contenidas en la aplicación
     *
     * @param context -> Contexto de la aplicación
     * @param lista -> lista de rutas correspondiente al recyclerview
     * @param rutas_favs -> Array que alberga la posición de las rutas favoritas
     * @param idMovil -> id del dispositivo móvil del usuario
     * @param origen -> Variable correspondiente al origen de la ruta
     * @param activity -> Actividad de la aplicación
     */
    public void cargarAdaptadorLineas(final Context context, final RecyclerView lista, final int[] rutas_favs,
                                      String idMovil, String origen, final Activity activity)
    {
        // URL con la consulta a la base de datos
        String newUrl = Constantes.GET_RUTAS + "?idMovil=" + idMovil;

        // Petición GET que devolverán la lista total de rutas
        VolleySingleton.
                getInstance(context).
                addToRequestQueue(
                        new JsonObjectRequest(
                                Request.Method.GET,
                                newUrl,
                                (String) null,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {

                                        procesarRespuesta(response, context, lista, rutas_favs, activity);
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.d(TAG, "Error Volley: " + error.toString());
                                    }
                                }
                        )
                );
    }

    /**
     *
     * @param response -> Objeto JSON que devuelve la respuesta a la consulta a la base de datos.
     * @param context -> Contexto de la aplicación
     * @param lista -> lista de rutas correspondiente al recyclerview
     * @param rutas_favs -> Array que alberga la posición de las rutas favoritas
     * @param activity -> Actividad de la aplicación
     */
    private void procesarRespuesta(JSONObject response, Context context, RecyclerView lista, int[] rutas_favs,
                                   Activity activity)
    {
        try
        {
            // En función de si la consulta devuelve algún valor o no se mostrarán o no la lista con las rutas
            String estado = response.getString("estado");
            switch (estado)
            {
                case "1": // ÉXITO

                    // Obtener array "metas" Json
                    JSONArray mensaje = response.getJSONArray("rutas");
                    // Parsear con Gson
                    Ruta[] rutas = gson.fromJson(mensaje.toString(), Ruta[].class);
                    Horario[] horarios = gson.fromJson(mensaje.toString(), Horario[].class);
                    Linea[] lineas = gson.fromJson(mensaje.toString(), Linea[].class);
                    Estacion[] estaciones = gson.fromJson(mensaje.toString(), Estacion[].class);

                    JSONArray mensaje2 = response.getJSONArray("usuario");
                    Usuario[] usuarios = gson.fromJson(mensaje2.toString(), Usuario[].class);

                    // Inicializar adaptador
                    adapter = new Adaptador_Rutas(Arrays.asList(rutas), Arrays.asList(horarios),
                            Arrays.asList(lineas), Arrays.asList(estaciones), Arrays.asList(usuarios),
                            context, rutas_favs, activity);
                    // Setear adaptador a la lista
                    lista.setAdapter(adapter);
                    break;

                case "2": // FALLIDO
                    String mensaje3 = response.getString("estado");
                    Toast.makeText(
                            context,
                            "No hay rutas por mostrar",
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }
}
