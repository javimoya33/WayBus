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
public class eliminarFavorito {

    /**
     * Método encargado de eliminar las referencias a la rutas que indican que son favoritas
     *
     * @param idRuta -> id de la ruta a desmarcar como favorita
     * @param idUsuario -> id del dispositivo movil del usuario
     * @param TAG -> Etiqueta de depuración
     * @param context -> Contexto de la aplicación
     * @param activity -> Actividad d ela aplicación
     */
    public void eliminarFavorito(int idRuta, int idUsuario, final String TAG, final Context context, final Activity activity)
    {
        // Objeto que devuelve la referencia de la ruta a desmarcar como favorita y del usuario que lo desmarca
        HashMap<String, Integer> map = new HashMap<>();
        map.put("idRuta", idRuta);
        map.put("idUsuario", idUsuario);
        JSONObject jsonObject = new JSONObject(map);

        // Petición POST que eliminará la referencia de la ruta marcada como favorita
        VolleySingleton.getInstance(context).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.POST,
                        Constantes.DELETE_RUTA_FAV,
                        jsonObject,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {

                                procesarRespuestaEliminar(response, context, activity);
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
                    public Map<String, String> getHeaders() {
                        Map<String, String> headers = new HashMap<String, String>();
                        headers.put("Content-Type", "application/json; charset=utf-8");
                        headers.put("Accept", "application/json");
                        return headers;
                    }

                    @Override
                    public String getBodyContentType() {
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
    private void procesarRespuestaEliminar(JSONObject response, Context context, Activity activity)
    {
        try
        {
            String estado = response.getString("estado");
            String mensaje = response.getString("mensaje");

            switch (estado)
            {
                case "1":
                    // Mostrar mensaje
                    Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show();

                    // Enviar código de éxito
                    activity.setResult(Activity.RESULT_OK);
                    // Terminar actividad
                    activity.finish();
                    break;

                case "2":
                    // Mostrar mensaje
                    Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show();

                    // Enviar código de error
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
