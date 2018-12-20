package com.example.acer.waybus.Rutas;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.acer.waybus.Horarios.Fragment_HorariosAdapter;
import com.example.acer.waybus.Modelo.Horario;
import com.example.acer.waybus.R;
import com.example.acer.waybus.tools.Constantes;
import com.example.acer.waybus.web.VolleySingleton;
import com.example.acer.waybus.Adaptadores.*;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

/**
 * Actividad de la vista de detalle de la ruta donde se mostrarán datos más concretos de cada ruta
 *
 */
public class viewRutasDetalle extends AppCompatActivity
        implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private TextView tvTituloRuta;
    private TextView tvMsgVacio;
    private TextView tvNomLinea;
    private TextView tvNomEstacion;

    private RecyclerView recHorarios;
    private RecyclerView.LayoutManager layoutManager;

    private SupportMapFragment mMapFragment;
    private GoogleMap mMap;
    private String idRuta;

    private Fragment_HorariosAdapter adapterTable;
    private Gson gson = new Gson();
    private static final String TAG = Adaptador_Rutas.class.getSimpleName();

    private Polyline[] polyline = new Polyline[100];
    private int posItinerario = 0;

    private cargarItinerario cargarIt = new cargarItinerario();

    private Typeface fontPrincipal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lineas_activity_rutadetalle);

        Toolbar toolbar = (Toolbar) findViewById(R.id.ToolbarPrincipal);
        setSupportActionBar(toolbar);

        mMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.plano);
        mMapFragment.getMapAsync(this);

        tvTituloRuta = (TextView) findViewById(R.id.tituloRuta);
        tvMsgVacio = (TextView) findViewById(R.id.msgVacio);
        tvNomLinea = (TextView) findViewById(R.id.nomLinea);
        tvNomEstacion = (TextView) findViewById(R.id.nomEstacion);

        fontPrincipal = Typeface.createFromAsset(getApplicationContext().getAssets(),  "fonts/Atlantic_Cruise.ttf");
        tvTituloRuta.setTypeface(fontPrincipal);

        tvTituloRuta.setText(getIntent().getExtras().getString("TituloRuta"));
        tvNomLinea.setText("Línea: " + getIntent().getExtras().getString("NumLinea") + ", " +
                getIntent().getExtras().getString("Modelo") + ", " + getIntent().getExtras().getString("Capacidad"));
        tvNomEstacion.setText("Estación: " + getIntent().getExtras().getString("NomEstacion") + ", " +
                getIntent().getExtras().getString("CP") + ", " +
                getIntent().getExtras().getString("Telefono"));

        recHorarios = (RecyclerView) findViewById(R.id.recyclerTableHorarios);
        recHorarios.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recHorarios.setLayoutManager(layoutManager);

        // Recibimos el dato del id de la ruta que fue lanzado al pulsar el botón de enlace a la actividad de detalle
        idRuta = String.valueOf(getIntent().getExtras().getInt("IdRuta"));
        // Instancia del id propio del dispositivo Android donde se ejecute la aplicación
        String idMovil = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

        cargarAdaptador(idRuta, idMovil, recHorarios, tvMsgVacio, this);
    }

    /**
     *
     * @param idRuta -> id de la ruta asociada a la actividad
     * @param idMovil -> id del dispositivo móvil de usuario
     * @param recyclerHorarios -> Contenedor con la lista de horarios de la ruta
     * @param msgVacio -> Mensaje que se mostrará en caso de que la ruta no tenga horarios asociados
     * @param activity -> Actividad actual de la vista
     */
    public void cargarAdaptador(String idRuta, String idMovil, final RecyclerView recyclerHorarios,
                                final TextView msgVacio, final Activity activity)
    {
        // Url que se encarga de realizar la consulta correspondiente a la obtención de datos para la vista
        String newUrl = Constantes.GET_HORARIOS_BY_RUTA + "?Horarios_idRutas=" + idRuta + "&idMovil=" + idMovil;

        // Petición GET que devolverán la lista de horarios asociado a la ruta
        VolleySingleton.
                getInstance(this).
                addToRequestQueue(
                        new JsonObjectRequest(
                                Request.Method.GET,
                                newUrl,
                                (String) null,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {

                                        procesarRespuesta(response, msgVacio, recyclerHorarios, activity);
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
     * @param recyclerTableHorarios -> Contenedor de la lista de horarios
     */
    private void procesarRespuesta(JSONObject response, TextView msgVacio, RecyclerView recyclerTableHorarios,
                                   Activity activity)
    {

        try
        {
            // En función de si la consulta devuelve algún valor o no se mostrarán o no la lista con los horarios.
            String estado = response.getString("estado");
            switch (estado)
            {
                case "1": // ÉXITO

                    msgVacio.setVisibility(View.GONE);
                    recyclerTableHorarios.setVisibility(View.VISIBLE);

                    // Obtener arrays "metas" Json
                    JSONArray mensaje = response.getJSONArray("horarios");

                    // Parsear con Gson
                    Horario[] horarios = gson.fromJson(mensaje.toString(), Horario[].class);

                    // Inicializar adaptador
                    adapterTable = new Fragment_HorariosAdapter(Arrays.asList(horarios), activity);
                    // Setear adaptador a la lista
                    recyclerTableHorarios.setAdapter(adapterTable);
                    break;

                case "2": // FALLIDO
                    String mensaje2 = response.getString("estado");
                    Toast.makeText(
                            activity,
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

    /**
     * Método donde se cargará el itinerario y la marca de la posicion actual del bus asociada al mapa de la vista
     *
     * @param googleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        polyline[0] = mMap.addPolyline(new PolylineOptions().add(new LatLng(37.262103, -6.948940)));
        polyline[posItinerario].remove();
        posItinerario += 1;

        cargarIt.cargarItinerario(idRuta, getApplicationContext(), TAG, mMap);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    /**
     *
     * @param marker
     * @return -> Devuelve la información asociado al marcador del mapa
     */
    @Override
    public boolean onMarkerClick(Marker marker) {

        marker.showInfoWindow();

        return true;
    }
}
