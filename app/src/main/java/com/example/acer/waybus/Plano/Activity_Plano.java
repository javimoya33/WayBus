package com.example.acer.waybus.Plano;

import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.acer.waybus.Modelo.Ruta;
import com.example.acer.waybus.R;
import com.example.acer.waybus.Adaptadores.*;

import com.example.acer.waybus.tools.Constantes;
import com.example.acer.waybus.web.VolleySingleton;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Actividad de la vista de Plano
 *
 */
public class Activity_Plano extends AppCompatActivity
        implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    /* Etiqueta de depuración */
    private static final String TAG = Activity_Plano.class.getSimpleName();

    private SupportMapFragment mMapFragment;
    private GoogleMap mMap;

    /* Lista de objetos {@link} que representa la fuente de datos de inflado */
    private List<Ruta> itemsOrigenRuta;
    private List<Ruta> itemsDestinoRutas;

    private Gson gson = new Gson();

    private Spinner spinOrigen;
    private Spinner spinDestino;

    private boolean encontrado = false;

    private Polyline[] polyline = new Polyline[100];
    private int posItinerario = 0;

    private String valDestino = " ";

    private Typeface fontPrincipal;

    private cargarItinerario cargaIt = new cargarItinerario();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pln_view_plano);

        Toolbar toolbar = (Toolbar) findViewById(R.id.ToolbarPrincipal);
        setSupportActionBar(toolbar);
        getSupportActionBar().setIcon(R.mipmap.ic_main_plane2);

        mMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.plano);
        mMapFragment.getMapAsync(this);

        spinOrigen = (Spinner) findViewById(R.id.editOrigen);
        spinDestino = (Spinner) findViewById(R.id.editDestino);

        cargarOrigenRutas();
    }

    /**
     * Método lanzado cuando se haya cargado el contenedor del mapa
     *
     * @param googleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        polyline[0] = mMap.addPolyline(new PolylineOptions().add(new LatLng(37.262103, -6.948940)));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    /**
     * Método encargado de listar en el spinner todos los origenes de ruta disponibles
     */
    public void cargarOrigenRutas()
    {
        // Instancia del id propio del dispositivo Android donde se ejecute la aplicación
        String idMovil = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        // URL con la consulta a la base de datos
        String newUrl = Constantes.GET_RUTAS  + "?idMovil=" + idMovil;

        // Petición GET que devolverán la lista de origenes de la consulta a la base de datos
        VolleySingleton
                .getInstance(this)
                .addToRequestQueue(
                        new JsonObjectRequest(
                                Request.Method.GET,
                                newUrl,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {

                                        procesarOrigenRutas(response);
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
    private void procesarOrigenRutas(JSONObject response)
    {
        try
        {
            String estado = response.getString("estado");

            switch (estado)
            {
                case "1": // ÉXITO
                    JSONArray mensaje = response.getJSONArray("rutas");

                    Ruta[] rutas = gson.fromJson(mensaje.toString(), Ruta[].class);

                    itemsOrigenRuta = Arrays.asList(rutas);

                    final ArrayList<String> origenRuta = new ArrayList<String>();

                    for (int i = 0; i < itemsOrigenRuta.size(); i++)
                    {
                        encontrado = false;

                        for (int j = 0; j < origenRuta.size(); j++)
                        {
                            if (itemsOrigenRuta.get(i).getOrigen().equals(origenRuta.get(j)))
                            {
                                encontrado = true;
                            }
                        }

                        if (encontrado == false)
                        {
                            origenRuta.add(itemsOrigenRuta.get(i).getOrigen());
                        }
                    }

                    // Se crea el arrayadapter donde se alojarán todos los origenes guardados en el arraylist
                    ArrayAdapter<String> adapter1 =
                            new ArrayAdapter<String>(this, R.layout.simple_spinner, origenRuta)
                            {
                                public View getView(int position, View convertView, android.view.ViewGroup parent)
                                {
                                    fontPrincipal = Typeface.createFromAsset(getContext().getAssets(),  "fonts/Atlantic_Cruise.ttf");
                                    TextView textSpinnerDestino = (TextView) super.getView(position, convertView, parent);
                                    textSpinnerDestino.setTypeface(fontPrincipal);
                                    return textSpinnerDestino;
                                }

                                public View getDropDownView(int position, View convertView, android.view.ViewGroup parent) {

                                    fontPrincipal = Typeface.createFromAsset(getContext().getAssets(),  "fonts/Atlantic_Cruise.ttf");
                                    TextView textSpinnerDestino = (TextView) super.getView(position, convertView, parent);
                                    textSpinnerDestino.setTypeface(fontPrincipal);
                                    return textSpinnerDestino;
                                }
                            };

                    adapter1.setDropDownViewResource(R.layout.simple_spinner);
                    spinOrigen.setAdapter(adapter1);

                    spinOrigen.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                            spinDestino.setSelection(0);

                            cargarDestinoRuta(itemsOrigenRuta.get(pos).getOrigen());
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });

                case "2": // FALLIDO
                    String mensaje2 = response.getString("estado");
                    Toast.makeText(
                            this,
                            mensaje2,
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
     * Método encargado de devolver todos los destinos posibles en función del origen seleccionado
     *
     * @param origen
     */
    public void cargarDestinoRuta(String origen)
    {
        // URL con la consulta a la base de datos
        String newUrl = Constantes.GET_DESTINOS_RUTAS + "?origen=" + origen;

        // Petición GET que devolverán la lista de destinos de la consulta a la base de datos
        VolleySingleton
                .getInstance(this)
                .addToRequestQueue(
                        new JsonObjectRequest(
                                Request.Method.GET,
                                newUrl,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {

                                        procesarDestinosRutas(response);
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
    public void procesarDestinosRutas(JSONObject response)
    {
        try
        {
            String estado = response.getString("estado");

            switch (estado)
            {
                case "1": // ÉXITO -
                    JSONArray mensaje = response.getJSONArray("rutas");

                    Ruta[] rutas = gson.fromJson(mensaje.toString(), Ruta[].class);
                    itemsDestinoRutas = Arrays.asList(rutas);

                    ArrayList<String> destinoRutas = new ArrayList<>();
                    destinoRutas.add(" ");

                    for (int i = 0; i < itemsDestinoRutas.size(); i++)
                    {
                        destinoRutas.add(itemsDestinoRutas.get(i).getDestino());
                    }

                    spinDestino.setSelection(0);

                    // Se crea el arrayadapter donde se alojarán todos los destinos guardados en el arraylist
                    ArrayAdapter<String> adapter1 =
                            new ArrayAdapter<String>(this, R.layout.simple_spinner, destinoRutas)
                            {
                                public View getView(int position, View convertView, android.view.ViewGroup parent)
                                {
                                    fontPrincipal = Typeface.createFromAsset(getContext().getAssets(),  "fonts/Atlantic_Cruise.ttf");
                                    TextView textSpinnerDestino = (TextView) super.getView(position, convertView, parent);
                                    textSpinnerDestino.setTypeface(fontPrincipal);
                                    return textSpinnerDestino;
                                }

                                public View getDropDownView(int position, View convertView, android.view.ViewGroup parent) {

                                    fontPrincipal = Typeface.createFromAsset(getContext().getAssets(),  "fonts/Atlantic_Cruise.ttf");
                                    TextView textSpinnerDestino = (TextView) super.getView(position, convertView, parent);
                                    textSpinnerDestino.setTypeface(fontPrincipal);
                                    return textSpinnerDestino;
                                }
                            };

                    adapter1.setDropDownViewResource(R.layout.simple_spinner);
                    spinDestino.setAdapter(adapter1);

                    spinDestino.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                            // Si se selecciona un  destino diferente al anterior se eliminará el itinerario mostrado
                            // en el marca y se cargará uno nuevo
                            if (!valDestino.equals(String.valueOf(parent.getItemAtPosition(pos))))
                            {
                                valDestino = String.valueOf(parent.getItemAtPosition(pos));

                                //polyline[posItinerario].remove();

                                posItinerario += 1;

                                cargaIt.cargarItinerario(String.valueOf(itemsDestinoRutas.get(pos-1).getIdRuta()),
                                        getApplicationContext(), TAG, mMap);
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });

                    break;

                case "2": // FALLIDO
                    String mensaje2 = response.getString("estado");
                    Toast.makeText(
                            this,
                            "No hay destinos posibles para este origen de ruta",
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
     * Método encargado de mostrar el mensaje informativo del marcador
     *
     * @param marker
     * @return
     */
    @Override
    public boolean onMarkerClick(Marker marker) {

        marker.showInfoWindow();

        return true;
    }
}
