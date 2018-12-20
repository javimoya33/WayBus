package com.example.acer.waybus.Adaptadores;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.acer.waybus.Modelo.Ruta;
import com.example.acer.waybus.tools.Constantes;
import com.example.acer.waybus.web.VolleySingleton;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

/**
 * Clase contenedora de los métodos para la carga de rutas
 */
public class cargaRutas {

    private Gson gson = new Gson();

    private cargarUsuarioFavoritos cUsuarios = new cargarUsuarioFavoritos();

    /**
     *
     * @param context -> Contexto de la aplicación
     * @param itemsRuta -> Lista con las rutas a devolver
     * @param lat -> latitud de la posición actual del usuario
     * @param lon -> longitud de la posición actual del usuario
     * @param finalDifTotal -> Distancia entre las coordenadas de la posición de la estación de origen de las rutas
     *                      y la posición actual del usuario
     * @param origen -> Etiqueta de la zona del usuario
     * @param idMovil -> id del dispositivo móvil del usuario
     * @param lista -> lista de rutas
     * @param tvFavs -> Array con la posición de las rutas favoritas
     * @param activity -> Actividad de la aplicación
     * @param TAG -> Etiqueta de depuración
     */
    public void cargarLatLng(final Context context, final List<Ruta> itemsRuta, final Double lat, final Double lon,
                             final Double finalDifTotal, final TextView origen, final String idMovil, final RecyclerView lista,
                             final int[] tvFavs, final Activity activity, final String TAG)
    {
        String newUrl = Constantes.GET_LATLNG_ORIGEN;

        // Petición GET que devolverán la lista de rutas de una zona
        VolleySingleton.getInstance(context)
                .addToRequestQueue(
                        new JsonObjectRequest(
                                Request.Method.GET,
                                newUrl,
                                (String) null,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {

                                        procesarLatLngOrigen(response, context, itemsRuta, lat, lon, finalDifTotal, origen,
                                                idMovil, lista, tvFavs, activity, TAG);
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
     * Método que devolverá el titulo con el nombre de la zona actual del usuario y una lista de rutas
     * que se inician en esa zona
     *
     * @param context -> Contexto de la aplicación
     * @param itemsRuta -> Lista con las rutas a devolver
     * @param latitud -> latitud de la posición actual del usuario
     * @param longitud -> longitud de la posición actual del usuario
     * @param finalDifTotal -> Distancia entre las coordenadas de la posición de la estación de origen de las rutas
     *                      y la posición actual del usuario
     * @param origen -> Etiqueta de la zona del usuario
     * @param idMovil -> id del dispositivo móvil del usuario
     * @param lista -> lista de rutas
     * @param tvFavs -> Array con la posición de las rutas favoritas
     * @param activity -> Actividad de la aplicación
     * @param TAG -> Etiqueta de depuración
     */
    private void procesarLatLngOrigen(JSONObject response, Context context, List<Ruta> itemsRuta,
                                      Double latitud, Double longitud, Double finalDifTotal, TextView origen,
                                      String idMovil, final RecyclerView lista, final int[] tvFavs, Activity activity,
                                      String TAG)
    {
        try
        {
            String estado = response.getString("estado");

            switch (estado)
            {
                case "1": // ÉXITO
                    JSONArray mensaje = response.getJSONArray("latlng");

                    Ruta[] rutas = gson.fromJson(mensaje.toString(), Ruta[].class);

                    itemsRuta = Arrays.asList(rutas);

                    Double difLatitud, difLongitud;
                    Double difTotal;

                    for (int i = 0; i < itemsRuta.size(); i++)
                    {
                        if (itemsRuta.get(i).getLatitudOrigen() >= latitud)
                        {
                            difLatitud = itemsRuta.get(i).getLatitudOrigen() - latitud;
                        }
                        else
                        {
                            difLatitud = latitud - itemsRuta.get(i).getLatitudOrigen();
                        }

                        if (itemsRuta.get(i).getLongitudOrigen() >= longitud)
                        {
                            difLongitud = itemsRuta.get(i).getLongitudOrigen() - longitud;
                        }
                        else
                        {
                            difLongitud = longitud - itemsRuta.get(i).getLongitudOrigen();
                        }

                        difTotal = difLatitud + difLongitud;
                        if (difTotal < finalDifTotal)
                        {
                            finalDifTotal = difTotal;
                            origen.setText(itemsRuta.get(i).getOrigen());
                        }
                    }

                    cUsuarios.cargarUsuarioFavoritos(idMovil, "Huelva", context, lista, tvFavs, activity, TAG);

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
