package com.example.acer.waybus.Rutas;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.acer.waybus.Modelo.Estacion;
import com.example.acer.waybus.Modelo.Horario;
import com.example.acer.waybus.Modelo.Linea;
import com.example.acer.waybus.Modelo.Ruta;
import com.example.acer.waybus.R;
import com.example.acer.waybus.tools.Constantes;
import com.example.acer.waybus.web.VolleySingleton;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

/**
 * Fragmento que muestra una lista con todas las rutas próximas en salir.
 *
 */
public class Fragment_seccion3 extends Fragment {

    /* Etiqueta de depuración */
    private static final String TAG = Fragment_seccion3.class.getSimpleName();

    /* Adaptador del recycler view */
    private Adaptador_ProxRutas adapter;

    /* Instancia global del recycler view */
    private RecyclerView recListaHoy;
    private RecyclerView recListaManana;

    /* Instancia global del administrador */
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.LayoutManager layoutManager2;

    private TextView DiaActual;
    private TextView DiaManana;

    private Gson gson = new Gson();

    Typeface fontPrincipal;

    final static String[] mesesAnnio = new String[]{"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
            "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
    final static String[] diasSemana = new String[]{ "Domingo", "Lunes", "Martes", "Miercoles", "Jueves", "Viernes", "Sábado"};

    public Fragment_seccion3(){}

    /**
     *
     * @param inflater -> Infla el XML asociado al fragmento
     * @param container -> Controla la lista de views de la aplicación
     * @param savedInstanceState
     * @return
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View v = inflater.inflate(R.layout.lineas_fragmentmain_seccion3, container, false);

        fontPrincipal = Typeface.createFromAsset(getContext().getAssets(),  "fonts/Atlantic_Cruise.ttf");

        DiaActual = (TextView) v.findViewById(R.id.txtFechaHoy);
        DiaActual.setText(getActualDate());
        DiaActual.setTypeface(fontPrincipal);

        recListaHoy = (RecyclerView) v.findViewById(R.id.reciclador);
        recListaHoy.setHasFixedSize(true);

        // Administrador para la carga del recycler view en el layout
        layoutManager = new LinearLayoutManager(getActivity());
        recListaHoy.setLayoutManager(layoutManager);

        // Dependiendo del día de la semana de hoy las próximas rutas en salir en el día de hoy
        // serán entresemanales o de fin de semana.
        if (getDayOfTheWeek() >= 2 && getDayOfTheWeek() <= 6)
        {
            cargarAdaptador(Constantes.GET_PROX_RUTAS_HOY + "?diasSemana=LMXJV", recListaHoy);
        }
        else
        {
            cargarAdaptador(Constantes.GET_PROX_RUTAS_HOY + "?diasSemana=SD", recListaHoy);
        }

        DiaManana = (TextView) v.findViewById(R.id.txtFechaManana);
        DiaManana.setText(getTomorrowDate());
        DiaManana.setTypeface(fontPrincipal);

        recListaManana = (RecyclerView) v.findViewById(R.id.reciclador2);
        recListaManana.setHasFixedSize(true);

        layoutManager2 = new LinearLayoutManager(getActivity());
        recListaManana.setLayoutManager(layoutManager2);

        // Dependiendo del día de la semana de mañana las próximas rutas en salir en el día de hoy
        // serán entresemanales o de fin de semana.
        if (getDayOfTheWeek() <= 5)
        {
            cargarAdaptador(Constantes.GET_PROX_RUTAS_LMXJV, recListaManana);
        }
        else
        {
            cargarAdaptador(Constantes.GET_PROX_RUTAS_SD, recListaManana);
        }

        return v;
    }

    /**
     * Carga el adaptador con las próximas rutas del día de hoy en la respuesta
     */

    /**
     * Carga el adaptador con las próximas rutas del día de hoy en la respuesta
     *
     * @param getConstante -> URL del archivo php que realiza la consulta a la base de datos
     * @param recyclerView -> Contenedor de la lista de rutas
     */
    public void cargarAdaptador(String getConstante, final RecyclerView recyclerView)
    {
        // Petición GET que devolverán las rutas que estén proximas en salir
        VolleySingleton
                .getInstance(getActivity())
                .addToRequestQueue(
                        new JsonObjectRequest(
                                Request.Method.GET,
                                getConstante,
                                (String) null,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {

                                        procesarRespuesta(response, recyclerView);
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
     * @param recyclerView -> Contenedor de la lista de rutas
     */
    private void procesarRespuesta(JSONObject response, RecyclerView recyclerView)
    {
        try
        {
            // En función de si la consulta devuelve algún valor o no se mostrarán o no la lista con las rutas.
            String estado = response.getString("estado");
            switch (estado)
            {
                case "1": // ÉXITO
                    // Obtener array "metas" Json
                    JSONArray mensaje = response.getJSONArray("prox_rutas");
                    // Parsear con Gson
                    Horario[] horarios = gson.fromJson(mensaje.toString(), Horario[].class);
                    Ruta[] rutas = gson.fromJson(mensaje.toString(), Ruta[].class);
                    Linea[] lineas = gson.fromJson(mensaje.toString(), Linea[].class);
                    Estacion[] estaciones = gson.fromJson(mensaje.toString(), Estacion[].class);

                    // Inicializar adaptador
                    adapter = new Adaptador_ProxRutas(Arrays.asList(horarios), Arrays.asList(rutas),
                            Arrays.asList(lineas), Arrays.asList(estaciones), getActivity());
                    // Setear adaptador a la lista
                    recyclerView.setAdapter(adapter);

                    break;

                case "2": // FALLIDO
                    Toast.makeText(
                            getActivity(),
                            "No hay próximas rutas en salir",
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    /**
     *
     * @return -> Devuelve el día de la semana actual
     */
    public static int getDayOfTheWeek()
    {
        Calendar calendar = Calendar.getInstance();

        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    /**
     *
     * @return -> Devuelve la fecha actual.
     */
    public static String getActualDate()
    {
        Calendar calendar = Calendar.getInstance();

        String dia = Integer.toString(calendar.get(Calendar.DATE));
        int mes = calendar.get(Calendar.MONTH);
        String annio = Integer.toString(calendar.get(Calendar.YEAR));

        String fechaActual = diasSemana[getDayOfTheWeek() - 1] + ", " + dia + " de " +
                mesesAnnio[mes] + " del " + annio;

        return fechaActual;
    }

    /**
     *
     * @return -> Devuelve la fecha del día de mañana
     */
    public static String getTomorrowDate()
    {
        Calendar calendar = Calendar.getInstance();

        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date manana = calendar.getTime();

        DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");

        String mananaAsString = dateFormat.format(manana);

        return mananaAsString;
    }
}
