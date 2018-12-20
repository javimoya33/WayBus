package com.example.acer.waybus.tools;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.acer.waybus.Modelo.Favoritos;
import com.example.acer.waybus.Modelo.Ruta;
import com.example.acer.waybus.Modelo.Usuario;
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
 * Created by Acer on 19/05/2017.
 */
public class ObtenerIdUsuario {

    /**
     * Etiqueta de depuración
     */
    private static final String TAG = ObtenerIdUsuario.class.getSimpleName();

    private List<Usuario> itemsUsuario;
    private List<Ruta> itemsRuta;
    private List<Favoritos> itemsFavorito;

    private Gson gson = new Gson();

    private String idMovil;

    public int posicion = -1;

    public void cargarUsuario(String idMovil, final int[] favs, final Context context, final ContentResolver contentResolver,
                               final Activity activity)
    {

        String urlUser = Constantes.GET_USUARIO + "?idMovil=" + idMovil;
        System.out.println("Fresa " + urlUser);

        // Petición GET
        final String finalIdMovil = idMovil;

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
                                            String estado = response.getString("estado");

                                            switch (estado)
                                            {
                                                case "1": // ÉXITO
                                                    // Obtener array 'usuarios' Json
                                                    JSONArray mensaje = response.getJSONArray("usuario");

                                                    // Parsear con Gson
                                                    Usuario[] usuarios = gson.fromJson(mensaje.toString(), Usuario[].class);

                                                    itemsUsuario = Arrays.asList(usuarios);

                                                    Toast.makeText(
                                                            context,
                                                            "Se encontró al usuario" + itemsUsuario.get(0).getIdUsuario(),
                                                            Toast.LENGTH_SHORT).show();
                                                    cargarFavoritos(context, favs,
                                                            itemsUsuario.get(0).getIdUsuario());

                                                    break;

                                                case "2": // FALLIDO

                                                    guardarUsuario(finalIdMovil, context, activity);
                                                    Toast.makeText(
                                                            context,
                                                            "El usuario ha sido guardado",
                                                            Toast.LENGTH_SHORT).show();
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

                                        Log.d(TAG, "Error Volley: " + error.toString());
                                    }
                                }
                        )
                );
    }

    public void cargarFavoritos(final Context context, final int[] posFavs, int idUser)
    {
        String newUrl = Constantes.GET_FAV + "?Favoritos_idUsuarios=" + idUser;
        System.out.println("No puedo " + newUrl);

        // Petición GET
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

                                        procesarRespuestaFavorito(response, posFavs, context);
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

    public void procesarRespuestaFavorito(JSONObject response, int[] posFavs, Context context)
    {
        try
        {
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

                    for (int i = 0; i < itemsRuta.size(); i++)
                    {
                        for (int j = 0; j < itemsFavorito.size(); j++)
                        {
                            System.out.println("Peter " + itemsRuta.get(i).getIdRuta() + " = "
                                    + itemsFavorito.get(j).getFavoritos_idRutas());
                            if (itemsRuta.get(i).getIdRuta() == itemsFavorito.get(j).getFavoritos_idRutas())
                            {
                                posFavs[i] = itemsFavorito.get(j).getFavoritos_idRutas();
                                System.out.println("Silverrrr " + posFavs[i]);
                            }
                        }
                    }

                case "2": // FALLIDO
                    String mensaje3 = response.getString("estado");
                    Toast.makeText(
                            context,
                            mensaje3,
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    public void guardarUsuario(String idMovil, final Context context, final Activity activity)
    {
        HashMap<String, String> map = new HashMap<>(); // Mapeo previo

        map.put("idMovil", idMovil);

        // Crear nuevo objeto Json basado en el mapa
        JSONObject jsonObject = new JSONObject(map);

        // Depurando objeto Json...
        Log.d(TAG, jsonObject.toString());

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

                                Log.d(TAG, "Error Volley: " + error.getMessage());
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

    private void procesarRespuestaInsercion(JSONObject response, Context context, Activity activity)
    {
        try {
            // Obtener estado
            String estado = response.getString("estado");

            // Obtener mensaje
            String mensaje = response.getString("mensaje");

            System.out.println("Vainilla" + mensaje);

            switch (estado)
            {
                case "1":
                    Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show();
                    // Enviar código de éxito
                    activity.setResult(Activity.RESULT_OK);
                    // Terminar actividad
                    activity.finish();;
                    break;

                case "2":
                    // Mostrar mensaje
                    Toast.makeText(
                            activity,
                            mensaje,
                            Toast.LENGTH_LONG).show();
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
