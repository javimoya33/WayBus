package com.example.acer.waybus.MisParadas;

import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.acer.waybus.Adaptadores.cargarUsuarioFavoritos;

import com.example.acer.waybus.R;

/**
 * Fragmento que contiene la vista con los datos referentes a la lista de rutas marcadas como favoritas
 *
 */
public class Fragment_MisParadas extends Fragment {

    /* Etiqueta de depuración */
    private static final String TAG = Fragment_MisParadas.class.getSimpleName();

    /* Instancia global del recycler view */
    private RecyclerView lista;

    /* Instancia global del administrador */
    private RecyclerView.LayoutManager layoutManager;

    /* Array con lasposicionas de las rutas marcadas como davoritas */
    private int[] tvFavoritos = new int[100];

    private cargarUsuarioFavoritos cUsuarios = new cargarUsuarioFavoritos();

    public Fragment_MisParadas(){

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
        View v = inflater.inflate(R.layout.fav_fragment_paradas, container, false);

        lista = (RecyclerView) v.findViewById(R.id.reciclador);
        lista.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getActivity());
        lista.setLayoutManager(layoutManager);

        // Instancia del id propio del dispositivo Android donde se ejecute la aplicación
        String idMovil = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID);

        cUsuarios.cargarUsuarioFavoritos(idMovil, "Huelva", getContext(), lista, tvFavoritos, getActivity(), TAG);

        return v;
    }
}
