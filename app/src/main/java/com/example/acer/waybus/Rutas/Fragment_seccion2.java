package com.example.acer.waybus.Rutas;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.acer.waybus.Adaptadores.*;
import com.example.acer.waybus.Modelo.Ruta;
import com.example.acer.waybus.R;

import java.util.List;

/**
 * Fragmento que muestra una lista con todas las rutas de la población más cercana a la localización del usuario.
 *
 */
public class Fragment_seccion2 extends Fragment {

    /* Etiqueta de depuración */
    private static final String TAG = Fragment_seccion2.class.getSimpleName();

    private static final View TODO = null;

    /* Instancia global del recycler view */
    private RecyclerView lista;

    /* Instancia global del administrador */
    private RecyclerView.LayoutManager layoutManager;

    /* Array de las posiciones de las rutas marcadas como favoritas*/
    private int[] tvFavoritos = new int[1000];

    /* Método que carga la lista de las rutas favoritas de una localización concreta */
    private cargaRutas cRutas = new cargaRutas();

    private List<Ruta> itemsRuta;

    protected LocationManager locationManager;

    /* Etiqueta que muestra la población de las rutas más cercanas a la localización del usuario */
    private TextView origenUsuario;

    public Fragment_seccion2() {}

    /**
     *
     * @param inflater -> Infla el XML asociado al fragmento
     * @param container -> Controla la lista de views de la aplicación
     * @param savedInstanceState
     * @return
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.lineas_fragment_main, container, false);

        origenUsuario = (TextView) v.findViewById(R.id.tvOrigenUser);
        origenUsuario.setVisibility(View.VISIBLE);

        lista = (RecyclerView) v.findViewById(R.id.reciclador);
        lista.setHasFixedSize(true);

        // Administrador para la carga del recycler view en el layout
        layoutManager = new LinearLayoutManager(getActivity());
        lista.setLayoutManager(layoutManager);

        // Instancia del id propio del dispositivo Android donde se ejecute la aplicación
        String idMovil = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID);

        Double finalDiferencia = 1000.0;

        // Proceso que devolverá las coordenadas de GPS de nuestra localización actual
        locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return TODO;
        }

        /* Con la localización exacta devolveremos la lista con todas las rutas de nuestro servicio referentes a la
         *  población más cercana a la localización actual del usuario.
         *
         *  Si la localización es nula la app devolverá por defecto las rutas de Huelva capital
         */
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null) {


            cRutas.cargarLatLng(getContext(), itemsRuta, location.getLatitude(), location.getLongitude(),
                    finalDiferencia, origenUsuario, idMovil, lista, tvFavoritos, getActivity(), TAG);
        }
        else
        {
            cRutas.cargarLatLng(getContext(), itemsRuta, 37.269127, -6.938297,
                    finalDiferencia, origenUsuario, idMovil, lista, tvFavoritos, getActivity(), TAG);
        }

        return v;
    }
}
