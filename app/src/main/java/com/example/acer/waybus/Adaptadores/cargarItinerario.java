package com.example.acer.waybus.Adaptadores;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.acer.waybus.Modelo.Horario;
import com.example.acer.waybus.Modelo.Itinerario;
import com.example.acer.waybus.Modelo.Ruta;
import com.example.acer.waybus.R;
import com.example.acer.waybus.tools.Constantes;
import com.example.acer.waybus.web.VolleySingleton;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Clase contenedora con los métodos encargados d ela gestión d elos itinerarios y marcas mostradas en el mapa
 */
public class cargarItinerario {

    /* Lista de objetos {@link} que representa la fuente de datos de inflado */
    private List<Itinerario> itemsItinerario;
    private List<Horario> itemsHorario;
    private List<Ruta> itemsRuta;

    private Gson gson = new Gson();

    // Array que alojará todos los trazados de las rutas disponibles
    private Polyline[] polyline = new Polyline[1000];
    private int posItinerario = 0;

    int MARKER_UPDATE_INTERVAL = 10000; /* milliseconds */
    Handler handler = new Handler();

    double tomaActual = 0;
    double posActual = 0;

    private double segDuracion = 0;
    private double minDuracion = 0;
    private int posRutaActiva = 0;

    private boolean rutas_activas = false;

    Marker[] markerBus = new Marker[1000];
    int posMarkerBus = 0;

    /**
     * Método encargado de cargar el trazado con una ruta seleccionada
     *
     * @param idRuta -> id de la ruta asociada al itinerario a mostrar
     * @param context -> Contexto de la aplicación
     * @param TAG -> Etiqueta de depuración
     * @param mMap -> mapa donde se cargará el itinerario
     */
    public void cargarItinerario(String idRuta, final Context context, final String TAG, final GoogleMap mMap)
    {
        // URL con la consulta a la base de datos
        String newUrl = Constantes.GET_ITINERARIO + "?idRuta=" + idRuta + "&Horarios_idRutas=" + idRuta + "&idRutas=" + idRuta;

        // Petición GET que devolverán la lista de marcas de coordenadas que componen el itinerario
        VolleySingleton
                .getInstance(context)
                .addToRequestQueue(
                        new JsonObjectRequest(
                                Request.Method.GET,
                                newUrl,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {

                                        procesarRespuesta(response, mMap, context);
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
     * @param mMap -> mapa donde se cargará el itinerario
     * @param response -> Objeto JSON que devuelve la respuesta a la consulta a la base de datos.
     * @param context -> Contexto de la aplicación
     */
    private void procesarRespuesta(JSONObject response, final GoogleMap mMap, Context context)
    {
        try
        {
            String estado = response.getString("estado");

            switch (estado)
            {
                case "1": // ÉXITO
                    // Antes de cargar un nuevo itinerario se limpiará el mapa con el itinerario anterior
                    mMap.clear();

                    JSONArray mensaje = response.getJSONArray("itinerario");

                    Itinerario[] itinerarios = gson.fromJson(mensaje.toString(), Itinerario[].class);

                    itemsItinerario = Arrays.asList(itinerarios);

                    Double latMedia = 0.0;
                    Double lonMedia = 0.0;

                    // Creamos el PolylineOptions donde se alojarán todas las coordenadas que componen el trazado
                    PolylineOptions polylineOptions = new PolylineOptions();
                    for (int i = 0; i < itemsItinerario.size(); i++)
                    {
                        Double lat = itemsItinerario.get(i).getLatitud();
                        Double lon = itemsItinerario.get(i).getLongitud();
                        polylineOptions.add(new LatLng(lat, lon));

                        latMedia += lat;
                        lonMedia += lon;
                    }
                    polylineOptions.color(Color.parseColor("#FF8451"));

                    latMedia = latMedia / itemsItinerario.size();
                    lonMedia = lonMedia / itemsItinerario.size();

                    // Se añade la posición del arrayd e trazados al mapa de la vista
                    polyline[posItinerario] = mMap.addPolyline(polylineOptions);

                    // Posición cámara
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                            new LatLng(latMedia, lonMedia), 12.0f));

                    JSONArray mensaje2 = response.getJSONArray("horarios");
                    Horario[] horarios = gson.fromJson(mensaje2.toString(), Horario[].class);
                    itemsHorario = Arrays.asList(horarios);

                    for (int j = 0; j < itemsHorario.size(); j++)
                    {
                        SimpleDateFormat formato = new SimpleDateFormat("HH:mm");

                        Date datHoraActual = new Date();
                        String strHoraSalida = itemsHorario.get(j).getHoraSalida();
                        String strHoraLlegada = itemsHorario.get(j).getHoraLlegada();

                        String horaActualAsString = formato.format(datHoraActual);
                        Date dateFromStringHoraActual = formato.parse(horaActualAsString);
                        Date horaSalida = formato.parse(strHoraSalida.substring(0,5));
                        Date horaLlegada = formato.parse(strHoraLlegada.substring(0,5));

                        Calendar calendar = Calendar.getInstance();
                        int hoy = calendar.get(Calendar.DAY_OF_WEEK);

                        // Si el autobus actualmente está realizando el trazado se mostrará una marca en el mapa
                        // con su información correspondiente
                        if ((itemsHorario.get(j).getDiasSemana().equals("LMXJV") && ((hoy >= 2)||(hoy <= 6))) ||
                                ((itemsHorario.get(j).getDiasSemana().equals("SD")) && ((hoy > 6))||(hoy < 2))) {

                            if ((dateFromStringHoraActual.compareTo(horaSalida) >= 0) && (dateFromStringHoraActual.compareTo(horaLlegada) <= 0)) {
                                rutas_activas = true;

                                // Se toma el tiempo de duración del viaje
                                int horDuracion = (Integer.parseInt(itemsHorario.get(j).getDuracion().substring(0, 2)) * 60);
                                int minDuracion = Integer.parseInt(itemsHorario.get(j).getDuracion().substring(3, 5));
                                int totalDuracion = horDuracion + minDuracion;
                                segDuracion = totalDuracion * 60;
                                int tomas = 6 * totalDuracion;

                                // Se toma la hora actual
                                int horaActual = calendar.get(Calendar.HOUR_OF_DAY);
                                int minutoActual = calendar.get(Calendar.MINUTE);

                                int lngHoraActual = horaActual * 60 + minutoActual;

                                int lngHoraSalida = Integer.parseInt(strHoraSalida.toString().substring(0, 2)) * 60 +
                                        Integer.parseInt(strHoraSalida.toString().substring(3, 5));

                                // Se calcula la diferencia de hora entre la hora actual y la hora de salida
                                int difHoras = lngHoraActual - lngHoraSalida;

                                // Se calcula de manera estimada la posición en la que se encuentra el autobus en el trazado
                                tomaActual = (double) difHoras * tomas / totalDuracion;
                                posActual = (double) itemsItinerario.size() / tomas * tomaActual;

                                // Se calcula el tiempo de actualizado de la posición del marcador
                                MARKER_UPDATE_INTERVAL = (minDuracion * 60 / itemsItinerario.size()) *1000;

                                posRutaActiva = j;
                            }
                        }
                    }

                    // En el caso de que la ruta esté activa en estos momentos se añade el marcador al mapa y se calcula
                    // el tiempo que le queda para llegar a su destino
                    if (rutas_activas)
                    {
                        JSONArray mensaje3 = response.getJSONArray("ruta");
                        Ruta[] rutas = gson.fromJson(mensaje3.toString(), Ruta[].class);
                        itemsRuta = Arrays.asList(rutas);

                        segDuracion -= posActual * MARKER_UPDATE_INTERVAL / 1000;
                        minDuracion = segDuracion / 60;

                        markerBus[0] = mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(itemsItinerario.get((int) redondear(posActual, 0)).getLatitud(),
                                        itemsItinerario.get((int) redondear(posActual, 0)).getLongitud()))
                                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_pin_bus))
                                .title(itemsRuta.get(0).getOrigen() + " - " + itemsRuta.get(0).getDestino())
                                .snippet("Quedan " + (int) redondear(minDuracion, 0)
                                        + " minutos para que el autobús llegue a su destino"));
                        markerBus[0].hideInfoWindow();

                        // Método que actualizarla la posición e información del marcador transcurrido un tiempo estipulado
                        Runnable updateMarker = new Runnable() {
                            @Override
                            public void run() {

                                segDuracion -= MARKER_UPDATE_INTERVAL / 1000;
                                minDuracion = segDuracion / 60;

                                System.out.println("Anger - " + segDuracion + " = " + MARKER_UPDATE_INTERVAL + "/" + 1000);

                                markerBus[posMarkerBus].remove();

                                posMarkerBus += 1;
                                posActual += 1;

                                int redondeoPosActual = (int) redondear(posActual, 0);

                                if (redondeoPosActual < itemsItinerario.size())
                                {
                                    markerBus[posMarkerBus] = mMap.addMarker(new MarkerOptions()
                                            .position(new LatLng(itemsItinerario.get(redondeoPosActual).getLatitud(),
                                                    itemsItinerario.get(redondeoPosActual).getLongitud()))
                                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_pin_bus))
                                            .title(itemsRuta.get(0).getOrigen() + " - " + itemsRuta.get(0).getDestino())
                                            .snippet("Quedan " + (int) redondear(minDuracion, 0)
                                                    + " minutos para que el autobús llegue a su destino"));
                                    markerBus[posMarkerBus].hideInfoWindow();

                                    handler.postDelayed(this, MARKER_UPDATE_INTERVAL);
                                }
                            }
                        };

                        handler.postDelayed(updateMarker, MARKER_UPDATE_INTERVAL);
                    }
                    else
                    {
                        Toast.makeText(context, "No hay ningún autobus tomando esta ruta en estos momentos", Toast.LENGTH_LONG).show();
                    }

                    break;

                case "2": // FALLIDO
                    String mensaje4 = response.getString("estado");
                    Toast.makeText(
                            context,
                            "No hay itinerarios disponibles",
                            Toast.LENGTH_SHORT).show();
                    break;

            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Método utilizado para realizar redondeo en los minutos que quedan para terminar el viaje
     *
     * @param numero -> Número a redondear
     * @param numeroDecimales -> Número de decimales a mostrar
     * @return
     */
    public static double redondear(double numero, int numeroDecimales) {
        long mult=(long)Math.pow(10,numeroDecimales);
        double resultado=(Math.round(numero*mult))/(double)mult;
        return resultado;
    }
}
