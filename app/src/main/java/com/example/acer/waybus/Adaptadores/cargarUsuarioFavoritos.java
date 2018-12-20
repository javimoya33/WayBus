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
import com.example.acer.waybus.Modelo.Favoritos;
import com.example.acer.waybus.Modelo.Ruta;
import com.example.acer.waybus.Modelo.Usuario;
import com.example.acer.waybus.tools.Constantes;
import com.example.acer.waybus.web.VolleySingleton;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Clase contenedora de los métodos para la carga de usuarios y las rutas favoritas
 */
public class cargarUsuarioFavoritos {

    /* Etiqueta de depuración */
    private static final String tagCarga = cargarUsuarioFavoritos.class.getSimpleName();

    /*Lista de objetos {@link Ruta} que representa la fuente de datos de inflado */
    private List<Usuario> itemsUsuario;
    private List<Ruta> itemsRuta;
    private List<Favoritos> itemsFavorito;

    private Gson gson = new Gson();

    private cargarRutas cLineas = new cargarRutas();
    private cargarFavs cFavs = new cargarFavs();

    /**
     * Método que devolverá la lista de rutas favoritas.
     *
     * @param origen -> origen de la ruta
     * @param context -> Contexto de la aplicación
     * @param lista -> lista de rutas correspondiente al recyclerview
     * @param rutas_favs -> Array que alberga la posición de las rutas favoritas
     * @param idMovil -> id del dispositivo móvil del usuario
     * @param activity -> Actividad de la aplicación
     */
    public void cargarUsuarioFavoritos(final String idMovil, final String origen, final Context context,
                                       final RecyclerView lista, final int[] rutas_favs, final Activity activity,
                                       final String TAG)
    {
        // URL con la consulta a la base de datos
        String urlUser = Constantes.GET_USUARIO + "?idMovil=" + idMovil;

        final String finalIdMovil = idMovil;

        // Petición GET que devuelve los datos asociados a un usuario concreto
        VolleySingleton
                .getInstance(context)
                .addToRequestQueue(
                        new JsonObjectRequest(
                                Request.Method.GET,
                                urlUser,
                                (String) null,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {

                                        try
                                        {
                                            // En función de si la consulta devuelve algún valor o no se mostrarán o no el usuario a devolver
                                            String estado = response.getString("estado");
                                            switch (estado)
                                            {
                                                case "1": // ÉXITO
                                                    // Obtener array 'usuarios' Json
                                                    JSONArray mensaje = response.getJSONArray("usuario");
                                                    // Parsear con Gson
                                                    Usuario[] usuarios = gson.fromJson(mensaje.toString(), Usuario[].class);
                                                    itemsUsuario = Arrays.asList(usuarios);

                                                    // Al encontrar al usuario la aplicación cargará las rutas favoritas asociadas a su cuenta
                                                    cargarFavoritos(context, lista, rutas_favs,
                                                            itemsUsuario.get(0).getIdUsuario(), finalIdMovil, origen,
                                                            TAG, activity);

                                                    break;

                                                case "2": // FALLIDO - En caso de que la id del móvil del usuario no esté registrada en la base de
                                                          // datos la aplicación le creará automáticamente una cuenta.

                                                    guardarUsuario(finalIdMovil, context, activity);
                                                    Toast.makeText(
                                                            context,
                                                            "Bienvenido a WayBus",
                                                            Toast.LENGTH_SHORT).show();

                                                    // Tras cargar el usuario y recoger que valores de rutas favoritas tenía asociadas, en función
                                                    // del valor que devuelva la etiqueta de depuración la aplicación llamará a un
                                                    // cargador determinado.
                                                    switch (TAG)
                                                    {
                                                        case "Fragment_seccion1":
                                                            cLineas.cargarAdaptadorLineas(context, lista, rutas_favs,
                                                                    idMovil, origen, activity);
                                                            break;

                                                        case "Fragment_seccion2":
                                                            cLineas.cargarAdaptadorLineas(context, lista, rutas_favs,
                                                                    idMovil, origen, activity);
                                                            break;

                                                        case "Fragment_MisParadas":
                                                            cFavs.cargarFavs(context, lista, rutas_favs,
                                                                    idMovil, activity);
                                                            break;
                                                    }

                                                    break;
                                            }
                                        }
                                        catch (JSONException e)
                                        {
                                            e.printStackTrace();
                                        }
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {

                                        Log.d(tagCarga, "Error Volley: " + error.toString());
                                    }
                                }
                        )
                );
    }

    /**
     * Método que carga los valores que hacen referencia a las rutas marcadas como favoritas dentro de la cuenta
     * de un usuario.
     *
     * @param context -> Contexto de la ruta
     * @param lista -> Contenedor con la lista de valores que se transmitirán hasta el adaptador
     * @param rutas_favs -> Array que recoge las referencias de las rutas marcadas como favoritas
     * @param idUser -> id del usuario
     * @param idMovil -> id del dispositivo utilizado por el usuario
     * @param origen -> origen de la ruta
     * @param TAG -> Etiqueta de depuración
     * @param activity -> Activida de la aplicación
     */
    public void cargarFavoritos(final Context context, final RecyclerView lista, final int[] rutas_favs,
                                int idUser, final String idMovil, final String origen, final String TAG,
                                final Activity activity)
    {
        // URL con la consulta a la base de datos
        String newUrl = Constantes.GET_FAV + "?Favoritos_idUsuarios=" + idUser;

        // Petición GET que devuelve los datos asociados a una ruta favorita
        VolleySingleton
                .getInstance(context)
                .addToRequestQueue(
                        new JsonObjectRequest(
                                Request.Method.GET,
                                newUrl,
                                (String) null,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {

                                        procesarRespuestaFavorito(response, context, lista, rutas_favs, idMovil, origen,
                                                TAG, activity);
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
     * Método que procesa los datos que devuelve la consulta a la base de datos
     *
     * @param response -> Objeto JSON que devuelve la respuesta a la consulta a la base de datos.
     * @param context -> Contexto de la ruta
     * @param lista -> Contenedor con la lista de valores que se transmitirán hasta el adaptador
     * @param rutas_favs -> Array que recoge las referencias de las rutas marcadas como favoritas
     * @param idMovil -> id del dispositivo utilizado por el usuario
     * @param origen -> origen de la ruta
     * @param TAG -> Etiqueta de depuración
     * @param activity -> Activida de la aplicación
     */
    public void procesarRespuestaFavorito(JSONObject response, Context context, RecyclerView lista,
                                          int[] rutas_favs, String idMovil, String origen, String TAG, Activity activity)
    {
        try
        {
            // En función de si la consulta devuelve algún valor o no se mostrarán o no las
            // referencias de las rutas favoritas
            String estado = response.getString("estado");
            switch (estado)
            {
                case "1": // ÉXITO
                    // Obtener array 'usuarios' Json
                    JSONArray mensaje = response.getJSONArray("favoritas");
                    JSONArray mensaje2 = response.getJSONArray("rutas");

                    // Parsear con Gson
                    Favoritos[] favoritos = gson.fromJson(mensaje.toString(), Favoritos[].class);
                    Ruta[] rutas = gson.fromJson(mensaje2.toString(), Ruta[].class);

                    itemsRuta = Arrays.asList(rutas);
                    itemsFavorito = Arrays.asList(favoritos);

                    rutas_favs = new int[itemsRuta.size()];
                    for (int i = 0; i < itemsRuta.size(); i++)
                    {
                        for (int j = 0; j < itemsFavorito.size(); j++)
                        {
                            if (itemsRuta.get(i).getIdRuta() == itemsFavorito.get(j).getFavoritos_idRutas())
                            {
                                rutas_favs[i] = itemsFavorito.get(j).getFavoritos_idRutas();
                            }
                        }
                    }

                    switch (TAG)
                    {
                        case "Fragment_seccion1":
                            cLineas.cargarAdaptadorLineas(context, lista, rutas_favs, idMovil, origen, activity);
                            break;

                        case "Fragment_seccion2":
                            cLineas.cargarAdaptadorMiZona(context, lista, rutas_favs, idMovil, origen, activity);
                            break;

                        case "Fragment_MisParadas":
                            cFavs.cargarFavs(context, lista, rutas_favs, idMovil,  activity);
                            break;
                    }

                    break;

                case "2": // FALLIDO
                    String mensaje3 = response.getString("estado");

                    // Si no hay referencias de rutas marcadas como favoritas se cargará igualmente el adaptador de
                    // de la vista correspondiente
                    switch (TAG)
                    {

                        case "Fragment_seccion1":
                            cLineas.cargarAdaptadorLineas(context, lista, rutas_favs,
                                    idMovil, origen, activity);
                            break;

                        case "Fragment_seccion2":
                            cLineas.cargarAdaptadorLineas(context, lista, rutas_favs,
                                    idMovil, origen, activity);
                            break;

                        case "Fragment_MisParadas":

                            Toast.makeText(
                                    context,
                                    "No hay rutas marcadas como favoritas",
                                    Toast.LENGTH_SHORT).show();

                            cFavs.cargarFavs(context, lista, rutas_favs,
                                    idMovil, activity);
                            break;
                    }
                    break;
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Método encargado de guardar el id del móvil del usuario en caso de que aún no esté registrado en la
     * base de datos
     *
     * @param idMovil
     * @param context
     * @param activity
     */
    public void guardarUsuario(String idMovil, final Context context, final Activity activity)
    {
        // Mapeo encargado de insertar el id del dispositivo móvil del usuario que ejecuta la aplicación
        HashMap<String, String> map = new HashMap<>(); // Mapeo previo
        map.put("idMovil", idMovil);

        JSONObject jsonObject = new JSONObject(map);

        Log.d(tagCarga, jsonObject.toString());

        // Actualizar datos en el servidor
        VolleySingleton.getInstance(context).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.POST,
                        Constantes.INSERT_USUARIO,
                        jsonObject,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {

                                procesarRespuestaInsercion(response, context, activity);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                                Log.d(tagCarga, "Error Volley: " + error.getMessage());
                            }
                        }
                ) {
                    @Override
                    public Map<String, String> getHeaders(){

                        Map<String, String> headers = new HashMap<String, String>();
                        headers.put("Content-Type", "application/json; charset=utf-8");
                        headers.put("Accept", "application/json");
                        return headers;
                    }

                    @Override
                    public String getBodyContentType(){
                        return "application/json; charset=utf-8" + getParamsEncoding();
                    }
                }
        );
    }

    /**
     * Método que procesa la respuesta del método anterior
     *
     * @param response -> Objeto JSON que devuelve la respuesta a la consulta a la base de datos.
     * @param context -> Contexto de la aplicación
     * @param activity -> Actividad de la aplicación
     */
    public void procesarRespuestaInsercion(JSONObject response, Context context, Activity activity)
    {
        try {
            String estado = response.getString("estado");

            String mensaje = response.getString("mensaje");

            switch (estado)
            {
                case "1":
                    // Enviar código de éxito
                    activity.setResult(Activity.RESULT_OK);
                    // Terminar actividad
                    activity.finish();;
                    break;

                case "2":
                    // Enviar código de falla
                    activity.setResult(Activity.RESULT_CANCELED);
                    // Terminar actividad
                    activity.finish();
                    break;
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }
}
