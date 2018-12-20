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
import com.example.acer.waybus.Rutas.Adaptador_TableHorarios;
import com.example.acer.waybus.Modelo.Horario;
import com.example.acer.waybus.Modelo.Usuario;
import com.example.acer.waybus.tools.Constantes;
import com.example.acer.waybus.web.VolleySingleton;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

/**
 * Clase contenedora de los métodos para la carga de horarios
 */
public class cargaHorarios {

    /* Adaptador del recycler view */
    public Adaptador_TableHorarios adapterTable;

    public Gson gson = new Gson();

    /**
     * Método encargado de cargar una lista de horarios asociado a una ruta concreta
     *
     * @param idRuta -> id de la ruta asociada a la lista de horarios a devolver
     * @param idMovil -> id del dispositivo móvil de usuario
     * @param recyclerHorarios -> Contenedor de la lista de horarios
     * @param context -> Contexto de la aplicación
     * @param TAG -> Etiqueta de depuración
     * @param activity -> Actividad de la aplicación
     */
    public void cargarHorarios(String idRuta, String idMovil, final RecyclerView recyclerHorarios,
                               final Context context, final String TAG, final Activity activity)
    {
        // URL con la consulta a la base de datos
        String newUrl = Constantes.GET_HORARIOS_BY_RUTA + "?Horarios_idRutas=" + idRuta + "&idMovil=" + idMovil;

        // Petición GET que devolverán la lista de horarios de la ruta
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

                                        procesarRespuestaHorarios(response, recyclerHorarios, context, activity);
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
     * Método encargado de procesar los datos recibidos en la consulta a la abse de datos
     *
     * @param response -> Objeto JSON que devuelve la respuesta a la consulta a la base de datos.
     * @param recyclerHorarios -> Contenedor de la lista de horarios
     * @param context -> Contexto de la aplicación
     * @param activity -> Actividad de la aplicación
     */
    private void procesarRespuestaHorarios(JSONObject response, RecyclerView recyclerHorarios, Context context,
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
                    JSONArray mensaje = response.getJSONArray("horarios");
                    JSONArray mensaje2 = response.getJSONArray("usuario");
                    // Parsear con Gson
                    Horario[] horarios = gson.fromJson(mensaje.toString(), Horario[].class);
                    Usuario[] usuarios = gson.fromJson(mensaje2.toString(), Usuario[].class);

                    // Inicializar adaptador
                    adapterTable = new Adaptador_TableHorarios(Arrays.asList(horarios),
                            Arrays.asList(usuarios), context, activity);
                    // Setear adaptador a la lista
                    recyclerHorarios.setAdapter(adapterTable);
                    break;

                case "2": // FALLIDO
                    String mensaje3 = response.getString("estado");
                    Toast.makeText(
                            context,
                            "No hay horarios por mostrar en esta ruta",
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
