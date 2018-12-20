package com.example.acer.waybus.Rutas;

import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.acer.waybus.R;

import com.example.acer.waybus.Adaptadores.*;

/**
 * Fragmento que contiene una lista con todas las rutas disponibles de la aplicación
 *
 */
public class Fragment_seccion1 extends Fragment {

    /* Etiqueta de depuración */
    private static final String TAG = Fragment_seccion1.class.getSimpleName();

    /* Instancia global del recycler view */
    private RecyclerView lista;

    /* Instancia global del administrador */
    private RecyclerView.LayoutManager layoutManager;

    /* Array de las posiciones de las rutas marcadas como favoritas*/
    private int[] tvFavoritos = new int[1000];

    /* Método que carga la lista de las rutas favoritas de un usuario concreto */
    private cargarUsuarioFavoritos cUsuarios = new cargarUsuarioFavoritos();

    public Fragment_seccion1(){}

    /**
     *
     * @param inflater -> Infla el XML asociado al fragmento
     * @param container -> Controla la lista de views de la aplicación
     * @param savedInstanceState
     * @return
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.lineas_fragment_main, container, false);

        lista = (RecyclerView) v.findViewById(R.id.reciclador);
        lista.setHasFixedSize(true);

        // Administrador para la carga del recycler view en el layout
        layoutManager = new LinearLayoutManager(getActivity());
        lista.setLayoutManager(layoutManager);

        // Instancia del id propio del dispositivo Android donde se ejecute la aplicación
        String idMovil = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID);

        cUsuarios.cargarUsuarioFavoritos(idMovil, "Huelva", getContext(), lista, tvFavoritos, getActivity(), TAG);

        return v;
    }
}
