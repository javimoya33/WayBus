package com.example.acer.waybus.Adaptadores;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.acer.waybus.tools.Constantes;
import com.example.acer.waybus.web.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Clase contenedora de los métodos para la carga de eliminación de rutas favoritas
 */
public class insertarFavorito {

    /**
     * Método encargado de insertar la referencias a la ruta que indican que es favorita
     *
     * @param idRuta -> id de la ruta a marcar como favorita
     * @param idUsuario -> id del dispositivo movil del usuario
     * @param TAG -> Etiqueta de depuración
     * @param context -> Contexto de la aplicación
     * @param activity -> Actividad d ela aplicación
     */
    public void insertarFavorito(String nomFav, int idRuta, int idUsuario, final String TAG, final Context context,
                                 final Activity activity)
    {
        // Mapeo encargado de enviar la referencia de favorito a la ruta especificado por el usuario
        HashMap<String, Object> map = new HashMap<>();
        map.put("NomFav", nomFav);
        map.put("idRuta", idRuta);
        map.put("idUsuario", idUsuario);

        JSONObject jsonObject = new JSONObject(map);

        Log.d(TAG, jsonObject.toString());

        // Petición POST que insertará la referencia de la ruta marcada como favorita
        VolleySingleton.getInstance(context).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.POST,
                        Constantes.INSERT_RUTA_FAV,
                        jsonObject,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {

                                procesarRespuesta(response, context, activity);
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

    /**
     * Método encargado de actualizar la referencias del nombre de la ruta marcada como favorita
     *
     * @param nomFav -> nombre de la ruta marcada como favorita
     * @param TAG -> Etiqueta de depuración
     * @param context -> Contexto de la aplicación
     * @param activity -> Actividad d ela aplicación
     */
    public void actualizarNombreFav(String nomFav, int idFav, final String TAG, final Context context,
                                    final Activity activity)
    {
        // Mapeo encargado de actualizar la referencia al nombre de la ruta marcada como favorita
        HashMap<String, Object> map = new HashMap<>();
        map.put("NomFav", nomFav);
        map.put("idFavorito", idFav);

        JSONObject jsonObject = new JSONObject(map);

        // Petición POST que actualizará el nombre de la ruta marcada como favorita
        VolleySingleton.getInstance(context)
                .addToRequestQueue(
                        new JsonObjectRequest(
                                Request.Method.POST,
                                Constantes.UPDATE_NOMBRE_FAV,
                                jsonObject,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {

                                        procesarRespuesta(response, context, activity);
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
                            public Map<String, String> getHeaders()
                            {
                                Map<String, String> headers = new HashMap<>();
                                headers.put("Content-Type", "application/json; charset=utf-8");
                                headers.put("Accept", "application/json");
                                return headers;
                            }

                            @Override
                            public String getBodyContentType()
                            {
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
    public void procesarRespuesta(JSONObject response, Context context, Activity activity)
    {
        try {
            // Obtener estado
            String estado = response.getString("estado");

            // Obtener mensaje
            String mensaje = response.getString("mensaje");

            switch (estado)
            {
                case "1":
                    Toast.makeText(context, "especial", Toast.LENGTH_SHORT).show();
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
