package com.example.acer.waybus.Horarios;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.acer.waybus.Modelo.Estacion;
import com.example.acer.waybus.Modelo.Horario;
import com.example.acer.waybus.Modelo.Linea;
import com.example.acer.waybus.R;
import com.example.acer.waybus.tools.Constantes;
import com.example.acer.waybus.web.VolleySingleton;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

/**
 * Fragmento que contiene la vista con los datos referentes a la lista de horarios de una ruta concreta
 *
 */
public class Fragment_Horarios extends Fragment {

    /* Etiqueta de depuración */
    private static final String TAG = Fragment_Horarios.class.getSimpleName();

    /* Adaptador del recycler view */
    private Fragment_HorariosAdapter adapter;

    Typeface fontPrincipal;

    /* Instancia global del administrador */
    private RecyclerView.LayoutManager layoutManager;

    private Gson gson = new Gson();

    private EditText editOrigen;
    private EditText editDestino;
    private Button butHorarios;

    private TextView tvTituloRuta;

    private TextView tvColHoraSalidaLlegada;
    private TextView tvColHoraDuracion;
    private TextView tvColHoraPrecio;
    private TextView tvColHoraFrecuencia;

    private TextView tvMsgHoracioVacio;

    private String txtOrigen;
    private String txtDestino;

    private RecyclerView recyclerTableHorarios;

    private TextView txtEstacion;
    private TextView txtLinea;
    private TextView tvEstacion;
    private TextView tvLinea;

    public Fragment_Horarios(){

    }

    /**
     *
     * @param inflater -> Infla el XML asociado al fragmento
     * @param container -> Controla la lista de views de la aplicación
     * @param savedInstanceState
     * @return
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.hor_fragment_horarios, container, false);

        editOrigen = (EditText) v.findViewById(R.id.editOrigen);
        editDestino = (EditText) v.findViewById(R.id.editDestino);
        butHorarios = (Button) v.findViewById(R.id.butHorarios);

        tvTituloRuta = (TextView) v.findViewById(R.id.tvTituloRuta);
        tvTituloRuta.setVisibility(View.GONE);

        tvColHoraSalidaLlegada = (TextView) v.findViewById(R.id.colHoraSalidaLlegada);
        tvColHoraDuracion = (TextView) v.findViewById(R.id.colDuracion);
        tvColHoraPrecio = (TextView) v.findViewById(R.id.colPrecio);
        tvColHoraFrecuencia = (TextView) v.findViewById(R.id.colDiasSemana);

        tvMsgHoracioVacio = (TextView) v.findViewById(R.id.msgVacio);
        recyclerTableHorarios = (RecyclerView) v.findViewById(R.id.recyclerTableHorarios);
        recyclerTableHorarios.setHasFixedSize(true);
        recyclerTableHorarios.setVisibility(View.GONE);

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerTableHorarios.setLayoutManager(layoutManager);

        txtEstacion = (TextView) v.findViewById(R.id.txtEstacion);
        txtLinea = (TextView) v.findViewById(R.id.txtLinea);
        txtEstacion.setVisibility(View.GONE);
        txtLinea.setVisibility(View.GONE);

        tvEstacion = (TextView) v.findViewById(R.id.resultEstacion);
        tvLinea = (TextView) v.findViewById(R.id.resultLinea);

        fontPrincipal = Typeface.createFromAsset(getContext().getAssets(),  "fonts/Atlantic_Cruise.ttf");

        editOrigen.setTypeface(fontPrincipal);
        editDestino.setTypeface(fontPrincipal);
        butHorarios.setTypeface(fontPrincipal);
        tvTituloRuta.setTypeface(fontPrincipal);

        tvColHoraSalidaLlegada.setTypeface(fontPrincipal);
        tvColHoraSalidaLlegada.setTypeface(fontPrincipal);
        tvColHoraDuracion.setTypeface(fontPrincipal);
        tvColHoraPrecio.setTypeface(fontPrincipal);
        tvColHoraFrecuencia.setTypeface(fontPrincipal);

        tvMsgHoracioVacio.setTypeface(fontPrincipal);

        editOrigen.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            /**
             * Evento que se carga al tomar o dejar el foco del edittext de Origen
             *
             * @param v
             * @param hasFocus
             */
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (hasFocus)
                {
                    editOrigen.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.primary_edittext_light));
                    editOrigen.setHint("");
                }
                else
                {
                    editOrigen.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.primary_medium));
                    editOrigen.setHint("Origen");
                    editOrigen.setHintTextColor(ContextCompat.getColor(getContext(), R.color.primary_edittext_focus));
                }
            }
        });

        /**
         * Evento que se carga al tomar o dejar el foco del edittext de Destino
         *
         * @param v
         * @param hasFocus
         */
        editDestino.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (hasFocus)
                {
                    editDestino.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.primary_edittext_light));
                    editDestino.setHint("");
                }
                else
                {
                    editDestino.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.primary_medium));
                    editDestino.setHint("Destino");
                    editDestino.setHintTextColor(ContextCompat.getColor(getContext(), R.color.primary_edittext_focus));
                }
            }
        });

        butHorarios.setOnClickListener(new View.OnClickListener() {

            /**
             * Evento que llamará al meétodo encargado de mostrar la lista de horarios asociado a una ruta al pulsar
             * sobre el botón correspondiente
             *
             * @param v
             */
            @Override
            public void onClick(View v) {

                txtOrigen = editOrigen.getText().toString();
                txtOrigen = txtOrigen.replace(" ", "%20");

                txtDestino = editDestino.getText().toString();
                txtDestino = txtDestino.replace(" ", "%20");

                editOrigen.getText().toString();
                editDestino.getText().toString();

                cargarAdaptador(txtOrigen, txtDestino, tvMsgHoracioVacio, recyclerTableHorarios);
                cargarEstacion(txtOrigen, txtDestino);

                tvTituloRuta.setText(txtOrigen + " - " + txtDestino);
                tvTituloRuta.setVisibility(View.VISIBLE);

                txtEstacion.setVisibility(View.VISIBLE);
                txtLinea.setVisibility(View.VISIBLE);
            }
        });

        return v;
    }

    /**
     * Método encargado de cargar la estación y la línea de autobús asociada a una ruta en función
     * del origen y destino insertado en los edittexts
     *
     * @param textOrigen -> Variable con el 'Origen' insertado por el usuario
     * @param textDestino -> Variable con el 'Destino' insertado por el usuario
     */
    public void cargarEstacion(String textOrigen, String textDestino)
    {
        String newUrl = Constantes.GET_ESTACION_BY_RUTA + "?origen=" + textOrigen + "&destino=" + textDestino;

        // Petición GET que devolverán la la lista de datos asociado a la estación y la línea d ela ruta
        VolleySingleton.getInstance(getActivity())
                .addToRequestQueue(
                        new JsonObjectRequest(
                                Request.Method.GET,
                                newUrl,
                                (String) null,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {

                                        procesarRespuestaEstacion(response);
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
    private void procesarRespuestaEstacion(JSONObject response)
    {
        try
        {
            // En función de si la consulta devuelve algún valor o no se mostrarán o no los datos de estación y línea.
            String estado = response.getString("estado");

            switch (estado)
            {
                case "1": // ÉXITO

                    JSONArray mensaje = response.getJSONArray("estacion");

                    Estacion[] estaciones = gson.fromJson(mensaje.toString(), Estacion[].class);
                    Linea[] lineas = gson.fromJson(mensaje.toString(), Linea[].class);

                    List<Estacion> itemsEstacion = Arrays.asList(estaciones);
                    List<Linea> itemsLinea = Arrays.asList(lineas);

                    tvEstacion.setText(itemsEstacion.get(0).getNombre() + ",\n " + itemsEstacion.get(0).getDireccion()
                            + ", " + itemsEstacion.get(0).getCP());
                    tvLinea.setText(itemsLinea.get(0).getNumBus());
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Cargar el adaptador con una lista de horarios asociados a un ruta que ha sio insertada por el usuario.
     *
     * @param textOrigen -> Variable con el 'Origen' insertado por el usuario
     * @param textDestino -> Variable con el 'Destino' insertado por el usuario
     * @param msgVacio -> Etiqueta con mensaje en caso de que el método devuelva un valor vacío.
     * @param recyclerTableHorarios -> -> recycler con la lista de horarios asociado a la ruta.
     */
    public void cargarAdaptador(String textOrigen, String textDestino, final TextView msgVacio,
                                final RecyclerView recyclerTableHorarios)
    {
        // Url que se encarga de realizar la consulta correspondiente a la obtención de datos para la vista
        String newURL = Constantes.GET_HORARIO_BY_ORDES + "?origen=" + textOrigen + "&destino=" + textDestino;

        // Petición GET que devolverán la lista de horarios asociado a la ruta
        VolleySingleton
                .getInstance(getActivity())
                .addToRequestQueue(
                        new JsonObjectRequest(
                                Request.Method.GET,
                                newURL,
                                (String) null,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {

                                        procesarRespuesta(response, msgVacio, recyclerTableHorarios);
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
    private void procesarRespuesta(JSONObject response, TextView msgVacio, RecyclerView recyclerTableHorarios)
    {
        try {
            // En función de si la consulta devuelve algún valor o no se mostrarán o no los datos de estación y línea.
            String estado = response.getString("estado");

            switch (estado)
            {
                case "1": // ÉXITO

                    msgVacio.setVisibility(View.GONE);
                    recyclerTableHorarios.setVisibility(View.VISIBLE);

                    // Obtener arrays "metas" Json
                    JSONArray mensaje = response.getJSONArray("horario");

                    // Parsear con Gson
                    Horario[] horarios = gson.fromJson(mensaje.toString(), Horario[].class);

                    // Inicializar adaptador
                    adapter = new Fragment_HorariosAdapter(Arrays.asList(horarios), getActivity());
                    // Setear adaptador a la lista
                    recyclerTableHorarios.setAdapter(adapter);
                    break;

                case "2": // FALLIDO
                    String mensaje2 = response.getString("estado");
                    Toast.makeText(
                            getActivity(),
                            "Esta ruta no la tomamos, prueba otra",
                            Toast.LENGTH_SHORT).show();

                    msgVacio.setVisibility(View.VISIBLE);
                    recyclerTableHorarios.setVisibility(View.GONE);

                    break;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
