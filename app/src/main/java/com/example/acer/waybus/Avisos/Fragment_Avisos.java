package com.example.acer.waybus.Avisos;

import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.acer.waybus.Modelo.Aviso;
import com.example.acer.waybus.Modelo.Estacion;
import com.example.acer.waybus.Modelo.Horario;
import com.example.acer.waybus.Modelo.Linea;
import com.example.acer.waybus.Modelo.Ruta;
import com.example.acer.waybus.Modelo.Usuario;
import com.example.acer.waybus.tools.Constantes;
import com.example.acer.waybus.web.VolleySingleton;
import com.example.acer.waybus.Adaptadores.*;
import com.google.gson.Gson;

import com.example.acer.waybus.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

/**
 * Fragmento que contiene la vista con los datos referentes a la lista de alertas asociado a un usuario concreto
 *
 */
public class Fragment_Avisos extends Fragment {

    /* Etiqueta de depuración */
    private static final String TAG = Fragment_Avisos.class.getSimpleName();

    /* Adaptador del recycler view */
    private Fragment_AvisosAdapter adapter;

    /* Instancia global del recycler view */
    private RecyclerView recyclerAvisos;

    /* Instancia global del administrador */
    private RecyclerView.LayoutManager layoutManager;

    private Gson gson = new Gson();

    private cargaAviso eliminar = new cargaAviso();

    public Fragment_Avisos(){}

    /**
     *
     * @param inflater -> Infla el XML asociado al fragmento
     * @param container -> Controla la lista de views de la aplicación
     * @param savedInstanceState
     * @return
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.avi_fragment_avisos, container, false);

        recyclerAvisos = (RecyclerView) v.findViewById(R.id.recAvisos);
        recyclerAvisos.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerAvisos.setLayoutManager(layoutManager);

        // Instancia del id propio del dispositivo Android donde se ejecute la aplicación
        String idMovil = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID);

        // Método encargado de eliminar los avisos pasados en el tiempo
        eliminar.eliminarAvisosPasados(TAG, getContext(), getActivity());
        cargarAvisos(idMovil);

        return v;
    }

    /**
     * Método encargado de cargar la lista de alertas de un usuario concreto
     *
     * @param idMovil
     */
    public void cargarAvisos(String idMovil)
    {
        // URL con la consulta a la base de datos
        String newUrl = Constantes.GET_AVISOS + "?idMovil=" + idMovil;

        // Petición GET que devolverán la lista de alertas de un usuario concreto
        VolleySingleton.getInstance(getActivity())
                .addToRequestQueue(
                        new JsonObjectRequest(
                                Request.Method.GET,
                                newUrl,
                                (String) null,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {

                                        procesarRespuestaAvisos(response);
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
     */
    private void procesarRespuestaAvisos(JSONObject response)
    {
        try
        {
            // En función de si la consulta devuelve algún valor o no se mostrarán o no las alertas de un usuario.
            String estado = response.getString("estado");
            switch (estado)
            {
                case "1": // ÉXITO
                    JSONArray mensaje = response.getJSONArray("avisos");

                    // Parsear con Gson
                    Estacion[] estaciones = gson.fromJson(mensaje.toString(), Estacion[].class);
                    Linea[] lineas = gson.fromJson(mensaje.toString(), Linea[].class);
                    Ruta[] rutas = gson.fromJson(mensaje.toString(), Ruta[].class);
                    Horario[] horarios = gson.fromJson(mensaje.toString(), Horario[].class);
                    Aviso[] avisos = gson.fromJson(mensaje.toString(), Aviso[].class);
                    Usuario[] usuarios = gson.fromJson(mensaje.toString(), Usuario[].class);

                    adapter = new Fragment_AvisosAdapter(Arrays.asList(estaciones), Arrays.asList(lineas),
                            Arrays.asList(rutas), Arrays.asList(horarios), Arrays.asList(avisos),
                            Arrays.asList(usuarios), getActivity(), getActivity());

                    recyclerAvisos.setAdapter(adapter);
                    break;

                case "2": // FALLIDO
                    String mensaje2 = response.getString("estado");
                    Toast.makeText(
                            getActivity(),
                            "No tienes alertas pendientes",
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
